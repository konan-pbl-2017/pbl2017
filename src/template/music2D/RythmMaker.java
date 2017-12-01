package template.music2D;

import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.gameMain.SimpleShootingGame;
import framework.model3D.Universe;

public class RythmMaker extends SimpleShootingGame {

	@Override
	public void init(Universe universe) {
		
	}

	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
		// TODO Auto-generated method stub

	}
	
	protected RWTContainer createRWTContainer() {
		return new RhythmMakerContainer();
	}

	@Override
	public RWTFrame3D createFrame3D() {
		RWTFrame3D f = new RWTFrame3D();
		f.setSize(800, 400);
		f.setTitle("Rhithm Maker for Music 2DGame");
		return f;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RythmMaker game = new RythmMaker();
		game.start();
	}

}
