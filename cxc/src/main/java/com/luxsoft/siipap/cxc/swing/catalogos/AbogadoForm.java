package com.luxsoft.siipap.cxc.swing.catalogos;

import java.awt.BorderLayout;
import java.beans.PropertyDescriptor;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.beans.BeanUtils;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

public class AbogadoForm extends AbstractControl{
	
	private final AbogadosFormModel model;
	

	public AbogadoForm(final AbogadosFormModel model) {		
		this.model = model;
	}


	@Override
	protected JComponent buildContent() {
		FormLayout layout=new FormLayout("l:50dlu,3dlu,f:p:g","");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Nombre", add("abonombre"),true);
		builder.appendSeparator("Dirección");
		builder.append("Calle",add("abocalle"),true);
		return builder.getPanel();
	}
	
	protected JComponent add(final String propertyName){
		try {
			JComponent c=getComponent(propertyName);
			return c;
		} catch (Exception e) {
			JTextField tf=new JTextField(e.getMessage());
			ValidationComponentUtils.setErrorBackground(tf);
			return tf;
		}
	}
	
	protected JComponent getComponent(String id){
		PropertyDescriptor pd=BeanUtils.getPropertyDescriptor(model.getBeanClass(), id);
		
		if(String.class.equals(pd.getPropertyType())){
			return BasicComponentFactory.createTextField(model.getComponentModel(id));
		}else if(Date.class.equals(pd.getPropertyType())){
			ComponentValueModel vm=model.getComponentModel(id);
			if(vm.isEnabled())
				return Binder.createDateComponent(vm);
			else
				return BasicComponentFactory.createDateField(vm);
		}
		return new JTextField();
	}
	
	
	public static SXAbstractDialog getFormDialog(final AbogadosFormModel model){
		final AbogadoForm form=new AbogadoForm(model);
		SXAbstractDialog dialog=new SXAbstractDialog(""){

			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());
				p.add(form.getControl(),BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}
			
		};
		return dialog;
	}
	
	public static void main(String[] args) {
		AbogadosFormModel model=new AbogadosFormModel();
		getFormDialog(model).open();
	}

}
