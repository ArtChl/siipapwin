package com.luxsoft.siipap.ventas.agents;

import java.util.Date;

/**
 * Agente que vigila la consistencia de las ventas aplicando procesos
 * rutinarios
 * 
 * 
 * @author Ruben Cancino
 *
 */
public interface AgenteDeVentas {
	
	
	/**
	 * Revisa la consistencia de las ventas en funcion de:
	 * 
	 *  -Saldo
	 *  -Costo
	 *  -Utilidad
	 *  -Venta Neta
	 *  -Provision
	 *  
	 *  para el dia especificado
	 *
	 */
	public void revisarVentas(Date date);
	
	/**
	 * Revisa la consistencia de las ventas de las ventas del dia
	 * 
	 *  
	 *
	 */
	public void revisarVentas();
	
	

}
