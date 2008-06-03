package com.luxsoft.siipap.inventarios.domain.acumulados;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.utils.domain.PersistentObject;

public class Saldo extends PersistentObject implements Serializable{ 
	
	/*
	 * PERIODO,ALMSUCUR,ARTICULO_ID,ALMARTIC,ALMNOMBR,ALMKILOS,ALMUNIXUNI,ALMUNIDMED,SUM(ALMCANTI/ALMUNIXUNI) AS SALDO
	 */
	
	private String periodo; //
	private Integer sucursal;
	private Articulo articulo;
	private String clave;
	//private String descripcion;
	//private BigDecimal kilos;
	//private String unidad;
	//private Integer factor;
	private BigDecimal saldo;
	
	
		
	

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
		Saldo s=(Saldo)obj;
		return new EqualsBuilder()
		.append(getPeriodo(),s.getPeriodo())
		.append(getSucursal(),s.getSucursal())
		.append(getArticulo(),s.getArticulo())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getPeriodo())
		.append(getSucursal())
		.append(getArticulo())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getPeriodo())
		.append(getSucursal())
		.append(getArticulo())
		.append(getSaldo())
		.toString();
	}

}
