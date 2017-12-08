package framework.model3D;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.j3d.Appearance;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedGeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Material;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.View;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

import cv97.SceneGraph;
import cv97.j3d.IndexedFaceSetNodeObject;
import cv97.j3d.SceneGraphJ3dObject;
import cv97.j3d.TransformNodeObject;
import cv97.node.AppearanceNode;
import cv97.node.IndexedFaceSetNode;
import cv97.node.Node;
import cv97.node.ShapeNode;
import cv97.node.TransformNode;
import cv97.util.LinkedListNode;
import framework.schedule.ScheduleManager;
import framework.schedule.TaskController;

/**
 * 3次元モデルを生成するためのクラス
 * @author 新田直也
 *
 */
public class ModelFactory {
	private static Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
	public static final String STL_FILE_EXTENSION = ".stl";
	public static final String OBJ_FILE_EXTENSION = ".obj";
	
	/**
	 * 3次元モデルを階層構造と各部の名称とともにファイルから読み込む
	 * @param fileName ファイル名
	 * @return
	 */
	public static Model3D loadModel(String fileName){		
		return loadModel(fileName, false, false);
	}
	
	/**
	 * 3次元モデルを階層構造と各部の名称とともにファイルから読み込む（透明テクスチャの有無の指定付き）
	 * @param fileName ファイル名
	 * @param bTransparent 透明テクスチャがあるか否か
	 * @param bCalcNormal TODO
	 * @return
	 */
	public static Model3D loadModel(String fileName, boolean bTransparent, boolean bCalcNormal){		
//		System.out.println(fileName);
		if (fileName.toLowerCase().endsWith(STL_FILE_EXTENSION)) {
			// STLファイル読み込み
			try {
				return loadStlFile(fileName, null, bCalcNormal);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ModelFileFormatException e) {
				e.printStackTrace();
			}			
		} else if (fileName.toLowerCase().endsWith(OBJ_FILE_EXTENSION)) {
			// OBJファイル読み込み
			try {
				return loadObjFile(fileName, bCalcNormal);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ModelFileFormatException e) {
				e.printStackTrace();
			}
		}
		
		SceneGraph sg = new SceneGraph(SceneGraph.NORMAL_GENERATION);
		
		// ファイル読み込み時にレンダリングが行われるので、オフスクリーンレンダリングを禁止する
		TaskController renderingController = ScheduleManager.getInstance().getController("rendering");
		renderingController.waitForActivation();
		renderingController.activate();
		
		// ファイル読み込み
		View view = canvas.getView();
		if (view != null) view.removeCanvas3D(canvas);
 		SceneGraphJ3dObject sgObject = new SceneGraphJ3dObject(canvas, sg);
		sg.setObject(sgObject);
		sg.load(fileName);
		
		// オフスクリーンレンダリングの許可
		renderingController.deactivate();
		
		// Model3Dへの変換
		Model3D model= loadModel(sg, bTransparent, bCalcNormal);
		
		return model;	
	}

	private static Model3D loadModel(LinkedListNode node, boolean bTransparent, boolean bCalcNormal){
//		System.out.println(node.getClass());
		
		if( ! (node instanceof ShapeNode) ){
			Node childNodes;
			String name = "";
			if (node instanceof SceneGraph) {
				childNodes = ((SceneGraph)node).getNodes();
			} else {
				childNodes = ((Node)node).getChildNodes();
				name = ((Node)node).getName();				
			}
			if(childNodes != null) {
				ArrayList<Model3D> list = new ArrayList<Model3D>();
				for(Node n = childNodes;n != null;n = n.next()){
					Model3D model;
					if((model = loadModel(n, bTransparent, bCalcNormal)) != null){
						list.add(model);
					}
					
					//兄弟で無限ループにならないようにする処理
					Node dummy = n;
					dummy = dummy.next();
					if(n == dummy){
						break;
					}
				}
				
				if(!list.isEmpty()){
					Model3D modelList[] = new Model3D[list.size()];
					for(int i = 0;i < list.size();i++){
						modelList[i] = list.get(i);
					}
					if (node instanceof TransformNode) {
						// 各階層で定義されている変換情報も一緒に保持しておく
						TransformNode transformNode = (TransformNode)node;
						TransformNodeObject transformNodeObj = (TransformNodeObject)transformNode.getObject();
						Transform3D transform = new Transform3D();
						transformNodeObj.getTransform(transform);
						return new ContainerModel(name, modelList, transform);						
					}
					return new ContainerModel(name, modelList);
				}
			}
			return null;
		} else{	
			//ジオメトリデータなら葉ノードにあたるModel3Dデータを作って結果を返す
			
			String name = ((Node)node).getName();
			ShapeNode sp = (ShapeNode)node;
			
			//ジオメトリデータの取得
			IndexedFaceSetNode indexfsNode = sp.getIndexedFaceSetNodes();			

			if(indexfsNode ==null){
				return null;
			}
			IndexedFaceSetNodeObject indexfsNodeObj = (IndexedFaceSetNodeObject)
													indexfsNode.getObject();
			
			IndexedGeometryArray newGeom;
			if (bCalcNormal) {
				GeometryInfo gInfo = new GeometryInfo(indexfsNodeObj);
				NormalGenerator ng = new NormalGenerator();
				ng.generateNormals(gInfo);
	//			Stripifier st = new Stripifier();
	//			st.stripify(gInfo);
				newGeom = gInfo.getIndexedGeometryArray();
			} else {
				newGeom = indexfsNodeObj;
			}
			
			//アピアランスデータの取得
			AppearanceNode appearNodes = (AppearanceNode)sp.getAppearanceNodes();
			
			Appearance appearObj = null;
			if(appearNodes != null){
				appearObj = (Appearance)appearNodes.getObject();
				appearObj = (Appearance)appearObj.cloneNodeComponent(true);
				TextureAttributes ta = appearObj.getTextureAttributes();
				if (ta == null) {
					ta = new TextureAttributes();
				}
				ta.setTextureMode(TextureAttributes.MODULATE);	// シェーディングとテクスチャを合成する
				appearObj.setTextureAttributes(ta);
				if (bTransparent) {
					// 透明テクスチャが貼ってある場合
					TransparencyAttributes tra = appearObj.getTransparencyAttributes();
					if (tra == null) {
						tra = new TransparencyAttributes();
					}
					tra.setTransparencyMode(TransparencyAttributes.FASTEST);
					appearObj.setTransparencyAttributes(tra);
				}
//				Texture tex = appearObj.getTexture();
//				if (tex != null) {
//					ImageComponent ic[] = tex.getImages();
//					for (int n = 0; n < ic.length; n++) {
//						boolean b = ic[n].isYUp();
//						ic[n].setYUp(!b);
//					}
//					tex.setImages(ic);
//					appearObj.setTexture(tex);
//				}
			}
			
			//リーフモデルの作成
			return new LeafModel(name,newGeom,appearObj);
		}
	}
	
	/**
	 * STLファイル(アスキー、バイナリ)の読み込み
	 * @param res リソース
	 * @param fileName ファイル名
	 * @param ap 3Dモデルに適用する表面属性
	 * @param bCalcNormal 法線の再計算を行うか
	 * @return 読み込んだ3Dモデル
	 * @throws IOException 
	 * @throws ModelFileFormatException 
	 */
	public static Model3D loadStlFile(String fileName, Appearance ap, boolean bCalcNormal) throws IOException, ModelFileFormatException {
		// STLファイル読み込み
		FileInputStream in = new FileInputStream(fileName);
		BufferedInputStream bis = new BufferedInputStream(in);
		byte[] headerBin = new byte[80];
		int n = bis.read(headerBin);
		if (headerBin[0] == 's' && 
			headerBin[1] == 'o' &&
			headerBin[2] == 'l' &&
			headerBin[3] == 'i' &&
			headerBin[4] == 'd' &&
			headerBin[5] == ' ') {
			bis.close();
			
			// STLはテキストファイル
			File file = new File(fileName);
			FileReader filereader = new FileReader(file);
			BufferedReader br = new BufferedReader(filereader);
			Model3D model = loadStlAsciiFile(br, ap, bCalcNormal);
			br.close();
			return model;
		} else if (n == 80) {
			// STLはバイナリファイル
			Model3D model = loadStlBinaryFile(bis, headerBin, ap, bCalcNormal);
			bis.close();
			return model;
		}
		return null;
	}

	private static Model3D loadStlAsciiFile(BufferedReader br, Appearance ap, boolean bCalcNormal) throws IOException, ModelFileFormatException {		
		// ヘッダ読み込み
		String header = br.readLine();
		if (header == null) {
			br.close();
			throw new ModelFileFormatException();
		}
		header.trim();
		String headerContents[] = header.split(" ");
		String name;
		if (headerContents.length > 0 && headerContents[0].equals("solid")) {
			if (headerContents.length > 1) name = headerContents[1];
			else name = "";
		} else {
			System.out.println("STL file's header error!!");
			br.close();
			throw new ModelFileFormatException();
		}
		
		// Facet読み込み
		ArrayList<double []> verticies = new ArrayList<double []>();
		String line;
		while ((line = br.readLine()) != null && !line.startsWith("endsolid ")) {
			// facet normal ...
			line.trim();
			String[] data = line.split(" ");
			if (data.length < 1 || !data[0].equals("facet")) {
				System.out.println("Expected facet normal ...");
				br.close();
				throw new ModelFileFormatException();
			}
			
			// outer loop
			line = br.readLine();
			if (line == null) {
				System.out.println("Expected outer loop");
				br.close();
				throw new ModelFileFormatException();
			}
			line.trim();
			if (!line.equals("outer loop")) {
				System.out.println("Expected outer loop");
				br.close();
				throw new ModelFileFormatException();
			}
			
			// vertex * 3
			for (int i = 0; i < 3; i++) {
				line = br.readLine();
				if (line == null) {
					System.out.println("Expected vertex x y z");
					br.close();
					throw new ModelFileFormatException();
				}
				line.trim();
				data = line.split(" ");
				if (data.length < 4 || !data[0].equals("vertex")) {
					System.out.println("Expected vertex x y z");
					br.close();
					throw new ModelFileFormatException();
				}
				double[] vertex = new double[]{
						Double.parseDouble(data[1]),	// X座標
						Double.parseDouble(data[2]),	// Y座標
						Double.parseDouble(data[3])		// Z座標
				};
				verticies.add(vertex);		// Z座標
			}
			
			// endloop
			line = br.readLine();
			if (line == null) {
				System.out.println("Expected endloop");
				br.close();
				throw new ModelFileFormatException();
			}
			line.trim();
			if (!line.equals("endloop")) {
				System.out.println("Expected endloop");
				br.close();
				throw new ModelFileFormatException();
			}
			
			// endfacet
			line = br.readLine();
			if (line == null) {
				System.out.println("Expected endfacet");
				br.close();
				throw new ModelFileFormatException();
			}
			line.trim();
			if (!line.equals("endfacet")) {
				System.out.println("Expected endfacet");
				br.close();
				throw new ModelFileFormatException();
			}
		}

		TriangleArray triArray = new TriangleArray(verticies.size(), TriangleArray.COORDINATES);
		for (int n = 0; n < verticies.size(); n++) {
			triArray.setCoordinate(n, verticies.get(n));
		}
		if (ap == null) ap = new Appearance();
		return new LeafModel(name, triArray, ap);
	}

	private static Model3D loadStlBinaryFile(BufferedInputStream bis, byte[] header, Appearance ap, boolean bCalcNormal) throws IOException, ModelFileFormatException {
		// Facet数読み込み
		Integer numTri = readInt(bis);
		if (numTri == null) {
			System.out.println("Expected vertex count");
			bis.close();
			throw new ModelFileFormatException();
		}
		
		// Facet読み込み
		ArrayList<float []> verticies = new ArrayList<float []>();
		ArrayList<float []> normals = new ArrayList<float []>();
		Vector3f[] vertex = new Vector3f[] {
				new Vector3f(), 
				new Vector3f(), 
				new Vector3f()
		};
		
		for (int n = 0; n < numTri; n++) {
			Float nx = readFloat(bis);	// 法線X成分
			if (nx == null) {
				System.out.println("Expected normal");
				bis.close();
				throw new ModelFileFormatException();
			}
			
			Float ny = readFloat(bis);	// 法線Y成分
			if (ny == null) {
				System.out.println("Expected normal");
				bis.close();
				throw new ModelFileFormatException();
			}

			Float nz = readFloat(bis);	// 法線Z成分
			if (nz == null) {
				System.out.println("Expected normal");
				bis.close();
				throw new ModelFileFormatException();
			}

			for (int i = 0; i < 3; i++) {
				Float x = readFloat(bis);	// X座標
				if (x == null) {
					System.out.println("Expected vertex" + (i + 1));
					bis.close();
					throw new ModelFileFormatException();
				}
				
				Float y = readFloat(bis);	// Y座標
				if (y == null) {
					System.out.println("Expected vertex" + (i + 1));
					bis.close();
					throw new ModelFileFormatException();
				}
				
				Float z = readFloat(bis);	// Z座標
				if (z == null) {
					System.out.println("Expected vertex" + (i + 1));
					bis.close();
					throw new ModelFileFormatException();
				}
				
				verticies.add(new float[]{x, y, z});
				vertex[i].x = x;
				vertex[i].y = y;
				vertex[i].z = z;
			}
			if (bCalcNormal) {
				vertex[1].sub(vertex[0]);
				vertex[2].sub(vertex[0]);
				vertex[1].normalize();
				vertex[2].normalize();
				vertex[0].cross(vertex[1], vertex[2]);
				nx = vertex[0].x;
				ny = vertex[0].y;
				nz = vertex[0].z;
			}
			normals.add(new float[]{nx, ny, nz});
			normals.add(new float[]{nx, ny, nz});
			normals.add(new float[]{nx, ny, nz});				
			
			byte[] optionBin = new byte[2];
			if (bis.read(optionBin) < 2) {
				System.out.println("Expected option");
				bis.close();
				throw new ModelFileFormatException();
			}
		}

		TriangleArray triArray = new TriangleArray(verticies.size(), TriangleArray.COORDINATES | TriangleArray.NORMALS);
		for (int n = 0; n < verticies.size(); n++) {
			triArray.setCoordinate(n, verticies.get(n));
			triArray.setNormal(n, normals.get(n));
		}
		
		if (ap == null) ap = new Appearance();
		return new LeafModel(new String(header), triArray, ap);
	}

	/**
	 * OBJファイルの読み込み
	 * @param res リソース
	 * @param fileName ファイル名
	 * @param bCalcNormal 法線の再計算を行うか
	 * @return 読み込んだ3Dモデル
	 * @throws IOException 
	 * @throws ModelFileFormatException 
	 */
	public static Model3D loadObjFile(String fileName, boolean bCalcNormal) throws IOException, ModelFileFormatException {		
		File file = new File(fileName);
		FileReader filereader = new FileReader(file);
		BufferedReader br = new BufferedReader(filereader);
		
		HashMap<String, Appearance> appearances = new HashMap<String, Appearance>();
		ArrayList<Model3D> objects = new ArrayList<Model3D>();
		ArrayList<Model3D> groups = new ArrayList<Model3D>();
		ArrayList<Model3D> subGroups = new ArrayList<Model3D>();
		ArrayList<Vector3f> objCoordinates = new ArrayList<Vector3f>();
		ArrayList<Vector2f> objTexCoordinates = new ArrayList<Vector2f>();
		ArrayList<Vector3f> objNormals = new ArrayList<Vector3f>();
		ArrayList<ArrayList<Integer>> subGroupCoordinateIndicies = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> subGroupTexCoordIndicies = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> subGroupNormalIndicies = new ArrayList<ArrayList<Integer>>();
		String objectName = "";
		String groupName = "";
		String subGroupName = "";
		Appearance subGroupAp = null;
		
		String line = null;
		while ((line = br.readLine()) != null) {
			line.trim();
			String data[] = line.split(" ");
			if (data[0].equals("#")) {
				// コメント
			} else if (data[0].equals("mtllib")) {
				// 材質ファイルの指定
				String dir = new File(fileName).getParent();
				String mtlFileName = line.substring(line.indexOf(" ") + 1);
				String mtlFilePath = new File(dir, mtlFileName).getPath();
				appearances = loadMtlFile(mtlFilePath);
			} else if (data[0].equals("o")) {
				// オブジェクトの開始
				if (groups.size() > 0 || subGroups.size() > 0 || subGroupCoordinateIndicies.size() > 0) {
					// 前のオブジェクトのグループの残り、サブグループの残り、ストリップの残りを新しいオブジェクトとして追加
					Model3D newObject = createRemainingObject(objectName, groups, groupName, objCoordinates,
							objTexCoordinates, objNormals, subGroups, subGroupName, subGroupAp,
							subGroupCoordinateIndicies, subGroupTexCoordIndicies, subGroupNormalIndicies);
					objects.add(newObject);
					groups.clear();
				}
				
				if (data.length < 1) {
					System.out.println("Expected object's name");
					throw new ModelFileFormatException();
				}
				objectName = data[1];
			} else if (data[0].equals("g")) {
				// グループの開始
				if (subGroups.size() > 0 || subGroupCoordinateIndicies.size() > 0) {
					// 前のグループのサブグループの残り、ストリップの残りを新しいグループとして追加
					Model3D newGroup = createRemainingGroup(groupName, objCoordinates, objTexCoordinates, objNormals, subGroups,
							subGroupName, subGroupAp, subGroupCoordinateIndicies, subGroupTexCoordIndicies, subGroupNormalIndicies);
					groups.add(newGroup);
					subGroups.clear();
				}
				
				if (data.length < 1) {
					System.out.println("Expected group's name");
					throw new ModelFileFormatException();
				}
				groupName = data[1];
			} else if (data[0].equals("usemtl")) {
				// 材質の使用(サブグループの開始)
				if (subGroupCoordinateIndicies.size() > 0) {
					// 前のサブグループのストリップの残りを新しいサブグループとして追加
					Model3D newSubGroup = createRemainingSubGroup(objCoordinates, objTexCoordinates, objNormals, 
						subGroupName, subGroupAp, subGroupCoordinateIndicies, subGroupTexCoordIndicies, subGroupNormalIndicies);
					subGroups.add(newSubGroup);
					subGroupCoordinateIndicies.clear();
					subGroupTexCoordIndicies.clear();
					subGroupNormalIndicies.clear();
				}
				
				if (data.length < 1) {
					System.out.println("Expected mtl file's name");
					throw new ModelFileFormatException();
				}
				subGroupAp = appearances.get(data[1]);
				subGroupName = groupName + "_" + data[1] + subGroups.size();	// ※サブグループの名前が重複しないようにする処理
			} else if (data[0].equals("v")) {
				// 頂点座標データ
				if (data.length < 4) {
					System.out.println("Expected vertex coordinate");
					throw new ModelFileFormatException();
				}
				float x = Float.parseFloat(data[1]);
				float y = Float.parseFloat(data[2]);
				float z = Float.parseFloat(data[3]);
				Vector3f v = new Vector3f(x, y, z);
				objCoordinates.add(v);
			} else if (data[0].equals("vt")) {
				// テクスチャ座標データ
				if (data.length < 3) {
					System.out.println("Expected texture coordinate");
					throw new ModelFileFormatException();
				}
				float x = Float.parseFloat(data[1]);
				float y = Float.parseFloat(data[2]);
				Vector2f v = new Vector2f(x, y);
				objTexCoordinates.add(v);
			} else if (data[0].equals("vn")) {
				// 法線ベクトルデータ
				if (data.length < 4) {
					System.out.println("Expected normal vector");
					throw new ModelFileFormatException();
				}
				float x = Float.parseFloat(data[1]);
				float y = Float.parseFloat(data[2]);
				float z = Float.parseFloat(data[3]);
				Vector3f v = new Vector3f(x, y, z);
				objNormals.add(v);				
			} else if (data[0].equals("f")) {
				// ポリゴン(ストリップ)データ
				if (data.length < 4) {
					System.out.println("At least 3 verticies are needed");
					throw new ModelFileFormatException();
				}
				ArrayList<Integer> coordinateIndicies = new ArrayList<Integer>();
				ArrayList<Integer> texCoordIndicies = new ArrayList<Integer>();
				ArrayList<Integer> normalIndicies = new ArrayList<Integer>();
				for (int n = 1; n < data.length; n++) {
					String elements[] = data[n].split("/");
					if (elements.length >= 1) {
						// 頂点座標インデックス
						coordinateIndicies.add(Integer.parseInt(elements[0]) - 1);
						if (elements.length >= 2) {
							// テクスチャ座標インデックス
							if (elements[0].length() > 0) {
								// "v//vn"の場合もあるため
								texCoordIndicies.add(Integer.parseInt(elements[1]) - 1);
							}
							if (elements.length >= 3) {
								// 法線ベクトルインデックス
								normalIndicies.add(Integer.parseInt(elements[2]) - 1);
							}
						}
					}
				}
				subGroupCoordinateIndicies.add(coordinateIndicies);
				subGroupTexCoordIndicies.add(texCoordIndicies);
				subGroupNormalIndicies.add(normalIndicies);
			}
		}
		
		// ファイル読み込み後の処理
		if (groups.size() > 0 || subGroups.size() > 0 || subGroupCoordinateIndicies.size() > 0) {
			// ストリップの残り、サブグループの残り、グループの残りを最後のオブジェクトとして追加
			Model3D newObject = createRemainingObject(objectName, groups, 
					groupName, objCoordinates, objTexCoordinates, objNormals, subGroups, 
					subGroupName, subGroupAp, subGroupCoordinateIndicies, subGroupTexCoordIndicies, subGroupNormalIndicies);
			objects.add(newObject);
		}
		br.close();
		
		if (objects.size() == 0) {
			System.out.println("No polygon is included");
			throw new ModelFileFormatException();
		} else if (objects.size() == 1) {
			if (groups.size() == 0) {
				System.out.println("No polygon is included");
				throw new ModelFileFormatException();				
			} else if (groups.size() == 1) {
				// グループがただ一つの場合
				return groups.get(0);
			} else {
				// オブジェクトがただ一つの場合
				return objects.get(0);
			}
		} else {
			// オブジェクトが複数の場合
			return new ContainerModel(fileName, (Model3D [])objects.toArray(new Model3D []{}));
		}
	}

	/**
	 * グループの残りを新しいオブジェクトとして追加
	 * @param objectName 新しいオブジェクトの名前
	 * @param groups　新しいオブジェクトを構成するグループ
	 * @param groupName
	 * @param groupCoordinates
	 * @param groupTexCoordinates
	 * @param groupNormals
	 * @param subGroups
	 * @param subGroupName
	 * @param subGroupAp
	 * @param subGroupCoordinateIndicies
	 * @param subGroupTexCoordIndicies
	 * @param subGroupNormalIndicies
	 * @return
	 */
	private static Model3D createRemainingObject(String objectName, ArrayList<Model3D> groups, 
			String groupName, ArrayList<Vector3f> groupCoordinates, ArrayList<Vector2f> groupTexCoordinates,
			ArrayList<Vector3f> groupNormals, ArrayList<Model3D> subGroups, 
			String subGroupName, Appearance subGroupAp,
			ArrayList<ArrayList<Integer>> subGroupCoordinateIndicies,
			ArrayList<ArrayList<Integer>> subGroupTexCoordIndicies,
			ArrayList<ArrayList<Integer>> subGroupNormalIndicies) {
		if (subGroups.size() > 0 || subGroupCoordinateIndicies.size() > 0) {
			// ストリップの残り、サブグループの残りを新しいグループとして追加
			Model3D newGroup = createRemainingGroup(groupName, groupCoordinates, groupTexCoordinates, groupNormals, subGroups,
					subGroupName, subGroupAp, subGroupCoordinateIndicies, subGroupTexCoordIndicies, subGroupNormalIndicies);
			groups.add(newGroup);
			subGroups.clear();
		}

		// グループの残りを新しいオブジェクトとして作成
		Model3D newObject = new ContainerModel(objectName, (Model3D [])groups.toArray(new Model3D []{}));
		return newObject;
	}

	/**
	 * サブグループの残りを新しいグループとして作成
	 * @param groupName　新しいグループの名前
	 * @param groupCoordinates　頂点座標の値のリスト
	 * @param groupTexCoordinates　テクスチャ座標の値のリスト
	 * @param groupNormals　法線ベクトルの値のリスト
	 * @param subGroups 新しいグループを構成するサブグループ
	 * @param subGroupName
	 * @param subGroupAp
	 * @param subGroupCoordinateIndicies
	 * @param subGroupTexCoordIndicies
	 * @param subGroupNormalIndicies
	 * @return
	 */
	private static Model3D createRemainingGroup(String groupName,
			ArrayList<Vector3f> groupCoordinates, ArrayList<Vector2f> groupTexCoordinates, ArrayList<Vector3f> groupNormals, 
			ArrayList<Model3D> subGroups, 
			String subGroupName, Appearance subGroupAp,
			ArrayList<ArrayList<Integer>> subGroupCoordinateIndicies,
			ArrayList<ArrayList<Integer>> subGroupTexCoordIndicies,
			ArrayList<ArrayList<Integer>> subGroupNormalIndicies) {
		if (subGroupCoordinateIndicies.size() > 0) {
			// ストリップの残りを新しいサブグループとして追加
			Model3D newSubGroup = createRemainingSubGroup(groupCoordinates, groupTexCoordinates, groupNormals, 
				subGroupName, subGroupAp, subGroupCoordinateIndicies, subGroupTexCoordIndicies, subGroupNormalIndicies);
			subGroups.add(newSubGroup);
			subGroupCoordinateIndicies.clear();
			subGroupTexCoordIndicies.clear();
			subGroupNormalIndicies.clear();
		}
		
		// サブグループの残りを新しいグループとして作成
		return new ContainerModel(groupName, (Model3D [])subGroups.toArray(new Model3D []{}));
	}

	/**
	 * ストリップの残りを新しいサブグループとして作成
	 * @param groupCoordinates
	 * @param groupTexCoordinates
	 * @param groupNormals
	 * @param subGroupName 新しいサブグループの名前
	 * @param subGroupAp 新しいサブグループの表面属性
	 * @param subGroupCoordinateIndicies　頂点座標へのインデックス
	 * @param subGroupTexCoordIndicies テクスチャ座標へのインデックス
	 * @param subGroupNormalIndicies　法線ベクトルへのインデックス
	 * @return
	 */
	private static Model3D createRemainingSubGroup(ArrayList<Vector3f> groupCoordinates,
			ArrayList<Vector2f> groupTexCoordinates, ArrayList<Vector3f> groupNormals,
			String subGroupName, Appearance subGroupAp, ArrayList<ArrayList<Integer>> subGroupCoordinateIndicies,
			ArrayList<ArrayList<Integer>> subGroupTexCoordIndicies,
			ArrayList<ArrayList<Integer>> subGroupNormalIndicies) {
		// ストリップの残りを新しいサブグループとして作成
		// Geometry の作成
		GeometryArray geometry = createGeometryArray(groupCoordinates, groupTexCoordinates, groupNormals, 
				subGroupCoordinateIndicies, subGroupTexCoordIndicies, subGroupNormalIndicies);
		// Object3D の作成
		return new LeafModel(subGroupName, geometry, subGroupAp);
	}

	/**
	 * ストリップのインデックス情報からジオメトリーを作成
	 * @param groupCoordinates 頂点座標の値のリスト
	 * @param groupTexCoordinates テクスチャ座標の値のリスト
	 * @param groupNormals 法線ベクトルの値のリスト
	 * @param subGroupCoordinateIndicies 頂点座標へのインデックス
	 * @param subGroupTexCoordIndicies テクスチャ座標へのインデックス
	 * @param subGroupNormalIndicies 法線ベクトルへのインデックス
	 * @return 作成したジオメトリー
	 */
	private static GeometryArray createGeometryArray(
			ArrayList<Vector3f> groupCoordinates,
			ArrayList<Vector2f> groupTexCoordinates, 
			ArrayList<Vector3f> groupNormals,
			ArrayList<ArrayList<Integer>> subGroupCoordinateIndicies,
			ArrayList<ArrayList<Integer>> subGroupTexCoordIndicies,
			ArrayList<ArrayList<Integer>> subGroupNormalIndicies) {
		int vertexCount = 0;
		int indexCount = 0;
		for (int n = 0; n < subGroupCoordinateIndicies.size(); n++) {
			ArrayList<Integer> coordinateIndicies = subGroupCoordinateIndicies.get(n);
			vertexCount += coordinateIndicies.size();
			indexCount += (coordinateIndicies.size() - 2) * 3;
		}
		int vertexFormat = IndexedGeometryArray.COORDINATES;
		if (subGroupTexCoordIndicies.size() > 0) {
			vertexFormat |= IndexedGeometryArray.TEXTURE_COORDINATE_2;
		}
		if (subGroupNormalIndicies.size() > 0) {
			vertexFormat |= IndexedGeometryArray.NORMALS;					
		}
		IndexedGeometryArray geometry = new IndexedTriangleArray(vertexCount, vertexFormat, indexCount);
		int vertexNum = 0;
		int index = 0;
		for (int n = 0; n < subGroupCoordinateIndicies.size(); n++) {
			ArrayList<Integer> coordinateIndicies = subGroupCoordinateIndicies.get(n);
			ArrayList<Integer> texCoordIndicies = subGroupTexCoordIndicies.get(n);
			ArrayList<Integer> normalIndicies = subGroupNormalIndicies.get(n);
			Vector3f c = groupCoordinates.get(coordinateIndicies.get(0));
			geometry.setCoordinate(vertexNum, new float[]{c.x, c.y, c.z});
			c = groupCoordinates.get(coordinateIndicies.get(1));
			geometry.setCoordinate(vertexNum + 1, new float[]{c.x, c.y, c.z});			
			if (texCoordIndicies.size() > 0) {
				Vector2f t = groupTexCoordinates.get(texCoordIndicies.get(0));
				geometry.setTextureCoordinate(vertexNum, new float[]{t.x, t.y});		
				t = groupTexCoordinates.get(texCoordIndicies.get(1));
				geometry.setTextureCoordinate(vertexNum + 1, new float[]{t.x, t.y});
			}
			if (normalIndicies.size() > 0) {
				Vector3f nr = groupNormals.get(normalIndicies.get(0));
				geometry.setNormal(vertexNum, nr);
				nr = groupNormals.get(normalIndicies.get(1));
				geometry.setNormal(vertexNum + 1, nr);
			}
			int firstCount = vertexNum;
			vertexNum += 2;
			for (int i = 2; i < coordinateIndicies.size(); i++) {
				c = groupCoordinates.get(coordinateIndicies.get(i));
				geometry.setCoordinate(vertexNum, new float[]{c.x, c.y, c.z});
				geometry.setCoordinateIndex(index, firstCount);
				geometry.setCoordinateIndex(index + 1, vertexNum - 1);
				geometry.setCoordinateIndex(index + 2, vertexNum);
				if (texCoordIndicies.size() > 0) {
					Vector2f t = groupTexCoordinates.get(texCoordIndicies.get(i));
					geometry.setTextureCoordinate(vertexNum, new float[]{t.x, t.y});		
					geometry.setTextureCoordinateIndex(index, firstCount);
					geometry.setTextureCoordinateIndex(index + 1, vertexNum - 1);
					geometry.setTextureCoordinateIndex(index + 2, vertexNum);
				}
				if (normalIndicies.size() > 0) {
					Vector3f nr = groupNormals.get(normalIndicies.get(i));
					geometry.setNormal(vertexNum, nr);
					geometry.setNormalIndex(index, firstCount);
					geometry.setNormalIndex(index + 1, vertexNum - 1);
					geometry.setNormalIndex(index + 2, vertexNum);
				}
				vertexNum += 1;
				index += 3;
			}
		}
		return geometry;
//		ArrayList<Integer> coordinateIndiciesMap = new ArrayList<Integer>();
//		ArrayList<Integer> texCoordIndiciesMap = new ArrayList<Integer>();
//		ArrayList<Integer> normalIndiciesMap = new ArrayList<Integer>();
//		int indexCount = 0;
//		int[] stripIndexCounts = new int[subGroupCoordinateIndicies.size()];
//		for (int n = 0; n < subGroupCoordinateIndicies.size(); n++) {
//			ArrayList<Integer> coordinateIndicies = subGroupCoordinateIndicies.get(n);
//			ArrayList<Integer> texCoordIndicies = subGroupTexCoordIndicies.get(n);
//			ArrayList<Integer> normalIndicies = subGroupNormalIndicies.get(n);
//			indexCount += coordinateIndicies.size();
//			stripIndexCounts[n] = coordinateIndicies.size();
//			for (int i = 0; i < coordinateIndicies.size(); i++) {
//				int c = coordinateIndicies.get(i);
//				int coordinateIndex = coordinateIndiciesMap.indexOf(c);
//				if (coordinateIndex == -1) {
//					coordinateIndex = coordinateIndiciesMap.size();
//					coordinateIndiciesMap.add(c);
//				}
//				coordinateIndicies.set(i, coordinateIndex);
//				if (texCoordIndicies.size() > 0) {
//					int t = texCoordIndicies.get(i);
//					int texCoordIndex = texCoordIndiciesMap.indexOf(t);
//					if (texCoordIndex == -1) {
//						texCoordIndex = texCoordIndiciesMap.size();
//						texCoordIndiciesMap.add(t);
//					}
//					texCoordIndicies.set(i, texCoordIndex);
//				}
//				if (normalIndicies.size() > 0) {
//					int nr = normalIndicies.get(i);
//					int normalIndex = normalIndiciesMap.indexOf(nr);
//					if (normalIndex == -1) {
//						normalIndex = normalIndiciesMap.size();
//						normalIndiciesMap.add(nr);
//					}
//					normalIndicies.set(i, normalIndex);
//				}
//			}
//		}
//		int vertexCount = coordinateIndiciesMap.size();
//		int vertexFormat = IndexedGeometryArray.COORDINATES;
//		if (texCoordIndiciesMap.size() > 0) {
//			if (vertexCount < texCoordIndiciesMap.size()) vertexCount = texCoordIndiciesMap.size();
//			vertexFormat |= IndexedGeometryArray.TEXTURE_COORDINATE_2;
//		}
//		if (normalIndiciesMap.size() > 0) {
//			if (vertexCount < normalIndiciesMap.size()) vertexCount = normalIndiciesMap.size();
//			vertexFormat |= IndexedGeometryArray.NORMALS;					
//		}
//		IndexedGeometryArray geometry = new IndexedTriangleFanArray(vertexCount, vertexFormat, indexCount, stripIndexCounts);
//		for (int n = 0; n < coordinateIndiciesMap.size(); n++) {
//			Vector3f c = groupCoordinates.get(coordinateIndiciesMap.get(n));
//			geometry.setCoordinate(n, new float[]{c.x, c.y, c.z});
//		}
//		for (int n = 0; n < texCoordIndiciesMap.size(); n++) {
//			Vector2f t = groupTexCoordinates.get(texCoordIndiciesMap.get(n));
//			geometry.setTextureCoordinate(n, new float[]{t.x, t.y});
//		}
//		for (int n = 0; n < normalIndiciesMap.size(); n++) {
//			Vector3f nr = groupNormals.get(normalIndiciesMap.get(n));
//			geometry.setNormal(n, nr);
//		}
//		int index = 0;
//		for (int n = 0; n < subGroupCoordinateIndicies.size(); n++) {
//			ArrayList<Integer> coordinateIndicies = subGroupCoordinateIndicies.get(n);
//			ArrayList<Integer> texCoordIndicies = subGroupTexCoordIndicies.get(n);
//			ArrayList<Integer> normalIndicies = subGroupNormalIndicies.get(n);
//			for (int i = 0; i < coordinateIndicies.size(); i++) {
//				geometry.setCoordinateIndex(index, coordinateIndicies.get(i));
//				if (texCoordIndicies.size() > 0) {
//					geometry.setTextureCoordinateIndex(index, texCoordIndicies.get(i));
//				}
//				if (normalIndicies.size() > 0) {
//					geometry.setNormalIndex(index, normalIndicies.get(i));
//				}
//				index++;
//			}
//		}
//		return geometry;
	}
	
	/**
	 * MTLファイルの読み込み
	 * @param res リソース
	 * @param fileName ファイル名
	 * @return　材質名から表面属性オブジェクトへのマッピング
	 * @throws IOException 
	 * @throws ModelFileFormatException 
	 */
	private static HashMap<String, Appearance> loadMtlFile(String fileName) throws IOException, ModelFileFormatException {
		File file = new File(fileName);
		FileReader filereader = new FileReader(file);
		BufferedReader br = new BufferedReader(filereader);

		HashMap<String, Appearance> appearances = new HashMap<String, Appearance>();
		String materialName;
		Appearance ap = null;
		Material m = null;
		
		String line = null;
		while ((line = br.readLine()) != null) {
			line.trim();
			String data[] = line.split(" ");
			if (data[0].equals("newmtl")) {
				if (data.length < 1) {
					System.out.println("Expected material's name");
					throw new ModelFileFormatException();
				}
				materialName = data[1];
				m = new Material();
				ap = new Appearance();
				ap.setMaterial(m);
				appearances.put(materialName, ap);
			} else if (data[0].equals("Kd")) {
				if (data.length < 4) {
					System.out.println("Expected diffuse color");
					throw new ModelFileFormatException();
				}
				float r = Float.parseFloat(data[1]);
				float g = Float.parseFloat(data[2]);
				float b = Float.parseFloat(data[3]);
				m.setDiffuseColor(r, g, b);
			} else if (data[0].equals("Ka")) {
				if (data.length < 4) {
					System.out.println("Expected ambient color");
					throw new ModelFileFormatException();
				}
				float r = Float.parseFloat(data[1]);
				float g = Float.parseFloat(data[2]);
				float b = Float.parseFloat(data[3]);
				m.setAmbientColor(r, g, b);
			} else if (data[0].equals("Ks")) {
				if (data.length < 4) {
					System.out.println("Expected specular color");
					throw new ModelFileFormatException();
				}
				float r = Float.parseFloat(data[1]);
				float g = Float.parseFloat(data[2]);
				float b = Float.parseFloat(data[3]);
				m.setSpecularColor(r, g, b);
			} else if (data[0].equals("Tr")) {
				if (data.length < 4) {
					System.out.println("Expected emissive color");
					throw new ModelFileFormatException();
				}
				float r = Float.parseFloat(data[1]);
				float g = Float.parseFloat(data[2]);
				float b = Float.parseFloat(data[3]);
				m.setEmissiveColor(r, g, b);
			} else if (data[0].equals("Ns")) {
				if (data.length < 2) {
					System.out.println("Expected shiness value");
					throw new ModelFileFormatException();
				}
				float s = Float.parseFloat(data[1]);
				m.setShininess(s);
			} else if (data[0].equals("map_Kd")) {
				if (data.length < 2) {
					System.out.println("Expected texture file's name");
					throw new ModelFileFormatException();
				}
				String dir = new File(fileName).getParent();
				String texFileName = line.substring(line.indexOf(" ") + 1);
				String texFilePath = new File(dir, texFileName).getPath();
				TextureLoader texLoader = new TextureLoader(texFilePath, TextureLoader.BY_REFERENCE | TextureLoader.Y_UP, null);
				ap.setTexture(texLoader.getTexture());
			}
		}
		return appearances;
	}

	private static Integer readInt(BufferedInputStream bis) throws IOException {
		byte[] intBin = new byte[4];
		if (bis.read(intBin, 3, 1) < 1) {
			return null;
		}
		if (bis.read(intBin, 2, 1) < 1) {
			return null;
		}
		if (bis.read(intBin, 1, 1) < 1) {
			return null;
		}
		if (bis.read(intBin, 0, 1) < 1) {
			return null;
		}
		return ByteBuffer.wrap(intBin).getInt();
	}	
	
	private static Float readFloat(BufferedInputStream bis) throws IOException {
		byte[] floatBin = new byte[4];
		if (bis.read(floatBin, 3, 1) < 1) {
			return null;
		}
		if (bis.read(floatBin, 2, 1) < 1) {
			return null;
		}
		if (bis.read(floatBin, 1, 1) < 1) {
			return null;
		}
		if (bis.read(floatBin, 0, 1) < 1) {
			return null;
		}
		return ByteBuffer.wrap(floatBin).getFloat();
	}
}
