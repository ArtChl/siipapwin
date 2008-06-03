package com.luxsoft.siipap.maquila.dao;

import java.util.Collection;
import java.util.List;

import com.luxsoft.siipap.maquila.domain.Almacen;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.OrdenDeCorte;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;

public interface OrdenDeCorteDao {
	
	public void create(OrdenDeCorte oc);
	
	public OrdenDeCorte get(Long id);
	
	public void update(OrdenDeCorte oc);
	
	public void delete(OrdenDeCorte oc);
	
	
	
	public List<OrdenDeCorte> buscarOrdenes();
	
	/**
	 * Busca las entradas de material disponibles (con existencia)
	 * para el almacen determinado
	 * @param a
	 * @return
	 */
	public List<EntradaDeMaterial> buscarEntradasDisponibles(Almacen a);
	
	
	/**
	 * Busca todas las entrads de material disponibles
	 * @return
	 */
	public List<EntradaDeMaterial> buscarEntradasDisponibles();
	
	
	/**
	 * Inicializa todas las asociacienes importantes de la
	 * orden de corte
	 * 
	 * @param oc
	 */
	public void inicializarOrdenDeCorte(OrdenDeCorte oc);
	
	
	
	
	
	/**
	 * Actualiza el saldo de las entradas anteriormente asociadas
	 * a estas Salidas 
	 * 
	 * @param beans
	 */
	public void actualizarSaldoDeEntradas(Collection<SalidaACorte> salidas);
	
	
	/**
	 * Busca las SalidasACorte
	 * 
	 * @param disponibles
	 * @return
	 */
	public List<SalidaACorte> buscarSalidas(boolean disponibles);
	
	
	public void inicializarSalidaACorte(SalidaACorte sc);

}
