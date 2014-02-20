package org.kevoree.tools.eclipse.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class KevoreeLauncher implements
		ILaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		String modelPath = configuration.getAttribute(
				KevoreeLauncherConfigurationConstants.LAUNCH_MODEL_PATH, "");

		String languageName = configuration
				.getAttribute(
						KevoreeLauncherConfigurationConstants.LAUNCH_SELECTED_LANGUAGE,
						"");

		IConfigurationElement confElement = null;
		IConfigurationElement[] confElements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(
						"org.gemoc.gemoc_language_workbench.xdsml");
		// retrieve the extension for the chosen language
		for (int i = 0; i < confElements.length; i++) {
			if (confElements[i].getAttribute("name").equals(languageName)) {
				confElement = confElements[i];
			}
		}
	}
}
