package org.kevoree.tools.eclipse.launcher;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.kevoree.tools.eclipse.Activator;
import org.kevoree.tools.eclipse.preferences.PreferenceConstants;

public class KevoreeLauncher implements ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		String modelPath = configuration.getAttribute(
				KevoreeLauncherConfigurationConstants.LAUNCH_MODEL_PATH, "");

		String projectPath = configuration.getAttribute(
				KevoreeLauncherConfigurationConstants.KEVSCRIPT_LAUNCH_PROJECT,
				"");

		String nodeName = configuration
				.getAttribute(
						KevoreeLauncherConfigurationConstants.KEVSCRIPT_LAUNCH_NODENAME,
						"");

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String version = store.getString(PreferenceConstants.P_STRING);

		System.err.println(modelPath);
		System.err.println(nodeName);
		
		runMavenGoal(getProject(projectPath), "compile kev:run",ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(modelPath)).getRawLocation()
				.toOSString(),nodeName,mode);
		
		/*File kevoreeAnnotator = KevoreeMavenResolver.resolve(
				"org.kevoree.tools", "org.kevoree.tools.annotator.standalone",
				version, "jar");

		// parameters.setMainClass("org.kevoree.tools.annotator.App");
		// parameters.getProgramParametersList().add(CompilerPaths.getModuleOutputDirectory(module,
		// false).getPath());*/

		/*ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
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
				getProject(projectPath).getName());
		workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME,
				"org.kevoree.tools.annotator.App");
		workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS,
				"target/classes org.kevoree.platform.standalone.App");
		// System.err.println(ResourcesPlugin.getWorkspace().getRoot().getFile(new
		// Path(modelPath)).getRawLocation().toOSString());

		// execArray = new String[]{getJava(), "-Dmodel.debug.port=" +
		// modelDebugPort.toString(), "-Dnode.bootstrap=" +
		// bootstrapFile.getAbsolutePath(), "-Dnode.name=" + nodeName, "-cp",
		// classPathBuf.toString(), "org.kevoree.tools.annotator.App",
		// classesDirectory, "org.kevoree.platform.standalone.test.App"};
		workingCopy.setAttribute(
				ATTR_VM_ARGUMENTS,
				"-Dkevoree.dev=true -Dmodel.debug.port=9080 -Dnode.bootstrap="
						+ ResourcesPlugin.getWorkspace().getRoot()
								.getFile(new Path(modelPath)).getRawLocation()
								.toOSString() + " -Dnode.name=" + nodeName);

		File jdkHome = jre.getInstallLocation();
		IPath toolsPath = new Path(jdkHome.getAbsolutePath()).append("lib")
				.append("tools.jar");
		IRuntimeClasspathEntry toolsEntry = JavaRuntime
				.newArchiveRuntimeClasspathEntry(toolsPath);
		toolsEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

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

		//workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);

		
		
		
		// ist sourcepath = new ArrayList();

		
//		IProject project1 = getProject(projectPath);
/*		IRuntimeClasspathEntry lSourceLookup = JavaRuntime
				.newStringVariableClasspathEntry("/HelloWorlKevoree/src/main/java");

		List<String> lSourceList = null;

		if (workingCopy
				.hasAttribute(IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH)) {
			lSourceList = workingCopy.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH,
					(List) null);
		} else {
			lSourceList = new ArrayList<String>();
		}

		
		
		//create runtime classpath entry for standard lib files an sources

		
		// IJavaProject javaProject1 = JavaCore.create(project1);
		// IRuntimeClasspathEntry bootstrapEntry3 = JavaRuntime
		// .newDefaultProjectClasspathEntry(javaProject1);
		// bootstrapEntry3
		// .setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

		// sourcepath.add(bootstrapEntry3.getMemento());

		// Add our standard A4L Source Path
		lSourceList.add(lSourceLookup.getMemento());

		workingCopy.setAttribute(ATTR_DEFAULT_SOURCE_PATH, false);
		workingCopy.setAttribute(ATTR_SOURCE_PATH, lSourceList);
		*/

		/*
		 * workingCopy.setAttribute(ATTR_VM_ARGUMENTS,
		 * "-Djava.endorsed.dirs=\"..\\common\\endorsed\"" +
		 * "-Dcatalina.base=\"..\"" + "-Dcatalina.home=\"..\"" +
		 * "-Djava.io.tmpdir=\"..\\temp\"");
		 */
/*
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				"${workspace_loc:" + getProject(projectPath).getName() + "}");
		ILaunchConfiguration configuration1 = workingCopy.doSave();
		configuration1.launch(ILaunchManager.RUN_MODE,
				new NullProgressMonitor());
*/
		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new URL("http://editor.kevoree.org/?host=127.0.0.1&port=9000"));
			// PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	
	   private static final String MAVEN_LAUNCH_CONFIG_TYPE = 
			   "org.eclipse.m2e.Maven2LaunchConfigurationType"; //$NON-NLS-1$

			      /**
			       * Run a maven goal for the project. After the goal has been run, the
			       * project is refreshed
			       *
			       * @param project
			       * @param mavenGoalString
			       */
			      public static void runMavenGoal(final IProject project,
			          final String mavenGoalString, final String modelPath, final String nodeName, final String mode) {
			        final ILaunchConfigurationType type = 
			   DebugPlugin.getDefault().getLaunchManager()
			            .getLaunchConfigurationType(MAVEN_LAUNCH_CONFIG_TYPE); 
			   //$NON-NLS-1$
			        final String launchName = project.getName();

			        IRunnableWithProgress runnable = new IRunnableWithProgress() {
			          @SuppressWarnings("unchecked")
			          public void run(IProgressMonitor monitor)
			              throws InvocationTargetException, InterruptedException {
			            try {
			              monitor.beginTask("Running maven goal '" + mavenGoalString + 
			   "'", 100);
			              ILaunchConfigurationWorkingCopy config = null;
			              config = type.newInstance(null, launchName);
			              List options = new ArrayList();
			              config.setAttribute(
			                  "org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", true);
			              config.setAttribute("M2_PROPERTIES", options);
			              
			   config.setAttribute("org.eclipse.jdt.launching.WORKING_DIRECTORY",
			                  "${workspace_loc:/" + project.getName() + "}");
			              config.setAttribute("M2_GOALS", mavenGoalString);
			              config.setAttribute("M2_PROFILES", "");
			              config.setAttribute("M2_SKIP_TESTS", true);
			              config.setAttribute(
				    				ATTR_VM_ARGUMENTS,
				    				"-Dkevoree.dev=true -Dmodel.debug.port=9080 -Dnode.bootstrap="
				    						+ modelPath + " -Dnode.name=" + nodeName);
				            
			            
						ILaunchConfiguration configuration1 = null;
						configuration1 = config.doSave();

						   ILaunch launch = null;
						if (mode.equals("debug")) {
							launch =configuration1.launch(ILaunchManager.DEBUG_MODE,
									new NullProgressMonitor());
						} else {
							launch =configuration1.launch(ILaunchManager.RUN_MODE,
									new NullProgressMonitor());
						}
			              
			               
			              // Launch the goal
			             
			              // add listener so the project can be refreshed later when   the maven
			              // goal is terminaded
			              
			              DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
			              
			              ILaunchesListener2 launchesListener = new ILaunchesListener2(){

							@Override
							public void launchesRemoved(ILaunch[] launches) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void launchesAdded(ILaunch[] launches) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void launchesChanged(ILaunch[] launches) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void launchesTerminated(ILaunch[] launches) {
								try {
									project.refreshLocal(IResource.DEPTH_INFINITE, null);
								} catch (CoreException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}};
			              DebugPlugin.getDefault().getLaunchManager().addLaunchListener(
			                  launchesListener);
			              monitor.worked(100);
			            }
			            catch (CoreException ce) {
			               
			            }
			          }
			        };

			        // Launch the task with progress
			        try {
			        	runnable.run(new NullProgressMonitor());
			        }
			        catch (InvocationTargetException ite) {
			            // TODO
			        }
			        catch (InterruptedException ie) {
			             // TODO
			        }
			      }

	
	public IProject getProject(String projectpath) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectpath);
	}
}
