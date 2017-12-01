package fight3D;

import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.audio.BGM3D;
import framework.audio.Sound3D;
import framework.gameMain.AbstractGameState;

public class Ranking1State extends AbstractGameState {
	private Game game;
	private Sound3D rankingBGM = BGM3D.registerBGM("data\\ending.wav");

	Ranking1State(Game game) {
		this.game = game;
	}

	@Override
	public void init(RWTFrame3D frame) {
		super.init(frame);
		
		// BGM
		BGM3D.playBGM(rankingBGM);
	}

	@Override
	public RWTContainer createContainer() {
		return new Ranking1Container(game);
	}

	@Override
	public void update(RWTVirtualController controller, long interval) {
	}

	@Override
	public boolean useTimer() {
		return false;
	}
}
