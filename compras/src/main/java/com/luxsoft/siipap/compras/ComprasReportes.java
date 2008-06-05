package com.luxsoft.siipap.compras;

import org.springframework.util.StringUtils;

public enum ComprasReportes {
	
	DiarioDeCobranza,
	RecepcionDeFacturas
	;
	
	
	
	public String toString(){
		return StringUtils.uncapitalize(name());
	}

}
