package com.luxsoft.siipap.cxc.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;

public class Datos {
	
	
	public static List<Venta> ventasDePrueba(){
		try {
			String clave="U050008";
			String nombre="UNION DE CREDITO";
			Cliente c=new Cliente();
			c.setClave(clave);
			c.setNombre(nombre);
			c.setTipo_vencimiento("V");
			List<Venta> ventas=new ArrayList<Venta>();
			DateFormat df=new SimpleDateFormat("dd/MM/yy");
			Double[] montos={12345.25,45365.50,25368.00};
			Date[] fechas={df.parse("31/03/07"),df.parse("31/03/07"),df.parse("31/03/07")};
			Long[] numeros={13423l,13424l,13428l};
			
			for(int i=0;i<montos.length;i++){
				Venta v=new Venta();
				v.setId(new Long(i+1));
				v.setCliente(c);
				v.setDiaRevision(6);
				v.setCobrador(8);
				v.setDiaPago(6);
				v.setClave(clave);
				v.setSucursal(1);
				v.setNombre(nombre);
				v.setImporteBruto(CantidadMonetaria.pesos(montos[i]));
				v.setSubTotal(CantidadMonetaria.pesos(montos[i]));
				v.setTotal(v.getSubTotal().multiply(1.15));
				v.setFecha(fechas[i]);
				v.setNumero(numeros[i]);
				v.setNumeroFiscal(v.getNumero().intValue());
				v.setSaldo(v.getTotal().amount());
				v.setTipo("E");
				v.setOrigen("CRE");
				v.setVencimiento(calcularVencimiento(v.getFecha()));
				v.actualizarDatosDeCredito();
				
				Provision p=new Provision(v){

					@Override
					public double getDescuento1Real() {
						return 55;
					}

					@Override
					public double getDescuentoFinal() {						
						return 52;
					}
					
				};
				p.calcularProvision();
				ventas.add(v);				
				
			}
			return ventas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;		}
		
	}
	
	
	
	public static Date calcularVencimiento(Date d){
		Calendar c=Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, 30);
		return c.getTime();
	}
	
		
	public static Provision crearProvision(Venta v){
		Provision p=new Provision();
		p.setVenta(v);
		p.setImporte(v.getImporteBruto().multiply(.43).amount());
		p.actualizarCargo();
		
		return p;
	}
	
	

}
