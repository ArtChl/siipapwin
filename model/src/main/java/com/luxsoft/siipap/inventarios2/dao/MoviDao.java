package com.luxsoft.siipap.inventarios2.dao;

import com.luxsoft.siipap.inventarios2.domain.MoviDoc;

/**
 * DAO para movimientos genericos de inventario
 * 
 * @author Ruben Cancino
 *
 */
public interface MoviDao {
	
	public MoviDoc buscarById(final Long id);
	
	public void salvar(MoviDoc mov);
	
	public void eliminar(MoviDoc mov);
	
	 

}
