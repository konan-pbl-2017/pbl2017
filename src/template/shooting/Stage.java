package template.shooting;

import framework.model3D.BaseObject3D;
import framework.physics.Ground;

public class Stage extends Ground {
	private String terrainPart;

	public Stage(BaseObject3D obj) {
		super(obj);
	}

	public Stage(BaseObject3D obj, String terrainPart) {
		super(obj);
		this.terrainPart = terrainPart;
	}
	
	public String getTerrainPart() {
		return terrainPart;
	}
}
