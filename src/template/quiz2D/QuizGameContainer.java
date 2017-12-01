package template.quiz2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;

import framework.RWT.RWTButton;
import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTLabel;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.RWT.RWTWidget;
import framework.gameMain.BaseScenarioGameContainer;
import framework.scenario.ScenarioManager;

/**
 * クイズゲーム用画面
 * @author Nitta
 *
 */
public class QuizGameContainer extends BaseScenarioGameContainer {
	
	private RWTButton[] optionButtons = new RWTButton[4];

	public QuizGameContainer() {
		super();
	}

	@Override
	public void build(GraphicsConfiguration gc) {				
		super.build(gc);
		canvas.setRelativePosition(0.0f, 0.0f);		// 3D表示部の左上端
		canvas.setRelativeSize(0.5f, 1.0f);		// 3D表示部のサイズ
		addCanvas(canvas);
		
		dialog.setRelativePosition(0.55f, 0.2f);	// ダイアログの左上端
		dialog.setFont(new Font("", Font.PLAIN, 12));	// 文字のフォント
		dialog.setColor(Color.WHITE);				// 文字の色
		addWidget(dialog);
		
		Font f = new Font("", Font.PLAIN, 30);
		optionButtons[0] = new RWTButton("1");
		optionButtons[0].setFont(f);
		optionButtons[0].setRelativePosition(0.5f, 0.3f);
		optionButtons[0].setRelativeHeight(0.2f);
		optionButtons[0].setRelativeWidth(0.2f);
		addSelectableWidget(optionButtons[0], 0, 0);
		
		optionButtons[1] = new RWTButton("2");
		optionButtons[1].setFont(f);
		optionButtons[1].setRelativePosition(0.75f, 0.3f);
		optionButtons[1].setRelativeHeight(0.2f);
		optionButtons[1].setRelativeWidth(0.2f);
		addSelectableWidget(optionButtons[1], 1, 0);		
		
		optionButtons[2] = new RWTButton("3");
		optionButtons[2].setFont(f);
		optionButtons[2].setRelativePosition(0.5f, 0.7f);
		optionButtons[2].setRelativeHeight(0.2f);
		optionButtons[2].setRelativeWidth(0.2f);
		addSelectableWidget(optionButtons[2], 0, 1);
		
		optionButtons[3] = new RWTButton("4");
		optionButtons[3].setFont(f);
		optionButtons[3].setRelativePosition(0.75f, 0.7f);
		optionButtons[3].setRelativeHeight(0.2f);
		optionButtons[3].setRelativeWidth(0.2f);
		addSelectableWidget(optionButtons[3], 1, 1);		
		
		repaint();
	}

	public void showOption(int n, String option) {
		optionButtons[n].setLabel(option);
	}

	@Override
	public void keyPressed(RWTVirtualKey key) {
		if (key.getVirtualKey() == RWTVirtualController.RIGHT) {
			cursorMoveRight();
		} else if (key.getVirtualKey() == RWTVirtualController.LEFT) {
			cursorMoveLeft();
		} else if (key.getVirtualKey() == RWTVirtualController.UP) {
			cursorMoveUp();
		} else if (key.getVirtualKey() == RWTVirtualController.DOWN) {
			cursorMoveDown();
		}
	}

	@Override
	public void keyReleased(RWTVirtualKey key) {
		if (key.getPlayer() == 0 && key.getVirtualKey() == RWTVirtualController.BUTTON_A) {
			RWTWidget selected = getSelectedWidget();
			for (int i = 0; i < 4; i++) {
				if (selected == optionButtons[i]) {
					scenario.fire(optionButtons[i].getLabel());
				}
			}
		}
	}

	@Override
	public void keyTyped(RWTVirtualKey key) {
	}
}
