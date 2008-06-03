package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.dao2.hibernate.GenericDaoHibernate;
import com.luxsoft.siipap.domain.Sucursales;

@SuppressWarnings("unchecked")
public class DepositosDaoImpl extends GenericDaoHibernate<Deposito, Long> implements DepositosDao{

	public DepositosDaoImpl() {
		super(Deposito.class);
	}

	@Override
	public Deposito save(Deposito object) {
		Deposito res=super.save(object);
		if(res.getFolio()==0)
			res.setFolio(res.getId().intValue());
		return super.save(res);
	}



	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.DepositosDao#buscarDeposito(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public Deposito buscarDeposito(final Long id){
		String hql="from Deposito d left join fetch d.partidas where d.id=?";
		List<Deposito> res=getHibernateTemplate().find(hql, id);
		return res.isEmpty()?null:res.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.DepositosDao#buscarDepositos(java.lang.String)
	 */
	public List<Deposito> buscarDepositos(String tipo){
		return getHibernateTemplate().find("from Deposito d where d.origen=?", tipo);
	}

	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.DepositosDao#buscarDepositosCredito(java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Deposito> buscarDepositosCredito(final Date fecha) {		
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<Deposito> deps=session.createQuery("from Deposito d where d.origen=:origen and d.fecha=:fecha ")
				.setString("origen", "CRE")
				.setParameter("fecha", fecha,Hibernate.DATE)
				.list();
				ListIterator<Deposito> iter=deps.listIterator();
				while(iter.hasNext()){
					Deposito d=iter.next();
					switch (d.getFormaDePago()) {
					case B:
					case C:
					case Q:
						iter.remove();
						break;
					default:
						break;
					}
				}
				return deps;
			}
			
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.DepositosDao#generarDepositosAutomaticosCredito(java.util.Date)
	*/	
	public List<Deposito> buscarDepositosEnCobranzaCre(final Date fecha){
		return getHibernateTemplate().executeFind(new HibernateCallback(){			
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<PagoM> pagos=session.createQuery("from PagoM p " +
						"  where p.fecha=:fecha " +
						"  and p.formaDePago in(\'N\',\'O\')" +
						"  and p.origen=:origen")
				.setDate("fecha", fecha)
				.setString("origen", "CRE")
				.list();
				System.out.println("Pagos: "+pagos.size());
				final List<Deposito> deps=new ArrayList<Deposito>();
				for(PagoM p:pagos){					
					Deposito d=new Deposito();					
					d.setCuentaDestino(p.getCuentaDeposito());
					d.setFormaDePago(p.getFormaDePago());
					d.setFecha(p.getFecha());
					d.setOrigen("CRE");
					d.setImporte(p.getImporte());
					d.setSucursal(Sucursales.OFICINAS);
					d.setSucursalId(1);
					d.resolverCuenta();
					d.setFormaDePago(p.getFormaDePago());
					//System.out.println("Dep: "+d);
					deps.add(d);
				}
				return deps;
			}			
		});
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.DepositosDao#buscarDpositosMostrador(java.util.Date)
	 */
	public List<Deposito> buscarDepositosContado(final Date fecha){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<Deposito> deps=session.createQuery("from Deposito d where d.origen=:origen and d.fecha=:fecha ")
				.setString("origen", "MOS")
				.setParameter("fecha", fecha,Hibernate.DATE)
				.list();
				
				return deps;
			}
			
		});
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.dao.DepositosDao#buscarDepositosCamioneta(java.util.Date)
	 */
	public List<Deposito> buscarDepositosCamioneta(final Date fecha) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<Deposito> deps=session.createQuery("from Deposito d where d.origen=:origen and d.fecha=:fecha ")
				.setString("origen", "CAM")
				.setParameter("fecha", fecha,Hibernate.DATE)
				.list();				
				return deps;
			}
			
		});
	}

	public void eliminarDepositosImportados(final Date fecha) {
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("delete Deposito where fecha=? and origen in (?,?)")
				.setParameter(0, fecha)
				.setString(1, "MOS")
				.setString(2, "CAM")
				.executeUpdate();
				return null;
			}			
		});
		
	}
	
	
	

}
