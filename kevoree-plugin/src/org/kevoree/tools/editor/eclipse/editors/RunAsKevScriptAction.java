package org.kevoree.tools.editor.eclipse.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class RunAsKevScriptAction implements org.eclipse.ui.IObjectActionDelegate {


	@Override
	public void run(IAction action) {
		System.out.println("I'm running");
		
		ElementListSelectionDialog dialog = 
			     new ElementListSelectionDialog(PlatformUI.getWorkbench().
			                getActiveWorkbenchWindow().getShell(), new LabelProvider());
			dialog.setTitle("String Selection");
			dialog.setMessage("Select a String (* = any string, ? = any char):");
			dialog.setElements(new Object[] { "one", "two", "three" });
			dialog.open();
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

}
