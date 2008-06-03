package com.luxsoft.siipap.cxp.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxp.domain.Requisicion;
import com.luxsoft.siipap.cxp.domain.RequisicionDetalle;
import com.luxsoft.siipap.domain.Periodo;

@SuppressWarnings("unchecked")
public class RequisicionDaoImpl extends HibernateDaoSupport implements RequisicionDao{

	public void salvarRequisicion(Requisicion req) {
		/*
		if(req.getId()!=null && req.isAplicada())
			throw new RuntimeException("Requisicion ya esta aplicada\n: "+req.toString());
		if(req.getId()==null){
			req.setModificado(req.getCreado());
		}
		*/
		getHibernateTemplate().saveOrUpdate(req);
	}

	public void actualizarRequisicion(Requisicion req) {
		salvarRequisicion(req);
		
	}

	public void eliminarRequisicion(Requisicion req) {
		if(req.isAplicada())
			throw new RuntimeException("No se puede eliminar una requisicion  aplicada\n: "+req.toString());
		getHibernateTemplate().delete(req);
	}

	public Requisicion buscarRequisicion(Long id) {
		return (Requisicion)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Requisicion.class)
					.setFetchMode("partidas",FetchMode.JOIN)
					.setMaxResults(1)
					.uniqueResult();
			}
			
		});
	}

	
	public List<Requisicion> buscarRequisicionesDeFacturas(final Periodo p) {		
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Requisicion.class)
				.setFetchMode("proveedor",FetchMode.JOIN)
				.add(Restrictions.between("fecha",p.getFechaInicial(),p.getFechaFinal()))
				.list();
			}
		});
	}
	
	public List<Requisicion> buscarRequisicionesDeFacturas() {		
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Requisicion.class)
				.setFetchMode("proveedor",FetchMode.JOIN)
				.list();
			}
		});
	}
	
	public List<Requisicion> buscarRequisicionesPendientes(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Requisicion.class)				
				.add(Restrictions.eq("aplicada",false)).list();
						
			}
		});
	}
	
	public void inicializar(final Requisicion r){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(r,LockMode.NONE);
				Hibernate.initialize(r.getPartidas());
				Hibernate.initialize(r.getProveedor());
				return null;
			}			
		});
	}

	public List<RequisicionDetalle> buscarRequisicionesConPagosAplicadas(){
		String hql="from RequisicionDetalle r " +
				" left join fetch r.requisicion req" +
				" left join fetch req.proveedor " +
				" where req.aplicada=?";
		return getHibernateTemplate().find(hql,new Boolean(true));
	}
	
	public List<Requisicion> buscarRequisicionesConNCAplicadas(){
		String hql="from Requisicion r " +		
		" left join fetch r.proveedor " +
		" where r.ncaplicada=?";
		return getHibernateTemplate().find(hql,new Boolean(true));
	}
	

}
