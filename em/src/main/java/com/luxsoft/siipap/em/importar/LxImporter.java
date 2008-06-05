package com.luxsoft.siipap.em.importar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.luxsoft.siipap.em.managers.EMServiceLocator;

/**
 * Applicacion de linea de commando para arrancar la importacion
 * automatica de datos
 * 
 * @author Ruben Cancino
 *
 */
public class LxImporter {
	
	public void start(){
		EMServiceLocator.instance().getTaskContext();
	}
	
	public void sincronizar(final Date fecha){
		System.out.println(fecha);
		EMServiceLocator.instance().getSincronizadorDeVentas().sinconizar(fecha);
	}
	
	
	
	public static void main(String[] args) throws Exception{
		
		if(args.length==0)
			new LxImporter().start();
		else{
			DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
			try {
				Date dia=df.parse(args[0]);
				
				new LxImporter().sincronizar(dia);
			} catch (ParseException pe) {
				System.out.println("El formato de la fecha debe ser: dd/MM/yyyy");
				throw pe;
			}
			
		}
	}
}
