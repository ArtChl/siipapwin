package com.luxsoft.siipap.ventas.managers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.UniqueList;

import com.luxsoft.siipap.cxc.dao.DescuentoEspecialDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorArticuloDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorClienteDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorVolumenDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.DescuentoEspecial;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.cxc.domain.DescuentoPorCliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorVolumen;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.cxc.domain.ProvisionDet;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaACredito;
import com.luxsoft.siipap.ventas.domain.VentaDet;

/**
 * Clase controladora para la persistencia de Ventas segun las reglas de negocios
 * 
 * @author Ruben Cancino
 * @TODO Este bean administrado por Spring debe generar un evento al contenedor cada vez
 * que se ejectue el metodo actualizarVenta para dar conocimiento a otros de que una venta fue actualizada
 * 
 * 
 *
 */
@SuppressWarnings("unchecked")
//@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
public class VentasManagerImpl extends HibernateDaoSupport implements VentasManager{
	
	
	private DescuentoPorClienteDao descuentoPorClienteDao;
	private DescuentoPorArticuloDao descuentoPorArticuloDao;
	private DescuentoPorVolumenDao descuentoPorVolumenDao;
	private DescuentoEspecialDao descuentoEspecialDao;
	private VentasDao ventasDao;
	
	private Logger logger=Logger.getLogger(getClass());
	
	
	/**
	 * Crea la provision para una venta a credito
	 * 
	 * @param v
	 * @return
	 */
	private Provision generarProvision(final Venta v){
		Provision p=new Provision(v);
		for(VentaDet det:v.getPartidas()){			
			p.agregarPartida(det);
		}		
		return p;
	}
	
	/**
	 * Actualiza la provision de una venta a credito con precios de lista
	 * 
	 * 
	 */
	@Transactional (propagation=Propagation.MANDATORY, readOnly=false)
	private void actualizarProvision(final Venta venta){
		/*
		if(venta.getSaldo().doubleValue()<=0 || venta.getDescuentos()>0)
			return;**/
		
		final boolean nueva=(venta.getProvision()==null);
		final Provision p=venta.getProvision()!=null
			?venta.getProvision()				
			:generarProvision(venta);
		boolean porEscala=true;
		
		Cliente c=p.getVenta().getCliente();
		ClienteCredito cr=c.getCredito();
		if(cr.isSuspenderDescuento())
			return;
		//Caso 1 existe una descuento especial para la venta
		
		DescuentoEspecial de=getDescuentoEspecialDao().buscar(p.getVenta());
		if(de!=null){
			p.setDescuento1(de.getDescuento());
			p.actualizarCargo();
			p.calcularProvision();
			p.actualizarAplicado();
			venta.getCredito().setDescuento(de.getDescuento());
			porEscala=false;
			return;
		}
		
		//Caso 2 cuando el cliente es cheque post fechado
		if(cr.isChequep()){
			porEscala=false;
			double valor=getDescuentoPorVolumenDao().calcularDescuentoChequeP(venta.getId());
			if(valor!=0){				
				int plazo=cr.getPlazo();
				int cargo=0;
				if(plazo<=15)
					cargo=1;
				else if(plazo<=30)
					cargo=2;
				else if(plazo<=45)
					cargo=3;
				valor-=cargo;
				p.setDescuento1(valor);
				p.actualizarCargo();
				p.calcularProvision();
				p.actualizarAplicado();
				venta.getCredito().setDescuento(valor);
				return;
			}
			return;
		}
		
		// Caso 3 cuando el cliente tiene descuentos fijos 
		
		DescuentoPorCliente descC=getDescuentoPorClienteDao().buscar(p.getVenta().getCliente());
		if(descC!=null){
			p.setDescuento1(descC.getDescuento());
			porEscala=false;
			
			//Si la provision es nueva ó el descuento adicional es cero
			if(nueva || p.getVenta().getCredito().getDescuento2()==0)
			//if(nueva)
				p.getVenta().getCredito().setDescuento2(descC.getAdicional());
		}
		
		// Caso 4 Cuando el cliente tiene descuentos por articulo
		
		for(ProvisionDet det:p.getPartidas()){
			DescuentoPorArticulo da=getDescuentoPorArticuloDao().buscar(p.getVenta().getCliente(), det.getArticulo());
			if(da!=null){
				det.setDescArticulo(da);
				porEscala=false;
			}
			
		}
		
		p.calcularProvision();
		p.actualizarAplicado();
		//Si la provision es nueva ó el descuento pactado es cero
		//if(nueva) {
		if(nueva || p.getVenta().getCredito().getDescuento()==0){
			double descPactado=p.getDescuentoConCargo();
			p.getVenta().getCredito().setDescuento(descPactado);
		}
		
		//Caso 5 Cuando el cliente solo tiene descuento por volumen (Ultima opcion)
		//if(p.getDescuento1()<=0){
		if(porEscala){
		//if(p.getDescuentoConCargo()==0){
			
			CantidadMonetaria acumulado;
			if(venta.getCliente().getCredito().getCorporativo()!=null){
				acumulado=getVentasDao().acumuladoDelMesPorCorporativo(p.getVenta().getClave(), p.getVenta().getYear(), p.getVenta().getMes());
			}else				
				acumulado=getVentasDao().acumuladoDelMesCredito(p.getVenta().getClave(), p.getVenta().getYear(), p.getVenta().getMes());
			
			DescuentoPorVolumen dVol=getDescuentoPorVolumenDao().buscar(acumulado.getAmount().doubleValue());
			if(dVol!=null){
				p.setDescuento1(dVol.getDescuento());
			}
			p.calcularProvision();
			p.actualizarAplicado();	
			double descuento=p.getDescuento1();
			p.getVenta().getCredito().setDescuento(descuento);
			
		}
		p.getVenta().getCredito().setCargo(p.getCargoCalculado());
		
	}
	
	/**
	 * Actualiza en todas las ventas provisionables
	 *
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void actualizarVentas(){
		getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {								
				List<Venta> ventas1=session
				.createQuery("from Venta v  " +
						" left join fetch v.credito vc" +
						" left join fetch v.provision p" +
						" left join fetch v.cliente c" +
						" left join fetch c.credito cc" +
						" where v.year>=2006 " +
						"   and v.origen =:cre " +
						"   and v.provision is not null" +						
						"   order by v.id")
				.setParameter("cre", "CRE")				
				.list();
				
				List<Venta> ventas2=session
				.createQuery("from Venta v  " +
						" left join fetch v.credito vc" +
						" left join fetch v.provision p" +
						" left join fetch v.cliente c" +
						" left join fetch c.credito cc" +
						"where v.year>=2006 " +
						"and v.origen =:cre " +						
						"and v.saldo>1 " +
						"order by v.id")
				.setParameter("cre", "CRE")
				.list();
				ventas2.addAll(ventas1);
				final EventList<Venta> source=GlazedLists.eventList(ventas2);
				UniqueList<Venta> ventas=new UniqueList<Venta>(source,GlazedLists.beanPropertyComparator(Venta.class, "id"));
				for(Venta v:ventas){
					actualizarVenta(v);
				}
				return null;
			}
		});
	}

	
	/**
	 * Actualiza una venta, con todo lo que esto implica
	 * 
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void actualizarVenta(final Venta v){
		
		
		//Hacemos persistente los objetos requeridos sincronizandolos  al persistent context de la transaccion
		getVentasDao().salvar(v);
		if(v.getCliente()==null){
			Object o=getSession().createQuery("from Cliente c where c.clave=?").setString(0, v.getClave()).uniqueResult();
			if(o==null)	return;
			v.setCliente((Cliente)o);
		}
		getSession().saveOrUpdate(v.getCliente());
		
		//Actualizamos el registro de ClienteCredito si no existe
		if("CRE".equals(v.getOrigen())){
			if(v.getCliente().getCredito()==null){
				v.getCliente().generarCredito();
			}
		}
		
		//Hibernate.initialize(v.getPartidas());
		//Salvamos posibles articulos nuevos
		for(VentaDet det:v.getPartidas()){
			if(det.getArticulo()!=null && det.getArticulo().getId()==null){
				getSession().save(det.getArticulo());
			}
		}
		
		//Actualizamos datos de la venta a credito (VentaACredito)
		//Este dato es general para todas las ventas origen=CRE
		//Hibernate.initialize(v.getCliente());
		v.actualizarDatosDeCredito();	
		
		//Actualizamos la provision para ventas a credito con precio de lista 
		if("E".equals(v.getSerie()) && ("E".equals(v.getTipo()) || "S".equals(v.getTipo()))){
			if(!"JUR".equals(v.getOrigen()))
				actualizarProvision(v);
		}
		
		if(logger.isDebugEnabled()){
			String pattern="Venta exitosamente actualizada {0}" +
					"\n Credito: {1} \nProvision: {2}";
			String msg=MessageFormat.format(pattern
					, v
					,v.getCredito()!=null?v.getCredito():"S.Cred"
					,v.getProvision()!=null?v.getProvision():"S.Prov");
			logger.debug(msg);
		}
		getVentasDao().salvar(v);
	}
	
	/**
	 * Actualiza una venta usando su id
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void actualizarVenta(Long id){
		Venta v=(Venta)getHibernateTemplate().get(Venta.class, id);
		actualizarVenta(v);
	}
	
	/**
	 *--Elimina el bean {@link VentaACredito} y el bean {@link Provision} vinculados on la venta
	 *<strong>Modificado para no eliminar nada solo pone en cero los campos adecuados
	 * 
	 * @param v
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void eliminarVentaCredito(final Venta v){
		/*
		if(v.getProvision()!=null)
			getSession().delete(v.getProvision());
		if(v.getCredito()!=null)
			getSession().delete(v.getCredito());
		v.setCredito(null);
		v.setProvision(null);
			*/
		v.getCredito().setDescuento(0);
		v.getCredito().setDescuento2(0);
		//if(v.getProvi)
		if(v.getProvision()!=null)
			v.getProvision().setCargoCalculado(0);
		getVentasDao().salvar(v);
	}
	
	/**
	 * Re-carga la venta con la base de datos
	 * 
	 * @param v
	 */
	@Transactional (propagation=Propagation.SUPPORTS, readOnly=true)
	public void refresh(Venta v) {
		getSessionFactory().getCurrentSession().update(v);
		getSessionFactory().getCurrentSession().refresh(v);
		Hibernate.initialize(v.getCliente());
		Hibernate.initialize(v.getCredito());
		//getSessionFactory().getCurrentSession().refresh(v);
	}

	/**
	 * Regresa una lista con lis ids de las ventas a credito con saldo
	 * 
	 */
	public List<Long> getListaDeVentasACreditoConSaldo(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {								
				return session
				.createQuery("select v.id from Venta v  where v.year>=2006 " +
						"and v.origen =:cre and v.saldo>1 order by v.clave")
				.setParameter("cre", "CRE")				
				.list();
				
			}
		});
	}
	
	/**
	 * Aplica la provision de manera inmediata
	 * 
	 * @param v
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public void aplicarProvision(final Venta v){
		if(v.getProvision()!=null){
			refresh(v);
			Hibernate.initialize(v.getCliente());
			Hibernate.initialize(v.getCliente().getCredito());
			//System.out.println("Venta: desc: "+v.getDescuento1());			
			v.getProvision().actualizarAplicado();
		}		
	}
		
	
	
	/**
	 * Regresa un arreglo de dos listas de ventas una para cobro y otra para revision
	 * 
	 * @return
	 */
	//@Transactional(propagation=Propagation.SUPPORTS)
	public List<Venta>[] buscarCobranzaDelDia(final Date fecha){
		List<Venta>[] l=new List[]{
				getVentasDao().buscarVentasParaRevisar(fecha),
				getVentasDao().buscarVentasParaCobrar(fecha)};		
		return l;
	}
	
	/**
	 * Elimina una venta 
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void eliminarVenta(Long id) {
		Venta v=getVentasDao().buscarPorId(id);
		if(!v.getOrigen().equals("CRE")){
			getVentasDao().eliminar(v);
		}
		if(v.getProvision()!=null)
			getSession().delete(v.getProvision());
		if(v.getCredito()!=null)
			getSession().delete(v.getCredito());		
		getSession().delete(v);
		
	}
	
	public void actualizarVentasYtd(final Cliente c){
		List<Venta> ventas=getVentasDao().buscarVentasConSaldo(c.getClave());
		for(Venta v:ventas){
			actualizarVenta(v);
		}
		
	}
	
	public List<Venta> buscarCuentasPorCobrar() {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {								
				return session
				.createQuery("from Venta v  " +
						" left join fetch v.credito vc" +
						" left join fetch v.provision p" +
						" left join fetch v.cliente c" +
						" left join fetch c.credito cc" +
						" where v.year>=2006 " +
						"   and v.origen =:cre " +
						"   and v.saldo>1" +
						"   order by v.id")
				.setParameter("cre", "CRE")				
				.list();
			}
		});
	}
	
	/**
	 * Localiza todas las ventas del periodo con saldo y de tipo CRE
	 * 
	 * @param p
	 * @return
	 */
	public List<Venta> buscarCuentasPorCobrarCre(final Periodo p){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				 return session
				.createQuery("from Venta v  " +
						" left join fetch v.credito vc" +
						" left join fetch v.provision p" +
						" left join fetch v.cliente c" +
						" left join fetch c.credito cc" +
						" where v.year>=2006 " +
						"   and v.origen =:cre " +
						"   and v.saldo>1" +
						"   and v.fecha between :f1 and :f2" +
						"   order by v.id")
						.setParameter("cre", "CRE")
						.setParameter("f1", p.getFechaInicial(),Hibernate.DATE)
						.setParameter("f2", p.getFechaFinal(),Hibernate.DATE)
						.list();
				
			}			
		});
	}

	/*** Colaboradores ***/
	
	public DescuentoPorClienteDao getDescuentoPorClienteDao() {
		return descuentoPorClienteDao;
	}

	public void setDescuentoPorClienteDao(
			DescuentoPorClienteDao descuentoPorClienteDao) {
		this.descuentoPorClienteDao = descuentoPorClienteDao;
	}

	public DescuentoPorVolumenDao getDescuentoPorVolumenDao() {
		return descuentoPorVolumenDao;
	}

	public void setDescuentoPorVolumenDao(
			DescuentoPorVolumenDao descuentoPorVolumenDao) {
		this.descuentoPorVolumenDao = descuentoPorVolumenDao;
	}

	public VentasDao getVentasDao() {
		return ventasDao;
	}

	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}

	public DescuentoEspecialDao getDescuentoEspecialDao() {
		return descuentoEspecialDao;
	}

	public void setDescuentoEspecialDao(DescuentoEspecialDao descuentoEspecialDao) {
		this.descuentoEspecialDao = descuentoEspecialDao;
	}

	public DescuentoPorArticuloDao getDescuentoPorArticuloDao() {
		return descuentoPorArticuloDao;
	} 

	public void setDescuentoPorArticuloDao(
			DescuentoPorArticuloDao descuentoPorArticuloDao) {
		this.descuentoPorArticuloDao = descuentoPorArticuloDao;
	}	
	
	
	public static void main(String[] args) {
		
		VentasManager manager=ServiceLocator.getVentasManager();		
		manager.actualizarVenta(2792646L);
		
		/**
		StopWatch watch=new StopWatch();
		watch.start();
        List<Venta> v=manager.buscarCuentasPorCobrarCre(new Periodo("01/11/2007","30/11/2007"));
        watch.stop();
        System.out.println("Ventas: "+v.size());
        System.out.println(watch.shortSummary());
		//manager.actualizarVentas();
		**/
		
		/**
		List<Long> ids=manager.getListaDeVentasACreditoConSaldo();
		System.out.println("Ventas a procesar: "+ids.size());		
		for(Long id:ids){
			try {
				manager.actualizarVenta(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		**/
		/**
		List<Map<String, Object>> rows=ServiceLocator.getJdbcTemplate().queryForList("select VENTA_ID from PROVISION_TEMP1");
		for(Map<String, Object> row:rows){
			BigDecimal id=(BigDecimal)row.get("VENTA_ID");
			manager.actualizarVenta(id.longValue());
		}
		**/
		/**
		StopWatch watch=new StopWatch();
		watch.start();		
		List<Venta> ventas=manager.getVentasDao().buscarVentasCreditoPorCliente("U050008",new Periodo("19/02/2007","19/02/2008"));
		watch.stop();		
		System.out.println("Ventas: "+ventas.size()+ "Time: "+watch.getTotalTimeSeconds());
		**/
	}

	
	

}
