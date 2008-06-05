package com.luxsoft.siipap.em.parches;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Anticipo;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.services.ServiceLocator;

/**
 * Parche para generar los Anticipos correspondientes a los saldos a favor de siipap
 * 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class Parche2 extends HibernateDaoSupport{
	
	public Parche2(){
		setSessionFactory(ServiceLocator.getSessionFactory());
	}
	
	public void execute(){
		String sql="select * from SALDOFAV ";
		final DateFormat df=new SimpleDateFormat("dd/MM/yyyy"); 
		final List<Map<String, Object>> rows=ServiceManager.instance().getSiipapTemplateFactory().getJdbcTemplate(new Date()).queryForList(sql);		
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				int count=0;
				for(Map<String, Object> row:rows){
					
					//int SUCURSAL =((Number)row.get("SUCURSAL")).intValue();					
					String TIPODOC=row.get("TIPODEDOCU").toString();
					String CLAVE=row.get("CLAVE").toString();
					Date FECHA=(Date)row.get("FECHA");
					Date FECHDOCT=(Date)row.get("FECHA_DOCT");
					
					double SALDORIG= ((Number)row.get("SALDO_ORIG")).doubleValue();
					double IMPAPLIC =((Number)row.get("IMP_APLIC")).doubleValue();
					String REFEREN=((Number)row.get("REFERENCIA")).toString();
					double IMPORTE =((Number)row.get("IMPORTE")).doubleValue();
					
					String pattern="F.fac:{0} Saldo:{1} Aplic: {2}";
					final String comentario=MessageFormat.format(pattern, df.format(FECHDOCT),SALDORIG,IMPAPLIC);
					
					Cliente c=ServiceLocator.getClienteDao().buscarPorClave(CLAVE);
					final Anticipo pago=new Anticipo(c,TIPODOC);
					pago.setComentario(comentario);
					pago.setFecha(FECHA);
					pago.setReferencia(REFEREN);
					pago.setImporte(CantidadMonetaria.pesos(IMPORTE));
					pago.setFormaDePago(FormaDePago.H);
					pago.setTipoDeDocumento(TIPODOC);
					System.out.println("Actualizando :"+pago);
					ServiceLocator.getPagosManager().salvarGrupoDePagos(pago);
					count++;
				}
				System.out.println(count);
				
				/*
				
				*/
				return null;
			}
			
		});		
	}
	
	public static void main(String[] args) {
		new Parche2().execute();
	}

}
