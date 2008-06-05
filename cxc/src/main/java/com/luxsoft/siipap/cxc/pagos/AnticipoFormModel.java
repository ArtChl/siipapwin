package com.luxsoft.siipap.cxc.pagos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.springframework.util.ClassUtils;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.PagoM;

public class AnticipoFormModel extends PresentationModel{
	
	private final ValidationResultModel validationModel;

	
	
	public AnticipoFormModel(final PagoM pago){
		super(pago);
		//getPago().setFormaDePago(FormaDePago.A);
		validationModel=new DefaultValidationResultModel();
		addBeanPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				validate();
			}			
		});
	}
	
	private PagoM getPago(){
		return (PagoM)getBean();
	}
	
	public ValidationResultModel getValidationModel(){
		return validationModel;
	}
	
	
	@SuppressWarnings("unchecked")
	public ValidationResult validate(){
		final Class clazz=PagoM.class;
		final String role=ClassUtils.getShortName(clazz);
		final PropertyValidationSupport support =new PropertyValidationSupport(clazz,role);
		
		final ClassValidator validator=new ClassValidator(clazz);
		final InvalidValue[] invalid=validator.getInvalidValues(getBean());
		for(InvalidValue iv:invalid){
			String propName=iv.getPropertyName();						
			support.addError(propName, iv.getMessage());
		}
		if(StringUtils.isBlank(getPago().getReferencia()))
			support.addError("referencia", "La referencia es obligatoria");
		if(StringUtils.isBlank(getPago().getBanco()))
			support.addError("banco", "El banco es obligatorio");		
		this.getValidationModel().setResult(support.getResult());
		return support.getResult();
	}
	

}
