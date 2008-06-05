package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.OrdenDeCorte;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.siipap.ventas.domain.VentaACredito;

/**
 * Parche para en maquila eliminar todos los movimientos de un almacen
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche5 extends HibernateDaoSupport{
	
	public Parche5(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void visitarEntradas(final String almacen){
		
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				//Eliminar todas las salidas de hojas
				System.out.println(" PROCESANDO SALIDAS DE HOJAS");
				final String h1="from SalidaDeHojas s where s.origen.origen.entrada.recepcion.almacen.nombre=:nombre and s.fecha>=:fecha";
				final ScrollableResults sc1=session.createQuery(h1)
				.setParameter("nombre",almacen)
				.setParameter("fecha", DateUtils.obtenerFecha("01/01/2007"))
				.scroll();								
				while(sc1.next()){
					SalidaDeHojas sh=(SalidaDeHojas)sc1.get()[0];
					System.out.println("Eliminando salida de hojas: "+sh);
					session.delete(sh);
				}
				
				//Eliminand todas las entrads de hojas
				System.out.println(" PROCESANDO ENTRADA DE HOJAS");
				final String h2="from EntradaDeHojas e where e.origen.entrada.recepcion.almacen.nombre=:nombre and e.fecha>=:fecha";
				final ScrollableResults sc2=session.createQuery(h2)
				.setParameter("nombre",almacen)
				.setParameter("fecha", DateUtils.obtenerFecha("01/01/2007"))
				.scroll();
				
				while(sc2.next()){
					EntradaDeHojas eh=(EntradaDeHojas)sc2.get()[0];
					System.out.println("Eliminando entrada de hojas: "+eh);
					session.delete(eh);
				}
				
				
				//Eliminand todas las entrads de hojas
				System.out.println(" PROCESANDO SALIDAS A CORTE");
				final String h3="from SalidaACorte s where s.entrada.recepcion.almacen.nombre=:nombre and s.fecha>=:fecha";
				final ScrollableResults sc3=session.createQuery(h3)
				.setParameter("nombre",almacen)
				.setParameter("fecha", DateUtils.obtenerFecha("01/01/2007"))
				.scroll();
				
				while(sc3.next()){
					SalidaACorte sc=(SalidaACorte)sc3.get()[0];
					System.out.println("Eliminando SalidaACortre: "+sc);
					session.delete(sc);
				}
				
				//Eliminand todas las entrads de hojas
				System.out.println(" PROCESANDO SALIDAS DE BOBINAS");
				final String h4="from SalidaDeMaterial s where s.entrada.recepcion.almacen.nombre=:nombre and s.fecha>=:fecha";
				final ScrollableResults sc4=session.createQuery(h4)
				.setParameter("nombre",almacen)
				.setParameter("fecha", DateUtils.obtenerFecha("01/01/2007"))
				.scroll();
				
				while(sc4.next()){
					SalidaDeMaterial s=(SalidaDeMaterial)sc4.get()[0];
					System.out.println("Eliminando SalidaACortre: "+s);
					session.delete(s);
				}
				
				System.out.println(" PROCESANDO ORDENES DE CORTE");
				final String h5="from OrdenDeCorte o where o.almacen.nombre=:nombre and o.fecha>=:fecha";
				final ScrollableResults sc5=session.createQuery(h5).setParameter("nombre",almacen)
				.setParameter("fecha", DateUtils.obtenerFecha("01/01/2007"))
				.scroll();
				
				while(sc5.next()){
					OrdenDeCorte oc=(OrdenDeCorte)sc5.get()[0];
					System.out.println("Eliminando OrdenDeortre: "+oc);
					//session.delete(oc);
				}
				
				return null;
				
			}
			
		});
		
	}
	
	public static void main(String[] args) {
		new Parche5().visitarEntradas("INTERCARTON");
	}

}
