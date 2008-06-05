package com.luxsoft.siipap.cxc.pagos;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

/**
 * Browser para la consulta de anticipos, Puede ser usado tanto para consulta
 * como para aplicacion de pagos con anticipos.
 * 
 * @author Ruben Cancino
 *
 */
public class AnticiposView extends SXAbstractDialog{
	
	private final Cliente cliente;
	private boolean readOnly=false;

	public AnticiposView(final Cliente c) {
		super("Anticipos");
		this.cliente=c;		
	}

	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout());		
		return panel;
	}
	

	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Cliente getCliente() {
		return cliente;
	}	
	

}
