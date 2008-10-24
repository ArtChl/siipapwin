package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.domain.Anticipo;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.utils.StringEnumUserType;

/**
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class PagoMDaoImpl extends HibernateDaoSupport implements PagoMDao{

	public PagoM buscarPorId(Long id) {
		return (PagoM)getHibernateTemplate().get(PagoM.class, id);
	}

	public void eliminar(PagoM pd) {
		getHibernateTemplate().delete(pd);
	}

	//@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void salvar(final PagoM pd) {
		//Assert.isTrue(!pd.getPagos().isEmpty(),"No se puede salvar un PagosConDescuento sin pagos");
		pd.setModificado(new Date());
		getHibernateTemplate().saveOrUpdate(pd);
		
	}
	
	
	public List<PagoM> buscarDisponibles(final Cliente cliente){
		final String hql="from PagoM p " +
		"left join fetch p.cliente c " +
		" where c.clave=? " +
		" and p.disponible>0 ";		
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setEntity(0, cliente)
				.list();
			}
			
		});
	}
	
	public List<PagoM> buscarDisponibles(final String cliente){
		final String hql="from PagoM p " +
				"left join fetch p.cliente c " +
				" where c.clave=?" +
				" and p.disponible>0";
		return getHibernateTemplate().find(hql, cliente);
	}
	
	
	
	public List<PagoM> buscarSaldosAFavor(final Date fecha) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final ScrollableResults rs=session.createCriteria(PagoM.class)
					.add(Restrictions.eq("fecha", fecha))
					.scroll();
				final List<PagoM> saldos=new ArrayList<PagoM>();
				while(rs.next()){
					PagoM p=(PagoM)rs.get()[0];
					
					//Buscar los hijos de este
					List<PagoM> children=session
					.createQuery("from PagoConOtros pp where pp.origen=:origen and pp.fecha<=:fecha")
					.setParameter("fecha", fecha)
					.setEntity("origen", p)
					.list();
					if(!children.isEmpty()){
						p.setChildren(children);
					}
					final CantidadMonetaria saldo=p.getSaldoAl(fecha);
					if(saldo.amount().doubleValue()>0){
						/*
						if(p.getClave().equals("G010226")){
							System.out.println("Disponible:" +p.getDisponible());
							System.out.println("Saldo a la fecha:" +p.getSaldo());
						}
						*/
						saldos.add(p);
						p.setSaldo(saldo.amount());
					}
				}
				Collections.sort(saldos, new Comparator<PagoM>(){
					public int compare(PagoM o1, PagoM o2) {
						return o1.getClave().compareTo(o2.getClave());
					}
					
				});
				for(PagoM p:saldos){
					p.getCuenta();
				}
				return saldos;
			}			
		});
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.PagoMDao#buscarPagosConOtros(java.util.Date)
	 */
	public List<PagoConOtros> buscarPagosConOtros(final Date fecha) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<PagoConOtros> otros=session.createCriteria(PagoConOtros.class)
				.add(Restrictions.eq("fecha", fecha))
				.list();
				for(PagoConOtros p:otros){
					p.getCuenta();
					Hibernate.initialize(p.getPagos());
				}
				return otros;
			}
			
		});
	}
	
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.PagoMDao#buscarAnticipos(java.util.Date)
	 * 
	 */
	public List<Anticipo> buscarAnticipos(final Date fecha) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Anticipo.class)
				.add(Restrictions.eq("fecha", fecha))
				.list();
			}			
		});
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.PagoMDao#buscarPagosDeMenos(java.util.Date)
	 */
	public List<PagoM> buscarPagosDeMenos(final Date fecha) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<PagoM> list=session.createQuery("from PagoM p " +
						" where p.fecha=:fecha " 
						//+"and p.formaDePago=:fp"
						)
				.setParameter("fecha", fecha)
				//.setParameter("fp", FormaDePago.D,Hibernate.custom(StringEnumUserType.class))
				.list();
				final List<PagoM> pagos=new ArrayList<PagoM>();
				for(PagoM p:list){
					if(p.getFormaDePago().equals(FormaDePago.D)){
						p.getCuenta();
						pagos.add(p);
					}
				}
				Collections.sort(pagos, new Comparator<PagoM>(){
					public int compare(PagoM o1, PagoM o2) {
						return o1.getClave().compareTo(o2.getClave());
					}
					
				});
				return pagos;
			}			
		});
	}
	
	public List<PagoM> buscarPagosEfectivos(final Date fecha,final String origen){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<PagoM> list=session.createQuery("from PagoM p " +
						" where p.fecha=:fecha " 
						//+"and p.formaDePago=:fp"
						)
				.setParameter("fecha", fecha)
				//.setParameter("fp", FormaDePago.D,Hibernate.custom(StringEnumUserType.class))
				.list();
				final List<PagoM> pagos=new ArrayList<PagoM>();
				for(PagoM p:list){
					if(p.getFormaDePago().equals(FormaDePago.D)){
						p.getCuenta();
						pagos.add(p);
					}
				}
				Collections.sort(pagos, new Comparator<PagoM>(){
					public int compare(PagoM o1, PagoM o2) {
						return o1.getClave().compareTo(o2.getClave());
					}
					
				});
				return pagos;
			}			
		});
	}
	
	
	/**
	 * 
	 */
	public List<PagoM> buscarPagosAplicadosConCheque(final String cliente) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<PagoM> list=session.createQuery("from PagoM p " +
						" where p.clave=:clave "
						)
				.setParameter("clave", cliente)
				
				.list();
				final List<PagoM> pagos=new ArrayList<PagoM>();
				for(PagoM p:list){
					if(p.getFormaDePago().equals(FormaDePago.H)
							|| p.getFormaDePago().equals(FormaDePago.Y)){
						p.getCuenta();
						pagos.add(p);
					}
				}
				Collections.sort(pagos, new Comparator<PagoM>(){
					public int compare(PagoM o1, PagoM o2) {
						return o1.getClave().compareTo(o2.getClave());
					}
					
				});
				return pagos;
			}			
		});
	}

	public List<PagoM> buscarPagos(final Date fecha,final FormaDePago forma,final String origen){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<PagoM> list=session.createQuery("from PagoM p " +
						" where p.fecha=:fecha " 
						//+"and p.formaDePago=:fp"
						)
				.setParameter("fecha", fecha,Hibernate.DATE)
				//.setParameter("fp", FormaDePago.H,Hibernate.custom(StringEnumUserType.class))
				.list();
				final List<PagoM> pagos=new ArrayList<PagoM>();
				for(PagoM p:list){
					if(p.getFormaDePago().equals(forma)){
						p.getCuenta();
						pagos.add(p);
					}
				}
				Collections.sort(pagos, new Comparator<PagoM>(){
					public int compare(PagoM o1, PagoM o2) {
						return o1.getClave().compareTo(o2.getClave());
					}
					
				});
				return pagos;
			}			
		});
	}

	public static void main(String[] args) {
		PagoMDao dao=(PagoMDao)ServiceLocator.getDaoContext().getBean("pagoMDao");
		/**
		List<PagoM> l=dao.buscarDisponibles("A010406");
		for(PagoM p:l){
			System.out.println(p);
		}
		**/
		List<PagoM> l=dao.buscarSaldosAFavor(DateUtils.obtenerFecha("02/01/2008"));
		for(PagoM p:l){
			System.out.println(l);
		}
	}
	

}
