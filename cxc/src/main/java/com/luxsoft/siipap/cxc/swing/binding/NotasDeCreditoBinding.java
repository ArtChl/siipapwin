package com.luxsoft.siipap.cxc.swing.binding;

import java.text.MessageFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.JFormattedTextField.AbstractFormatter;

import org.apache.commons.lang.math.NumberUtils;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.uif.util.ComponentUtils;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class NotasDeCreditoBinding extends Model{
	
	private JFormattedTextField numero;
	
	
	
	private boolean enabled=true;
	
	private final ValueModel valueModel;
	private final String clave;
	
	public NotasDeCreditoBinding(final ValueModel valueModel,final String cliente){
		this.valueModel=valueModel;
		this.clave=cliente;
		initComponents();
	}
	
	private void initComponents(){
		final ToNotaFormat format=new ToNotaFormat();		
		numero=new JFormattedTextField(format);	
		if(valueModel instanceof ComponentValueModel){
			ComponentValueModel cm=(ComponentValueModel)valueModel;
			setEnabled(cm.isEnabled());
			PropertyConnector.connect(this, "enabled", cm, ComponentValueModel.PROPERTYNAME_ENABLED);
		}		
		//Lookup
		ComponentUtils.addAction(numero, new DispatchingAction(this,"lookup"), KeyStroke.getKeyStroke("F2"));		
	}
	
	public Object lookup(){
		SelectorDeNotasParaPago selector=new SelectorDeNotasParaPago(clave);
		selector.open();
		if(selector.hasBeenCanceled())
			return null;
		Object obj=selector.getSelected();
		selector.disposeGlazedList();
		numero.setValue(obj);
		return obj;
	}
	
	public Object lookup(final String text){
		System.out.println("Buscando nota "+text+" cliente "+clave);
		NotaDeCredito n=null;
		if(NumberUtils.isNumber(text)){
			long numero=NumberUtils.toLong(text);
			n= getDao().buscarNotaConSaldo(numero, clave);
		}
		return n;		
	}
	
	
	private class ToNotaFormat extends AbstractFormatter{
		@Override
		public Object stringToValue(String text) throws ParseException {			
			return lookup(text);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if(value!=null){
				NotaDeCredito n=(NotaDeCredito)value;
				valueModel.setValue(n);
				return MessageFormat.format("Numero {0}, Tipo {1}, Fecha {2}", n.getNumero(),n.getTipo(),n.getFecha());
			}
			return "NA";
		}		
	}

	public JFormattedTextField getNumero() {
		return numero;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		Object old=this.enabled;
		this.enabled = enabled;
		numero.setEnabled(enabled);		
		firePropertyChange("enabled", old, enabled);
	}

	public NotaDeCreditoDao getDao() {
		return (NotaDeCreditoDao)ServiceLocator.getDaoContext().getBean("notasDeCreditoDao");
	}
	
	
	
}
