package org.kevoree.tools.eclipse.wizard;


/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewXtendKevoreeGroupWizard extends AbstractNewKevoreeElementWizard {
public static final String TITLE = "Kevoree Group Xtend"; //$NON-NLS-1$

	public NewXtendKevoreeGroupWizard() {
		
		super(new NewXtendKevoreeGroupWizardPage(), TITLE);
	}
	
}