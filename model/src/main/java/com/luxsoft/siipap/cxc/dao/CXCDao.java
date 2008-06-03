package com.luxsoft.siipap.cxc.dao;

public interface CXCDao {
	
	/**
	 * Actualiza los registros de CXC para el periodo indicado
	 * 
	 * NOTA: Los registros deben ser eliminados proviamente a la ejecicion de este metodo
	 * 
	 *
	 */
	public void actualizar(final int year,final int mes);

}
