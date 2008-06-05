package com.luxsoft.siipap.em.replica.pagos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.dao.PagoDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.dao.VentasDao;

public class PagosAntMapper implements RowMapper{
	
	private Logger logger=Logger.getLogger(getClass());
	
	private ClienteDao clienteDao;
	private VentasDao ventasDao;
	private NotaDeCreditoDao notaDeCreditoDao;
	private PagoDao pagoDao;
	
	private final String prefix;
	
	
	public PagosAntMapper(final String prefix){
		this.prefix=prefix;
	}
	
	
	public String getPrefix(){
		return prefix;
	}

	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		//System.out.println("Procesando registro: "+rowNum);
		Pago pago=new Pago();
		String origen="CAM";		
		pago.setOrigen(origen);
		
		String clave=rs.getString(getPrefix()+"CLAV");		
		pago.setClave(clave);
		/*Cliente cliente=getClienteDao().buscarPorClave(clave);
		pago.setCliente(cliente);*/
		
		Number numero=(Number)rs.getObject(getPrefix()+"NUME");
		Number sucursal=(Number)rs.getObject(getPrefix()+"SUCU");
		
		String comentario=rs.getString(getPrefix()+"OBSE");
		pago.setComentario(comentario);
		
		String descReferencia=rs.getString(getPrefix()+"BANC");
		pago.setDescReferencia(descReferencia);
		
		Date fecha=rs.getDate(getPrefix()+"FECH");
		pago.setFecha(fecha);
		
		//String formaDePago=rs.getString(getPrefix()+"TIPO");
		pago.setFormaDePago("K");
		
		Number importe=(Number)rs.getObject(getPrefix()+"IMPO");
		double impo=Math.abs(importe.doubleValue());
		pago.setImporte(CantidadMonetaria.pesos(impo));
		
		
		pago.setSucursal(sucursal.intValue());
		pago.setNumero(numero.longValue());
		
		
		
		
		Number rr=(Number)rs.getObject(getPrefix()+"CHEQ");
		if(rr!=null){
			pago.setReferencia(String.valueOf(rr.intValue()));
		}
		
		//getPagoDao().salvar(pago);
		if(logger.isDebugEnabled())
			logger.debug("Anticipo generado:" +pago);
		
		return pago;
		
	}
	
	
	
	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
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

	

}
