package com.luxsoft.siipap.cxc.swing.cobranza;

import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.ventas.domain.Venta;

public class PagosFactory {
	
	
	
	
	public static Pago getPago(final Venta v){
		Pago p=new Pago();
		p.setVenta(v);
		p.setNumero(v.getNumero());
		p.setTipoDocto(v.getTipo());		
		p.setCliente(v.getCliente());
		p.setClave(v.getClave());
		p.setOrigen(v.getOrigen());		
		v.agregarPago(p);		
		return p;
	}
	
	public static Pago getPagoOtrosProductosAFavor(final Pago p){		
		p.setFormaDePago(FormaDePago.U.getId());
		p.setDescFormaDePago(FormaDePago.U.getDesc());
		return p;
	}
	
	public static Pago getPagoOtrosProductosDeMenos(final Pago p){		
		p.setFormaDePago(FormaDePago.D.getId());
		p.setDescFormaDePago(FormaDePago.D.getDesc());
		p.setFormaDePago2(FormaDePago.D);
		return p;
	}

}
