package com.luxsoft.siipap.cxc.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXDatePicker;


import com.luxsoft.siipap.ventas.domain.Venta;


import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.Matchers;

/**
 * {@link MatcherEditor} para filtrar facturas por fecha determinada 
 *  
 * @author Ruben Cancino
 *
 */
public class FacturasPorFechaSelector extends AbstractMatcherEditor<Venta> implements PropertyChangeListener{
	
	private JXDatePicker control;
	private final String fechaProperty;
	
	
	public FacturasPorFechaSelector(String fechaProperty){
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
			Matcher<Venta> matcher=Matchers.beanPropertyMatcher(Venta.class, fechaProperty, date);
			fireChanged(matcher);
		}	
	}
	
	public JComponent getControl(){
		return control;
	}

}
