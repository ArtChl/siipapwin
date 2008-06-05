package com.luxsoft.siipap.swing.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

import javax.swing.JFormattedTextField;

import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.utils.FormatUtils;

/**
 * Binding para adaptar un ValueModel a un tipo de dato
 * CantidadMonetaria usando un JFormattedTextField
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class CantidadMonetariaBinding {
	
	private JFormattedTextField component;
	private final ValueModel model;
	
	
	public CantidadMonetariaBinding(final ValueModel model){
		this.model=model;
		component=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
		updateComponent();
		component.addPropertyChangeListener("value", new ViewHandler());		
		model.addValueChangeListener(new ModelHandler());
		
	}
	
	public void updateComponent(){
		CantidadMonetaria valor=CantidadMonetaria.pesos(0);
		if(model.getValue()!=null)
			valor=(CantidadMonetaria)model.getValue();
		component.setValue(valor.getAmount().doubleValue());
		if(model instanceof ComponentValueModel){
			ComponentValueModel cm=(ComponentValueModel)model;			
			component.setEnabled(cm.isEnabled());
			component.setEditable(cm.isEditable());
			PropertyConnector.connect(component, "enabled", cm, ComponentValueModel.PROPERTYNAME_ENABLED);
		}
	}
	private class ModelHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			CantidadMonetaria val=(CantidadMonetaria)evt.getNewValue();
			if(val==null)
				val=CantidadMonetaria.pesos(0);
			component.setValue(val.amount());
		}
	}
	
	private class ViewHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			Number val=(Number)evt.getNewValue();
			if(val==null)
				val=BigDecimal.ZERO;
			CantidadMonetaria imp=CantidadMonetaria.pesos(val.doubleValue());
			model.setValue(imp);
		}		
	}
	
	public JFormattedTextField getControl(){
		return component;
	}
	

}
