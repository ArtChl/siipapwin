package com.luxsoft.siipap.swing.reports;

import java.util.HashMap;
import java.util.Map;

/**
 * Facade para el uso de Rerportes 
 * 
 * @author Ruben Cancino
 *
 */
public final class ReportUtils {
	
	private final static ReportManagerImpl rmanager;
	
	static{
		rmanager=new ReportManagerImpl();
	}
	
	public static ReportManager getManager(){		
		return rmanager;
	}
	
	/**
	 * Ejecuta el reporte mostrandolo en panatalla
	 * 
	 * @param path
	 * @param params
	 */
	public static void viewReport(final String path,Map<String,Object> params){
		if(params==null)params=new HashMap<String, Object>();
		rmanager.showInDialog(path, params);
	}
	
	/**
	 * Ejecuta el reporte y lo imprime sin poderlo ver en panatalla pero con la posibilidad de
	 * configurar la impresora
	 * 
	 * @param path
	 * @param params
	 * @param preview
	 */
	public static void printReport(final String path,Map<String,Object> params,boolean preview){
		if(params==null)
			params=new HashMap<String, Object>();
		if(preview)			
			rmanager.printReport(path, params,preview);
		else{
			rmanager.printReport(path, params,preview);
		}
	}
	
	public static void main(String[] args) {
		//printReport("reportes/CobranzaCredito.jasper", null, true);
		//viewReport("reportes/CobranzaCredito.jasper", null);
		viewReport("reportes/RecepcionDeFacturas.jasper", null);
	}

}
