package com.luxsoft.siipap.inventarios.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Entrada;
import com.luxsoft.siipap.inventarios.domain.InventarioMensual;
import com.luxsoft.siipap.inventarios.domain.Salida;
import com.luxsoft.siipap.inventarios.domain.Transformacion;
import com.luxsoft.siipap.inventarios.services.InventariosManager;
import com.luxsoft.siipap.services.ServiceLocator;

@SuppressWarnings("unchecked")
public class TransformacionDaoImpl extends HibernateDaoSupport implements TransformacionDao{
	
	private InventariosManager manager;

	public void salvarTransformacion(final Transformacion t) {
		if(t.getId()==null){
			t.setModificado(t.getCreado());
		}else
			t.setModificado(Calendar.getInstance().getTime());
		System.out.println("Salvando: "+t);
		getHibernateTemplate().saveOrUpdate(t);
	}	

	public void borrarTransformacion(Transformacion t) {
		throw new RuntimeException("No se puede borrar una transfomracion se debe hacer mediante SQL");
	}
	
	 
	public List<Transformacion> buscarTransfomraciones(final Periodo p) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				final List<Transformacion> transformaciones=new ArrayList<Transformacion>();
				final String hql="from Salida e " +
						"  where e.ALMFECHA between :f1 and :f2" +
						"    and e.ALMTIPO=:tipo";
				List<Salida> salidas=session.createQuery(hql)
					.setParameter("f1",p.getFechaInicial(),Hibernate.DATE)
					.setParameter("f2",p.getFechaFinal(),Hibernate.DATE)
					.setString("tipo","TRS")
					.list();
				int count=0;
				for(Salida s:salidas){
					
					Entrada e=null;
					try{
						e=(Entrada)session.createCriteria(Entrada.class)
							.add(Restrictions.eq("ALMSUCUR",s.getALMSUCUR()))
							.add(Restrictions.eq("ALMNUMER",s.getALMNUMER()))
							.add(Restrictions.eq("ALMTIPO",s.getALMTIPO()))
							.add(Restrictions.eq("ALMRENGL",s.getALMRENGL()+1))
							.setMaxResults(1)
							.uniqueResult();
					}catch(Exception ex){
						logger.error(ex);
					}
					if(e==null){
						logger.warn("Salida TRS sin Entrada. Salida:"+s);
						continue;
					}
					Transformacion t=(Transformacion)session.createCriteria(Transformacion.class)
						.setFetchMode("origen",FetchMode.JOIN)
						.setFetchMode("destino",FetchMode.JOIN)
						.add(Restrictions.eq("origen.id",s.getId()))
						.setMaxResults(1)
						.uniqueResult();
					if(t==null){
						t=new Transformacion();
						t.setOrigen(s);
						t.setClaveOrigen(s.getALMARTIC());
						t.setDestino(e);
						t.setClaveDestino(e.getALMARTIC());						
						t.setModificado(t.getCreado());
						session.saveOrUpdate(t);
						
					}					
					t.setModificado(new Date());
					t.setModificado(t.getCreado());
					t.setYear(Periodo.obtenerYear(s.getALMFECHA()));
					t.setMes(Periodo.obtenerMes(s.getALMFECHA())+1);
					
					InventarioMensual im=null;
					{
						im=(InventarioMensual)session.createQuery(
								"from InventarioMensual i  left join fetch i.articulo a " +
								"where i.clave=:clave and i.year=:year and i.mes=:mes")
								.setString("clave", t.getClaveOrigen())
								.setInteger("year", t.getYear())
								.setInteger("mes", t.getMes())
								.uniqueResult();
						
					}
					
					System.out.println("Buscando: "+t.getYear()+" MEs "+t.getMes()+ "Art: "+t.getClaveOrigen());
					if(t.getClaveOrigen().equalsIgnoreCase("PBE60"))
						continue;
					//InventarioMensual im=getManager().buscarInventario(t.getYear(), t.getMes(), t.getClaveOrigen());
					if(im!=null)
						t.setCosto(im.getCostoPromedio());
						
					//session.saveOrUpdate(t);
					//session.flush();
					//session.clear();
					if(++count%10==0){
						session.flush();
						session.clear();
					}
					System.out.println("Transformacion generada: "+t);
					
					transformaciones.add(t);
					
				}
				return transformaciones;
			}			
		});
	}
	
	public List<Transformacion> buscarTransfomraciones(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				final List<Transformacion> transformaciones=new ArrayList<Transformacion>();
				final String hql="from Salida e " +						
						"    where e.ALMTIPO=:tipo";
				List<Salida> salidas=session.createQuery(hql)
					.setString("tipo","TRS")
					.list();
				for(Salida s:salidas){
					Entrada e=null;
					try{
						e=(Entrada)session.createCriteria(Entrada.class)
							.add(Restrictions.eq("ALMSUCUR",s.getALMSUCUR()))
							.add(Restrictions.eq("ALMNUMER",s.getALMNUMER()))
							.add(Restrictions.eq("ALMTIPO",s.getALMTIPO()))
							.add(Restrictions.eq("ALMRENGL",s.getALMRENGL()+1))
							.setMaxResults(1)
							.uniqueResult();
					}catch(Exception ex){
						logger.error(ex);
					}
					if(e==null){
						logger.warn("Salida TRS sin Entrada. Salida:"+s);
						continue;
					}
					Transformacion t=(Transformacion)session.createCriteria(Transformacion.class)
						.setFetchMode("origen",FetchMode.JOIN)
						.setFetchMode("destino",FetchMode.JOIN)
						.add(Restrictions.eq("origen.id",s.getId()))
						.setMaxResults(1)
						.uniqueResult();
					if(t==null){
						t=new Transformacion();
						t.setOrigen(s);
						t.setDestino(e);
						t.setModificado(t.getCreado());
						session.save(t);
					}
					transformaciones.add(t);
					
				}
				return transformaciones;
			}			
		});
	}
	
	
	
	public InventariosManager getManager() {
		return manager;
	}

	public void setManager(InventariosManager manager) {
		this.manager = manager;
	}

	public static void main(String[] args) {
		TransformacionDaoImpl dao=new TransformacionDaoImpl();
		dao.setSessionFactory(ServiceLocator.getSessionFactory());
		dao.setManager(ServiceLocator.getInventariosManager());
		dao.buscarTransfomraciones(new Periodo("01/01/2007","31/08/2007"));
	}

}
