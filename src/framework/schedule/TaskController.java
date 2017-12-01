package framework.schedule;

public class TaskController {
	private boolean bActive = false;
	
	synchronized public boolean activate() {
		if (isActive()) return false;
		setActive(true);
		return true;
	}
	synchronized public void deactivate() {
		synchronized(this) {
			notify();
		}
		setActive(false);
	}
	
	synchronized public void waitForActivation() {
		if (!isActive()) return;
		try {
			synchronized(this) {
				wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setActive(false);	// •s—v‚©‚à‚µ‚ê‚È‚¢
	}
	
	private boolean isActive() {
		return bActive;
	}
	
	private void setActive(boolean flag) {
		bActive = flag;
	}
}
