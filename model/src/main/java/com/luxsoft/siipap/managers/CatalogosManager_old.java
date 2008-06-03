package com.luxsoft.siipap.managers;


import java.util.List;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;

/**
 * Facade para los DAO mas utilizados, especialmente los selectores,binding 
 * y property editors
 * 
 * @author Ruben Cancino
 * @deprecated
 * TODO ELIMINAR
 *
 */
public class CatalogosManager_old {
	
	private ClienteDao clienteDao;
	
	/**
	 * Busca todos los clinetes con clave similar a la proporcinada
	 * 
	 * @param clave
	 * @return
	 */
	public List<Cliente> buscarClientesPorClave(final String clave){
		return getClienteDao().buscarClientesPorClave(clave);
	}
	
	/**
	 * Busca todos los clinetes con nombre similar a la proporcinada
	 * 
	 * @param nombre
	 * @return
	 */
	public List<Cliente> buscarClientesPorNombre(final String nombre){
		return getClienteDao().buscarPorNombre(nombre);
	}
	
	/**
	 * Busca un clinete con clave similar a la proporcinada
	 * 
	 * @param clave
	 * @return
	 */
	public Cliente buscarClientePorClave(final String clave){
		return getClienteDao().buscarPorClave(clave);
	}
	
	/**
	 * Busca un clinete con nombre similar a la proporcinada
	 * 
	 * @param clave
	 * @return
	 */
	public Cliente buscarClientePorNombre(final String nombre){
		List<Cliente> clientes=buscarClientesPorNombre(nombre);
		if(clientes.isEmpty())
			return null;
		return clientes.get(0);
	}

	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}
	
	

}
