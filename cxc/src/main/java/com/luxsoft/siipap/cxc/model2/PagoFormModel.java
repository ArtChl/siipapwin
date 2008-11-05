package com.luxsoft.siipap.cxc.model2;

import java.math.BigDecimal;
import java.util.List;

import ca.odell.glazedlists.EventList;

import com.luxsoft.siipap.cxc.domain.DepositoRow;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.swing.form2.IFormModel;

/**
 * Extiende {@link IFormModel} para las peculiaridades de un {@link PagoM}
 * 
 * @author Ruben Cancino
 *
 */
public interface PagoFormModel extends IFormModel{
	
	/**
	 * Verifica que el pago sea valido y util para ser utilizado en pago con otros
	 * 
	 * @param pago
	 * @return
	 */
	public boolean isValid(final PagoM pago);
	
	/**
	 * {@link EventList} de los pagos a generar
	 * 
	 * @return
	 */
	public EventList<Pago> getPagos();
	
	/**
	 * El pago que ser raliza es de facturas, de lo contrario es para pagar Notas de cargo
	 * 
	 * @return
	 */
	public boolean isParaFacturas();
	
	/**
	 * Valida que el importe del pago
	 * 
	 * @return
	 */
	public boolean validarImporte();
	
	
	/**
	 * Actualiza el importe de los pagos aplicados 
	 * 
	 */
	public void actualizarPagos();
	
	/**
	 * Regresa el disponible que se tiene para generar el pago
	 * 
	 * @return
	 */
	public BigDecimal getDisponible();
	
	public List<DepositoRow> buscarDepositosDisponibles();

}
