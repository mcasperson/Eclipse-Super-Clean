package superclean;

abstract public class CountProgressMonitor extends ProgressMonitor {

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
	
	public CountProgressMonitor(final int count) {
		this.count = count;
	}


}
