package com.luxsoft.siipap.cxc.services;


import com.luxsoft.siipap.cxc.domain.ClienteCredito;

public interface ActualizadorDeClientes {
	
	
	public void actualizarCliente(final ClienteCredito c);
	
	public void actualizarClientesCredito();
	
	/**
	 * Genera los clientes credito a partir de las ventas
	 *
	 */
	public void generarClientesCredito();

}
