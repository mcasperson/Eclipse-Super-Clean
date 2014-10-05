package superclean.actions;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.server.core.*;

import superclean.CountOperationListener;
import superclean.CountProgressMonitor;
import superclean.ProgressMonitor;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		/*
		 * Get the list of servers
		 */
		final IServer[] servers = ServerCore.getServers();
		
		/*
		 * This will be called as the servers are cleaned
		 */
		final CountProgressMonitor publishMonitor = new CountProgressMonitor(servers.length){
			@Override
			public void done() {
				this.setDoneCount(this.getDoneCount() + 1);
				if (this.getDoneCount() == this.getCount()) {
					MessageDialog.openInformation(
							window.getShell(),
							"Super Clean",
							"All projects and servers have been cleaned");	
				}
			}
		};
		
		/*
		 * This will be called as the projects are cleaned
		 */
		final ProgressMonitor cleanProgress = new ProgressMonitor() {

			@Override
			public void done() {
			

				
				/*
				 * When the projects are clean, publish them to the servers
				 */
				for (final IServer server : servers) {
					server.publish(IServer.PUBLISH_CLEAN, publishMonitor);
				}
			}
		};
		
		/*
		 * This will be called as the servers are stopped
		 */
		final CountOperationListener stopProgress = new CountOperationListener(servers.length) {

			@Override
			public void done(IStatus arg0) {
				this.setDoneCount(this.getDoneCount() + 1);
				if (this.getDoneCount() == this.getCount()) {
					/*
					 * When all the servers are stopped, clean the projects
					 */
					try {
						ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, cleanProgress);
					} catch (final CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		
		/*
		 * Initiate the process by stopping the servers
		 */
		for (final IServer server : servers) {
			server.stop(false, stopProgress);
		}
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}