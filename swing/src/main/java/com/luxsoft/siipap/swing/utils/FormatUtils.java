package com.luxsoft.siipap.swing.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import com.luxsoft.siipap.utils.DateUtils;

/**
 * Utilerias para crear Formatters
 * 
 * @author Ruben Cancino
 *
 */
public final class FormatUtils {
	
	/**
	 * FormatterFactory para porcentajes
	 * 
	 * @return
	 */
	public static AbstractFormatterFactory getPorcentageFormatterFactory(){
		
		NumberFormat nf=NumberFormat.getPercentInstance();
		//DecimalFormat nf=new DecimalFormat();
		((DecimalFormat)nf).setMultiplier(1);
		nf.setMaximumFractionDigits(2);
		nf.setMaximumIntegerDigits(2);
		NumberFormatter nff=new NumberFormatter(nf);
		nff.setCommitsOnValidEdit(true);
		
		NumberFormat ef=NumberFormat.getInstance();
		ef.setMaximumFractionDigits(2);
		ef.setMaximumIntegerDigits(2);
		
		NumberFormatter defaultFormatter=new NumberFormatter(nf);
		NumberFormatter editFormatter=new NumberFormatter(ef);
		editFormatter.setOverwriteMode(true);
		
		AbstractFormatterFactory factory=new DefaultFormatterFactory(defaultFormatter,defaultFormatter,editFormatter);
		return factory;
	}
	
	public static AbstractFormatterFactory getMoneyFormatterFactory(){
		
		final NumberFormat nf=NumberFormat.getNumberInstance();		
		((DecimalFormat)nf).setMultiplier(1);
		nf.setMaximumFractionDigits(2);	
		
		//NumberFormatter nff=new NumberFormatter(nf);
		//nff.setCommitsOnValidEdit(true);
		//nff.setValueClass(Number.class);
		final NumberFormat ef=NumberFormat.getNumberInstance();
		ef.setMaximumFractionDigits(2);
		//ef.setMaximumIntegerDigits(2);
		ef.setMinimumFractionDigits(2);
		
		final NumberFormatter defaultFormatter=new NumberFormatter(nf);
		
		final NumberFormatter editFormatter=new NumberFormatter(ef);
		//editFormatter.setOverwriteMode(true);
		
		AbstractFormatterFactory factory=new DefaultFormatterFactory(defaultFormatter,defaultFormatter,editFormatter);
		return factory;
	}
	
	public static AbstractFormatterFactory getBigDecimalMoneyFormatterFactory(){
		
		final NumberFormat nf=NumberFormat.getCurrencyInstance();		
		((DecimalFormat)nf).setMultiplier(1);
		nf.setMaximumFractionDigits(2);		
		NumberFormatter nff=new NumberFormatter(nf);
		nff.setCommitsOnValidEdit(true);
		nff.setValueClass(Double.class);
		final NumberFormat ef=NumberFormat.getInstance();
		ef.setMaximumFractionDigits(2);
		ef.setMinimumFractionDigits(2);
		final NumberFormatter defaultFormatter=new NumberFormatter(nf);
		
		final NumberFormatter editFormatter=new NumberFormatter(ef);
		editFormatter.setOverwriteMode(true);
		
		AbstractFormatterFactory factory=new DefaultFormatterFactory(defaultFormatter,defaultFormatter,editFormatter);
		return factory;
	}
	
	
	/**
	 * Formatter apropiado para valores de tipo Long
	 * 
	 * @param grouping
	 * @return
	 */
	public static NumberFormatter getLongFormatter(boolean grouping){
		final NumberFormat nf=NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(false);
		final NumberFormatter formatter=new NumberFormatter(nf);
		formatter.setValueClass(Long.class);
		return formatter;
	}
	
	
	public static AbstractFormatterFactory getBasicDateFormatterFactory(){
		
		final DateFormat editFormat=new SimpleDateFormat("dd/MM/yyyy");
		editFormat.setLenient(false);		
		final DateFormatter editFormatter=new DateFormatter(editFormat){

			public Object stringToValue(String text) throws ParseException {
		        if (text == null || text.trim().length() == 0) {
		            return null;
		        }
		        else
		        	return super.stringToValue(text);
			}

			
		};
		editFormatter.setAllowsInvalid(true);		
		editFormatter.setMinimum(DateUtils.obtenerFecha("01/01/2006"));
		
		final DateFormat defaultFormat=new SimpleDateFormat("dd/MM/yyyy");
		//defaultFormat.setLenient(false);
		//editFormatter.setAllowsInvalid(false);
		//editFormatter.setMinimum(DateUtils.obtenerFecha("01/01/2006"));
		
		final DateFormatter defaultFormatter=new DateFormatter(defaultFormat);
	
	
		
		AbstractFormatterFactory factory=new DefaultFormatterFactory(defaultFormatter,defaultFormatter,editFormatter);
		return factory;
		
	}
	

}
