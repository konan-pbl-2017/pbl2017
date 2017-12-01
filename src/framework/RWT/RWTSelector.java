package framework.RWT;
import java.awt.Component;
import java.util.ArrayList;

import javax.vecmath.Point2f;

public abstract class RWTSelector extends RWTWidget {
	protected float relativeX = 0.0f;
	protected float relativeY = 0.0f;
	protected float relativeWidth = 0.0f;
	protected float relativeHeight = 0.0f;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;	

	protected RWTSelectableWidget widget = null;
	
	public void setSelectableWidget(RWTSelectableWidget w) {
		widget = w;
	}
	
	public boolean hasWidget() {
		if(widget == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public void adjust(Component parent) {
		x = widget.getAbsoluteX();
		y = widget.getAbsoluteY();
		width = widget.getAbsoluteWidth();
		height = widget.getAbsoluteHeight();
	}
	
	@Override
	public boolean isVisible() {
		if(hasWidget()) {
			x = widget.getAbsoluteX();
			y = widget.getAbsoluteY();
			width = widget.getAbsoluteWidth();
			height = widget.getAbsoluteHeight();
			return visible;
		}
		return false;
	}
}
