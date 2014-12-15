package org.kevoree.tools.eclipse.builder;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.IPreferenceStore;
import org.kevoree.resolver.MavenResolver;
import org.kevoree.tools.eclipse.Activator;
import org.kevoree.tools.eclipse.preferences.PreferenceConstants;

public class KevoreeBuilder extends IncrementalProjectBuilder {

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				updateProjectPersistentProperties(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				removePersistentProperties(resource);
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				updateProjectPersistentProperties(resource);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			updateProjectPersistentProperties(resource);
			// return true to continue visiting children.
			return true;
		}
	}

	public static final String BUILDER_ID = "org.kevoree.kevoree_workbench.ui.kevoreeBuilder";

	private static final String MARKER_TYPE = "org.kevoree.kevoree_workbench.ui.kevoreeProblem";

	private SAXParserFactory parserFactory;

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				fullBuild(monitor);
//				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	private void updateProjectPersistentProperties(IResource resource) {
		/*
		 * if (resource instanceof IFile &&
		 * resource.getName().equals(Activator.GEMOC_PROJECT_CONFIGURATION_FILE
		 * )) { IFile file = (IFile) resource; IProject project =
		 * file.getProject(); try { if(file.exists()){
		 * 
		 * resetPersistentProperties(project);
		 * 
		 * Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		 * Map<String, Object> m = reg.getExtensionToFactoryMap();
		 * m.put("gemoc_language_conf", new XMIResourceFactoryImpl());
		 * 
		 * // Obtain a new resource set ResourceSet resSet = new
		 * ResourceSetImpl();
		 * 
		 * // Create the resource Resource modelresource =
		 * resSet.getResource(URI.createURI(file.getLocationURI().toString()),
		 * true); TreeIterator<EObject> it = modelresource.getAllContents();
		 * while (it.hasNext()) { EObject eObject = (EObject) it.next();
		 * if(eObject instanceof
		 * org.gemoc.gemoc_language_workbench.conf.DomainModelProject){
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_DOMAINMODEL), "true"); }
		 * if(eObject instanceof
		 * org.gemoc.gemoc_language_workbench.conf.DSAProject){
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_DSA), "true"); } if(eObject
		 * instanceof org.gemoc.gemoc_language_workbench.conf.DSEProject){
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_DSE), "true"); } if(eObject
		 * instanceof org.gemoc.gemoc_language_workbench.conf.MoCProject){
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_MOC), "true"); } if(eObject
		 * instanceof org.gemoc.gemoc_language_workbench.conf.EditorProject){
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_EDITOR), "true"); } if(eObject
		 * instanceof org.gemoc.gemoc_language_workbench.conf.AnimatorProject){
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_ANIMATOR), "true"); } } } }
		 * catch (CoreException e) { Activator.error(e.getMessage(), e); }
		 * 
		 * }
		 */
	}

	private void removePersistentProperties(IResource resource) {
		/*
		 * if (resource instanceof IFile &&
		 * resource.getName().equals(Activator.GEMOC_PROJECT_CONFIGURATION_FILE
		 * )) { IFile file = (IFile) resource;
		 * resetPersistentProperties(file.getProject()); }
		 */
	}

	private void resetPersistentProperties(IProject project) {
		/*
		 * try { project.setPersistentProperty(new
		 * QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_DOMAINMODEL), null);
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_DSA), null);
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_DSE), null);
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_MOC), null);
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_EDITOR), null);
		 * project.setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID,
		 * Activator.GEMOC_PROJECT_PROPERTY_HAS_ANIMATOR), null); } catch
		 * (CoreException e) { Activator.error(e.getMessage(), e); }
		 */
	}

	/*
	 * void checkXML(IResource resource) { if (resource instanceof IFile &&
	 * resource.getName().endsWith(".xml")) { IFile file = (IFile) resource;
	 * deleteMarkers(file); XMLErrorHandler reporter = new
	 * XMLErrorHandler(file); try { getParser().parse(file.getContents(),
	 * reporter); } catch (Exception e1) { } } }
	 */
	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	private final MavenResolver resolver = new MavenResolver();

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		/*try {
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
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName()
						.equals("Run App Processor on Project"
								+ getProject().getName())) {
					configuration.delete();
					break;
				}
			}
			ILaunchConfigurationWorkingCopy workingCopy = conftype.newInstance(
					null, "Run App Processor on Project"
							+ getProject().getName());

			IVMInstall jre = JavaRuntime.getDefaultVMInstall();
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
					jre.getName());
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, jre
							.getVMInstallType().getId());

			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					getProject().getName());
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME,
					"org.kevoree.tools.annotator.App");
			workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, "target/classes");

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

			IProject project = getProject(); 
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

			/*workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,"${workspace_loc:"+ getProject().getName()+"}");
			ILaunchConfiguration configuration = workingCopy.doSave();
			configuration.launch(ILaunchManager.RUN_MODE,
					new NullProgressMonitor());

			// You can also launch a configuration via the API
			// ILaunchConfiguration.launch(String mode, IProgressMonitor
			// monitor). This avoids saving editors and building the workspace,
			// but it requires you to provide a progress monitor.

			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}*/
	}

	/*
	 * private SAXParser getParser() throws ParserConfigurationException,
	 * SAXException { if (parserFactory == null) { parserFactory =
	 * SAXParserFactory.newInstance(); } return parserFactory.newSAXParser(); }
	 */

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
}
