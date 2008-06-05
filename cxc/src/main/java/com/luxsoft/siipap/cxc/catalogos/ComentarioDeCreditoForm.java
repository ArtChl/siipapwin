package com.luxsoft.siipap.cxc.catalogos;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.RowSpec;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.swing.form2.DefaultForm;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

/**
 * Forma para el mantenimiento del comentario de Cuentas por Cobrar
 * al catalogo de clientes
 * 
 * @author Ruben Cancino
 *
 */
public class ComentarioDeCreditoForm extends DefaultForm{

	public ComentarioDeCreditoForm(ClienteCredito cliente) {
		super(new DefaultFormModel(cliente), "Comentario de CXC");
	}

	@Override
	protected void insertComponents(DefaultFormBuilder builder) {
		final CellConstraints cc=new CellConstraints();		
		builder.append("Comentario");
		builder.appendRow(new RowSpec("45dlu"));
		JTextArea ta=BasicComponentFactory.createTextArea(model.getModel("comentarioCxc"),false);
		ta.setColumns(50);
		
		builder.add(new JScrollPane(ta),
				cc.xywh(builder.getColumn(), builder.getRow(),5,2));
		builder.nextLine(2);
	}
	
	
	
	@Override
	public String getHeaderDescription() {
		return "Registre el comentario vigente ";
	}

	@Override
	public String getHeaderTitle() {
		return "Cliente:  "+model.getModel("nombre").toString();
	}

	

}
