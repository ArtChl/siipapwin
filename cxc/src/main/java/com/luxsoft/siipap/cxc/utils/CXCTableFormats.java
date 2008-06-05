package com.luxsoft.siipap.cxc.utils;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.impl.beans.BeanTableFormat;

import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * {@link TableFormat} mas comunes en CxC
 * 
 * @author Ruben Cancino
 *
 */
public class CXCTableFormats {
	
	
	public static TableFormat<Venta> getVentasCreTF(){
		final String[] props={"id","sucursal","tipo","numero","numeroFiscal","fecha","vencimiento","atraso","credito.reprogramarPago","total","descuentoPactado","cargo","descuentos","bonificaciones","pagos","saldo"};
		final String[] labels={"Id","Sucursal","Tipo","Numero","No Fiscal","Fecha","Vencimiento","Atraso","Prox Pago","Total","Desc","Cargo","Desc (A)","Bonificaciones","Pagos","Saldo"};
		final TableFormat<Venta> tf=GlazedLists.tableFormat(Venta.class,props,labels);
		return tf;
	}
	
	public static TableFormat<ClienteCredito> getClienteCreditoTf(){
		//final String[] props={"clave","nombre","limite","saldo","saldoVencido","ultimoPago","comentarioCxc","dia_revision","dia_pago","cobrador"};
		//final String[] cols={"Cliente","Nombre","Límite","Saldo","S.Vencido","U.Pago","Comentario","Día Rev","Día Pago","Cobrador"};
		final String[] props={"clave","nombre","vendedor","saldoVencido","saldo","pagos","ultimoPago","atrasoMaximo","facturasVencidas","ultimaVenta","ventaNeta","vendedor","comentarioCxc"};
		final String[] cols={"clave","nombre","vendedor","saldoVencido","saldo","pagos","ultimoPago","atrasoMaximo","facturasVencidas","ultimaVenta","ventaNeta","vendedor","Comentario"};
		final TableFormat<ClienteCredito> tf=GlazedLists.tableFormat(ClienteCredito.class,props,cols);		
		return tf;
	}
	
	public static TableFormat<Pago> getPagoConVentaTF(){
		final String[] props={
				"venta.numero"
				,"venta.numeroFiscal"
				//,"tipoDocto"
				//,"venta.vencimiento"
				//,"venta.atraso"
				,"venta.total"
				,"venta.saldo"
				,"venta.descuentoPactado"
				,"venta.cargo"
				,"descuento"
				,"porPagar"
				,"importe"
				,"venta.descuentos"
				};
		final String[] labels={
				"Fac"
				,"N.Fiscal"
				//,"T"
				//,"Vence"
				//,"Atraso"
				,"Total","Saldo","Desc (%)","Cgo"
				,"Desc ($)","A Pagar","Pago","Desc Aplic"
				};
		
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class,props,labels);
		return tf;
	}
	
	public static TableFormat<Pago> getPagoConNotaTF(){
		final String[] props={
				"numero"
				,"tipoDocto"
				,"nota.total"
				,"nota.saldo"
				,"importe"
				
				};
		final String[] labels={
				"Dcto"
				,"T"
				,"Total"
				,"Saldo"
				,"Pago"
				};
		
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class,props,labels);
		return tf;
	}
	
	public static TableFormat<PagoM> getPagoMTF(){
		final String[] props={"label","id"
				,"clave"
				,"fecha"
				,"formaDePago","tipoDeDocumento","referencia","comentario","importe","disponible","year","mes"
				};
		final String[] cols={"Tipo","id"
				,"clave"
				,"fecha"
				,"F.P","Tip","referencia","Comentario","Importe","disponible","Año","Mes"
				};
		final TableFormat<PagoM> tf=GlazedLists.tableFormat(PagoM.class,props, cols);
		return tf;
	}
	
	/**
	 * Regresa un {@link TableFormat} estandar para {@link NotaDeCredito}
	 * 
	 * @return
	 */
	public static TableFormat<NotaDeCredito> getNotaDeCreditoTF(){
		final String[] props={"id","numero","fecha","serie","tipo","totalAsMoneda","pagosUnificados","saldoUnificado","aplicable","comentario","impreso"};
		final String[] cols={"Id","Numero","Fecha","Serie","Tipo","Total","Pagos","Saldo","Aplicable","Comentario","F.Impresión"};
		TableFormat<NotaDeCredito> tf=GlazedLists.tableFormat(props, cols);
		return tf;
	}
	
	public static TableFormat<NotasDeCreditoDet> getNotaDeCreditoDetTF(){
		final String[] props={"clave","factura.id","nota.numero","nota.fecha","numDocumento","fechaDocumento","tipoDocumento","descuento","importe"};
		final String[] names={"Cliente","FacturaId","Nota","Fecha","Docto","Fecha-Docto","Tipo-Docto","%","Importe"};
		return GlazedLists.tableFormat(NotasDeCreditoDet.class, props,names);
	}
	
	public static TableFormat<Pago> getNotaDeCargoTF(){
		final String[] props={
				"nota.id"
				,"nota.numero"
				,"nota.fecha"
				,"nota.total"
				,"nota.saldoDelCargoEnMoneda"
				,"importe"
				,"nota.comentario"
				
				};
		final String[] labels={
				"Id"
				,"Dcto"
				,"Fecha"
				,"Total"
				,"Saldo"
				,"Pago"
				,"Comentario"
				};
		final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class,props,labels);
		return tf;
	}
	
	public static TableFormat<Devolucion> getDevolucionTF(){
		final String[] props=new String[]{"id","numero","sucursal","fecha","tipoVenta","serieVenta","numeroVenta","venta.id","importe","mes","year"};
		final String[] names=new String[]{"Id","Numero","Sucursal","Fecha","T","S","Factura","VentaId","Importe","Mes","Año"};
		return GlazedLists.tableFormat(Devolucion.class,props,names);
	}
	
	public static TableFormat<Venta> getParaRevisionTF(){
		final String[] props={
				"id"
				,"sucursal"
				,"clave"
				,"nombre"
				,"tipo"
				,"numero"
				,"numeroFiscal"
				,"fecha"
				,"total"
				,"credito.revisada"
				,"credito.revision"
				,"credito.recibidaCXC"
				,"credito.fechaRecepcionCXC"
				,"credito.comentario"
				,"credito.vencimiento"				
				,"credito.fechaRevisionCxc"
				,"credito.comentarioRepPago"
				,"credito.reprogramarPago"
				};
		
		final String[] labels={
				"Id"
				,"Sucursal"
				,"Cliente"
				,"Nombre"
				,"Tipo"
				,"Numero"		
				,"N.Fiscal"				
				,"Fecha"	
				,"Total"
				,"Revisada"
				,"P/Rev"
				,"Recibida CXC"
				,"F.Rec CXC"
				,"Comentario Rec. Suc."
				,"Vencimiento"				
				,"Rev CXC"
				,"Comentario Rev/Cob"
				,"F. Pago"
				};		
		final boolean[] editables={
				  false,false,false,false,false
				 ,false,false,false,false,true
				 ,false,true,false,false,false
				 ,false,false,false
				};
		//final TableFormat<Venta> tf=GlazedLists.tableFormat(Venta.class,props,labels,editables);
		
		final TableFormat<Venta> tf=new BeanTableFormat<Venta>(Venta.class,props,labels,editables){

			@Override
			public boolean isEditable(Venta baseObject, int column) {
				if(column==11){
					if(baseObject.getCredito()==null) return false;
					boolean val=baseObject.getCredito().getFechaRecepcionCXC()==null;					
					return val;						
				}else
					return super.isEditable(baseObject, column);
			}			
		};		
		return tf;
	}
	
	/**
	 * Regresa un {@link TableFormat} para N.C por descuento financiero
	 * 
	 * @return
	 */
	public static TableFormat<NotasDeCreditoDet> getNCTableFormatParaDF(){
		final String[] props={
				"factura.id"
				,"numDocumento"
				,"fechaDocumento"
				,"factura.vencimientoFinanciero"				
				,"factura.total"				
				,"factura.descuentoPactado"
				,"factura.saldoEstimadoSinCargo"
				,"descuento"
				,"importe"};
		final String[] names={
				"FacId","Factura","Fecha (F)"
				,"Vto "
				,"Total (F)        "				
				,"Descuento       "
				,"Saldo            "
				,"Desc F. "
				,"Importe          "};
		final boolean[] edits={false,false,false,false,false,false,false,true,false};
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class, props,names,edits);
		return tf;
	}
	/**
	 * Regresa un {@link TableFormat} para N.C por bonificacion
	 * 
	 * @return
	 */
	public static TableFormat<NotasDeCreditoDet> getNCTableFormatParaBonificacion(){
		final String[] props={
				"factura.id","numDocumento","fechaDocumento","tipoDocumento"
				,"factura.total"
				,"factura.bonificaciones","factura.descuentos"
				,"factura.totalSinDevoluciones"
				,"descuento"
				,"importe"};
		final String[] names={
				"FacId","Factura","Fecha (F)","T"
				,"Total (F)        "
				,"Bonificaciones   "
				,"Descuentos       "
				,"Total S/Dev      "
				,"%"
				,"Importe          "};
		final boolean[] edits={false,false,false,false
				,false
				,false,false
				,false
				,true
				,false};
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class, props,names,edits);
		return tf;
	}
	
	/**
	 * Regresa un {@link TableFormat} para N.C por Devolución
	 * 
	 * @return
	 */
	public static TableFormat<DevolucionDet> getNCTableFormatParaDevolucion(){
		final String[] props={
				"id"
				,"articulo.clave"
				,"articulo.descripcion1"
				,"articulo.kilos"
				,"cantidad"
				,"ventaDet.precioFacturado"
				,"importe"
				};
		final String[] names={
				"Id"
				,"Artículo"
				,"Descripción"
				,"Kilos"
				,"Cantidad"
				,"Precio   "
				,"importe  "
				};
		final TableFormat<DevolucionDet> tf=GlazedLists.tableFormat(DevolucionDet.class, props,names);
		return tf;
	}

}
