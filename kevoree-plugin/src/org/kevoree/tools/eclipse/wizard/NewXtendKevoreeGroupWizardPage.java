package org.kevoree.tools.eclipse.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewXtendKevoreeGroupWizardPage extends AbstractNewKevoreeElementWizardPage {

	public NewXtendKevoreeGroupWizardPage() {
		super(CLASS_TYPE, NewXtendKevoreeGroupWizard.TITLE, ".xtend");
		this.setTitle(NewXtendKevoreeGroupWizard.TITLE);
		this.setDescription(Messages.XTEND_KEVOREE_GROUP_WIZARD_DESCRIPTION);
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
		return XtendKevoreeCreatorUtil.getInstance().createPackageDeclaration(getTypeName(), getPackageFragment(), lineSeparator);
	}

	@Override
	protected String getTypeContent(String indentation, String lineSeparator) {
		return XtendKevoreeCreatorUtil.getInstance().createKevoreeGroupXtend(getTypeName());
	}

}