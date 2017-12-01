package framework.game2D;


import javax.media.j3d.TransformGroup;

import framework.model3D.Placeable;
import framework.model3D.Position3D;
import framework.model3D.Universe;
import framework.view3D.Camera3D;

public abstract class Maze2D extends Map2D {
	// コンストラクタ
	public Maze2D(String blockImage, String tileImage) {
		super(new String[]{tileImage, blockImage}, 1);
	}

}
