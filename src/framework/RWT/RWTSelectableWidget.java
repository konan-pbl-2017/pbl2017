package framework.RWT;
import java.awt.Dimension;
import java.awt.Point;

/**
 * アクティブになるために必要なものです。
 * @author Wataru
 *
 */
public interface RWTSelectableWidget {

	public abstract void selected();
	
	public abstract void deselected();
	
	public abstract int getAbsoluteX();

	public abstract int getAbsoluteY();

	public abstract int getAbsoluteWidth();
	
	public abstract int getAbsoluteHeight();
}
