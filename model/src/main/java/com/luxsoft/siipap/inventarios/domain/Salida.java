/*
 * Created on 20/04/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.inventarios.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Movimiento de inventario que representa la salida de 
 * de articulos del inventario
 * 
 * @author Ruben Cancino
 * 
 */
public  class Salida extends Movimiento{
	
	
	
	private Entrada destino;
	
	
	/**
	 * Cuando el contenido de esta salida se usara en otro almacen
	 * o sucursal. Este es el caso de los TPS que tiene como destino
	 * un TPE, pero puede tambien ser usado por FAC's que son entradas
	 * 
	 * @return
	 */
	public Entrada getDestino() {
		return destino;
	}
	public void setDestino(Entrada destino) {
		Object old=this.destino;
		this.destino = destino;
		getPropertyChangeSupport().firePropertyChange("destino",old,destino);
	}

	
	
	public String toString(){
        return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)			
			.append("Id:",getId())
        	.append("Sucursal:",getALMSUCUR())
			.append("Movimiento:",getALMTIPO())
			.append("Documento:",getALMNUMER())			
        	.append("Tipo:",getALMTIPFA())
        	.append("Fecha:",getALMFECHA())
			.append("Articulo:",getALMARTIC())
			.append("Cantidad:",getALMCANTI())			
        	.toString();
    }
    
}
