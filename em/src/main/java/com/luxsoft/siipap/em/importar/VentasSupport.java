package com.luxsoft.siipap.em.importar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.ventas.MococaMapper;
import com.luxsoft.siipap.em.replica.ventas.MocomoMapper;
import com.luxsoft.siipap.em.replica.ventas.MovcreMapper;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Proporciona informacion relacionada con ventas tanto en Siipap
 * como en siipapwin
 *  
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class VentasSupport {
	
	
	private SiipapJdbcTemplateFactory factory;
	
	private Logger logger=Logger.getLogger(getClass());
	
	/**
	 * Busca las ventas sin cliente asignado
	 * 
	 * @return
	 */
	
	public List<String> buscarVentasSinCliente(){
		String sql="select distinct clave as CLAVE from sw_Ventas where year=2007 and cliente_id is null ";
		List<Map<String, String>> l=getJdbcTemplate().queryForList(sql);
		final List<String> clientes=new ArrayList<String>();
		for(Map<String, String> row:l){
			clientes.add(row.get("CLAVE"));
		}
		return clientes;
	}
	/***
	 * Busca ventas en siipap clipper transformandolas en beans de Venta
	 * 
	 * @param dia
	 * @return
	 */
	public List<Venta> buscarVentasEnSiipap(final Date dia){
		final Periodo p=new Periodo(dia);
		String s1=ReplicationUtils.resolveSQL(p, "MOVCRE", "MCRFECHA")+" AND MCRIDENOPE=1";
		String s2=ReplicationUtils.resolveSQL(p, "MOCOCA", "MCAFECHA")+" AND MCAIDENOPE=1";
		String s3=ReplicationUtils.resolveSQL(p, "MOCOMO", "MCMFECHA")+" AND MCMIDENOPE=1";
		final List<Venta> ventas=new ArrayList<Venta>();
		ventas.addAll(getFactory().getJdbcTemplate().query(s1, new MovcreMapper()));
		ventas.addAll(getFactory().getJdbcTemplate().query(s2, new MococaMapper()));
		ventas.addAll(getFactory().getJdbcTemplate().query(s3, new MocomoMapper()));
		for(Venta v:ventas){
			v.setYear(Periodo.obtenerYear(dia));
			v.setMes(Periodo.obtenerMes(dia)+1);
		}
		String pattern="Ventas end DBF para {0} :{1}";
		logger.info(MessageFormat.format(pattern, dia,ventas.size()));
		return ventas;
	}
	
	/**
	 * Copia de manera adecuada las propiedades de una venta a la otra
	 * 
	 * @param source
	 * @param target
	 */
	public void copyVenta(final Venta source,final Venta target){
		
	}
	
	/*
	public List<Map<String, Object>> buscarEnSiipapDbf(final long numero,final int sucursal,final String tipo,final String serie, final Date fecha){
		
		final Periodo p=new Periodo(fecha);		
		String sql=ReplicationUtils.resolveSQL(p, "ALMACE", "ALMFECHA ")+"ALMSUCUR=? AND ALMTIPFA=? AND ALMSERIE=? AND ALMNUMER=? AND ALMTIPO=\'FAC\'";
		final Object args[]={sucursal,tipo,serie,numero};
		return getFactory().getJdbcTemplate(fecha).queryForList(sql, args, argTypes);
			
	}
	*/
	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}
	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}
	
	public VentasDao getVentaDao() {
		return (VentasDao)ServiceLocator.getDaoContext().getBean("ventasDao");
	}
	
	public JdbcTemplate getJdbcTemplate(){
		return ServiceLocator.getJdbcTemplate();
	}
	

}
