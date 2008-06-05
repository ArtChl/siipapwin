package com.luxsoft.siipap.cxc.swing.cobranza;

import java.util.HashMap;
import java.util.Map;

import com.luxsoft.siipap.cxc.CXCReportes;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.swing.reports.ReportUtils;

public class CXCUtils {
	
	/**
	 * Ejecuta un reporte 
	 * 
	 * @param name
	 */
	public static final void ejecutarReporte(CXCReportes reporte){
		
	}
	
	/**
	 * Manda por correo un reporte en especifico para
	 * un cliente en particular. Proporciona un preview, lo manda usando
	 * el cliente de correo local
	 * 
	 * 
	 * @param reporte
	 * @param c
	 */
	public static final void email(final CXCReportes reporte,final Cliente c,final String saludo ){
		
	}
	
	public static void main(String[] args) {
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("NOTA_ID", new Long(92376));
		for(int i=0;i<10;i++){
			ReportUtils.printReport("reportes/NotaDeCredito.jasper", params,false );
		}
		
	}

}
