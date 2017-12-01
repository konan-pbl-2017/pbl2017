package framework.model3D;

import java.util.ArrayList;

import javax.media.j3d.Light;
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

/**
 * シャドウボリューム用のビジター
 * @author Nitta
 *
 */
public class ShadowVolumeVisitor extends ObjectVisitor {
	private boolean bInitialize = true;
	private ArrayList<Light> lights = null;
	private double shadowDepth = 0;

	/**
	 * シャドウボリュームを更新する場合
	 */
	public ShadowVolumeVisitor() {
		this.bInitialize = false;
		Transform3D t = new Transform3D();
		stackList.add(t);
	}
	
	/**
	 * シャドウボリュームを作成する場合
	 * @param lights 光源
	 * @param shadowDepth 影の深さ
	 */
	public ShadowVolumeVisitor(ArrayList<Light> lights, double shadowDepth) {
		this.lights = lights;
		this.shadowDepth = shadowDepth;
		this.bInitialize = true;
		Transform3D t = new Transform3D();
		stackList.add(t);
	}

	@Override
	public void preVisit(Object3D obj) {
		Transform3D t = new Transform3D(stackList.get(stackList.size() - 1));
		Transform3D t2 = new Transform3D();
		obj.pos.getTransform(t2);
		t.mul(t2);
		obj.rot.getTransform(t2);
		t.mul(t2);
		obj.scale.getTransform(t2);
		t.mul(t2);
		obj.center.getTransform(t2);
		t.mul(t2);
		stackList.add(t);
	}

	@Override
	public void postVisit(Object3D obj) {
		Transform3D t = stackList.remove(stackList.size() - 1);
		if (!obj.hasChildren()) {
			// 葉オブジェクトの場合、シャドウボリュームの処理をする
			if (bInitialize) {
				// 初期化の場合、シャドウボリュームの生成
				obj.createShadowVolume(lights, shadowDepth, t);
			} else {
				// 初期化でない場合、シャドウボリュームの更新
				obj.updateShadowVolume(t);
			}
		}
	}
}
