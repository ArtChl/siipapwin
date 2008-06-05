package com.luxsoft.siipap.cxc.nc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.matchers.Matcher;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Modelo para controlar el estado y comportamiento del proceso de generacion de notas de credito
 * por descuento
 * 
 * @author Ruben Cancino
 *
 */
public class NCDescuentoFormModel extends Model{
	
	private Date fecha=new Date();
	private boolean condonar=true;
	private final Cliente cliente;
	
	private EventList<Venta> ventas;
	private EventList<NotasDeCreditoDet> notasDet;
	private PresentationModel pmodel;
	private boolean anticipada=false;
	
	public NCDescuentoFormModel(final Cliente c,final List<Venta> vts){
		this(c,vts,false);
	}
	
	public NCDescuentoFormModel(final Cliente c,final List<Venta> vts,final boolean anticipada){
		Assert.notEmpty(vts,"La seleccion de ventas esta vacia");		
		this.cliente=c;
		this.anticipada=anticipada;
		Matcher<Venta> matcher=anticipada?new ParaDescuentoAnticipadoMatcher():new ParaDescuentoMatcher();
		this.ventas=GlazedLists.eventList(vts);
		this.ventas=new FilterList<Venta>(this.ventas,matcher);
		crearNotasAPartirDeVentas(this.ventas);
		actualizarPartidas();
		NotasUtils.validarMismoCliente(notasDet);
		addPropertyChangeListener("condonar", new CondonarHandler());
	}
	
	public void addVentas(final List<Venta> ventas){
		ventas.addAll(ventas);
	}
	
	public boolean isCondonar() {
		return condonar;
	}
	public void setCondonar(boolean condonar) {
		boolean old=this.condonar;
		this.condonar = condonar;
		firePropertyChange("condonar", old, condonar);
	}

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		Object old=this.fecha;
		this.fecha = fecha;
		firePropertyChange("fecha", old, fecha);
	}
	
	public Cliente getCliente() {
		return cliente;
	}
	
	public EventList<NotasDeCreditoDet> getPartidas(){
		return this.notasDet;
	}
	
	/** ValueModels **/
	
	public ValueModel getModel(String property){
		if(pmodel==null){
			pmodel=new PresentationModel(this);			
		}
		return pmodel.getComponentModel(property);
	}
	
	
	
	/**
	 * Crear las notasdet  a partir de las ventas
	 * 
	 * 
	 * @param vts
	 */
	private void crearNotasAPartirDeVentas(final List<Venta> vts){			
		notasDet=new ObservableElementList<NotasDeCreditoDet>(new BasicEventList<NotasDeCreditoDet>(),GlazedLists.beanConnector(NotasDeCreditoDet.class));			
		for(Venta v:vts){
			NotasDeCreditoDet det=NotasUtils.getNotaDet(v);
			det.setTipo("U");
			det.setSerie("U");
			notasDet.add(det);
		}
	}
	
	/**
	 * Re calcula el importe y descuento de las notas de credito
	 *
	 */
	private void actualizarPartidas(){
		System.out.println("Actualizando importe de partidas");
		for(NotasDeCreditoDet det:notasDet){
			final Venta v=det.getFactura();
			final double descuento;
			if(isCondonar())
				descuento=v.getDescuentoPactado();
			else
				descuento=v.getDescuentoPactado()-v.getCargo();
			final CantidadMonetaria devoluciones=CantidadMonetaria.pesos(v.getDevolucionesCred());
			final CantidadMonetaria importe=v.getTotal().add(devoluciones).multiply(descuento/100);
			det.setImporte(importe);
			det.setDescuento(descuento);
		}
	}
	
	public List<NotaDeCredito> procesar(){
		final List<NotaDeCredito> res=new ArrayList<NotaDeCredito>();			
		res.addAll(NotasUtils.getNotasFromDetalles(notasDet));
		CollectionUtils.forAllDo(res, new Closure(){
			public void execute(Object input) {
				NotaDeCredito nc=(NotaDeCredito)input;
				NotasUtils.configurarParaDescuento(nc);
				nc.setDescuentoAnticipado(anticipada);
				nc.setFecha(getFecha());
			}
		});
		return res;
		
	}
	
	private class CondonarHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			actualizarPartidas();			
		}
		
	}
	
	private class ParaDescuentoMatcher implements Matcher<Venta>{
		

		public boolean matches(Venta item) {
			boolean val =(item.getSaldo().abs().doubleValue()>0
					&& item.isProvisionable()
					&& item.getDescuento1()==0);			
			return (val && item.getPagos()!=0);
		}
		
	}
	
	private class ParaDescuentoAnticipadoMatcher implements Matcher<Venta>{	
		

		public boolean matches(Venta item) {
			boolean val =(item.getSaldo().abs().doubleValue()>0
					&& item.isProvisionable()
					&& item.getDescuento1()==0);
			if(getCliente().getCredito().isNotaAnticipada())
				return val;
			else
				return (val && item.getPagos()!=0);
		}
		
	}
}