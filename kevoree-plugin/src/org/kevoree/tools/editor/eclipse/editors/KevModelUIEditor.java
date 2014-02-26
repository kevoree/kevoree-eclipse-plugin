package org.kevoree.tools.editor.eclipse.editors;

import java.awt.Frame;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.kevoree.tools.ui.editor.KevoreeEditor;

public class KevModelUIEditor extends EditorPart implements IResourceChangeListener {

	public KevModelUIEditor(){
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	private KevoreeEditor artpanel = new KevoreeEditor();
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		
		setSite(site);
		setInput(input);
		final org.eclipse.ui.part.FileEditorInput inEditor = (org.eclipse.ui.part.FileEditorInput) input;
		setContentDescription("KevEditor:"+inEditor.getFile().getLocation().toString());
				
		new Thread(){
			public void run(){
				if(inEditor!= null){
					try {
			            System.out.println("init=" + inEditor.getFile().getLocation().toString());
			            artpanel.getPanel().load(inEditor.getFile().getLocation().toString());
					} catch(Exception e){
						System.err.println("Can open file"+e.getMessage());						
					}
		        }
			}
		}.start();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
        FillLayout layout = new FillLayout();
        composite.setLayout(layout);
        Frame frame = SWT_AWT.new_Frame(composite);
        frame.add(artpanel.getPanel());
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}


}
