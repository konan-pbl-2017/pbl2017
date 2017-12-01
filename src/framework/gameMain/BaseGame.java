package framework.gameMain;

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 一般的なゲーム用のクラス（状態遷移を持つことができる）
 * @author 新田直也
 *
 */
public abstract class BaseGame extends AbstractGame {
	Stack<AbstractGameState> gameStateStack = new Stack<AbstractGameState>();

	public abstract AbstractGameState getInitialGameState();
	public abstract boolean canGoPrevGameState();
	public abstract AbstractGameState changeNextGameState();
	
	public BaseGame() {
		super();
		pushNewGameState(getInitialGameState());
	}
	
	protected AbstractGameState getCurrentGameState() {
		return gameStateStack.peek();
	}
	
	public void goNextGameState(){		
		deactivateState(getCurrentGameState());
		AbstractGameState g = changeNextGameState();
		pushNewGameState(g);
		activateState(g);
	}

	public void goPrevGameState(){
		if(canGoPrevGameState()){
			deactivateState(getCurrentGameState());
			popGameState();
			activateState(getCurrentGameState());
		}
	}

	private void pushNewGameState(AbstractGameState g) {
		gameStateStack.push(g);
	}

	private void popGameState() {
		gameStateStack.pop();
	}
}
