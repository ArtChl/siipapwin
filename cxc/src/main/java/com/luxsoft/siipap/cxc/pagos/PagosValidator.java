package com.luxsoft.siipap.cxc.pagos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * Valida el proceso de pago generando,Obserba loa cambios en
 * algunas de las propiedades en PagosModel.PagoM
 * para activar la validacion
 *  
 * @author Ruben Cancino
 *
 */
public class PagosValidator implements PropertyChangeListener{
	
	private final PagosModel pagosModel;
	private Logger logger=Logger.getLogger(getClass());
	
	public PagosValidator(final PagosModel model){
		this.pagosModel=model;
		model.getPagoMPModel().addBeanPropertyChangeListener(this);
	}
	
	public void validate() {
		
		final String role="Pago";//ClassUtils.getShortName(PagoM.class);
		final PagoM target=getPagosModel().getPagoM();
		final PropertyValidationSupport support =new PropertyValidationSupport(target,role);
		
		final ClassValidator<PagoM> validator=new ClassValidator<PagoM>(PagoM.class);
		final InvalidValue[] invalid=validator.getInvalidValues(target);
		for(InvalidValue iv:invalid){
			String propName=iv.getPropertyName();						
			support.addError(propName, iv.getMessage());
		}
		//validarImporte(support, target);
		//validarReferencia(support, target);
		validarFormaDePago(support, target);
		validarFecha(support, target);
		if(logger.isDebugEnabled()){
			if(support.getResult().hasErrors()){
				String pattern="NVA Existen {0} errores en el proceso de pago";
				logger.debug(MessageFormat.format(pattern, support.getResult().getErrors().size()));
				
			}
			
		}
		getPagosModel().getValidationModel().setResult(support.getResult());
	}
	
	/**
	 * Valida que el importe del pago sea el correcto
	 *
	 */	
	public void validarImporte(final PropertyValidationSupport support,final PagoM target){
		CantidadMonetaria importe=target.getImporte();		
		if(importe==null || importe.amount().doubleValue()<=0)
			support.addError("[importe]", "El importe del pago debe ser >0 valor actual: "+importe);		
	}
	
	@SuppressWarnings("unused")
	private void validarReferencia(final PropertyValidationSupport support,final PagoM target){
		if(StringUtils.isBlank(target.getReferencia())){
			support.addWarning("[referencia]", "Es preferible registrar una referencia");
		}
	}
	
	private void validarFormaDePago(final PropertyValidationSupport support,final PagoM target){
		switch (target.getFormaDePago()) {
		case H:
		case Y:		
		case C:		 
		case O:
		case N:
			if(StringUtils.isBlank(target.getBanco())){
				support.addError("formaDePago", "El Banco es obligatorio para esta forma de pago, seleccione un banco");
			}
			if(StringUtils.isBlank(target.getReferencia())){
				support.addError("[referencia]", "La referencia es obligatoria para esta forma de pago");
			}
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("unused")
	private void validarFecha(final PropertyValidationSupport support,final PagoM target){
		if(target.getFecha()==null){
			support.addError("[fecha]", "La fecha es obligatoria");
		}
	}
	
	
	/**
	 * Acceso a PagoModel 
	 * @return
	 */
	public PagosModel getPagosModel() {
		return pagosModel;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(logger.isDebugEnabled()){
			String msg="Validando propiedad: {0} valor: {1}";		
			logger.debug(MessageFormat.format(msg, evt.getPropertyName(),evt.getNewValue()));
		}
		validate();
	}
	

}
