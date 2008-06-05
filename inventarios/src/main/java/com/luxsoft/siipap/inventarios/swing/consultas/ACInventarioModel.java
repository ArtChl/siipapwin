package com.luxsoft.siipap.inventarios.swing.consultas;

import java.math.BigDecimal;
import java.util.List;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;


import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.inventarios.domain.InventarioMensual;
import com.luxsoft.siipap.services.ServiceLocator;


public class ACInventarioModel extends PresentationModel{
	
	private final EventList<InventarioMensual> inventario;
	
	//private FilterList<InventarioMensual> inventarioFiltrado;
	private EventList<InventarioMensual> inventarioFiltrado;
	
	
	private BigDecimal invInicial=BigDecimal.ZERO;
	private BigDecimal totalCxp=BigDecimal.ZERO;
	private BigDecimal totalMaq=BigDecimal.ZERO;
	private BigDecimal gastosMaq=BigDecimal.ZERO;
	private BigDecimal comsSA=BigDecimal.ZERO;
	private BigDecimal invFinal=BigDecimal.ZERO;
	private BigDecimal costoVentasCalculado=BigDecimal.ZERO;
	private BigDecimal costoVentasReal=BigDecimal.ZERO;
	
	public ACInventarioModel(){
		super(null);
		setBean(this);
		inventario=GlazedLists.threadSafeList(new BasicEventList<InventarioMensual>());
		
	}
	
	public EventList<InventarioMensual> getInventario(){
		return inventario;
	}
	
	public void load(){
		try {
			inventario.clear();
			List<InventarioMensual> iv=getPorArticulo();
			System.out.println("Registros de inventario: "+iv.size());
			inventario.addAll(iv);
			updateTotales();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private List<InventarioMensual> getPorArticulo(){
		return ServiceLocator.getInventariosManager().inventarioCosteado();
	}
	
	public void close(){
		inventario.clear();
		//inventario=null;
	}
	
	public void registerInventarioFiltrado(final EventList<InventarioMensual> filterList){
		this.inventarioFiltrado=filterList;
	}
	
	public BigDecimal getTotalCxp() {
		return totalCxp;
	}
	public void setTotalCxp(BigDecimal totalCxp) {
		Object old=this.totalCxp;
		this.totalCxp = totalCxp;
		firePropertyChange("totalCxp",old,totalCxp);
	}
	
	public void updateTotales(){
		if(inventarioFiltrado!=null){
			CantidadMonetaria invIni=CantidadMonetaria.pesos(0);
			CantidadMonetaria cxp=CantidadMonetaria.pesos(0);
			CantidadMonetaria maq=CantidadMonetaria.pesos(0);
			CantidadMonetaria gas=CantidadMonetaria.pesos(0);
			CantidadMonetaria coms=CantidadMonetaria.pesos(0);
			CantidadMonetaria invFin=CantidadMonetaria.pesos(0);
			CantidadMonetaria cv1=CantidadMonetaria.pesos(0);
			BigDecimal cv2=BigDecimal.ZERO;
			for(InventarioMensual im:inventarioFiltrado){
				invIni=invIni.add(im.getCostoInicial());
				
				cxp=cxp.add(im.getCostoCxp());
				maq=maq.add(im.getCostoMaq());
				gas=gas.add(im.getGastosMaq());
				coms=coms.add(im.getComsSA());
				invFin=invFin.add(im.getCosto());
				cv1=cv1.add(im.getCostoDeVentas());
				cv2=cv2.add(im.getCostoDeVentasF());
			}
			setInvInicial(invIni.amount());
			setTotalCxp(cxp.amount());
			setTotalMaq(maq.amount());
			setGastosMaq(gas.amount());
			setComsSA(coms.amount());
			setInvFinal(invFin.amount());
			setCostoVentasCalculado(cv1.amount());
			setCostoVentasReal(cv2);
		}
	}
	

	public BigDecimal getCostoVentasCalculado() {
		return costoVentasCalculado;
	}
	public void setCostoVentasCalculado(BigDecimal costoVentasCalculado) {
		Object old=this.costoVentasCalculado;
		this.costoVentasCalculado = costoVentasCalculado;
		firePropertyChange("costoVentasCalculado", old, costoVentasCalculado);
	}

	public BigDecimal getCostoVentasReal() {
		return costoVentasReal;
	}
	public void setCostoVentasReal(BigDecimal costoVentasReal) {
		Object old=this.costoVentasReal;
		this.costoVentasReal = costoVentasReal;
		firePropertyChange("costoVentasReal", old, costoVentasReal);
	}

	public BigDecimal getGastosMaq() {
		return gastosMaq;
	}
	public void setGastosMaq(BigDecimal gastosMaq) {
		Object old=this.gastosMaq;
		this.gastosMaq = gastosMaq;
		firePropertyChange("gastosMaq", old, gastosMaq);
	}

	public BigDecimal getInvFinal() {
		return invFinal;
	}
	public void setInvFinal(BigDecimal invFinal) {
		Object old=this.invFinal;
		this.invFinal = invFinal;
		firePropertyChange("invFinal", old, invFinal);
	}

	public BigDecimal getInvInicial() {
		return invInicial;
	}
	public void setInvInicial(BigDecimal invInicial) {
		Object old=this.invInicial;
		this.invInicial = invInicial;
		firePropertyChange("invInicial", old, invInicial);
	}
	
	public BigDecimal getTotalMaq() {
		return totalMaq;
	}
	public void setTotalMaq(BigDecimal totalMaq) {
		Object old=this.totalMaq;
		this.totalMaq = totalMaq;
		firePropertyChange("totalMaq", old, totalMaq);
	}

	public BigDecimal getComsSA() {
		return comsSA;
	}
	public void setComsSA(BigDecimal comsSA) {
		Object old=this.comsSA;
		this.comsSA = comsSA;
		firePropertyChange("comsSA", old, comsSA);
	}
	
	
}
