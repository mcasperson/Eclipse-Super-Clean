package superclean.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.progress.IProgressConstants2;
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
public class SuperCleanAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public SuperCleanAction() {
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
		 * This will be called as the projects are cleaned, and initiates a clean of the servers
		 */
		final ProgressMonitor cleanProgress = new ProgressMonitor() {

			@Override
			public void done() {
				if (servers.length != 0) {
					try {
						final CountProgressMonitor publishMonitor = new CountProgressMonitor(servers.length){
							@Override
							public void done() {
								if (this.getDoneCount().incrementAndGet() == this.getCount()) {
									displayMessage("All projects and servers have been cleaned");
								}
							}
						};
						
						ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
								@Override
								public void run(final IProgressMonitor workspaceProgress) throws CoreException {									
									for (final IServer server : servers) {
										server.publish(IServer.PUBLISH_CLEAN, workspaceProgress);
									}						
								}
							}, publishMonitor
						);
					} catch (CoreException e) {
						displayMessage("An exception was thrown when attempting to clean the servers");
					}
				} else {
					/*
					 * If there were no servers, trigger the done method on the monitor manually
					 */
					displayMessage("All projects and servers have been cleaned");
				}
			}
		};
		
		/*
		 * This will be called as the servers are stopped, and initiates a clean of the projects
		 */
		final ProgressMonitor stopProgressMonitor = new ProgressMonitor() {
			@Override
			public void done() {
				try {
					ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
							@Override
							public void run(IProgressMonitor workspaceProgress) throws CoreException {
								ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, workspaceProgress);								
							}
						}, cleanProgress);
				} catch (CoreException e) {
					displayMessage("An exception was thrown when attempting to clean the projects");
				}				
			}
		};
		
		/*
		 * Initiate the process by stopping the servers
		 */
		if (servers.length != 0) {
			try {
				ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
						@Override
						public void run(final IProgressMonitor workspaceProgress) throws CoreException {
							final CountOperationListener stopProgress = new CountOperationListener(servers.length) {

								@Override
								public void done(IStatus arg0) {
									if (this.getDoneCount().incrementAndGet() == this.getCount()) {
										workspaceProgress.done();
									}
								}
							};
							
							for (final IServer server : servers) {
								server.stop(false, stopProgress);
							}						
						}
					}, stopProgressMonitor
				);
			} catch (CoreException e) {
				displayMessage("An exception was thrown when attempting to stop the servers");
			}
		} else {
			/*
			 * If there were no servers, trigger the done method on the monitor manually
			 */
			stopProgressMonitor.done();
		}
	}
	
	/**
	 * Displays a popup with the provided message
	 * @param message The message to display
	 */
	private void displayMessage(final String message) {
		MessageDialog.openInformation(
				window.getShell(),
				"Super Clean",
				message);
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