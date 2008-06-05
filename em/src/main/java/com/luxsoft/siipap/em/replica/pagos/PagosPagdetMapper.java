package com.luxsoft.siipap.em.replica.pagos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.dao.PagoDao;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.dao.VentasDao;

public class PagosPagdetMapper implements RowMapper{
	
	private Logger logger=Logger.getLogger(getClass());
	
	private int total;
	private VentasDao ventasDao;
	private NotaDeCreditoDao notaDeCreditoDao;
	private PagoDao pagoDao;
	
	private final String prefix;
	
	
	public PagosPagdetMapper(final String prefix){
		this.prefix=prefix;
	}
	
	
	public String getPrefix(){
		return prefix;
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		//System.out.println("Procesando registro: "+rowNum);
		Pago pago=new Pago();
		String origen=getPrefix();
		
		if(getPrefix().equals("PCR"))
			origen="CRE";
		else if(getPrefix().equals("PCA"))
			origen="CAM";
		else if(getPrefix().equals("PCH"))
			origen="CHE";
		else if(getPrefix().equals("PJU"))
			origen="JUR";
		
		pago.setOrigen(origen);
		
		String clave=rs.getString(getPrefix()+"CLIENTE");	
		pago.setClave(clave);
		
		
		String tipo=rs.getString(getPrefix()+"REFTIPO");
		pago.setTipoDocto(tipo);
		Number numero=(Number)rs.getObject(getPrefix()+"REFEREN");
		Number sucursal=(Number)rs.getObject(getPrefix()+"REFSUC");
		if(sucursal==null)
			sucursal=new Integer(0);
		/**
		if(sucursal.intValue()==1 && numero!=null){
			
			NotaDeCredito nota=getNotaDeCreditoDao().buscarNota(numero.longValue(), tipo);			
			pago.setNota(nota);
			if(nota==null)				
				logger.info("Pago de Nota con Nota_id nulo  "+numero+" tipo"+tipo+" sucursal:"+sucursal.intValue());
				
		}else{
			//Venta
			
			if(numero!=null){
			Venta venta=getVentasDao().buscarVenta(sucursal.intValue(), tipo, numero.longValue());
			pago.setVenta(venta);
			if(venta==null)
				logger.info("Pago de Venta con Venta_id nulo "+numero+" tipo"+tipo+" sucursal:"+sucursal.intValue());
			}
		}
		**/
		String comentario=rs.getString(getPrefix()+"COMENTA");
		pago.setComentario(comentario);
		
		String descFormaDePago=rs.getString(getPrefix()+"NOMTIPO");
		pago.setDescFormaDePago(descFormaDePago);
		
		String descReferencia=rs.getString(getPrefix()+"INSTITU");
		pago.setDescReferencia(descReferencia);
		
		Date fecha=rs.getDate(getPrefix()+"FECHA");
		pago.setFecha(fecha);
		
		String formaDePago=rs.getString(getPrefix()+"TIPO");
		pago.setFormaDePago(formaDePago);
		
		Number importe=(Number)rs.getObject(getPrefix()+"IMPORTE");
		pago.setImporte(CantidadMonetaria.pesos(importe.doubleValue()));
		
		
		pago.setSucursal(sucursal.intValue());
		if(numero!=null)
			pago.setNumero(numero.longValue());
		
		
		//Si es tipo T la referencia se obtiene de NOTCRED
		String referencia=rs.getString(getPrefix()+"NOCOMPR");
		if(formaDePago==null){
			logger.debug("Forma de pago nula en origen:"+origen+" numero:"+numero);			
		}
		if(formaDePago!=null && formaDePago.equals("T")){
			Number rr=(Number)rs.getObject(getPrefix()+"NOTCRED");
			if(rr!=null){				
				String reff=String.valueOf(rr.intValue());
				String tipoDeNotaPago=rs.getString(getPrefix()+"TIPCRED");
				StringBuffer buff=new StringBuffer();
				
				buff.append(tipoDeNotaPago);
				buff.append(" ");
				buff.append(reff);
				
				//pago.setReferencia(buff.toString());
				referencia=buff.toString();
				//System.out.println("Nota: "+referencia);
				//Buscamos la nota de credito con la que se paga
				/**
				NotaDeCredito notaPago=getNotaDeCreditoDao().buscarNota(rr.longValue(), tipoDeNotaPago);
				if(notaPago==null){
					logger.info("No encontre nota de credito para el pago"+referencia);
				}
				pago.setNotaDelPago(notaPago);
				**/
			}
		}
		
		pago.setReferencia(referencia);
		//getPagoDao().salvar(pago);
		if(rowNum%200==0)
			System.out.println("Registro "+rowNum+" De: "+total);
		//logger.info("Pago generado: "+pago);
		
		//Obtenemos el tipo de tarjeta si es que aplica
		if(pago.getFormaDePago().equals("C")){
			String  tipoTarjeta=rs.getString(getPrefix()+"TCREDEB");
			pago.setTarjetaTip(tipoTarjeta);
		}
		
		return pago;
		
	}

	public NotaDeCreditoDao getNotaDeCreditoDao() {
		return notaDeCreditoDao;
	}


	public void setNotaDeCreditoDao(NotaDeCreditoDao notaDeCreditoDao) {
		this.notaDeCreditoDao = notaDeCreditoDao;
	}


	public VentasDao getVentasDao() {
		return ventasDao;
	}


	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}


	public PagoDao getPagoDao() {
		return pagoDao;
	}


	public void setPagoDao(PagoDao pagoDao) {
		this.pagoDao = pagoDao;
	}


	public int getTotal() {
		return total;
	}


	public void setTotal(int total) {
		this.total = total;
	}

	

}
