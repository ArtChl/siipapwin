package com.luxsoft.siipap.clipper.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.clipper.domain.Mvcomp;
import com.luxsoft.siipap.dao.GenericHibernateDao;
import com.luxsoft.siipap.inventarios.domain.Movimiento;

@SuppressWarnings("unchecked")
public class MvcompDaoImpl extends GenericHibernateDao<Mvcomp,Long> 
	implements MvcompDao{

	public MvcompDaoImpl() {
		super(Mvcomp.class);
	}
	
	/**
	 * Busca las entradas para determinado dia
	 * @param date
	 * @return
	 */
	public List<Mvcomp> buscaEntradas(final Date date){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				Criteria criteria=sess.createCriteria(Mvcomp.class)
					.add(Restrictions.eq("MVCFECHA",date))
					.setFetchMode("partidas",FetchMode.JOIN);
				return criteria.list();
			}			
		});
	}
	
	/**
	 * Busca los movimientos de tipo  COM,DEC que no tiene padre
	 *  
	 * @param date
	 * @return
	 */
	public List<Movimiento> buscarPartidasPendientes(final Date date){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				Criteria criteria=sess.createCriteria(Movimiento.class)
				.add(Restrictions.eq("ALMFECHA",date))
				.add(Restrictions.in("ALMTIPO",new String[]{"COM","DEC"}))
				.add(Restrictions.isNull("mvcomp"));
				return criteria.list();
			}			
		});
	}
	
	

}
