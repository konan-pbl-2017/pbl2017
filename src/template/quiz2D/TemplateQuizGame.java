package template.quiz2D;

import java.awt.Color;
import java.math.BigDecimal;



import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;
import framework.game2D.Sprite;
import framework.gameMain.BaseScenarioGameContainer;
import framework.gameMain.SimpleRolePlayingGame;
import framework.gameMain.SimpleScenarioGame;
import framework.model3D.ModelFactory;
import framework.model3D.Object3D;
import framework.model3D.Position3D;
import framework.model3D.Quaternion3D;
import framework.model3D.Universe;
import framework.physics.PhysicsUtility;
import framework.scenario.Event;
import framework.scenario.ScenarioManager;
import framework.scenario.ScenarioState;
import framework.view3D.Camera3D;

public class TemplateQuizGame extends SimpleScenarioGame {
	private RWTFrame3D frame;
	
	@Override
	public void init(Universe universe, Camera3D camera) {		
		// シナリオの設定
		setScenario("data\\TemplateQuiz\\scenario.xml");
		container.setScenario(scenario);
		scenario.fire("開始");
	}

	@Override
	public RWTFrame3D createFrame3D() {
		frame = new RWTFrame3D();
		frame.setSize(1000, 800);
		frame.setTitle("Template for 2D Quiz Game");
		frame.setBackground(Color.BLACK);
		return frame;
	}
	
	@Override
	protected RWTContainer createRWTContainer() {
		container = new QuizGameContainer();
		return container;
	}
	
	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
	}
	
	@Override
	public void showOption(int n, String option) {
		((QuizGameContainer)container).showOption(n, option);
	}

	@Override
	public void action(String action, Event event, ScenarioState nextState) {
		// シナリオ進行による世界への作用をここに書く
		if (action.equals("right")) {
		} else if (action.equals("wrong")) {
		}
	}

	/**
	 * ゲームのメイン
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TemplateQuizGame game = new TemplateQuizGame();
		game.setFramePolicy(5, 33, false);
		game.start();
	}

}
