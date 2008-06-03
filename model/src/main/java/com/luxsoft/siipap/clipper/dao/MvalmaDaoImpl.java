package com.luxsoft.siipap.clipper.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.clipper.domain.Mvalma;
import com.luxsoft.siipap.dao.GenericHibernateDao;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.inventarios.domain.Movimiento;
import com.luxsoft.siipap.inventarios.domain.Salida;

@SuppressWarnings("unchecked")
public class MvalmaDaoImpl extends GenericHibernateDao<Mvalma,Long>
	implements MvalmaDao{

	public MvalmaDaoImpl() {
		super(Mvalma.class);
	}
	
	/**
	 * Busca las instancias de Mvalma para la fecha 
	 * especificada
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Mvalma> buscarPorFecha(final Date fecha){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				Criteria cr=sess.createCriteria(Mvalma.class)
					.setFetchMode("partidas",FetchMode.JOIN)
					.add(Restrictions.eq("MVAFECHA",fecha));
					
				return cr.list();
			}			
		});
	}
	
	public List<Mvalma> buscar(final Periodo p, final String tipo){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				String hql="from Mvalma m where m.MVATIPO=:tipo and m.MVAFECHA between :f1 and :f2 order by m.MVAFECHA";
				return sess.createQuery(hql)
				.setParameter("tipo",tipo)
				.setParameter("f1",p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2",p.getFechaFinal(),Hibernate.DATE)
				.list();
			}			
		});
	}
	
	/**
	 * Busca los movimientos pendientes de asignar
	 * maestro de tipo MVALMA
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Movimiento> buscarPendientes(final Date fecha){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				Criteria cr=sess.createCriteria(Movimiento.class)
					.add(Restrictions.eq("ALMFECHA",fecha))
					.add(Restrictions.in("ALMTIPO",MvalmaDao.ALMTIPOS.tipos()))
					.add(Restrictions.isNull("mvalma"));
				return cr.list();
			}			
		});
	}
	
	public List<Movimiento> buscarPendientes(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				String hql="from Movimiento m where m.mvalma is null and m.ALMTIPO not in (?,?,?,?,?)";
				List l=sess.createQuery(hql)
				.setString(0,"COM")
				.setString(1,"DEC")
				.setString(2,"FAC")
				.setString(3,"RMD")
				.setString(4,"INI")
				.list();
				return l;
			}			
		});
	}
	
	/**
	 * Vincula todos los TPS con TPE
	 * 
	 * @param fecha
	 * @return
	 */
	public void enlazarTraslados(final Date fecha){
		
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				Criteria c=sess.createCriteria(Entrada.class)
					.setFetchMode("mvalma",FetchMode.JOIN)
					.add(Restrictions.eq("ALMFECHA",fecha))
					.add(Restrictions.eq("ALMTIPO","TPE"))
					.addOrder(Order.asc("ALMFEREAL"))
					.addOrder(Order.asc("ALMSEGS"));
				List<Entrada> tpes=c.list();
				for(Entrada e:tpes){
					if(logger.isDebugEnabled()){
						logger.debug("Procesando: "+e);
					}
					if(e.getMvalma()==null){
						logger.error("TPE sin vinculo a Mvalma.  TPE:"+e);
						continue;
					}
					Integer sucursal=new Long(e.getMvalma().getMVASUCUAT()).intValue();
					Long numero=e.getMvalma().getMVANUMEAT();
					String tipo=e.getMvalma().getMVATIPMAT();
					String articulo=e.getALMARTIC().trim();
					Integer renglon=e.getALMRENGL();
					List<Salida> tpss=sess.createCriteria(Salida.class)
					.add(Restrictions.eq("ALMSUCUR",sucursal))
					.add(Restrictions.eq("ALMNUMER",numero))
					.add(Restrictions.eq("ALMTIPO",tipo))
					.add(Restrictions.eq("ALMARTIC",articulo))
					.add(Restrictions.eq("ALMRENGL",renglon))
					.setMaxResults(1)
					.list();
					if(tpss.size()==0){
						logger.error("TPE sin TPS: "+e+"\n\t Supuesto Tps para Sucursal: "
								+sucursal+" numero:"+numero+" tipo:"+tipo+" articulo:"+articulo+" renglon:"+renglon);
						continue;
					}else if(tpss.size()>1){
						logger.error("TPE con varios TPS's. TPE:"+e+"\n\tTPS's:"+tpss);
						continue;
					}
					Salida tps=tpss.get(0);
					if(logger.isDebugEnabled()){
						logger.debug("\tOrigen de tpe: "+tps);
					}
					e.setOrigen(tps);
					tps.setDestino(e);
				}					
				return null;
			}			
		});
	}
	
	/**
	 * Busca los movimientos TPE y TPS 
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Movimiento> buscarTraslados(final Date fecha){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				List<Entrada> tpes=sess.createCriteria(Entrada.class)
				.setFetchMode("origen",FetchMode.JOIN)
				.add(Restrictions.eq("ALMFECHA",fecha))
				.add(Restrictions.in("ALMTIPO",new String[]{"TPE","TPS"}))
				.addOrder(Order.asc("ALMFEREAL"))
				.addOrder(Order.asc("ALMSEGS"))
				.list();
			
				List<Entrada> tpss=sess.createCriteria(Salida.class)
				.setFetchMode("destino",FetchMode.JOIN)
				.add(Restrictions.eq("ALMFECHA",fecha))
				.add(Restrictions.in("ALMTIPO",new String[]{"TPE","TPS"}))
				.addOrder(Order.asc("ALMFEREAL"))
				.addOrder(Order.asc("ALMSEGS"))
				.list();
				
				List<Movimiento> movs=new ArrayList<Movimiento>();
				movs.addAll(tpes);
				movs.addAll(tpss);
				return movs;
			}			
		});
	}
	
	public Mvalma buscarDocumento(final Integer sucursal,final String tipo,final Long numero){
		return (Mvalma)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				final String hql="from Mvalma a where a.MVASUCUR=:sucur and a.MVATIPO=:tipo and a.MVANUMER=:numero";
				Mvalma m=(Mvalma)sess.createQuery(hql)
				.setParameter("sucur",sucursal)
				.setParameter("tipo",tipo)
				.setParameter("numero",numero)
				.setMaxResults(1)
				.uniqueResult();
				return m;
			}			
		});
	}

}
