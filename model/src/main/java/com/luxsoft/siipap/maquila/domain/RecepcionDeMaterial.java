package com.luxsoft.siipap.maquila.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.utils.domain.PersistentObject;

/**
 * Esta es la entidad que representa el documento de Recepcion de Material 
 * La RecepcionDeMaterial agrupa una seria de entradas unitarias todas
 * dirigidas al mismo almacen del maquilador
 * Mantiene una relacion BIDIRECCIONAL 1-many con EntradaDeMaterial en lo que se conoce
 * com relacion Padre-Hijo
 * 
 * @author Ruben Cancino
 *
 */
public class RecepcionDeMaterial extends PersistentObject{
	
	private Almacen almacen;
	private String entradaDeMaquilador;
	private Date fecha=Calendar.getInstance().getTime();
	private String observaciones;	
	private Set<EntradaDeMaterial> entradas=new HashSet<EntradaDeMaterial>();
		
	
	
	public String getEntradaDeMaquilador() {
		return entradaDeMaquilador;
	}
	public void setEntradaDeMaquilador(String entradaDeMaquilador) {
		this.entradaDeMaquilador = entradaDeMaquilador;
	}
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
	
	
	public Set<EntradaDeMaterial> getEntradas() {
		return entradas;
	}
	public void setEntradas(Set<EntradaDeMaterial> entradas) {
		this.entradas = entradas;
	}
	
	public EntradaDeMaterial add(final EntradaDeMaterial e){
		e.setRecepcion(this);
		getEntradas().add(e);
		return e;
	}
	
	public boolean tieneCortes(){
		return CollectionUtils.exists(getEntradas(),new Predicate(){
			public boolean evaluate(Object object) {
				EntradaDeMaterial em=(EntradaDeMaterial)object;
				return em.isCortado();
			}
		});
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
			.append(getAlmacen().getNombre())
			.append(getFecha())
			.append(getId())
			.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		RecepcionDeMaterial r=(RecepcionDeMaterial)obj;		
		return new EqualsBuilder()
		.append(getId(),r.getId())
		.isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
			.append(getId())
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
