package com.luxsoft.siipap.em.importar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

/**
 * Importa ventas de siipap clipper, pero solo al grado de completar sus partidas
 * y vinculaciones
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class ImportadorDeVentas implements Importador{
	
	private Logger logger=Logger.getLogger(getClass());
	
	protected SiipapJdbcTemplateFactory factory;
	private RowMapper partidasMapper;	
	private ClienteDao clienteDao;
	private ArticuloDao articuloDao;
	private ImportadorDeClientes importadorDeClientes;
	private ImportadorDeArticulos importadorDeArticulos;

	/**
	 * Completa un grupo de ventas que debieron ser procesadas desde siipap
	 * 
	 * 
	 */
	public Object[] importar(Object... params) {
		final List<Venta> ventas=new ArrayList<Venta>();
		for(Object o:params){			
			Venta v=importarVenta((Venta)o);
			ventas.add(v);
		}
		return ventas.toArray();
	}
	
	/**
	 * Recibe una venta, normalmente generada desde siipap y la completa
	 * para ser importada a siipapwin
	 * 
	 * @param v
	 * @return
	 */
	public Venta importarVenta(final Venta v){
		List<VentaDet> partidas=buscarPartidas(v);
		for(VentaDet det:partidas){
			v.agregarDetalle(det);
		}
		Cliente c=getClienteDao().buscarPorClave(v.getClave());
		if(c==null){
			c=importarClienteNuevo(v.getClave());
			logger.info("Cliente importado: "+c);
			
		}
		v.setCliente(c);
		logger.info("Venta importada: "+v);
		return v;
	}
	
	/**
	 * Trata de importar un cliente nuevo delegando el proceso al bean ImportadorDeClientes
	 * 
	 * @param clave
	 * @return
	 */
	private Cliente importarClienteNuevo(final String clave){
		Object[] beans=getImportadorDeClientes().importar(clave);
		if(beans.length==1){
			Cliente c=(Cliente)beans[0];
			return c;
		}
		logger.info("No se fue posible generar cliente nuevo desde los dbfs clave: "+clave);
		return null;
			
	}
	
	/**
	 * Busca
	 * @param v
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<VentaDet> buscarPartidas(final Venta v){
		String sql=getSql(v);
		List<VentaDet> partidas=getFactory().getJdbcTemplate().query(sql, getPartidasMapper());
		for(VentaDet det:partidas){
			det.setYear(v.getYear());
			det.setMes(v.getMes());	
			det.setCantidad(det.getCantidad()/det.getFactorDeConversionUnitaria());
		}
		vincularPartidas(partidas);
		if(logger.isDebugEnabled()){
			logger.debug("Partidas encontradas: "+partidas.size());
		}
		return partidas;
	}
	/**
	 * Vincula con Articulo
	 * 
	 * @param partidas
	 */
	private void vincularPartidas(final List<VentaDet> partidas){
		for(VentaDet det:partidas){
			Articulo a=getArticuloDao().buscarPorClave(det.getClave());
			if(a==null){
				a=importarArticulo(det.getClave());
			}
			det.setArticulo(a);
		}
	}
	
	/**
	 * Trata de importar un articulo que no existe en el catalogo de articulos
	 * 
	 *	
	 * @param clave
	 * @return
	 */
	private Articulo importarArticulo(final String clave){
		logger.info("No existe el articulo tratando de importarlo :"+clave);
		Articulo a=getImportadorDeArticulos().importar(clave);
		if(a!=null)
			return a;
		else 
			throw new RuntimeException("No existe el articulo: "+clave+ " en los dbfs");
	}
	
	/**
	 * Construye una sentencia sql adecuada para buscar las partidas correspondientes
	 *  a una venta, en el archivo ALMACEXX correspondiente al año mes de la venta
	 *   
	 * @param v
	 * @return
	 */
	private String getSql(final Venta v){
		Calendar c=Calendar.getInstance();
		c.set(Calendar.YEAR, v.getYear());		
		c.set(Calendar.MONTH, v.getMes()-1);
		Date fechaDbf=c.getTime();
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT * FROM ");
		sql.append(ReplicationUtils.resolveTable("ALMACE", fechaDbf));		
		//sql.append(ReplicationUtils.resolveWherePart(new Periodo(fechaDbf), "ALMFECHA"));
		String where=" WHERE ALMNUMER=@NUMERO AND ALMTIPO=\'FAC\'" +
				" AND ALMSUCUR=@SUCURSAL AND ALMSERIE=\'@SERIE\' AND ALMTIPFA=\'@TIPO\'";
		where=where.replace("@NUMERO", String.valueOf(v.getNumero()));
		where=where.replace("@SUCURSAL", String.valueOf(v.getSucursal()));
		where=where.replace("@SERIE", v.getSerie());
		where=where.replace("@TIPO", v.getTipo());
		//String where=MessageFormat.format(pattern, v.getNumero(),v.getSucursal(),v.getSerie(),v.getTipo());
		sql.append(where);
		if(logger.isDebugEnabled()){
			logger.debug("Query generado: "+sql.toString());
		}
		return sql.toString();
	}
	
	/*** Colaboradores **/
	
	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}

	public RowMapper getPartidasMapper() {
		return partidasMapper;
	}

	public void setPartidasMapper(RowMapper partidasMapper) {
		this.partidasMapper = partidasMapper;
	}

	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}

	public ImportadorDeClientes getImportadorDeClientes() {
		return importadorDeClientes;
	}

	public void setImportadorDeClientes(ImportadorDeClientes importadorDeClientes) {
		this.importadorDeClientes = importadorDeClientes;
	}

	public ArticuloDao getArticuloDao() {
		return articuloDao;
	}

	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}

	public ImportadorDeArticulos getImportadorDeArticulos() {
		return importadorDeArticulos;
	}

	public void setImportadorDeArticulos(ImportadorDeArticulos importadorDeArticulos) {
		this.importadorDeArticulos = importadorDeArticulos;
	}	
	

}
