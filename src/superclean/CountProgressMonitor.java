package superclean;

import java.util.concurrent.atomic.AtomicInteger;

abstract public class CountProgressMonitor extends ProgressMonitor {

	private final int count;
	private final AtomicInteger doneCount = new AtomicInteger(0);
	
	public int getCount() {
		return count;
	}
	
	public AtomicInteger getDoneCount() {
		return doneCount;
	}
	public CountProgressMonitor(final int count) {
		this.count = count;
	}


}
