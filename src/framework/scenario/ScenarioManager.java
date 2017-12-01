package framework.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

public class ScenarioManager {
	private IWorld realWorld;
	private Hashtable<String, ScenarioFSM> stateMachines = new Hashtable<String, ScenarioFSM>();
	private Hashtable<String, ScenarioState> allStates = new Hashtable<String, ScenarioState>();
	private Hashtable<String, Event> allEvents = new Hashtable<String, Event>();
	
	public ScenarioManager(String xmlFileName, IWorld realWorld) {
		this.realWorld = realWorld;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
			factory.setValidating(true);
			factory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
	
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new DefaultHandler());
			Document document = builder.parse(xmlFileName);
	
			// óLå¿èÛë‘É}ÉVÉìÇÃçÏê¨ÅAèÛë‘ÇÃìoò^ÅAÉCÉxÉìÉgÇÃìoò^
			Hashtable<ScenarioState, NodeList> allTrans = new Hashtable<ScenarioState, NodeList>();
			NodeList scenario = document.getChildNodes();
			NodeList fsmNodes = scenario.item(0).getChildNodes();
			for (int n = 0; n < fsmNodes.getLength(); n++) {
				Node fsmNode = fsmNodes.item(n);
				if (fsmNode.getNodeName().equals("FSM")) {
					Hashtable<String, State> fsmStates = new Hashtable<String, State>();
					NodeList stateNodes = fsmNode.getChildNodes();
					for (int m = 0; m < stateNodes.getLength(); m++) {
						Node stateNode = stateNodes.item(m);
						if (stateNode.getNodeName().equals("State")) {
							ScenarioState state = new ScenarioState();
							String stateName = stateNode.getAttributes().getNamedItem("name").getNodeValue();
							fsmStates.put(stateName, state);
							allStates.put(stateName, state);
							Node stateMessageNode = stateNode.getAttributes().getNamedItem("message");
							if (stateMessageNode != null) state.setMessage(stateMessageNode.getNodeValue());
							NodeList eventNodes = stateNode.getChildNodes();
							allTrans.put(state, eventNodes);
							for (int l = 0; l < eventNodes.getLength(); l++) {
								Node eventNode = eventNodes.item(l);
								if (eventNode.getNodeName().equals("Event")) {
									String eventName = eventNode.getAttributes().getNamedItem("name").getNodeValue();
									Event e = allEvents.get(eventName);
									if (e == null) {
										e = new Event(eventName);
										allEvents.put(eventName, e);
									}							
								}
							}
						}
					}
					String initialStateName = fsmNode.getAttributes().getNamedItem("initial").getNodeValue();
					State initialState = fsmStates.get(initialStateName);
					ScenarioFSM fsm = new ScenarioFSM(initialState, fsmStates, this);
					String fsmName = fsmNode.getAttributes().getNamedItem("name").getNodeValue();
					stateMachines.put(fsmName, fsm);
				}
			}
			
			// èÛë‘ëJà⁄Çê›íËÇ∑ÇÈ
			Set<Entry<ScenarioState, NodeList>> allTransEntries = allTrans.entrySet();
			Iterator<Entry<ScenarioState, NodeList>> it = allTransEntries.iterator();
			while (it.hasNext()) {
				Entry<ScenarioState, NodeList> transEntry = it.next();
				ScenarioState state = transEntry.getKey();
				NodeList eventNodes = transEntry.getValue();
				for (int l = 0; l < eventNodes.getLength(); l++) {
					Node eventNode = eventNodes.item(l);
					if (eventNode.getNodeName().equals("Event")) {
						Node eventNameNode = eventNode.getAttributes().getNamedItem("name");
						Event e = null;
						if (eventNameNode != null) {
							String eventName = eventNameNode.getNodeValue();
							e = allEvents.get(eventName);
						}
						Node nextStateNameNode = eventNode.getAttributes().getNamedItem("trans");
						State nextState = null;
						if (nextStateNameNode != null) {
							String nextStateName = nextStateNameNode.getNodeValue();
							nextState = allStates.get(nextStateName);
						}
						Node syncEventNameNode = eventNode.getAttributes().getNamedItem("sync");
						Event syncEvent = null;
						if (syncEventNameNode != null) {
							String syncEventName = syncEventNameNode.getNodeValue();
							syncEvent = allEvents.get(syncEventName);
						}
						ArrayList<State> guards = new ArrayList<State>();
						Node guardStateNameNode = eventNode.getAttributes().getNamedItem("guard");
						if (guardStateNameNode != null) {
							String guardStateName = guardStateNameNode.getNodeValue();
							State guardState = allStates.get(guardStateName);
							if (guardState != null) guards.add(guardState);
						}
						Node guardStateNameNode2 = eventNode.getAttributes().getNamedItem("guard2");
						if (guardStateNameNode2 != null) {
							String guardStateName2 = guardStateNameNode2.getNodeValue();
							State guardState2 = allStates.get(guardStateName2);
							if (guardState2 != null) guards.add(guardState2);
						}
						Node guardStateNameNode3 = eventNode.getAttributes().getNamedItem("guard3");
						if (guardStateNameNode3 != null) {
							String guardStateName3 = guardStateNameNode3.getNodeValue();
							State guardState3 = allStates.get(guardStateName3);
							if (guardState3 != null) guards.add(guardState3);
						}
						Node actionNameNode = eventNode.getAttributes().getNamedItem("action");
						ScenarioAction action = null;
						if (actionNameNode != null) {
							String actionName = actionNameNode.getNodeValue();
							action = new ScenarioAction(actionName);
						}
						state.addTransition(e, nextState, syncEvent, guards, action);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fire(Event e){
		Collection<ScenarioFSM> fsms = stateMachines.values();
		Iterator<ScenarioFSM> it = fsms.iterator();
		while (it.hasNext()) {
			ScenarioFSM fsm = it.next();
			fsm.trans(e);
		}
	}
	
	public void fire(String eventName) {
		Event e = allEvents.get(eventName);
		if (e == null) return;
		fire(e);
	}

	public void action(ScenarioAction action, Event event, ScenarioState nextState) {
		if (action != null) {
			String sAction = action.getName();
			if (sAction.equals("openDialog")) {
				realWorld.dialogOpen();
			} else if (sAction.equals("closeDialog")) {
				realWorld.dialogClose();
			} else if (sAction.equals("print")) {
				System.out.println(event.getName());
			}
			realWorld.action(sAction, event, nextState);
		}
		if (realWorld.isDialogOpen()) {
			String message = nextState.getMessage();
			if (message != null) {
				realWorld.dialogMessage(message);
				Enumeration<Event> events = nextState.getEvents();
				int n = 0;
				while (events.hasMoreElements()) {
					Event ev = events.nextElement();
					realWorld.showOption(n, ev.getName());
					n++;
				}
			}
		}
	}
}
