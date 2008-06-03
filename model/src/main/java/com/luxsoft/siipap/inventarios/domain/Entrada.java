/*
 * Created on 20/04/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.inventarios.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Precio;


/**
 * Movimiento de inventario que representa la entrada de articulos
 * 
 * 
 * @author Ruben Cancino 
 */

public class Entrada extends Movimiento{
	
	
	
	
	private Salida origen;	
	
	/**
	 * Precio del producto al momento de la entrada.
	 * 
	 */
	private Precio precio;
	
	/**
	 * El DEC  o XCO de un COM
	 */
	private Salida devolucion;
	
	
	//TODO: Crear el vinculo con CXP
	//private Analisis
	
	public Entrada(){
		
	}
	
	/**
	 * Regresa el origen de la entrada cuando se trata de traslados, actualmente se usa cuando
	 * el tipo de entrada es TPE y lo que esta variable almacena es la salida TPS
	 * origen del movimiento. Tambien puede funcionar para las entradas FAC
	 * 
	 */	
	public Salida getOrigen() {
		return origen;
	}

	public void setOrigen(Salida origen) {
		this.origen = origen;
	}


	public Precio getPrecio() {
		return precio;
	}


	public void setPrecio(Precio precio) {
		this.precio = precio;
	}
	
	
	/**
	 * Regresa el DEC o XCO de un COM si es que existe
	 * @return
	 */
    public Salida getDevolucion() {
		return devolucion;
	}
	public void setDevolucion(Salida devolucion) {
		this.devolucion = devolucion;
	}

	public String toString(){
        return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
        	.append(getALMSUCUR())
        	.append(getALMTIPO())
        	.append(getALMNUMER())
        	.append(getALMSERIE())
        	.append(getALMTIPFA())
        	.append(getALMRENGL())
        	.append(getALMARTIC())
        	.append(getALMFECHA())
        	.append(getALMCANTI())
        	.append(getCosto())
        	//.append(getPrecio()!=null?getPrecio().getNeto():"")
        	//.append(getOrigen()!=null? getOrigen():"")        			
        	.toString();
    }
}
