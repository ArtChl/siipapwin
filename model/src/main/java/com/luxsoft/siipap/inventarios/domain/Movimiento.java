/*
 * Created on 27-feb-2005
 *
 * TODO Colocar informacion de licencia
 */
package com.luxsoft.siipap.inventarios.domain;




import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.luxsoft.siipap.clipper.domain.Almace;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;

/**
 * Representacion  de un movimiento de inventario, ya sea una
 * salida o una entrada. Se clasifican por tipo Tipo y las entradas y
 * salidas se diferencian por el valor + o - de la cantidad
 * 
 * @author Ruben Cancino 
 * 
 * 
 */
public  class Movimiento extends Almace{
    
    
    
    private Articulo articulo;
    private CantidadMonetaria costo;
    private String periodo;
    private Date creado;
    private Date modificado;
	
	
    
    public Movimiento(){        
    }



	public Articulo getArticulo() {
		return articulo;
	}
	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}



	public CantidadMonetaria getCosto() {
		return costo;
	}
	public void setCosto(CantidadMonetaria costo) {
		this.costo = costo;
	}
	
	public CantidadMonetaria calcularImporte(){
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		BigDecimal cantidad=BigDecimal.valueOf(getALMCANTI());
		BigDecimal unidades=BigDecimal.valueOf(getALMUNIXUNI().longValue());
		cantidad=cantidad.divide(unidades,3,RoundingMode.HALF_EVEN);
		if(getCosto()!=null)
			importe=getCosto().multiply(cantidad);
		return importe;
	}



	public String getPeriodo() {
		return periodo;
	}



	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}



	public Date getCreado() {
		return creado;
	}



	public void setCreado(Date creado) {
		this.creado = creado;
	}



	public Date getModificado() {
		return modificado;
	}



	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	
	public double getCantidad(){
		return getALMCANTI().doubleValue()/getALMUNIXUNI().doubleValue();
	}
	
    	
		
}
