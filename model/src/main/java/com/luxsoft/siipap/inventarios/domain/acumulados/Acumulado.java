package com.luxsoft.siipap.inventarios.domain.acumulados;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.utils.domain.PersistentObject;

public class Acumulado extends PersistentObject{
	
	private String periodo;
	private String tipo;
	private String articulo;
	private Long saldoInicial=new Long(0);
	private Long movimientos=new Long(0);
	private Integer unixuni=new Integer(1000);
	private BigDecimal costoPromedioIni=BigDecimal.ZERO;
	private BigDecimal costoPromedio=BigDecimal.ZERO;
	private BigDecimal costoPepsIni=BigDecimal.ZERO;
	private BigDecimal costoPeps=BigDecimal.ZERO;
	private BigDecimal costoUepsIni=BigDecimal.ZERO;
	private BigDecimal costoUeps=BigDecimal.ZERO;
	private BigDecimal costoUltimoIni=BigDecimal.ZERO;
	private BigDecimal costoUltimo=BigDecimal.ZERO;

	public Acumulado() {
	}
	
	public String getArticulo() {
		return articulo;
	}	
	public void setArticulo(String articulo) {
		this.articulo = articulo;
	}
	
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public void setPeriodo(Periodo p){
		setPeriodo(AcumuladoUtils.format(p));
	}	
	
	public Periodo toPeriodo(){
		return AcumuladoUtils.parse(getPeriodo());
	}
	
	public BigDecimal getCostoPeps() {
		return costoPeps;
	}
	public void setCostoPeps(BigDecimal costoPeps) {
		this.costoPeps = costoPeps;
	}

	public BigDecimal getCostoPromedio() {
		return costoPromedio;
	}
	public void setCostoPromedio(BigDecimal costoPromedio) {
		this.costoPromedio = costoPromedio;
	}

	public BigDecimal getCostoUeps() {
		return costoUeps;
	}
	public void setCostoUeps(BigDecimal costoUeps) {
		this.costoUeps = costoUeps;
	}

	public BigDecimal getCostoUltimo() {
		return costoUltimo;
	}
	public void setCostoUltimo(BigDecimal costoUltimo) {
		this.costoUltimo = costoUltimo;
	}

	public Long getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(Long movimientos) {
		this.movimientos = movimientos;
	}
	
	

	public Integer getUnixuni() {
		return unixuni;
	}

	public void setUnixuni(Integer unixuni) {
		this.unixuni = unixuni;
	}
	
	

	public BigDecimal getCostoPepsIni() {
		return costoPepsIni;
	}

	public void setCostoPepsIni(BigDecimal costoPepsIni) {
		this.costoPepsIni = costoPepsIni;
	}

	public BigDecimal getCostoPromedioIni() {
		return costoPromedioIni;
	}

	public void setCostoPromedioIni(BigDecimal costoPromedioIni) {
		this.costoPromedioIni = costoPromedioIni;
	}

	public BigDecimal getCostoUepsIni() {
		return costoUepsIni;
	}

	public void setCostoUepsIni(BigDecimal costoUepsIni) {
		this.costoUepsIni = costoUepsIni;
	}

	public BigDecimal getCostoUltimoIni() {
		return costoUltimoIni;
	}

	public void setCostoUltimoIni(BigDecimal costoUltimoIni) {
		this.costoUltimoIni = costoUltimoIni;
	}

	public Long getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(Long saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this)return true;
		Acumulado acu=(Acumulado)obj;
		return new EqualsBuilder()
			.append(getPeriodo(),acu.getPeriodo())
			.append(getTipo(),acu.getTipo())
			.append(getArticulo(),acu.getArticulo())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
			.append(getPeriodo())
			.append(getTipo())			
			.append(getArticulo())
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append(getPeriodo())
			.append(getTipo())
			.append(getArticulo())
			.append(getMovimientos())
			.toString();
	}

}
