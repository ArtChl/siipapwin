package com.luxsoft.siipap.cxc.catalogos;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.Cobrador;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.form2.IFormModel;

public class CobradoresForm extends AbstractForm{

	public CobradoresForm(IFormModel model) {
		super(model);
	}

	@Override
	protected JComponent buildFormPanel() {
		final FormLayout layout=new FormLayout(
				"p,2dlu,50,2dlu,p,2dlu,max(p;70dlu):g"
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Clave",getControl("clave"),true);
		builder.append("Nombre",getControl("nombre"),5);
		builder.nextLine();
		builder.append("Comisión",getControl("comision"),true);
		builder.append("Activo",getControl("activo"),true);
		return builder.getPanel();
	}
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if("comision".equals(property)){
			JTextField tf=Binder.createDescuentoBinding(model.getComponentModel(property));
			return tf;
		}else if("nombre".equals(property)){
			JTextField tf=Binder.createMayusculasTextField(model.getComponentModel(property));
			return tf;
		}
		return null;
	}
	
	

	@Override
	protected void onWindowOpened() {		
		super.onWindowOpened();
		getControl("comision").requestFocusInWindow();
	}

	public static void main(String[] args) {
		final DefaultFormModel model=new DefaultFormModel(new Cobrador());
		final CobradoresForm form=new CobradoresForm(model);
		form.open();
		
	}

}
