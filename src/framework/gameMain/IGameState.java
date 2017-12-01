package framework.gameMain;
import framework.RWT.RWTFrame3D;
import framework.RWT.RWTVirtualController;

public interface IGameState {
	public abstract void init(RWTFrame3D frame);
	public abstract boolean useTimer();
	public abstract void update(RWTVirtualController virtualController, long interval);
}