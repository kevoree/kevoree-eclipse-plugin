package org.kevoree.tools.eclipse.launcher;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.kevoree.tools.eclipse.Activator;

public class KevoreeMainTab extends AbstractLaunchConfigurationTab {


	protected Text modelLocationText;
	protected Text projectLocationText;
	protected Text nodeNameText;

	
	public int GRID_DEFAULT_WIDTH = 200;
	
	
	@Override
	public void createControl(Composite parent) {
		Composite area = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		area.setLayout(gl);
		area.layout();
		setControl(area);

		Group modelArea = new Group(area, SWT.NULL);
		modelArea.setText("KevScript:");
		modelArea.setLayout(new FillLayout());
		// Create the area for the filename to get
		createModelLayout(modelArea, null);
		
		//Group languageArea = new Group(area, SWT.NULL);
		//languageArea.setText("Language:");
		//languageArea.setLayout(new FillLayout());
		//createLanguageLayout(languageArea, null);
		
		//Group prototypeArea = new Group(area, SWT.NULL);
		//prototypeArea.setText("Gemoc Engine Prototype parameters (these info will probably be removed in future version):");
		//prototypeArea.setLayout(new FillLayout());
		//createPrototypeLayout(prototypeArea, null);

		
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			this.modelLocationText.setText(configuration.getAttribute(KevoreeLauncherConfigurationConstants.LAUNCH_MODEL_PATH, ""));
			this.projectLocationText.setText(configuration.getAttribute(KevoreeLauncherConfigurationConstants.KEVSCRIPT_LAUNCH_PROJECT, ""));
			this.nodeNameText.setText(configuration.getAttribute(KevoreeLauncherConfigurationConstants.KEVSCRIPT_LAUNCH_NODENAME, ""));
		} catch (CoreException e) {
			Activator.error(e.getMessage(), e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				KevoreeLauncherConfigurationConstants.LAUNCH_MODEL_PATH,
				this.modelLocationText.getText());
		configuration.setAttribute(
				KevoreeLauncherConfigurationConstants.KEVSCRIPT_LAUNCH_PROJECT,
				this.projectLocationText.getText());
		configuration.setAttribute(
				KevoreeLauncherConfigurationConstants.KEVSCRIPT_LAUNCH_NODENAME,
				this.nodeNameText.getText());

	}

	@Override
	public String getName() {
		return "Main";
	}
	
	
	// -----------------------------------
	
	/** Basic modify listener that can be reused if there is no more precise need */
	private ModifyListener fBasicModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			updateLaunchConfigurationDialog();
		}
	};
	
	// -----------------------------------
	
	/***
	 * Create the Field where user enters model to execute
	 * 
	 * @param parent
	 * @param font
	 * @return
	 */
	public Composite createModelLayout(Composite parent, Font font) {
		createTextLabelLayout(parent, "Project");		
		
		
		createTextLabelLayout(parent, "Project");		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.horizontalSpan = 1;
		gd.widthHint = GRID_DEFAULT_WIDTH;

		projectLocationText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		projectLocationText.setLayoutData(gd);
		projectLocationText.setFont(font);
		projectLocationText.addModifyListener(fBasicModifyListener);
		Button projectLocationButton1 = createPushButton(parent, "Browse", null);
		projectLocationButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				//handleModelLocationButtonSelected();
				// TODO launch the appropriate selector
				
				ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.PROJECT);
				if(dialog.open() == Dialog.OK){
					String projectPath = ((IResource)dialog.getResult()[0]).getFullPath().toPortableString();
					projectLocationText.setText(projectPath);
					updateLaunchConfigurationDialog();
				}				
			}
		});

		
		createTextLabelLayout(parent, "KevScript to execute");		
		// Create the project selector button

		
		// Project location text
		modelLocationText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		modelLocationText.setLayoutData(gd);
		modelLocationText.setFont(font);
		modelLocationText.addModifyListener(fBasicModifyListener);
		Button projectLocationButton = createPushButton(parent, "Browse", null);
		projectLocationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				//handleModelLocationButtonSelected();
				// TODO launch the appropriate selector
				
				SelectAnyIFileDialog dialog = new SelectAnyIFileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				//dialog.setPattern("*.kevs");
				if(dialog.open() == Dialog.OK){
					String modelPath = ((IResource)dialog.getResult()[0]).getFullPath().toPortableString();
					modelLocationText.setText(modelPath);
					updateLaunchConfigurationDialog();
				}				
			}
		});

		createTextLabelLayout(parent, "Node name to start");		
		// Create the project selector button

		
		// Project location text
		nodeNameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		nodeNameText.setLayoutData(gd);
		nodeNameText.setFont(font);
		nodeNameText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
				
			}
		});

		
		return parent;
	}
	

	// -----------------------------------
	
	/**
	 * 
	 * @param parent
	 *            the Parent of this argument tab
	 * @param labelString
	 *            the label of the input text to create
	 * @param adapter
	 *            the event that is triggered when clicking on OK button
	 */
	public void createTextLabelLayout(Composite parent, String labelString) {
		GridLayout locationLayout = new GridLayout();
		locationLayout.numColumns = 2;
		locationLayout.marginHeight = 10;
		locationLayout.marginWidth = 10;
		parent.setLayout(locationLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		parent.setLayoutData(gd);
		// parent.setFont(null);

		Label inputLabel = new Label(parent, SWT.NONE);
		inputLabel.setText(labelString); //$NON-NLS-1$
		gd = new GridData();
		gd.horizontalSpan = 2;
		inputLabel.setLayoutData(gd);
	}

}
