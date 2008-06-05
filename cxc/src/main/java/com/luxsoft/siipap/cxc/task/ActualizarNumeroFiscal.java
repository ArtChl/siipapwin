package com.luxsoft.siipap.cxc.task;

import java.awt.BorderLayout;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma simple para actualizar el numero fiscal de una factura
 * 
 * @author Ruben Cancino
 *
 */
public class ActualizarNumeroFiscal extends SXAbstractDialog{
	
	private JFormattedTextField input;
	private JTextField anterior;	
	private final Venta venta;
	
	public ActualizarNumeroFiscal(final Venta venta){
		super("Número Fiscal");		
		this.venta=venta;
	}

	@Override
	protected JComponent buildContent() {
		final JPanel panel=new JPanel(new BorderLayout());
		
		anterior=new JTextField(String.valueOf(venta.getNumeroFiscal()));
		anterior.setEnabled(false);
		final NumberFormat nf=NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(false);
		final NumberFormatter formatter=new NumberFormatter(nf);
		formatter.setValueClass(Integer.class);
		input=new JFormattedTextField(formatter);
		final FormLayout layout=new FormLayout("l:p,2dlu,f:max(60dlu;p)","");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Anterior",anterior,true);
		builder.append("Número Fiscal",input,true);
		panel.add(builder.getPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		
		return panel;
	}

	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Actualización de número fiscal"
				,getDescription());
	}
	
	private String getDescription(){
		String pattern="Actualiza el número fiscal para la factura {0} del Cliente {1} ({2})";
		return MessageFormat.format(pattern, venta.getNumero(),venta.getNombre(),venta.getClave());
	}

	@Override
	public void doApply() {
		venta.setNumeroFiscal((Integer)input.getValue());
		super.doApply();
	}
	
	

}
