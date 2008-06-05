package com.luxsoft.siipap.cxc.model2;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;

public class DescuentoContable {
	
	private int sucursal;
	private String tipo;
	private CantidadMonetaria importe;
	private String cuenta;
	
	
	public DescuentoContable() {	
	}
	
	public  DescuentoContable(int sucursal, String tipo, CantidadMonetaria importe) {
		super();
		this.sucursal = sucursal;
		this.tipo = tipo;
		this.importe = importe;
	}
	
	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		this.importe = importe;
	}
	public int getSucursal() {
		return sucursal;
	}
	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public double getImporteAsDouble(){
		return getImporte().abs().divide(BigDecimal.valueOf(1.15)).amount().doubleValue();
	}
	public double getIva(){
		return getImporteAsDouble()*.15;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Tipo",getTipo())
		.append("Suc",getSucursal())
		.append("Importe",getImporteAsDouble())
		.append("Cuenta",getCuenta())
		.toString();
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	
	public String getSucursalAsString(){
		return StringUtils.leftPad(String.valueOf(getSucursal()), 3,'0');
	}

}
