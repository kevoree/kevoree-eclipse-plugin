package org.kevoree.tools.eclipse.launcher;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.IPreferenceStore;
import org.kevoree.tools.eclipse.Activator;
import org.kevoree.tools.eclipse.builder.KevoreeMavenResolver;
import org.kevoree.tools.eclipse.preferences.PreferenceConstants;


public class KevoreeLauncher implements
		ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		String modelPath = configuration.getAttribute(
				KevoreeLauncherConfigurationConstants.LAUNCH_MODEL_PATH, "");

		String projectPath = configuration.getAttribute(
				KevoreeLauncherConfigurationConstants.KEVSCRIPT_LAUNCH_PROJECT, "");

		String nodeName = configuration.getAttribute(
				KevoreeLauncherConfigurationConstants.KEVSCRIPT_LAUNCH_NODENAME, "");

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String version =store.getString(PreferenceConstants.P_STRING);

		
		
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
			if (configuration1.getName()
					.equals("Run KevScript")) {
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
				getProject(projectPath).getName());
		workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME,
				"org.kevoree.tools.annotator.App");
		workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, "target/classes org.kevoree.platform.standalone.App");
		//System.err.println(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(modelPath)).getRawLocation().toOSString());
		
		
		 //execArray = new String[]{getJava(), "-Dmodel.debug.port=" + modelDebugPort.toString(), "-Dnode.bootstrap=" + bootstrapFile.getAbsolutePath(), "-Dnode.name=" + nodeName, "-cp", classPathBuf.toString(), "org.kevoree.tools.annotator.App", classesDirectory, "org.kevoree.platform.standalone.test.App"};
		workingCopy.setAttribute(ATTR_VM_ARGUMENTS, "-Dmodel.debug.port=9080 -Dnode.bootstrap=" + ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(modelPath)).getRawLocation().toOSString() + " -Dnode.name="+nodeName);

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
		
		
		IProject project = getProject(projectPath); 
		IJavaProject javaProject = JavaCore.create(project); 
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

		/*
		 * workingCopy.setAttribute(ATTR_VM_ARGUMENTS,
		 * "-Djava.endorsed.dirs=\"..\\common\\endorsed\"" +
		 * "-Dcatalina.base=\"..\"" + "-Dcatalina.home=\"..\"" +
		 * "-Djava.io.tmpdir=\"..\\temp\"");
		 */

		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,"${workspace_loc:"+ getProject(projectPath).getName()+"}");
		ILaunchConfiguration configuration1 = workingCopy.doSave();
		configuration1.launch(ILaunchManager.RUN_MODE,
				new NullProgressMonitor());

		
		

	}
	
	public IProject getProject(String projectpath){
		return  ResourcesPlugin.getWorkspace().getRoot().getProject(projectpath);
	}
}
