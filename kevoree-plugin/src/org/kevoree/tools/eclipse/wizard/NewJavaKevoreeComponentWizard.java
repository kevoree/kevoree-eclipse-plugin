package org.kevoree.tools.eclipse.wizard;


/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewJavaKevoreeComponentWizard extends AbstractNewKevoreeElementWizard {
public static final String TITLE = "Kevoree Component Java"; //$NON-NLS-1$

	public NewJavaKevoreeComponentWizard() {
		
		super(new NewJavaKevoreeComponentWizardPage(), TITLE);
	}
	
}