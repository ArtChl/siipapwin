package com.luxsoft.siipap.domain;

public enum Unidades {
	
	PIEZA
	;
	
	public String toString(){
		if(this==PIEZA)
			return "PZA";
		return name();
	}

}
