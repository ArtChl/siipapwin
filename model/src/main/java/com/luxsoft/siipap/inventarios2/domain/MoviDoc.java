package com.luxsoft.siipap.inventarios2.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
//import org.hibernate.validator.Length;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.utils.SystemUtils;
import com.luxsoft.utils.domain.MutableObject;

/**
 * Maestro de movimientos de inventario
 * 
 * @author Ruben Cancino
 *
 */
@Entity 
@Table(name="SW_MOVIDOC")
public class MoviDoc extends MutableObject{
	
	@Id @GeneratedValue (strategy=GenerationType.AUTO)
	private Long id;
	
	@Version
	private int version;
	
	@Column (nullable=false)
	@NotNull
	private Integer sucursal;
	
	@Column (nullable=false)
	private Date fecha=new Date(); 
	
	private Concepto concepto;
	
	@Column (nullable=false) @Length (max=3, min=3)
	private String claveConcepto;
	
	@Length (max=255, message="Tamaño máximo del comentario 255 caracteres")
	private String comentario;
	
	private Set<MoviDet> partidas=new HashSet<MoviDet>();
	
	@Column (nullable=false)
	private Date creado;
	
	@Column (nullable=false,updatable=false,insertable=false)
	@Generated (GenerationTime.ALWAYS)
	private Date modificado;
	
	public MoviDoc(){
		SystemUtils.sleep(55);
		creado=new Date();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public Integer getSucursal() {
		return sucursal;
	}
	public void setSucursal(Integer sucursal) {
		this.sucursal = sucursal;
	}
	
	public String getClaveConcepto() { 
		return claveConcepto;
	}

	public Concepto getConcepto() {
		return concepto;
	}
	public void setConcepto(Concepto concepto) {
		this.concepto = concepto;
	}

	public void setClaveConcepto(String claveConcepto) {
		this.claveConcepto = claveConcepto;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	

	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public Date getCreado() {
		return creado;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(this==obj) return true;
		MoviDoc m=(MoviDoc)obj;
		return new EqualsBuilder()
		.append(getId(), m.getId())
		.append(getCreado(), m.getCreado())
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(15,35)
		.append(getId())
		.append(getCreado())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append(getId())
		.append(getSucursal())
		.append(getFecha())
		.append(getClaveConcepto())
		.toString();
		
	}

	public Set<MoviDet> getPartidas() {		
		return partidas;
	}

	@SuppressWarnings("unused")
	private void setPartidas(Set<MoviDet> partidas) {
		this.partidas = partidas;
	}
	
	public void agregarPartida(final MoviDet det){
		det.setMovidoc(this);
		getPartidas().add(det);
	}
	
	

}
