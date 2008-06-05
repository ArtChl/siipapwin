package com.luxsoft.siipap.cxc.swing.cobranza;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import com.jgoodies.binding.value.ComponentValueModel;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;
import com.luxsoft.siipap.swing.form.FormModel;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * PresentationModel para encapsular el comportamiento y estado 
 * del manenimiento de Facturas
 * 	
 * 	Responsablidades
 * 		1 Cargar sus pagos
 * 		2 Cargar sus notas
 *  
 * @author Ruben Cancino
 *
 */
public class MantenimientoDeFacturaModel extends AbstractGenericFormModel<Venta, Long>{
	
	private EventList<Pago> pagos;
	private EventList<NotasDeCreditoDet> notas;

	public MantenimientoDeFacturaModel(Object bean) {
		super(bean);		
	}
	
	@Override
	protected void initEventHandling() {		
		super.initEventHandling();
	}

	@Override
	protected void initModels() {
		super.initModels();		
		pagos=GlazedLists.threadSafeList(new BasicEventList<Pago>());
		notas=GlazedLists.threadSafeList(new BasicEventList<NotasDeCreditoDet>());
		pagos.addListEventListener(new PagosHandler());		
	}
	
	
	
	@Override
	public ComponentValueModel getComponentModel(String propertyName) {
		ComponentValueModel vm=super.getComponentModel(propertyName);
		//vm.setEnabled(false);
		vm.setEditable(false);
		return vm;
	}

	public void loadData(){
		pagos.addAll(getManager().buscarPagos(getFormBean()));
		notas.addAll(getManager().buscarNotas(getFormBean()));
	}
	public void eliminarPago(final Pago p){
		getManager().eliminar(p);
		getPagos().remove(p);
	}
	
	public void actualizarVenta(){
		System.out.println("Actualizando ...");
		//getManager().actualizarVenta(getFormBean());
	}
	
	
	/**
	 * Controla las modificaciones de la lista de pagos de la factura
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class PagosHandler implements ListEventListener<Pago>{

		public void listChanged(ListEvent<Pago> listChanges) {
			while(listChanges.hasNext()){
				if(listChanges.next()){
					System.out.println(listChanges.toString());
					if(listChanges.getType()==ListEvent.INSERT){
						actualizarVenta();
					}
				}
			}
		}
		
	}



	private CXCManager getManager(){
		return (CXCManager)ServiceLocator.getDaoContext().getBean("cxcManager");
	}

	public EventList<NotasDeCreditoDet> getNotas() {
		return notas;
	}

	public EventList<Pago> getPagos() {
		return pagos;
	}

}
