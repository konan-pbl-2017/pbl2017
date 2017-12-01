package framework.gameMain;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import framework.RWT.RWTFrame3D;
import framework.schedule.ScheduleManager;

/**
 * ゲームの基本クラス
 * @author 新田直也
 *
 */
public abstract class AbstractGame implements Runnable {
	protected RWTFrame3D mainFrame;
	private ScheduledExecutorService t;
	private long minimumFrameTime = 5;
	private long maximumFrameTime = 15;
	private boolean bWaitForRender = true;
	private long prevTime = 0L;

	public abstract RWTFrame3D createFrame3D();
	protected abstract IGameState getCurrentGameState();
	
	public AbstractGame() {
		mainFrame = createFrame3D();
	}

	public void start() {
		if (getCurrentGameState() != null) {
			activateState(getCurrentGameState());
		}
	}

	public void stop() {
		if (getCurrentGameState() != null) {
			deactivateState(getCurrentGameState());
		}
	}
	
	public void setFramePolicy(long minimumFrameTime, boolean bWaitForRender) {
		this.minimumFrameTime = minimumFrameTime;
		this.bWaitForRender = bWaitForRender;
	}

	public void setFramePolicy(long minimumFrameTime, long maximumFrameTime, boolean bWaitForRender) {
		this.minimumFrameTime = minimumFrameTime;
		this.maximumFrameTime = maximumFrameTime;
		this.bWaitForRender = bWaitForRender;
	}
	
	public void run() {
		if (bWaitForRender) {
			// レンダリングの終了と同期してゲームを進める
			ScheduleManager.getInstance().getController("rendering").waitForActivation();
		}
		mainFrame.doRender();
		long curTime = System.currentTimeMillis();
		long interval;
		if (prevTime == 0L) {
			interval = minimumFrameTime;
		} else {
			interval = curTime - prevTime;
			if (interval > maximumFrameTime) {
				interval = maximumFrameTime;
			}
		}
		prevTime = curTime;
		// 今のゲーム状態のupdateを呼ぶ。
		getCurrentGameState().update(mainFrame.getVirtualController(), interval);
	}
	
	protected void activateState(IGameState s) {
		s.init(mainFrame);
		if(s.useTimer()){
			timerRun();
		}
	}

	protected void deactivateState(IGameState s) {
		if(s.useTimer()){
			timerCancel();
		}
	}

	private void timerCancel() {
		if (t != null) t.shutdownNow();
	}

	private void timerRun() {
		t = new ScheduledThreadPoolExecutor(1);
		// scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
		t.scheduleWithFixedDelay(this, minimumFrameTime, minimumFrameTime, TimeUnit.MILLISECONDS);
	}
}