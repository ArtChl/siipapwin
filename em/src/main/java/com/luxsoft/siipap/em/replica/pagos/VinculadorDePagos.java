package com.luxsoft.siipap.em.replica.pagos;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.em.replica.service.ServiceManager;


/**
 * Parche para vincular algunas referencias de pagos que no se importaran adecuadamente
 * 
 * TODO: Esta clase es temporal para el proceso de implementacion se suspendera en cuanto el control
 * de las notas y pagos este totalmente en siipapwin
 * 
 * @author Rubeb Cancino
 *
 */
public class VinculadorDePagos {
	
	
	
	public HibernateDaoSupport getTemplate(){
		return (HibernateDaoSupport)ServiceManager.instance().getContext().getBean("pagoDao");
	}
	
	/**
	 * Vincula pagos con notas para los pagos con forma de pago tipo T que no tengan
	 * su respectiva nota vinculada
	 * 
	 * Regresa una lista de los pagos no encontrados 
	 *
	 */
	public List<Pago> vincularPagosConNota(){
		final List<Pago> errores=new ArrayList<Pago>();
		getTemplate().getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createQuery("from Pago p where p.year=2007 and p.formaDePago=\'T\' and p.notaDelPago is null").scroll();
				
				int notfound=0;
				int found=0;
				int buffer=0;
				while(rs.next()){
					Pago p=(Pago)rs.get()[0];
					String referencia=p.getReferencia();
					String tipo=referencia.substring(0, 1);
					String ref=referencia.substring(1,referencia.length());
					Long numero=Long.valueOf(ref.trim());
					NotaDeCredito notaPago=getNotaDeCreditoDao().buscarNota(numero, tipo);
					
					if(notaPago!=null){
						System.out.println("Pago: "+p.getId()+" NotaPago: "+p.getNotaDelPago()+" Nota encontrada:"+notaPago.getId());
						p.setNotaDelPago(notaPago);
						
						found++;
					}else{
						@SuppressWarnings("unused")
						String msg=MessageFormat.format("No encontre la nota para referencia: " +
								"{0} tipo: {1} ref: {2} numero: {3} fecha: {4} pago_id: {5} forma pago: {6}"
								, referencia,tipo,ref,numero,p.getFecha(),p.getId(),p.getFormaDePago());
						System.out.println(msg);
						errores.add(p);
						notfound++;
					}
					
					//p.setNotaDelPago(notaPago);
					if(buffer++%20==0){
						session.flush();
						session.clear();
					}
				}
				System.out.println("not found "+notfound);
				System.out.println("found "+found);
				return null;
			}
			
		});
		return errores;
	}
	
	private NotaDeCreditoDao getNotaDeCreditoDao(){
		return (NotaDeCreditoDao)ServiceManager.instance().getContext().getBean("notasDeCreditoDao");
	}
	
	public static void main(String[] args) {
		new VinculadorDePagos().vincularPagosConNota();
	}

}
