package com.luxsoft.siipap.cxc.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * FactoryMethods para la creacion de notas de credito
 * 
 * @author Ruben Cancino
 *
 */
public class NotasFactory {
	
	public static NotaDeCredito configurarParaDescuento(final NotaDeCredito nc){		
		nc.setOrigen("CRE");
		nc.setTipo("U");
		nc.setSerie("U");
		for(NotasDeCreditoDet det:nc.getPartidas()){
			det.setOrigen("CRE");
			det.setTipo("U");
			det.setSerie("U");
		}
		return nc;
	}
	
	public static NotaDeCredito getNotaDeCreidotDevo(){
		NotaDeCredito nc=new NotaDeCredito();
		nc.setFecha(new Date());
		return nc;
	}
	
	public static NotaDeCredito getNotaDeCreditoBonificacionCRE(final Cliente c){
		NotaDeCredito nc=new NotaDeCredito();
		nc.setCliente(c);
		nc.setClave(c.getClave());
		nc.setOrigen("CRE");		
		nc.setFecha(new Date());		
		nc.setBonificacion(ConceptoDeBonificacion.BONIFICACION);
		nc.setTipo("L");
		nc.setSerie("J");
		nc.setAplicable(false);
		return nc;
		
	}
	
	public static NotaDeCredito getNotaDeCreditoDescFinancieroCRE(final Cliente c){
		NotaDeCredito nc=new NotaDeCredito();
		nc.setCliente(c);
		nc.setClave(c.getClave());
		nc.setOrigen("CRE");
		nc.setTipo("V");
		nc.setSerie("V");
		nc.setBonificacion(ConceptoDeBonificacion.ADICIONAL);
		nc.setFecha(new Date());		
		return nc;
		
	}
	
	
	/**
	 * 
	 * @param nota
	 * @return
	 * @deprecated usar NotasUtils.getNotaDet
	 */
	public static NotasDeCreditoDet getPartidaDeNota(final NotaDeCredito nota){
		NotasDeCreditoDet det=new NotasDeCreditoDet();
		det.setFecha(nota.getFecha());
		det.setClave(nota.getClave());
		det.setOrigen(nota.getOrigen());
		det.setTipo(nota.getTipo());
		det.setSerie(nota.getSerie());
		det.setYear(nota.getYear());
		det.setMes(nota.getMes());
		//det.setComentario(nota.getComentario());		
		return det;
	}
	
	public static NotasDeCreditoDet getPartidaParaNotaDevo(final NotaDeCredito nota){
		NotasDeCreditoDet det=getPartidaDeNota(nota);
		Venta v=nota.getDevolucion().getVenta();
		det.setFactura(nota.getDevolucion().getVenta());
		det.setNumDocumento(v.getNumero());
		det.setSerieDocumento(v.getSerie());
		det.setTipoDocumento(v.getTipo());
		det.setFechaDocumento(v.getFecha());
		det.setSucDocumento(v.getSucursal());
		return det;
	}
	
	public static String toString(NotaDeCredito nc){
		ToStringBuilder builder=new ToStringBuilder(nc,ToStringStyle.MULTI_LINE_STYLE);		
		builder.append("Clave",nc.getClave())
		.append("Numero",nc.getNumero())
		.append("Serie",nc.getSerie())
		.append("Tipo",nc.getTipo())
		.append("Origen",nc.getOrigen())
		.append("Importe",nc.getImporte())
		.append("Bonificacion",nc.getBonificacion())
		.append("Comentario 1",nc.getComentario())
		.append("Comentario 2",nc.getComentario2());
		return builder.toString();
	}
	
	public static String toString(NotasDeCreditoDet nc){
		ToStringBuilder builder=new ToStringBuilder(nc,ToStringStyle.MULTI_LINE_STYLE);		
		builder.append("Clave",nc.getClave())
		.append("Numero",nc.getNumero())
		.append("Serie",nc.getSerie())
		.append("Tipo",nc.getTipo())
		.append("Origen",nc.getOrigen())
		.append("Importe",nc.getImporte())		
		.append("Comentario ",nc.getComentario());
		return builder.toString();
	}
	
	/**
	 * Genera una nota de credito para cancelar en su totalidad una 
	 * nota de cargo 
	 *  
	 * @return
	 */
	public static NotaDeCredito crearNotaDeCancelacion(final NotaDeCredito cargo){
		final NotaDeCredito nota=new NotaDeCredito();
		nota.setCliente(cargo.getCliente());
		nota.setClave(cargo.getClave());
		nota.setAplicable(true);
		nota.setBonificacion(ConceptoDeBonificacion.RECLAMACION);
		nota.setComentario("Cancelación de cargo: "+cargo.getNumero());
		nota.setDescuento(Math.abs(cargo.getDescuento()));
		nota.setFecha(new Date());		
		nota.setImporte(cargo.getImporte().abs().multiply(-1));
		nota.setMes(Periodo.obtenerMes(nota.getFecha()));
		//nota.setFechaPagoCxC(fechaPagoCxC);
		//nota.setFechaRevisionCxC(vencimiento);
		
		nota.setYear(Periodo.obtenerYear(nota.getFecha()));
		for(NotasDeCreditoDet det:cargo.getPartidas()){
			if(det.getFactura()==null) 
				continue;
			final NotasDeCreditoDet partida=getNotaDet(det.getFactura());
			partida.setDescuento(Math.abs(det.getDescuento()));
			//partida.setComentario(?);
			partida.setImporte(det.getImporte().abs().multiply(-1));
			final String tipo;
			final String serie;
			final String origen=cargo.getOrigen();
			if("CRE".equals(origen)){
				tipo="L";
				serie="J";
			}else{
				tipo="C";
				serie="H";
			}	
			partida.setTipo(tipo);
			partida.setSerie(serie);
			partida.setOrigen(origen);
			nota.setOrigen(origen);
			nota.setSerie(serie);
			nota.setTipo(tipo);
			nota.agregarPartida(partida);
		}
		
		return nota;
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
	
	public static boolean isBonificacion(final NotaDeCredito nota){
		
		String tipo=nota.getTipo();
		return BONIFICACIONES.contains(tipo);
	}
	
	/**
	 *  Genera una nota de cargo por cheque devuelto 
	 *   
	 * @param origen
	 * @return
	 
	public static NotaDeCredito generaNotaDeCargoPorChequeDevuelto(final PagoM origen)
	{
		final NotaDeCredito cargo=new NotaDeCredito();
		cargo.setCliente(origen.getCliente());
		cargo.setClave(origen.getClave());
		//cargo.setDescuento(20);
		cargo.setTipo(" ");
		cargo.setSerie(" ");
		cargo.setOrigen("CHE");
		cargo.setComentario("CHEQUE DEVUELTO: "+origen.getReferencia());
		for(Pago p:origen.getPagos()){
			final NotasDeCreditoDet det=getNotaDet(p.getVenta());
			det.setImporte(p.getImporte());
			det.setTipo(" ");
			det.setSerie(" ");
			det.setOrigen("CHE");
			//det.setDescuento(0);
			cargo.agregarPartida(det);
		}		
		cargo.actualizar();		
		return cargo;
		
	}
	
	
	public static NotaDeCredito generaNotaDeCargoPorChequeDevuelto(final Cliente cliente,int numero,int sucursal)
	{
		final NotaDeCredito cargo=new NotaDeCredito();
		cargo.setCliente(cliente);
		cargo.setClave(cliente.getClave());		
		cargo.setTipo(" ");
		cargo.setSerie(" ");
		cargo.setOrigen("CHE");
		cargo.setComentario("CHEQUE DEVUELTO: "+numero);				
		cargo.actualizar();		
		return cargo;
		
	}
	
	*/
	
	
	private static List<String> BONIFICACIONES=new ArrayList<String>();
	 
	static{
		BONIFICACIONES.add("C");
		BONIFICACIONES.add("F");
		BONIFICACIONES.add("L");
		BONIFICACIONES.add("Y");
	}

}
