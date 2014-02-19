package org.kevoree.tools.eclipse.wizard;


/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewJavaKevoreeGroupWizard extends AbstractNewKevoreeElementWizard {
public static final String TITLE = "Kevoree Group Java"; //$NON-NLS-1$

	public NewJavaKevoreeGroupWizard() {
		
		super(new NewJavaKevoreeGroupWizardPage(), TITLE);
	}
	
}