package org.kevoree.tools.eclipse.launcher;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.kevoree.tools.eclipse.Activator;

/**
 * Dialog that allows to select any IProject
 * Uses a quite ugly workaround to access and set the default pattern filter, but this prevents from duplicating eclipse code.
 * @author dvojtise
 *
 */
public class SelectAnyIFileDialog extends ResourceListSelectionDialog {
	
	public SelectAnyIFileDialog(Shell parentShell){
		super(parentShell, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control result = super.createDialogArea(parent);
		setPattern("*.kevs");
		return result;
	}
	
	
	public void setPattern(String newPattern){
		//this.pattern.setText("*");
		// workaround to set the initial value of the pattern to "*"
		try {
			Field patternField = ResourceListSelectionDialog.class.getDeclaredField("pattern");
			patternField.setAccessible(true);
			Text pattern =(Text) patternField.get(this);
			pattern.setText("*.kevs");
		} catch (NoSuchFieldException e) {
			Activator.error(e.getMessage(), e);
		} catch (SecurityException e) {
			Activator.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Activator.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Activator.error(e.getMessage(), e);
		}
	}
	

}