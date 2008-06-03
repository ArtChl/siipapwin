package com.luxsoft.siipap.maquila.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.PersistentObject;
 
public class OrdenDeCorte extends PersistentObject{
	
	private Date fecha=Calendar.getInstance().getTime();
	private Almacen almacen;
	private String observaciones;
	private Set<SalidaACorte> salidas=new HashSet<SalidaACorte>();
	
	
	
	
	public Almacen getAlmacen() {
		return almacen;
	}

	public void setAlmacen(Almacen almacen) {
		this.almacen = almacen;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	

	public Set<SalidaACorte> getSalidas() {
		return salidas;
	}

	public void setSalidas(Set<SalidaACorte> salidas) {
		this.salidas = salidas;
	}
	
	public SalidaACorte addCorte(final SalidaACorte sc){
		sc.setOrden(this);
		getSalidas().add(sc);
		return sc;
	}
	
	

	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append(getId())
		.append(fecha)
		.append(almacen)
		.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj ==this)return true;
		OrdenDeCorte oc=(OrdenDeCorte)obj;
		return new EqualsBuilder()
			.append(getAlmacen(),oc.getAlmacen())
			.append(getCreado(),oc.getAlmacen())
			.isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getAlmacen())
		.append(getCreado())
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
