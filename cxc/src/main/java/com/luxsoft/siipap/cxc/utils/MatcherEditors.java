package com.luxsoft.siipap.cxc.utils;


import ca.odell.glazedlists.matchers.Matcher;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * FactoryMethods para construir matchers de uso comun 
 * en CXC y Ventas
 * 
 * @author Ruben Cancino
 *
 */
public final class MatcherEditors {
	
	public static final CheckBoxSelector<Venta> createFacturasParaNCDescuento(){
		return new FacturasParaDescuento();
	}
	
	/**
	 * Crea un CheckBoxSelector(MatcherEditor) para facturas con saldo a la fecha actual
	 * 
	 * @return
	 */
	public static final CheckBoxSelector<Venta> createFacturasPendientesSelector(){
		return new FacturasConSaldoSelector();
	}
	
	/**
	 * Crea un  CheckBoxSelector(MatcherEditor) para facturas vencidas con respecto a la fecha actual
	 * 
	 * @return
	 */
	public static final CheckBoxSelector<Venta> createFacturasVencidasSelector(){
		return new FacturasVencidasCheckBoxSelector();
	};
	
	public static final CheckBoxSelector<Venta> createFacturasPorVencerSelector(){
		return new FacturasPorVencerCheckBoxSelector();
	}
	
	public static final CheckBoxSelector<Venta> createFacturasPagadasSelector(){
		return new FacturasPagadasSelector();
	}
	
	public static final FacturasPorFechaSelector createFacturasPorFechaSelector(final String fechaProperty){
		return new FacturasPorFechaSelector(fechaProperty);
	}
	
	public static final PagosPorFechaSelector createPagosPorFechaSelector(final String fechaProperty){
		return new PagosPorFechaSelector(fechaProperty);
	}
	
	public static final DateSelector createDateSelector(){
		return new DateSelector();
	}
	
	public static final DateSelector createDateSelector(boolean before){
		return new DateSelector(before);
	}
	
	/**
	 * {@link MatcherEditors} para seleccionar cargos
	 * 
	 * @return
	 */
	public static CheckBoxSelector<NotaDeCredito> createSelectorDeCargos(){
		return new CheckBoxSelector<NotaDeCredito>(){
			@Override
			protected Matcher<NotaDeCredito> getSelectMatcher(Object... obj) {
				return new CargoMatcher();
			}
			
			class CargoMatcher implements Matcher<NotaDeCredito>{

				public boolean matches(NotaDeCredito item) {
					return item.getSerie().equals("M");
				}
				
			}
			
		};
	}
	
	public static CheckBoxSelector<NotaDeCredito> createSelectorNotasConSaldo(){
		return new CheckBoxSelector<NotaDeCredito>(){
			@Override
			protected Matcher<NotaDeCredito> getSelectMatcher(Object... obj) {
				return new NCMatcher();
			}
			
			class NCMatcher implements Matcher<NotaDeCredito>{

				public boolean matches(NotaDeCredito item) {
					if(item.getSerie().equals("M"))
						return Math.abs(item.getSaldoDelCargo())>1;
					return Math.abs(item.getSaldo())>1;
				}
				
			}
			
		};
	}
	
	public static CheckBoxSelector<NotaDeCredito> createSelectorNotasSinSaldo(){
		return new CheckBoxSelector<NotaDeCredito>(){
			@Override
			protected Matcher<NotaDeCredito> getSelectMatcher(Object... obj) {
				return new NCMatcher();
			}
			
			class NCMatcher implements Matcher<NotaDeCredito>{

				public boolean matches(NotaDeCredito item) {
					if(item.getSerie().equals("M"))
						return Math.abs(item.getSaldoDelCargo())==0;
					return Math.abs(item.getSaldo())==0;
				}
				
			}
			
		};
	}

}
