package com.luxsoft.siipap.cxc.model;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.Validator;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.Pago;

public class PagoValidator implements Validator{
	
	private final Pago pago;

	public PagoValidator(final Pago pago) {
		super();
		this.pago = pago;
	}

	public ValidationResult validate() {
		PropertyValidationSupport support = 
            new PropertyValidationSupport(pago, "Pago");
		if(pago.getId()!=null){
			support.addError("id", "No se permite modifcar Pagos realizados");
		}
		if("U".equals(pago.getFormaDePago())){
			support.addError("importe", "No se permiten pagos tipo U");
		}else{
			if(pago.getImporte().amount().doubleValue()<=0 && "X".equalsIgnoreCase(pago.getFormaDePago())){
				support.addError("importe", "El importe debe ser mayor a cero");
			}if(StringUtils.isBlank(pago.getReferencia())){
				support.addWarning("referencia", "Preferentemente anote un dato como referencia ");
			}
		}
		if( ("T".equals(pago.getFormaDePago())) && pago.getNotaDelPago()==null){
			support.addError("Nota No.","Se requiere la nota de credito para el pago con Nota" );
		}
		if( ("T".equals(pago.getFormaDePago())) && pago.getNotaDelPago()!=null){			
			double saldo=Math.abs(pago.getNotaDelPago().getSaldo());
			double imp=pago.getImporte().amount().doubleValue();
			if(imp>saldo)
				support.addError("importe", "El importe del pago no puede ser mayor al importe de la nota origen del pago que es :"+saldo );		
			
		}
		if( (pago.getComentario()!=null) && pago.getComentario().length()>50)
			support.addError("comentario", "Longitud maxima del comentario 50 ");
		if( (pago.getReferencia()!=null) && pago.getReferencia().length()>20)
			support.addError("referencia", "Longitud maxima del referencia 20 ");
		return support.getResult();
	}
	
	

}
