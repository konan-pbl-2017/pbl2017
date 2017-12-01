package template.music2D;

import java.awt.Color;
import java.awt.GraphicsConfiguration;

import framework.RWT.RWTCanvas3D;
import framework.RWT.RWTContainer;
import framework.RWT.RWTLine;
import framework.RWT.RWTVirtualKey;

public class MusicContainer extends RWTContainer {
	private RWTCanvas3D canvas;
	private RWTLine line1;
	private RWTLine line2;
	private RWTLine line3;

	@Override
	public void build(GraphicsConfiguration gc) {

		if (gc != null) {
			canvas = new RWTCanvas3D(gc);
		} else {
			canvas = new RWTCanvas3D();
		}
		canvas.setRelativePosition(0.0f, 0.0f);
		canvas.setRelativeSize(1.0f, 1.0f);
		addCanvas(canvas);

		line1 = new RWTLine();
		line1.setRelativePosition(0f, 0.7f, 1.0f, 0.7f);
		canvas.addWidget(line1);

		line2 = new RWTLine();
		line2.setRelativePosition(0f, 0.6f, 1.0f, 0.6f);
		line2.setColor(Color.blue);
		canvas.addWidget(line2);

		line3 = new RWTLine();
		line3.setRelativePosition(0f, 0.8f, 1.0f, 0.8f);
		line3.setColor(Color.blue);
		canvas.addWidget(line3);

		repaint();
	}

	// RWTë§Ç≈ÇÕÉCÉxÉìÉgèàóùÇÇµÇ»Ç¢
	@Override
	public void keyPressed(RWTVirtualKey key) {
	}

	@Override
	public void keyReleased(RWTVirtualKey key) {
	}

	@Override
	public void keyTyped(RWTVirtualKey key) {
	}
}
