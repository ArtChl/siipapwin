package com.luxsoft.siipap.cxc.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.Assert;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.luxsoft.siipap.cxc.CXCReportes;
import com.luxsoft.siipap.cxc.chequed.SolicitudDeCargoPorChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ConceptoDeBonificacion;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.NotasFactory;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.model2.SolicitudDeNotaDeCargo2;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.siipap.swing.reports.ReportUtils;
import com.luxsoft.siipap.utils.ImporteALetra;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

public class NotasUtils extends NotasFactory{
	
	/**
	 * Numero maximo de partidas por nota de credito normal
	 */
	public static final int MAX_PARTIDAS=10;
	
	/**
	 * Numero maximo de detalle por devolucion 
	 * aplicables por nota de credito
	 * 
	 */
	public static final int MAX_DEVO_PARTIDAS=8;
	
	/**
	 * Crea una nota de credito para una y solo una venta
	 * 
	 * @param v
	 * @return
	 */
	public static NotaDeCredito getNota(final Venta v){
		NotaDeCredito nc=new NotaDeCredito();
		nc.setCliente(v.getCliente());
		nc.setClave(v.getClave());
		nc.setFecha(new Date());
		nc.setOrigen(v.getOrigen());
		return nc;
	}
	
	
	
	/**
	 * FactoryMethod para crear un Bean NotasDeCreditoDet a partir de una venta sin importe
	 * 
	 * @param v
	 * @param importe
	 * @return
	 */
	public static NotasDeCreditoDet getNotaDet(final Venta v){
		NotasDeCreditoDet det=new NotasDeCreditoDet();
		det.setClave(v.getClave());
		det.setFactura(v);
		det.setFecha(new Date());
		det.setFechaDocumento(v.getFecha());
		det.setNumDocumento(v.getNumero());
		det.setOrigen(v.getOrigen());
		det.setSerieDocumento(v.getSerie());
		det.setSucDocumento(v.getSucursal());
		det.setTipoDocumento(v.getTipo());		
		return det;
	}
	
	/**
	 * Crea una lista de notas de credito a partir de una lista de
	 * NotasDeCreditoDet atendiendo a la regla que las notas tiene 
	 * un numero limintado de partidas, MAX_PARTIDAS
	 * Las partidas deben ser del mismo cliente
	 *  
	 * 
	 * @param partidas
	 * @return
	 */
	public static List<NotaDeCredito> getNotasFromDetalles(final List<NotasDeCreditoDet> partidas){
		
		if(partidas.isEmpty())
			return null;
		validarMismoCliente(partidas);
		final List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
		NotaDeCredito n=null;
		int buffer=0;
		for(NotasDeCreditoDet det:partidas){
			if(n==null){
				//n=getNota(det.getFactura());
				//n=getNotaPorDescuento();
				n=new NotaDeCredito();				
				if(det.getFactura()!=null)
					if(det.getFactura().getCliente()!=null){
						n.setCliente(det.getFactura().getCliente());
						n.setClave(det.getFactura().getClave());
					}
			}
			n.agregarPartida(det);
			
			if(++buffer%MAX_PARTIDAS==0){
				n.actualizar();
				notas.add(n);
				n=null;
			}
		}
		if(n!=null)
			notas.add(n);
		return notas;
	}
	
	public static void validarMismoCliente(final List<NotasDeCreditoDet> dets){
		String clave=null;
		for(NotasDeCreditoDet det:dets){
			Assert.hasLength(det.getClave(), "NotasDeCreditoDet requiere la clave del cliente para este proceso");
			if(clave==null){
				clave=det.getClave();
			}
			Assert.isTrue(clave.equals(det.getClave()),"Todas las partidas requieren ser del mismo cliente para este proceso");
		}
	}
	
	/**
	 * Valida que el grupo de ventas sean del cliente 
	 * 
	 * @param c
	 * @param ventas
	 */
	public static void validarMismoCliente(final Cliente c,final Collection<Venta> ventas){
		/**
		CollectionUtils.forAllDo(ventas, new Closure(){
			public void execute(Object input) {
				Venta v=(Venta)input;
				Assert.isTrue(c.equals(v.getCliente()),
						MessageFormat.format(
								"La Venta {0} no pertenece al cliente {1} pertenece a : {2}"
								,v.getId(),c.getClave(),v.getCliente().getClave()));
			}			
		});
		**/
	}
	
	
	public static void ordenarPorFactura(final List<NotasDeCreditoDet> partidas){
		Comparator<NotasDeCreditoDet> c=GlazedLists.beanPropertyComparator(NotasDeCreditoDet.class, "factura.numero");
		Collections.sort(partidas,c);
	}
	
	
	public static void asignarNumeroConsecutivo(final List<NotaDeCredito> notas,long consecutivo){
		for(NotaDeCredito nota:notas){
			nota.setNumero(consecutivo);
			nota.setNumeroFiscal(consecutivo);
			for(NotasDeCreditoDet det:nota.getPartidas()){
				det.setNumero(consecutivo);				
			}
			consecutivo++;
		}
	}
	
	/**
	 * 
	 * @param notas
	 */
	public static void aplicarProvision(final List<NotaDeCredito> notas){
		for(NotaDeCredito nota:notas){
			for(NotasDeCreditoDet det:nota.getPartidas()){
				//if(det.getFactura().getProvision()!=null){
				if(det.getFactura().getProvision()!=null){
					final Venta v=det.getFactura();					
					double ip=v.getProvision().getImporteDescuento1().doubleValue()*1.15;
					double desc=ip*100/v.getSaldo().doubleValue();
					det.setSaldoFactura(v.getSaldoEnMoneda());
					det.setDescuento(desc);
					det.actualizarImporte();
				}
				
			}
			nota.actualizar();
		}
	}
	
	public static void actualizarFecha(final List<NotaDeCredito> notas,final Date fecha){
		for(NotaDeCredito nota:notas){
			for(NotasDeCreditoDet det:nota.getPartidas()){
				det.setFecha(fecha);				
			}
			nota.setFecha(fecha);
		}
	}
	
	/**
	 * Regresa una lista de notas de cargo para la solicitud generada
	 * 
	 * @param solicitud
	 * @return
	 */
	public static List<NotaDeCredito> generarNotasDeCargo(final SolicitudDeNotaDeCargo2 solicitud){
		List<NotasDeCreditoDet> detalles=new ArrayList<NotasDeCreditoDet>();
		for(Venta v:solicitud.getVentas()){
			final NotasDeCreditoDet det=getNotaDet(v);
			det.setImporte(v.getPago());
			det.setTipo("M");
			det.setSerie("M");
			det.setOrigen(v.getOrigen());
			det.setFecha(solicitud.getFecha());
			det.setComentario(solicitud.getComentario());
			if(solicitud.isPorPorcentaje())
				det.setDescuento(v.getDescuentoTemporal());
			detalles.add(det);
		}
		final List<NotaDeCredito> notas=getNotasFromDetalles(detalles);
		for(NotaDeCredito n:notas){
			n.setTipo("M");
			n.setSerie("M");
			n.setOrigen("CRE");
			n.setFecha(solicitud.getFecha());
			n.setComentario(solicitud.getComentario());
			if(solicitud.isPorPorcentaje()){
				n.setDescuento(solicitud.getPorcentaje());				
			}
		}
		return notas;
	}	
	
	/**
	 * Regresa una lista de notas de cargo para la solicitud generada
	 * 
	 * @param solicitud
	 * @return
	 */
	public static NotaDeCredito generarNotaDeCargoPorChequeDevuelto(final SolicitudDeCargoPorChequeDevuelto solicitud){
		final NotaDeCredito n=new NotaDeCredito();
		n.setCliente(solicitud.getCliente());
		n.setClave(solicitud.getCliente().getClave());
		n.setFecha(solicitud.getFecha());
		n.setTipo("O");
		n.setSerie("M");
		n.setOrigen("CHE");
		n.setFecha(solicitud.getFecha());
		n.setComentario(solicitud.getComentario());
		n.setImporte(MonedasUtils.calcularImporteDelTotal(solicitud.getImporte()));
		n.setSucursalDevo(1);
		if(solicitud.isPorPorcentaje())
			n.setDescuento(solicitud.getPorcentaje());
		
		final NotasDeCreditoDet det=new NotasDeCreditoDet();
		det.setClave(n.getClave());
		det.setComentario(n.getComentario());
		det.setFecha(n.getFecha());
		det.setImporte(solicitud.getImporte());
		det.setFechaDocumento(n.getFecha());
		det.setOrigen("CHE");
		det.setNumDocumento(Long.valueOf(solicitud.getCheque().getNumero()));
		det.setGrupo(solicitud.getCheque().getId().intValue());
		det.setSucDocumento(1);
		det.setTipoDocumento(" ");
		det.setCheque(solicitud.getCheque());
		if(solicitud.isPorPorcentaje())
			det.setDescuento(solicitud.getPorcentaje());
		det.setSaldoFactura(solicitud.getImporte());
		n.agregarPartida(det);
		n.setComentario2("CH.Dev num:"+solicitud.getCheque().getNumero());
		return n;
	}	
	
	public static List<NotaDeCredito> generarNotasDeCreditoParaDevolucion(final Devolucion devo){		
		
		final EventList<DevolucionDet> partidas=GlazedLists.eventList(devo.getPartidas());
		final List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
		
		if(partidas.size()>MAX_DEVO_PARTIDAS){
			notas.add(createNotaDeCreditoDeDevolucion(partidas.subList(0, MAX_DEVO_PARTIDAS)));			
			notas.add(createNotaDeCreditoDeDevolucion(partidas.subList(MAX_DEVO_PARTIDAS, partidas.size())));
			
		}else{
			notas.add(createNotaDeCreditoDeDevolucion(partidas));
		}
		
		
		
		if(notas.size()==2){
			final NotasDeCreditoDet detFinal=notas.get(1).getPartidas().get(0);
			CantidadMonetaria imp=detFinal.getImporte();
			imp=imp.add(devo.getImpCortes());
			//notas.get(1).getPartidas().get(0).setImporte(imp);
			detFinal.setImporte(imp);
		}else if(notas.size()==1){			
			notas.get(0).getPartidas().get(0).setImporte(devo.getTotal());
		}
		
		/*Aplicar los descuentos de la devolucion
		for(NotaDeCredito n:notas){
			CantidadMonetaria imp=n.getPartidas().get(0).getImporte();
			//DEBUG 17/09/08 imp=MonedasUtils.aplicarDescuentosEnCascada(imp,devo.getDescuento1()/100,devo.getDescuento2()/100);
			n.getPartidas().get(0).setImporte(imp);
			n.actualizar();
		}
		*/
		return notas;
	}
	
	/**
	 * Crea una nota de credito a partir de un grupo de partidas de DevolucionDet
	 * La única partida {@link NotasDeCreditoDet} es generada en otro metodo
	 * 
	 * El importe de la nota no tiene aplicados los descuentos de la devolucion
	 * 
	 * @param partidas
	 * @return
	 */
	public static NotaDeCredito createNotaDeCreditoDeDevolucion(final List<DevolucionDet> partidas){
		final NotaDeCredito nc=getNotaDeCreidotDevo();		
		//Total con iva
		NotasDeCreditoDet det=createNotaDetParaDevolucion(partidas);
		nc.agregarPartida(det);
		nc.actualizar();
		//CantidadMonetaria total=MonedasUtils.calcularTotal(imp);
		for(DevolucionDet dd:partidas){
			dd.setNota(nc);
		}
		return nc;
	}
	
	public static NotasDeCreditoDet createNotaDetParaDevolucion(final List<DevolucionDet> partidas){
		
		//El total de las partidas
		CantidadMonetaria imp=CantidadMonetaria.pesos(0);		
		for(DevolucionDet d:partidas){
			imp=imp.add(d.getImporteAsMoneda());
			//imp=imp.add(d.getImporteNetoAsMoneda());
		}		
		final NotasDeCreditoDet nc=new NotasDeCreditoDet();
		//Las partidas son con iva
		nc.setImporte(MonedasUtils.calcularTotal(imp));
		nc.setFactura(partidas.get(0).getDevolucion().getVenta());
		
		return nc;
	}
	/**
	public static NotaDeCredito preparaNotaDeReclamacion(final NotaDeCredito nota){
		return nota;
	}
	**/
	
	
	public static void imprimirNota(final NotaDeCredito nota,final boolean preview){
		final Map<String, Object> params=new HashMap<String, Object>();
		params.put("NOTA_ID", nota.getId());
		params.put("IMPORTE_LETRA", ImporteALetra.aLetra(nota.getTotalAsMoneda()));
		if(!preview)
			ReportUtils.printReport("reportes/"+CXCReportes.NotaDeCredito.name()+".jasper", params, false);
		else
			ReportUtils.viewReport("reportes/"+CXCReportes.NotaDeCredito.name()+".jasper", params);
	}
	
	
	public static double calcularDescuentoFinanciero(final Venta factura,final Date fecha,final double descuentoBase){
		
		double descuento=0;
		final int plazo=factura.getCredito().getPlazo();
		final Date vto=calcularVencimientoFinanciero(factura);		
		final Date origen=DateUtils.truncate(fecha,Calendar.DATE);
		
		if(fecha.before(vto)){
			final int dias=com.luxsoft.siipap.utils.DateUtils.obtenerIntervaloEnDias( origen,vto);
			descuento=(dias*descuentoBase)/plazo;
		}
		return descuento;
	}
	
	
	
	/**
	 * Calcula el descuento financiero para facturas
	 * 
	 * @param factura
	 * @return
	 */
	public static Date calcularVencimientoFinanciero(final Venta factura){
		if(factura.getCredito()==null)
			return null;
		final int plazo=factura.getCredito().getPlazo();
		final Date ffactura=factura.getFecha();		
		Calendar c=Calendar.getInstance();
		c.setTime(ffactura);
		c.getTime();
		c.add(Calendar.DATE, plazo);		
		final Date vto=DateUtils.truncate(c.getTime(),Calendar.DATE);
		return vto;
	}
	
	
 
}
