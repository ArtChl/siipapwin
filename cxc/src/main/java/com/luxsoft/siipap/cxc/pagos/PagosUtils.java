package com.luxsoft.siipap.cxc.pagos;

import java.util.ArrayList;
import java.util.List;

import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.ventas.domain.Venta;

public class PagosUtils {
	
	public static double getMinimoParaAutomatico(){
		return 100;
	}
	
	
	
	public static PagoM generarPagoAutomatico(final List<Venta> ventas){
		PagoM pago=new PagoM();
		pago.setCliente(ventas.get(0).getCliente());
		for(Venta v:ventas){
			pago.aplicarPago(v, v.getSaldoEnMoneda());
		}
		pago.setFormaDePago(FormaDePago.U);
		return pago;
	}
	
	
	/**
	 * Genera una lista de las ventas que se pagaron mediante aplicaciones
	 * de este pago
	 * 
	 * @param pago
	 * @return
	 */
	public static List<Venta> extraerVentas(final PagoM pago){
		final List<Venta> ventas=new ArrayList<Venta>();
		for(Pago p:pago.getPagos()){
			if(p.getVenta()!=null)
				ventas.add(p.getVenta());
		}
		return ventas;
	}

}
