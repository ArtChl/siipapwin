package com.luxsoft.siipap.cxc;

import org.springframework.util.StringUtils;

/**
 * Enumeracion de permisos  a nivel de dominio del modulo de CXC
 * 
 * @author Ruben Cancino
 *
 */
public enum CXCPermiso {
	
	EliminarNotasUsadasEnPagos("Eliminra Notas de Credito usadas en pago"),
	CondonarCargos("Condonar cargos a clientes de credito")
	;
	
	
	private final String desc;
	
	private CXCPermiso(final String desc){
		this.desc=desc;
	}

	public String getDesc() {
		return desc;
	}
	
	public String getId(){
		return StringUtils.uncapitalize(name());
	}

}
