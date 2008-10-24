package com.luxsoft.siipap.cxc.pagos;

import java.util.List;

import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.ValidationResultModel;
import com.luxsoft.siipap.cxc.domain.DepositoRow;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Mantiene el estado y comportamiento del proceso de pagos de facturas
 * 
 * @author Ruben Cancino
 *
 */
public interface PagosModel {
				
	
	/**
	 * Acceso al bean de PagoM 
	 * 
	 * @return
	 */
	public PagoM getPagoM();
	
	/**
	 * PresentationModel para la forma de Pagos
	 * 
	 * @return
	 */
	public PresentationModel getPagoMPModel();
	
	/**
	 * ValidationResultModel para la forma de pagos
	 * @return
	 */
	public ValidationResultModel getValidationModel();
	
	/**
	 * Actualiza la validacion en funcion del estado
	 *
	 */
	//public void validate();
	
	/**
	 * Lista de pagos individuales a facturas
	 * 
	 * @return
	 */
	public EventList<Pago> getPagos();
	
	/**
	 * Genera una aplicacion de pago para cada factura
	 * tomando como base el saldo-descuento 
	 * 
	 * @param facturas
	 */
	public EventList<Pago> generarPagos(final List<Venta> facturas);
			
	/**
	 * El saldo total de las facturas seleccionadas
	 * 
	 * @return
	 */
	public ValueModel getPorPagar();
	
	/**
	 * Pendiente de pago de todas las facturas del grupo, tomando en cuenta
	 * el descuento
	 * 
	 * @return
	 */
	public CantidadMonetaria getPendiente();
	
	/**
	 * Regresa el pendiente de las facturas considerando lo que el usuario ha
	 * digitado como importe del pago
	 * 
	 * @return
	 */
	public CantidadMonetaria getPendienteDespuesDePago();
	
	/**
	 * Registra un importe para el pago y regresa verdadero si este cubre
	 * el saldo de las facturas para aplicar dicho pago, si el importe no es 
	 * suficiente regresa falso
	 * 
	 * @param importe
	 * @return
	 
	public boolean importeRegistrado();
	*/
	
	/**
	 * Calcula el importe del pago estimado para esta factura
	 * principalmente depende del saldo de la misma y las politicas
	 * de credito
	 * @param v
	 * @return
	 */
	public CantidadMonetaria estimarPago(final Venta v);
	
	
	public List<DepositoRow> buscarDepositosDisponibles();
}
