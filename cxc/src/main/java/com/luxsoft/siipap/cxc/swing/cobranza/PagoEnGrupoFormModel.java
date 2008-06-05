package com.luxsoft.siipap.cxc.swing.cobranza;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.TiposDeNotas;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;
import com.luxsoft.siipap.ventas.domain.Venta;

public class PagoEnGrupoFormModel extends AbstractGenericFormModel<PagoEnGrupo, String>{
	
	private EventList<Venta> ventas;
	private EventList<Pago> pagos=new BasicEventList<Pago>();
	private EventList<NotaDeCredito> notas=new BasicEventList<NotaDeCredito>();

	public PagoEnGrupoFormModel() {
		super(new PagoEnGrupo());
	}

	public PagoEnGrupoFormModel(List<Venta> ventas) {
		super();		
		validarVentas(ventas);
		getVentas().addAll(ventas);
		
	}
	
	private void validarVentas(List<Venta> data){
		if(data.isEmpty())return;
		String clave=null;
		String nombre=null;
		Date fecha=null;
		for(Venta v:data){
			Assert.notNull(v.getCredito(),"No se puede procesar una venta sin datos de credito venta: "+v.getId());
			if(clave==null){
				clave=v.getClave();
				nombre=v.getNombre();
				fecha=v.getCredito().getReprogramarPago();
				continue;
			}
			Assert.isTrue(clave.equals(v.getClave()),"Debe ser pago para facturas del mismo cliente");
			//Assert.isTrue(fecha.equals(v.getCredito().getReprogramarPago()),"Si paga mas de una factura deben ser del mismo dia de pago");			
		}
		getFormBean().setClave(clave);
		//getFormBean().setFecha(fecha);
		getFormBean().setCliente(nombre);
	}
	
	protected void initModels(){
		super.initModels();
		ventas=new BasicEventList<Venta>();
		GlazedLists.syncEventListToList(ventas, getFormBean().getVentas());
		ventas.addListEventListener(new VentasHandler());
		getComponentModel("importe").addValueChangeListener(new ImporteHandler());
		getComponentModel("saldoFacturas").setEnabled(false);
		getComponentModel("saldo").setEnabled(false);
		//getComponentModel("fecha").setEnabled(false);
		getComponentModel("aplicarDeMenos").setEnabled(false);
		getComponentModel("condonarAbono").addValueChangeListener(new CargosHandler());
		
	}

	@Override
	protected void initEventHandling() {		
		super.initEventHandling();
	}
	
	public EventList<Venta> getVentas() {
		return ventas;
	} 
	

	/**
	 * Actualiza segun se modifiquen los
	 */
	private class VentasHandler implements ListEventListener<Venta>{
		public void listChanged(ListEvent<Venta> listChanges) {
			while(listChanges.hasNext()){				
				listChanges.next();
				getFormBean().recalcular();
			}
		}
		
	};
	
	/**
	 * Actualiza el del grupo de facturas en funcion del monto del importe del pago
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class ImporteHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {			
			getFormBean().recalcular();			
			double sal=getFormBean().getImporteAOtrosPagos().amount().doubleValue();
			boolean activar=sal>=-100&&sal<=0;			
			getComponentModel("aplicarDeMenos").setEnabled(activar);
			asignarPago();
		}		
	}
	
	/**
	 * Actualiza si se condona cargos
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class CargosHandler implements PropertyChangeListener{

		public void propertyChange(PropertyChangeEvent evt) {
			Boolean val=(Boolean)evt.getNewValue();
			if(val)
				System.out.println("Actualizando por que se condonan cargos");
			getFormBean().recalcular();
			//asignarPago();
		}
		
	}

	private void asignarPago(){
		CantidadMonetaria pago=getFormBean().getImporte();
		if(pago==null)
			pago=CantidadMonetaria.pesos(0);
		for(Venta v:ventas){
			if(pago.amount().doubleValue()>0){
				//CantidadMonetaria saldo=v.getSaldoEstimado();		
				CantidadMonetaria saldo=getFormBean().getPorPagar(v);				
				CantidadMonetaria dif=pago.subtract(saldo);
				if(dif.amount().doubleValue()>0)
					v.setPago(saldo);
				else{
					v.setPago(pago);
				}
				System.out.println("Pago recalculado :"+pago);
				pago=dif;
				continue;
			}
			
		}
	}

	@Override
	public ValidationResult validate() {
		final String role=ClassUtils.getShortName(getBeanClass());
		final PropertyValidationSupport support =new PropertyValidationSupport(getBean(),role);
		if(getFormBean().getImporte().amount().doubleValue()==0)
			support.addError("Importe ", "Digite el importe del pago");
		return support.getResult();
	}
	
	
	
	/**
	public EventList<NotaDeCredito> getNotas() {
		return notas;
	}**/
	

	public EventList<Pago> getPagos() {
		return pagos;
	}

	public void commit(){
		//List<Pago> pagos=new ArrayList<Pago>();
		for(Venta v:getVentas()){
			Pago p=getFormBean().aplicarPago(v);
			p.setImporte(v.getPago());			
			pagos.add(p);
		}
		final int index=ventas.size();
		final Venta otros=ventas.get(index-1);
		if(getFormBean().getImporteAOtrosPagos().amount().doubleValue()>0){
			//Hay saldo a favor por registrar			
			final Pago pp=getFormBean().aplicarPago(otros);
			PagosFactory.getPagoOtrosProductosAFavor(pp);
			pp.setReferencia(getFormBean().getFormaDePago().getId()+" "+getFormBean().getReferencia()+" "+pp.getFormaDePago2().getId());
			pp.setImporte(getFormBean().getImporteAOtrosPagos());
			pagos.add(pp);
			if(logger.isDebugEnabled()){
				logger.debug("Pago de mas: "+pp);
			}
		}else if(getFormBean().getImporteAOtrosPagos().amount().doubleValue()<0){
			CantidadMonetaria menos=getFormBean().getImporteAOtrosPagos();
			if(menos.amount().abs().doubleValue()<=100 && getFormBean().isAplicarDeMenos()){
				//Pago de menos valido				
				final Pago pp=getFormBean().aplicarPago(otros);
				PagosFactory.getPagoOtrosProductosDeMenos(pp);
				pp.setReferencia(getFormBean().getFormaDePago().getId()+" "+getFormBean().getReferencia()+" "+pp.getFormaDePago2().getId());
				pp.setImporte(getFormBean().getImporteAOtrosPagos());
				pagos.add(pp);
				if(logger.isDebugEnabled()){
					logger.debug("Pago de menos: "+pp);
				}
			}
		}
		
		//Generar notas de credito por descuento
		//final List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
		notasDet.clear();
		
		CollectionUtils.forAllDo(getVentas(), new Closure(){
			public void execute(Object input) {
				Venta v=(Venta)input;
				if(v.getDescuento1()==0){
					NotasDeCreditoDet det=NotasUtils.getNotaDet(v);
					notasDet.add(det);
					double ip=v.getProvision().getImporteDescuento1().doubleValue()*1.15;
					double desc=ip*100/v.getSaldo().doubleValue();
					//det.setDescuento(getFormBean().getDescuento(v));
					
					det.setDescuento(desc);
					det.setSaldoFactura(v.getSaldoEnMoneda());
					det.actualizarImporte();
					det.setFecha(getFormBean().getFecha());
					det.setOrigen("CRE");
					det.setTipo("U");
					det.setSerie("U");
				}
				
			}			
		});
		
		List<NotaDeCredito> creadas=NotasUtils.getNotasFromDetalles(notasDet);
		
		CollectionUtils.forAllDo(creadas, new Closure(){
			public void execute(Object input) {
				NotaDeCredito nn=(NotaDeCredito)input;
				//nn.setClave(getFormBean().getClave());
				nn.setComentario(getFormBean().getComentario());
				nn.setFecha(getFormBean().getFecha());
				nn.setOrigen("CRE");
				nn.setTipo("U");
				nn.setSerie("U");
				
			}			
		});
		if(!creadas.isEmpty())
			notas.addAll(creadas);
		
		if(logger.isDebugEnabled()){
			for(Pago p:pagos){
				logger.debug("Pago: "+p);
			}
			for(NotaDeCredito n:notas){
				logger.debug("Nota automatica: "+n);
			}
		}
		
	}
	
	private EventList<NotasDeCreditoDet> notasDet=new BasicEventList<NotasDeCreditoDet>();
	
	public void persistir(){
		try {
			CXCManager manager=(CXCManager)ServiceLocator.getDaoContext().getBean("cxcManager");
			manager.aplicarPagosConDescuentos(pagos,notas);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public EventList<NotasDeCreditoDet> getNotasDet() {
		return notasDet;
	}

}
