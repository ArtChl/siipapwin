package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;

/**
 * Verifica la integridad de las devoluciones
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche6 extends HibernateDaoSupport{
	
	public Parche6(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void execute(){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createQuery(
						" from NotaDeCredito n " +
						" where n.year=2007 and n.tipo in(?,?,?)" )
						.setString(0, "H")
						.setString(1, "I")
						.setString(2, "J")
						.scroll();
				while(rs.next()){					
					NotaDeCredito nota=(NotaDeCredito)rs.get()[0];
					if(nota.getDevolucion()!=null){
						try {
							Devolucion d=nota.getDevolucion();
							if(d.getPartidas().size()==0){
								//System.out.println("Devolucion sin partidas: "+d);
								//System.out.println("Eliminandola");
								//nota.setDevolucion(null);
								//session.delete(d);
							}else{
								for(DevolucionDet det:d.getPartidas()){
									if(det.getNota()==null){
										System.out.println("Partida sin nota: "+det+ "Nota: "+nota);
										long numero=det.getCxcnumero();
										String tipo=det.getTipocxc();
										NotaDeCredito nn=(NotaDeCredito)session.createQuery(
												"from NotaDeCredito n where n.devolucion=:devo and n.numero=:numero and n.tipo=:tipo")
										.setEntity("devo", d)
										.setLong("numero", numero)
										.setString("tipo", tipo)
										.uniqueResult();
										det.setNota(nn);
										session.update(det);
									}
								}
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(nota.getDevolucion()==null){
						System.out.println("ERR Nota de devo sin devo_id: "+nota);
					}
					
				}
				return null;
			}			
		});		
	}
	
	public static void main(String[] args) {
		new Parche6().execute();
	}

}
