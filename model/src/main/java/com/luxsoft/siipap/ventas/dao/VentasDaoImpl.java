package com.luxsoft.siipap.ventas.dao;



import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.list.SetUniqueList;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;

@SuppressWarnings("unchecked")
public class VentasDaoImpl extends HibernateDaoSupport implements  VentasDao{

	

	//@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void salvar(final Venta venta) {
		getHibernateTemplate().saveOrUpdate(venta);		
	}
	
	public void eliminar(Long id) {
		eliminar(buscarPorId(id));
	}

	public void eliminar(Venta venta) {
		getHibernateTemplate().delete(venta);
	}

	public Venta buscarPorId(Long id) {
		return (Venta)getHibernateTemplate().get(Venta.class,id);
	}
	
	public void inicializarPartidas(final Venta v) {
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.update(v);
				Hibernate.initialize(v.getPartidas());
				return null;
			}			
		});
	}
	

	public int salvarVentas(final Collection<Venta> ventas){
		logger.debug("Ventas a salvar: "+ventas.size());
		return (Integer)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				int buff=0;
				int rows=0;
				for(Venta v:ventas){					
					session.saveOrUpdate(v);
					rows++;
					if(++buff%20==0){						
						session.flush();
						session.clear();
						
						
					}
					
				}			
				
				System.out.println("Registros salvados: "+rows);
				return rows;
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	public Venta buscarVenta(final Integer sucursal,final String serie,final String tipo,final long numero){
		String hql="select distinct v from Venta v left join fetch v.partidas where v.sucursal=? and v.serie=? and v.tipo=? and v.numero=?";
		List<Venta> ventas=getHibernateTemplate().find(hql,new Object[]{sucursal,serie,tipo,numero});
		Assert.isTrue(ventas.size()<=1,"No puede existir mas de una factura/venta por sucursal,serie,tipo,numero");
		if(ventas.size()==1)
			return ventas.get(0);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Venta buscarVenta(final Integer sucursal ,final String tipo,final long numero){
		String hql="select distinct v from Venta v left join fetch v.partidas where v.sucursal=? and v.tipo=? and v.numero=?";
		List<Venta> ventas=getHibernateTemplate().find(hql,new Object[]{sucursal,tipo,numero});
		Assert.isTrue(ventas.size()<=1,"No puede existir mas de una factura/venta por sucursal,tipo,numero");
		if(ventas.size()==1)
			return ventas.get(0);
		return null;
	}
	
		
	public List<Venta> buscarVentasPorCliente(final String clave,int year,int mes){
		return getHibernateTemplate().find(" from Venta v where v.year=? and v.mes=? and v.clave=?"
				, new Object[]{year,mes,clave});
	}
	
	/**
	 * Calcula el acumulado del mes para el cliente determinado (En pesos)
	 * 
	 * @param cliente
	 * @param year
	 * @param mes
	 * @return
	 */
	public CantidadMonetaria acumuladoDelMesCredito(String cliente,int year,int mes){
		return acumuladoDelMesCredito(cliente, year, mes,CantidadMonetaria.PESOS);
	}
	
	/**
	 * Calcula el acumulado del mes para el cliente determinado 
	 * 
	 * @param cliente
	 * @param year
	 * @param mes
	 * @return
	 */
	public CantidadMonetaria acumuladoDelMesCredito(final String cliente,final int year,final int mes,final Currency moneda){
		return (CantidadMonetaria)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List l=session.createQuery("select sum(v.total.amount) " +
						" from Venta v " +
						" where v.year=:year and v.mes=:mes and v.clave=:clave "+ 
						" and v.subTotal.currency=:moneda" +
						" and v.tipo in (:tipo1,:tipo2)" +
						" and v.serie=:serie "
						)
				.setInteger("year", year)
				.setInteger("mes", mes)
				.setString("clave", cliente)
				.setParameter("moneda", moneda, Hibernate.CURRENCY)
				.setParameter("tipo1", "E")
				.setParameter("tipo2", "S")
				.setParameter("serie", "E")
				.list();
				Number n1=l.isEmpty()?0:(Number)l.get(0);				
				List l2=session.createQuery("select sum(v.importe.amount) " +
						" from NotasDeCreditoDet v " +
						" where v.year=:year and v.mes=:mes and v.clave=:clave " +
						" and v.importe.currency=:moneda" +
						" and v.nota.tipo=:tipo")
				.setInteger("year", year)
				.setInteger("mes", mes)
				.setString("clave", cliente)
				.setParameter("moneda", moneda, Hibernate.CURRENCY)
				.setParameter("tipo", "J")
				.list();
				Number n2=l2.isEmpty()?0:(Number)l2.get(0);
				if(n2==null)
					n2=0;
					String msg=MessageFormat.format("Ventas: {0} Devoluciones: {1}", n1,n2);
					logger.debug(msg);					
				double val=n1.doubleValue()+n2.doubleValue();
				return new CantidadMonetaria(val,moneda);
			}
			
		});
	}
	
	/**
	 * Acumulado de ventas para un grupo de clientes conocido como corporativo
	 * @param cliente
	 * @param year
	 * @param mes
	 * @return
	 */
	public CantidadMonetaria acumuladoDelMesPorCorporativo(final String cliente,final int year,final int mes){
		return (CantidadMonetaria)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List l=session.createQuery("select sum(v.subTotal.amount) " +
						" from Venta v " +
						" where v.year=:year and v.mes=:mes and v.clave=:clave "+
						" and v.tipo in(:tipo1,:tipo2)" +
						" and v.serie=:serie "
						)
				.setInteger("year", year)
				.setInteger("mes", mes)
				.setString("clave", cliente)				
				.setParameter("tipo1", "E")
				.setParameter("tipo2", "S")
				.setParameter("serie", "E")
				.list();
				Number n1=l.isEmpty()?0:(Number)l.get(0);
				//System.out.println("Ventas "+n1);
				List l2=session.createQuery("select sum(v.importe.amount) " +
						" from NotasDeCreditoDet v " +
						" where v.year=:year and v.mes=:mes and v.clave=:clave " +
						//" and v.importe.currency=:moneda" +
						" and v.nota.tipo=:tipo")
				.setInteger("year", year)
				.setInteger("mes", mes)
				.setString("clave", cliente)
				.setParameter("tipo", "J")
				.list();
				Number n2=l2.isEmpty()?0:(Number)l2.get(0);
				if(n2==null)
					n2=0;
				//System.out.println("Devoluciones "+n2);
				
				//if(logger.isDebugEnabled()){
					String msg=MessageFormat.format("Ventas: {0} Devoluciones: {1}", n1,n2);
					logger.debug(msg);
					System.out.println(msg);
				//}
				double val=n1.doubleValue()+n2.doubleValue();
				return new CantidadMonetaria(val,CantidadMonetaria.PESOS);
			}
			
		});
	}

	public List<Venta> buscarVentasConSaldo(String clave) {
		return getHibernateTemplate().find("from Venta v left join fetch v.credito where v.year>2005 and v.saldo>0 and v.clave=?",clave);
	}
	
	public List<Venta> buscarVentasCreditoConSaldo(final String clave){
		return getHibernateTemplate().find("from Venta v left join fetch v.credito where v.credito is not null and " +
				" v.year>2005 and v.saldo>0 and v.clave=?",clave);
	}

	public List<Venta> buscarVentasParaRevisar(final Date fecha) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from Venta v " +
						"left join fetch v.credito c " +
						"left join fetch v.cliente cc " +
						"left join fetch cc.credito cred " +
						"where c.fechaRevisionCxc=:date and v.saldo>1")
						.setParameter("date", fecha,Hibernate.DATE)
						.list();				
			}			
		});
	}

	public List<Venta> buscarVentasParaCobrar(final Date fecha) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from Venta v left join fetch v.credito c where c.reprogramarPago=:date and v.saldo>1")
						.setParameter("date", fecha,Hibernate.DATE)
						.list();				
			}			
		});
	}

	/**
	 * Actualiza algunos valires de la venta que son calculados con sub selects
	 */
	public void sincronizar(final Venta v) {
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(v, LockMode.NONE);
				v.getSaldo();
				return null;
			}
			
		});
		
	}

	/**
	 * 
	 */
	public List<Venta> buscarVentasYTD(Cliente c) {
		final int year=Periodo.obtenerYear(new Date());
		return getHibernateTemplate().find("from Venta v where v.year=? and v.clave=?  ", new Object[]{new Integer(year),c.getClave()}); 
		
	}

	public List<Long> buscarVentasIdsConSaldo() {
		List<Long> ventas=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {								
				return session
				.createQuery("select v.id from Venta v  where v.year>=2006 " +
						"and v.origen =:cre and v.saldo>1 order by v.id")
				.setParameter("cre", "CRE")
				.list();
			}
		});
		return ventas;
	}

	public List<Venta> buscarVentas(final Date dia){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List res=session.createQuery(
						"from Venta v left join fetch v.partidas " +
						"where  v.fecha=:fecha")				
				.setParameter("fecha", dia,Hibernate.DATE)
				.list();
				SetUniqueList list=SetUniqueList.decorate(res);
				return list;
			}
			
		});
	}

	/**
	 * Busca todas las ventas de credito de un cliente a partir del 2007 y/o
	 * con saldo del  2006
	 * 
	 * @param clave
	 * @return
	 */
	public List<Venta> buscarVentasCreditoPorCliente(final String clave) {
		List<Venta> ventas=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {								
				return session
				.createQuery(" from Venta v  " +
						" left join fetch v.credito c " +
						" left join fetch v.cliente cc" +
						" where v.year>=2006" +						
						" and v.clave=:clave " +
						" and v.origen =:cre " +
						" order by v.fecha desc")
				.setParameter("cre", "CRE")
				.setParameter("clave", clave)
				.list();
			}
		});
		return ventas;
	}
	

	/**
	 * 
	 */
	public List<Venta> buscarVentasCreditoPorCliente(final String clave,final int year,final int mes){
		List<Venta> ventas=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {								
				return session
				.createQuery(" from Venta v  " +
						" left join fetch v.credito c " +
						" left join fetch v.provision p" +
						" left join fetch v.cliente" +
						" where v.year=:year " +
						" and v.mes=:mes" +
						" and v.clave=:clave " +
						" and v.origen =:cre " +
						" order by v.fecha desc")
				.setParameter("cre", "CRE")
				.setParameter("clave", clave)
				.setInteger("year", year)
				.setInteger("mes", mes)
				.list();
			}
		});
		return ventas;
	}
	
	
	/**
	 * Busca las ventas de credito para un cliente para el periodo indicado
	 * 
	 * @param clave
	 * @param periodo
	 * @return
	 */
	public List<Venta> buscarVentasCreditoPorCliente(final String clave, final Periodo periodo) {
		System.out.println("Buscando ventas para cliente: "+clave+" periodo:"+periodo);
		List<Venta> ventas=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {								
				return session
				.createQuery(" from Venta v  " +
						" left join fetch v.credito c " +
						" left join fetch v.provision p" +
						" left join fetch v.cliente" +
						" where v.clave=:clave " +
						" and v.origen =:cre " +
						" and v.fecha between :f1 and :f2 " +
						" order by v.fecha desc")
				.setParameter("cre", "CRE")
				.setParameter("clave", clave)
				.setParameter("f1", periodo.getFechaInicial(),Hibernate.DATE)
				.setParameter("f2", periodo.getFechaFinal(),Hibernate.DATE)
				.list();
			}
		});
		return ventas;
	}

	public List<VentaDet> buscarVentasDetPorCliente(final String clave,final  Periodo p) {
		List<VentaDet> ventas=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {								
				return session
				.createQuery("from VentaDet det  " +
						" left join fetch det.articulo a " +						
						" left join fetch det.venta v " +
						" where v.year>=2006 " +						
						" and v.clave=:clave " +
						" and v.fecha between :fechaIni and :fechaFin" +						
						" order by v.fecha desc")				
				.setParameter("clave", clave)
				.setParameter("fechaIni", p.getFechaInicial(),Hibernate.DATE)
				.setParameter("fechaFin", p.getFechaFinal(),Hibernate.DATE)
				.list();
			}
		});
		return ventas;
	}
	
	public BigDecimal buscarAcumuladoDeVentas(String clave,final int year, final int mes){
		return null;
	}
	
	public static void main(String[] args) {
		//ServiceLocator.getVentasManager().getVentasDao().buscarVentasDetPorCliente("A010406", new Periodo("01/01/07","30/06/07"));
		//CantidadMonetaria monto=ServiceLocator.getVentasManager().getVentasDao().acumuladoDelMesCredito("M030549", 2007, 7);
		//System.out.println(monto);
		List<Venta> ventas=ServiceLocator.getVentasManager().getVentasDao().buscarVentasCreditoPorCliente("P010384", new Periodo("01/10/2006","31/10/2006"));
		System.out.println(ventas.size());
		
	}

}
