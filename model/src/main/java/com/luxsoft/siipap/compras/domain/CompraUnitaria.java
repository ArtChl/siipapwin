package com.luxsoft.siipap.compras.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.ArticuloPorProveedor;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Unidad;
import com.luxsoft.utils.domain.MutableObject;


/**
 * 
 * @author Ruben Cancino
 *
 */
public class CompraUnitaria extends MutableObject {

	private Long id;
    
    private Compra compra;
    
    @NotNull
    private ArticuloPorProveedor artxProveedor;
    
    
    
    @NotNull
    private String clave;
    
    @NotNull
    @Length(max=60)
    private String descripcion;
    
   
    private Unidad unidad;
    
    @NotNull
    private BigDecimal cantidad;
    
    @NotNull
    private BigDecimal recibido;
    
    @NotNull
    private BigDecimal devueltos;
    
    private Date ultimaEntrada;
    
    @Length (max=255)
    private String comentario;
    
    private Estado estado=Estado.Solicitado;
    
    @NotNull
    private CantidadMonetaria precioLista;
    
    @NotNull
    private double descuento;
    
    
    private BigDecimal importe=BigDecimal.ZERO;
    
    
    private Date creado;
    
    private Date modificado;
    
    private int version;
    
    public CompraUnitaria(){
    	
    }
    
    
	
    public CompraUnitaria(ArticuloPorProveedor artxProveedor) {		
		this.artxProveedor = artxProveedor;
		setClave(artxProveedor.getArticulo().getClave());
		setDescripcion(artxProveedor.getArticulo().getDescripcion1());
	}	

	public ArticuloPorProveedor getArtxProveedor() {
		return artxProveedor;
	}

	public void setArtxProveedor(ArticuloPorProveedor artxProveedor) {
		this.artxProveedor = artxProveedor;
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

	public Compra getCompra() {
		return compra;
	}

	public void setCompra(Compra compra) {
		this.compra = compra;
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

	public BigDecimal getDevueltos() {
		return devueltos;
	}

	public void setDevueltos(BigDecimal devueltos) {
		this.devueltos = devueltos;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getModificado() {
		return modificado;
	}

	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}

	public BigDecimal getRecibido() {
		return recibido;
	}

	public void setRecibido(BigDecimal recibido) {
		this.recibido = recibido;
	}

	public Date getUltimaEntrada() {
		return ultimaEntrada;
	}

	public void setUltimaEntrada(Date ultimaEntrada) {
		this.ultimaEntrada = ultimaEntrada;
	}

	public Unidad getUnidad() {
		return unidad;
	}

	public void setUnidad(Unidad unidad) {
		this.unidad = unidad;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public CantidadMonetaria getPrecioLista() {
		return precioLista;
	}
	public void setPrecioLista(CantidadMonetaria precioLista) {
		this.precioLista = precioLista;
	}
	
	public Articulo getArticulo(){
		return getArtxProveedor().getArticulo();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		CompraUnitaria c=(CompraUnitaria)obj;
		return new EqualsBuilder()
		.append(getCompra(),c.getCompra())
		.append(getArticulo(),c.getArticulo())
		.isEquals();
		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getCompra())
		.append(getArticulo())
		.toHashCode();
	}
    
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getCompra())
		.append(getArticulo())
		.append(getRecibido())
		.toString();
	}

	
	    
    
}
