package com.luxsoft.siipap.cxc.model2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Encapsula el estad y comportamiento para el procesamiento de una nota
 * de cargo
 * 
 * @author Ruben Cancino
 *
 */
public class SolicitudDeNotaDeCargo extends PresentationModel{
	
	private final ValidationResultModel validationModel;
	private EventList<Venta> ventas;
	private Cliente cliente;
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	private CantidadMonetaria totalFacturas=CantidadMonetaria.pesos(0);
	private boolean porPorcentaje=true;
	private double porcentaje=0;
	private Date fecha=new Date();
	private String comentario="";
	

	public SolicitudDeNotaDeCargo(final Cliente cliente,final List<Venta> ventas) {
		super(null);		
		setBean(this);
		this.ventas = GlazedLists.eventList(ventas);
		this.cliente = cliente;
		
		final Handler h=new Handler();
		addBeanPropertyChangeListener("importe", h);
		addBeanPropertyChangeListener("porcentage", h);		
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

	public void aplicarCargo(){
		for(int index=0;index<getVentas().size();index++){
			Venta v=getVentas().get(index);
			CantidadMonetaria imp=null;
			if(isPorPorcentaje()){
				final CantidadMonetaria devoluciones=CantidadMonetaria.pesos(v.getDevolucionesCred());
				imp=v.getTotal().add(devoluciones).multiply(getPorcentaje()/100);
				//imp=v.getSaldoEnMoneda().multiply(getPorcentaje()/100);
			}
			else{
				BigDecimal divisor=new BigDecimal(getVentas().size());				
				imp=getImporte().divide(divisor.doubleValue());
			}			
			v.setPago(imp);
			getVentas().set(index, v);
		}
		
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
	
	
	private class Handler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			aplicarCargo();			
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
