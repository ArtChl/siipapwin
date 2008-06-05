package com.luxsoft.siipap.em.replica.ventas;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.em.replica.ConverterUtils;
import com.luxsoft.siipap.ventas.domain.VentaDet;

public class AlmaceMapper implements RowMapper{

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		VentaDet det=new VentaDet();
		//det.setArticulo(s.getArticulo());
		det.setFactorDeConversionUnitaria(ConverterUtils.toInteger(rs.getObject("ALMUNIXUNI")));
		long canti=ConverterUtils.toLong(rs.getObject("ALMCANTI"));
		det.setCantidad(canti/det.getFactorDeConversionUnitaria());
		det.setClave(rs.getString("ALMARTIC"));
		det.setDescripcion(rs.getString("ALMNOMBR"));
		
		
		det.setFecha(rs.getDate("ALMFECHA"));					
		det.setNumero(ConverterUtils.toLong(rs.getObject("ALMNUMER")));					
		det.setRenglon(ConverterUtils.toInteger(rs.getObject("ALMRENGL")));
		det.setSerie(rs.getString("ALMSERIE"));
		det.setSucursal(ConverterUtils.toInteger(rs.getObject("ALMSUCUR")));
		det.setTipo(rs.getString("ALMTIPO"));
		det.setTipoFactura(rs.getString("ALMTIPFA"));
		det.setUnidad(rs.getString("ALMUNIDMED"));
		det.setKilos(ConverterUtils.toDouble(rs.getObject("ALMKILOS")));
		
		
		det.setPrecioFacturado(ConverterUtils.toDouble(rs.getObject("ALMPREFA")));
		det.setPrecioLista(ConverterUtils.toDouble(rs.getObject("ALMPRECI")));
		det.setFechaReal(rs.getDate("ALMFEREAL"));
		if(det.getSerie()==null)det.setSerie("X");
		if(det.getTipoFactura()==null)det.setTipoFactura("X");
		if(det.getSucursal()==null)det.setSucursal(0);
								
		//Actualizable desde otro commando 
		//TODO Incorporar en un metodo de Venta 
		
		/*
		///Calculamos el precio real
		double descF=v.getDescuentoFacturado()/100;
		double impDesF=det.getPrecioFacturado()*descF;
		double pReal=det.getPrecioFacturado()-impDesF;
		det.setPrecioReal(pReal);
		
		//Importe real
		det.setImporteReal(det.getPrecioReal()*det.getCantidad());
		logger.debug("\t\t Agregando partida: "+det);
		detalles++;
		**/
		return det;
	}

}
