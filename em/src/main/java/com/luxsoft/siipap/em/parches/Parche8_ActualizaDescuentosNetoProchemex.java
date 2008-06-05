package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Actualiza los descuentos en ventas credito para tipo G N etc
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche8_ActualizaDescuentosNetoProchemex extends HibernateDaoSupport{
	
	public Parche8_ActualizaDescuentosNetoProchemex(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void execute(final int mes){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				ScrollableResults rs=session.createQuery(
						" from Venta n " +
						" where n.year=2007 " +
						" and n.mes=?" +
						" and n.serie=? " +
						" and n.origen=?" )
						.setInteger(0, mes)
						.setString(1, "E")
						.setString(2, "CRE")
						.scroll();
				int buff=0;
				while(rs.next()){
					try {
						Venta v=(Venta)rs.get()[0];	
						if(v.getCredito()==null){
							System.out.println("Venta credito nula: "+v.getId());
							v.actualizarDatosDeCredito();
							session.update(v);
							continue;
						}
						v.getCredito().actualizarDescuentoPrecioNetoProchemex();
						if(++buff%10==0){
							session.flush();
							session.clear();
							//System.out.println("Flushing...");
						}
					} catch (Exception e) {
						e.printStackTrace();
						
					}
					
				}
				return null;
			}			
		});		
	}
	
	public static void main(String[] args) {
		for(int mes=10;mes<=11;mes++){
			System.out.println("Procesando mes: "+mes);
			new Parche8_ActualizaDescuentosNetoProchemex().execute(mes);
		}
		
	}

}
