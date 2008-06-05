package com.luxsoft.siipap.cxc.model2;

import ca.odell.glazedlists.EventList;

import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.swing.form2.IFormModel;

/**
 * Interfaz para el proceso de pago con otros
 * 
 * 
 * @author Ruben Cancino
 *
 */
public interface PagoConOtrosModel extends IFormModel{
	
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
	 * @return
	 */
	public boolean isParaFacturas();
	
	/**
	 * Valida que el importe asignado no exceda el disponible del origen
	 * @return
	 */
	public boolean validarImporte();
	
	
	/**
	 * Actualiza el importe de los pagos aplicados 
	 * 
	 */
	public void actualizarPagos();
	
	//public TableFormat<Pago> getVentasTF();

}
