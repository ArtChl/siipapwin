package com.luxsoft.siipap.cxc.swing.descuentos;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import com.luxsoft.siipap.swing.controls.AbstractFrame;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class PruebasVisuales extends AbstractFrame{
	
	private DescuentosView view;
	private String id;
	
	protected PruebasVisuales(String id) {
		super("Prueba Visual de la vista de Descuentos");
		this.id=id;
	}

	@Override
	protected JComponent buildContentPane() {
		view=new DescuentosView();
		view.setId(id);
		return view.getContent();
	}
	
	
	@Override
	protected void configureCloseOperation() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter(){			
			public void windowClosed(WindowEvent e) {
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				view.close();
			}
			
		});
	}

	public static void main(String[] args) {
		SWExtUIManager.setup();
		PruebasVisuales app=new PruebasVisuales("descuentosView");
		app.build();
		app.open();
		
	}

	
	

}
