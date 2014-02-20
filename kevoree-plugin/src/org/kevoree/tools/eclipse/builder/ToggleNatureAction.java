package org.kevoree.tools.eclipse.builder;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.core.ui.internal.Messages;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.kevoree.tools.eclipse.Activator;

public class ToggleNatureAction implements IObjectActionDelegate {

	private ISelection selection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject) {
					project = (IProject) element;
				} else if (element instanceof IAdaptable) {
					project = (IProject) ((IAdaptable) element)
							.getAdapter(IProject.class);
				}
				if (project != null) {
					toggleNature(project);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * Toggles sample nature on a project
	 * 
	 * @param project
	 *            to have sample nature added or removed
	 */
	public void toggleNature(final IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();


			
			for (int i = 0; i < natures.length; ++i) {
				if (KevoreeNature.NATURE_ID.equals(natures[i])) {
					// Remove the nature
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i,
							natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);
					return;
				}
			}

			if(!project.hasNature(JavaCore.NATURE_ID)){
				description.setNatureIds(new String[] { JavaCore.NATURE_ID });
				project.setDescription(description, null);
			}
			
			
			//  add the maven nature (not removed)
			if(!project.hasNature(IMavenConstants.NATURE_ID)){
				
				final MavenPlugin plugin = MavenPlugin.getDefault();
			      IFile pom = project.getFile(IMavenConstants.POM_FILE_NAME);
			      Job job = new Job(Messages.EnableNatureAction_job_enable) {
			  
			        protected IStatus run(IProgressMonitor monitor) {
			          try {
			            ResolverConfiguration configuration = new ResolverConfiguration();
			            //configuration.setResolveWorkspaceProjects(workspaceProjects);
			            //configuration.setActiveProfiles(""); //$NON-NLS-1$
			  
			            boolean hasMavenNature = project.hasNature(IMavenConstants.NATURE_ID);
			  
			            IProjectConfigurationManager configurationManager = plugin.getProjectConfigurationManager();
			  
			            configurationManager.enableMavenNature(project, configuration, new NullProgressMonitor());
			  
			            if(!hasMavenNature) {
			              configurationManager.updateProjectConfiguration(project, monitor);
			            }
			          } catch(CoreException ex) {

			          }
			          return Status.OK_STATUS;
			        }
			      };
			      job.schedule();

			}
			
	
			// Add the nature
			addAsMainNature(project, KevoreeNature.NATURE_ID);
						
			
			addMissingResourcesToNature(project);
		} catch (CoreException e) {
			Activator.warn("Problem while adding Kevoree nature to project. "+e.getMessage(), e);
		}
	}
	

	
	// add the nature making sure this will be the first
	private void addAsMainNature(IProject project, String natureID) throws CoreException{
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 1, natures.length);
		newNatures[0] = natureID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}
	
	private void addMissingResourcesToNature(IProject project) {
	/*	IFile configFile = project.getFile(new Path(Activator.GEMOC_PROJECT_CONFIGURATION_FILE)); 
		if(!configFile.exists()){
			Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		    Map<String, Object> m = reg.getExtensionToFactoryMap();
		    m.put(Activator.GEMOC_PROJECT_CONFIGURATION_FILE_EXTENSION, new XMIResourceFactoryImpl());

		    // Obtain a new resource set
		    ResourceSet resSet = new ResourceSetImpl();

		    // Create the resource
		    Resource resource = resSet.createResource(URI.createURI(configFile.getLocationURI().toString()));
		    // Creates default root elements,
		    GemocLanguageWorkbenchConfiguration gemocLanguageWorkbenchConfiguration = confFactoryImpl.eINSTANCE.createGemocLanguageWorkbenchConfiguration();
		    gemocLanguageWorkbenchConfiguration.getLanguageDefinitions().add(confFactoryImpl.eINSTANCE.createLanguageDefinition());
		    resource.getContents().add(gemocLanguageWorkbenchConfiguration);
		    			
			
			try {
				resource.save(null);
			} catch (IOException e) {
				Activator.error(e.getMessage(), e);
			}
		}
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			Activator.error(e.getMessage(), e);
		}*/
	}
	

}
