package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;


import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.CXC;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class CXCDaoImpl extends HibernateDaoSupport implements CXCDao{
	
	private VentasManager ventasManager;

	public void actualizar(final int year,final int mes) {
		actualizarVentas(year, mes);
		actualizarCargos(year, mes);
		actualizarCrreditos(year, mes);
		actualizarPagos(year, mes);
	}
	
	private void actualizarVentas(final int year,final int mes) {
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createCriteria(Venta.class)
				.add(Restrictions.eq("year", year))
				.add(Restrictions.eq("mes", mes))
				.add(Restrictions.eq("origen", "CRE"))
				.scroll();
				int count=0;
				while(rs.next()){
					Venta v=(Venta)rs.get()[0];
					session.update(v);
					CXC cxc=CXC.registrar(v);
					session.save(cxc);
					while(++count%20==0){						
						session.flush();
						session.clear();
						
					}
				}
				return null;
			}			
		});
	}
	
	private void actualizarCrreditos(final int year,final int mes) {
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createQuery("from NotasDeCreditoDet n " +
						"where n.year=:year" +
						"  and n.mes=:mes" +
						"  and n.factura.origen=:ori")
						.setInteger("year", year)
						.setInteger("mes", mes)
						.setString("ori", "CRE")
				.scroll();
				int count=0;
				while(rs.next()){
					NotasDeCreditoDet v=(NotasDeCreditoDet)rs.get()[0];					
					session.update(v);
					if(v.getImporte().getAmount().doubleValue()>0)
						continue;
					final CXC cxc=CXC.registrarCredito(v);
					session.save(cxc);
					while(++count%20==0){						
						session.flush();
						session.clear();
						
					}
				}
				return null;
			}			
		});
	}
	
	private void actualizarCargos(final int year,final int mes) {
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createQuery("from NotaDeCredito n " +
						"where n.year=:year" +
						"  and n.mes=:mes" +
						"  and n.tipo=:tipo")
						.setInteger("year", year)
						.setInteger("mes", mes)
						.setString("tipo", "M")
				.scroll();
				int count=0;
				while(rs.next()){
					NotaDeCredito v=(NotaDeCredito)rs.get()[0];					
					session.update(v);
					final CXC cxc;
					cxc=CXC.registrarCargo(v);
					session.save(cxc);
					while(++count%20==0){						
						session.flush();
						session.clear();
						
					}
				}
				return null;
			}			
		});
	}
	
	private void actualizarPagos(final int year,final int mes) {
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createQuery("from Pago p " +
						"where p.year=:year" +
						"  and p.mes=:mes" +
						"  and p.origen=:ori")
						.setInteger("year", year)
						.setInteger("mes", mes)
						.setString("ori", "CRE")												
				.scroll();
				int count=0;
				while(rs.next()){
					Pago p=(Pago)rs.get()[0];					
					session.update(p);
					final CXC cxc=CXC.registrarPago(p);					
					session.save(cxc);
					while(++count%20==0){						
						session.flush();
						session.clear();
						
					}
				}
				return null;
			}			
		});
	}
	
	public VentasManager getVentasManager() {
		return ventasManager;
	}
	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}




	public static void main(String[] args) {
		CXCDaoImpl dao=new CXCDaoImpl();
		dao.setSessionFactory(ServiceLocator.getSessionFactory());
		dao.setVentasManager(ServiceLocator.getVentasManager());
		dao.actualizar(2007,1);
		//dao.actualizarNotas();
		//dao.actualizarPagos();
	}

}
