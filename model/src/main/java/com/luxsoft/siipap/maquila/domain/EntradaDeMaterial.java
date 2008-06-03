package com.luxsoft.siipap.maquila.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;


/**
 * Es la entidad que representa las entradas de material 
 * al inventario de maquila, Originalmente estan dadas por bobinas
 * Mantiene una relacion bi-direccional many-to-one con RecepcionDeMaterial
 * en lo que es una clasica relacion Padre/Hijo.
 * 
 * @author Ruben Cancino
 *
 */
public class EntradaDeMaterial extends MovimientoDeMaterial{
	
	
	
	private String entradaDeMaquilador;
	private String factura;
	private String fabricante;	
	private RecepcionDeMaterial recepcion;
	private SalidaDeMaterial trasladoOrigen;	
	
	private int bobinas=0;
	private Set<SalidaACorte> cortes=new HashSet<SalidaACorte>();
	
	private BigDecimal disponibleKilos=BigDecimal.ZERO;
	
	private BigDecimal disponibleEnM2=BigDecimal.ZERO;
	
	/**
	 * Analisis de esta entrada. Es actualizado cuando esta entrada ha sido analizada
	 *  
	 */
	private AnalisisDeEntradas analisis;
		
	public String getEntradaDeMaquilador() {
		return entradaDeMaquilador;
	}

	public void setEntradaDeMaquilador(String entrada) {
		this.entradaDeMaquilador = entrada;
	}

	public String getFabricante() {
		return fabricante;
	}

	public void setFabricante(String fabricante) {
		this.fabricante = fabricante;
	}

	public String getFactura() {
		return factura;
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}	
	
	public RecepcionDeMaterial getRecepcion() {
		return recepcion;
	}

	public void setRecepcion(RecepcionDeMaterial recepcion) {
		this.recepcion = recepcion;
	}
	
	public int getBobinas() {
		return bobinas;
	}

	public void setBobinas(int bobinas) {
		this.bobinas = bobinas;
	}	
	

	public BigDecimal getDisponibleEnM2() {
		return disponibleEnM2;
	}

	public void setDisponibleEnM2(BigDecimal disponibleEnM2) {
		Object old=this.disponibleEnM2;
		this.disponibleEnM2 = disponibleEnM2;
		getPropertyChangeSupport().firePropertyChange("disponibleEnM2",old,disponibleEnM2);
	}

	public BigDecimal getDisponibleKilos() {
		return disponibleKilos;
	}

	public void setDisponibleKilos(BigDecimal disponibleKilos) {
		Object old=this.disponibleKilos;
		this.disponibleKilos = disponibleKilos;
		getPropertyChangeSupport().firePropertyChange("disponibleKilos",old,disponibleKilos);
	}

	public Set<SalidaACorte> getCortes() {
		return cortes;
	}

	public void setCortes(Set<SalidaACorte> cortes) {
		this.cortes = cortes;
	}
	
	public void agregarCorte(SalidaACorte corte){
		corte.setEntrada(this);
		cortes.add(corte);
	}
	
	/**
	 * Verifica si esta clase tiene ordenes de corte asociados
	 * es decir maquilado
	 * 
	 * @return
	 */
	public boolean isCortado(){
		return !getCortes().isEmpty();
	}
	
		
	public void recalcularSaldos(){
		BigDecimal m2=BigDecimal.ZERO;
		for(SalidaACorte c:getCortes()){
			if(c.getMetros2()==null)continue;
			BigDecimal cm2=c.getMetros2();
			m2=m2.add(cm2,MathContext.DECIMAL32);
		}
		Assert.isTrue(m2.compareTo(getMetros2())<=0,"Las salidas a corte no pueden superar los m2 de la entrada");
		setDisponibleEnM2(getMetros2().subtract(m2,MathContext.DECIMAL32));
		BigDecimal kilos=m2.multiply(getFactorDeConversion());
		setDisponibleKilos(getKilos().subtract(kilos,MathContext.DECIMAL32));
		
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false;
		if(obj==this) return true;
		EntradaDeMaterial e=(EntradaDeMaterial)obj;
		return new EqualsBuilder()
		.append(getRecepcion(),e.getRecepcion())
		.append(getArticulo(),e.getArticulo())
		.append(getEntradaDeMaquilador(),e.getEntradaDeMaquilador())
		.append(getFactura(),e.getFactura())		
		.append(getCreado(),e.getCreado())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getRecepcion())
		.append(getArticulo())
		.append(getEntradaDeMaquilador())
		.append(getFactura())
		.append(getCreado())
		.toHashCode();
	}
	
	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SIMPLE_STYLE)			
			.append(getEntradaDeMaquilador())
			.append(" Bobina:")
			.append(getArticulo().getClave())
			.append(" ")
			.append(getArticulo().getDescripcion1())
			//.append(getAlmacen())
			.toString();
	}

	public SalidaDeMaterial getTrasladoOrigen() {
		return trasladoOrigen;
	}

	public void setTrasladoOrigen(SalidaDeMaterial trasladoOrigen) {
		this.trasladoOrigen = trasladoOrigen;
	}

	public AnalisisDeEntradas getAnalisis() {
		return analisis;
	}

	public void setAnalisis(AnalisisDeEntradas analisis) {
		this.analisis = analisis;
	}

	
		
	
	
	

}
