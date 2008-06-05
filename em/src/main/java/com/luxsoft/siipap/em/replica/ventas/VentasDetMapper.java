package com.luxsoft.siipap.em.replica.ventas;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.luxsoft.siipap.em.replica.DefaultMapper;
import com.luxsoft.siipap.ventas.domain.VentaDet;

public class VentasDetMapper extends DefaultMapper{

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		VentaDet det=(VentaDet)super.mapRow(rs, rowNum);
		
		double cantidad=det.getCantidad()/det.getFactorDeConversionUnitaria();
		det.setCantidad(cantidad);		
							
		double importeB=det.getPrecioLista()*det.getCantidad();
		det.setImporteBruto(importeB);
		double importeN=det.getPrecioFacturado()*det.getCantidad();
		det.setImporteNeto(importeN);
		
		///Calculamos el precio real
		//double descF=v.getDescuentoFacturado()/100;
		//double impDesF=det.getPrecioFacturado()*descF;
		//double pReal=det.getPrecioFacturado()-impDesF;
		//det.setPrecioReal(pReal);
		
		//Importe real
		det.setImporteReal(det.getPrecioReal()*det.getCantidad());
		
		return det;
	}

}
