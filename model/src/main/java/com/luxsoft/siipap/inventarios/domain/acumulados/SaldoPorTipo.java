package com.luxsoft.siipap.inventarios.domain.acumulados;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.utils.domain.PersistentObject;

public class SaldoPorTipo extends PersistentObject implements Serializable{ 
	
	
	private String periodo; //
	private Integer sucursal;
	private Articulo articulo;
	private String tipo;
	private String clave;
	private BigDecimal saldo;
	//private BigDecimal movimientos;	
	
	
	
		
	

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public Integer getSucursal() {
		return sucursal;
	}

	public void setSucursal(Integer sucursal) {
		this.sucursal = sucursal;
	}

	

	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		SaldoPorTipo s=(SaldoPorTipo)obj;
		return new EqualsBuilder()
		.append(getPeriodo(),s.getPeriodo())
		.append(getSucursal(),s.getSucursal())
		.append(getArticulo(),s.getArticulo())
		.append(getTipo(),s.getTipo())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getPeriodo())
		.append(getSucursal())
		.append(getArticulo())
		.append(getTipo())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getPeriodo())
		.append(getSucursal())
		.append(getArticulo())
		.append(getTipo())
		.append(getSaldo())
		.toString();
	}

}
