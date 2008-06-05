package com.luxsoft.siipap.cxc.chequed;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Date;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.CantidadMonetaria;


/**
 * Encapsula el estad y comportamiento para el procesamiento de una nota
 * de cargo por cheque devuelto
 * 
 * @author Ruben Cancino
 *
 */
public class SolicitudDeCargoPorChequeDevuelto extends PresentationModel{
	
	private final ValidationResultModel validationModel;
	
	private final ChequeDevuelto cheque;
	private Cliente cliente;
	
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	
	private boolean porPorcentaje=true;
	private double porcentaje=20;
	private Date fecha=new Date();
	private String comentario="";
	

	public SolicitudDeCargoPorChequeDevuelto(final ChequeDevuelto cheque) {
		super(null);		
		setBean(this);
		this.cheque=cheque;
		this.cliente = cheque.getCliente();
		
		final ImporteHandler h=new ImporteHandler();
		addBeanPropertyChangeListener("porPorcentaje", h);
		addBeanPropertyChangeListener("porcentaje", h);
		
		validationModel=new DefaultValidationResultModel();		
		addBeanPropertyChangeListener(new ValidationHandler());
		actualizarImporte();
		
	}
	
	public ChequeDevuelto getCheque(){
		return cheque;
	}
	
	public BigDecimal getImporteDelCheque(){
		return cheque.getImporte();
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
		firePropertyChange("porPorcentaje", old, porPorcentage);
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(double porcentage) {
		double oldValue=this.porcentaje;
		this.porcentaje = porcentage;
		firePropertyChange("porcentaje", oldValue, porcentage);
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
	
	private void actualizarImporte(){		
		BigDecimal imp=cheque.getImporte();
		final double por=getPorcentaje()/100;
		setImporte(CantidadMonetaria.pesos(imp.doubleValue()*por));
		
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
		if(getImporte().abs().amount().doubleValue()>cheque.getImporte().doubleValue()){
			support.addError("importe", "El importe del cargo no puede superar al del cheque");
		}
	}
	
	
	private class ImporteHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			if(!isPorPorcentaje())
				setPorcentaje(0);
			actualizarImporte();			
		}
	}	
	
	
	private class ValidationHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			validate();
		}
	}	

}
