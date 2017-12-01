package fight3D;
import framework.animation.Animation3D;
import framework.gameMain.ActorModel;


public class WeaponModel extends ActorModel {
	public Animation3D weaponAnimation = new Animation3D();

	public WeaponModel(String fileName) {
		super(fileName);
		// TODO Auto-generated constructor stub
	}

	public Animation3D getAnimation() {
		return new Animation3D(weaponAnimation);
	}
}
