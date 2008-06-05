package com.luxsoft.siipap.cxc.swing.notas;

import java.util.Date;

import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.cxc.domain.TiposDeNotas;

/**
 * Bean para controlar la impresion de notas de credito
 * por descuento tipo U y V 
 * 
 * @author Ruben Cancino
 *
 */
public class ImpresionPorDescuento extends Model{
	
	private Date fecha=new Date();
	private TiposDeNotas tipo=TiposDeNotas.U;
	private int consecutivo=0;
	
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public TiposDeNotas getTipo() {
		return tipo;
	}
	public void setTipo(TiposDeNotas tipo) {
		this.tipo = tipo;
	}
	public int getConsecutivo() {
		return consecutivo;
	}
	public void setConsecutivo(int consecutivo) {
		this.consecutivo = consecutivo;
	}
	

}
