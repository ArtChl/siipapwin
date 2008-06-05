package com.luxsoft.siipap.inventarios.model;


/**
 * Interfaz para injectar totales al bean {@link TotalizadorDeMaquila}
 * 
 * @author Ruben Cancino
 *
 */
public interface Totalizable {
	
	public void totalizar(final TotalizadorDeMaquila totalizador);

}
