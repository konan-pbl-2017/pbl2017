package framework.gameMain;

import java.awt.GraphicsConfiguration;

import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTLabel;
import framework.scenario.ScenarioManager;

/**
 * シナリオゲーム用画面
 * @author Nitta
 *
 */
abstract public class BaseScenarioGameContainer extends RWTContainer {
	protected RWTCanvas3D canvas;
	protected RWTLabel dialog;
	protected ScenarioManager scenario;
	
	public BaseScenarioGameContainer() {
		scenario = null;
	}

	public BaseScenarioGameContainer(ScenarioManager scenario) {
		this.scenario = scenario;
	}

	@Override
	public void build(GraphicsConfiguration gc) {				
		if (gc != null) {
			canvas = new RWTCanvas3D(gc);
		} else {
			canvas = new RWTCanvas3D();
		}
		dialog = new RWTLabel();
	}

	public void dialogOpen() {
		dialog.setVisible(true);
		repaint();
	}

	public void dialogClose() {
		dialog.setVisible(false);
		repaint();
	}

	public void dialogMessage(String message) {
		dialog.setString(message);
		repaint();
	}

	public boolean isDialogOpen() {
		return dialog.isVisible();
	}

	public void setScenario(ScenarioManager scenario) {
		this.scenario = scenario;
	}
}
