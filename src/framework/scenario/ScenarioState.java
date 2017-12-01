package framework.scenario;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class ScenarioState extends State {
	private Hashtable<Event, Event> transitionSyncs = new Hashtable<Event, Event>();
	private Hashtable<Event, ArrayList<State>> transitionGuards = new Hashtable<Event, ArrayList<State>>();
	private Hashtable<Event, ScenarioAction> transitionActions = new Hashtable<Event, ScenarioAction>();
	private String message = null;
	
	public ScenarioState() {
		super();
	}

	public ScenarioState(Hashtable<Event, State> transitions, 
			Hashtable<Event, Event> transitionSyncs, 
			Hashtable<Event, ArrayList<State>> transitionGuards, 
			Hashtable<Event, ScenarioAction> transitionActions,
			String message) {
		super(transitions);
		this.transitionSyncs = transitionSyncs;
		this.transitionGuards = transitionGuards;
		this.transitionActions = transitionActions;
		this.message = message;
	}
	
	public void addTransition(Event event, State succ, Event syncEvent, ArrayList<State> guards, ScenarioAction action) {
		addTransition(event, succ);
		if (syncEvent != null) transitionSyncs.put(event, syncEvent);
		if (guards != null) transitionGuards.put(event, guards);
		if (action != null) transitionActions.put(event, action);
	}
	
	Event canTrans(Event event) {
		Enumeration<Event> events = getEvents();
		while (events.hasMoreElements()) {
			Event e = events.nextElement();
			if (event == e || event == transitionSyncs.get(e)) {
				ArrayList<State> guardStates = transitionGuards.get(e);
				boolean bSatisfyGuard = true;
				for (int n = 0; n < guardStates.size(); n++) {
					State s = guardStates.get(n);
					if (s.getOwner().getCurrentState() != s) {
						bSatisfyGuard = false;
						break;
					}
				}
				if (bSatisfyGuard) return e;
			}
		}
		return null;
	}

	public ScenarioAction getAction(Event e) {
		return transitionActions.get(e);
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
