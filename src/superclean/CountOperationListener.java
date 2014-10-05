package superclean;

import org.eclipse.wst.server.core.IServer.IOperationListener;

abstract public class CountOperationListener implements IOperationListener {

	private final int count;
	private int doneCount = 0;
	
	public int getCount() {
		return count;
	}
	
	public int getDoneCount() {
		return doneCount;
	}

	public void setDoneCount(final int doneCount) {
		this.doneCount = doneCount;
	}
	
	public CountOperationListener(final int count) {
		this.count = count;
	}


}
