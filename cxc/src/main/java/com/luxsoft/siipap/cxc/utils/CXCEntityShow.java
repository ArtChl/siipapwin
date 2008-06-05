package com.luxsoft.siipap.cxc.utils;

import com.luxsoft.siipap.cxc.consultas.ConsultaUtils;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.nc.RONotaDeCreditoForm;
import com.luxsoft.siipap.cxc.nc.RONotaDeCreditoFormModel;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Clase con metodos estaticos para msotrar las diversas entidades
 * del modulo de CXC en una interfaz de solo lectura, es decir solo
 * para consulta
 * 
 * @author Ruben Cancino
 *
 */
public class CXCEntityShow {
	
	
	public static void mostrarEntidad(final Object obj){
		if(obj instanceof Venta)
			mostrarVenta((Venta)obj);
		if(obj instanceof NotaDeCredito)
			mostrarNota((NotaDeCredito)obj);
		else
			MessageUtils.showMessage("No existe un visor para la entidad: \n"+obj.getClass().getName(), "Error");
	}
	
	public static void mostrarVenta(final Venta v){
		ConsultaUtils.mostrarVenta(v);
	}
	
	public static void mostrarNota(final NotaDeCredito nota){
		final RONotaDeCreditoFormModel model=new RONotaDeCreditoFormModel(nota);
		final RONotaDeCreditoForm form=new RONotaDeCreditoForm(model);
		form.open();
	}

}
