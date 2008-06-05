package com.luxsoft.siipap.cxc.swing.cobranza;

import java.util.List;

import org.apache.log4j.Logger;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.ObservableElementList.Connector;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.cxc.services.ActualizadorDeClientes;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Estado y comportamiento para la vista principal de Cobranza
 * 
 * @author Ruben Cancino
 *
 */
public class CobranzaViewModel extends Model{
	
	private Logger logger=Logger.getLogger(getClass());
	 
	private EventList<ClienteCredito> clientes;
	private CXCManager manager;
	private VentasManager ventasManager;
	private EventList<Venta> ventas;
	//private EventList<Venta> observedVentas;
	private PresentationModel clienteModel;
	private ActualizadorDeClientes actualizador;
	
	public CobranzaViewModel(){
		Cliente c=new Cliente();
		c.setClave("");
		c.setNombre("");
		clienteModel=new PresentationModel(c);
		
		initGlazedLists();
	}
	
	private void initGlazedLists(){
		
		clientes=GlazedLists.threadSafeList(new BasicEventList<ClienteCredito>());
		//Connector<ClienteCredito> connector=GlazedLists.beanConnector(ClienteCredito.class);
		//clientes=new ObservableElementList<ClienteCredito>(clientes,connector);
		ventas=GlazedLists.threadSafeList(new BasicEventList<Venta>());
		//Connector<Venta> connector=GlazedLists.beanConnector(Venta.class);
		//observedVentas=new ObservableElementList<Venta>(ventas,connector);
		
		
	}
	
	public PresentationModel getClienteModel(){
		return clienteModel;
	}
	
	public List<ClienteCredito> loadClientes(){		
		List<ClienteCredito> l=getManager().buscarClientesACredito();
		return l;
	}
	
	public void actualizarCliente(final Cliente c){
		getActualizador().actualizarCliente(c.getCredito());
	}
	
	public List<Venta> loadVentas(final Cliente c){		
		clienteModel.setBean(c);
		List<Venta> ventas=getManager().buscarVentasConSaldo(c);
		return ventas;
	}
	
	
	public void actualizarVenta(final Venta v){
		getVentasManager().actualizarVenta(v);
	}	
	
	public EventList<ClienteCredito> getClientes() {
		return clientes;
	}	

	public EventList<Venta> getVentas() {
		return ventas;
		//return observedVentas;
	}	

	public CXCManager getManager() {
		return manager;
	}

	public void setManager(CXCManager manager) {
		this.manager = manager;
	}

	public void dispose(){
		try {
			ventas.clear();
			clientes.clear();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}finally{
			
		}
	}

	public VentasManager getVentasManager() {
		return ventasManager;
	}

	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}

	public ActualizadorDeClientes getActualizador() {
		return actualizador;
	}

	public void setActualizador(ActualizadorDeClientes actualizador) {
		this.actualizador = actualizador;
	}	
	
}
