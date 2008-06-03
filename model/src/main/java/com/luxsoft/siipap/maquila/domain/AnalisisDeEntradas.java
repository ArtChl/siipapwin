package com.luxsoft.siipap.maquila.domain;

import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.utils.domain.PersistentObject;

/**
 * Modelo conceptual para representar las facturas de Impap a Papel correspondientes
 * a entradas de material en almacenes de los maquiladores.
 * Todas las entradas de material (Recepciones de material) deben tener un AnalisisDeEntradas referenciado
 * para que su costo sea definitivo y correcto. 
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeEntradas extends PersistentObject{
	
	
	/**
	 * Proveedor de la factura. DEBE ser Impap
	 */
	private Proveedor proveedor;

	/**
	 * Numero de la factura
	 */
	private String factura;
	
	/**
	 * Fecha de la factura
	 */
	private Date fecha=new Date();
	
	/**
	 * Moneda de la factura
	 */
	private Currency moneda=CantidadMonetaria.PESOS;
	
	/**
	 * Tipo de cambio 
	 */
	private double tc=1.0;
	
	/**
	 * Importe de las entradas analizadas.
	 * 
	 */
	private CantidadMonetaria importeAnalizado;
	
	/**
	 * Impuesto de las entradas analizadas
	 */
	private CantidadMonetaria impuestoAnalizado;
	
	/**
	 * Total de las entradas analizadas
	 */
	private CantidadMonetaria totalAnalizado;
	
	/**
	 * Importe de la factura
	 */
	private CantidadMonetaria importef;
	
	/**
	 * Impuesto de la factura
	 */
	private CantidadMonetaria impuestof;
	
	/**
	 * Total de la factura
	 */
	private CantidadMonetaria totalf;

	private Set<EntradaDeMaterial> entradas;
	
	private int version;
	
	private Date creado=new Date();
	
	public AnalisisDeEntradas(){
		
	}

	public String getFactura() {
		return factura;
	}

	public void setFactura(String factura) {
		this.factura = factura;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public CantidadMonetaria getImporteAnalizado() {
		return importeAnalizado;
	}

	public void setImporteAnalizado(CantidadMonetaria importeAnalizado) {
		Object old=this.importeAnalizado;
		this.importeAnalizado = importeAnalizado;
		getPropertyChangeSupport().firePropertyChange("importeAnalizado",old,importeAnalizado);
	}

	public CantidadMonetaria getImportef() {
		return importef;
	}

	public void setImportef(CantidadMonetaria importef) {
		Object old=this.impuestof;		
		this.importef = importef;
		getPropertyChangeSupport().firePropertyChange("importef",old,importef);
	}

	public CantidadMonetaria getImpuestoAnalizado() {
		return impuestoAnalizado;
	}

	public void setImpuestoAnalizado(CantidadMonetaria impuestoAnalizado) {
		Object old=this.importeAnalizado;
		this.impuestoAnalizado = impuestoAnalizado;
		getPropertyChangeSupport().firePropertyChange("impuestoAnalizado",old,impuestoAnalizado);
	}

	public CantidadMonetaria getImpuestof() {
		return impuestof;
	}

	public void setImpuestof(CantidadMonetaria impuestof) {
		Object old=this.importef;
		this.impuestof = impuestof;
		getPropertyChangeSupport().firePropertyChange("importef",old,importef);
	}

	public Currency getMoneda() {
		return moneda;
	}

	public void setMoneda(Currency moneda) {
		this.moneda = moneda;
	}

	

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public double getTc() {
		return tc;
	}

	public void setTc(double tc) {
		this.tc = tc;
	}

	public CantidadMonetaria getTotalAnalizado() {
		return totalAnalizado;
	}

	public void setTotalAnalizado(CantidadMonetaria totalAnalizado) {
		Object old=this.totalAnalizado;
		this.totalAnalizado = totalAnalizado;
		getPropertyChangeSupport().firePropertyChange("totalAnalizado",old,totalAnalizado);
	}

	public CantidadMonetaria getTotalf() {
		return totalf;
	}

	public void setTotalf(CantidadMonetaria totalf) {
		Object old=this.totalf;
		this.totalf = totalf;
		getPropertyChangeSupport().firePropertyChange("totalf",old,totalf);
	}
	
	

	public Set<EntradaDeMaterial> getEntradas() {
		if(entradas==null){
			entradas=new HashSet<EntradaDeMaterial>();
		}
		return entradas;
	}

	public void setEntradas(Set<EntradaDeMaterial> entradas) {
		this.entradas = entradas;
	}
	
	public boolean addEntrada(final EntradaDeMaterial entrada){		
		boolean res=getEntradas().add(entrada);
		if(res){
			entrada.setAnalisis(this);
		}
		return res;
		
	}
	
	public boolean removeEntrada(EntradaDeMaterial e){
		boolean res=getEntradas().remove(e);
		if(res){
			e.setAnalisis(null);
		}
		return res;
		
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		AnalisisDeEntradas a=(AnalisisDeEntradas)obj;
		return getFactura().equals(a.getFactura());
	}

	@Override
	public int hashCode() {
		return getFactura().hashCode();
	}
	
	public CantidadMonetaria recalcular(){
		CantidadMonetaria i=null;
		for(EntradaDeMaterial e:getEntradas()){
			if(i==null ){
				i=e.getImporte();
				continue;
			}
			i=i.add(e.getImporte());
		}
		if(i!=null){
			setImporteAnalizado(i);
			setImpuestoAnalizado(MonedasUtils.calcularImpuesto(i));
			setTotalAnalizado(MonedasUtils.calcularTotal(i));
		}
		return i;
	}
	
	public void recalcularFactura(){
		setImportef(getImporteAnalizado());
		setImpuestof(getImpuestoAnalizado());
		setTotalf(getTotalAnalizado());
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

	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	public void removerEntradas(){
		for(EntradaDeMaterial e:getEntradas()){
			e.setAnalisis(null);			
		}
		getEntradas().clear();
	}
	

}
