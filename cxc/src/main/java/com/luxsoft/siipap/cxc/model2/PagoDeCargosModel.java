package com.luxsoft.siipap.cxc.model2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * {@link PagoFormModel} especializado en pago de cargos cuando el tipo de pago
 * Dinero en cualquiera de sus formas
 * 
 * @author Ruben Cancino
 *
 */
public class PagoDeCargosModel extends DefaultPagoFormModelImpl{

	public PagoDeCargosModel(PagoM pago) {
		super(pago,false);
	}
	
	protected void init(){
		PropertyChangeListener handler=new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {				
				actualizarPagos();
			}			
		}; 
		this.pmodel.addBeanPropertyChangeListener("importe", handler);
	}
	
	/**
	private void actualizarDisponible(){
		System.out.println("Actualizando el disponible");
	}
	
	public void actualizarPagos(){
		super.actualizarPagos();
		actualizarDisponible();
	}
	**/
	
	/**
	 * Metodo sobre cargado por eficiencia ya q' los cargos no manejan descuentos
	 * 
	 */
	protected void actualizarDescuentos(){}
	
	protected void actualizarDisponible(final CantidadMonetaria disponible){
		getPago().setDisponible(disponible.amount());
	}
	
	protected void addValidation(PropertyValidationSupport support){		
		validarFormaDePago(support);
		validarReferencia(support);
	}
	
	private void validarReferencia(final PropertyValidationSupport support){
		if(StringUtils.isBlank(getPago().getReferencia())){
			support.addWarning("referencia", "Es preferible registrar una referencia");
		}
	}
	
	private void validarFormaDePago(final PropertyValidationSupport support){
		switch (getPago().getFormaDePago()) {
		case H:
		case Y:		
		case C:		 
		case O:
		case N:
			if(StringUtils.isBlank(getPago().getBanco())){
				support.addError("formaDePago", "El Banco es obligatorio para esta forma de pago, seleccione un banco");
			}
			if(StringUtils.isBlank(getPago().getReferencia())){
				support.addError("referencia", "La referencia es obligatoria para esta forma de pago");
			}
			break;
		default:
			break;
		}
	}
	
	private class ImporteHandler implements PropertyChangeListener{

		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals("importe")){
				
			}
			
		}
		
	}

}
