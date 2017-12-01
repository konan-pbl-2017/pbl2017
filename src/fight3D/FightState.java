package fight3D;

import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.audio.BGM3D;
import framework.audio.Sound3D;
import framework.gameMain.AbstractGameState;


public class FightState extends AbstractGameState {
	private Game game;
	private Fight fight;
	private Sound3D fightBGM = BGM3D.registerBGM("data\\fight.wav");
    
	FightState(Game game){
		this.game = game;
	}
	
	@Override
	public void init(RWTFrame3D frame) {	    
	    super.init(frame);
		((FightContainer)container).build(game, 2);

	    int [] characterList = new int[2];
		int stageNumber=0;
	    stageNumber = game.getStage();
		characterList = game.getCharacter();
		
		// 対戦処理を初期化(getSimpleUniverse()は、コンテナのbuild()の後でないとnullを返すので注意）
	    fight = new Fight(((FightContainer)container).getCanvas(), 
	    		stageNumber, 
	    		characterList, 
	    		(FightListener)getRWTCanvas3D());
	    System.gc();
	    // BGM
	    BGM3D.playBGM(fightBGM);
	}

	@Override
	public RWTContainer createContainer() {
		// TODO Auto-generated method stub
		return new FightContainer();
	}

	@Override
	public boolean useTimer() {
		return true;
	}

	@Override
	public void update(RWTVirtualController controller, long interval) {
		fight.progress(controller, interval);
	}
}
