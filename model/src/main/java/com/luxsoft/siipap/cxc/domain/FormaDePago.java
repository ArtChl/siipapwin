package com.luxsoft.siipap.cxc.domain;

import com.luxsoft.siipap.utils.EnumId;

public enum FormaDePago implements EnumId{
	
	E("E","Efectivo"),
	H("H","Cheque"),
	N("N","Transferencia"),
	T("T","Nota de Crédito"),
	C("C","Tarjeta (Cre)"),
	B("B","Tarjeta Amex"),
	Q("Q","Tarjeta (Deb)"),
	S("S","Saldo a favor"),
	U("U","Otros productos "),
	P("P","Check plus (CON)"),
	X("X","Especie"),
	R("R","Incobrabilidad"),
	D("D","Diferencias"),
	F("F","Dif Camnbiaria"),
	O("O","Orden (Deposito)"),
	A("A","Anticipo"),
	M("M","RMD Camioneta"),
	W("W","RMD Mostrador"),
	Z("Z","Depuración (CAM)"),
	Y("Y","Check plus (CRE)")
	;
	
	private String id;	
	private String desc;
	
	private FormaDePago(String id,String desc){
		this.id=id;	
		this.desc=desc;
	}
	
	public String getId() {
		return id;
	}
	public String getDesc(){
		return desc;
	}

	public String toString(){
		return getDesc();
	}
	
	
}
