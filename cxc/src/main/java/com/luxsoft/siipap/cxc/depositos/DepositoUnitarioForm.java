package com.luxsoft.siipap.cxc.depositos;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.DepositoUnitario;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.form2.IFormModel;

public class DepositoUnitarioForm extends AbstractForm{

	public DepositoUnitarioForm(IFormModel model) {
		super(model);
	}

	@Override
	protected JComponent buildFormPanel() {
		FormLayout layout=new FormLayout(
				"p,2dlu,max(p;70dlu), 3dlu" 
				//+",p,2dlu,max(p;70dlu) "
				,""
				);
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Banco",getControl("banco"));
		builder.append("Número",getControl("numero"));		
		builder.append("Importe",getControl("importe"));
		//ComponentUtils.decorateSpecialFocusTraversal(builder.getPanel());
		return builder.getPanel();
	}
	
	
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if("importe".equals(property)){
			return Binder.createBigDecimalMonetaryBinding(model.getModel(property));
		}if("banco".equals(property)){
			return CXCBindings.createBancosBinding(model.getModel(property));
		}
		else
			return null;
	}
	
	

	@Override
	protected void onWindowOpened() {
		model.validate();
		super.onWindowOpened();
	}

	public static DepositoUnitario editarDeposito(){
		return editarDeposito(new DepositoUnitario());
	}
	
	public static DepositoUnitario editarDeposito(final DepositoUnitario bean){
		final DefaultFormModel model=new DefaultFormModel(bean);
		final DepositoUnitarioForm form=new DepositoUnitarioForm(model);
		((JFormattedTextField)form.getControl("importe")).setValue(model.getValue("importe"));  //PARCHE TEMPORAL
		form.open();		
		if(!form.hasBeenCanceled()){
			return bean;
		}
		return null;
	}
	
	public static void mostrarDeposito(final DepositoUnitario bean){
		final DefaultFormModel model=new DefaultFormModel(bean);
		model.setReadOnly(true);
		
		final DepositoUnitarioForm form=new DepositoUnitarioForm(model);
		((JFormattedTextField)form.getControl("importe")).setValue(model.getValue("importe"));
		
		form.open();
	}

}
