package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

@SuppressWarnings("unchecked")
public class NotaDeCreditoDaoImpl extends HibernateDaoSupport implements NotaDeCreditoDao{

	public NotaDeCredito buscar(final Long id) {
		return (NotaDeCredito)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				NotaDeCredito nota=(NotaDeCredito)session.createQuery("from NotaDeCredito n " +
						" left join fetch n.cliente c" +
						" where n.id=:id")
						.setLong("id", id)
						.setMaxResults(1)
						.uniqueResult();
				if(nota!=null){
					Hibernate.initialize(nota.getPartidas());
					for(NotasDeCreditoDet det:nota.getPartidas()){
						Hibernate.initialize(det.getFactura());
					}
					return nota;
				}
				return null;
			}			
		});
	}
	
	public NotaDeCredito buscarDevolucion(final Long id){
		return (NotaDeCredito)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				NotaDeCredito nota=(NotaDeCredito)session.createQuery("from NotaDeCredito n " +
						" left join fetch n.cliente c" +
						" left join fetch n.devolucion d" +
						" where n.id=:id and n.devolucion is not null")
						.setLong("id", id)
						.setMaxResults(1)
						.uniqueResult();
				if(nota==null)
					return null;
				Hibernate.initialize(nota.getDevolucion().getPartidas());				
				for(DevolucionDet dd:nota.getDevolucion().getPartidas()){
					Hibernate.initialize(dd.getVentaDet());
					Hibernate.initialize(dd.getArticulo());
				}
				for(NotasDeCreditoDet det:nota.getPartidas()){
					Hibernate.initialize(det.getFactura());
				}
				return nota;
			}			
		});
	}

	public void eliminar(NotaDeCredito nota) {
		getHibernateTemplate().delete(nota);
		
	}

	public void actualizar(final NotaDeCredito nota) {
		//getHibernateTemplate().saveOrUpdate(nota);
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.update(nota);
				return null;
			}
			
		});
	}
	
	public void salvar(final NotaDeCredito nota) {
		getHibernateTemplate().saveOrUpdate(nota);
	}
	
	public void salvar(NotasDeCreditoDet det){
		getHibernateTemplate().saveOrUpdate(det);
	}

	public void salvar(final Collection<NotaDeCredito> notas) {
		logger.debug("Notas  a salvar: "+notas.size());
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int buff=0;
				
				for(NotaDeCredito nc:notas){					
					session.saveOrUpdate(nc);
					
					if(++buff%20==0){						
						session.flush();
						session.clear();
					}				
				}				
				return null;
			}
			
		});
		
	}
	
	
	public void salvarDetalles(final Collection<NotasDeCreditoDet> notas){
		logger.debug("Notas  a salvar: "+notas.size());
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int buff=0;
				
				for(NotasDeCreditoDet nc:notas){					
					session.saveOrUpdate(nc);
					if(++buff%20==0){						
						session.flush();
						session.clear();
					}					
				}				
				return null;
			}
			
		});
	}

	
	public List<NotaDeCredito> buscar(final Periodo p) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotaDeCredito n " +
						" left join n.partidas" +
						" where n.fecha between :f1 and :f2";
				return session.createQuery(hql)
				.setParameter("f1",p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2",p.getFechaFinal(),Hibernate.DATE)
				.list();
			}
			
		});
	}
	
	public List<NotaDeCredito> buscarNotas(final Periodo p){
		return getHibernateTemplate().find(
				"from NotaDeCredito n left join fetch n.cliente where n.fecha between ? and ?"
				, new Object[]{p.getFechaInicial(),p.getFechaFinal()});
	}
	
	public List<NotaDeCredito> buscarNotasCre(final Periodo p){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotaDeCredito n " +
						" left join fetch n.cliente " +
						" left join fetch n.devolucion " +						
						" where n.fecha between :f1 and :f2 and n.origen=:origen";
				return session.createQuery(hql)
				.setParameter("f1",p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2",p.getFechaFinal(),Hibernate.DATE)
				.setString("origen", "CRE")
				.list();
			}
			
		});
	}
	
	
	public NotaDeCredito buscarNota(long numero,String tipo,String serie){
		String hql="from NotaDeCredito n where n.numero=? and n.tipo=? and n.serie=?";
		Object[] vals=new Object[]{numero,tipo,serie};
		List<NotaDeCredito> l=getHibernateTemplate().find(hql, vals);
		return l.isEmpty()?null:l.get(0);
	}
	
	public NotaDeCredito buscarNotaConDetalle(long numero,String tipo,String serie){
		String hql="from NotaDeCredito n left join fetch n.partidas where n.numero=? and n.tipo=? and n.serie=?";
		Object[] vals=new Object[]{numero,tipo,serie};
		List<NotaDeCredito> l=getHibernateTemplate().find(hql, vals);
		return l.isEmpty()?null:l.get(0);
	}
	
	
	
	public NotaDeCredito buscarNotaSinNumero(int grupo, String tipo, String serie) {
		String hql="from NotaDeCredito n left join fetch n.partidas where n.grupo=? and n.tipo=? and n.serie=?";
		Object[] vals=new Object[]{grupo,tipo,serie};
		List<NotaDeCredito> l=getHibernateTemplate().find(hql, vals);
		return l.isEmpty()?null:l.get(0);
	}

	public NotaDeCredito buscarNota(long numero,String tipo){
		String hql="from NotaDeCredito n where n.numero=? and n.tipo=? ";
		Object[] vals=new Object[]{numero,tipo};
		List<NotaDeCredito> l=getHibernateTemplate().find(hql, vals);
		return l.isEmpty()?null:l.get(0);
	}

	public NotaDeCredito buscarNotaConSaldo(long numero, String clave) {
		List<NotaDeCredito> list=getHibernateTemplate().find("from NotaDeCredito nc where nc.numero=? and nc.clave=? and nc.saldo<0", new Object[]{numero,clave});
		return list.isEmpty()?null:list.get(0);
	}

	public List<NotaDeCredito> buscarNotasConSaldo(final String clave) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from NotaDeCredito nc where nc.clave=:clave and nc.serie " +
						" not in(\'U\',\'V\',\'M\') and nc.origen!=\'MOS\' and nc.saldo<-1 and year>=2006";
				List<NotaDeCredito> notas=session.createQuery(hql).setParameter("clave", clave).list();								
				return notas;
			}
			
		});
	}

	public List<NotasDeCreditoDet> buscarNotasDet(final Venta v) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from NotasDeCreditoDet d " +
						" left join fetch d.nota n" +
						" left join fetch d.factura f" +
						" where d.factura=:venta")
				.setEntity("venta", v)
				.list();
			}
			
		});
	}

	public List<NotaDeCredito> buscarNotasPorChequeDevueltoYTD(Cliente c) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				return new ArrayList<NotaDeCredito>();
			}			
		});
	}

	public List<NotaDeCredito> buscarNotasPorDevolucionYTD(final Cliente c) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from NotaDeCredito n where n.year=:year and n.clave=:clave and n.devolucion is not null")
				.setParameter("year", Periodo.obtenerYear(new Date()))
				.setParameter("clave", c.getClave())
				.list();
			}			
		});
	}
	

}
