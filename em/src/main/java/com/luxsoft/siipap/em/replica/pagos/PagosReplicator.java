package com.luxsoft.siipap.em.replica.pagos;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.dao.PagoDao;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;

@SuppressWarnings("unchecked")
public class PagosReplicator extends AbstractReplicatorSupport{
	
	
	
	private NotaDeCreditoDao notaDeCreditoDao;
	private VentasDao ventasDao;
	private ClienteDao clienteDao;
	
	
	
	@Override
	public List importar(Periodo periodo) {
		ReplicationUtils.validarMismoMes(periodo);
		List<Pago> pagos=new ArrayList<Pago>();
		try {
			pagos.addAll(cargarPagosGenerales(periodo, "PAGCAM", "PCA"));
			pagos.addAll(cargarPagosMostrador(periodo, "CAJNOR", "CAN"));
			pagos.addAll(cargarPagosDeAnticipos(periodo, "CAMANT", "CAMANT"));
			//pagos.addAll(cargarPagosGenerales(periodo, "PAGCHE", "PCH"));
			//pagos.addAll(cargarPagosGenerales(periodo, "PAGJUR", "PJU"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		vincular(pagos);
		persistir(pagos, periodo);
		return pagos;
	}
	
	public void persistir(List<Pago> pagos,Periodo p) {
		//borrarPagos(p);
		logger.debug("Borrando pagos del periodo" +p);
		try {
			logger.debug("Eliminando CAM");
			getPagoDao().eliminarPagos(p,"CAM");
			logger.debug("Eliminando MOS");
			getPagoDao().eliminarPagos(p,"MOS");
			logger.debug("Eliminando JUR");
			getPagoDao().eliminarPagos(p,"JUR");
			logger.debug("Eliminando CHE");
			getPagoDao().eliminarPagos(p,"CHE");
			//logger.debug("Eliminando CRE");
			//getPagoDao().eliminarPagos(p,"CRE");
			for(Pago pago:pagos){
				persistir(pago);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		
		
	}
	
	private void borrarPagos(final Periodo p){
		logger.debug("Eliminando los pagos existentes en el periodo..."+p);
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="delete from Pago p where p.fecha between :f1 and :f2";
				session.createQuery(hql)
				.setParameter("f1", p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2", p.getFechaFinal(),Hibernate.DATE)
				.executeUpdate();
				return null;
			}
			
		});
	}

	@Override
	public void persistir(Object bean) {
		try {
			Pago p=(Pago)bean;
			getPagoDao().salvar(p);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	private void vincular(List<Pago> pagos){
		logger.debug("Pagos a vincular: "+pagos.size());
		for(Pago p:pagos){
			//logger.debug("Vinculando pago: "+p.getNumero());
			p.setCliente(getClienteDao().buscarPorClave(p.getClave()));
			if(p.getSucursal()!=1 ){
				Venta v=getVentasDao().buscarVenta(p.getSucursal(), p.getTipoDocto(), p.getNumero());				
				p.setVenta(v);
				if(v==null){
					NotaDeCredito nota=getNotaDeCreditoDao().buscarNota(p.getNumero(), p.getTipoDocto());
					p.setNota(nota);
				}
				
			}else{
				NotaDeCredito nota=getNotaDeCreditoDao().buscarNota(p.getNumero(), p.getTipoDocto());
				p.setNota(nota);
			}
			
			if((p.getFormaDePago()!=null) && (p.getFormaDePago().equals("T"))){
				String referencia=p.getReferencia();
				String tipo=referencia.substring(0, 1);
				String ref=referencia.substring(1,referencia.length());
				Long numero=Long.valueOf(ref.trim());
				NotaDeCredito notaPago=getNotaDeCreditoDao().buscarNota(numero, tipo);
				p.setNotaDelPago(notaPago);
			}
		}
		logger.debug("Pagos vinculados .....");
	}

	public void bulkImport(Periodo periodo) {
		
				 
	}
	
	private List<Pago> cargarPagosGenerales(final Periodo periodo,final String tabla,final String prefix){
		
		//String s=ReplicationUtils.resolveTable(tabla, periodo.getFechaFinal());		
		//String sql="select * from "+s+" where "+prefix+"FECHA=";
		String sql=ReplicationUtils.resolveSQL(periodo,tabla, prefix+"FECHA");		
		//logger.debug("Cargando pagos con: SQL : "+sql);
		String sqlRow=ReplicationUtils.resolveSQLCount(periodo, tabla, prefix+"FECHA");
		int total=getFactory().getJdbcTemplate(periodo).queryForInt(sqlRow);			
		PagosPagdetMapper mapper=new PagosPagdetMapper(prefix);
		mapper.setTotal(total);
		
		Assert.notNull(getVentasDao());
		Assert.notNull(getNotaDeCreditoDao());
		mapper.setVentasDao(getVentasDao());
		mapper.setNotaDeCreditoDao(getNotaDeCreditoDao());
		mapper.setPagoDao(getPagoDao());
		
		List<Pago> pagos=getFactory().getJdbcTemplate(periodo).query(sql,mapper);
		logger.debug(MessageFormat.format("Pagos importados en tabla {0}: {1} "
				,tabla,pagos.size()));
		injectYearMonth(periodo, pagos);
		return pagos;
	}
	
	private List<Pago> cargarPagosMostrador(final Periodo periodo,final String tabla,final String prefix){
		
		String sql=ReplicationUtils.resolveSQL(periodo,tabla, prefix+"DAYSIST");		
		//String s=ReplicationUtils.resolveTable(tabla, periodo.getFechaFinal());		
		//String sql="select * from "+s;	
		//String sql=ReplicationUtils.resolveSQL(periodo,tabla,prefix);	
		sql=sql+" and "+prefix+"SERIFAC=\'A\'";
		//logger.debug("Cargando pagos con : "+sql);
				
		
		PagosCajMapper mapper=new PagosCajMapper(prefix);
		String sqlRow=ReplicationUtils.resolveSQLCount(periodo, tabla, prefix+"DAYSIST");
		int total=getFactory().getJdbcTemplate(periodo).queryForInt(sqlRow+" and "+prefix+"SERIFAC=\'A\'");			
		
		mapper.setTotal(total);
		mapper.setVentasDao(getVentasDao());
		mapper.setNotaDeCreditoDao(getNotaDeCreditoDao());
		mapper.setPagoDao(getPagoDao());		
		List<Pago> pagos=getFactory().getJdbcTemplate(periodo).query(sql,mapper);
		injectYearMonth(periodo, pagos);
		//salvar(pagos.toArray());
		logger.debug("Pagos mostrador importados "+pagos.size());
		return pagos;
	}
	
	private List<Pago> cargarPagosDeAnticipos(Periodo periodo,final String tabla,final String prefix){
		 
		String sql=ReplicationUtils.resolveSQL(periodo,"CAMANT", "CAMANTFECH");
		//sql=sql+" where CAMANTFECH=?";
		logger.debug("SQL Final: "+sql);
		
		PagosAntMapper mapper=new PagosAntMapper(prefix);
		mapper.setPagoDao(getPagoDao());		
		List<Pago> pagos=getFactory().getJdbcTemplate(periodo).query(sql,mapper);
		injectYearMonth(periodo, pagos);
		return pagos;
	}
	
	@Override
	protected int contarBeans(Periodo p, Object... args) {		
		String hql="select count(*) from Pago e where e.fecha between ? and ? and e.origen=?";		
		List l=getDao().getHibernateTemplate().find(hql, new Object[]{p.getFechaInicial(),p.getFechaFinal(),args[0]});
		return ((Long)l.get(0)).intValue();
	}

	

	public List<ReplicaLog> validar(Periodo periodo) {
		Iterator<Date> dias=periodo.getDiasIterator();
		List<ReplicaLog> list=new ArrayList<ReplicaLog>();
		while(dias.hasNext()){
						
			Periodo mes=new Periodo(dias.next());
			//String rowsSQL="select count(*) from "+ReplicationUtils.resolveTable("PAGCRE", mes.getFechaFinal());
			String rowsSQL=ReplicationUtils.resolveSQLCount(mes, "PAGCRE","PCRFECHA");
			int rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			int beans=contarBeans(mes, "CRE");			
			list.add(registrar("Pagos","PAGCRE","PAG",mes,beans,rows));
						
			
			rowsSQL=ReplicationUtils.resolveSQLCount(mes, "PAGCAM","PCAFECHA");
			rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			beans=contarBeans(mes, "CAM");			
			list.add(registrar("Pagos","PAGCAM","PAG",mes,beans,rows));
						
			
			rowsSQL=ReplicationUtils.resolveSQLCount(mes, "PAGCHE","PCHFECHA");
			rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			beans=contarBeans(mes, "CHE");			
			list.add(registrar("Pagos","PAGCHE","PAG",mes,beans,rows));
			
			
			rowsSQL=ReplicationUtils.resolveSQLCount(mes, "PAGJUR","PJUFECHA");
			rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			beans=contarBeans(mes, "JUR");			
			list.add(registrar("Pagos","PAGJUR","PAG",mes,beans,rows));

			rowsSQL=ReplicationUtils.resolveSQLCount(mes, "CAJNOR","CANDAYSIST")+" and CANSERIFAC=\'A\'";							
			rows=getFactory().getJdbcTemplate(mes).queryForInt(rowsSQL);
			beans=contarBeans(mes, "MOS");			
			list.add(registrar("Pagos","CAJNOR","PAG",mes,beans,rows));			
			
			
		}
		return list;
	}
	
	public void validarBulkImport(Periodo periodo) {
		
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
		return (PagoDao)getDao();
	}

	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}

	
	

}
