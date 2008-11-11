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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.em.importar.VentasSync;
import com.luxsoft.siipap.em.managers.EMServiceLocator;
import com.luxsoft.siipap.em.utils.DbUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

public class VentasReplicaModel extends PresentationModel implements PropertyChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EventList<Venta> ventasWin;
	private EventList<VentaDet> ventasDetWin;
	private EventList<Venta> ventasDbf;
	private EventList<VentaDet> ventasDetDbf;
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
		ventasDetWin=new BasicEventList<VentaDet>();
		ventasDbf=new BasicEventList<Venta>();
		ventasDetDbf=new BasicEventList<VentaDet>();
		faltantes=new BasicEventList<Venta>();
		sobrantes=new BasicEventList<Venta>();
	}
	
	public void loadData(){
		LoadWorker worker=new LoadWorker();
		worker.addPropertyChangeListener(this);
		TaskUtils.executeSwingWorker(worker);
	}
	
	@SuppressWarnings("unchecked")
	private void cargarFaltantes(){
		faltantes.clear();
		sobrantes.clear();
		Collection<Venta> res=CollectionUtils.subtract(ventasDbf, ventasWin);
		faltantes.addAll(res);
		sobrantes.addAll(CollectionUtils.subtract( ventasWin,ventasDbf));
		
	}
	
	
	
	public EventList<Venta> getVentasWin() {
		return ventasWin;
	}
	

	public EventList<VentaDet> getVentasDetWin() {
		return ventasDetWin;
	}

	public EventList<Venta> getVentasDbf() {
		return ventasDbf;
	}

	public EventList<VentaDet> getVentasDetDbf() {
		return ventasDetDbf;
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
	
	@SuppressWarnings("unchecked")
	public void sincronizar(){
		SwingWorker worker=new SwingWorker(){
			protected Object doInBackground() throws Exception {
				getVentasSync().sinconizar(getDia());
				return "OK";
			}
			protected void done() {
				loadData();
			}
			
		};
		TaskUtils.executeSwingWorker(worker);
		
	}
	
	
	
	/**
	 * SwingWorker para cargar las listas de ventas 
	 * 
	 * @author RUBEN
	 *
	 */
	@SuppressWarnings("unchecked")
	private class LoadWorker extends SwingWorker<List<List>, String>{
		
		
		@Override
		protected List<List> doInBackground() throws Exception {
			List<List> list=new ArrayList<List>();
			List<Venta> vdbf=getVentasSync().buscarVentasEnSiipap(getDia());			
			List<Venta> vwin=getVentasSync().buscarVentasEnSW(getDia());
			List<VentaDet> vdetDbf=getVentasSync().buscarVentasDetEnSiipap(getDia());
			list.add(0,vdbf);
			list.add(1,vwin);
			list.add(2,vdetDbf);
			List<VentaDet> vdetWin=new ArrayList<VentaDet>();
			for(Venta v:vwin){
				vdetWin.addAll(v.getPartidas());
			}
			list.add(3,vdetWin);
			return list;
		}
		
		
		@Override
		protected void done() {
			try {
				List<List> res=get();
				ventasWin.clear();
				ventasWin.addAll(res.get(1));
				ventasDbf.clear();
				ventasDbf.addAll(res.get(0));
				ventasDetDbf.clear();
				ventasDetDbf.addAll(res.get(2));
				ventasDetWin.clear();
				ventasDetWin.addAll(res.get(3));
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
