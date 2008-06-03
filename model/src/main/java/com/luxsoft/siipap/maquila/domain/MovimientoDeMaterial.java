package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.utils.domain.PersistentObject;

public class MovimientoDeMaterial extends PersistentObject{
	
	private Bobina articulo;	
	private Date fecha=Calendar.getInstance().getTime();
	private BigDecimal metros2=BigDecimal.ZERO;
	private BigDecimal kilos=BigDecimal.ZERO;	
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	private double precioPorKilo;
	private double precioPorM2;	
	private String observaciones;
	
	
	public Bobina getArticulo() {
		return articulo;
	}

	public void setArticulo(Bobina articulo) {
		this.articulo = articulo;
	}		
	

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public CantidadMonetaria getImporte() {
		return importe;
	}

	public void setImporte(CantidadMonetaria importe) {	
		Object old=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe",old,importe);
		recalcularPrecios();
	}

	public BigDecimal getKilos() {
		return kilos;
	}

	public void setKilos(BigDecimal kilos) {
		this.kilos = kilos;
	}

	public BigDecimal getMetros2() {
		return metros2;
	}

	public void setMetros2(BigDecimal metros2) {
		Object old=this.metros2;
		this.metros2 = metros2;
		getPropertyChangeSupport().firePropertyChange("metros2",old,metros2);
	}
	
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	

	/**
	 * Factor de conversion  Kilos/M2
	 * 
	 * Si se require ir de Kilos a M2 se multiplica
	 * Si se requiere ir de M2 a Kilos se dividen
	 * @return
	 */
	public BigDecimal getFactorDeConversion(){
		//return getMetros2().divide(getKilos(),5,RoundingMode.HALF_EVEN);
		//return getKilos().divide(getMetros2(),6,RoundingMode.HALF_EVEN);
		double val=getKilos().doubleValue()/getMetros2().doubleValue();
		return BigDecimal.valueOf(val);
	}
	
	public void recalcularPrecios(){
		if(getKilos()!=null && !getKilos().equals(BigDecimal.ZERO)){
			double imp=getImporte().getAmount().doubleValue();
			double kil=getKilos().abs().doubleValue();
			setPrecioPorKilo(imp/kil);
		}
		if(getMetros2()!=null && !getMetros2().equals(BigDecimal.ZERO)){
			double imp=getImporte().getAmount().doubleValue();
			double m2=getMetros2().abs().doubleValue();
			setPrecioPorM2(imp/m2);
		}
	}
	
	public double getPrecioPorKilo(){
		return precioPorKilo;
	}
	public void setPrecioPorKilo(double precioPorKilo) {
		Object old=this.precioPorKilo;
		this.precioPorKilo = precioPorKilo;
		getPropertyChangeSupport().firePropertyChange("precioPorKilo",old,precioPorKilo);
	}
	
	public double getPrecioPorM2(){
		return precioPorM2;
	}
	public void setPrecioPorM2(double precioPorM2) {
		Object old=this.precioPorM2;
		this.precioPorM2 = precioPorM2;
		getPropertyChangeSupport().firePropertyChange("precioPorM2",old,precioPorM2);
		
	}
	

	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this) return true;
		EntradaDeMaterial e=(EntradaDeMaterial)obj;
		return new EqualsBuilder()
		.append(getArticulo(),e.getArticulo())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getArticulo())
		.toHashCode();
	}
	
	private Date creado=Calendar.getInstance().getTime();
	private Date modificado;
	
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
	
	


}
