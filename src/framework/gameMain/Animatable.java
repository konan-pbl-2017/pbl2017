package framework.gameMain;

import javax.media.j3d.TransformGroup;

import framework.animation.Animation3D;
import framework.model3D.BaseObject3D;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Placeable;

public abstract class Animatable implements Placeable {
	public Object3D body;
	public Animation3D animation;

	public Animatable(Object3D body, Animation3D animation) {
		this.body = body;
		this.animation = animation;
	}

	/**
	 * 単位時間ごとの動作（アニメーション処理）
	 * @param interval --- 前回呼び出されたときからの経過時間（ミリ秒単位）
	 */
	public void motion(long interval) {
		if (animation != null) {
			// 1. アニメーションを実行
			if (animation.progress(interval) == false) {
				onEndAnimation();
			}
			
			// 2. 姿勢を変える
			body.apply(animation.getPose(), false);
		}
	}
	
	public TransformGroup getTransformGroupToPlace() {
		return getBody().getTransformGroupToPlace();
	}
	
	public BaseObject3D getBody() {
		return body;
	}
	
	/**
	 * アニメーションが終了するたびに呼ばれる
	 */
	public abstract void onEndAnimation();

	public Position3D getPosition() {
		return body.getPosition3D();
	}

	public void setPosition(Position3D p) {
		body.apply(p, false);
	}

}