package org.kevoree.tools.eclipse.wizard;


/**
 * @author Olivier Barais - Initial contribution and API
 */
public class NewXtendKevoreeChannelWizard extends AbstractNewKevoreeElementWizard {
public static final String TITLE = "Kevoree Channel Xtend"; //$NON-NLS-1$
	public NewXtendKevoreeChannelWizard() {		
		super(new NewXtendKevoreeChannelWizardPage(), TITLE);
	}
	
}