package com.luxsoft.siipap.cxc.swing.descuentos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.SwingWorker;

import org.springframework.util.ClassUtils;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.cxc.managers.DescuentosManager;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;

/**
 * 
 * FormModel para el mantenimiento de Descuentos por articulo en grupo partiendo de el cliente, utiliza
 * el bean DescuentosPorArticulos (aka No es lo mismo que DescuentoPorArticulo)
 * 
 * @author Ruben Cancino
 *
 */
public class DescuentosPorArticulosModel extends AbstractGenericFormModel<DescuentosPorArticulos, String>{
	
	private DescuentosManager manager;
	
	

	public DescuentosPorArticulosModel() {
		super();
	}

	public DescuentosPorArticulosModel(Object bean) {
		super(bean);		
	}
	
	@SuppressWarnings("unchecked")
	public void registerFilterList(EventList list){
		getFormBean().setDescuentos(list);
		/*
		list.addListEventListener(new ListEventListener(){
			public void listChanged(ListEvent listChanges) {
				
				while(listChanges.hasNext()){
					listChanges.next();
					System.out.println(listChanges.getSourceList().size());
				}
				
			}
			
		});*/
	}

	@Override
	protected void initModels() {		
		super.initModels();
		getComponentModel("precioKilo").setEnabled(getFormBean().isPorPrecioKilo());
	}

	@Override
	protected void initEventHandling() {		
		super.initEventHandling();
		//addBeanPropertyChangeListener(new DescuentoHandler());
		PropertyChangeListener descHandler=new DescuentoHandler();
		addBeanPropertyChangeListener("descuento",descHandler);
		addBeanPropertyChangeListener("precioKilo",descHandler);
		addBeanPropertyChangeListener(new ActivoHandler());
		addBeanPropertyChangeListener("porPrecioKilo",new TipoHandler());
	}

	/**
	 * Obtiene de la base de datos la lista de descuentos por articulo asignados al clientes
	 * especifico
	 * 
	 * @return
	 */
	public List<DescuentoPorArticulo> loadDescuentos(){		
		Cliente c=getFormBean().getCliente();
		if(c==null) return new ArrayList<DescuentoPorArticulo>();
		List<DescuentoPorArticulo> descs=getManager().buscarDescuentosPorArticulo(getFormBean().getCliente());
		return descs;				
	}
	
	
	
	
	
	/**
	 * Elimina descuentos si no se han salvado con anterioridad
	 * 
	 * @param desc
	 */
	public void delete(EventList<DescuentoPorArticulo> desc){
		desc.getReadWriteLock().writeLock().lock();
		try{
			desc.clear();
		}finally{
			desc.getReadWriteLock().writeLock().unlock();
		}
		
	}
	
	/**
	 * Genera una lista de descuentos a partir de los articulos seleccionados
	 * 
	 * @param articulos
	 */
	public List<DescuentoPorArticulo> crearDescuentos(final List<Articulo> articulos){
		List<DescuentoPorArticulo> descs=new ArrayList<DescuentoPorArticulo>();
		if(getFormBean().getCliente()==null)
			return descs;
		for(Articulo a:articulos){
			DescuentoPorArticulo d=DescuentosFactory.descuentoPorArticulo(a, getFormBean().getCliente());
			d.setDescuento(getFormBean().getDescuento());
			d.setPorPrecioKilo(getFormBean().isPorPrecioKilo());
			descs.add(d);
		}		
		return descs;
	}
	
	/**
	 * Carga en un sub-proceso  los costos de los descuentos
	 *
	 */
	public void cargarCostos(){
		SwingWorker worker=new SwingWorker(){
			
			protected Object doInBackground() throws Exception {
				logger.debug("Cargando costos");
				try{
					getFormBean().getDescuentos().getReadWriteLock().writeLock().lock();
					getManager().asignarCostos(getFormBean().getDescuentos());
				}finally{
					getFormBean().getDescuentos().getReadWriteLock().writeLock().unlock();
				}				
				return null;
			}			
		};		
		worker.execute();
		
	}
	
	/**
	 * Busca los articulos en la base de datos
	 * 
	 * @return
	 */
	public List<Articulo> buscarArticulos(){
		return getManager().buscarArticulos();
	}

	public DescuentosManager getManager() {
		return manager;
	}
	public void setManager(DescuentosManager manager) {
		this.manager = manager;
	}	
	
	public void salvarDescuentos(List<DescuentoPorArticulo> list){
		System.out.println("Salvando..."+list);
		getManager().salvar(list);
	}
	
	
	
	@Override
	public void commit() {
		salvarDescuentos(getFormBean().getDescuentos());
	}

	@Override
	public ValidationResult validate() {
		final String role=ClassUtils.getShortName(getBeanClass());
		final PropertyValidationSupport support =new PropertyValidationSupport(getBean(),role);
		if(getFormBean().getDescuento()<=1)
			support.addError("Descuento", ": el mínimo es 1%");
		return support.getResult();
	}

	private void actualizarDescuento(){		
		for(DescuentoPorArticulo d:getFormBean().getDescuentos()){
			if(getFormBean().isPorPrecioKilo()){
				d.setPrecioKilo(getFormBean().getPrecioKilo());
				d.calcularDescuentoEnFuncionDePrecioKilo();				
			}
			else{
				d.setDescuento(getFormBean().getDescuento());
				d.calcularPrecioKiloEnFuncionDeDescuento();				
			}
			d.setPorPrecioKilo(getFormBean().isPorPrecioKilo());
			setDirty(true);
		}
	}
	
	
	private void actualizarActivo(){		
		for(DescuentoPorArticulo d:getFormBean().getDescuentos()){
			d.setActivo(getFormBean().isActivo());			
			if(!d.isActivo()){
				d.setBaja(new Date());
			}else
				d.setBaja(null);
		}
	}
	
	
	private class ActivoHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {			
			actualizarActivo();
		}
	}
	
	private class DescuentoHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {			
			actualizarDescuento();
		}
	}
	
	private class TipoHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			Boolean val=(Boolean)evt.getNewValue();
			getComponentModel("precioKilo").setEnabled(val);
			getComponentModel("descuento").setEnabled(!val);			
		}		
	}
	
	

	
}
