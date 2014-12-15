package org.kevoree.tools.eclipse.launcher;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class KevoreeCompilerLauncherShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		try {
			org.eclipse.jface.viewers.TreeSelection sel = (TreeSelection) selection;
			IFile file = (IFile) sel.getFirstElement();
			IProject activeProject = file.getProject();
			
			String nodeName = "node0";

			KevoreeLauncher.runMavenGoal(activeProject, "clean install kev:run", file.getRawLocation().toOSString(), nodeName, mode);
		 

			try {
				PlatformUI.getWorkbench().getBrowserSupport()
						.getExternalBrowser()
						.openURL(new URL("http://editor.kevoree.org/?host=127.0.0.1&port=9000"));
				// PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		try {

			IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
			IFile file = input.getFile();
			IProject activeProject = file.getProject();
			
			String nodeName = "node0";

			KevoreeLauncher.runMavenGoal(activeProject, "clean install kev:run", file.getRawLocation().toOSString(), nodeName, mode);
			
			
			try {
				PlatformUI.getWorkbench().getBrowserSupport()
						.getExternalBrowser()
						.openURL(new URL("http://editor.kevoree.org/?host=127.0.0.1&port=9000"));
				// PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

}
