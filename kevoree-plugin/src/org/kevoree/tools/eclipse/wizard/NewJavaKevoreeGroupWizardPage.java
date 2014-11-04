package org.kevoree.tools.eclipse.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewJavaKevoreeGroupWizardPage extends AbstractNewKevoreeElementWizardPage {

	public NewJavaKevoreeGroupWizardPage() {
		super(CLASS_TYPE, NewJavaKevoreeGroupWizard.TITLE, ".java");
		this.setTitle(NewJavaKevoreeGroupWizard.TITLE);
		this.setDescription(Messages.JAVA_KEVOREE_GROUP_WIZARD_DESCRIPTION);
	} 

	public void createControl(Composite parent) {
		setControl(createCommonControls(parent));
	}

	@Override
	protected void doStatusUpdate() {
		IStatus[] status = new IStatus[] { fContainerStatus, fPackageStatus, fTypeNameStatus };
		updateStatus(status);
	}

	@Override
	protected String getElementCreationErrorMessage() {
		return Messages.ERROR_CREATING_GROUP;
	}

	@Override
	protected String getPackageDeclaration(String lineSeparator) {
		return XtendKevoreeCreatorUtil.getInstance().createPackageDeclaration(getTypeName(), getPackageFragment(), ";\n");
	}

	@Override
	protected String getTypeContent(String indentation, String lineSeparator) {
		return XtendKevoreeCreatorUtil.getInstance().createKevoreeGroupJava(getTypeName());
	}

}