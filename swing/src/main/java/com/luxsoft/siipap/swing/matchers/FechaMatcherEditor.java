package com.luxsoft.siipap.swing.matchers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.Date;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;

import org.jdesktop.swingx.JXDatePicker;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;

import com.luxsoft.siipap.swing.utils.FormatUtils;

/**
 * Matcher editor para filtrar listas por alguna fecha
 * 
 * 
 * 
 * @author Ruben Cancino
 *
 */
public abstract class FechaMatcherEditor extends AbstractMatcherEditor implements PropertyChangeListener{
	
	protected JXDatePicker picker;
	
	
	public FechaMatcherEditor(){
		picker=new JXDatePicker();
		/**
		final JFormattedTextField editor=new JFormattedTextField(FormatUtils.getBasicDateFormatterFactory());
		editor.setInputVerifier(new InputVerifier(){

			public boolean verify(JComponent input) {
				          if (input instanceof JFormattedTextField) {
				              JFormattedTextField ftf = (JFormattedTextField)input;
				              AbstractFormatter formatter = ftf.getFormatter();
				              if (formatter != null) {
				                  String text = ftf.getText();
				                  try {
				                       formatter.stringToValue(text);
				                       return true;
				                   } catch (ParseException pe) {
				                	   System.out.println("FAlse");
				                       return false;
				                   }
				               }
				           }
				           return true;
				       }
			
			public boolean shouldYieldFocus(JComponent input) {
				return false;
			}
			
		});
		picker.setEditor(editor);
		**/
		picker.setFormats(new String[]{"dd/MM/yy"});
		picker.getEditor().setText("");
		picker.addPropertyChangeListener(this);
		
	}

	public void propertyChange(PropertyChangeEvent evt) {		
		if("date".equalsIgnoreCase(evt.getPropertyName())){
			Object val=evt.getNewValue();
			if(val==null)
				fireMatchAll();
			else{
				Date fecha=(Date)val;
				evaluarFecha(fecha);
			}
		}
	}
	
	protected abstract void evaluarFecha(final Date fecha);
	
	public JXDatePicker getPikcer(){
		return picker;
	}
	
	
/**
	public static void main(String[] args) {
		SXAbstractDialog dialog=new SXAbstractDialog("TEST"){

			@Override
			protected JComponent buildContent() {
				FechaMatcherEditor e=new FechaMatcherEditor();
				return e.picker;
			}
			
		};
		dialog.open();
	}
	
	**/

}
