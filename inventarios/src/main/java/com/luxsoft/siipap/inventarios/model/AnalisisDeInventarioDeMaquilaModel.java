package com.luxsoft.siipap.inventarios.model;

import java.util.Date;
import java.util.List;

import ca.odell.glazedlists.EventList;

import com.jgoodies.binding.value.ValueModel;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;

public interface AnalisisDeInventarioDeMaquilaModel {
	
	/**
	 * Lista todas las entradas de material
	 * 
	 * @return
	 */
	public EventList<EntradaDeMaterial> getEntradas();
	
	
	/**
	 * Carga la informacion de la base de datos;
	 * 
	 * @return
	 */
	public List<EntradaDeMaterial> buscarEntradas();
	
	/**
	 * {@link EventList} List de todas las salidas a corte
	 * @return
	 */
	public EventList<SalidaACorte> getSalidas();
	
	/**
	 * {@link EventList} de las salidas a corte
	 * @return
	 */
	public EventList<SalidaACorte> getSalidasACorte();
	
	
	
	/**
	 * Busca las salidas a corte existentes
	 * 
	 * @return
	 */
	public List<SalidaACorte> buscarSalidasACorte();
	
	/**
	 * Regresa la lista filtrada de salidas de bobinas
	 * 
	 * @return
	 */
	public EventList<SalidaDeBobinas> getSalidaDeBobinas();
	
	/**
	 * Busca en la BD todas las salidas de bobina
	 * 
	 * @return
	 */
	public List<SalidaDeBobinas> buscarSalidaDeBobinas();
	
	/**
	 * Regresa un List con las salidas que estan en proceso
	 * @return
	 */
	public EventList<SalidaACorte> getSalidasEnProceso();
	
	
	
	
	/**
	 * Regresa un {@link EventList} de salidas de material por trslado entre almacenes del maquilador
	 * 
	 * @return
	 */
	public EventList<SalidaDeMaterial> getTraslados();
	
	
	/**
	 * Busca todas las salidas de material(Traslados y Salidas de bobinas directas)
	 * 
	 * @return
	 */
	public List<SalidaDeMaterial> buscarSalidasDeMaterial();
	
	/**
	 * {@link EventList} de Salidas de material
	 * @return
	 */
	public EventList<SalidaDeMaterial> getSalidasDeMaterial();
	
	
	/// *********************Interfaz relacionada con material cortado***************************
	
	/**
	 * Esta lista origen de todas las salidas de hojas existentes.
	 * 
	 * <p> Esta lista sirve como base para diversas listas filtradas como la
	 * que regresa el metodo getSalidasDeHojas();
	 * 
	 * @return
	 */
	public EventList<SalidaDeHojas> getSalidasDeHojasSource();
	
	/**
	 * Regrea una lista filtrada de todas los beans {@link SalidaDeHojas}
	 * 
	 * @return
	 */
	public EventList<SalidaDeHojas> getSalidaDeHojas();
	
	/**
	 * Regresa una lista de todas los beans {@link EntradaDeHojas}
	 * 
	 * @return
	 */
	public EventList<EntradaDeHojas> getEntradasDeHojas();	
	
	
	/**
	 * Carga el inventario de Maquila de material cortado
	 */
	public void cargarInvnentarioDeHojas();
	
	///  ****************Fin Interfaz relacionada con material cortado************************** /
	
	/**
	 * Regresa un value model adecuado para las diversas opciones de la vista
	 *  
	 * 
	 * @param property
	 * @return
	 */
	public ValueModel getModel(String property);
	
	/**
	 * Regresa la fecha de corte
	 * 
	 * @return
	 */
	public Date getFechaDeCorte();

}
