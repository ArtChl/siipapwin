package com.luxsoft.siipap.em.parches;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Verifica la integridad de las devoluciones
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche7 extends HibernateDaoSupport{
	
	public Parche7(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void execute(final int mes){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				ScrollableResults rs=session.createQuery(
						" from Venta n " +
						" where n.year=2007 " +
						" and n.serie=? and n.origen=?" +
						" and n.mes=? " )
						.setString(0, "E")
						.setString(1, "CRE")
						.setInteger(2, mes)
						.scroll();
				int buff=0;
				while(rs.next()){
					try {
						Venta v=(Venta)rs.get()[0];
						if(v.getSaldo().doubleValue()!=0 || (v.getCredito()!=null))
							continue;					
						v.actualizarDatosDeCredito();
						v.getCredito().setRevisada(true);					
						v.getCredito().setRecibidaCXC(true);
						v.getCredito().setFechaRecepcionCXC(v.getFecha());
						v.getCredito().actualizar(v.getFecha());
						v.getCredito().setComentario("TXA");
						//final double total=v.getTotalSinDevolucionesAsDouble();
						
						//Caso 1 Precio neto y  Prochemex
						if(v.getTipo().equals("X") || v.getTipo().equals("P")|| v.getTipo().equals("N")||v.getTipo().equals("G")){
							if(v.getCredito()==null){
								v.actualizarDatosDeCredito();
							}
							v.getCredito().actualizarDescuentoPrecioNetoProchemex();
							continue;
						}
						
						
						System.out.println("Procesando venta:" +v.getId());
						NotasDeCreditoDet ntdet=(NotasDeCreditoDet)session.createQuery("from NotasDeCreditoDet d where d.factura=? and d.nota.tipo=?")
						.setEntity(0, v)
						.setString(1, "U")
						.uniqueResult();
						if(ntdet!=null){
							v.getCredito().setDescuento(ntdet.getDescuento());
						}else{
							ntdet=(NotasDeCreditoDet)session.createQuery("from NotasDeCreditoDet d where d.factura=? and d.nota.tipo=?")
							.setEntity(0, v)
							.setString(1, "L")
							.uniqueResult();
							if(ntdet!=null)
								v.getCredito().setDescuento(ntdet.getDescuento());
								
						
						}
						
						ntdet=(NotasDeCreditoDet)session.createQuery("from NotasDeCreditoDet d where d.factura=? and d.nota.tipo=?")
						.setEntity(0, v)
						.setString(1, "V")
						.uniqueResult();
						if(ntdet!=null){
							v.getCredito().setDescuento2(ntdet.getDescuento());
						}
						
					} catch (Exception e) {
						continue;
					}
					if(++buff%10==0){
						session.flush();
						session.clear();
					}
				}
				return null;
			}			
		});		
	}
	
	public static void main(String[] args) {
		
		Parche7 p= new Parche7();
		for(int i=1;i<12;i++){
			System.out.println("Procesando mes: "+i);
			p.execute(i);
		}
	}

}
