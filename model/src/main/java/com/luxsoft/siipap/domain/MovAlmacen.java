package com.luxsoft.siipap.domain;

import java.util.ArrayList;
import java.util.List;

public enum MovAlmacen{
		
AJA("AJA","AJA"),
AJU("AJU","AJU"),
CIM("CIM","CIM"),
CIS("CIS","CIS"),
COM("COM","COM"),
DEC("DEC","DEC"),
INI("INI","INI"),
MER("MER","MER"),
OIM("OIM","OIM"),
RAU("RAU","RAU"),
REC("REC","REC"),
REF("REF","REF"),
REM("REM","REM"),
RMC("RMC","RMC"),
RMD("RMD","RMD"),
TPE("TPE","TPE"),
TPM("TPM","TPM"),
TPS("TPS","TPS"),
TRS("TRS","TRS"),
TRV("TRV","TRV"),
VIR("VIR","VIR"),
XCI("XCI","XCI"),
XCM("XCM","XCM"),
XCO("XCO","XCO"),
XME("XME","XME"),
XOI("XOI","XOI"),
XRA("XRA","XRA"),
XRC("XRC","XRC"),
XRE("XRE","XRE"),
XRF("XRF","XRF"),
XRM("XRM","XRM"),
XTR("XTR","XTR"),
XTV("XTV","XTV"),
XVI("XVI","XVI")
;
		private final String descripcion;
		private final String numero;
		
		private MovAlmacen(final String descripcion, final String numero) {
			this.descripcion = descripcion;
			this.numero = numero;
		}
		
		public String toString(){
			return descripcion;
		}
		
		public String getNumero(){
			return numero;
		}
		
		public Integer[] todos(){
			return new Integer[]{1,4,6,7,8};
		}
		
		public static List<MovAlmacen> getOrder1(){
			ArrayList<MovAlmacen> l=new ArrayList<MovAlmacen>();
			for(MovAlmacen c:values()){			
				l.add(c);
			}
			return l;
		}
		
		public static MovAlmacen getOrder1(String id){
			for(MovAlmacen c:values()){
				if(c.getNumero()==id)
					return c;
			}
			return null;
		}
		}