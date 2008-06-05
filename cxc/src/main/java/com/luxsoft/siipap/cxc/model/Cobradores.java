package com.luxsoft.siipap.cxc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeracion temporal para el control de cobradores
 * sera sustituida por un catalogo
 * 
 * TODO Implementar un catalogo
 * 
 * @author Ruben Cancino
 *
 */
public enum Cobradores {
	
	TODOS("Todos",0),
	Directo("Directo (Casa)",1),
	CUATRO("José Enrique Escalante Martínez",4),
	SEIS("Ivan Estrada Maya",6),
	SIETE("E. Antonio Escalante Maya",7),
	OCHO("Cesar Escalante Maya",8),	
	;
	
	private final String descripcion;
	private final int numero;
	
	private Cobradores(final String descripcion, final int numero) {
		this.descripcion = descripcion;
		this.numero = numero;
	}
	
	public String toString(){
		return descripcion+" ("+getNumero()+")";
	}
	
	public int getNumero(){
		return numero;
	}
	
	public static Integer[] todos(){
		return new Integer[]{0,1,4,6,7,8};
	}
	
	public static List<Cobradores> getCobradores(){
		ArrayList<Cobradores> l=new ArrayList<Cobradores>();
		for(Cobradores c:values()){
			if(c.equals(TODOS))
				continue;
			l.add(c);
		}
		return l;
	}
	
	public static Cobradores getCobrador(int id){
		for(Cobradores c:values()){
			if(c.getNumero()==id)
				return c;
		}
		return null;
	}

}
