package com.luxsoft.siipap.cxc.utils;

import ca.odell.glazedlists.matchers.Matcher;

import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Matches facturas que puede requerir nota de credito por descuento
 * 
 * @author Ruben Cancino
 *
 */
public class FacturasParaDescuento extends CheckBoxSelector<Venta>{

	@Override
	protected Matcher<Venta> getSelectMatcher(Object... obj) {		
		return new VentasParaDescuentoMatcher();
	}
	
	public static class VentasParaDescuentoMatcher implements Matcher<Venta>{

		public boolean matches(Venta item) {
			if(item.getSaldo().abs().doubleValue()>0
					&& item.isProvisionable()
						&& item.getDescuentos()==0
						 	&& item.getPagos()!=0)
				return true;
			return false;
		}
		
	}

}
