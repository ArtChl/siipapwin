package com.luxsoft.siipap.em.importar;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;

public class ImportadorDeDepositosConTarjeta {
	
	public static HibernateTemplate template;
	
	public static void execute(final Date fecha){
		getTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				ScrollableResults rs=session.createQuery("from Pago p where p.fecha=?")
				.setParameter(0, fecha,Hibernate.DATE)
				.scroll();
				int row=1;
				while(rs.next()){
					Pago p=(Pago)rs.get()[0];
					if(p.getFormaDePago().equals("C")){
						if(p.getVenta()!=null){
							if(p.getVenta().getSerie().equals("A") || p.getVenta().getSerie().equals("C")){
								String pattern="PG Targeta {0} Venta: {1} Importe:{2} PagoId:{3} Referencia:{4} TarjTip: {5} Origen:{6} FormaPago: {7}" ;
								System.out.println(MessageFormat.format(pattern,row++
										,p.getVenta().getId(),p.getImporteAsDouble(),p.getId(),p.getReferencia(),p.getTarjetaTip() ,p.getOrigen(),p.getFormaDePago()));
								
								Deposito d=new Deposito();								
								d.setCuentaDestino("BANAMEX   (1858193)");
								d.setFecha(fecha);
								d.setFolio(p.getId().intValue());
								if(p.getTarjetaTip()!=null){
									String tip=p.getTarjetaTip();
									FormaDePago fp=FormaDePago.C;
									if(tip.equals("D")) fp=FormaDePago.Q;
									else if(tip.equals("A")) fp=FormaDePago.B;
									else if(tip.equals("E")) fp=FormaDePago.B;	
									d.setFormaDePago(fp);
								}else								
									d.setFormaDePago(FormaDePago.C);
								d.setImporte(p.getImporte());
								d.setOrigen(p.getOrigen());
								//d.setSucursal(p.getVenta().getSucursal());
								d.setSucursalId(p.getSucursal());
								d.resolverCuenta();
								
								System.out.println(d+"TarjetaTip"+p.getTarjetaTip());
								session.save(d);
								
							}
						}
						
					}
				}
				return null;
			}
			
		});
	}
	
	public static void updateFechaContable(){
		int update=ServiceLocator.getJdbcTemplate().update("update sw_depositos set fechacobranza=fecha where fechacobranza is null");
		System.out.println("Registros actualizados: "+update);
	}
	
	public static HibernateTemplate getTemplate(){
		if(template==null){
			template=new HibernateTemplate(ServiceLocator.getSessionFactory());
		}
		return template;
	}
	
	public static void main(String[] args) {
		Periodo p=new Periodo("09/07/2008","19/07/2008");
		List<Date> dias=p.getListaDeDias();
		for(Date d:dias){
			execute(d);
		}
	
		
		updateFechaContable();
	}

}
