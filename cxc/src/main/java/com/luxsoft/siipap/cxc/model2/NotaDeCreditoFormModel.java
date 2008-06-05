package com.luxsoft.siipap.cxc.model2;

import java.util.List;

import ca.odell.glazedlists.EventList;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.swing.form2.IFormModel;

public interface NotaDeCreditoFormModel extends IFormModel{
	
	
	public EventList<NotasDeCreditoDet> getPartidas();
	
	/**
	 * En virtud de que las notas solo tiene capacidad de 11 partidas
	 * este metodo regresa una lista con las notas correctamente generadas
	 * a partir de las ventas seleccionadas
	 * 
	 * @return
	 */
	public List<NotaDeCredito> procesar();
	
	
	/**
	 * Genera el importe de la nota en funcion de un descuento general
	 * 
	 * @param desc
	 */
	public void aplicarDescuento();
	
	/**
	 * Genera el importe de las partidas a partir de un importe fijo definido
	 * en el maestro {@link NotaDeCredito}
	 * 
	 * @param importe
	 */
	public void aplicarImporte();
	
	/**
	 * Determina si el calculo sera mediante descuento o mediante
	 * importe fijo asignado
	 * 
	 * @return
	 */
	public boolean isPorDescuento();
	
	public void setPorDescuento(boolean porDescuento);
	
	/**
	 * Actualiza los totales de la nota
	 */
	public void actualizar();
	
	public NotaDeCredito getNota();
	

}
