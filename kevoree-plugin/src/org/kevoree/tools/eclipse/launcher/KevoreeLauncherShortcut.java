package org.kevoree.tools.eclipse.launcher;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.kevoree.tools.eclipse.Activator;
import org.kevoree.tools.eclipse.builder.KevoreeMavenResolver;
import org.kevoree.tools.eclipse.preferences.PreferenceConstants;

public class KevoreeLauncherShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		try {
			org.eclipse.jface.viewers.TreeSelection sel = (TreeSelection) selection;
			IFile file = (IFile) sel.getFirstElement();
			IProject activeProject = file.getProject();
			
			String nodeName = "node0";

			KevoreeLauncher.runMavenGoal(activeProject, "compile kev:run", file.getRawLocation().toOSString(), nodeName, mode);
		

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

			KevoreeLauncher.runMavenGoal(activeProject, "compile kev:run", file.getRawLocation().toOSString(), nodeName, mode);
			
			
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
