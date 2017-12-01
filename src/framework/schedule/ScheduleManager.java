package framework.schedule;

import java.util.Hashtable;

public class ScheduleManager {
	private static ScheduleManager theInstance = null;
	private Hashtable<String, TaskController> taskControllerTable = new Hashtable<String, TaskController>();
	
	private ScheduleManager() {
	}
	
	public static ScheduleManager getInstance() {
		if (theInstance == null) {
			theInstance = new ScheduleManager();
		}
		return theInstance;
	}
	
	public TaskController registerTask(String taskName) {
		TaskController controller = new TaskController();
		taskControllerTable.put(taskName, controller);
		return controller;
	}
	
	public TaskController getController(String taskName) {
		return taskControllerTable.get(taskName);
	}
}
