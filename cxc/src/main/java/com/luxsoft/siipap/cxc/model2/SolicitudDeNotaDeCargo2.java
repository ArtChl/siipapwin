package com.luxsoft.siipap.cxc.model2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Encapsula el estad y comportamiento para el procesamiento de una nota
 * de cargo
 * 
 * @author Ruben Cancino
 *
 */
public class SolicitudDeNotaDeCargo2 extends PresentationModel{
	
	private final ValidationResultModel validationModel;
	private EventList<Venta> ventas;
	private Cliente cliente;
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	private CantidadMonetaria totalFacturas=CantidadMonetaria.pesos(0);
	private boolean porPorcentaje=true;
	private double porcentaje=0;
	private Date fecha=new Date();
	private String comentario="";
	

	public SolicitudDeNotaDeCargo2(final Cliente cliente,final List<Venta> ventas) {
		super(null);		
		setBean(this);
		final DescUnitarioHandler duh=new DescUnitarioHandler();
		for(Venta v:ventas){
			v.addPropertyChangeListener("descuentoTemporal", duh);
		}
		this.ventas = GlazedLists.eventList(ventas);
		this.cliente = cliente;
		
		final ImporteHandler h=new ImporteHandler();
		addBeanPropertyChangeListener("importe", h);
		final DescHandler dh=new DescHandler();
		addBeanPropertyChangeListener("porcentage", dh);		
		validationModel=new DefaultValidationResultModel();		
		addBeanPropertyChangeListener(new ValidationHandler());		
		actualizarTotales();
	}


	public EventList<Venta> getVentas() {
		return ventas;
	}
	public void setVentas(EventList<Venta> ventas) {
		this.ventas = ventas;
	}

	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public CantidadMonetaria getImporte() {
		return importe;
	}
	public void setImporte(CantidadMonetaria importe) {
		Object oldValue=this.importe;
		this.importe = importe;
		firePropertyChange("importe", oldValue, importe);
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public boolean isPorPorcentaje() {
		return porPorcentaje;
	}

	public void setPorPorcentaje(boolean porPorcentage) {
		boolean old=this.porPorcentaje;
		this.porPorcentaje = porPorcentage;
		firePropertyChange("porPorcentage", old, porPorcentage);
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(double porcentage) {
		double oldValue=this.porcentaje;
		this.porcentaje = porcentage;
		firePropertyChange("porcentage", oldValue, porcentage);
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		Object oldValue=this.comentario;
		this.comentario = comentario;
		firePropertyChange("comentario", oldValue, comentario);
	}
	
	
	public ValidationResultModel getValidationModel() {
		return validationModel;
	}

	public void aplicarImporteFijo(){
		/**
		for(int index=0;index<getVentas().size();index++){
			Venta v=getVentas().get(index);
			CantidadMonetaria imp=null;			
			BigDecimal divisor=new BigDecimal(getVentas().size());				
			imp=getImporte().divide(divisor.doubleValue());
			v.setPago(imp);
			getVentas().set(index, v);
		}
		*/
		/**
		 * final CantidadMonetaria pago=getNota().getImporte();
		if(pago.amount().doubleValue()==0)
			return;
		//Obtnere el total de los saldos
		double totalAPagar=0;
		for(NotasDeCreditoDet partida:getPartidas()){
			totalAPagar=totalAPagar+partida.getFactura().getTotalSinDevoluciones().amount().doubleValue();
		}
		
		//Prorrateamos entre las partidas
		for(int index=0;index<getPartidas().size();index++){
			NotasDeCreditoDet det=(NotasDeCreditoDet)getPartidas().get(index);
			double saldo=det.getFactura().getTotalSinDevoluciones().amount().doubleValue();
			//Obtenemos la participacion de la partida
			double participacion=saldo/totalAPagar;
			final CantidadMonetaria importe=pago.multiply(participacion);
			det.setImporte(MonedasUtils.calcularTotal(importe).abs().multiply(-1));
			det.setDescuento(participacion*100);
			getPartidas().set(index, det);
		}
		 */
		final CantidadMonetaria pago=MonedasUtils.calcularTotal(getImporte());
		if(pago.amount().doubleValue()==0)
			return;
		//Obtnere el total de los saldos
		//CantidadMonetaria importeSinDevo=CantidadMonetaria.pesos(0);
		double imp=0;
		for(int index=0;index<getVentas().size();index++){
			Venta factura=getVentas().get(index);
			//importeSinDevo=importeSinDevo.add(factura.getTotalSinDevoluciones());
			imp+=factura.getTotalSinDevolucionesAsDouble();
		}
		//final CantidadMonetaria totalSinDevo=MonedasUtils.calcularTotal(importeSinDevo);
		
		for(int index=0;index<getVentas().size();index++){
			final Venta factura=getVentas().get(index);
			final double participacion=factura.getTotalSinDevoluciones().amount().doubleValue()/imp;
			System.out.println("Desc :"+participacion);
			factura.setDescuentoTemporal(participacion*100);
			final CantidadMonetaria totFac=pago.multiply(participacion);
			factura.setPago(totFac);
			getVentas().set(index, factura);
		}
	}
	
	public void aplicarCargo(final double desc)
	{
		System.out.println("Aplicando descuento general de: "+desc);
		for(int index=0;index<getVentas().size();index++){			
			Venta v=getVentas().get(index);
			v.setDescuentoTemporal(desc);
			getVentas().set(index, v);
		}
	}
	
	public void actualizarCargoConDescuento(){		
		for(int index=0;index<getVentas().size();index++){
			Venta v=getVentas().get(index);
			final CantidadMonetaria devoluciones=CantidadMonetaria.pesos(v.getDevolucionesCred());
			CantidadMonetaria imp=v.getTotal().add(devoluciones).multiply(v.getDescuentoTemporal()/100);
			v.setPago(imp);
			getVentas().set(index, v);
		}
	}
	
	private void actualizarImporteConDescuento(){
		
		CantidadMonetaria imp=CantidadMonetaria.pesos(0);
		
		for(int index=0;index<getVentas().size();index++){			
			Venta v=getVentas().get(index);
			getVentas().set(index, v);
			imp=imp.add(v.getPago());
		}
		setImporte(MonedasUtils.calcularImporteDelTotal(imp));
	}
	
	public void validate(){
		final PropertyValidationSupport support=new PropertyValidationSupport(this,"Cargo");
		if(getComentario().length()>70){
			support.addError("comentario", "Longitud del comentario no puede ser mayor a 70");
		}
		validarImporte(support);
		validationModel.setResult(support.getResult());
	}
	
	private void validarImporte(final PropertyValidationSupport support){
		if(isPorPorcentaje()){
			if(getPorcentaje()<=0)
				support.addError("porcentage", "El importe del cargo es incorrecto");
		}else{
			if(getImporte().amount().doubleValue()<=0)
				support.addError("porcentage", "El importe del cargo es incorrecto");
		}
	}
	
	
	private class ImporteHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			if(!isPorPorcentaje()){
				//aplicarCargo(0);
				aplicarImporteFijo();
			}
						
		}		
	}
	
	private class DescHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			Double val=(Double)evt.getNewValue();
			if(isPorPorcentaje())
				aplicarCargo(val);
			//actualizarCargoConDescuento();
		}		
	}
	
	private class DescUnitarioHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			if(isPorPorcentaje()){
				actualizarCargoConDescuento();				
				actualizarImporteConDescuento();
			}		
		}		
	}
	
	private class ValidationHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			validate();
		}
	}

	public CantidadMonetaria getTotalFacturas() {
		return totalFacturas;
	}


	public void setTotalFacturas(CantidadMonetaria totalFacturas) {
		Object old=this.totalFacturas;
		this.totalFacturas = totalFacturas;
		firePropertyChange("totalFacturas", old, totalFacturas);
	}
	
	private void actualizarTotales(){
		CantidadMonetaria total=CantidadMonetaria.pesos(0);
		for(Venta v:getVentas()){
			total=total.add(v.getTotal());
		}
		setTotalFacturas(total);
	}
	

}
