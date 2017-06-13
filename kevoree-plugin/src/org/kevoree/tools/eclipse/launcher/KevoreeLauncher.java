package org.kevoree.tools.eclipse.launcher;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

import java.lang.reflect.InvocationTargetException;
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
		String version =store.getString(PreferenceConstants.P_STRING);

		
		if (getProject(projectPath).hasNature("org.nodeclipse.ui.NodeNature"))
			runGruntGoal(getProject(projectPath), ResourcesPlugin.getWorkspace().getRoot()
					.getFile(new Path(modelPath)).getRawLocation()
					.toOSString(), nodeName,true);
			
		else
			runMavenGoal(
				getProject(projectPath),
				"clean install kev:run",
				ResourcesPlugin.getWorkspace().getRoot()
						.getFile(new Path(modelPath)).getRawLocation()
						.toOSString(), nodeName, mode,version);

	}

	private static final String MAVEN_LAUNCH_CONFIG_TYPE = "org.eclipse.m2e.Maven2LaunchConfigurationType"; //$NON-NLS-1$

	private static final String GRUNT_LAUNCH_CONFIG_TYPE = "org.eclipse.ui.externaltools.ProgramLaunchConfigurationType"; //$NON-NLS-1$

	public static void runMavenGoal(final IProject project,
			final String mavenGoalString, final String modelPath,
			final String nodeName, final String mode, final String kevoreeversion) {
		final ILaunchConfigurationType type = DebugPlugin.getDefault()
				.getLaunchManager()
				.getLaunchConfigurationType(MAVEN_LAUNCH_CONFIG_TYPE);
		//$NON-NLS-1$
		final String launchName = project.getName();

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@SuppressWarnings("unchecked")
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask("Running maven goal '" + mavenGoalString
							+ "'", 100);
					ILaunchConfigurationWorkingCopy config = null;
					config = type.newInstance(null, launchName);
					List options = new ArrayList();
					config.setAttribute(
							"org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND",
							true);
					config.setAttribute("M2_PROPERTIES", options);

					config.setAttribute(
							"org.eclipse.jdt.launching.WORKING_DIRECTORY",
							"${workspace_loc:/" + project.getName() + "}");
					config.setAttribute("M2_GOALS", mavenGoalString);
					config.setAttribute("M2_PROFILES", "");
					config.setAttribute("M2_SKIP_TESTS", true);
					config.setAttribute(ATTR_VM_ARGUMENTS,
							"-Dkevoree.dev=true -Dmodel.debug.port=9080" /*-Dnode.bootstrap="
									+ modelPath*/ + " -Dnode.name=" + nodeName + " -Dkevoree.version=" + kevoreeversion);

					ILaunchConfiguration configuration1 = null;
					configuration1 = config.doSave();

					ILaunch launch = null;
					if (mode.equals("debug")) {
						launch = configuration1.launch(
								ILaunchManager.DEBUG_MODE,
								new NullProgressMonitor());
					} else {
						launch = configuration1.launch(ILaunchManager.RUN_MODE,
								new NullProgressMonitor());
					}

					// Launch the goal

					// add listener so the project can be refreshed later when
					// the maven
					// goal is terminaded

					DebugPlugin.getDefault().getLaunchManager()
							.addLaunch(launch);

					ILaunchesListener2 launchesListener = new ILaunchesListener2() {

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
								project.refreshLocal(IResource.DEPTH_INFINITE,
										null);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					};
					DebugPlugin.getDefault().getLaunchManager()
							.addLaunchListener(launchesListener);
					monitor.worked(100);
				} catch (CoreException ce) {

				}
			}
		};

		// Launch the task with progress
		try {
			runnable.run(new NullProgressMonitor());
		} catch (InvocationTargetException ite) {
			// TODO
		} catch (InterruptedException ie) {
			// TODO
		}
	}

	public static void runGruntGoal(final IProject project,
			 final String modelPath,
			final String nodeName, final boolean compile) {
		final ILaunchConfigurationType type = DebugPlugin.getDefault()
				.getLaunchManager()
				.getLaunchConfigurationType(GRUNT_LAUNCH_CONFIG_TYPE);
		//$NON-NLS-1$
		final String launchName = project.getName();

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask("Running grunt goal "+ project.getName(), 100);
					ILaunchConfigurationWorkingCopy config = null;
					config = type.newInstance(null, "grunt" + launchName);
					
					config.setAttribute("org.eclipse.ui.externaltools.ATTR_LOCATION", "${system_path:grunt}");
					if (!compile)
						config.setAttribute("org.eclipse.ui.externaltools.ATTR_TOOL_ARGUMENTS", "kevoree --node="+nodeName+" --kevscript="+modelPath +" --no-reinstall");
					else
						config.setAttribute("org.eclipse.ui.externaltools.ATTR_TOOL_ARGUMENTS", "kevoree --node="+nodeName+" --kevscript="+modelPath);						
					config.setAttribute("org.eclipse.ui.externaltools.ATTR_WORKING_DIRECTORY", "${workspace_loc:/"+project.getName()+"}");
					ILaunchConfiguration configuration1 = null;
					configuration1 = config.doSave();

					ILaunch launch = null;
						launch = configuration1.launch(ILaunchManager.RUN_MODE,
								new NullProgressMonitor());
	
					// Launch the goal

					// add listener so the project can be refreshed later when
					// the maven
					// goal is terminaded

					DebugPlugin.getDefault().getLaunchManager()
							.addLaunch(launch);

					ILaunchesListener2 launchesListener = new ILaunchesListener2() {

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
								project.refreshLocal(IResource.DEPTH_INFINITE,
										null);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					};
					DebugPlugin.getDefault().getLaunchManager()
							.addLaunchListener(launchesListener);
					monitor.worked(100);
				} catch (CoreException ce) {
					ce.printStackTrace();
				}
			}
		};

		// Launch the task with progress
		try {
			runnable.run(new NullProgressMonitor());
		} catch (InvocationTargetException ite) {
			// TODO
		} catch (InterruptedException ie) {
			// TODO
		}
	}

	
	public IProject getProject(String projectpath) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectpath);
	}
}
