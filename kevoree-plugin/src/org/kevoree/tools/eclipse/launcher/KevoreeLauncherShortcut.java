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
			String projectPath = activeProject.getName();

			String nodeName = "node0";

			IPreferenceStore store = Activator.getDefault()
					.getPreferenceStore();
			String version = store.getString(PreferenceConstants.P_STRING);

			File kevoreeAnnotator = KevoreeMavenResolver.resolve(
					"org.kevoree.tools",
					"org.kevoree.tools.annotator.standalone", version, "jar");

			// parameters.setMainClass("org.kevoree.tools.annotator.App");
			// parameters.getProgramParametersList().add(CompilerPaths.getModuleOutputDirectory(module,
			// false).getPath());

			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();
			ILaunchConfigurationType conftype = manager
					.getLaunchConfigurationType(ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager
					.getLaunchConfigurations(conftype);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration1 = configurations[i];
				if (configuration1.getName().equals("Run KevScript")) {
					configuration1.delete();
					break;
				}
			}
			ILaunchConfigurationWorkingCopy workingCopy = conftype.newInstance(
					null, "Run KevScript");

			IVMInstall jre = JavaRuntime.getDefaultVMInstall();
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
					jre.getName());
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, jre
							.getVMInstallType().getId());

			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					projectPath);
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME,
					"org.kevoree.tools.annotator.App");
			workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS,
					"target/classes org.kevoree.platform.standalone.App");
			// System.err.println(ResourcesPlugin.getWorkspace().getRoot().getFile(new
			// Path(modelPath)).getRawLocation().toOSString());

			// execArray = new String[]{getJava(), "-Dmodel.debug.port=" +
			// modelDebugPort.toString(), "-Dnode.bootstrap=" +
			// bootstrapFile.getAbsolutePath(), "-Dnode.name=" + nodeName,
			// "-cp",
			// classPathBuf.toString(), "org.kevoree.tools.annotator.App",
			// classesDirectory, "org.kevoree.platform.standalone.test.App"};
			workingCopy.setAttribute(ATTR_VM_ARGUMENTS,
					"-Dmodel.debug.port=9080 -Dnode.bootstrap="
							+ file.getRawLocation().toOSString()
							+ " -Dnode.name=" + nodeName);

			File jdkHome = jre.getInstallLocation();
			IPath toolsPath = new Path(jdkHome.getAbsolutePath()).append("lib")
					.append("tools.jar");
			IRuntimeClasspathEntry toolsEntry = JavaRuntime
					.newArchiveRuntimeClasspathEntry(toolsPath);
			toolsEntry
					.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

			IPath bootstrapPath = new Path(kevoreeAnnotator.getAbsolutePath());
			IRuntimeClasspathEntry bootstrapEntry = JavaRuntime
					.newArchiveRuntimeClasspathEntry(bootstrapPath);
			bootstrapEntry
					.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

			IJavaProject javaProject = JavaCore.create(activeProject);
			IRuntimeClasspathEntry bootstrapEntry2 = JavaRuntime
					.newDefaultProjectClasspathEntry(javaProject);
			bootstrapEntry2
					.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

			IPath bootstrapPath1 = new Path(KevoreeMavenResolver.resolve(
					"org.kevoree.platform", "org.kevoree.platform.standalone",
					version, "jar").getAbsolutePath());
			IRuntimeClasspathEntry bootstrapEntry1 = JavaRuntime
					.newArchiveRuntimeClasspathEntry(bootstrapPath1);
			bootstrapEntry1
					.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

			IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
			IRuntimeClasspathEntry systemLibsEntry = JavaRuntime
					.newRuntimeContainerClasspathEntry(systemLibsPath,
							IRuntimeClasspathEntry.STANDARD_CLASSES);
			List classpath = new ArrayList();
			classpath.add(toolsEntry.getMemento());
			classpath.add(bootstrapEntry.getMemento());
			classpath.add(bootstrapEntry1.getMemento());
			classpath.add(bootstrapEntry2.getMemento());
			classpath.add(systemLibsEntry.getMemento());
			workingCopy.setAttribute(ATTR_CLASSPATH, classpath);
			workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);

			// workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);

			// ist sourcepath = new ArrayList();

			// IProject project1 = getProject(projectPath);
			/*
			 * IRuntimeClasspathEntry lSourceLookup = JavaRuntime
			 * .newStringVariableClasspathEntry
			 * ("/HelloWorlKevoree/src/main/java");
			 * 
			 * List<String> lSourceList = null;
			 * 
			 * if (workingCopy
			 * .hasAttribute(IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH
			 * )) { lSourceList = workingCopy.getAttribute(
			 * IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH, (List) null);
			 * } else { lSourceList = new ArrayList<String>(); }
			 * 
			 * 
			 * 
			 * //create runtime classpath entry for standard lib files an
			 * sources
			 * 
			 * 
			 * // IJavaProject javaProject1 = JavaCore.create(project1); //
			 * IRuntimeClasspathEntry bootstrapEntry3 = JavaRuntime //
			 * .newDefaultProjectClasspathEntry(javaProject1); //
			 * bootstrapEntry3 //
			 * .setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			 * 
			 * // sourcepath.add(bootstrapEntry3.getMemento());
			 * 
			 * // Add our standard A4L Source Path
			 * lSourceList.add(lSourceLookup.getMemento());
			 * 
			 * workingCopy.setAttribute(ATTR_DEFAULT_SOURCE_PATH, false);
			 * workingCopy.setAttribute(ATTR_SOURCE_PATH, lSourceList);
			 */

			/*
			 * workingCopy.setAttribute(ATTR_VM_ARGUMENTS,
			 * "-Djava.endorsed.dirs=\"..\\common\\endorsed\"" +
			 * "-Dcatalina.base=\"..\"" + "-Dcatalina.home=\"..\"" +
			 * "-Djava.io.tmpdir=\"..\\temp\"");
			 */

			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					"${workspace_loc:/" + projectPath + "}");
			ILaunchConfiguration configuration1 = null;
			configuration1 = workingCopy.doSave();

			if (mode.equals("debug")) {
				configuration1.launch(ILaunchManager.DEBUG_MODE,
						new NullProgressMonitor());
			} else {
				configuration1.launch(ILaunchManager.RUN_MODE,
						new NullProgressMonitor());
			}

			try {
				PlatformUI.getWorkbench().getBrowserSupport()
						.getExternalBrowser()
						.openURL(new URL("http://kwe.braindead.fr/"));
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
			String projectPath = activeProject.getName();

			String nodeName = "node0";

			IPreferenceStore store = Activator.getDefault()
					.getPreferenceStore();
			String version = store.getString(PreferenceConstants.P_STRING);

			File kevoreeAnnotator = KevoreeMavenResolver.resolve(
					"org.kevoree.tools",
					"org.kevoree.tools.annotator.standalone", version, "jar");

			// parameters.setMainClass("org.kevoree.tools.annotator.App");
			// parameters.getProgramParametersList().add(CompilerPaths.getModuleOutputDirectory(module,
			// false).getPath());

			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();
			ILaunchConfigurationType conftype = manager
					.getLaunchConfigurationType(ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager
					.getLaunchConfigurations(conftype);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration1 = configurations[i];
				if (configuration1.getName().equals("Run KevScript")) {
					configuration1.delete();
					break;
				}
			}
			ILaunchConfigurationWorkingCopy workingCopy = conftype.newInstance(
					null, "Run KevScript");

			IVMInstall jre = JavaRuntime.getDefaultVMInstall();
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
					jre.getName());
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, jre
							.getVMInstallType().getId());

			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					projectPath);
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME,
					"org.kevoree.tools.annotator.App");
			workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS,
					"target/classes org.kevoree.platform.standalone.App");
			// System.err.println(ResourcesPlugin.getWorkspace().getRoot().getFile(new
			// Path(modelPath)).getRawLocation().toOSString());

			// execArray = new String[]{getJava(), "-Dmodel.debug.port=" +
			// modelDebugPort.toString(), "-Dnode.bootstrap=" +
			// bootstrapFile.getAbsolutePath(), "-Dnode.name=" + nodeName,
			// "-cp",
			// classPathBuf.toString(), "org.kevoree.tools.annotator.App",
			// classesDirectory, "org.kevoree.platform.standalone.test.App"};
			workingCopy.setAttribute(ATTR_VM_ARGUMENTS,
					"-Dmodel.debug.port=9080 -Dnode.bootstrap="
							+ file.getRawLocation().toOSString()
							+ " -Dnode.name=" + nodeName);

			File jdkHome = jre.getInstallLocation();
			IPath toolsPath = new Path(jdkHome.getAbsolutePath()).append("lib")
					.append("tools.jar");
			IRuntimeClasspathEntry toolsEntry = JavaRuntime
					.newArchiveRuntimeClasspathEntry(toolsPath);
			toolsEntry
					.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

			IPath bootstrapPath = new Path(kevoreeAnnotator.getAbsolutePath());
			IRuntimeClasspathEntry bootstrapEntry = JavaRuntime
					.newArchiveRuntimeClasspathEntry(bootstrapPath);
			bootstrapEntry
					.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

			IJavaProject javaProject = JavaCore.create(activeProject);
			IRuntimeClasspathEntry bootstrapEntry2 = JavaRuntime
					.newDefaultProjectClasspathEntry(javaProject);
			bootstrapEntry2
					.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

			IPath bootstrapPath1 = new Path(KevoreeMavenResolver.resolve(
					"org.kevoree.platform", "org.kevoree.platform.standalone",
					version, "jar").getAbsolutePath());
			IRuntimeClasspathEntry bootstrapEntry1 = JavaRuntime
					.newArchiveRuntimeClasspathEntry(bootstrapPath1);
			bootstrapEntry1
					.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

			IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
			IRuntimeClasspathEntry systemLibsEntry = JavaRuntime
					.newRuntimeContainerClasspathEntry(systemLibsPath,
							IRuntimeClasspathEntry.STANDARD_CLASSES);
			List classpath = new ArrayList();
			classpath.add(toolsEntry.getMemento());
			classpath.add(bootstrapEntry.getMemento());
			classpath.add(bootstrapEntry1.getMemento());
			classpath.add(bootstrapEntry2.getMemento());
			classpath.add(systemLibsEntry.getMemento());
			workingCopy.setAttribute(ATTR_CLASSPATH, classpath);
			workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);

			// workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);

			// ist sourcepath = new ArrayList();

			// IProject project1 = getProject(projectPath);
			/*
			 * IRuntimeClasspathEntry lSourceLookup = JavaRuntime
			 * .newStringVariableClasspathEntry
			 * ("/HelloWorlKevoree/src/main/java");
			 * 
			 * List<String> lSourceList = null;
			 * 
			 * if (workingCopy
			 * .hasAttribute(IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH
			 * )) { lSourceList = workingCopy.getAttribute(
			 * IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH, (List) null);
			 * } else { lSourceList = new ArrayList<String>(); }
			 * 
			 * 
			 * 
			 * //create runtime classpath entry for standard lib files an
			 * sources
			 * 
			 * 
			 * // IJavaProject javaProject1 = JavaCore.create(project1); //
			 * IRuntimeClasspathEntry bootstrapEntry3 = JavaRuntime //
			 * .newDefaultProjectClasspathEntry(javaProject1); //
			 * bootstrapEntry3 //
			 * .setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			 * 
			 * // sourcepath.add(bootstrapEntry3.getMemento());
			 * 
			 * // Add our standard A4L Source Path
			 * lSourceList.add(lSourceLookup.getMemento());
			 * 
			 * workingCopy.setAttribute(ATTR_DEFAULT_SOURCE_PATH, false);
			 * workingCopy.setAttribute(ATTR_SOURCE_PATH, lSourceList);
			 */

			/*
			 * workingCopy.setAttribute(ATTR_VM_ARGUMENTS,
			 * "-Djava.endorsed.dirs=\"..\\common\\endorsed\"" +
			 * "-Dcatalina.base=\"..\"" + "-Dcatalina.home=\"..\"" +
			 * "-Djava.io.tmpdir=\"..\\temp\"");
			 */

			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					"${workspace_loc:/" + projectPath + "}");
			ILaunchConfiguration configuration1 = null;
			configuration1 = workingCopy.doSave();

			if (mode.equals("debug")) {
				configuration1.launch(ILaunchManager.DEBUG_MODE,
						new NullProgressMonitor());
			} else {
				configuration1.launch(ILaunchManager.RUN_MODE,
						new NullProgressMonitor());
			}

			try {
				PlatformUI.getWorkbench().getBrowserSupport()
						.getExternalBrowser()
						.openURL(new URL("http://kwe.braindead.fr/"));
				// PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

}