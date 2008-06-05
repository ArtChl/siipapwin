package com.luxsoft.siipap.cxc.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.cxc.managers.PagosManager;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * 
 * Modelo para la adminsitracion de pagos de ventas a credito
 * Es utilizado como base en las distintas consultas de operaciones
 * de pagos
 * 
 * @author Ruben Cancino
 *
 */
public class PagosDeCreditoModel extends PresentationModel{
	
	private Cliente cliente;
	private Periodo periodo=Periodo.getPeriodoConAnteriroridad(11);
	
	
	private EventList<Venta> ventas;
	private EventList<Pago> pagos;
	private EventList<NotaDeCredito> notasDeCargo;
	
	private CXCManager manager;
	private PagosManager pagosManager;
	private VentasManager ventasManager;
	
	
	
	public PagosDeCreditoModel(){
		super(null);
		setBean(this);
		init();
	}
	
	private void init(){
		ventas=GlazedLists.threadSafeList(new BasicEventList<Venta>());
		addPropertyChangeListener(new Handler());
	}
	
	/**
	 * Actualiza el estado del modelo
	 * 
	 */
	private void updateModel(){
		getPagos().clear();
		getVentas().clear();
	}
	
	public EventList<Venta> getVentas(){
		if(ventas==null){
			ventas=GlazedLists.threadSafeList(new BasicEventList<Venta>());
		}
		return ventas;
	}
	
	public EventList<Pago> getPagos(){
		if(pagos==null){
			pagos=GlazedLists.threadSafeList(new BasicEventList<Pago>());
		}
		return pagos;
	}	
	
	public EventList<NotaDeCredito> getNotasDeCargo() {
		if(notasDeCargo==null){
			notasDeCargo=GlazedLists.threadSafeList(new BasicEventList<NotaDeCredito>());
		}
		return notasDeCargo;
	}

	public List<Venta> getBuscarVentas(){		
		return getManager().buscarVentasCredito(getCliente(),getPeriodo());
	}
	
	public List<Pago> buscarPagos(){
		return getPagosManager().buscarPagosAplicados(getCliente(), getPeriodo());
	}
	
	public Venta refrescar(final Venta v){
		getVentasManager().refresh(v);		
		return v;
	}
	
	

	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		Object old=this.cliente;
		this.cliente = cliente;
		firePropertyChange("cliente", old, cliente);
	}
	
	public Periodo getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Periodo periodo) {
		Object old=this.periodo;
		this.periodo = periodo;
		firePropertyChange("periodo", old, periodo);
	}
	
	public List<ClienteCredito> buscarClientesACredito(){
		return getManager().buscarClientesACredito();
	}
	
	public void clear(){
		getPagos().clear();
		getVentas().clear();
		
	}
	
	/**** Colaboradores ***/
	
	public PagosManager getPagosManager() {
		return pagosManager;
	}
	public void setPagosManager(PagosManager pagosManager) {
		this.pagosManager = pagosManager;
	}

	public CXCManager getManager() {
		return manager;
	}
	public void setManager(CXCManager manager) {
		this.manager = manager;
	}
	public VentasManager getVentasManager() {
		return ventasManager;
	}
	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}
	
	private class Handler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			updateModel();
		}		
	}

	
	
	

}
