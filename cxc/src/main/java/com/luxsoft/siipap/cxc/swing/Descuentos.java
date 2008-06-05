package com.luxsoft.siipap.cxc.swing;

import org.springframework.util.StringUtils;

public enum Descuentos {
	
	VOLUMEN,
	CLIENTE,
	ARTICULO,
	VENTA;
	
	public String toString(){
		return " Descuento por "+StringUtils.capitalize(name().toLowerCase());
	}

}
