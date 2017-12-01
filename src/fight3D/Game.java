package fight3D;

import java.awt.Color;

import framework.RWT.RWTFrame3D;
import framework.gameMain.BaseGame;
import framework.gameMain.AbstractGameState;
import framework.gameMain.IGameState;

public class Game extends BaseGame {
	private  int playerNum; 
	private  int stage;     
	private  int character[];
	private  int rank[];
	private  int tp[];
	private  int defeat[];
	private  int defeated[];

	@Override
	public RWTFrame3D createFrame3D() {
		RWTFrame3D frame = new RWTFrame3D();
        frame.setSize(720, 480);
        frame.setBackground(Color.BLACK);
        frame.setShadowCasting(true);
        return frame;
	}
	
	@Override
	public AbstractGameState getInitialGameState() {
		// TODO Auto-generated method stub	    
//		AbstractGameState state = new FightState(this);
	    AbstractGameState state = new CharacterSelectState(this);
		return state;
	}
	
	public AbstractGameState changeNextGameState(){
		IGameState lastGameState = getCurrentGameState();
		
		AbstractGameState newGameState = null;
		
		if (lastGameState instanceof CharacterSelectState) {
			newGameState = new StageSelectState(this);
		}
		else if (lastGameState instanceof StageSelectState) {
			newGameState = new FightState(this);
		}
		else if (lastGameState instanceof FightState) {
			newGameState = new Ranking1State(this);
		}
		else if (lastGameState instanceof Ranking1State) {
			newGameState = new Ranking2State(this);
		}
		else if (lastGameState instanceof Ranking2State) {
			newGameState = new CharacterSelectState(this);
		}	    
	    return newGameState;
	}
	
	public boolean canGoPrevGameState(){
		if (getCurrentGameState() instanceof StageSelectState) {
			return true;
		}	
		return false;
	}


	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}


	public int getPlayerNum() {
		return playerNum;
	}


	public void setStage(int stage) {
		this.stage = stage;
	}


	public int getStage() {
		return stage;
	}


	public void setCharacter(int character[]) {
		this.character = character;
	}


	public int[] getCharacter() {
		return character;
	}


	public void setRank(int rank[]) {
		this.rank = rank;
	}


	public int[] getRank() {
		return rank;
	}


	public void setTp(int tp[]) {
		this.tp = tp;
	}


	public int[] getTp() {
		return tp;
	}


	public void setDefeat(int defeat[]) {
		this.defeat = defeat;
	}


	public int[] getDefeat() {
		return defeat;
	}


	public void setDefeated(int defeated[]) {
		this.defeated = defeated;
	}


	public int[] getDefeated() {
		return defeated;
	}
	

}
