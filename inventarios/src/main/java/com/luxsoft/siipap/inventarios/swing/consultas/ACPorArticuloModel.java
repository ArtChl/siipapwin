package com.luxsoft.siipap.inventarios.swing.consultas;

import java.util.List;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.services.InventariosManager;

/**
 * Comportamiento y estado relacionados con la consulta de AnalisisDecostosPorArticulo
 * 
 * @author Ruben Cancino
 *
 */
public class ACPorArticuloModel extends PresentationModel{
	
	private Articulo articulo;
	private Periodo periodo;
	private EventList<AnalisisDeEntrada> entradas;
	private InventariosManager manager;
	
	public ACPorArticuloModel(){
		super(null);
		setBean(this);
	}
	
	public EventList<AnalisisDeEntrada> getEntradas(){
		if(entradas==null){
			entradas=GlazedLists.threadSafeList(new BasicEventList<AnalisisDeEntrada>());
		}
		return entradas;
	}
	
	public void load(){
		if(getPeriodo()!=null && getArticulo()!=null){
			List<AnalisisDeEntrada> list=getManager().buscarEntradasCom(getPeriodo(), getArticulo().getClave());
			GlazedLists.replaceAll(entradas, list, false);
		}		
	}
	
	public Articulo getArticulo() {
		return articulo;
	}
	public void setArticulo(Articulo articulo) {
		Object old=this.articulo;
		this.articulo = articulo;
		firePropertyChange("articulo",old, articulo);
	}
	
	public Periodo getPeriodo() {
		if(periodo==null){
			periodo=Periodo.getPeriodoDelMesActual();
		}
		return periodo;
	}
	public void setPeriodo(Periodo periodo) {
		Object old=this.periodo;
		this.periodo = periodo;
		firePropertyChange("periodo", old, periodo);
	}
	
	public void close(){
		entradas.clear();
	}

	public InventariosManager getManager() {
		return manager;
	}
	public void setManager(InventariosManager manager) {
		this.manager = manager;
	}
	
	

}
