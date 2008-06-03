package com.luxsoft.siipap.inventarios2.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.utils.domain.MutableObject;

/**
 * Movimientos de inventario
 * 
 * @author Ruben Cancino
 *
 */
@Entity
@Table (name="SW_MOVI")
public class Movi extends MutableObject{
	
	private Long id;
	
	private int version;
	
	@Length (max=1, min=1) @NotNull	
	private String tipo;
	
	private int sucursal;
	
	private Date fecha=new Date();
	
	private String concepto;
	
	private Articulo articulo;	
	
	private String clave;
	
	private String descripcion;
	
	private BigDecimal kilos;
	
	private BigDecimal cantidad;
		
	private int unixuni;
	
	@Length (max=255)
	private String comentario;	
	
	private String unidad;
	
	private BigDecimal costoP;
	
	private BigDecimal costoU;
	
	private Date creado=new Date();
	
	private Date modificado=new Date();
	
	private int year;
	
	private int mes;	
	
	private Long origen;

	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public BigDecimal getCostoP() {
		return costoP;
	}

	public void setCostoP(BigDecimal costoP) {
		this.costoP = costoP;
	}

	public BigDecimal getCostoU() {
		return costoU;
	}

	public void setCostoU(BigDecimal costoU) {
		this.costoU = costoU;
	}

	public Date getCreado() {
		return creado;
	}

	public void setCreado(Date creado) {
		this.creado = creado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	public BigDecimal getKilos() {
		return kilos;
	}

	public void setKilos(BigDecimal kilos) {
		this.kilos = kilos;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public Date getModificado() {
		return modificado;
	}

	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}

	public Long getOrigen() {
		return origen;
	}

	public void setOrigen(Long origen) {
		this.origen = origen;
	}

	public int getSucursal() {
		return sucursal;
	}

	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public int getUnixuni() {
		return unixuni;
	}

	public void setUnixuni(int unixuni) {
		this.unixuni = unixuni;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Movi other = (Movi) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
	
	public void fijarYearMes(){
		setMes(Periodo.obtenerMes(getFecha()));
		setYear(Periodo.obtenerMes(getFecha()));
	}

}
