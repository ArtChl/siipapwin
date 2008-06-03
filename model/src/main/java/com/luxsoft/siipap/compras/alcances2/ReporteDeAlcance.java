package com.luxsoft.siipap.compras.alcances2;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.utils.domain.MutableObject;

/**
 * Modelo de dominio que proporciona y analiza informacion relacionada con la disponibilidad
 * de articulos para su venta
 * 
 * @author Ruben Cancino
 *
 */
public class ReporteDeAlcance extends MutableObject{
	
	//public static final String[] TIPOS={"SULFATADA","SUPER_POLAR"};
	
	public static enum TIPOS{
		SULFATADA,
		SUPER_POLAR
	}
	
	public static enum BOUND_PROPERTIES{
		tipo
		,mesesParaPedido
		,periodoDeVentas
	}
	
	private Long id;
	
	private Date fecha;
	
	private int mesesParaPedido;
	
	private String tipo;	
	
	private Set<AlcanceUnitario> alcances;
	
	private Periodo periodoDeVentas;
	
	private String comentario;
	
	private Date creado;
	
	
	public ReporteDeAlcance(){
		this.tipo=TIPOS.SULFATADA.name();
		this.mesesParaPedido=3;
		this.creado=new Date();
		this.periodoDeVentas=Periodo.getPeriodo(5);
	}

	public Set<AlcanceUnitario> getAlcances() {
		if(alcances==null){
			setAlcances(new HashSet<AlcanceUnitario>());
		}
		return alcances;
	}

	private void setAlcances(Set<AlcanceUnitario> alcances) {
		this.alcances = alcances;
	}
	
	public boolean addAlcance(final AlcanceUnitario a){
		a.setAlcance(this);
		return getAlcances().add(a);
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getMesesParaPedido() {
		return mesesParaPedido;
	}

	public void setMesesParaPedido(int mesesParaPedido) {
		Object old=this.mesesParaPedido;
		this.mesesParaPedido = mesesParaPedido;
		getPropertyChangeSupport().firePropertyChange(BOUND_PROPERTIES.mesesParaPedido.name(),old,mesesParaPedido);
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		Object old=this.tipo;
		this.tipo = tipo;
		//getPropertyChangeSupport().firePropertyChange(BOUND_PROPERTIES.tipo.name(),old,tipo);
		getPropertyChangeSupport().firePropertyChange("tipo",old,tipo);
	}

	public Periodo getPeriodoDeVentas() {
		return periodoDeVentas;
	}

	public void setPeriodoDeVentas(Periodo periodoDeVentas) {
		Object old=this.periodoDeVentas;
		this.periodoDeVentas = periodoDeVentas;
		getPropertyChangeSupport().firePropertyChange(BOUND_PROPERTIES.periodoDeVentas.name(),old,periodoDeVentas);
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		ReporteDeAlcance r=(ReporteDeAlcance)obj;
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
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getId())
		.append(getFecha())
		.append(getComentario())
		.toString();
	}
	

}
