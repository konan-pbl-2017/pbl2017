package framework.RWT;
import java.awt.Color;
import java.awt.Graphics;


public class DefaultSelector extends RWTSelector {
	@Override
	public void paint(Graphics g) {
		g.setColor(new Color(0.0f, 0.5f, 1.0f, 0.3f));
		g.fill3DRect(widget.getAbsoluteX() - 5, widget.getAbsoluteY() - 5, widget.getAbsoluteWidth() + 10, widget.getAbsoluteHeight() + 10, true);
	}
}
