package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.domain.Venta;

@SuppressWarnings("unchecked")
public class PagoDaoImpl extends HibernateDaoSupport implements PagoDao{

	public Pago buscar(Long id) {
		return (Pago)getHibernateTemplate().get(Pago.class, id);
	}

	public void eliminar(Pago p) {
		
		getHibernateTemplate().delete(p);
	}

	public void salvar(Pago p) {
		getHibernateTemplate().saveOrUpdate(p);
		
	}
	
	public void eliminarPagos(final Periodo p,final String origen){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="delete from Pago p where p.fecha between :f1 and :f2 and p.origen=:ori";
				session.createQuery(hql)
				.setParameter("f1",p.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2",p.getFechaFinal(),Hibernate.DATE)
				.setString("ori", origen)
				.executeUpdate();
				return null;
			}
			
		});
		
	}

	@SuppressWarnings("unchecked")
	public List<Pago> buscarPagos(final Venta v) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from Pago p" +
						" left join fetch p.pagoM pm " +
						" left join fetch p.venta v " +
						" left join fetch p.notaDelPago np" +
						" where v=:venta")
					.setEntity("venta", v)
					.list();
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<Pago> buscarPagosConNota(final NotaDeCredito nota){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from Pago p where p.notaDelPago=:nota")
				.setEntity("nota", nota)
				.list();
				
			}
			
		});
	}

	
	public List<Pago> buscarPagosYtd(Cliente c) {
		int year=Periodo.obtenerYear(new Date());
		return getHibernateTemplate().find("from Pago p where p.year=? and p.clave=?", new Object[]{year,c.getClave()});
	}

}
