package framework.view3D;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.DistanceLOD;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.LOD;
import javax.media.j3d.Light;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

import framework.model3D.BackgroundBox;
import framework.model3D.BaseObject3D;
import framework.model3D.BumpMapGenerator;
import framework.model3D.FresnelsReflectionMapGenerator;
import framework.model3D.IViewer3D;
import framework.model3D.Object3D;
import framework.model3D.ReflectionMapGenerator;

public class Viewer3D implements IViewer3D {
	GraphicsContext3D graphicsContext3D = null;
	ReflectionMapShader reflectionMappingShader = null;
	FresnelsReflectionMapShader fresnelsReflectionMappingShader = null;
	Dot3BumpMapShader dot3BumpMappingShader = null;
	FlatDot3BumpMapShader flatDot3BumpMappingShader = null;
	ArrayList<Light> lights = null;
	BackgroundBox skyBox = null;
	Camera3D camera = null;
	
	// 以下は省メモリ化のため導入
	static private Point3f center = new Point3f();
	static private Point3f origin = new Point3f();
	
	public Viewer3D(Camera3D camera) {
		this.camera = camera;
		reflectionMappingShader = new ReflectionMapShader();
		fresnelsReflectionMappingShader = new FresnelsReflectionMapShader();
		dot3BumpMappingShader = new Dot3BumpMapShader();
		flatDot3BumpMappingShader = new FlatDot3BumpMapShader();
	}

	@Override
	public void setGraphicsContext3D(GraphicsContext3D graphicsContext3D) {
		this.graphicsContext3D = graphicsContext3D;
	}
	

	@Override
	public void update(ArrayList<Light> lights, BackgroundBox skyBox) {
		// 光源の更新
		if (this.lights != lights) {
			this.lights = lights;
			for (int n = 0; n < lights.size(); n++) {
				if (lights.get(n) instanceof DirectionalLight) {
					// 平行光源の場合
					DirectionalLight dirlight = (DirectionalLight)lights.get(n);
					dot3BumpMappingShader.init(dirlight);
					flatDot3BumpMappingShader.init(dirlight);
					break;
				}
			}
		}
		
		// スカイボックスの更新
		if (this.skyBox != skyBox) {
			this.skyBox = skyBox;
			reflectionMappingShader.init(skyBox);
			fresnelsReflectionMappingShader.init(skyBox);
		}
	}
	
	public void draw(BaseObject3D obj) {
		
		// GraphicsContext3D 内の光源の更新
		if (lights != null && lights.size() != graphicsContext3D.numLights()) {
			for (int n = 0; n < lights.size(); n++) {
				graphicsContext3D.addLight(lights.get(n));
			}				
		}		
		
		Enumeration<Node> primitives = obj.getPrimitiveNodes();
		ReflectionMapGenerator reflectionMapGenerator = obj.getReflectionMappingInfo();
		BumpMapGenerator bumpMapGenerator = obj.getBumpMappingInfo();
		int n = 0;
		while (primitives.hasMoreElements()) {
			if (obj instanceof Object3D && ((Object3D)obj).isLODSet()) {
				// LOD の判定
				LOD lodNode = ((Object3D)obj).getLOD();
				if (lodNode instanceof DistanceLOD) {
					double nearSide = 0.0;
					double farSide = 0.0;
					DistanceLOD distanceLOD = ((DistanceLOD)lodNode);
					if (n > distanceLOD.numDistances()) break;
					if (n > 0) nearSide = distanceLOD.getDistance(n - 1);
					if (n < distanceLOD.numDistances()) farSide = distanceLOD.getDistance(n);
					
					distanceLOD.getPosition(center);
					Transform3D trans = new Transform3D();
					graphicsContext3D.getModelTransform(trans);
					trans.transform(center);	// オブジェクトの中心座標（世界座標上）
					trans = camera.getWorldToView();
					trans.transform(center);	// オブジェクトの中心座標（カメラ座標上）
					double distance = (double)center.distance(origin);
					if (distance < nearSide) {
						break;
					} else if (distance > farSide && n < distanceLOD.numDistances()) {
						n++;
						continue;
					}
				}
			}
			Node primitive = primitives.nextElement();
			if (primitive instanceof Shape3D) {
				// Shape3Dをレンダリング
				Appearance ap = ((Shape3D)primitive).getAppearance();
				if (bumpMapGenerator != null) {
					// obj にはバンプマッピングが設定されている
					if (!bumpMapGenerator.isHorizontal()) {
						dot3BumpMappingShader.updateAppearance(ap, bumpMapGenerator, camera);
					} else {
						// 水平のポリゴンに対しては、よりテクスチャユニットの消費量が少ない FlatDot3BumpMapShader を使う
						flatDot3BumpMappingShader.updateAppearance(ap, bumpMapGenerator, camera);
					}
				}
				if (reflectionMapGenerator != null) {
					// obj には反射マッピングが設定されている
					if (reflectionMapGenerator instanceof FresnelsReflectionMapGenerator) {
						// フレネル反射を使用
						fresnelsReflectionMappingShader.updateAppearance(ap, (FresnelsReflectionMapGenerator)reflectionMapGenerator, camera);
					} else {
						reflectionMappingShader.updateAppearance(ap, reflectionMapGenerator, camera);
					}
				}
				
				if (((Shape3D)primitive).getGeometry() != null) {
					graphicsContext3D.draw((Shape3D)primitive);
				}
			} else if (primitive instanceof Primitive) {
				Appearance ap = ((Primitive)primitive).getAppearance();
				if (bumpMapGenerator != null) {
					// obj にはバンプマッピングが設定されている
					if (!bumpMapGenerator.isHorizontal()) {
						dot3BumpMappingShader.updateAppearance(ap, bumpMapGenerator, camera);					
					} else {
						// 水平のポリゴンに対しては、よりテクスチャユニットの消費量が少ない FlatDot3BumpMapShader を使う
						flatDot3BumpMappingShader.updateAppearance(ap, bumpMapGenerator, camera);					
					}
				}
				if (reflectionMapGenerator != null) {
					// obj には反射マッピングが設定されている
					if (reflectionMapGenerator instanceof FresnelsReflectionMapGenerator) {
						// フレネル反射を使用
						fresnelsReflectionMappingShader.updateAppearance(ap, (FresnelsReflectionMapGenerator)reflectionMapGenerator, camera);
					} else {
						reflectionMappingShader.updateAppearance(ap, reflectionMapGenerator, camera);
					}
				}
				graphicsContext3D.setAppearance(ap);
				if (primitive instanceof Cone) {
					// Coneをレンダリング
					Geometry g = ((Cone)primitive).getShape(Cone.BODY).getGeometry();
					graphicsContext3D.draw(g);
					g = ((Cone)primitive).getShape(Cone.CAP).getGeometry();
					graphicsContext3D.draw(g);
				} else if (primitive instanceof Cylinder) {
					// Cylinderをレンダリング
					Geometry g = ((Cylinder)primitive).getShape(Cylinder.BODY).getGeometry();			
					graphicsContext3D.draw(g);
					g = ((Cylinder)primitive).getShape(Cylinder.TOP).getGeometry();			
					graphicsContext3D.draw(g);
					g = ((Cylinder)primitive).getShape(Cylinder.BOTTOM).getGeometry();			
					graphicsContext3D.draw(g);
				} else if (primitive instanceof Box) {
					// Boxをレンダリング
					Geometry g = ((Box)primitive).getShape(Box.TOP).getGeometry();
					graphicsContext3D.draw(g);
					g = ((Box)primitive).getShape(Box.BOTTOM).getGeometry();
					graphicsContext3D.draw(g);
					g = ((Box)primitive).getShape(Box.FRONT).getGeometry();
					graphicsContext3D.draw(g);
					g = ((Box)primitive).getShape(Box.BACK).getGeometry();
					graphicsContext3D.draw(g);
					g = ((Box)primitive).getShape(Box.LEFT).getGeometry();
					graphicsContext3D.draw(g);
					g = ((Box)primitive).getShape(Box.RIGHT).getGeometry();
					graphicsContext3D.draw(g);
				} else if (primitive instanceof Sphere) {
					// Sphereをレンダリング
					Geometry g = ((Sphere)primitive).getShape().getGeometry();
					graphicsContext3D.draw(g);
				}
			}
			n++;
		}		
	}

	@Override
	public void setModelTransform(Transform3D t) {
		graphicsContext3D.setModelTransform(t);
	}
}
