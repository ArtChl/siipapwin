package com.luxsoft.siipap.cxc.task;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXFrame;

import com.jgoodies.uif.util.ScreenUtils;
import com.luxsoft.siipap.cxc.consultas.ComisionesView;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

public class ComisionesCommand extends SWXAction{
	
	private String target;

	@Override
	protected void execute() {		
		final ComisionesDialog dialog=new ComisionesDialog();
		dialog.open();
		
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public class ComisionesDialog extends JXFrame{			
		
		final ComisionesView view;//=new ComisionesView(getTarget());

		public ComisionesDialog() {
			super("Comisiones por "+StringUtils.capitalize(getTarget()));
			view=new ComisionesView(getTarget());
			add(view.getContent(),BorderLayout.CENTER);
			addWindowListener(new WindowAdapter(){				
				public void windowClosed(WindowEvent e) {
					view.close();
				}				
			});
			
		}
		
		public void open(){
			SwingWorker worker=new SwingWorker(){
				protected Object doInBackground() throws Exception {
					view.load();
					return null;
				}				
				protected void done() {
					pack();
					ScreenUtils.locateOnScreenCenter(ComisionesCommand.ComisionesDialog.this);
					setVisible(true);
				}
				
			};
			worker.execute();
		}

		/*
		@Override
		protected JComponent buildContent() {				
			return view.getContent();
		}
		*/

		/*

		@Override
		protected void setResizable() {
			setResizable(true);
		}

		@Override
		protected void onWindowOpened() {
			
		}

		@Override
		public void close() {
			view.close();
			super.close();
		}		
		*/
		
	};		
	
	public static void main(String[] args) {
		ComisionesCommand c=new ComisionesCommand();
		c.setTarget("vendedor");
		c.execute();
	}
	

}
