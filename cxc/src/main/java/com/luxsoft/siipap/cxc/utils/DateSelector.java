package com.luxsoft.siipap.cxc.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.jdesktop.swingx.JXDatePicker;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * MatcherEditor para filtrar por fecha usando un componente
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class DateSelector extends AbstractMatcherEditor implements PropertyChangeListener{
	
	private JXDatePicker control;
	private final boolean before;
	
	public DateSelector(){
		this(false);
	}
	
	public DateSelector(final boolean before){		
		control=new JXDatePicker();		
		SimpleDateFormat df1=new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat df2=new SimpleDateFormat("dd/MM/yy");
		control.setFormats(new DateFormat[]{df1,df2});
		control.getEditor().addPropertyChangeListener("value", this);
		control.getEditor().setValue(null);
		this.before=before;
	}	

	public JXDatePicker getControl() {
		return control;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getNewValue()==null)
			fireMatchAll();
		else{			
			fireChanged(new DateMatcher(control.getDate(),before));
		}
			
	}
	
	private class DateMatcher implements Matcher{
		
		private final Date current;
		private final boolean before;
		
		public DateMatcher(final Date date,final boolean before){
			this.current=date;
			this.before=before;
		}

		public boolean matches(Object item) {
			try {
				Object val=PropertyUtils.getProperty(item, "fecha");
				Date date=(Date)val;
				if(before)
					return date.getTime()<current.getTime();
				else
					return date.getTime()>=current.getTime();
			} catch (Exception e) {}			
			return false;
		}
		
	}
	

}
