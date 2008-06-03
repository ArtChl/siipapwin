package com.luxsoft.siipap.inventarios2.domain;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.utils.domain.MutableObject;

public class MoviDet extends MutableObject{
	
	private Long id;
	
	private int version;	
	
	private MoviDoc movidoc;
	
	@NotNull
	private Articulo articulo;
	
	@Length (max=10)
	private String clave;
	
	@NotNull
	private int unixuni;
	
	private BigDecimal cantidad;
	
	private Movi movi;
	
	
	public Movi getMovi() {
		return movi;
	}
	public void setMovi(Movi movi) {
		this.movi = movi;
	}

	public MoviDoc getMovidoc() {
		return movidoc;
	}

	public void setMovidoc(MoviDoc movidoc) {
		this.movidoc = movidoc;
	}

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((articulo == null) ? 0 : articulo.hashCode());
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
		final MoviDet other = (MoviDet) obj;
		if (articulo == null) {
			if (other.articulo != null)
				return false;
		} else if (!articulo.equals(other.articulo))
			return false;
		return true;
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getClave())
		.append(getCantidad())
		.toString();
	}

	
	

}
