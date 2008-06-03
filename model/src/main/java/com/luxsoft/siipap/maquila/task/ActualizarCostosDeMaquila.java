package com.luxsoft.siipap.maquila.task;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.services.ServiceLocator;

/**
 * Actualiza cualquier costo nulo o negativo que exista en Salidas de Hojeado
 * 
 * Tarea de ejeccion diaria
 * 
 * @author Ruben Cancino
 *
 */
public class ActualizarCostosDeMaquila extends HibernateDaoSupport{
	
	
	
	public void execute(){		
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				ScrollableResults rs=sess.createCriteria(EntradaDeHojas.class)
				.scroll();
				int buff=0;
				while(rs.next()){
					EntradaDeHojas entrada=(EntradaDeHojas)rs.get()[0];
					CantidadMonetaria costoActual=entrada.getCosto();
					
					double metros=entrada.getOrigen().getMetros2().doubleValue();
					double pm2=entrada.getOrigen().getEntrada().getPrecioPorM2();
					double millares=entrada.getCantidad().doubleValue();
					CantidadMonetaria costo=new CantidadMonetaria(Math.abs(metros*pm2/millares),costoActual.currency());
					double dif=costo.subtract(costoActual).getAmount().doubleValue();
					
					if(dif!=0){
						System.out.println("Entrada: "
								+entrada.getId()
								+" Fecha: "+entrada.getFecha()
								+"Corte_id:"
								+entrada.getOrigen().getId()
								+" Costo actual:"+costoActual
								+" Costo calculado:"+costo
								+" Dif: "+dif);
						entrada.setCosto(costo);
					}
					
					if(costoActual.amount().doubleValue()<=0){
						System.out.println("Entrada: "
								+entrada.getId()
								+"Corte_id:"
								+entrada.getOrigen().getId()
								+" Costo actual:"+costoActual
								+" Costo calculado:"+costo
								+" Dif: "+dif);
						entrada.setCosto(costo);
					}
					
					
					if(buff++%20==0){
						sess.flush();
						sess.clear();
					}
				}
				return null;
			}
			
		});		
	}
	
	public static void main(String[] args) {
		ActualizarCostosDeMaquila m=new ActualizarCostosDeMaquila();
		m.setSessionFactory(ServiceLocator.getSessionFactory());
		m.execute();
		
	}

}
