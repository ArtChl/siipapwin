package com.luxsoft.siipap.inventarios.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TipoDeMovimiento {
	
	COM("COM","ENTRADA POR COMPRA"),
	TPE("TPE","TRASPASO DE ENTRADA"),
	FAC("FAC","OPERACIONES DE VENTA (FACTURACION)"),
	TPS("TPS","TRASPASO DE SALIDA"),
	AJU("AJU","AJU"),
	AJA("AJA","AJUSTE SOBRE EXISTENCIA EN ALMACEN"),
	INI("INI",""),
	CIM("CIM","CORRECCION INVENTARIO MENSUAL"),
	CIS("CIS","CONSUMO INTERNO (VALES ADMON."),	
	DEC("DEC","DEVOLUCIONES A PROVEEDOR"),	
	MER("MER","	MERMA"),
	OIM("OIM","OTROS INGRESOS DE MERCANCIA"),
	RAU("RAU","RECLASIFICACION AUTORIZADA"),
	REC("REC","RECLASIFICACION DE MERCANCIA"),
	REF("REF","RECLASIF. SOBRE FACTURAS"),
	REM("REM","REMISIONES DE VENTA"),
	RMC("RMC","REPOSISICION DE MATERIAL A CLIENTE"),
	RMD("RMD","DEVOLUCION SOBRE VENTAS"),
	SOL("SOL","SOLICITUD DE MERCANCIA A SUCURSAL"),
	TRS("TRS","TRANSFORMACIONES"),
	TRV("TRV","TRANSF. P/TARJETAS DE PRESENTACION"),
	VIR("VIR","INGRESO DE VIRUTA"),
	XCO("XCO","BAJA -> COM"),
	XRM("XRM","BAJA -> RMD"),
	XOI("XOI","OTROS EGRESOS DE MERCANCIA"),
	XRE("XRE","Baja de REM"),
	XCI("XCI","Baja de SIS");
	
	private String clave;
	private String descripcion;
	
	private TipoDeMovimiento(String clave,String descripcion){
		this.clave=clave;
		this.descripcion=descripcion;
	}
	
	public String getClave() {
		return clave;
	}		
	public String getDescripcion() {
		return descripcion;
	}
	
	public String toString(){
		return getClave()+" - "+getDescripcion();
		//return getClave();	
	}
	
	
	public static Collection<TipoDeMovimiento> collection(){
		List<TipoDeMovimiento> list=new ArrayList<TipoDeMovimiento>();
		for(TipoDeMovimiento t:values()){
			list.add(t);
		}
		return list;
	}
	
	public static Map<String,TipoDeMovimiento> toMap(){
		Map<String,TipoDeMovimiento> map=new HashMap<String,TipoDeMovimiento>();
		for(TipoDeMovimiento tipo:collection()){
			map.put(tipo.getClave(),tipo);
		}
		return map;
	}
	
	

	
}
