package fight3D;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import framework.RWT.RWTContainer;
import framework.RWT.RWTLabel;
import framework.RWT.RWTLine;
import framework.RWT.RWTSelectionCanvas3D;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.model3D.BaseObject3D;

public class StageSelectContainer extends RWTContainer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3826880530032193822L;

	Game game;
	
	private RWTSelectionCanvas3D objectCanvas;
	private boolean goFight = false;
	private RWTLabel activatedLabel = new RWTLabel();
	private ArrayList<Stage> stages;
	private RWTLabel title = new RWTLabel();
	private RWTLabel back = new RWTLabel();
	private RWTLabel message1 = new RWTLabel();
	private RWTLabel message2 = new RWTLabel();
	private RWTLine line1 = new RWTLine();
	private RWTLine line2 = new RWTLine();

	public StageSelectContainer(Game g) {
		game = g;
	}
	
	// フレームに追加してサイズが確定した後に中身を構築する
	public void build(GraphicsConfiguration gc) {
		// 初期化
		removeAll();
		setBackground(Color.BLACK);
		
		// 選択したステージを描画するためのキャンバスを追加
		objectCanvas = new RWTSelectionCanvas3D();
		objectCanvas.setRelativePosition(0.03f, 0.675f);
		objectCanvas.setRelativeSize(0.25f, 0.30f);
		addCanvas(objectCanvas);

		stages = StageManager.getInstance().getStageList();

		// タイトル・戻るアイコン・メッセージエリアの描画
		title.setString("｢ステージ選択｣");
		title.setColor(Color.white);
		title.setFont(new Font("", Font.PLAIN, 20));
		title.setRelativePosition(0.20f, 0.10f);
		addWidget(title);

		back.setString("B:ｷｬﾗｸﾀｰ選択画面へ");
		back.setColor(Color.white);
		back.setFont(new Font("", Font.PLAIN, 8));
		back.setRelativePosition(0.025f, 0.05f);
		addWidget(back);

		message1.setRelativePosition(0.60f, 0.05f);
		message1.setString("ステージを選択して下さい。");
		message1.setColor(Color.YELLOW);
		addWidget(message1);
		message2.setRelativePosition(0.60f, 0.10f);
		message2.setString("Ａ：決定");
		message2.setColor(Color.YELLOW);
		addWidget(message2);

		// ステージの描画
		for (int i = 0; i < stages.size(); i++) {
			String str = stages.get(i).getName() + RWTLabel.NEW_PARAGRAPH
					+ stages.get(i).getComment();
			StageSelectImageButton stage = new StageSelectImageButton(str,
					stages.get(i).getModel(), activatedLabel, i, this);
			stage.setString(stages.get(i).getName());
			stage.setColor(Color.white);
			stage.setRelativePosition(0.10f + i % 3 * 0.3f, (i / 3 + 1) * 0.3f);
			addSelectableWidget(stage, i % 3, i / 3);
		}

		// 選択されたステージの説明文の描画
		addWidget(activatedLabel);

		// 画面分割ラインの描画
		line1.setColor(Color.white);
		line1.setRelativePosition(0.00f, 0.15f, 1.00f, 0.15f);
		addWidget(line1);

		line2.setColor(Color.white);
		line2.setRelativePosition(0.00f, 0.65f, 1.00f, 0.65f);
		addWidget(line2);

		repaint();
	}

	public void updateObjectCanvas(BaseObject3D o) {
		if (objectCanvas != null) objectCanvas.setObject(o);
	}

	@Override
	public void keyPressed(RWTVirtualKey key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(RWTVirtualKey key) {
		// TODO Auto-generated method stub

		if (key.getVirtualKey() == RWTVirtualController.BUTTON_A) {
			// 確認画面
			setConfirmMessage();
			repaint();

			if (goFight == true) {
				// 選択されたステージの登録
				StageSelectImageButton activeWidget;
				activeWidget = (StageSelectImageButton) getSelectedWidget();
				game.setStage(activeWidget.number);

				game.goNextGameState();
			}
			goFight = true;
		}

		if (key.getVirtualKey() == RWTVirtualController.BUTTON_B) {
			if (goFight == true) {
				initializeMessage();
				repaint();
				goFight = false;
			} else {
				game.goPrevGameState();
			}
		}

		if (key.getVirtualKey() == RWTVirtualController.RIGHT) {
			cursorMoveRight();

		}
		if (key.getVirtualKey() == RWTVirtualController.LEFT) {
			cursorMoveLeft();
		}
	}

	@Override
	public void keyTyped(RWTVirtualKey key) {
		// TODO Auto-generated method stub

	}

	// ステージを選んだ際のラベルの書き換え処理
	private void setConfirmMessage() {
		back.setString("");
		message1.setString("これでよろしいですか。");
		message2.setString("Ａ：決定　Ｂ：戻る");
	}

	// ステージを選びなおした際のラベルの書き換え処理
	private void initializeMessage() {
		back.setString("B:ｷｬﾗｸﾀｰ選択画面へ");
		message1.setString("ステージを選択して下さい。");
		message2.setString("Ａ：決定");
	}
}
