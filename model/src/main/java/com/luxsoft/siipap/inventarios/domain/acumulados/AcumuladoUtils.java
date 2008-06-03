package com.luxsoft.siipap.inventarios.domain.acumulados;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.luxsoft.siipap.domain.Periodo;

public class AcumuladoUtils {
	
	
	protected static DateFormat dateFormat =new SimpleDateFormat("MM/yyyy");
	
	/**
	 * Convierte a <code>String</code> un <code>Periodo</code> a la forma adecuada
	 * de los acumulados usando un <code>SimpleDateFormat</code> con formato es MM/YYYY
	 * 
	 * @param periodo
	 * @return
	 */
	public static String format(final Periodo periodo){
		return format(periodo.getFechaInicial());
	}
	
	/**
	 * Convierte a <code>String</code> un <code>Date</code> a la forma adecuada
	 * de los acumulados usando un <code>SimpleDateFormat</code> con formato es MM/YYYY
	 * 
	 * @param date
	 * @return
	 */
	public static String format(final Date date){
		return dateFormat.format(date);
	}
	
	/**
	 * Convierte a <code>Periodo</code> un <code>String</code> a la forma adecuada
	 * de los acumulados usando un <code>SimpleDateFormat</code> con formato es MM/YYYY
	 * 
	 * @param periodo
	 * @return
	 */
	public static Periodo parse(final String periodo){		
		return Periodo.getPeriodo(periodo,dateFormat);
	}

}
