package com.luxsoft.siipap.cxc.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorVolumen;
import com.luxsoft.siipap.ventas.domain.Venta;

@SuppressWarnings("unchecked")
public class DescuentoPorVolumenDaoImpl extends HibernateDaoSupport implements DescuentoPorVolumenDao{

	public void actualisar(DescuentoPorVolumen d) {		
		salvar(d);
	}

	public DescuentoPorVolumen buscarPorId(Long id) {
		return (DescuentoPorVolumen)getHibernateTemplate().get(DescuentoPorVolumen.class, id);
	}

	public void eliminar(DescuentoPorVolumen d) {
		getHibernateTemplate().delete(d);
		
	}

	public void salvar(DescuentoPorVolumen d) {
		getHibernateTemplate().saveOrUpdate(d);
		
	}
	
	public void eliminarDescuentos(final int year,final int mes){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("delete DescuentoPorVolumen d where d.year=? and d.mes=?")
				.setInteger(0, year)
				.setInteger(1, mes)
				.executeUpdate();
				return null;
			}
			
		});
	}
	
	/**
	 * Descuento por volumen  
	 * 
	 * @param importe
	 * @return
	 */
	public DescuentoPorVolumen buscar(final double importe){
		return (DescuentoPorVolumen)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from DescuentoPorVolumen d where d.maximo>=? and d.activo=:activo and d.origen=:origen order by d.maximo asc";
				List<DescuentoPorVolumen> descs= session.createQuery(hql)
				.setParameter(0, importe)
				.setBoolean("activo", true)
				.setString("origen", "CRE")
				.list();
				if(descs.isEmpty())
					return session.createQuery("from DescuentoPorVolumen d order by d.maximo desc").setMaxResults(1).uniqueResult();
				return descs.get(0);
								
			}
			
		});
		
	}
	
	/**
	 * Calcula el descuento para un cliente de cheque post fechado 
	 * para el importe determinado
	 * 
	 * @param ventaId
	 * @return
	 */
	public double calcularDescuentoChequeP(final Long ventaId){		
		DescuentoPorVolumen d=(DescuentoPorVolumen)getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final Venta v1=(Venta)session.get(Venta.class, ventaId);
				//double importe=v1.getTotal().abs().amount().doubleValue();
				
				double importe=0;
				if(v1.getPedido()==0)
					importe=v1.getTotal().abs().amount().doubleValue();
				else{
					final String clave=v1.getClave();
					final int sucursal=v1.getSucursal();
					final int pedido=v1.getPedido();
					final List<Venta> ventas=session.createQuery("from Venta v where v.year>2005 and " +
							"v.clave=:clave and v.sucursal=:suc and v.pedido=:p")
							.setString("clave", clave)
							.setInteger("suc", sucursal)
							.setInteger("p", pedido)
							.list();
					for(Venta v:ventas){
						importe=importe+v.getTotal().amount().abs().doubleValue();
					}
				}
				
				String hql="from DescuentoPorVolumen d " +
						"where d.activo=:activo " +
						"  and d.origen=:origen " +
						"  order by d.maximo asc";
				List<DescuentoPorVolumen> descs= session.createQuery(hql)				
				.setBoolean("activo", true)
				.setString("origen", "CON")
				.list();
				DescuentoPorVolumen descuento=null;
				
				final int total=descs.size();
				
				for(int index=0;index<total;index++){
					DescuentoPorVolumen actual=descs.get(index);
					if(importe>actual.getMaximo()){
						descuento=actual;
						continue;
					}else{
						if(index<total){							
							descuento=descs.get(index);
						}
						else 
							descuento=actual;
						break;
					}
					
				}
				/**
				for(DescuentoPorVolumen dd:descs){
					if(importe>dd.getMaximo()){
						descuento=dd;
						continue;
					}
					break;
				}
				**/
				return descuento;
			}			
		});
		if(logger.isDebugEnabled()){
			if(d!=null)
				logger.debug("Descuento obtenido: "+d);
			else
				logger.debug("No se localizo un descuento para la venta:"+ventaId);
		}
		return d!=null?d.getDescuento():0;
	}
	
	/**
	 * Localiza un descuento por volumen especifico para el cliente
	 * indicado. Normalmente es un descuento adicional
	 * 
	 * @param c
	 * @return
	 */
	public DescuentoPorVolumen buscar(final Cliente c){
		return (DescuentoPorVolumen)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from DescuentoPorVolumen d where d.cliente=? and d.activo=:activo order by d.maximo desc";
				return session.createQuery(hql)
				.setEntity(0, c)
				.setBoolean("activo", true)
				.setMaxResults(1)
				.uniqueResult();				
			}
			
		});
	}

	
	public List<DescuentoPorVolumen> buscar() {
		return getHibernateTemplate().find("from DescuentoPorVolumen d");
	}

}
