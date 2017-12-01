package framework.model3D;

public abstract class MapGenerator {
	private boolean bMapped = false;

	public MapGenerator() {
		super();
	}

	public void setMapped() {
		bMapped = true;
	}

	public boolean hasMapped() {
		return bMapped;
	}
}