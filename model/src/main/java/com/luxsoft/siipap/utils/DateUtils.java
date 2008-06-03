package com.luxsoft.siipap.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Utilerias para el manejo de fechas
 * 
 * @author Ruben Cancino
 *
 */
public final class DateUtils {
	
	/**
	 * Encuentra la fecha mas proxima para el dia de la semana posterior a la fecha
	 * especificada, El rango de los dias es 1 al 7, el lunes es 1 y el domingo es 7
	 * Si el parametro inclusive es verdadero la fecha especificada cuenta para el calculo, es
	 * decir esta fecha puede ser la misma que la calculada, en caso de que el parametro sea
	 * falso la fecha regresada nunca sera igual a la fecha solicitada
	 * 
	 * @param fecha
	 * @param diaDeLaSemana
	 * @return
	 */
	public static final Date calcularFechaMasProxima(final Date fecha,final int diaDeLaSemana,final boolean inclusive){		
		Assert.isTrue(diaDeLaSemana>0 && diaDeLaSemana<=7,"El rando  es de 1 a 7");
		 Map<Integer, Integer> map=new HashMap<Integer, Integer>();
		 map.put(Calendar.MONDAY,1 );
		 map.put(Calendar.TUESDAY,2 );
		 map.put(Calendar.WEDNESDAY,3 );
		 map.put(Calendar.THURSDAY,4);
		 map.put(Calendar.FRIDAY,5 );
		 map.put(Calendar.SATURDAY,6 );
		 map.put(Calendar.SUNDAY,7 );
		 //Obtener el dia que corresponde al 5
		 if(diaDeLaSemana==7){
			 final Calendar c=Calendar.getInstance();
			 c.setTime(fecha);
			 c.add(Calendar.DATE, 1);
			 return c.getTime();
		 }
		 Calendar c=Calendar.getInstance();
		 c.setTime(fecha);
		 
		 Date revision=null;
		 
		 while(true){			 
			 revision=c.getTime();
			 int diaCalendario=c.get(Calendar.DAY_OF_WEEK);
			 int diaMapeo=map.get(diaCalendario);
			 if(inclusive){
				 if(diaMapeo==diaDeLaSemana  )				 
					 break;				 
			 }else{
				 if(diaMapeo==diaDeLaSemana && (!fecha.equals(revision)) )				 
					 break;
				 				 
			 }
			 c.add(Calendar.DATE, 1);
		 }
		 return revision;
	}
	
	/**
	 * Transforma un string en fecha usando el DateFormat suminstrado
	 * 
	 * @param df
	 * @param fecha
	 * @return null si el formato no es correcto
	 */
	public static Date obtenerFecha(final DateFormat df,final String fecha){
		try {
			Date fechaVenta=df.parse(fecha);
			return fechaVenta;
		} catch (Exception e) {
			return null;
		}		
	}
	
	/**
	 * Transforma un String en fecha, usando el formato dd/MM/yyyy
	 * 
	 * @param fecha
	 * @return la fecha o nulo si el formato es incurrecto
	 */
	public static Date obtenerFecha(final String fecha){
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		return obtenerFecha(df, fecha);
		 
	}
	
	/**
	 * Transforma la fecha en String a un formato de tipo dd/MM/yyyy
	 * 
	 * @param date
	 * @return
	 */
	public static String format(final Date date){
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		return df.format(date);
	}
	
	/**
	 * Regresa el numer de dias entre 2 fechas
	 * 
	 * @param ini
	 * @param fin
	 * @return
	 */
	public static int obtenerIntervaloEnDias(final Date ini,final Date fin){
		long time=fin.getTime()-ini.getTime();
		return (int)(time/(86400*1000));
	}
	
	
	public static Date truncate(Date date, int field) {
		return org.apache.commons.lang.time.DateUtils.truncate(date, field);
	}

}
