package framework.AI;

import java.util.ArrayList;

public abstract class StateMachine {
	protected ArrayList<IState> states = new ArrayList();
	protected IState curState;

}
