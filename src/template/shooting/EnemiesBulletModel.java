package template.shooting;

import framework.animation.Animation3D;
import framework.animation.AnimationFactory;
import framework.gameMain.ActorModel;
import framework.physics.Solid3D;

public class EnemiesBulletModel extends ActorModel {
	String animationFileName = null;
	Animation3D animation = null;
	
	public EnemiesBulletModel(String modelFileName, String animationFileName) {
		super(modelFileName);
		this.animationFileName  = animationFileName;
	}

	public EnemiesBullet createEnemiesBullet() {
		Solid3D body = new Solid3D(getModel().createObject());
		if (animationFileName != null && animation == null) {
			animation = AnimationFactory.loadAnimation(animationFileName);
		}
		return new EnemiesBullet(body, animation);
	}
}
