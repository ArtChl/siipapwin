package com.luxsoft.siipap.inventarios.dao;

import java.util.List;

import com.luxsoft.siipap.dao.GenericDao;
import com.luxsoft.siipap.domain.Periodo;

import com.luxsoft.siipap.inventarios.domain.CostoUltimo;

public interface CostoUltimoDao extends GenericDao<CostoUltimo,Long>{
	
	/**
	 * Busca el costo de ultima compra para un articulo determinado
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoUltimo buscarCostoUltimo(String articulo,Periodo periodo);
	
	/**
	 * Busca el costo de ultima compra para un articulo determinado
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoUltimo buscarCostoUltimo(String articulo,String periodo);
	
	/**
	 * Busca el costo de ultima compra para un articulo determinado sin importar el periodo
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoUltimo buscarCostoUltimo(String articulo);
	
	
	public List<CostoUltimo> buscarCostosUltimos();

}
