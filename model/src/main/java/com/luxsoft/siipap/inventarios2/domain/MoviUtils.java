package com.luxsoft.siipap.inventarios2.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.luxsoft.siipap.domain.Articulo;

public class MoviUtils {
	
	
	public static Movi generarMovimiento(
			final Articulo a,final BigDecimal cantidad,final Concepto c,final Long origen,final int sucursal){
		Movi m=new Movi();
		m.setArticulo(a);
		m.setClave(a.getClave());
		m.setDescripcion(a.getDescripcion1());
		m.setConcepto(c.getClave());
		m.setTipo(c.getTipo());
		m.setFecha(new Date());
		m.setKilos(a.getKilos());
		m.setOrigen(origen);
		m.setSucursal(sucursal);
		m.setUnidad(a.getUnidad().getClave());
		m.setUnixuni(a.getUnidad().getCantidad());
		
		return m;
	}

}
