package com.luxsoft.siipap.cxc.model2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

/**
 * Implementación de {@link PagoConOtrosModel}
 * 
 * @author Ruben Cancino
 *
 */
public class PagoConOtrosModelImpl extends DefaultFormModel implements PagoConOtrosModel{
	
	private EventList<Pago> pagos;
	private final boolean paraFacturas;
	
	public PagoConOtrosModelImpl(){
		this(true,new PagoConOtros());
	}
	
	public PagoConOtrosModelImpl(boolean paraFacturas,final PagoConOtros bean) {
		super(bean);
		this.paraFacturas=paraFacturas;
		getComponentModel("referencia").setEnabled(false);
		PropertyChangeListener handler=new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Modificando importe");
				actualizarPagos();
			}			
		};
		this.pmodel.setValue("condonar", true);
		this.pmodel.addBeanPropertyChangeListener("importe", handler);
		this.pmodel.addBeanPropertyChangeListener("condonar", handler);
		
		
	}
	
	/**
	 * Verifica que el pago sea valido y util para ser utilizado en pago con otros
	 * 
	 * @param pago
	 * @return
	 */
	public boolean isValid(final PagoM pago){
		return pago.getDisponible().doubleValue()>0;
	}

	/**
	 * {@link EventList} de los pagos a generar
	 * 
	 * @return
	 */
	public EventList<Pago> getPagos() {
		if(pagos==null){
			pagos=new BasicEventList<Pago>();
			pagos.addAll(getPago().getPagos());
		}
		return pagos;
	}

	public boolean isParaFacturas() {
		return paraFacturas;
	}
		
	
	protected void addValidation(PropertyValidationSupport support){
		if(!validarImporte()){
			support.addError("importe", "El importe no puede exceder el disponible del pago origen");
		}
		CantidadMonetaria total=CantidadMonetaria.pesos(0);
		for(Pago p:getPagos()){
			total=total.add(p.getImporte());
		}
		/*
		if(getPago().getImporteAsDouble()>total.getAmount().doubleValue()){
			support.addError("importe", "El importe no puede ser mayor al saldo total de las facturas/cargos Total: "+total);
		}
		*/
	}

	/**
	 * Valida que el importe asignado no exceda el disponible del origen
	 * 
	 * @return
	 */
	public boolean validarImporte() {		
		final BigDecimal v1=getPago().getImporte().amount();
		final BigDecimal v2=getPago().getOrigen().getDisponible();
		return v1.compareTo(v2)<=0;
	}
	
	
	
	/**
	 * Actualiza el importe de los pagos aplicados 
	 * 
	 */
	public void actualizarPagos(){
		CantidadMonetaria disponible=getPago().getImporte();//getPago().getOrigen().getDisponible();
		for(int index=0;index<getPagos().size();index++){			
			final Pago p=getPagos().get(index);
			p.setImporte(CantidadMonetaria.pesos(0));
			final CantidadMonetaria pago=estimarPago(p);
			
			
			if(disponible.amount().doubleValue()>=pago.amount().doubleValue()){				
				p.setImporte(pago);
				disponible=disponible.subtract(p.getImporte());
			}else{
				p.setImporte(disponible);
				disponible=CantidadMonetaria.pesos(0);
			}
			//disponible=disponible.subtract(p.getImporte());
			getPagos().set(index, p);			
			actualizarDescuentos();
			if(disponible.amount().doubleValue()<=0)
				break;
		}
	}
	
	private CantidadMonetaria estimarPago(final Pago pago){
		if(pago.getVenta()!=null){
			if(getPago().isCondonar()){				
				return pago.getVenta().getSaldoEstimadoSinCargo();
			}
			else
				return pago.getVenta().getSaldoEstimado();
			
		}else 
			return pago.getNota().getSaldoDelCargoEnMoneda();
	}
	
	private void actualizarDescuentos(){
		for(int index=0;index<getPagos().size();index++){
			Pago p=getPagos().get(index);
			if( (p.getVenta()==null) || p.getVenta().getDescuentos()!=0)
				continue;
			final CantidadMonetaria totalV=p.getVenta().getTotal();
			final CantidadMonetaria devo=CantidadMonetaria.pesos(p.getVenta().getDevolucionesCred());
			final CantidadMonetaria total=totalV.add(devo);
			final double descuento;
			if(getPago().isCondonar()){
				descuento=p.getVenta().getDescuentoPactado();				 
			}else{
				descuento=p.getVenta().getDescuento();
			}
			final CantidadMonetaria impdesc=total.multiply(descuento/100);
			p.setDescuento(impdesc);
			getPagos().set(index, p);
		}
	}
	

	private PagoConOtros getPago(){
		return (PagoConOtros)getBaseBean();
	}
	
}
