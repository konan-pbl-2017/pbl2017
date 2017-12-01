package framework.gameMain;

import framework.animation.Animation3D;
import framework.model3D.CollisionResult;
import framework.model3D.Object3D;

public class FlyingActor extends Actor {

	public FlyingActor(Object3D body, Animation3D animation) {
		super(body, animation);
	}
	
	public FlyingActor(ActorModel model) {
		super(model);
	}

	@Override
	public void onEndFall() {
	}

	@Override
	public void onIntersect(CollisionResult normal, long interval) {
	}

	@Override
	public void onEndAnimation() {
	}

}
