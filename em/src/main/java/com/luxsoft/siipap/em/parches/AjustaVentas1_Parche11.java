package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.em.importar.ImportadorDeVentas;
import com.luxsoft.siipap.em.managers.EMServiceLocator;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

/**
 * 
 *  
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class AjustaVentas1_Parche11 extends HibernateDaoSupport{
	
	public AjustaVentas1_Parche11(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void execute(final long id){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Venta v=(Venta)session.get(Venta.class, id);
				if(v.getPartidas().size()==0){
					ImportadorDeVentas imp=(ImportadorDeVentas)EMServiceLocator.instance().getContext().getBean("importadorDeVentas");
					List<VentaDet> partidas=imp.buscarPartidas(v);
					for(VentaDet det:partidas){
						v.agregarDetalle(det);
					}
					
				}
				
				return null;
			}
			
		});
	}
	
	public static void main(String[] args) {		
		AjustaVentas1_Parche11 par=new AjustaVentas1_Parche11();
		par.execute(2661178L);
		
	}

}
