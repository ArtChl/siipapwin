package com.luxsoft.siipap.em.replica.devo;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

public class VinculacionDeDevoluciones {
	
	private HibernateDaoSupport dao;
	private JdbcTemplate jdbcTemplate;
	
	protected Logger logger=Logger.getLogger(getClass());
	
	
	
	public void vincularVentasDet(final int year){
		System.out.println("Procesando devoluciones");
		
		final String hql="select det from DevolucionDet det " +
		" join fetch det.devolucion  " +
		"where det.year=:year" +
		"  and det.ventaDet is null";
		
		getDao().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults sc=session.createQuery(hql)
				.setInteger("year",year)
				.scroll();
				int row=0;
				
				while(sc.next()){					
					DevolucionDet d=(DevolucionDet)sc.get()[0];
					System.out.println("Procesando DevoDet:"+d.getId()+" Registro: "+(sc.getRowNumber()));
					Venta v=d.getDevolucion().getVenta();
					if(v==null)
						continue;
					for(VentaDet vd:v.getPartidas()){
						String claveV=vd.getClave().trim();
						String claveD=d.getClave().trim();
						if(claveV.equals(claveD)){
							double cantidadV=Math.abs(vd.getCantidad());
							double cantidadD=d.getCantidad();
							if(cantidadV>=cantidadD){
								d.setVentaDet(vd);
								break;
							}
						}
					}
					
					if(row++%20==0){
						session.flush();
						session.clear();
					}
				}
				return null;
			}
			
		});
	}
	
	public void vincularNotasDeCreditoDet(){
		
	}
	
	
	
	public HibernateDaoSupport getDao() {
		return dao;
	}

	public void setDao(HibernateDaoSupport dao) {
		this.dao = dao;
	}
	
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public static void vincular(Periodo p){
		HibernateDaoSupport dao=(HibernateDaoSupport)ServiceManager.instance().getContext().getBean("ventasDao");
		VinculacionDeDevoluciones v=new VinculacionDeDevoluciones();
		v.setDao(dao);
		v.setJdbcTemplate(ServiceManager.instance().getDefaultJdbcTemplate());	
		int year=Periodo.obtenerYear(p.getFechaFinal());
		v.vincularVentasDet(year);
	}

	
	

}
