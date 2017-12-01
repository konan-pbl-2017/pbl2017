package framework.scenario;

public interface IWorld {
	abstract public void dialogOpen();
	abstract public void dialogClose();
	abstract public void dialogMessage(String message);
	abstract public void showOption(int n, String option);
	abstract public boolean isDialogOpen();
	abstract public void action(String action, Event event, ScenarioState nextState);
}
