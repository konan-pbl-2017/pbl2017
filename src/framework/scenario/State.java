package framework.scenario;

import java.util.Enumeration;
import java.util.Hashtable;

public class State {
	private FSM owner = null;
	private Hashtable<Event, State> transitions = new Hashtable<Event, State>();
	
	public State() {
	}

	public State(Hashtable<Event, State> transitions) {
		this.transitions = transitions;
	}
	
	public void addTransition(Event e, State s) {
		transitions.put(e, s);
	}
	
	public Enumeration<Event> getEvents() {
		return transitions.keys();
	}
	
	public State getSuccessor(Event e) {
		if (!transitions.containsKey(e)) return null;
		return transitions.get(e);
	}
	
	public void setOwner(FSM fsm) {
		owner = fsm;
	}
	
	public FSM getOwner() {
		return owner;
	}
}
