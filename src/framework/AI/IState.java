package framework.AI;

import java.util.ArrayList;

public interface IState {
	
	abstract ArrayList<IState> getSuccessors();

	abstract void addSuccessor(IState s);
	/**
	 * ’T¸Œ³‚Ìƒm[ƒhæ“¾B
	 * @return
	 * ’T¸Œ³‚Ìƒm[ƒh‚ğ•Ô‚·B
	 */
	abstract IState getParent();
}
