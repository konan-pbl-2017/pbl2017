package template.music2D;

import java.awt.Color;
import java.awt.GraphicsConfiguration;

import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTLine;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.game2D.Ground2D;
import framework.game2D.Velocity2D;
import framework.gameMain.SimpleShootingGame;
import framework.model3D.Universe;

public class TemplateMusic2D extends SimpleShootingGame {
	private Target target;
	private Target target1;
	private Ground2D stage;

	@Override
	public void init(Universe universe) {
		// /////////////////////////////////////////////////////////
		//
		// 各登場物の初期化
		//
		// ////////////////////////////////////////////////////////
		
		// 
		target = new Target("data\\images\\MyShip.gif");
		universe.place(target);
		target.setPosition(-10,15);
		
		target1  = new Target("data\\images\\Enemy.gif");
		universe.place(target1);
		target1.setPosition(10,10);

		stage = new Ground2D(null, "data\\images\\m101.jpg", windowSizeWidth,
				windowSizeHeight);
		universe.place(stage);

		// 表示範囲を決める（左上が原点としてその原点から幅、高さを計算する）
		setViewRange(30, 30);

	}

	@Override
	public void progress(RWTVirtualController virtualController, long interval) {
		// キー操作によるプレイヤーのアクション処理
		// 左
		if (virtualController.isKeyDown(0, RWTVirtualController.BUTTON_B)) {
			if (target.getPosition().getY() < (30 - (15 * 0.6 * 2) - 15)
					&& target.getPosition().getY() > (30 - (15 * 0.8 * 2) - 15)) {
				System.out.println("target:OK");
			}

		}
		else if(virtualController.isKeyDown(0, RWTVirtualController.BUTTON_A)){
			if (target1.getPosition().getY() < (30 - (15 * 0.6 * 2) - 15)
					&& target1.getPosition().getY() > (30 - (15 * 0.8 * 2) - 15)) {
				System.out.println("target1:OK");
			}
		}

		target.setVelocity(new Velocity2D(0, -4.0));
		target.motion(interval);
		target1.setVelocity(new Velocity2D(0, -4.0));
		target1.motion(interval);
		
//		System.out.println("target" + target.getPosition().getY());
//		System.out.println("target1" + target1.getPosition().getY());

	}

	protected RWTContainer createRWTContainer() {
		return new MusicContainer();
	}

	@Override
	public RWTFrame3D createFrame3D() {
		RWTFrame3D f = new RWTFrame3D();
		f.setSize(400, 800);
		f.setTitle("Template for Music 2DGame");
		return f;
	}

	/**
	 * ゲームのメイン
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TemplateMusic2D game = new TemplateMusic2D();
		game.setFramePolicy(5, 33, false);
		game.start();
	}

}
