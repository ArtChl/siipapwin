package com.luxsoft.siipap.cxc.consultas;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXFrame.StartPosition;

import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class MovimientoCuentaPanelAction  extends SWXAction{
	
	JXFrame app;

	@Override
	protected void execute() {
		if(app==null){
			app=new JXFrame("Estado de Cuenta",false);
			final MovimientosDeCuentaPanel control=new MovimientosDeCuentaPanel();
			control.setOwner(app);
			app.setContentPane(control.getControl());
			app.setStartPosition(StartPosition.CenterInScreen);				
			app.pack();
		}
		app.setVisible(true);
		
	}
	
	
	public static void main(String[] args) {
		MovimientoCuentaPanelAction a=new MovimientoCuentaPanelAction();
		a.execute();
	}
	
	
	
	
	

}
