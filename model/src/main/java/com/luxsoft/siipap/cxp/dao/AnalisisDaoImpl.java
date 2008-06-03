package com.luxsoft.siipap.cxp.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxp.domain.Analisis;
import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.cxp.domain.AnalisisDet;
import com.luxsoft.siipap.cxp.domain.CXPFactura;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.utils.SiipapDateUtils;

/**
 * Implementacion de AnalisisDao 
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class AnalisisDaoImpl extends HibernateDaoSupport implements AnalisisDao{
	
	private CXPDao cxpDao;

	public void salvar(final Analisis analisis) {		
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				//boolean val=SessionFactoryUtils.isSessionTransactional(session,getSessionFactory());
				//System.out.println("Estamos en una transaccion? "+val);
				analisis.setFactura(analisis.getFactura().trim());//Garantizar un trim al campo factura
				analisis.setClave(analisis.getProveedor().getClave());
				analisis.setNombre(analisis.getProveedor().getNombre());
				
				//session.update(analisis);
				//session.lock(analisis,LockMode.NONE);
				//Actualizar  la entrada
				
				// Actualizar el costo de los coms Y CANTIDAD ANALIZADA
				//TODO Refactorizar a un metodo para recalcular la entrada analizada 
				
				for(AnalisisDet det:analisis.getPartidas()){					
					AnalisisDeEntrada entrada=det.getEntrada();
					det.setTc(analisis.getTc());
					session.lock(entrada,LockMode.NONE);
					if(entrada.getFENT().after(SiipapDateUtils.getMXDate("31/12/05"))){
						entrada.getCom().setCosto(det.getNetoMN()); //COSTO
					}
					BigDecimal analizadas=det.getCantidad();
					if(det.getId()!=null)
						entrada.setAnalizada(entrada.getAnalizada().subtract(analizadas));
					entrada.setAnalizada(entrada.getAnalizada().add(analizadas));
				}
				
				
				analisis.calcularImportesEnMN();
				
				//De ser necesario eliminamos la cuenta por pagar
				//Actualizar CXP
				
				if(analisis.getId()!=null){
					try{
						getCxpDao().eliminarCXP(analisis.getCargo());
					}catch(Exception ex){
						throw new RuntimeException("No se pudo eliminar la cuenta por pagar, es posible que la factura tenga pagos aplicados",ex);
					}					
				}				
				CXPFactura cargo=new CXPFactura(analisis);
				cargo.calcularDescuentoFinanciero();
				cargo.calcularVencimiento();
				getCxpDao().actualizarCXP(cargo);
				
				
				session.saveOrUpdate(analisis);
				return null;
			}
		});		
	}

	public void eliminar(final Analisis analisis) {
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {				
				session.lock(analisis,LockMode.NONE);
				session.delete(analisis);
				
				//Actualizar el costo de la entrada
				for(AnalisisDet det:analisis.getPartidas()){					
					AnalisisDeEntrada entrada=det.getEntrada();
					session.lock(entrada,LockMode.NONE);
					entrada.getCom().setCosto(CantidadMonetaria.pesos(0));
					entrada.setAnalizada(entrada.getAnalizada().subtract(det.getCantidad()));
				}
				
				//Actualizar la cuenta por pagar
				getCxpDao().eliminarCXP(analisis.getCargo());
				session.delete(analisis);
				
				
				return null;
			}
		});
		
	}

	public Analisis buscarAnalisis(final Long id) {
		return (Analisis)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Analisis.class)
				.setFetchMode("partidas",FetchMode.JOIN)
				.add(Restrictions.eq("id",id))
				.uniqueResult();
			}			
		});
	}

	
	public List<AnalisisDet> buscarAnalisisUnitarios(final Date fecha) {
		final String hql="select d from AnalisisDet d " +
			" left join fetch d.analisis fa " +
			" where fa.fecha=:date order by fa.proveedor.clave";
		List<AnalisisDet> beans=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter("date",fecha,Hibernate.DATE)
				.list();
			}
	
		});
		return beans;
	}

	public List<AnalisisDet> buscarAnalisisUnitarios(final Periodo periodo) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(AnalisisDet.class)
				.createAlias("analisis","fa")
				.setFetchMode("analisis",FetchMode.JOIN)
				.setFetchMode("analisis.proveedor",FetchMode.JOIN)
				.add(Restrictions.between("fa.fecha",periodo.getFechaInicial(),periodo.getFechaFinal()))
				.setMaxResults(1000)
				.list();
			}			
		});
	}

	public List<Analisis> buscarAnalisisPorPeriodo(final Periodo p) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria c=session.createCriteria(Analisis.class);
				c.setFetchMode("proveedor",FetchMode.JOIN);
				return 
					c.add(Restrictions.between("fecha",p.getFechaInicial(),p.getFechaFinal()))
					.list();
				
			}
			
		});
	}
	
	/**
	 * Busca todos los analisis de un proveedor
	 * 
	 * @param p
	 * @return
	 */
	public List<Analisis> buscarAnalisisPorProveedor(final Proveedor p){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Analisis.class)
				.setFetchMode("proveedor",FetchMode.JOIN)				 
				.add(Restrictions.eq("proveedor",p))
				.list();
				
			}
			
		});
	}

	public void actualizarImpresion(Analisis analisis) {
		analisis.setImpreso(true);
		getHibernateTemplate().saveOrUpdate(analisis);
		
	}

	public void inicializarPartidas(Object analisis) {
		final Analisis fac=(Analisis)analisis;
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(fac,LockMode.NONE);
				Hibernate.initialize(fac.getPartidas());
				for(AnalisisDet det:fac.getPartidas()){
					Hibernate.initialize(det.getEntrada());
					Hibernate.initialize(det.getEntrada().getCom());
					Hibernate.initialize(det.getEntrada().getCom().getMvcomp());
				}
				return null;
			}
			
		});
		
	}
	
	/**
	 * Inicializa una partida de analisis
	 * 
	 * @param det
	 */
	public void inicializarAnalisisDet(final AnalisisDet det){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(det,LockMode.NONE);
				Hibernate.initialize(det.getPrecio());
				return null;
			}
			
		});

	}

	public void reload(Analisis analisls) {
		getHibernateTemplate().refresh(analisls);
		
	}
	
	public void reAsignarCostoDeCom(final Long id){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				AnalisisDet det=(AnalisisDet)session.get(AnalisisDet.class,id);
				AnalisisDeEntrada ae=det.getEntrada();
				CantidadMonetaria old=ae.getCom().getCosto();
				ae.getCom().setCosto(det.getNeto());
				Assert.isTrue(ae.getCom().getALMFECHA().after(SiipapDateUtils.getMXDate("31/12/2005")),"Solo se pueden modificar coms a partir del 2006");
				session.update(ae.getCom());
				CantidadMonetaria nuevo=ae.getCom().getCosto();
				logger.debug("Costo anterior:"+ old);
				logger.debug("Costo nuevo: "+nuevo);
				return null;
			}			
		});
	}

	public CXPDao getCxpDao() {
		return cxpDao;
	}

	public void setCxpDao(CXPDao cxpDao) {
		this.cxpDao = cxpDao;
	}
	
	/**
	 * Localiza un analisis a partir de una entrada
	 * @param entrada
	 * @return
	 */
	public AnalisisDet buscarAnalisis(final AnalisisDeEntrada entrada){
		/**
		String hql="from AnalisisDet d  where d.entrada=:entrada";
		List<AnalisisDet> det=getHibernateTemplate().findByNamedParam(hql,
				new String[]{"entrada"},
				new Object[]{Hibernate.entity(AnalisisDeEntrada.class)});
		if(det.size()>0)
			return det.get(0);
		return null;
		**/
		AnalisisDet det=(AnalisisDet)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List analisis=session.createCriteria(AnalisisDet.class)
				.add(Restrictions.eq("entrada",entrada))
				.list();
				if(analisis.size()>0)
					return analisis.get(0);
				return null;
			}
			
		});
		return det;
	}
	
	
	/**
	 * Valida que el costo del analisis este reflejado en el campo
	 * de COMS. Regresa una lista con los AnalisDet que fueron ajustados
	 * si que estos cambios sean persistidos a la base de datos
	 * 
	 * @return
	 */
	public List<AnalisisDet> validarCostoEnComs(){
		final List<AnalisisDet> errores=new ArrayList<AnalisisDet>();
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ScrollableResults rs=session.createCriteria(AnalisisDet.class).scroll();
				int row=0;
				while(rs.next()){
					row=rs.getRowNumber();
					AnalisisDet det=(AnalisisDet)rs.get(0);
					BigDecimal tc=det.getAnalisis().getTc();
					det.setTc(tc);
					CantidadMonetaria neto=det.getNeto().multiply(tc);
					
					if(neto.getCurrency().equals(Currency.getInstance(Locale.US))){						
						neto=CantidadMonetaria.pesos(neto.getAmount().doubleValue());
						System.out.println("Error en la moneda del neto del analisis: "+det+" \nNeto correcto: "+neto);
					}
					
					det.setNetoMN(neto);
					CantidadMonetaria costo=det.getEntrada().getCom().getCosto();
					
					if(!neto.equals(costo)){
						//CantidadMonetaria dif=neto.subtract(costo);
						//System.out.println("Row: "+row+" DetId: "+det.getId()+" Neto: "+neto.getAmount()+"Costo: "+costo.getAmount()+" Dif: "+dif.getAmount());
						System.out.println("Ajustando Costo anterior: "+costo+ "Costo nuevo: "+neto);
						det.getEntrada().getCom().setCosto(neto);
					}
					//System.out.println("Row: "+row+"  ID "+det.getId()+" OK");
					if(row%20==0){
						session.flush();
						session.clear();
					}
				}
				return null;
			}
			
		});
		return errores;
	}
	
	/**
	 * TODO BORRAR
	 */
	public void parcheDeCambioDeProveedor(){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				String hql="from Analisis a where a.id in(16345)";
				List<Analisis> analisis=session.createQuery(hql).list();
				
				Proveedor p=(Proveedor)session.createCriteria(Proveedor.class).add(Restrictions.eq("clave","P095")).list().get(0);
				System.out.println("Nuevo proveedor: "+p);
				for(Analisis a:analisis){
					a.setProveedor(p);
					a.setClave(a.getProveedor().getClave());
					a.setNombre(a.getProveedor().getNombre());
					a.getCargo().setClave(a.getProveedor().getClave());
					a.getCargo().setProveedor(p);
					a.getCargo().setClave(p.getClave());
					a.getCargo().setNombre(a.getNombre());
					System.out.println("Proveedor  en CXP:"+a.getCargo().getProveedor());
					System.out.println("Proveedor  en Analisis:"+a.getProveedor());
					System.out.println("Analisis: "+a);
					
				}
				
				
				return null;
			}
			
		});
	}
	
	public List<AnalisisDet> buscarAnalisisUnitariosPorFechaDeEntrada(final Date fecha,final Integer sucursal){
		final String hql="select d from AnalisisDet d " +
		" left join fetch d.analisis fa " +
		" left join fetch d.analisis.proveedor p" +
		" where d.entrada.FENT=:date and d.entrada.SUCURSAL=:suc order by fa.proveedor.clave";
		List<AnalisisDet> beans=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter("date",fecha,Hibernate.DATE)
				.setInteger("suc",sucursal)
				.list();
			}

		});
		return beans;
	}
	
	public Analisis buscarAnalisis(final CXPFactura cargo)	{
		return(Analisis)getHibernateTemplate().execute(new HibernateCallback(){			

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Analisis a " +				
				"where a.cargo=:cargo";
				return session.createQuery(hql)
					.setParameter("cargo",cargo,Hibernate.entity(CXPFactura.class))
					.setMaxResults(1)
					.uniqueResult();
			}			
		});
		
	}

}
