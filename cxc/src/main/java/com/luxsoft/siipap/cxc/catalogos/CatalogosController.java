package com.luxsoft.siipap.cxc.catalogos;

import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

/**
 * Facade para la administracion de los catalogos de CxC
 * 
 * @author Ruben Cancino
 *
 */
public class CatalogosController {
	
	/**
	 * Actualiza un cliente de credito
	 * 
	 * @param credito
	 */
	public static void actualizarClienteCredito(final ClienteCredito credito){
		DefaultFormModel model=new DefaultFormModel(credito);
		System.out.println("Cliente antes: "+credito);
		model.setReadOnly(false);
		getCXCManager().getClienteDao().refrescar(credito);
		ClienteForm form=new ClienteForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			System.out.println("Despues: "+credito);
			getCXCManager().actualizarCliente(credito);
			
		}
	}
	
	
	private static CXCManager getCXCManager(){
		return ServiceLocator.getCXCManager();
	}

}
