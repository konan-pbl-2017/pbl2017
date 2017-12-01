package framework.model3D;

import javax.vecmath.Color4f;

public class FresnelsReflectionMapGenerator extends ReflectionMapGenerator {
	private boolean bTransparent = true;

	public FresnelsReflectionMapGenerator(Color4f blendColor, boolean bTransparent) {
		super(blendColor);
		this.bTransparent = bTransparent;
	}
	
	public boolean isTransparent() {
		return bTransparent;
	}
}
