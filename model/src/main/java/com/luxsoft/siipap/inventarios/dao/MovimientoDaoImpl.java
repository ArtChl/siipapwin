package com.luxsoft.siipap.inventarios.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.dao.GenericHibernateDao;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.inventarios.domain.Movimiento;
import com.luxsoft.siipap.inventarios.domain.Salida;
import com.luxsoft.siipap.utils.DateUtils;

@SuppressWarnings("unchecked")
public class MovimientoDaoImpl extends GenericHibernateDao<Movimiento,Long>
							implements MovimientoDao{

	public MovimientoDaoImpl() {
		super(Movimiento.class);
	}
	
	public List<Movimiento> buscarPorDia(final Date dia){
		return getHibernateTemplate().find("from Movimiento m where m.ALMFECHA=?", dia);
	}
	
	/**
	 * Busca las partidas de un documento en especifico
	 * 
	 * @param sucursal
	 * @param tipo
	 * @param numero
	 * @return
	 */
	public List<Movimiento> buscarDocumento(final Integer sucursal,final String tipo,final Long numero){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Movimiento.class)
					.add(Restrictions.eq("ALMSUCUR",sucursal))
					.add(Restrictions.eq("ALMTIPO",tipo))
					.add(Restrictions.eq("ALMNUMER",numero))
					.list();
			}			
		});
	}
	
	/**
	 * Localiza la entrada de la remision determinada
	 */
	public Entrada buscarEntradaPorRemision(final Integer sucursal,final Long facrem,final String articulo){
		final String hql="select e from Entrada e " +
				"where e.ALMARTIC=:articulo" +
				"  and e.ALMSUCUR=:sucursal" +
				"  and e.mvcomp.MVCFACREM=:facrem";
		List<Entrada> beans=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
					.setParameter("articulo",articulo)
					.setParameter("sucursal",sucursal)
					.setParameter("facrem",facrem)					
					.list();
			}			
		});
		if(beans.isEmpty()){
			logger.warn("No encontro Entrada para la remision: "+facrem +" de sucursal: "+sucursal);
			return null;
		}else if(beans.size()>1){
			logger.warn("Encontro mas de una  Entrada para la remision: "+facrem +" de sucursal: "+sucursal);
			return beans.get(0);
		}else
			return beans.get(0);
		
	}
	
	/**
	 * Busca las entradas de un articulo por periodo y tipo
	 * @param articulo
	 * @param tipo
	 * @param periodo
	 * @return
	 */
	public List<Entrada> buscarEntradas(final String articulo,final String tipo,final Periodo periodo){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				return sess.createCriteria(Entrada.class)
						.setFetchMode("origen",FetchMode.JOIN)
						.add(Restrictions.eq("ALMARTIC",articulo))
						.add(Restrictions.eq("ALMTIPO",tipo))
						.add(Restrictions.between("ALMFECHA",periodo.getFechaInicial(),periodo.getFechaFinal()))
						.list();
			}
			
		});
	}
	
	/**
	 * Busca las entradas existentes hasta la fecha indicada
	 * Util en el caluclo de costo promedio
	 * 
	 * @param articulo
	 * @param tipo
	 * @param hasta
	 * @return
	 */
	public List<Entrada> buscarEntradas(final String articulo,final String tipo,final Date hasta){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				return sess.createCriteria(Entrada.class)
						.setFetchMode("origen",FetchMode.JOIN)
						.add(Restrictions.eq("ALMARTIC",articulo))
						.add(Restrictions.eq("ALMTIPO",tipo))
						.add(Restrictions.le("ALMFECHA",hasta))
						.list();
			}
		});
	}
	
	/**
	 * Busca las entradas del articulo para el periodo
	 * 
	 * @param art
	 * @param p
	 * @return
	 */
	public List<Entrada> buscarEntradas(final String art,final Periodo p){
		final String hql="select m from Entrada m" +
		" left join fetch m.origen" +
		" where m.ALMARTIC=:articulo" +
		"   and m.ALMFECHA between :fechaIni and :fechaFin ";
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter("articulo",art)
				.setParameter("fechaIni",p.getFechaInicial(),Hibernate.DATE)
				.setParameter("fechaFin",p.getFechaFinal(),Hibernate.DATE)					
				.list();
			}
		});
	}
	
	/**
	 * Buscar las salidas en el periodo
	 * 
	 * @param articulo
	 * @param p
	 * @return
	 */
	public List<Salida> buscarSalidas(final String articulo,final Periodo p){
		final String hql="select s from Salida s" +
		" left join s.mvcomp c" +
		"  where s.ALMARTIC=:articulo" +
		"  	and s.ALMFECHA between :fechaIni and :fechaFin";
		return getHibernateTemplate().executeFind(new HibernateCallback(){
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					return session.createQuery(hql)
					.setParameter("articulo",articulo)
					.setParameter("fechaIni",p.getFechaInicial(),Hibernate.DATE)
					.setParameter("fechaFin",p.getFechaFinal(),Hibernate.DATE)					
					.list();
				}
		});
	}
	
	/**
	 * Suma las entradas del articulo en el periodo  y sucursal
	 * @param a
	 * @param p
	 * @param sucursal
	 * @return
	 */
	public Long entradas(final String a,final Periodo p,final Integer sucursal){
		final String hql="select sum(m.ALMCANTI) from Entrada m" +
		" where m.ALMARTIC=:articulo"+
		"   and m.ALMSUCUR=:sucursal"+
		"   and m.ALMFECHA between :fechaIni and :fechaFin ";
		List<Long> l=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter("articulo",a)
				.setParameter("sucursal",sucursal)
				.setParameter("fechaIni",p.getFechaInicial(),Hibernate.DATE)
				.setParameter("fechaFin",p.getFechaFinal(),Hibernate.DATE)
				.list();
			}
		});
		if(l.size()==1){
			Long val=l.get(0);
			if(val==null)
				return new Long(0);
			return val;
		}
		return new Long(0);
	}
	
	
	
	/**
	 * Suma las salidas del articulo en el periodo y sucursal
	 * @param a
	 * @param p
	 * @param sucursal
	 * @return
	 */
	public Long salidas(final String a,final Periodo p,final Integer sucursal){
		final String hql="select sum(m.ALMCANTI) from Salida m" +
		" where m.ALMARTIC=:articulo"+
		"   and m.ALMSUCUR=:sucursal"+
		"   and m.ALMFECHA between :fechaIni and :fechaFin ";
		List<Long> l=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter("articulo",a)
				.setParameter("sucursal",sucursal)
				.setParameter("fechaIni",p.getFechaInicial(),Hibernate.DATE)
				.setParameter("fechaFin",p.getFechaFinal(),Hibernate.DATE)
				.list();
			}
		});
		if(l.size()==1){
			Long val=l.get(0);
			if(val==null)
				return new Long(0);
			return val.longValue();
		}
		return new Long(0);
	}
	
	
	
	/**
	 * Calcula la existencia del articulo a la fecha en la sucursal la fecha es inclusiva
	 * 
	 * @param articulo
	 * @param fecha
	 * @param sucursal
	 * @return
	 */
	public Long existencia(final String articulo, final Date fecha,
			final Integer sucursal) {

		final String hql = "select sum(m.ALMCANTI) from Movimiento m "
				+ "where m.ALMSUCUR=:sucursal " + "  and m.ALMARTIC=:articulo"
				+ "  and m.ALMFECHA<=:fecha ";
		List<Long> l = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createQuery(hql).setParameter(
								"sucursal", sucursal).setString("articulo",
								articulo)
						// .setParameter("fecha",fecha,Hibernate.DATE)
								.setDate("fecha", fecha).setMaxResults(1)
								.list();
					}
				});
		Long val = l.get(0);
		if (val != null)
			return new Long(val.longValue());
		return new Long(0);
	}
	
	/**
	 * Calcula la existencia del articulo a la fecha en la sucursal
	 * sin incluir la fecha indicada es decir antes de la fecha
	 * 
	 * @param articulo
	 * @param fecha
	 * @param sucursal
	 * @return
	 */
	public Long saldo(final String articulo, final Date antesDe,	final  Integer sucursal){
		final String hql = "select sum(m.ALMCANTI) from Movimiento m "
			+ "where m.ALMSUCUR=:sucursal " 
			+ "  and m.ALMARTIC=:articulo"
			+ "  and m.ALMFECHA<:fecha ";
		List<Long> l = getHibernateTemplate().executeFind(
			new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					return session.createQuery(hql)
						.setParameter("sucursal", sucursal)
						.setString("articulo",articulo)
						.setParameter("fecha",antesDe,Hibernate.DATE)
						.setMaxResults(1)
						.list();
				}
			});
		Long val = l.get(0);
		if (val != null)
			return new Long(val.longValue());
		return new Long(0);
	}
	
	/**
	 * Calcula la existencia en millares del articulo a la fecha en la sucursal 
	 * @param articulo
	 * @param fecha
	 * @param sucursal
	 * @return
	 */
	public BigDecimal existenciaEnUnidad(final String articulo, final Date fecha,	final  Integer sucursal){
		final String hql = "select sum(m.ALMCANTI/ALMUNIXUNI) from Movimiento m "
			+ "where m.ALMARTIC=:articulo " + "  and m.ALMSUCUR=:sucursal"
			+ "  and m.ALMFECHA<=:fecha ";
		List<Long> l = getHibernateTemplate().executeFind(
			new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					return session.createQuery(hql)
						.setParameter("sucursal", sucursal)
						.setString("articulo",articulo)
						.setParameter("fecha",fecha,Hibernate.DATE)
						.setMaxResults(1)
						.list();
				}
			});
		Long val = l.get(0);
		if (val != null)
			return BigDecimal.valueOf(val.doubleValue());
		return BigDecimal.ZERO;
	}

	/**
	 * Calcula la existencia del articulo a la fecha 
	 * Se ocupa en los acumulados
	 * 
	 * @param articulo
	 * @param fecha
	 * @param sucursal
	 * @return
	 */
	public Long existencia(final String articulo, final Date fecha){
		final String hql = "select sum(m.ALMCANTI) from Movimiento m "
			+ "where m.ALMARTIC=:articulo"
			+ "  and m.ALMFECHA<=:fecha ";
		List<Long> l = getHibernateTemplate().executeFind(
			new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
						return session.createQuery(hql)
						.setString("articulo",articulo)
						.setParameter("fecha", fecha,Hibernate.DATE)
						.setMaxResults(1)
						.list();
				}
			});
		Long val = l.get(0);
		if (val != null)
			return new Long(val.longValue());
		return new Long(0);
	}
	
	
	
	
	/**
	 * Busca las entradas sin costo
	 * Las entradas regresadas estan tienen inicializadas sus relaciones
	 * many-to-one :
	 * 		Precio
	 * 		Facxpde  TODO: To be implemented
	 * 
	 * @param dia
	 * @return
	 */
	public List<Entrada> buscarEntradasSinCosto(final Date dia){
		//final String hql="from Entrada e where e.ALMFECHA=:date and (e.costo=0 or e.costo is null)";
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Entrada.class)
						.setFetchMode("precio",FetchMode.JOIN)
						.setFetchMode("origen",FetchMode.JOIN)
						.createAlias("articulo","a")
						.add(Restrictions.not(Restrictions.like("ALMARTIC","VIRU",MatchMode.ANYWHERE)))
						.add(Restrictions.eq("a.afectaInventario",Boolean.TRUE))
						.add(Restrictions.eq("ALMFECHA",dia))
						.add(Restrictions.or(
								Restrictions.eq("costo",CantidadMonetaria.pesos(0)),
								Restrictions.isNull("costo")
						)).list();
				
			}
			
		});
	}
	
	/**
	 * Busca las entradas de un periodo (mes/año)
	 * 
	 * @param p
	 * @return
	 */
	public List<Entrada> buscarEntradas(final String periodo){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Entrada.class)						
						//.setFetchMode("origen",FetchMode.JOIN)
						.setFetchMode("mvcomp",FetchMode.JOIN)
						.add(Restrictions.eq("periodo",periodo))
						//.setMaxResults(1000)
						.list();
			}
			
		});
	}
	
	/**
	 * Busca las entradas de un periodo 
	 * 
	 * @param p
	 * @return
	 */
	public List<Entrada> buscarEntradas(final Periodo p){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Entrada.class)						
						//.setFetchMode("origen",FetchMode.JOIN)
						.setFetchMode("mvcomp",FetchMode.JOIN)
						.add(Restrictions.between("ALMFECHA",p.getFechaInicial(),p.getFechaFinal()))
						//.setMaxResults(1000)
						.list();
			}
			
		});
	}
	
	/**
	 * Busca las entradas de un periodo para un tipo determinado
	 * 
	 * @param p
	 * @param tipo
	 * @return
	 */
	public List<Entrada> buscarEntradas(final Periodo p,final String tipo){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Entrada.class)
						.setFetchMode("mvcomp",FetchMode.JOIN)
						.add(Restrictions.eq("ALMTIPO",tipo))
						.add(Restrictions.between("ALMFECHA",p.getFechaInicial(),p.getFechaFinal()))						
						.list();
			}
			
		});
	}
	
	/**
	 * Busca las entradas por folio
	 * @param numero
	 * @return
	 */
	public List<Entrada> buscarEntradasPorFolio(final Long numero){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Entrada.class)
						.setFetchMode("mvcomp",FetchMode.JOIN)
						.add(Restrictions.eq("ALMNUMER",numero))
						.setMaxResults(200)
						.list();
			}
			
		});
	}

	public int contarRegistros(Date fecha) {
		List l= getHibernateTemplate().find("select count(*) from Movimiento m where m.ALMFECHA=?", fecha);
		return (Integer)l.get(0);
	}
	
	public List<Salida> buscarSalidas(final Periodo p,final String... tipos){
		List<Salida> salidas=new ArrayList<Salida>();
		for(String tipo:tipos){
			salidas.addAll(buscarSalidas(p, tipo));
		}
		return salidas;
	}
	
	private List<Salida> buscarSalidas(final Periodo p,final String tipo){
		List<Salida> salidas=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Salida s " +
						" left join fetch s.destino" +						
						" where s.ALMFECHA between :fechaIni and :fechaFin " +
						" and s.ALMTIPO=:tipo";
				return session.createQuery(hql)
				.setParameter("tipo",tipo)				
				.setParameter("fechaIni",p.getFechaInicial(),Hibernate.DATE)
				.setParameter("fechaFin",p.getFechaFinal(),Hibernate.DATE)
				.list();
			}
			
		});
		return salidas;
	}
	
	
	
	
	
	public List<Entrada> buscarEntradas(final String tipo,final String articulo,final Integer sucursal,final Periodo p){
		List<Entrada> entradas=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Entrada s " +
						" left join fetch s.origen" +						
						" where s.ALMARTIC=:articulo" +
						" and s.ALMSUCUR=:sucursal" +
						" and s.ALMFECHA >:fechaIni" +
						" and s.ALMTIPO=:tipo";
				return session.createQuery(hql)
				.setParameter("tipo",tipo)				
				.setParameter("fechaIni",DateUtils.obtenerFecha("01/11/2006"),Hibernate.DATE)				
				.setInteger("sucursal", sucursal)
				.setString("articulo", articulo)
				.list();
			}
			
		});
		return entradas;
	}
}
