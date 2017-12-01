package fight3D;

import framework.RWT.RWTContainer;
import framework.RWT.RWTVirtualController;
import framework.gameMain.AbstractGameState;

public class StageSelectState extends AbstractGameState {

	Game game;

	StageSelectState(Game g) {
		this.game = g;
	}

	@Override
	public RWTContainer createContainer() {
		return new StageSelectContainer(game);
	}

	@Override
	public void update(RWTVirtualController controller, long interval) {
	}

	@Override
	public boolean useTimer() {
		return false;
	}
}
