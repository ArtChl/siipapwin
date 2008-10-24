package com.luxsoft.siipap.cxc.managers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.dao.PagoMDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DepositoRow;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Implementacion de PagosManager
 * 
 * Encapsula todas las reglas de negicios vigentes para la persistencia
 * de PagoM y Pagos
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class PagosManagerImpl extends HibernateDaoSupport implements PagosManager{
	
	private PagoMDao pagoMDao;
	private VentasManager ventasManager;

	/**
	 * Salva un grupo de pagos aplicando las reglas de negocios vigentes
	 * para este proceso. Opera en una transaccion
	 * 
	 * @param grupo
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void salvarGrupoDePagos(final PagoM pago) {
		if(StringUtils.isBlank(pago.getClave())){
			pago.setClave(pago.getCliente().getClave());
		}
		for(Pago p:pago.getPagos()){
			p.setClave(pago.getClave());
			p.setCliente(pago.getCliente());
			p.setReferencia(pago.getReferencia());
			p.setDescReferencia(pago.getBanco());
			p.setComentario(pago.getComentario());
			p.setFecha(pago.getFecha());
			p.setFormaDePago2(pago.getFormaDePago());
			p.setDescFormaDePago(pago.getFormaDePago().getDesc());
			p.setMes(pago.getMes());
			p.setYear(pago.getYear());
		}
		pago.depurar();		
		getPagoMDao().salvar(pago);
		if(pago.getDepositoRow()!=null)
			actualizarTablaDepositos(pago);
		for(Pago p:pago.getPagos()){
			if(p.getVenta()!=null){
				//getVentasManager().actualizarVenta(p.getVenta());
			}
		}
	}
	
	private void actualizarTablaDepositos(final PagoM pago){
		System.out.println("Debemos salvar pagom_id en depositosdet : "+pago.getDepositoRow());
		String sql="UPDATE SW_DEPOSITOSDET SET PAGOAPLICADO=? WHERE DEPOSITO_ID=? AND CLAVE=? AND NUMERO=?";
		DepositoRow row=pago.getDepositoRow();
		Object[] args={pago.getId(),row.getDepositoId(),row.getClave(),row.getNumero()};
		int updated=ServiceLocator.getJdbcTemplate().update(sql, args);
		logger.debug("Actualizados: "+updated);
	}
	
	/**
	 * Actualiza un pagoM desde la base de datos
	 * 
	 * @param pago
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void refresh(final PagoM pago){
		getSession().update(pago);
		Hibernate.initialize(pago.getPagos());
		Hibernate.initialize(pago.getCliente());
		for(Pago p:pago.getPagos()){
			if(p.getVenta()!=null){
				Hibernate.initialize(p.getVenta());
				p.getVenta().getDescuentos();
			}else if(p.getNota()!=null){
				Hibernate.initialize(p.getNota());
			}
		}
		
	}
	
	
	
	/**
	 * Busca las posibles ventas suceptibles de pagos automaticos
	 * 
	 * @param pago
	 * @return
	 */	
	public List<Venta> buscarPosiblesPagosAutomaticos(final PagoM pago){
		final String hql="select p.venta from Pago p " +
				" where p.pagoM=:pago" +
				"  and p.venta.saldo<=100";
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {				
				return session.createQuery(hql)
				.setEntity("pago", pago)
				.list();
			}			
		});
	}

	public PagoMDao getPagoMDao() {
		return pagoMDao;
	}

	public void setPagoMDao(PagoMDao pagoMDao) {
		this.pagoMDao = pagoMDao;
	}

	public List<Pago> buscarPagosAplicados(final Cliente c, final  Periodo p) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from Pago p" +
						" left join fetch p.cliente c  " +
						" left join fetch p.pagoM pm " +
						" left join fetch p.venta v" +
						" left join fetch p.nota n" +
						" where p.cliente=:cliente " +
						" and p.fecha between :f1 and :f2" +
						" and p.pagoM is not null")
				.setEntity("cliente", c)
				.setParameter("f1", p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2", p.getFechaFinal(),Hibernate.DATE)
				.list();
			}
			
		});
	}
	
	/**
	 * Busca todos los pagos aplicados para el cliente en el periodo determinado
	 * @param c
	 * @param p
	 * @return
	 */
	public List<PagoM> buscarSaldosAFavor(final Cliente c){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from PagoM p " +
						" left join fetch p.cliente c" +						
						" where c=:cliente " +						
						"   and p.disponible >0")
				.setEntity("cliente", c)
				.list();
			}
			
		});
	}
	
	/**
	 * Busca todos los pagos aplicados 
	 * @param c
	 * @param p
	 * @return
	 */
	public List<Map<String, Object>> buscarSaldosAFavor(){
		return ServiceLocator.getJdbcTemplate().queryForList(
				"SELECT A.TIPO,B.CLAVE,B.NOMBRE,A.IMPORTE,(SELECT NVL(SUM(C.IMPORTE),0) FROM SW_PAGOS C WHERE A.PAGOM_ID=C.PAGOM_ID AND C.YEAR>2007) AS APLICADO,A.IMPORTE-(SELECT NVL(SUM(C.IMPORTE),0) FROM SW_PAGOS C WHERE A.PAGOM_ID=C.PAGOM_ID AND C.YEAR>2007) AS DISPONIBLE,A.FORMADP,A.REFERENCIA,A.COMENTARIO,A.FECHA,A.ORIGEN_ID,A.NOTA_ID FROM SW_PAGOM A JOIN SW_CLIENTES B ON(A.CLIENTE_ID=B.CLIENTE_ID) WHERE  A.IMPORTE-(SELECT NVL(SUM(C.IMPORTE),0) FROM SW_PAGOS C WHERE A.PAGOM_ID=C.PAGOM_ID AND C.YEAR>2007)>0"
				);
	}
	
	
	/**
	 * Busca todos los pagos aplicados para el periodo determinado
	 * 
	 * @param c
	 * @param p
	 * @return
	 */
	public List<Pago> buscarPagosAplicados(final Periodo p){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from Pago p" +
						" left join fetch p.cliente c " +
						" left join fetch p.pagoM " +
						" left join fetch p.venta v" +
						" where p.fecha between :f1 and :f2" +
						" and p.pagoM is not null")				
				.setParameter("f1", p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2", p.getFechaFinal(),Hibernate.DATE)
				.list();
			}
			
		});
	}
	
	/**
	 * Busca todos los pagos aplicados para el periodo determinado incluidos los del sistema anterior
	 * @param c
	 * @param p
	 * @return
	 */
	public List<Pago> buscarPagosAplicadosOld(final Periodo p){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from Pago p" +
						" left join fetch p.cliente c " +
						" left join fetch p.pagoM " +
						" left join fetch p.venta v" +
						" where p.fecha between :f1 and :f2" +
						" order by p.fecha,p.numero" )
				.setParameter("f1", p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2", p.getFechaFinal(),Hibernate.DATE)
				.list();
			}
			
		});
	}
	
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void eliminarPago(Pago pago) {
		if(pago.getPagoM()!=null){			
			PagoM m=pago.getPagoM();
			refresh(m);
			m.eliminarPago(pago);			
			getPagoMDao().salvar(m);
		}
	}
	
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void eliminarPagoM(PagoM pago) {
		getPagoMDao().eliminar(pago);
	}
	
	public void eliminarPagoM(final Long id){
		eliminarPagoM(getPagoMDao().buscarPorId(id));
	}

	public VentasManager getVentasManager() {
		return ventasManager;
	}
	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}

	

	public List<Pago> buscarPagos(final Date fecha,final String origen) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final int year=Periodo.obtenerYear(fecha);				
				final List<Pago> pagos= session.createQuery(
						"from Pago p  " +
						" left join fetch p.venta " +
						" left join fetch p.nota" +
						" where p.year=:year " +
						"   and p.fecha=:fecha" +
						"   and p.origen=:origen" +
						"   and p.formaDePago in (:f1,:f2,:f3,:f4,:f5,:f6,:f7,:f8) " 
						
						)
						.setInteger("year",year)
						.setParameter("fecha", fecha)
						.setString("origen", origen)
						.setString("f1", "H")
						.setString("f2", "N")
						.setString("f3", "Y")
						.setString("f4", "E")
						.setString("f5", "C")						
						.setString("f6", "O")
						.setString("f7", "B")
						.setString("f8", "Q")
						.list();
				for(Pago p:pagos){
					p.getCuenta();
					if(p.getVenta()!=null)
						p.getVenta().getYear();
					p.getNota();
				}
				return pagos;
			}			
		});		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.managers.PagosManager#buscarPagosM(java.util.Date, java.lang.String)
	 */
	public List<PagoM> buscarPagosM(final Date fecha,final String origen){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final int year=Periodo.obtenerYear(fecha);				
				final ScrollableResults rs=session.createQuery(
						"from PagoM p " +
						"where p.year=:year " +
						"  and p.fecha=:fecha" 
						)
						.setInteger("year",year)
						.setParameter("fecha", fecha)						
						.scroll();
				final List<PagoM> pagos=new ArrayList<PagoM>();
				while(rs.next()){
					PagoM p=(PagoM)rs.get()[0];
					switch (p.getFormaDePago()) {
					case H:
					case N:
					case Y:
					case E:
					case C:
						p.getCuenta();
						pagos.add(p);
						continue;
					default:
						continue;
					}
				}
				return pagos;
			}			
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.managers.PagosManager#buscarPagosDeEefectivamenteCobrados(java.util.Date, java.lang.String)
	 */
	public List<PagoM> buscarPagosEfectivamenteCobrados(final Date fecha){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final int year=Periodo.obtenerYear(fecha);				
				final ScrollableResults rs=session.createQuery(
						"from PagoM p " +
						"where p.year=:year " 
						+"  and p.fecha=:fecha"
						)
						.setInteger("year",year)
						.setParameter("fecha", fecha)						
						.scroll();
				final List<PagoM> pagos=new ArrayList<PagoM>();
				while(rs.next()){
					PagoM p=(PagoM)rs.get()[0];					
					switch (p.getFormaDePago()) {
					case H:
					case N:
					case Y:
					case E:					
						p.getCuenta();
						pagos.add(p);
						continue;
					default:
						continue;
					}
				}
				return pagos;
			}			
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.managers.PagosManager#buscarPagosConTarjeta(java.util.Date)
	 */
	public List<PagoM> buscarPagosConTarjeta(final Date fecha){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final int year=Periodo.obtenerYear(fecha);				
				final ScrollableResults rs=session.createQuery(
						"from PagoM p " +
						"where p.year=:year " 
						+"  and p.fecha=:fecha"
						)
						.setInteger("year",year)
						.setParameter("fecha", fecha)						
						.scroll();
				final List<PagoM> pagos=new ArrayList<PagoM>();
				while(rs.next()){
					PagoM p=(PagoM)rs.get()[0];					
					switch (p.getFormaDePago()) {
					case C:
					case B:
					case Q:
						p.getCuenta();
						pagos.add(p);
						continue;
					default:
						continue;
					}
				}
				return pagos;
			}			
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.managers.PagosManager#buscarSaldosAFavor(java.util.Date)
	 */
	public List<PagoM> buscarSaldosAFavor(Date fecha) {
		return getPagoMDao().buscarSaldosAFavor(fecha);
	}

	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.managers.PagosManager#buscarPagosConOtros(java.util.Date)
	 */
	public List<PagoConOtros> buscarPagosConOtros(Date fecha) {
		return getPagoMDao().buscarPagosConOtros(fecha);
	}

	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.managers.PagosManager#buscarPagosDeMenos(java.util.Date)
	 */
	public List<PagoM> buscarPagosDeMenos(Date fecha) {
		return getPagoMDao().buscarPagosDeMenos(fecha);
	}
	

	public List<PagoM> buscarPagosAplicadosConCheque(String cliente) {
		return getPagoMDao().buscarPagosAplicadosConCheque(cliente);
	}

	public static void main(String[] args) {
		Cliente c=ServiceLocator.getClienteDao().buscarPorClave("A010406");
		List<Pago> pagos=ServiceLocator.getPagosManager().buscarPagosAplicados(c, Periodo.periodoDeloquevaDelYear());
		System.out.println("Pagos: "+pagos.size());
		for(Pago p:pagos){
			System.out.println("Clave: "+p.getClave());
		}
	}

}
