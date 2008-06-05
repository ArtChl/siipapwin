package com.luxsoft.siipap.em.parches;

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
public class Parche9_AltaComisiones {
	
	public void execute(){
		Comision c1=new Comision();
		c1.setId(4l);
		c1.setMinimo(41);
		c1.setMaximo(47);
		c1.setComision(1.0);
		ServiceLocator.getUniversalDao().save(c1);
		
	}
	
	public static void main(String[] args) {
		new Parche9_AltaComisiones().execute();
	}

}
