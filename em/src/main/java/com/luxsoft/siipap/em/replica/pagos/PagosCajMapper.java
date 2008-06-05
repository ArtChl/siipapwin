package com.luxsoft.siipap.em.replica.pagos;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.dao.PagoDao;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.dao.VentasDao;

public class PagosCajMapper implements RowMapper{
	
	
	private int total;
	
	private VentasDao ventasDao;
	private NotaDeCreditoDao notaDeCreditoDao;
	private PagoDao pagoDao;
	
	private final String prefix;
	
	
	public PagosCajMapper(final String prefix){
		this.prefix=prefix;
	}
	
	
	public String getPrefix(){
		return prefix;
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		//System.out.println("Procesando registro: "+rowNum);
		Pago pago=new Pago();
		String origen="MOS";		
		pago.setOrigen(origen);
		
		String clave=rs.getString(getPrefix()+"CLAVCLI");
		if(clave!=null)
		pago.setClave(clave);
		
		
		
		String tipo=rs.getString(getPrefix()+"TIPOFAC");
		pago.setTipoDocto(tipo);
		Number numero=(Number)rs.getObject(getPrefix()+"NUMFAC");
		Number sucursal=(Number)rs.getObject(getPrefix()+"SUCURS");
		
		//Assert.notNull(numero);
		//Assert.notNull(sucursal);
		//Assert.notNull(tipo);
		
			//Venta
		/**
		Venta venta=null;
		if(numero!=null && sucursal!=null && tipo!=null)
			venta=getVentasDao().buscarVenta(sucursal.intValue(), tipo, numero.longValue());
		pago.setVenta(venta);
		if(venta==null)
			logger.info("Pago de Venta con Venta_id nulo "+numero+" tipo"+tipo+" sucursal:"+sucursal.intValue());
		
		**/
		String comentario=rs.getString(getPrefix()+"OBSER");
		pago.setComentario(comentario);
		
		Date fecha=rs.getDate(getPrefix()+"DAYSIST");
		pago.setFecha(fecha);
		
		String formaDePago=rs.getString(getPrefix()+"TIPAGO");
		pago.setFormaDePago(formaDePago);
		
		Number importe=(Number)rs.getObject(getPrefix()+"RECIBIO");
		if(importe==null)
			importe=BigDecimal.ZERO;
		pago.setImporte(CantidadMonetaria.pesos(importe.doubleValue()));
		
		if(sucursal!=null)
			pago.setSucursal(sucursal.intValue());
		if(numero!=null)
			pago.setNumero(numero.longValue());
		
		String referencia=rs.getString(getPrefix()+"DOCTO");		
		pago.setReferencia(referencia);
		
		Number institucion=(Number)rs.getObject(getPrefix()+"CLAVBAN");
		if(institucion!=null)
			pago.setInstitucion(institucion.intValue());
		
		Date corte=rs.getDate(getPrefix()+"CORTE");
		pago.setCorte(corte);
		
		String estado=rs.getString(getPrefix()+"STATUS");
		pago.setEstado(estado);
		
		
		
		if(rowNum%200==0)
			System.out.println("Registro "+rowNum+" De: "+total);
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
