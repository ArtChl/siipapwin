package com.luxsoft.siipap.cxc.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXDatePicker;


import com.luxsoft.siipap.cxc.domain.Pago;



import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.Matchers;

/**
 * {@link MatcherEditor} para filtrar instancias de {@link Pago} por fecha determinada 
 *  
 * @author Ruben Cancino
 *
 */
public class PagosPorFechaSelector extends AbstractMatcherEditor<Pago> implements PropertyChangeListener{
	
	private JXDatePicker control;
	private final String fechaProperty;
	
	
	public PagosPorFechaSelector(String fechaProperty){
		this.fechaProperty=fechaProperty;
		control=new JXDatePicker();		
		SimpleDateFormat df1=new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat df2=new SimpleDateFormat("dd/MM/yy");
		control.setFormats(new DateFormat[]{df1,df2});
		control.getEditor().addPropertyChangeListener("value", this);
		control.getEditor().setValue(null);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getNewValue()==null)
			fireMatchAll();
		else{
			Date date=(Date)evt.getNewValue();
			Matcher<Pago> matcher=Matchers.beanPropertyMatcher(Pago.class, fechaProperty, date);
			fireChanged(matcher);
		}	
	}
	
	public JComponent getControl(){
		return control;
	}

}
