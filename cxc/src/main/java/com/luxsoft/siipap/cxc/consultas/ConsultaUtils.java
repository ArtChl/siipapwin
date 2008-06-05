package com.luxsoft.siipap.cxc.consultas;


import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Static Helper para las consulas mas comunes
 * 
 * @author RUBEN
 *
 */
public class ConsultaUtils {
	
	
	public static void mostrarVenta(final Venta v){
		final FacturaFormROModel model=new FacturaFormROModel(v);
		final FacturaFormRO dialog=new FacturaFormRO(model);
		dialog.open();
	}

}
