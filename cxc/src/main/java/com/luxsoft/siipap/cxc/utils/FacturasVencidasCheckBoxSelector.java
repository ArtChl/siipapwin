package com.luxsoft.siipap.cxc.utils;

import ca.odell.glazedlists.matchers.Matcher;


import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * CheckBoxSelector (MatcherEditor) para seleccionar facturas vencidas 
 * 
 * @author Ruben Cancino
 *
 */
public  class FacturasVencidasCheckBoxSelector extends CheckBoxSelector<Venta>{
	

	@Override
	protected Matcher<Venta> getSelectMatcher(Object... obj) {
		return new VencidasMatcher();
	}
	
	private class VencidasMatcher implements Matcher<Venta>{
		
		
		public VencidasMatcher(){
			
		}
		public boolean matches(Venta item) {				
			return item.getAtraso()>0;
		}
	}
	
}