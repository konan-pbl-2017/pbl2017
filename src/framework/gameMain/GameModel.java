package framework.gameMain;
import framework.animation.Animation3D;
import framework.model3D.Model3D;
import framework.model3D.ModelFactory;

public class GameModel {

	private Model3D model = null;
	private String fileName;

	public GameModel(String fileName) {
		this.fileName = fileName;
	}

	public Animation3D getAnimation() {
		return new Animation3D();
	}
	
	public Model3D getModel() {
		if(model == null && fileName != null) {
			model = ModelFactory.loadModel(fileName, false, true);
		}
		return model;
	}
	
	public void clearModel(){
		model = null;
	}
}