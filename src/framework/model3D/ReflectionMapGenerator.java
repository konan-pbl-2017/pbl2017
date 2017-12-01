package framework.model3D;

import javax.vecmath.Color4f;

public class ReflectionMapGenerator extends MapGenerator {
	private Color4f blendColor = null;
	
	public ReflectionMapGenerator(Color4f blendColor) {
		this.blendColor = blendColor;
	}
	
	public Color4f getBlendColor() {
		return blendColor;
	}
}
