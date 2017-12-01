package fight3D;

import framework.RWT.RWTContainer;
import framework.RWT.RWTVirtualController;
import framework.gameMain.AbstractGameState;

public class Ranking2State extends AbstractGameState {

	Game game;

	Ranking2State(Game game) {
		this.game = game;
	}

	@Override
	public RWTContainer createContainer() {
		return new Ranking2Container(game);
	}

	@Override
	public void update(RWTVirtualController controller, long interval) {
	}

	@Override
	public boolean useTimer() {
		return false;
	}
}
