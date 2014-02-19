package org.kevoree.tools.eclipse.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewXtendKevoreeChannelWizardPage extends AbstractNewKevoreeElementWizardPage {

	public NewXtendKevoreeChannelWizardPage() {
		super(ANNOTATION_TYPE, NewXtendKevoreeComponentWizard.TITLE, ".xtend");
		this.setTitle(NewXtendKevoreeChannelWizard.TITLE);
		this.setDescription(Messages.XTEND_KEVOREE_CHANNEL_WIZARD_DESCRIPTION);
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
		return Messages.ERROR_CREATING_KEVOREE_CHANNEL;
	}

	@Override
	protected String getPackageDeclaration(String lineSeparator) {
		return XtendKevoreeCreatorUtil.getInstance().createPackageDeclaration(getTypeName(), getPackageFragment(), lineSeparator);
	}

	@Override
	protected String getTypeContent(String indentation, String lineSeparator) {
		return XtendKevoreeCreatorUtil.getInstance().createKevoreeChannel(getTypeName());
	}
}