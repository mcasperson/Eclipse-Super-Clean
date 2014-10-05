package superclean;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.wst.server.core.IServer.IOperationListener;

abstract public class CountOperationListener implements IOperationListener {

	private final int count;
	private final AtomicInteger doneCount = new AtomicInteger(0);
	
	public int getCount() {
		return count;
	}
	
	public AtomicInteger getDoneCount() {
		return doneCount;
	}
	
	public CountOperationListener(final int count) {
		this.count = count;
	}


}
