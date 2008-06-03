/*
 * Created on 04-dic-2004
 *
 * by Propietario
 */
package com.luxsoft.siipap.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



/**
 * @author Ruben Cancino
 *
 * TODO Propietario has to write a description for SiipapDateUtils
 */
public class SiipapDateUtils {
    
    
    public static final DateFormat MX_DATEFORMAT=
        new SimpleDateFormat("dd/MM/yy");
    public static final DateFormat SIIPAP_DATEFORMAT=
        new SimpleDateFormat("yyyy/MM/dd");
    
    public static  synchronized int getYear(final Date date){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
    /**
     * Regresa el mes de una fecha pero basando el conteo de los mese
     * en 1 no en 0, ej: diciembre es el mes 12
     * @param date
     * @return
     */
    public static  synchronized int getMonth(final Date date){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH)+1;
    }
    
    /**
     * Regresa una fecha con el formato "dd/MM/yy"
     * @param date
     * @return
     */
    public static synchronized Date getMXDate(String date){
        try{
            return MX_DATEFORMAT.parse(date);
        }catch(ParseException pe){
            throw new RuntimeException("Formato de la fecha:" +date+ " " +
            		"es incorrecto se esperaba (dd/mm/yy)",pe);
        }
    }
    
    public static synchronized String getSiipapDate(final Date date){
        String s="#@fecha#";
        s=s.replaceFirst("@fecha",SIIPAP_DATEFORMAT.format(date));
        return s;
    }
    
    public static synchronized Date paresSiipapDate(final String date){
        try{
            return SIIPAP_DATEFORMAT.parse(date);
        }catch(ParseException pe){
            throw new RuntimeException("Formato de la fecha:" +date+ " " +
            		"es incorrecto se esperaba (yyyy/mm/dd)",pe);
        }
    }
    
    protected static DateFormat siipapTimeStamp=new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
    
    public static synchronized Date parseSiipapTimeStamp(final String s){
        try{
            return siipapTimeStamp.parse(s);
        }catch(ParseException pe){
            throw new RuntimeException("String Siipap TimeStamp incorrecto: "+s,pe);
        }
        
    }
    protected static final DateFormat SIIPAP_DBF_DATEFORMAT=new SimpleDateFormat("dd/MM/yyyy"); 
    
    public static synchronized Date parseSiipapTimeStamp(final Date fecha,final String hora){
        String f=SIIPAP_DBF_DATEFORMAT.format(fecha);
        return parseSiipapTimeStamp(f+":"+hora);
    }
    
    public static Date fechaAnterior(final Date d){
    	Calendar c=Calendar.getInstance();
    	c.setTime(d);
    	c.getTime();
    	c.add(Calendar.DATE,-1);
    	return c.getTime();
    }
    
    public static DateFormat[] getAvailabelDateFormats(){
    	SimpleDateFormat d1=new SimpleDateFormat("dd/MM/yy");
    	SimpleDateFormat d2=new SimpleDateFormat("dd/MM/yyyy");
    	return new DateFormat[]{d2,d1};
    }
    

}
