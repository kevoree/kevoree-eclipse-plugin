package org.kevoree.tools.eclipse.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.kevoree.tools.eclipse.Activator;
import org.kevoree.tools.eclipse.builder.PomCreator;
import org.kevoree.tools.eclipse.builder.ToggleNatureAction;

public abstract class AbstractCreateNewKevoreeProject extends Wizard implements INewWizard {


	WizardNewProjectCreationPage mainPage;
	ToggleNatureAction nature;
	boolean xtend = false;
	public AbstractCreateNewKevoreeProject(boolean xtend) {
		super();
		this.xtend = xtend;
		this.nature = new ToggleNatureAction();
		this.setWindowTitle("Create Kevoree Project");
		this.mainPage = new WizardNewProjectCreationPage("NewKevoreeProject");
		this.mainPage.setTitle("Kevoree Project");
		this.mainPage.setDescription("Create a new Kevoree Project");
		addPage(this.mainPage);
	}

	@Override
	public void addPages() {
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		
		try {
			final IProject createdProject = this.mainPage.getProjectHandle();
			IWorkspaceRunnable operation = new IWorkspaceRunnable() {
				 public void run(IProgressMonitor monitor) throws CoreException {
					 createdProject.create(monitor);
					 createdProject.open(monitor);
					 createPomFile(createdProject);				 
					 addKevoreeProjectNature(createdProject);
					 createdProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				 }
			};
			ResourcesPlugin.getWorkspace().run(operation, null);
		} catch (CoreException exception) {
			Activator.error(exception.getMessage(), exception);
			return false;
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private void createPomFile(IProject project) throws CoreException {
		IFolder sourceFolder = project.getFolder("src");
		sourceFolder.create(false, true, null);
		 sourceFolder = project.getFolder("src/main");
		sourceFolder.create(false, true, null);
		 sourceFolder = project.getFolder("src/main/java");
			sourceFolder.create(false, true, null);
			 sourceFolder = project.getFolder("src/main/kevs");
		sourceFolder.create(false, true, null);
		IFile pom = project.getFile("pom.xml");
		byte[] bytes = PomCreator.getPom(project.getName(),xtend).getBytes();
	    InputStream source = new ByteArrayInputStream(bytes);
	    pom.create(source, true, null);
		IFile kevs = project.getFile("src/main/kevs/main.kevs");
		bytes = PomCreator.getKevs(project.getName()).getBytes();
	    source = new ByteArrayInputStream(bytes);
	    kevs.create(source, true, null);

	}

	private void addKevoreeProjectNature(IProject project) {
		 this.nature.toggleNature(project);
	}
}
