package org.kevoree.tools.eclipse.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class KevoreeCompilerLauncherShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
			org.eclipse.jface.viewers.TreeSelection sel = (TreeSelection) selection;
			IFile file = (IFile) sel.getFirstElement();
			IProject activeProject = file.getProject();
			String nodeName = "node0";
			try {
				if (activeProject.hasNature("org.nodeclipse.ui.NodeNature"))
					KevoreeLauncher.runGruntGoal(activeProject,file.getRawLocation().toOSString(), nodeName,true);
				else
					KevoreeLauncher.runMavenGoal(activeProject, "kev:run", file.getRawLocation().toOSString(), nodeName, mode);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	@Override
	public void launch(IEditorPart editor, String mode) {
			IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
			IFile file = input.getFile();
			IProject activeProject = file.getProject();
			
			String nodeName = "node0";

			try {
				if (activeProject.hasNature("org.nodeclipse.ui.NodeNature"))
					KevoreeLauncher.runGruntGoal(activeProject,file.getRawLocation().toOSString(), nodeName,true);
				else
					KevoreeLauncher.runMavenGoal(activeProject, "kev:run", file.getRawLocation().toOSString(), nodeName, mode);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

}
