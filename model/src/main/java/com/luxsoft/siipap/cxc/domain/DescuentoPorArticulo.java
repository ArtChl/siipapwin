package com.luxsoft.siipap.cxc.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Familia;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

/**
 * Descuento por Cliente y articulo para ser aplicado a productos
 * especificos.
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentoPorArticulo extends AbstractDescuento{
	
	/**
	 * Familia del articulo
	 */
	private Familia familia;
	
	private String claveFamilia;
	
	/**
	 * Articulo al que aplica
	 */
	private Articulo articulo;
	
	/**
	 * Clave del articulo
	 */
	private String claveArticulo;
	
	/**
	 * Por compatibilidad con SIIPAP
	 */
	private CantidadMonetaria precioKilo;
	
	/**
	 * Gramos maximos. Por compatibilidad con SIIPAP
	 * TEMPORAL
	 */
	private double gramMax;
	
	/**
	 * Gramos minimos. Por compatibilidad con SIIPAP
	 * TEMPORAL
	 */
	private double gramMin;
	
	private boolean porPrecioKilo=false;
	
	private CantidadMonetaria costo=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria ultimoCosto=CantidadMonetaria.pesos(0);
	
	public Articulo getArticulo() {
		return articulo;
	}
	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}
	
	public Familia getFamilia() {
		return familia;
	}
	public void setFamilia(Familia familia) {
		this.familia = familia;
	}
	public CantidadMonetaria getPrecioKilo() {
		return precioKilo;
	}
	public void setPrecioKilo(CantidadMonetaria precioKilo) {
		Object old=this.precioKilo;
		this.precioKilo = precioKilo;
		getPropertyChangeSupport().firePropertyChange("precioKilo", old, precioKilo);
	}
	
	
	
	
	public double getGramMax() {
		return gramMax;
	}
	public void setGramMax(double gramMax) {
		this.gramMax = gramMax;
	}
	public double getGramMin() {
		return gramMin;
	}
	public void setGramMin(double gramMin) {
		this.gramMin = gramMin;
	}
	public String getClaveFamilia() {
		return claveFamilia;
	}
	public void setClaveFamilia(String claveFamilia) {
		this.claveFamilia = claveFamilia;
	}
	public String getClaveArticulo() {
		return claveArticulo;
	}
	public void setClaveArticulo(String claveArticulo) {
		this.claveArticulo = claveArticulo;
	}
	
	public CantidadMonetaria calcularDescuento(final Venta v){
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		for(VentaDet det:v.getPartidas()){
			if(getClaveArticulo().equals(det.getClave())){
				//Aplicamos el descuento
				CantidadMonetaria suma=calcularDescuento(det);
				importe=importe.add(suma);
			}
		}
		return importe;
	}
	
	public CantidadMonetaria calcularDescuento(final VentaDet det){
		Assert.isTrue(getClave().equals(det.getVenta().getClave()),"No es el mismo cliente");
		if( (getClaveArticulo().equals(det.getClave())) && isActivo()){
			if(getTipoFac().equals(det.getTipoFactura())){
				double desc=getDescuento()/100;				
				CantidadMonetaria imp=new CantidadMonetaria(det.getImporteBruto(),det.getVenta().getImporteBruto().currency());
				imp=imp.multiply(desc);
				if(getAdicional()!=0){
					CantidadMonetaria ii=new CantidadMonetaria(det.getImporteBruto(),det.getVenta().getImporteBruto().currency());
					//CantidadMonetaria ii=CantidadMonetaria.pesos(det.getImporteBruto());
					CantidadMonetaria nvoImporte=ii.subtract(imp);
					CantidadMonetaria adi=nvoImporte.multiply(getAdicional());
					imp=imp.add(adi);
				}
				return imp;
			}else
				return new CantidadMonetaria(0,det.getVenta().getImporteBruto().currency());
			
		}
		return new CantidadMonetaria(0,det.getVenta().getImporteBruto().currency());
	}
	
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("Cliente: ",getClave())
		.append(getDescuento())
		.append(getAdicional())
		.append(isActivo())				
		.append(getPrecioKilo())
		.append(getGramMin())
		.append(getGramMax())
		.append("PrecioNeto:",isPrecioNeto())
		.append("Tipos: ",getTipoFac())
		.toString();
		
	}
	
	
	
	
	public void calcularDescuentoEnFuncionDePrecioKilo(){
		final CantidadMonetaria pk=getPrecioKilo();
		if(pk==null || pk.amount().doubleValue()==0)
			setDescuento(0);
		else{
			//Obtenemos el precio kilo de lista
			CantidadMonetaria pl=getArticulo().getPrecioKiloCred();
			if(pl==null || pl.amount().doubleValue()==0)
				setDescuento(0);
			else{
				double val=100-((pk.amount().doubleValue()*100)/pl.amount().doubleValue());
				setDescuento(val);
			}
		}
	}
	
	public void calcularPrecioKiloEnFuncionDeDescuento(){
		if(getDescuento()==0)
			setPrecioKilo(CantidadMonetaria.pesos(0));
		else{
			CantidadMonetaria val=getArticulo().getPrecioKiloCred();
			CantidadMonetaria desc=val.multiply(getDescuento()/100);
			setPrecioKilo(val.subtract(desc));
		}
	}
	public boolean isPorPrecioKilo() {
		return porPrecioKilo;
	}
	public void setPorPrecioKilo(boolean porPrecioKilo) {
		boolean old=this.porPrecioKilo;
		this.porPrecioKilo = porPrecioKilo;
		getPropertyChangeSupport().firePropertyChange("porPrecioKilo", old, porPrecioKilo);
	}
	public CantidadMonetaria getCosto() {
		return costo;
	}
	public void setCosto(CantidadMonetaria costo) {
		this.costo = costo;
	}
	public CantidadMonetaria getUltimoCosto() {
		return ultimoCosto;
	}
	public void setUltimoCosto(CantidadMonetaria ultimoCosto) {
		this.ultimoCosto = ultimoCosto;
	}
	
	public CantidadMonetaria getPrecioPorMillar(){
		if(getPrecioKilo()==null) return CantidadMonetaria.pesos(0);
		return getPrecioKilo().multiply(getArticulo().getKilos());
	}
	
	public double getMargen(){
		CantidadMonetaria utilidad=getPrecioPorMillar().subtract(getCosto());		
		return (utilidad.amount().doubleValue()*100)/getCosto().amount().doubleValue();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getId())
		.toHashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)	return true;
		if(obj==null) return false;
		if (getClass() != obj.getClass())return false;
		final DescuentoPorArticulo other = (DescuentoPorArticulo) obj;
		return new EqualsBuilder()
		.append(getId(), other.getId())
		.isEquals();
	}
	
	

}
