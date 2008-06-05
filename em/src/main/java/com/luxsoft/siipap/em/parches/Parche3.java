package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;
import java.text.MessageFormat;

import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.services.ServiceLocator;

/**
 * Actualiza las notas de credito en su propiedad de aplicable
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche3 extends HibernateDaoSupport{
	
	public Parche3(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void execute(){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createQuery(
						" from NotasDeCreditoDet n " +
						" left join fetch n.nota nn  " +
						"where n.year=2007" +
						"  and n.factura is not null" +
						"  and n.nota.serie not in(?,?,?)" )
						.setParameter(0, "U")
						.setParameter(1, "V")
						.setParameter(2, "M")
						.scroll();
				while(rs.next()){
					
					NotasDeCreditoDet nota=(NotasDeCreditoDet)rs.get()[0];
					//if(nota.getNota().)
					
					double saldoNota=nota.getNota().getSaldo();
					
					// La nota no tiene saldo
					if(saldoNota>-1){
						String pattern="La  nota: {0} not tiene saldo por lo que ya no es aplicable actual mente: {1}";
						System.out.println(MessageFormat.format(pattern,nota.getNota().getId(),nota.getNota().isAplicable()));
						nota.getNota().setAplicable(true);						
					}
					
					//La nota tiene saldo se decide en base al saldo de la factura
					if(saldoNota<-1){
						final double saldoFactura=nota.getNota().getSaldo();
						
						//La factura tiene saldo la nota netea  la venta
						if(saldoFactura>1){
							String pattern="La  nota: {0}  tiene saldo la factura: {1}  tiene saldo por lo tanto no es aplicable";
							System.out.println(MessageFormat.format(pattern,nota.getNota().getId(),nota.getFactura().getId()));
							nota.getNota().setAplicable(false);
							
						}else{
							String pattern="LA NOTA : {0}  tiene saldo LA FACTURA: {1} no tiene saldo por lo tanto es aplicable";
							System.out.println(MessageFormat.format(pattern,nota.getNota().getId(),nota.getFactura().getId()));
							nota.getNota().setAplicable(true);
							
						}						
						
					}
					session.update(nota.getNota());
				}
				return null;
			}			
		});		
	}
	
	public static void main(String[] args) {
		new Parche3().execute();
	}

}
