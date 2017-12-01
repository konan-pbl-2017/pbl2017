package framework.gameMain;
import framework.physics.Solid3D;


public abstract class ActorModel extends GameModel {

	public ActorModel(String fileName) {
		super(fileName);
	}

	public Solid3D createBody() {
		return new Solid3D(getModel().createObject());
	}
}
