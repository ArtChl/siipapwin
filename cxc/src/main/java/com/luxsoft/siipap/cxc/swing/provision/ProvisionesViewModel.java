package com.luxsoft.siipap.cxc.swing.provision;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.domain.Periodo;

public class ProvisionesViewModel extends Model{
	
	private Logger logger=Logger.getLogger(getClass());
	
	private Date fechaInicial=Periodo.periodoDeloquevaDelMes().getFechaInicial();
	private Date fechaFinal=new Date();
	private final PresentationModel parametrosModel; 
	private EventList<Provision> provisiones;
	
	private NotasManager manager;
	
	public ProvisionesViewModel(){
		parametrosModel=new PresentationModel(this);		
	}
	
	public void load(){
		try {
			Assert.notNull(getManager());			
			List<Provision> source=getManager().buscarProvisiones();
			loadData(source, getProvisiones());
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void loadData(final List source,final EventList target){
		target.getReadWriteLock().writeLock().lock();
		try{
			target.clear();
			target.addAll(source);			
		}catch(Exception ex){
			logger.error("Errror al cargar datos en eventList :",ex);
		}finally{
			target.getReadWriteLock().writeLock().unlock();
		}
	}
	private void unloadData(final EventList target){
		target.getReadWriteLock().writeLock().lock();
		try{
			target.clear();
		}catch(Exception ex){
			logger.error("Error al limpiar datos en eventList", ex);
		}finally{
			target.getReadWriteLock().writeLock().unlock();
		}
	}
	
	public void dispose(){
		unloadData(getProvisiones());
	}
	
	public EventList<Provision> getProvisiones() {
		if(provisiones==null){
			provisiones=new BasicEventList<Provision>();
		}
		return provisiones;
	}

	public PresentationModel getParametrosModel(){
		return parametrosModel;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	public Periodo periodo(){
		return new Periodo(getFechaInicial(),getFechaFinal());
	}

	public NotasManager getManager() {
		return manager;
	}
	public void setManager(NotasManager manager) {
		this.manager = manager;
	}
	
}
