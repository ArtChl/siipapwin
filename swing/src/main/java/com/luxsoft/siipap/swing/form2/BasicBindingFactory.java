package com.luxsoft.siipap.swing.form2;

import java.text.NumberFormat;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.binding.CantidadMonetariaBinding;

/**
 * Implementación basica de BindingFactory
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class BasicBindingFactory implements BindingFactory{
	
	protected Logger logger=Logger.getLogger(getClass());
	
	private BeanPropertiesExtractor extractor;

	
	public FormControl getFormControl(final String property,final IFormModel model){
		Class clazz=model.getPropertyType(property);
		FormControl control;
		try {
			if(clazz==null){
				throw new RuntimeException("No localizo la propiedad :"+getFullPath(model.getBaseBeanClass(), property));
			}
			else if(clazz.equals(String.class)){
				control= getStringControl(property, model);
			}else if(clazz.equals(Date.class)){
				control=getDateControl(property,model);
			}else if(clazz.equals(CantidadMonetaria.class)){
				control=getCantidadMonetariaControl(property, model);
			}else if(isEntero(clazz)){
				control=getIntegerControl(property,model);
			}else if(isDecimal(clazz)){
				if(isPorcentage(property, model)){
					control=getPorcentageControl(property,model);
				}else
					control=getDoubleControl(property, model);
			}else if(clazz.equals(boolean.class) || clazz.equals(Boolean.class)){
				return getBooleanControl(property, model);
			}
			else
				control= getNotFoundComponent(model.getBaseBeanClass(), property);
		} catch (Throwable tx) {
			tx.printStackTrace();
			control=getErrorComponent(getFullPath(model.getBaseBeanClass(), property), tx);
		}
		control.setLabelKey(getFullPath(model.getBaseBeanClass(), property));
		control.setLabel(getLabel(model.getBaseBeanClass(), property));
		return control;
	}
	
	protected FormControl getStringControl(final String property,final IFormModel model){
		final JTextField tf=BasicComponentFactory.createTextField(model.getComponentModel(property));
		final FormControl fc=new FormControl(tf,property);
		//fc.setLabelKey(getLabel(model.getBaseBeanClass(), property));
		return fc;
	}
	
	protected FormControl getIntegerControl(final String property,final IFormModel model){
		final NumberFormat format=NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		final JTextField tf=BasicComponentFactory.createIntegerField(model.getComponentModel(property),format);
		tf.setEnabled(!model.isReadOnly());
		final FormControl fc=new FormControl(tf,property);
		return fc;
	}
	
	protected FormControl getDateControl(final String property,final IFormModel model){
		final ValueModel vm=model.getModel(property);
		JXDatePicker c=(JXDatePicker)Binder.createDateComponent(vm);
		c.setEditable(!model.isReadOnly());
		final FormControl rc=new FormControl(c,property);
		return rc;
	}
	
	protected FormControl getCantidadMonetariaControl(final String property,IFormModel model){
		final CantidadMonetariaBinding b=new CantidadMonetariaBinding(model.getModel(property));
		b.getControl().setEnabled(!model.isReadOnly());
		final FormControl rc=new FormControl(b.getControl(),property);
		return rc;
	}
	
	protected FormControl getDoubleControl(final String property,IFormModel model){
		JFormattedTextField tf=Binder.createNumberBinding(model.getComponentModel(property));		
		final FormControl rc=new FormControl(tf,property);
		return rc;
	}
	
	protected FormControl getPorcentageControl(final String property,final IFormModel model){
		JFormattedTextField tf=Binder.createDescuentoBinding(model.getComponentModel(property));		
		final FormControl rc=new FormControl(tf,property);
		return rc;
	}
	
	protected FormControl getBooleanControl(final String property,final IFormModel model){
		JCheckBox box=BasicComponentFactory.createCheckBox(model.getModel(property), "");
		final FormControl rc=new FormControl(box,property);
		return rc;
	}
	
	private FormControl getErrorComponent(final String proprety,final Throwable tx){
		final JTextField tf=new JTextField(20);
		tf.setEditable(false);
		tf.setToolTipText(tx.getLocalizedMessage());
		ValidationComponentUtils.setErrorBackground(tf);
		final FormControl control=new FormControl(tf,proprety);
		return control;		
	}
	
	
	
	private FormControl getNotFoundComponent(final Class clazz,final String property){
		final JTextField tf=new JTextField(20);
		tf.setEnabled(false);
		final String label=getFullPath(clazz, property);
		tf.setToolTipText("Not binding for property: "+label);
		//ValidationComponentUtils.setWarningBackground(tf);
		final FormControl control=new FormControl(tf,property);
		return control;		
	}
	
	private String getFullPath(final Class clazz,final String property){
		return ClassUtils.getShortName(clazz)+"."+property;
	}
	
	protected String getLabel(final Class clazz,final String property){
		String key=getFullPath(clazz, property);
		if(getExtractor()!=null){
			String l= getExtractor().getLabel(clazz, property);
			if(StringUtils.hasText(l))
				return l;
		}
		return key;
	}
	
	private boolean isEntero(Class clazz){
		return (ClassUtils.isAssignable(Long.class, clazz)
				||ClassUtils.isAssignable(Integer.class, clazz)
				||ClassUtils.isAssignable(Short.class, clazz)
				||ClassUtils.isAssignable(long.class, clazz)
				||ClassUtils.isAssignable(int.class, clazz)
				||ClassUtils.isAssignable(short.class, clazz));		
	}
	
	private boolean isDecimal(Class clazz){
		return (ClassUtils.isAssignable(Double.class, clazz)
				||ClassUtils.isAssignable(Float.class, clazz)
				||ClassUtils.isAssignable(double.class, clazz)
				||ClassUtils.isAssignable(float.class, clazz)
				);
	}
	
	private boolean isPorcentage(final String property,final IFormModel model){
		return getExtractor().isPorcentage(model.getPropertyType(property), property);		
	}

	public BeanPropertiesExtractor getExtractor() {
		if(extractor==null){
			extractor=new BeanPropertiesExtractorImpl();
		}
		return extractor;
	}
	public void setExtractor(BeanPropertiesExtractor extractor) {
		this.extractor = extractor;
	}
	
	
	

}
