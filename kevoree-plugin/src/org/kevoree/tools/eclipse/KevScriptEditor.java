package org.kevoree.tools.eclipse;

import org.eclipse.ui.editors.text.TextEditor;

public class KevScriptEditor extends TextEditor {

	private ColorManager colorManager;

	public KevScriptEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
