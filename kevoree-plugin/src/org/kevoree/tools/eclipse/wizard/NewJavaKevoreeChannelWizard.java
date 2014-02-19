package org.kevoree.tools.eclipse.wizard;


/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewJavaKevoreeChannelWizard extends AbstractNewKevoreeElementWizard {
public static final String TITLE = "Kevoree Channel Java"; //$NON-NLS-1$
	public NewJavaKevoreeChannelWizard() {		
		super(new NewJavaKevoreeChannelWizardPage(), TITLE);
	}
	
}