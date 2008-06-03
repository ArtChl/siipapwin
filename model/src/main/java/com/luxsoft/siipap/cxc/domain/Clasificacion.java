////package com.luxsoft.siipap.ventas.domain;
package com.luxsoft.siipap.cxc.domain;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;

import com.luxsoft.utils.domain.MutableObject;

/**
 * Representa la entidad para clasificar por diversos criterios
 * un cliente
 * 
 * @author Ruben Cancino
 *
 */

@Entity
@Table(name="SW_CLASIFICACION")
public class Clasificacion extends MutableObject{
	
	/**
	 * Es el numero que clasifica al cliente
	 */
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="CLASIFICACION_SEQ")
	@Column(name="CLASIFICACION_ID")
	@Range(min=1,max=99, message="Rango invalido (1 - 99)")
	private Long id;
	
	@Column(name="CLAVE",length=15,nullable=false,unique=true)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	private long clave;
	/**
	 * Es la que describe que tipo de clasificacion se 
	 * le asigno al cliente 
	 */
	@Column(name="DESCRIPCION",length=30,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=10,max=30,message="Longitud incorrecta (10 - 30)")
	private String descripcion;
	
	/**
	 * Es el tipo de Venta (Tipo Factura) que se le 
	 * puede realizar al cliente
	 */
	@Column(name="TIPOFACT",length=15,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=5,max=15,message="Longitud incorrecta (5 - 15)")
	private String tipofact;
	
	@Column(name="COMENTARIOS",length=50,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=10,max=50,message="Longitud incorrecta (10 - 50 )")
	private String comentarios;
	/**
	 * Define si puede realizar venta a este
	 * tipo de clasificacion
	 */
	@Column(name="PERMITIRVENTA",length=1,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=1,max=1,message="Longitud incorrecta (1 - 1)")
	private String permitirVenta;
	
	/**
	 * En una venta  se tabula
	 * el importe maximo de venta de credito
	 * y cuando rebaza este limite, solicita 
	 * autorizacion segun la clasificacion
	 * esta puede aparecer como Si o No
	 * en esta propiedad
	 */
	@Column(name="SOLICITAR_AUTORIZACION",length=1,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=1,max=1,message="Longitud incorrecta (1 - 1)")
	private String solicitarAutorizacion;
	
	
	@Column(name="OBSERVACIONES",length=40,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=10,max=40,message="Longitud incorrecta(10 - 40)")
	private String observaciones;
	
	/**
	 * Esta fecha define  la alta del registro
	 * de la clasificacion correspondiente
	 * 
	 */
	@Column(name="FECHAALTA",length=10,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=8,max=10,message="Longitud de incorrecta (8 - 10)")
	private Date fechaAlta;
	
	/**
	 * Esta fecha define la baja del registro de 
	 * la clasificacion correspondiente
	 * 
	 */
	@Column(name="FECHABAJA",length=10,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=8,max=10,message="Longitud de incorrecta (8 - 10)")
	private Date fechaBaja;

	/**
	 * Esta propiedad define la sucursal
	 * en la cual permitira la venta en 
	 * esta clasificacion para el cliente 
	 * asignado
	 */
	@Column(name="SUCPERMITEVTA",length=10,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	@Length(min=8,max=10,message="Longitud de incorrecta (8 - 10)")
	private String sucPermiteVta;
	
	/**
	 * Fecha de creacion del registro
	 * 
	 */
	@Column(name="CREACION",length=10,nullable=false)
	@NotNull
	@NotEmpty (message="No se permite nulo")
	private Date creacion;
	
	
	


	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	/**
	 * Es el numero que clasifica al cliente
	 */
	
	public long getClave() {
		return clave;
	}



	public void setClave(long clave) {
		this.clave = clave;
	}



	public String getComentarios() {
		return comentarios;
	}



	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}



	public Date getCreacion() {
		return creacion;
	}



	public void setCreacion(Date creacion) {
		this.creacion = creacion;
	}


	/**
	 * Es la que describe que tipo de clasificacion se 
	 * le asigno al cliente 
	 */
	public String getDescripcion() {
		return descripcion;
	}



	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	/**
	 * Esta fecha define  la alta del registro
	 * de la clasificacion correspondiente
	 * 
	 */
	public Date getFechaAlta() {
		return fechaAlta;
	}



	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}


	/**
	 * Esta fecha define la baja del registro de 
	 * la clasificacion correspondiente
	 * 
	 */
	public Date getFechaBaja() {
		return fechaBaja;
	}



	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}


	
	public String getObservaciones() {
		return observaciones;
	}



	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}


	/**
	 * Define si puede realizar venta a este
	 * tipo de clasificacion
	 */
	public String getPermitirVenta() {
		return permitirVenta;
	}



	public void setPermitirVenta(String permitirVenta) {
		this.permitirVenta = permitirVenta;
	}


	/**
	 * En una venta  se tabula
	 * el importe maximo de venta de credito
	 * y cuando rebaza este limite, solicita 
	 * autorizacion segun la clasificacion
	 * esta puede aparecer como Si o No
	 * en esta propiedad
	 */
	public String getSolicitarAutorizacion() {
		return solicitarAutorizacion;
	}



	public void setSolicitarAutorizacion(String solicitarAutorizacion) {
		this.solicitarAutorizacion = solicitarAutorizacion;
	}


	/**
	 * Esta propiedad define la sucursal
	 * en la cual permitira la venta en 
	 * esta clasificacion para el cliente 
	 * asignado
	 */
	public String getSucPermiteVta() {
		return sucPermiteVta;
	}



	public void setSucPermiteVta(String sucPermiteVta) {
		this.sucPermiteVta = sucPermiteVta;
	}


	/**
	 * Es el tipo de Venta (Tipo Factura) que se le 
	 * puede realizar al cliente
	 */
	public String getTipofact() {
		return tipofact;
	}



	public void setTipofact(String tipofact) {
		this.tipofact = tipofact;
	}



	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this)return true;
		Clasificacion d=(Clasificacion)obj;
		return new EqualsBuilder()
		.append(getClave(),d.getFechaAlta())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getClave())
		.append(getFechaAlta())
		.toHashCode();
	}



	

	

	
	

}