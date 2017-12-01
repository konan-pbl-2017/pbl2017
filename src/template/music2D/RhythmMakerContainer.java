package template.music2D;

import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import framework.RWT.RWTButton;
import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTLabel;
import framework.RWT.RWTVirtualController;
import framework.RWT.RWTVirtualKey;
import framework.audio.BGM3D;
import framework.audio.Sound3D;

public class RhythmMakerContainer extends RWTContainer {
	private RWTCanvas3D canvas;
	private RWTButton startButton;
	private RWTButton stopButton;
	private Sound3D music = BGM3D.registerBGM("data\\fight.wav");
	private long startTime;
	private FileWriter filewriter;
	private RWTLabel keyLabel;

	@Override
	public void build(GraphicsConfiguration gc) {
		if (gc != null) {
			canvas = new RWTCanvas3D(gc);
		} else {
			canvas = new RWTCanvas3D();
		}
		canvas.setRelativePosition(0.0f, 0.0f);
		canvas.setRelativeSize(0.0f, 0.0f);
		addCanvas(canvas);
		
		Font f = new Font("", Font.PLAIN, 30);
		startButton = new RWTButton("Play!");
		startButton.setFont(f);
		startButton.setRelativePosition(0.2f, 0.3f);
		startButton.setRelativeHeight(0.1f);
		startButton.setRelativeWidth(0.2f);
		addSelectableWidget(startButton, 0, 0);
		
		stopButton = new RWTButton("Stop!");
		stopButton.setFont(f);
		stopButton.setRelativePosition(0.7f, 0.3f);
		stopButton.setRelativeHeight(0.1f);
		stopButton.setRelativeWidth(0.2f);
		addSelectableWidget(stopButton, 1, 0);
		
		Font f2 = new Font("", Font.PLAIN, 60);
		keyLabel = new RWTLabel();
		keyLabel.setRelativePosition(0.5f, 0.7f);
		keyLabel.setFont(f2);
		keyLabel.setVisible(false);
		addWidget(keyLabel);
		
		repaint();
	}

	@Override
	public void keyPressed(RWTVirtualKey key) {
		if (key.getVirtualKey() == RWTVirtualController.RIGHT) {
			cursorMoveRight();
		} else if (key.getVirtualKey() == RWTVirtualController.LEFT) {
			cursorMoveLeft();
		}
		try {
			if (key.getVirtualKey() == RWTVirtualController.BUTTON_A && getSelectedWidget() == startButton) {
				// çƒê∂
					File file = new File("data\\rhythm.txt");
					filewriter = new FileWriter(file);
					BGM3D.playBGM(music);
					startTime = System.currentTimeMillis();
			} else if (key.getVirtualKey() == RWTVirtualController.BUTTON_A && getSelectedWidget() == stopButton) {
				// í‚é~
				music.stop();
				filewriter.close();
			} else if (filewriter != null) {
				keyLabel.setString("" + (key.getVirtualKey() + key.getPlayer() * 8));
				keyLabel.setVisible(true);
				repaint();
				filewriter.write((key.getVirtualKey() + key.getPlayer() * 8) + "," + (System.currentTimeMillis() - startTime) + "\n");
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	@Override
	public void keyReleased(RWTVirtualKey key) {
		if (filewriter != null) {
			keyLabel.setVisible(false);
			repaint();
		}
	}

	@Override
	public void keyTyped(RWTVirtualKey key) {
	}

}
