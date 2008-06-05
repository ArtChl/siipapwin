package com.luxsoft.siipap.inventarios.model;

import java.math.BigDecimal;

import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;

/**
 * Permite totalizar los inventarios de maquila
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class TotalizadorDeMaquila extends Model{
	
	
	
	
	private BigDecimal entradasEnKilos=BigDecimal.ZERO;
	private BigDecimal salidasACorteEnKilos=BigDecimal.ZERO;
	private BigDecimal procesoEnKilos=BigDecimal.ZERO;
	private BigDecimal trasladosEnKilos=BigDecimal.ZERO;
	private BigDecimal salidaDirectasEnKilos=BigDecimal.ZERO;
	private BigDecimal inventarioKilos=BigDecimal.ZERO;
	
	private CantidadMonetaria importeEntradasEnKilos=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importeSalidasACorteEnKilos=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importeProceso=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importeTraslados=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importeSalidaDirectasEnKilos=CantidadMonetaria.pesos(0);
	private CantidadMonetaria inventarioFinalEnKilos=CantidadMonetaria.pesos(0);
	
	//private BigDecimal enProceso;
	private BigDecimal totalEntradaDeMillares=BigDecimal.ZERO;
	private BigDecimal totalSalidaDeMillares=BigDecimal.ZERO;
	private BigDecimal inventarioHojas=BigDecimal.ZERO;
	
	private CantidadMonetaria importeEntradaEnMillares=CantidadMonetaria.pesos(0);
	private CantidadMonetaria importeSalidaEnMillares=CantidadMonetaria.pesos(0);
	private CantidadMonetaria inventarioFinalEnHojas=CantidadMonetaria.pesos(0);
	
	private CantidadMonetaria inventario=CantidadMonetaria.pesos(0);
	
	
	
	public TotalizadorDeMaquila(){
		
	}	
	
	
	
	/*** Entrada de Bobinas *****/
	
	public BigDecimal getEntradasEnKilos() {
		return entradasEnKilos;
	}	
	public void setEntradasEnKilos(BigDecimal entradasEnKilos) {
		Object old=this.entradasEnKilos;
		this.entradasEnKilos = entradasEnKilos;
		firePropertyChange("entradasEnKilos", old, entradasEnKilos);
	}
	
	public CantidadMonetaria getImporteEntradasEnKilos() {
		return importeEntradasEnKilos;
	}
	public void setImporteEntradasEnKilos(CantidadMonetaria importeEntradasEnKilos) {
		Object old=this.importeEntradasEnKilos;
		this.importeEntradasEnKilos = importeEntradasEnKilos;
		firePropertyChange("importeEntradasEnKilos", old, importeEntradasEnKilos);
	}
	
	/*** End Entrada de Bobinas *****/
	
	
	public BigDecimal getSalidasACorteEnKilos() {
		return salidasACorteEnKilos;
	}
	public void setSalidasACorteEnKilos(BigDecimal salidasACorteEnKilos) {
		Object old=this.salidasACorteEnKilos;
		this.salidasACorteEnKilos = salidasACorteEnKilos;
		firePropertyChange("salidasACorteEnKilos", old,salidasACorteEnKilos);
	}
	
	public BigDecimal getSalidaDirectasEnKilos() {
		return salidaDirectasEnKilos;
	}
	public void setSalidaDirectasEnKilos(BigDecimal salidaDirectasEnKilos) {
		Object oldValue=this.salidaDirectasEnKilos;
		this.salidaDirectasEnKilos = salidaDirectasEnKilos;
		firePropertyChange("salidaDirectasEnKilos",oldValue,salidaDirectasEnKilos);
	}
	
	public BigDecimal getTrasladosEnKilos() {
		return trasladosEnKilos;
	}
	public void setTrasladosEnKilos(BigDecimal trasladosEnKilos) {
		Object oldValue=this.trasladosEnKilos;
		this.trasladosEnKilos = trasladosEnKilos;
		firePropertyChange("trasladosEnKilos", oldValue, trasladosEnKilos);
	}
	
	public BigDecimal getInventarioKilos() {
		return inventarioKilos;
	}
	public void setInventarioKilos(BigDecimal inventarioKilos) {
		Object oldValue=this.inventarioKilos;
		this.inventarioKilos = inventarioKilos;
		firePropertyChange("inventarioKilos", oldValue, inventarioKilos);
	}
	
	public CantidadMonetaria getImporteSalidasACorteEnKilos() {
		return importeSalidasACorteEnKilos;
	}
	public void setImporteSalidasACorteEnKilos(
			CantidadMonetaria importeSalidasACorteEnKilos) {
		Object old=this.importeSalidasACorteEnKilos;
		this.importeSalidasACorteEnKilos = importeSalidasACorteEnKilos;
		firePropertyChange("importeSalidasACorteEnKilos", old, importeSalidasACorteEnKilos);
	}
	
	public CantidadMonetaria getImporteProceso() {
		return importeProceso;
	}
	public void setImporteProceso(CantidadMonetaria importeProceso) {
		Object oldValue=this.importeProceso;
		this.importeProceso = importeProceso;
		firePropertyChange("importeProceso", oldValue, importeProceso);
	}

	public BigDecimal getProcesoEnKilos() {
		return procesoEnKilos;
	}
	public void setProcesoEnKilos(BigDecimal procesoEnKilos) {
		Object oldValue=this.procesoEnKilos;
		this.procesoEnKilos = procesoEnKilos;
		firePropertyChange("procesoEnKilos", oldValue, procesoEnKilos);
	}
	
	public CantidadMonetaria getImporteSalidaDirectasEnKilos() {
		return importeSalidaDirectasEnKilos;
	}
	public void setImporteSalidaDirectasEnKilos(CantidadMonetaria importeSalidaDirectasEnKilos) {
		Object oldValue=this.importeSalidaDirectasEnKilos;
		this.importeSalidaDirectasEnKilos = importeSalidaDirectasEnKilos;
		firePropertyChange("importeSalidaDirectasEnKilos", oldValue, importeSalidaDirectasEnKilos);
	}
	
	public CantidadMonetaria getImporteTraslados() {
		return importeTraslados;
	}
	public void setImporteTraslados(CantidadMonetaria importeTraslados) {
		Object oldValue=this.importeTraslados;
		this.importeTraslados = importeTraslados;
		firePropertyChange("importeTraslados", oldValue, importeTraslados);
	}
	
	public CantidadMonetaria getInventarioFinalEnKilos() {
		return inventarioFinalEnKilos;
	}
	public void setInventarioFinalEnKilos(CantidadMonetaria inventarioFinalEnKilos) {
		Object oldValue=this.inventarioFinalEnKilos;
		this.inventarioFinalEnKilos = inventarioFinalEnKilos;
		firePropertyChange("inventarioFinalEnKilos", oldValue, inventarioFinalEnKilos);
	}
	
	public BigDecimal getTotalEntradaDeMillares() {
		return totalEntradaDeMillares;
	}
	public void setTotalEntradaDeMillares(BigDecimal totalEntradaDeMillares) {
		Object oldValue=this.totalEntradaDeMillares;
		this.totalEntradaDeMillares = totalEntradaDeMillares;
		firePropertyChange("totalEntradaDeMillares", oldValue, totalEntradaDeMillares);
	}
	
	public BigDecimal getTotalSalidaDeMillares() {
		return totalSalidaDeMillares;
	}
	public void setTotalSalidaDeMillares(BigDecimal totalSalidaDeMillares) {
		Object old=this.totalSalidaDeMillares;
		this.totalSalidaDeMillares = totalSalidaDeMillares;
		firePropertyChange("totalSalidaDeMillares", old, totalSalidaDeMillares);
	}
	
	public BigDecimal getInventarioHojas() {
		return inventarioHojas;
	}
	public void setInventarioHojas(BigDecimal inventarioHojas) {
		Object oldValue=this.inventarioHojas;
		this.inventarioHojas = inventarioHojas;
		firePropertyChange("inventarioHojas", oldValue, inventarioHojas);
	}
	
	public CantidadMonetaria getImporteEntradaEnMillares() {
		return importeEntradaEnMillares;
	}
	public void setImporteEntradaEnMillares(
			CantidadMonetaria importeEntradaEnMillares) {
		Object oldValue=this.importeEntradaEnMillares;
		this.importeEntradaEnMillares = importeEntradaEnMillares;
		firePropertyChange("importeEntradaEnMillares", oldValue, importeEntradaEnMillares);
	}
	
	public CantidadMonetaria getImporteSalidaEnMillares() {
		return importeSalidaEnMillares;
	}
	public void setImporteSalidaEnMillares(CantidadMonetaria importeSalidaEnMillares) {
		Object old=this.importeSalidaEnMillares;
		this.importeSalidaEnMillares = importeSalidaEnMillares;
		firePropertyChange("importeSalidaEnMillares", old, importeSalidaEnMillares);
	}
	
	public CantidadMonetaria getInventarioFinalEnHojas() {
		return inventarioFinalEnHojas;
	}
	public void setInventarioFinalEnHojas(CantidadMonetaria inventarioFinalEnHojas) {
		Object old=this.inventarioFinalEnHojas;
		this.inventarioFinalEnHojas = inventarioFinalEnHojas;
		firePropertyChange("inventarioFinalEnHojas", old,inventarioFinalEnHojas);
	}
	
	public CantidadMonetaria getInventario() {
		return inventario;
	}
	public void setInventario(CantidadMonetaria inventario) {
		Object oldValue=this.inventario;
		this.inventario = inventario;
		firePropertyChange("inventario", oldValue, inventario);
	}
	
	public void recalcularTotales(){
		calcularTotalesDeBobinas();
		calcularTotalseDeCortado();
		setInventario(getInventarioFinalEnKilos()
				.add(getInventarioFinalEnHojas()));
	}
	
	public void calcularEntradasDeBobinas(final EventList<EntradaDeMaterial> entradas){
		BigDecimal kilos=BigDecimal.ZERO;
		CantidadMonetaria pesos=CantidadMonetaria.pesos(0);
		
		this.importeEntradasEnKilos=CantidadMonetaria.pesos(0);
		if(entradas!=null){
			for(EntradaDeMaterial em:entradas){
				kilos=kilos.add(em.getKilos().abs());
				pesos=pesos.add(em.getImporte().abs());
			}
		}
		setEntradasEnKilos(kilos);
		setImporteEntradasEnKilos(pesos);
	}
	
	private void calcularTotalesDeBobinas(){
		
		setInventarioKilos(
				getEntradasEnKilos()
				.add(getSalidasACorteEnKilos())
				//.add(getProcesoEnKilos().abs())
				.add(getTrasladosEnKilos())
				.add(getSalidaDirectasEnKilos())
				);
		setInventarioFinalEnKilos(
				getImporteEntradasEnKilos()
				.add(getImporteSalidasACorteEnKilos())
				//.add(getImporteProceso().abs())
				.add(getImporteTraslados())
				.add(getImporteSalidaDirectasEnKilos())
				);
	}
	
	private void calcularTotalseDeCortado(){
		setInventarioHojas(getTotalEntradaDeMillares().subtract(getTotalSalidaDeMillares()));
		setInventarioFinalEnHojas(getImporteEntradaEnMillares().subtract(getImporteSalidaEnMillares()));
	}
	
	
	
	

}
