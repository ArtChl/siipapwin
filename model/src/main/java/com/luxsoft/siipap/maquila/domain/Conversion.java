package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.Unidad;
import com.luxsoft.siipap.domain.UnidadesPorArticulo;
import com.luxsoft.utils.domain.PersistentObject;


/**
 * Proporciona el factor de conversion entre una bobina
 * y un producto terminado (Articulo)
 * Es la relación bidireccional many-to-many entre una
 * bobina y un Articulo. Es decir una bobina puede producir
 * n articulos y un Articulo puede ser producido por n bobinas
 * 
 * @author Ruben Cancino
 *
 */
public class Conversion extends PersistentObject{
	
	
	private Bobina bobina;
	private Articulo articulo;
	private BigDecimal factorDeRendimiento;	
	private Unidad origen;
	private Unidad destino;
	
	
	public Conversion(){		
	}

	public Conversion(Articulo articulo, Bobina bobina) {
		this(articulo,bobina,
				articulo.getUnidadDisponible("KGS").getUnidad(),
				articulo.getUnidadDisponible("MIL").getUnidad());
	}
	
	public Conversion(Articulo articulo, Bobina bobina,Unidad origen,Unidad destino) {
		this.articulo = articulo;
		this.bobina = bobina;		
		this.origen=origen;
		this.destino=destino;
		UnidadesPorArticulo ua=getArticulo().getUnidadDisponible(getOrigen().getClave());		
		BigDecimal val=BigDecimal.valueOf(1);		
		setFactorDeRendimiento(val.divide(ua.getFactor(),4,RoundingMode.HALF_EVEN));
	}
	
	/**
	 * Articulo resultante
	 * @return
	 */
	public Articulo getArticulo() {
		return articulo;
	}
	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}
	
	/**
	 * Bobina productora
	 * 
	 * @return
	 */
	public Bobina getBobina() {
		return bobina;
	}
	public void setBobina(Bobina bobina) {
		this.bobina = bobina;
	}
	
	/**
	 * Rendimiento ideal de la bobina en las
	 * unidades del articulo resultante
	 * @return
	 */
	public BigDecimal getFactorDeRendimiento() {
		return factorDeRendimiento;
	}
	
	public void setFactorDeRendimiento(BigDecimal rendimiento) {
		this.factorDeRendimiento = rendimiento;
	}	
	
	public BigDecimal calcularRendimiento(final BigDecimal cantidad){
		Assert.notNull(getOrigen());
		Assert.notNull(getDestino());
		return cantidad.multiply(getFactorDeRendimiento());
	}

	public Unidad getDestino() {
		return destino;
	}

	public void setDestino(Unidad destino) {
		this.destino = destino;
	}

	public Unidad getOrigen() {
		return origen;
	}

	public void setOrigen(Unidad origen) {
		this.origen = origen;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!obj.getClass().isAssignableFrom(obj.getClass())) return false;
		Conversion other=(Conversion)obj;
		return new EqualsBuilder()
			.append(bobina,other.getBobina())
			.append(articulo,other.getArticulo())
			.append(origen,other.getOrigen())
			.append(destino,other.getDestino())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(bobina)
			.append(articulo)
			.append(origen)
			.append(destino)
			.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)
			.append(this.bobina)			
			.append(this.origen)
			.append(this.articulo)
			.append(this.destino)
			.append(this.factorDeRendimiento)
			.toString();
	}	

}
