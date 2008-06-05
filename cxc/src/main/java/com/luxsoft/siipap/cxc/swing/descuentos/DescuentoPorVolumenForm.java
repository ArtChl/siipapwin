package com.luxsoft.siipap.cxc.swing.descuentos;

import javax.swing.JComponent;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form.AbstractForm;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.form.FormModel;
import com.luxsoft.siipap.swing.utils.FormatUtils;

public class DescuentoPorVolumenForm extends AbstractForm{
	
	

	public DescuentoPorVolumenForm(FormModel model) {
		super(model);
		
	}

	@Override
	protected JComponent buildFormPanel() {
		FormLayout layout=new FormLayout(
				"l:40dlu,3dlu,max(p;60dlu),3dlu,l:40dlu,3dlu,max(p;60dlu):g"
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Límite",add("maximo"),true);
		builder.append("Descuento",add("descuento"));
		builder.append("Activo",add("activo"),true);
		builder.append("Año",add("year"));
		builder.append("Mes",add("mes"),true);
		return builder.getPanel();
	}
	
	
	@Override
	protected JComponent getCustomComponent(String property,final ComponentValueModel vm) {
		if("descuento".equals(property))
			return Binder.createDescuentoBinding(getFormModel().getPresentationModel().getComponentModel("descuento"));
		else if("maximo".equals(property)){
			return BasicComponentFactory.createFormattedTextField(vm, FormatUtils.getMoneyFormatterFactory());
		}
		else return null;
	}
	

	public static void main(String[] args) {
		DescuentoPorVolumenFormModel model=new DescuentoPorVolumenFormModel();
		DescuentoPorVolumenForm form=new DescuentoPorVolumenForm(model);
		FormDialog dialog=new FormDialog(form);
		dialog.setTitle("Descuento Por Volumen");
		dialog.setDescription("Mantenimiento y consulta de descuento por volumen");
		dialog.open();
	}

}
