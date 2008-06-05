package com.luxsoft.siipap.cxc.model2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
//import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

/**
 * Implementación de {@link PagoConOtrosModel}
 * 
 * @author Ruben Cancino
 *
 */
public class DefaultPagoFormModelImpl extends DefaultFormModel implements PagoFormModel{
	
	private EventList<Pago> pagos;
	private boolean paraFacturas=true;
	
	
	public DefaultPagoFormModelImpl(final Object bean) {
		this(bean,true);
	}
	
	public DefaultPagoFormModelImpl(final Object bean,final boolean paraFactura) {
		super(bean);
		this.paraFacturas=paraFactura;
	}

	protected void init(){
		getComponentModel("referencia").setEnabled(false);
		PropertyChangeListener handler=new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Modificando importe");
				actualizarPagos();
			}			
		}; 
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
		}/*if(validarImporte2()){
			support.addError("importe", "El importe no el total del saldo pendiente : "+maximo());
		}*/
	}

	/**
	 * Valida que el importe asignado no exceda el disponible del origen
	 * 
	 * @return
	 */
	public boolean validarImporte() {		
		final BigDecimal v1=getPago().getImporte().amount();
		final BigDecimal v2=getDisponible();
		return v1.compareTo(v2)<=0;
	}
	
	private boolean validarImporte2(){
		final BigDecimal v1=getPago().getImporte().amount();
		CantidadMonetaria importe=maximo();
		return importe.amount().doubleValue()<=v1.doubleValue(); 
	}
	
	private CantidadMonetaria maximo(){
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		for(Pago p:getPagos()){			
			importe=importe.add(estimarPago(p));
		}
		return importe;
	}
	
	
	/**
	 * Actualiza el importe de los pagos aplicados 
	 * 
	 */
	public void actualizarPagos(){
		
		CantidadMonetaria disponible=getPago().getImporte();
		
		for(int index=0;index<getPagos().size();index++){			
			final Pago p=getPagos().get(index);
			p.setImporte(CantidadMonetaria.pesos(0));
			final CantidadMonetaria adeudo=estimarPago(p);
			
			if(disponible.amount().doubleValue()>=adeudo.amount().doubleValue()){
				p.setImporte(adeudo);
			}else{
				p.setImporte(disponible);
			}
			disponible=disponible.subtract(p.getImporte());
			getPagos().set(index, p);			
			actualizarDescuentos();
			actualizarDisponible(disponible);
			if(disponible.amount().doubleValue()<=0)
				break;			
		}
	}
	
	protected CantidadMonetaria estimarPago(final Pago pago){
		if(pago.getVenta()!=null){
			if(getPago().isCondonar()){				
				return pago.getVenta().getSaldoEstimadoSinCargo().abs();
			}
			else
				return pago.getVenta().getSaldoEstimado().abs();
			
		}else 
			return pago.getNota().getSaldoDelCargoEnMoneda().abs();
	}
	
	protected void actualizarDescuentos(){
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
	
	/**
	 * Template method para hacer algo con el disponible
	 * @param disponible
	 */
	protected void actualizarDisponible(final CantidadMonetaria disponible){
		
	}
	

	protected PagoM getPago(){
		return (PagoM)getBaseBean();
	}
	
	public BigDecimal getDisponible(){
		if(getBaseBean() instanceof PagoConNota){
			final double val=((PagoConNota)getBaseBean()).getNota().getSaldo();
			return BigDecimal.valueOf(val).abs();
		}
		if(getBaseBean() instanceof PagoConOtros)
			return ((PagoConOtros)getBaseBean()).getOrigen().getDisponible();
		return getPago().getDisponible();
	}
	
}
