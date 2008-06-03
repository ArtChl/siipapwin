package com.luxsoft.siipap.cxc.dao;


import java.util.List;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;

public interface ClienteDao {
	
	public void salvar(final Cliente c);
	
	public Cliente buscar(Long id);
	
	public Cliente buscarPorClave(final String clave);
	
	public List<Cliente> buscarClientesPorClave(final String clave);
	
	public List<Cliente> buscarPorNombre(final String nombre);
	
	//public List<Cliente> buscarClientesCredito();
	
	public List<ClienteCredito> buscarClientesDeCredito();
	
	public void refrescar(final ClienteCredito c);
	
	/**
	 * Habilita al cliente con ClienteCredito 
	 * 
	 * @param clave
	 */
	public void generarClienteCredito(final String clave);

}
