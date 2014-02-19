package org.kevoree.tools.eclipse.wizard;


/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewXtendKevoreeComponentWizard extends AbstractNewKevoreeElementWizard {
public static final String TITLE = "Kevoree Component Xtend"; //$NON-NLS-1$

	public NewXtendKevoreeComponentWizard() {
		
		super(new NewXtendKevoreeComponentWizardPage(), TITLE);
	}
	
}