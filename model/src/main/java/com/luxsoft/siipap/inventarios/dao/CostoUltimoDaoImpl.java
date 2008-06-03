package com.luxsoft.siipap.inventarios.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.dao.GenericHibernateDao;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.CostoUltimo;

public class CostoUltimoDaoImpl extends GenericHibernateDao<CostoUltimo,Long>
	implements CostoUltimoDao{

	public CostoUltimoDaoImpl() {
		super(CostoUltimo.class);
	}
	
	/**
	 * Busca el costo de ultima compra para un articulo determinado
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoUltimo buscarCostoUltimo(final String articulo,final Periodo periodo) {
		String pp=CostoUltimo.format(periodo);
		return buscarCostoUltimo(articulo,pp);
	}

	/**
	 * Busca el costo de ultima compra para un articulo determinado
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoUltimo buscarCostoUltimo(final String articulo,final String periodo) {
		Object costo=getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				return sess.createCriteria(CostoUltimo.class)
					.add(Restrictions.eq("periodo",periodo))
					.createAlias("articulo","a")
					.add(Restrictions.eq("a.clave",articulo))
					.setMaxResults(1)
					.uniqueResult();
			}
		});
		return (CostoUltimo)costo;
	}
	
	/**
	 * Busca el costo de ultima compra para un articulo determinado sin importar el periodo
	 * 
	 * @param articulo
	 * @param periodo
	 * @return
	 */
	public CostoUltimo buscarCostoUltimo(final String articulo){
		Object costo=getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				return sess.createCriteria(CostoUltimo.class)					
					.createAlias("articulo","a")
					.add(Restrictions.eq("a.clave",articulo))
					.setMaxResults(1)
					.uniqueResult();
			}
		});
		return (CostoUltimo)costo;
	}
	
	@SuppressWarnings("unchecked")
	public List<CostoUltimo> buscarCostosUltimos(){
		String hql="from CostoUltimo cu join fetch cu.articulo";
		return getHibernateTemplate().find(hql);
	}
	

}
