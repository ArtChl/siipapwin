package com.luxsoft.siipap.em.parches;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.Comision;
import com.luxsoft.siipap.services.ServiceLocator;


/**
 * Actualiza los descuentos en ventas credito para tipo G N etc
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche10_AltaClienteCredito {
	
	public void execute(final String clave){
		Cliente c=ServiceLocator.getClienteDao().buscarPorClave(clave);
		c.generarCredito();
		ServiceLocator.getClienteDao().salvar(c);
		
	}
	
	public static void main(String[] args) {
		new Parche10_AltaClienteCredito().execute("D010094");
	}

}
