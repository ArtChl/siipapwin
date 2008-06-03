package com.luxsoft.siipap.cxc.domain;

import com.luxsoft.siipap.utils.EnumId;

/**
 * Clasificacion de las diferencias en Pagos
 * 
 * @author Ruben Cancino
 *
 */
public enum ClasificacionDeDiferencias implements EnumId{
	
	Cambiara("F"),
	Saldo("U"),
	MenorAutorizado("D");
	
	private final String id;
	
	private ClasificacionDeDiferencias(String id){
		this.id=id;
	}

	public String getId() {
		return id;
	}
	
	

}
