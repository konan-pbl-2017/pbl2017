package framework.model3D;

import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.vecmath.Color4f;

public class BumpMapGenerator extends MapGenerator {
	private Texture bumpMap = null;
	private TexCoordGeneration texCoorgGen = null;
	private boolean bHorizontal = false;
	
	public BumpMapGenerator(Texture bumpMap, TexCoordGeneration texCoorgGen, boolean bHorizontal) {
		this.bumpMap = bumpMap;
		this.texCoorgGen = texCoorgGen;
		this.bHorizontal = bHorizontal;
	}
	
	public Texture getBumpMap() {
		return bumpMap;
	}

	public TexCoordGeneration getTexCoordGeneration() {
		return texCoorgGen;
	}
	
	public boolean isHorizontal() {
		return bHorizontal;
	}
}
