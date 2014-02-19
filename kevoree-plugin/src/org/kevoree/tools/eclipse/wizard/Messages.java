package org.kevoree.tools.eclipse.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * @author Olivier Barais
 */
public class Messages extends NLS {
	/*
	 * ERROR_CREATING_PACKAGE=Error creating package
ERROR_CREATING_CLASS=Error creating class
XTEND_KEVOREE_COMPONENT_WIZARD_DESCRIPTION=Create a new Xtend Kevoree Component
ERROR_CREATING_KEVOREE_CHANNEL=Error creating Kevoree Channel
XTEND_KEVOREE_CHANNEL_WIZARD_DESCRIPTION=Create a new Xtend Kevoree Channel
ERROR_CREATING_GROUP=Error creating Kevoree group
XTEND_KEVOREE_GROUP_WIZARD_DESCRIPTION=Create a new Xtend Kevoree Group
ERROR_CREATING_NODE=Error creating Node Type
XTEND_KEVOREE_NODETYPE_WIZARD_DESCRIPTION=Create a new Xtend Kevoree Node Type
TYPE_EXISTS_0=Type 
TYPE_EXISTS_1=\ already exists
	 */
	
	private static final String BUNDLE_NAME = "org.kevoree.tools.eclipse.wizard.messages"; //$NON-NLS-1$
	public static String ERROR_CREATING_PACKAGE;
	public static String ERROR_CREATING_CLASS;
	public static String ERROR_CREATING_KEVOREE_CHANNEL;
	public static String ERROR_CREATING_GROUP;
	public static String ERROR_CREATING_NODE;
	public static String XTEND_KEVOREE_COMPONENT_WIZARD_DESCRIPTION;
	public static String XTEND_KEVOREE_CHANNEL_WIZARD_DESCRIPTION;
	public static String XTEND_KEVOREE_NODETYPE_WIZARD_DESCRIPTION;
	public static String XTEND_KEVOREE_GROUP_WIZARD_DESCRIPTION;
	public static String JAVA_KEVOREE_COMPONENT_WIZARD_DESCRIPTION;
	public static String JAVA_KEVOREE_CHANNEL_WIZARD_DESCRIPTION;
	public static String JAVA_KEVOREE_NODETYPE_WIZARD_DESCRIPTION;
	public static String JAVA_KEVOREE_GROUP_WIZARD_DESCRIPTION;
	public static String TYPE_EXISTS_0;
	public static String TYPE_EXISTS_1;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}