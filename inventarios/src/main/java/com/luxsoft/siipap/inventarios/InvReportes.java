package com.luxsoft.siipap.inventarios;

import org.springframework.util.StringUtils;

public enum InvReportes {
	
	DiarioDeCobranza,
	RecepcionDeFacturas
	;
	
	
	
	public String toString(){
		return StringUtils.uncapitalize(name());
	}

}
