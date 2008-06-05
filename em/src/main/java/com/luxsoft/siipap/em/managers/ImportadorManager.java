package com.luxsoft.siipap.em.managers;

import java.util.List;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.em.importar.ImportadorDeClientes;
import com.luxsoft.siipap.em.importar.VentasSupport;

public final class ImportadorManager {
	
	private VentasSupport ventasSupport;
	private ImportadorDeClientes importadorDeClientes;
	private ClienteDao clienteDao;
	
	/**
	 * Importa los clientes faltantes en ventas
	 */
	public void importarClientesFaltantes(){
		List<String> clientes=getVentasSupport().buscarVentasSinCliente();
		System.out.println("Cliente sin id: "+clientes);
		for(String clave:clientes){
			try {
				Cliente c=(Cliente)getImportadorDeClientes().importar(clave)[0];
				System.out.println("Cliente: "+clave);
				System.out.println(c);
				getClienteDao().salvar(c);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
		}
		
	}

	public VentasSupport getVentasSupport() {
		return ventasSupport;
	}

	public void setVentasSupport(VentasSupport ventasSupport) {
		this.ventasSupport = ventasSupport;
	}

	public ImportadorDeClientes getImportadorDeClientes() {
		return importadorDeClientes;
	}

	public void setImportadorDeClientes(ImportadorDeClientes importadorDeClientes) {
		this.importadorDeClientes = importadorDeClientes;
	}

	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}
	
	public static void main(String[] args) {
		ImportadorManager manager=EMServiceLocator.instance().getImportadorManager();
		manager.importarClientesFaltantes();
		
	}
}
