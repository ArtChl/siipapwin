package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Parche para actualizar las fechas de revision de las facturas
 * correspondientes a la cartera del 8/2007 para el inicio de operaionces
 * de CXC
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche1 extends HibernateDaoSupport{
	
	public Parche1(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void actualizarVentasCredito(){
		String sql="select * from CREDRV06 where FECHAREVIS is not null order by VENTA_ID asc ";
		final List<Map<String, Object>> rows=ServiceManager.instance().getSiipapTemplateFactory().getJdbcTemplate(new Date()).queryForList(sql);
		for(Map<String, Object> row:rows){			
			Long id=((Number)row.get("VENTA_ID")).longValue();
			actualizar(id);
		}
	}
	
	
	public void execute(){
		String sql="select * from CREDRV06 where FECHAREVIS is not null order by VENTA_ID asc ";
		final DateFormat df=new SimpleDateFormat("dd/MM/yyyy"); 
		final List<Map<String, Object>> rows=ServiceManager.instance().getSiipapTemplateFactory().getJdbcTemplate(new Date()).queryForList(sql);		
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int count=0;
				for(Map<String, Object> row:rows){
					final Object objId=row.get("VENTA_ID");
					if(objId==null)
						continue;
					final Long id=((Number)objId).longValue();
					final Object obj=row.get("FECHAREVIS");
					try {
						
						if(!obj.equals("<null>")){
							//System.out.println(obj);
							System.out.println("Procesando venta: "+id);
							Date fecha;
							if(obj instanceof Date)
								fecha=(Date)obj;
							else
								fecha=df.parse(obj.toString());
							
							Venta v=(Venta)session.get(Venta.class, id);
							if(v.getCliente().getCredito()==null){
								System.out.println("Generando cliente credito nuevo para venta: "+id);
								ClienteCredito cc=v.getCliente().generarCredito();
								cc.copyFromCliente();
								session.saveOrUpdate(cc);
							}
							if(v.getCredito()==null){
								v.actualizarDatosDeCredito();
							}
							v.getCredito().setFechaRevisionCxc(fecha);
							v.getCredito().setRecibidaCXC(true);
							v.getCredito().setRevisada(true);
							v.getCredito().actualizar();
							session.update(v);
						}
						
						
					} catch (Exception e) {
						System.out.println("Error acualizando venta: "+id);
						e.printStackTrace();
					}				
					if(count++%20==0){
						session.flush();
						session.clear();
					}
					
				}
				return null;
			}
			
		});		
	}
	
	public void actualizar(Long id){
		
		try {
			VentasManager manager=(VentasManager)ServiceLocator.getDaoContext().getBean("ventasManager");
			manager.actualizarVenta(id);
		} catch (Exception e) {
			System.out.println("Error actualizando :"+id);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Parche1().execute();
		//new Parche1().actualizar(2556757l);
		//new Parche1().actualizar(203839l);
		//new Parche1().actualizar(204603l);
		//new Parche1().actualizar(204606l);
		//new Parche1().actualizar(204608l);
		//new Parche1().actualizar(204876l);
		//new Parche1().actualizar(232164l);
		
		
		
		
		
		
		
	}

}
