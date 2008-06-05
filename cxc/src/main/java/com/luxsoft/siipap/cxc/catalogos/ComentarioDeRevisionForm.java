package com.luxsoft.siipap.cxc.catalogos;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaACredito;

/**
 * Forma para el mantenimiento del comentario 
 * para actualizar los comentarios de {@link VentaACredito} en el proceso de revisión
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class ComentarioDeRevisionForm extends AbstractForm{

	public ComentarioDeRevisionForm(final VentaACredito venta) {
		super(new DefaultFormModel(venta));
	}
	
	@Override
	protected JComponent buildFormPanel() {
		final DefaultFormBuilder builder=new DefaultFormBuilder(new FormLayout("l:p,2dlu,p, 3dlu,l:p,2dlu,p:g",""));
		final JTextField comentario=BasicComponentFactory.createTextField(model.getModel("comentario"),false);
		builder.append("Comentario Rec. Suc.",comentario,5);		
		builder.nextLine();
		
		final CellConstraints cc=new CellConstraints();
		builder.append("Comentario Rev/Cob");
		builder.appendRow(new RowSpec("45dlu"));
		JTextArea ta=BasicComponentFactory.createTextArea(model.getModel("comentarioRepPago"),false);
		ta.setColumns(50);
		builder.add(new JScrollPane(ta),
				cc.xywh(builder.getColumn(), builder.getRow(),5,2));
		builder.nextLine(2);
		return builder.getPanel();
	}
	
	public String getHeaderDescription() {
		return "Comentarios de revisión y cobro";
	}

	
	public String getHeaderTitle() {
		return "Venta:  "+model.getModel("id").toString();
	}

	public static void main(String[] args) {
		Venta v=DatosDePrueba.ventasDePrueba().get(0);
		ComentarioDeRevisionForm form=new ComentarioDeRevisionForm(v.getCredito());
		form.open();
	}

	

}
