package com.luxsoft.siipap.inventarios.domain.acumulados;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.luxsoft.siipap.inventarios.domain.TipoDeMovimiento;

public class AcumuladoCrossTab {
	
	private String periodo;
	private String articulo;
	private String descripcion;
	private String familia;
	private String familiaNombre;
	private BigDecimal totalPromedio;
	
	private Map<TipoDeMovimiento,Acumulado> acumulados=new HashMap<TipoDeMovimiento,Acumulado>();
	
	

	public AcumuladoCrossTab() {		
	}
	

	public AcumuladoCrossTab(String periodo, String articulo) {		
		this.periodo = periodo;
		this.articulo = articulo;
	}


	public Map<TipoDeMovimiento, Acumulado> getAcumulados() {
		return acumulados;
	}

	public void setAcumulados(Map<TipoDeMovimiento, Acumulado> acumulados) {
		this.acumulados = acumulados;
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


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	public void setTotalPromedio(BigDecimal totalPromedio) {
		this.totalPromedio = totalPromedio;
	}


	public BigDecimal getTotalPromedio(){
		if(totalPromedio==null){
			BigDecimal val=BigDecimal.ZERO;
			for(Acumulado a:getAcumulados().values()){
				val=val.add(a.getCostoPromedio());
			}
			totalPromedio=val;
		}
		return totalPromedio;
	}


	public String getFamilia() {
		return familia;
	}


	public void setFamilia(String familia) {
		this.familia = familia;
	}


	public String getFamiliaNombre() {
		return familiaNombre;
	}


	public void setFamiliaNombre(String familiaNombre) {
		this.familiaNombre = familiaNombre;
	}
	
	
	

}
