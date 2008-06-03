package com.luxsoft.siipap.cxp.dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxp.domain.Crecibos;
import com.luxsoft.siipap.cxp.domain.Crecibosde;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Proveedor;

@SuppressWarnings("unchecked")
public class ContraRecibosDaoImpl extends HibernateDaoSupport implements ContraRecibosDao{

	/**
	 * Salva un contra recibo aplicando todas las reglas  de negocios vigentes
	 * 
	 * @param recibo
	 */
	public void salvarContraRecibo(final Crecibos recibo){		
		if(recibo.getId()!=null){
			recibo.setModificado(Calendar.getInstance().getTime());
		}
		for(Crecibosde det:recibo.getPartidas()){
			det.setProveedor(recibo.getProveedor());
			det.calcularTotales();
		}
		recibo.setCLAVE(recibo.getProveedor().getClave());
		recibo.setPROVEEDOR(recibo.getProveedor().getNombre());
		getHibernateTemplate().saveOrUpdate(recibo);
		//afectarCXP(recibo);
	}
	/*
	private void afectarCXP(final Crecibos recibo){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				for(Crecibosde det:recibo.getPartidas()){
					CXPFactura factura=(CXPFactura)session.createCriteria(CXPFactura.class)
						.add(Restrictions.eq("proveedor",recibo.getProveedor()))
						.add(Restrictions.eq("referencia",det.getFACTURA().trim()))
						.setMaxResults(1)
						.uniqueResult();
					
					if(factura==null){				
						factura=new CXPFactura(det);
						session.saveOrUpdate(factura);
					}else{
						factura.setRecibo(det);
					}
				}
				return null;
			}
		});
	}*/
	
	/**
	 * Actualiza un contrarecibo aplicando todas las reglas de negocios vigentes
	 * @param recibo
	 */
	public void actualizarContraRecibo(final Crecibos recibo){
		salvarContraRecibo(recibo);		
	}
	
	/**
	 * Elimina un contrarecibo aplicando todas las reglas de negocios vigentes
	 * 
	 * @param recibo
	 */
	public void eliminarContraRecibo(final Crecibos recibo){
		getHibernateTemplate().delete(recibo);
	}
	
	/**
	 * Busca el contrarecibo indicado
	 * @param id
	 */
	public Crecibos buscarContraRecibo(final Long id){
		String hql="from Crecibos c join fetch c.partidas where c.id=?";
		List<Crecibos> l=getHibernateTemplate().find(hql,id);
		return l.isEmpty()?null:l.get(0);
	}
	
	/**
	 * Busca todas las partidas del los recibos del periodo seleccionado
	 * @param p
	 * @return
	 */
	public List<Crecibosde> buscarPartidasDeRecibos(final Periodo p){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Crecibosde.class)
					.createAlias("crecibos","r")
					.setFetchMode("crecibos",FetchMode.JOIN)
					.setFetchMode("analisis",FetchMode.JOIN)
					.setFetchMode("proveedor",FetchMode.JOIN)
					.add(Restrictions.between("r.FCRECIBO",p.getFechaInicial(),p.getFechaFinal()))
					.list();
			}
			
		});
	}
	
	/**
	 * Localiza un detalle de contrarecibo 
	 * 
	 * @param p
	 * @param factura
	 * @return
	 */
	public Crecibosde buscarPartida(final Proveedor p,final String factura){
		return (Crecibosde)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Crecibosde.class)
					.setFetchMode("crecibos",FetchMode.JOIN)
					.add(Restrictions.eq("proveedor",p))
					.add(Restrictions.eq("FACTURA",factura.trim()))		
					.setMaxResults(1)
					.uniqueResult();
			}
			
		});
	}
	
	/**
	 * Re inicializa un contrarecibo
	 * 
	 * @param recibo
	 */
	public void refresh(final Crecibos recibo){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(recibo,LockMode.NONE);
				Hibernate.initialize(recibo.getProveedor());
				Hibernate.initialize(recibo.getPartidas());
				return null;					
			}
			
		});
	}
}
