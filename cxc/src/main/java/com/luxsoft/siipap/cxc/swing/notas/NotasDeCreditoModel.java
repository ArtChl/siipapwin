package com.luxsoft.siipap.cxc.swing.notas;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.managers.NotasManagerImpl;
import com.luxsoft.siipap.domain.Periodo;

public class NotasDeCreditoModel extends Model{
	
	private Logger logger=Logger.getLogger(getClass());
	
	private Date fechaInicial=Periodo.periodoDeloquevaDelMes().getFechaInicial();
	private Date fechaFinal=new Date();
	private final PresentationModel parametrosModel; 
	private EventList<NotaDeCredito> notas;
	
	private NotasManagerImpl manager;
	
	public NotasDeCreditoModel(){
		parametrosModel=new PresentationModel(this);		
	}
	
	public void load(){
		try {
			Assert.notNull(getManager());
			Periodo p=periodo();
			List<NotaDeCredito> source=getManager().buscarNotasCre(p);
			loadData(source, getNotas());
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
		unloadData(getNotas());
	}
	
	public EventList<NotaDeCredito> getNotas() {
		if(notas==null){
			notas=new BasicEventList<NotaDeCredito>();
		}
		return notas;
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

	public NotasManagerImpl getManager() {
		return manager;
	}
	public void setManager(NotasManagerImpl manager) {
		this.manager = manager;
	}
	
	
	
	
}
