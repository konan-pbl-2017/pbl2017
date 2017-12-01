package framework.scenario;

import java.util.Hashtable;

public class ScenarioFSM extends FSM {
	ScenarioManager manager;
	
	public ScenarioFSM(State initialState, Hashtable<String, State> states, ScenarioManager manager) {
		super(initialState, states);
		this.manager = manager;
	}

	public boolean trans(Event e) {
		e = ((ScenarioState)currentState).canTrans(e);
		if (e == null) return false;
		ScenarioAction action = ((ScenarioState)currentState).getAction(e);
		boolean result = super.trans(e);
		manager.action(action, e, (ScenarioState)currentState);
		return result;
	}
}
