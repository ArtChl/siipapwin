package com.luxsoft.siipap.inventarios.dao;

import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.dao.GenericDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.CostoPromedio;

public interface CostoPromedioDao extends GenericDao<CostoPromedio,Long>{
	
	/**
	 * Busca el costo promedio para un articulo determinado
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoPromedio buscarCostoPromedio(String articulo,Periodo periodo);
	
	/**
	 * Busca el costo promedio para un articulo determinado
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoPromedio buscarCostoPromedio(String articulo,String periodo);
	
	
	public List<CostoPromedio> buscarCostosPromedio();
	
	/**
	 * @deprecated usar buscarCostoMasReciente
	 * 
	 * @param articulo
	 * @param antesDe
	 * @return
	 */
	public CostoPromedio buscarCostoMasProximo(final Articulo articulo,final Date antesDe);
	
	/**
	 * Busca el costo promedio mas reciente
	 * 
	 * @param articulo
	 * @return
	 */
	public CostoPromedio buscarCostoMasReciente(final Articulo articulo);
	
	/**
	 * Busca en las tablas de CXP el costo mas reciente para el articulo
	 * 
	 * @param articulo
	 * @return
	 */
	public CantidadMonetaria buscarUltimoCosto(final Articulo articulo);

}
