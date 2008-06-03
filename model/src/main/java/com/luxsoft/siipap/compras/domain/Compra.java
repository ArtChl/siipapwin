package com.luxsoft.siipap.compras.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Columns;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.utils.domain.MutableObject;


/** 
 * 
 *      
*/
@Entity
@Table(name="SW_COMPRA")
public class Compra extends MutableObject {
    
    @Id
    @GeneratedValue (strategy=GenerationType.AUTO)
    @Column(name="COMPRA_ID")
    private Long id;
    
    @NotNull    
    private Date fecha;
    
    @NotNull
    private Proveedor proveedor;
    
    @NotNull
    private String clave;
    
    @NotNull
    private String nombre;
    
    @Length(max=255)
    private String comentario1;
    
    @Length(max=255)
    private String comentario2;
    
    @NotNull
    private Currency moneda=CantidadMonetaria.PESOS;
    
    @NotNull  
    @Type(type="CantidadMonetariaCompositeUserType")
    @Columns(columns={
    		@Column(name="IMPORTE" ,columnDefinition="default 0"),
    		@Column(name="IMPORTE_MON",length=3,columnDefinition="default 'MXN'")
    })
    @Generated(GenerationTime.INSERT)
    private CantidadMonetaria importe;
    
    
    @NotNull    
    private Estado estado=Estado.Solicitado;
    
    @NotNull
    private Date entrega;
    
    @NotNull
    private Tipo tipo=Tipo.Normal;
    
    @NotNull
    private Date creado;
    
    @NotNull
    @Column(updatable=false,insertable=false)
    @Generated(GenerationTime.ALWAYS)
    private Date modificado;
    
    private int version;
    
    List<CompraUnitaria> partidas=new ArrayList<CompraUnitaria>();
    
    public Compra(Proveedor proveedor) {		
		this.proveedor = proveedor;
		setClave(proveedor.getClave());
		setNombre(proveedor.getNombre());
	}
    
    public Compra() {		
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
    
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	public Date getEntrega() {
		return entrega;
	}
	public void setEntrega(Date entrega) {
		this.entrega = entrega;
	}

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Tipo getTipo() {
		return tipo;
	}
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	
	public List<CompraUnitaria> getPartidas() {
		return partidas;
	}
	public void setPartidas(List<CompraUnitaria> partidas) {
		this.partidas = partidas;
	}
	
	public void agregarPartida(CompraUnitaria comuni){
		comuni.setCompra(this);
		getPartidas().add(comuni);
	}
	
	
	public String getComentario1() {
		return comentario1;
	}
	public void setComentario1(String comentario1) {
		this.comentario1 = comentario1;
	}
	public String getComentario2() {
		return comentario2;
	}
	public void setComentario2(String comentario2) {
		this.comentario2 = comentario2;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
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


	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		this.importe = importe;
	}

	public CantidadMonetaria getIva() {
		return getImporte().multiply(BigDecimal.valueOf(.15));
	}

	public Currency getMoneda() {
		return moneda;
	}
	public void setMoneda(Currency moneda) {
		this.moneda = moneda;
	}

	public CantidadMonetaria getTotal() {
		return getImporte().multiply(1.15);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		Compra c=(Compra)obj;
		return new EqualsBuilder()
		.append(getCreado(),c.getCreado())
		.append(getId(),c.getId())
		.isEquals();
		
		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getCreado())
		.append(getId())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
		.append(getId())
		.append(getClave())
		.append(getNombre())
		.append(getFecha())
		.append(getTotal())
		.toString();
	}

	
	   
    
}
