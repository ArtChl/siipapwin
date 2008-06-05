package com.luxsoft.siipap.cxc.catalogos;

import javax.swing.JComponent;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.Vendedor;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.form2.IFormModel;

public class VendedorForm extends AbstractForm{

	public VendedorForm(IFormModel model) {
		super(model);
	}

	@Override
	protected JComponent buildFormPanel() {
		final FormLayout layout=new FormLayout(
				"p,2dlu,50,2dlu,p,2dlu,max(p;70dlu):g"
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		if((Long)model.getValue("clave")!=0){
			getControl("clave").setEnabled(false);
		}
		builder.append("Clave",getControl("clave"),true);
		builder.append("Nombre",getControl("nombre"),5);
		builder.nextLine();
		builder.append("Email",getControl("email"),5);
		builder.nextLine();		
		builder.append("Activo",getControl("activo"),true);
		return builder.getPanel();
	}
	

	

	public static void main(String[] args) {
		final DefaultFormModel model=new DefaultFormModel(new Vendedor());
		final VendedorForm form=new VendedorForm(model);
		form.open();
		
	}

}
