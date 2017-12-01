package fight3D;

import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.audio.BGM3D;
import framework.audio.Sound3D;
import framework.gameMain.AbstractGameState;

public class CharacterSelectState extends AbstractGameState {
	private Game game;
	private Sound3D openingBGM = BGM3D.registerBGM("data\\opening.wav");

	CharacterSelectState(Game game) {
		this.game = game;
	}

	@Override
	public void init(RWTFrame3D frame) {
		super.init(frame);
		// BGM
		BGM3D.playBGM(openingBGM);
	}
	
	@Override
	public RWTContainer createContainer() {
		return new CharacterSelectContainer(game);
	}

	@Override
	public boolean useTimer() {
		return false;		// falseÇ…Ç∑ÇÈÇ±Ç∆Ç≈updateÇ™åƒÇŒÇÍÇ»Ç≠Ç»ÇÈÅB
	}

	@Override
	public void update(RWTVirtualController controller, long interval) {
	}
}
