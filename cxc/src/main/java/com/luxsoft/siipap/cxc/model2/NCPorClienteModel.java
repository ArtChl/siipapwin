package com.luxsoft.siipap.cxc.model2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.UniqueList;
import ca.odell.glazedlists.FunctionList.Function;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * 
 * Modelo para la adminsitracion de notas de credito/cargo de un cliente
 * Mantiene una lista de las ventas y notas de un cliente para un periodo en particular
 * Si la seleccion del cliente cambia este actualiza limpiando las listas
 * 
 * @author Ruben Cancino
 *
 */
public class NCPorClienteModel extends PresentationModel{
	
	private Logger logger=Logger.getLogger(getClass());
	
	private Cliente cliente;
	private Periodo periodo=Periodo.getPeriodoConAnteriroridad(4);
	private EventList<Venta> ventas;
	private EventList<NotaDeCredito> notas;
	private EventList<NotasDeCreditoDet> notasSource;
	private CXCManager manager;
	
	public NCPorClienteModel(){
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
		if(logger.isDebugEnabled()){
			String pattern="Cambio en el modelo cliente: {0} periodo: {1}";
			logger.debug(MessageFormat.format(pattern, getCliente(),getPeriodo()));
		}
		getVentas().clear();
		getNotasSource().clear();
	}
	
	public EventList<Venta> getVentas(){
		if(ventas==null){
			ventas=GlazedLists.threadSafeList(new BasicEventList<Venta>());
		}
		return ventas;
	}
	
	/**
	 * Obtiene si no existe un {@link EventList} de {@link NotaDeCredito} derivado de un {@link EventList} de {@link NotasDeCreditoDet}
	 * 
	 * @return
	 */
	public EventList<NotaDeCredito> getNotas(){
		if(notas==null){			
			final Function<NotasDeCreditoDet,NotaDeCredito> function=GlazedLists.beanFunction(NotasDeCreditoDet.class, "nota");
			final FunctionList<NotasDeCreditoDet, NotaDeCredito> flist=new FunctionList<NotasDeCreditoDet, NotaDeCredito>(getNotasSource(),function);
			notas=new UniqueList<NotaDeCredito>(flist,GlazedLists.beanPropertyComparator(NotaDeCredito.class, "id"));
		}
		return notas;
	}
	
	public EventList<NotasDeCreditoDet> getNotasSource(){
		if(notasSource==null){
			notasSource=GlazedLists.threadSafeList(new BasicEventList<NotasDeCreditoDet>());
		}
		return notasSource;
	}
	
	public void loadNotasSilently(){
		SwingWorker worker=new SwingWorker(){			
			protected Object doInBackground() throws Exception {
				loadNotas();
				return null;
			}
			
		};
		worker.execute();
	}
	
	
	/**
	 * Carga las notas de credito de la base de datos 
	 *
	 */
	public void loadNotas(){
		try {
			final List<NotasDeCreditoDet> detalles=getManager().buscarNotas(getCliente(),getPeriodo());			
			getNotasSource().clear();
			getNotasSource().addAll(detalles);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * Busca las ventas de cliente para el periodo seleccionado
	 *  
	 * @return
	 */
	public List<Venta> getBuscarVentas(){
		return getManager().buscarVentasCredito(getCliente(),getPeriodo());
	}
	
	public Venta refrescar(final Venta v){
		getManager().refrescarVenta(v);
		return v;
	}
	
	public void close(){
		getVentas().clear();
		getNotasSource().clear();
	}
	
	/*************** Bean property accesors ***********************/

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
	
	/**** Colaboradores *************************************/

	public CXCManager getManager() {
		return manager;
	}
	public void setManager(CXCManager manager) {
		this.manager = manager;
	}
	
	/**
	 * Detecta los cambios en este bean para actualizar el modelo.
	 * Los cambios pueden ser en el cliente o en el periodo
	 * 
	 * @author Ruben Cancino
	 *
	 */
	private class Handler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			updateModel();
		}		
	}

	
	
	

}
