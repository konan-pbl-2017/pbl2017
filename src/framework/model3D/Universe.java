package framework.model3D;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Light;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;



/**
 * 影、反射マッピング、バンプマッピングなどがレンダリングできる SimpleUniverse
 * （すべての物体を配置した後に、必ず compile() を呼ばないといけないことに注意！）
 * @author 新田直也
 *
 */
public class Universe extends VirtualUniverse {
	private Locale locale = null;
	private BranchGroup root = null;
	private BranchGroup additionalRoot = null;
	private ArrayList<Light> lights = new ArrayList<Light>();
	private BackgroundBox skyBox = null;
//	private ArrayList<Background> backgrounds = new ArrayList<Background>();
	private double shadowDepth = 100.0;
	private ArrayList<Object3D> occluders = new ArrayList<Object3D>();
	private ArrayList<BaseObject3D> shadows =  new ArrayList<BaseObject3D>();
	private ArrayList<BaseObject3D> receivers =  new ArrayList<BaseObject3D>();
	private ArrayList<BaseObject3D> extraObjects = new ArrayList<BaseObject3D>();
	
	public Universe() {
		super();
		locale = new Locale(this);
		root = new BranchGroup();
		additionalRoot = new BranchGroup();
		additionalRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		additionalRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		additionalRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		shadowDepth = 100.0;
	}
	
	/**
	 * モデルのルートを設定する
	 * @param root
	 */
	public void setRoot(BranchGroup root) {
		removeRoot();
		this.root = root;
	}

	/**
	 * モデルのルートを取得する
	 * @return モデルのルートに相当する SceneGraph
	 */
	public BranchGroup getRoot() {
		return root;
	}

	/**
	 * モデルのルートを削除する
	 */
	public void removeRoot() {
		if (root == null) return;
		Enumeration<BranchGroup> branchGroups = locale.getAllBranchGraphs();
		while (branchGroups.hasMoreElements()) {
			if (branchGroups.nextElement() == root) {
				locale.removeBranchGraph(root);
				root = null;
				break;
			}
		}
	}
	
	public void addBranchGraph(BranchGroup g) {
		locale.addBranchGraph(g);
	}

	/**
	 * オブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void placeUnremovable(Node obj) {
		if (!root.isCompiled()) {
			root.addChild(obj);
		} else {
			additionalRoot.addChild(obj);
		}
	}

	/**
	 * オブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void placeUnremovable(Placeable obj) {
		BaseObject3D body = obj.getBody();
		if (body.isReflectionMappingApplied() || body.isBumpMappingApplied()) {
			extraObjects.add(body);
		} else {
			placeUnremovable(obj.getTransformGroupToPlace());
		}
	}
	
	/**
	 * 後で取り除けるようにオブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void place(Node obj) {
		BranchGroup objRoot;
		if (obj.getParent() != null 
				&& obj.getParent() instanceof BranchGroup) {
			objRoot = (BranchGroup)obj.getParent();
		} else {
			objRoot = new BranchGroup();
			objRoot.setCapability(BranchGroup.ALLOW_DETACH);
			objRoot.addChild(obj);
		}
		additionalRoot.addChild(objRoot);
	}
	
	/**
	 * 後で取り除けるようにオブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void place(Placeable obj) {
		BaseObject3D body = obj.getBody();
		if (body != null && (body.isReflectionMappingApplied() || body.isBumpMappingApplied())) {
			extraObjects.add(body);
		} else {
			place(obj.getTransformGroupToPlace());
		}
	}
	
	/**
	 * 影付きで配置する
	 * @param obj 配置するオブジェクト
	 */
	public void placeAsAnOcculuder(Placeable obj) {
		BaseObject3D body = obj.getBody();
		if (body instanceof Object3D) {
			addShadowOcculuder((Object3D)body);
		} else {
			placeUnremovable(obj);
		}
	}
	
	/**
	 * 他のオブジェクトの影が落ちるようにオブジェクトを配置する
	 * @param obj 配置するオブジェクト
	 */
	public void placeAsAReceiver(Placeable obj) {
		addShadowReceiver(obj.getBody(), new Color3f(0.5f, 0.5f, 0.5f));
	}

	/**
	 * 他のオブジェクトの影が落ちるようにオブジェクトを表示する
	 * @param obj 表示するオブジェクト
	 * @param shadowColor 影の色
	 */
	public void placeAsAReceiver(Placeable obj, Color3f shadowColor) {
		addShadowReceiver(obj.getBody(), shadowColor);
	}

	/**
	 * 光源の追加
	 * @param light 追加する光源
	 */
	public void placeLight(Light light) {
		root.addChild(light);
		getLights().add((Light)light.cloneTree());
	}
	
	/**
	 * 光源の追加（影つき）
	 * @param light 追加する光源
	 * @param shadowDepth 影の深さ
	 */
	public void placeLight(Light light, double shadowDepth) {
		root.addChild(light);
		getLights().add((Light)light.cloneTree());
		this.shadowDepth = shadowDepth;
	}
	
	/**
	 * スカイボックスの追加
	 * @param skyBox 追加するスカイボックス
	 */
	public void placeSkyBox(BackgroundBox skyBox) {
		root.addChild(skyBox);		
		this.skyBox = skyBox;
	}
		
	/**
	 * オブジェクトを可能ならば取り除く
	 * @param obj 取り除くオブジェクト
	 */
	public void displace(Node obj) {
		Node parent = obj.getParent();
		if (parent == root) return;
		if (parent == additionalRoot) {
			additionalRoot.removeChild(obj);
		} else if (parent instanceof BranchGroup 
				&& parent.getCapability(BranchGroup.ALLOW_DETACH) 
				&& parent.getParent() == additionalRoot) {
			additionalRoot.removeChild(parent);
		}
	}
	
	/**
	 * オブジェクトを可能ならば取り除く
	 * @param obj 取り除くオブジェクト
	 */
	public void displace(Placeable obj) {
		BaseObject3D body = obj.getBody();
		if (occluders.contains(body)) {
			occluders.remove(body);
		}
		if (shadows.contains(body)) {
			shadows.remove(body);
		}
		if (receivers.contains(body)) {
			receivers.remove(body);
		}
		if (extraObjects.contains(body)) {
			extraObjects.remove(body);
		}
		displace(obj.getTransformGroupToPlace());
	}
		
	public ArrayList<Light> getLights() {
		return lights;
	}
	
	public BackgroundBox getSkyBox() {
		return skyBox;
	}
	
	private void addShadowOcculuder(Object3D occluder) {
//		occluder.view(this);					// occluder 自身の表示
		ShadowVolumeVisitor shadowVolumeVisitor = 
			new ShadowVolumeVisitor(getLights(), shadowDepth);	// ShadowVolume の生成
		occluder.accept(shadowVolumeVisitor);	// occluder 内部で ShadowVolume が保持される
		occluders.add(occluder);
	}
	
	private void addShadowReceiver(BaseObject3D receiverObject, Color3f shadowColor) {
		BaseObject3D receiverObjectBody = receiverObject.duplicate();
		
		// 元の BaseObject3D を影表示用にする
		ArrayList<Appearance> aps = receiverObject.getAppearances();
		Material m = new Material();
		m.setDiffuseColor(shadowColor);			// 影の色
		m.setAmbientColor(0.0f, 0.0f, 0.0f);
		m.setSpecularColor(0.0f, 0.0f, 0.0f);
		m.setShininess(1.0f);
		RenderingAttributes ra = new RenderingAttributes();
		ra.setStencilEnable(false);									// ステンシルバッファを使わない
		ra.setDepthBufferWriteEnable(true);							// 通常通りZバッファに書き込む
		ra.setDepthTestFunction(RenderingAttributes.LESS_OR_EQUAL);	// 通常通りZバッファで判定する
		for (int n = 0; n < aps.size(); n++) {
			Appearance ap = aps.get(n);
			ap.setMaterial(m);
			ap.setRenderingAttributes(ra);
		}
		
		shadows.add(receiverObject);
		
		// 複製した BaseObject3D を通常表示用（ただし影の部分は描画しない）にする
		ArrayList<Appearance> aps2 = receiverObjectBody.getAppearances();
		RenderingAttributes ra2 = new RenderingAttributes();
		ra2.setStencilEnable(true);												// ステンシルバッファを使う
		ra2.setDepthBufferWriteEnable(true);
		ra2.setDepthTestFunction(RenderingAttributes.LESS_OR_EQUAL);			// 影の上に上書きする
		ra2.setStencilFunction(RenderingAttributes.GREATER_OR_EQUAL, 0, ~0);	// ステンシルバッファの値が1以上のとき描画しない
		ra2.setStencilOp(RenderingAttributes.STENCIL_REPLACE, 						// ステンシルバッファをクリアする
				RenderingAttributes.STENCIL_REPLACE, 
				RenderingAttributes.STENCIL_REPLACE);
//		TransparencyAttributes ta2 = new TransparencyAttributes();
//		ta2.setTransparencyMode(TransparencyAttributes.BLENDED);
//		ta2.setTransparency(0.0001f);
		for (int n = 0; n < aps2.size(); n++) {
			Appearance ap2 = aps2.get(n);
			ap2.setRenderingAttributes(ra2);
//			ap2.setTransparencyAttributes(ta2);
		}

		receivers.add(receiverObjectBody);
	}
		
	public void update() {
		ShadowVolumeVisitor shadowVolumeVisitor = 
			new ShadowVolumeVisitor();	// ShadowVolume の 更新
		for (int n = 0; n < occluders.size(); n++) {
			occluders.get(n).accept(shadowVolumeVisitor);	
		}
	}
	
	/**
	 * Mixedモードレンダリングを用いて影の描画を行う
	 * （影が落ちるオブジェクト（レシーバ）の影部分、シャドウボリューム）
	 * @param graphicsContext3D
	 */
	public void preRender(IViewer3D viewer) {	
		update();
		
		viewer.update(lights, skyBox);
	}

	/**
	 * Mixedモードレンダリングを用いて影の描画を行う
	 * （影が落ちるオブジェクトの影以外の部分）
	 * @param graphicsContext3D
	 */
	public void renderField(IViewer3D viewer) {
		// 		＊＊＊ レンダリングは以下の順番でしかうまくいかない ＊＊＊
		// たとえば、シャドウボリュームをレンダリングした後にレシーバの影以外の部分をレンダリングすると、
		// BackgroundBoxの描画がなぜか汚くなってしまう。
		
		// 1. レシーバの影部分
		Transform3D trans = new Transform3D();
		for (int n = 0; n < shadows.size(); n++) {
			BaseObject3D obj = shadows.get(n);
			if (obj instanceof Object3D) {
				((Object3D)obj).accept(new ObjectRenderer(viewer));
			} else {
				obj.center.getTransform(trans);
				viewer.setModelTransform(trans);
				viewer.draw(obj);
			}
		}
		
		// 2. レシーバの影以外の部分
		for (int n = 0; n < receivers.size(); n++) {
			BaseObject3D obj = receivers.get(n);			
			if (obj instanceof Object3D) {
				((Object3D)obj).accept(new ObjectRenderer(viewer));
			} else {
				obj.center.getTransform(trans);
				viewer.setModelTransform(trans);
				viewer.draw(obj);
			}
		}
		
		// 3. シャドウボリュームの表面（occuluder内部に積んでいる）
		// 4. シャドウボリュームの裏面（occuluder内部に積んでいる）
		for (int n = 0; n < occluders.size(); n++) {
			Object3D occuluder = occluders.get(n);
			occuluder.accept(new ObjectRenderer(viewer));
		}
		
		// 特殊なレンダリングが必要となるオブジェクト
		for (int n = 0; n < extraObjects.size(); n++) {
			BaseObject3D extra = extraObjects.get(n);
			if (extra instanceof Object3D) {
				((Object3D)extra).accept(new ObjectRenderer(viewer));
			} else {
				extra.center.getTransform(trans);
				viewer.setModelTransform(trans);
				viewer.draw(extra);
			}
		}
	}
	
	public void postRender(IViewer3D viewer) {
	}

	public void compile() {
		root.compile();
		locale.addBranchGraph(root);
		locale.addBranchGraph(additionalRoot);
	}
	
	private class ObjectRenderer extends ObjectVisitor {
		Stack<Transform3D> transforms = new Stack<Transform3D>();
		IViewer3D viewer = null;
		
		public ObjectRenderer(IViewer3D viewer) {
			Transform3D t = new Transform3D();
			transforms.push(t);
			this.viewer = viewer;
		}
		
		@Override
		public void preVisit(Object3D obj) {
			Transform3D t = new Transform3D(transforms.peek());
			Transform3D t2 = new Transform3D();
			obj.pos.getTransform(t2);
			t.mul(t2);
			obj.rot.getTransform(t2);
			t.mul(t2);
			obj.scale.getTransform(t2);
			t.mul(t2);
			obj.center.getTransform(t2);
			t.mul(t2);
			transforms.push(t);
		}
		
		@Override
		public void postVisit(Object3D obj) {
			Transform3D t = transforms.pop();
			if (!obj.hasChildren()) {
				viewer.setModelTransform(t);
				viewer.draw(obj);
			}
		}		
	}
}
