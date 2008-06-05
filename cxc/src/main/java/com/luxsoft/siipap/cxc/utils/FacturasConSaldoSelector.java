package com.luxsoft.siipap.cxc.utils;

import ca.odell.glazedlists.matchers.Matcher;


import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * MatcherEditor para seleccionar facturas con saldo
 *  
 * @author Ruben Cancino
 *
 */
public class FacturasConSaldoSelector extends CheckBoxSelector<Venta>{
	
	
	@Override
	protected Matcher<Venta> getSelectMatcher(Object... obj) {
		return new ConSaldoMatcher();
	}
	
	private class ConSaldoMatcher implements Matcher<Venta>{
		public boolean matches(Venta item) {				
			return item.getSaldo().abs().doubleValue()>0;
		}			
	}

}