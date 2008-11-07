package com.luxsoft.siipap.em.importar.ui;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.em.importar.VentasSync;
import com.luxsoft.siipap.em.managers.EMServiceLocator;
import com.luxsoft.siipap.em.utils.DbUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

public class VentasReplicaModel extends PresentationModel implements PropertyChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EventList<Venta> ventasWin;
	private EventList<Venta> ventasDbf;
	private EventList<Venta> faltantes;
	private EventList<Venta> sobrantes;
	
	private Date dia=new Date();
	
	private boolean actualizando;
	
	private VentasSync ventasSync;
	
	public VentasReplicaModel(){
		super(null);
		initGlazedLists();
		setBean(this);
	}
	
	private void initGlazedLists(){
		ventasWin=new BasicEventList<Venta>();
		ventasDbf=new BasicEventList<Venta>();
		faltantes=new BasicEventList<Venta>();
		sobrantes=new BasicEventList<Venta>();
	}
	
	public void loadData(){
		LoadWorker worker=new LoadWorker();
		worker.addPropertyChangeListener(this);
		TaskUtils.executeSwingWorker(worker);
	}
	
	private void cargarFaltantes(){
		faltantes.clear();
		sobrantes.clear();
		System.out.println("Win "+ventasWin.size());
		System.out.println("Dbf:"+ventasDbf.size());
		Collection<Venta> res=CollectionUtils.subtract( ventasWin,ventasDbf);
		faltantes.addAll(res);
		sobrantes.addAll(CollectionUtils.subtract( ventasDbf,ventasWin));
		
	}
	
	private boolean exists(EventList<Venta> source,final Venta bean){
		return CollectionUtils.exists(source, new Predicate(){

			public boolean evaluate(Object object) {
				Venta v=(Venta)object;
				System.out.println("Evaluando: "+v);
				if(v.getSucursal().equals(bean.getSucursal()))
					if(v.getNumero().equals(bean.getNumero()))
						if(v.getSerie().equals(bean.getSerie()))
							return v.getTipo().equals(bean.getTipo());
				return false;
			}
			
		});
	}

	public EventList<Venta> getVentasWin() {
		return ventasWin;
	}

	public EventList<Venta> getVentasDbf() {
		return ventasDbf;
	}

	public EventList<Venta> getFaltantes() {
		return faltantes;
	}

	public EventList<Venta> getSobrantes() {
		return sobrantes;
	}

	public VentasSync getVentasSync() {
		if(ventasSync==null){
			ventasSync=EMServiceLocator.instance().getSincronizadorDeVentas();
		}
		return ventasSync;
	}

	public void setVentasSync(VentasSync ventasSync) {
		this.ventasSync = ventasSync;
	}

	public Date getDia() {
		return dia;
	}

	public void setDia(Date dia) {
		Object old=this.dia;
		this.dia = dia;
		firePropertyChange("dia",old,dia);
	}

	public boolean isActualizando() {
		return actualizando;
	}

	public void setActualizando(boolean actualizando) {
		boolean old=this.actualizando;
		this.actualizando = actualizando;
		firePropertyChange("actualizando", old, actualizando);
	}
	
	/**
	 * Template callback
	 */
	public void afterDataLoaded(){
		
	}
	
	private String odbc;

	public String getOdbc() {
		return odbc;
	}

	public void setOdbc(String odbc) {
		Object old=this.odbc;
		this.odbc = odbc;
		firePropertyChange("odbc", old, odbc);
	}

	public void propertyChange(PropertyChangeEvent event) {
		 if ("state".equals(event.getPropertyName())){
			 SwingWorker.StateValue st= (StateValue)event.getNewValue();
			 switch (st) {
			 	case DONE:
			 		setActualizando(true);
			 		break;
			 	case PENDING:
			 	case STARTED:
			 		setActualizando(true);
			 		break;
			 	default:
			 		break;
			}
		 }
		
	}
	
	/**
	 * SwingWorker para cargar las listas de ventas 
	 * 
	 * @author RUBEN
	 *
	 */
	private class LoadWorker extends SwingWorker<List<List<Venta>>, String>{
		
		@SuppressWarnings("unchecked")
		@Override
		protected List<List<Venta>> doInBackground() throws Exception {
			List<List<Venta>> list=new ArrayList<List<Venta>>();
			list.add(0,getVentasSync().buscarVentasEnSiipap(getDia()));
			list.add(1,getVentasSync().buscarVentasEnSW(getDia()));
			return list;
		}
		
		
		@Override
		protected void done() {
			try {
				List<List<Venta>> res=get();
				ventasWin.clear();
				ventasWin.addAll(res.get(0));
				ventasDbf.clear();
				ventasDbf.addAll(res.get(1));
				cargarFaltantes();
				VentasReplicaModel.this.afterDataLoaded();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}			
			if(getOdbc()==null)
				setOdbc(DbUtils.getSiipapOdbc());
		}
	}
	
	
}
