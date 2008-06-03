package com.luxsoft.siipap.cxp.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Currency;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

//import com.luxsoft.siipap.DaosEnum;
//import com.luxsoft.siipap.ServiceLocator;
import com.luxsoft.siipap.cxp.domain.Analisis;
import com.luxsoft.siipap.cxp.domain.CXP;
import com.luxsoft.siipap.cxp.domain.CXPFactura;
import com.luxsoft.siipap.cxp.domain.CXPNCargo;
import com.luxsoft.siipap.cxp.domain.CXPNCredito;
import com.luxsoft.siipap.cxp.domain.CXPPago;
import com.luxsoft.siipap.cxp.domain.RequisicionDetalle;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.utils.SiipapDateUtils;

@SuppressWarnings("unchecked")
public class CXPDaoImpl extends HibernateDaoSupport implements CXPDao{

	public void salvarCXP(final CXP cxp) {
		if(cxp.getId()==null){
			cxp.setModificado(cxp.getCreado());
			cxp.setClave(cxp.getProveedor().getClave());
		}
		Assert.isTrue(cxp.actualizarTotales(),"Error al actualizar los totales del Movimiento: "+cxp);
		
		if((cxp instanceof CXPNCredito) || (cxp instanceof CXPPago)){
			
			cxp.setImporte(negativo(cxp.getImporte()));
			cxp.setImpuesto(negativo(cxp.getImpuesto()));
			cxp.setTotal(negativo(cxp.getTotal()));
			
			cxp.setImporteMN(negativo(cxp.getImporteMN()));
			cxp.setImpuestoMN(negativo(cxp.getImpuestoMN()));
			cxp.setTotalMN(negativo(cxp.getTotalMN()));
			
			if(cxp instanceof CXPNCredito)
				cxp.getFactura().aplicarNotaDeCredito((CXPNCredito)cxp);
			if(cxp instanceof CXPPago){
				CXPPago pago=(CXPPago)cxp;
				//CXPFactura factura=pago.getFactura();
				Assert.isTrue(pago.getTotal().getAmount().doubleValue()<0,"El pago  no es negativo");
//				 NSALDO factura.setSaldo(factura.getSaldo().add(pago.getTotal()));
//				 NSALDO factura.setSaldoMN(factura.getSaldoMN().add(pago.getTotalMN()));
			}
		}		
		getHibernateTemplate().saveOrUpdate(cxp);
		
	}
	
	private CantidadMonetaria negativo(CantidadMonetaria importe){
		if(importe.getAmount().doubleValue()<0)
			return importe;
		BigDecimal monto=importe.getAmount().abs().multiply(BigDecimal.valueOf(-1));
		return new CantidadMonetaria(monto.doubleValue(),importe.getCurrency());
	}

	public void eliminarCXP(final CXP cxp) {
		
		if(cxp instanceof CXPPago){
			CXPPago pago=(CXPPago)cxp;
			CantidadMonetaria importemn=pago.getTotalMN();
			CantidadMonetaria importe=pago.getTotal();
//			 NSALDO CantidadMonetaria saldo=pago.getFactura().getSaldo().subtract(importe);
//			 NSALDO CantidadMonetaria saldomn=pago.getFactura().getSaldoMN().subtract(importemn);
//			 NSALDO pago.getFactura().setSaldo(saldo);
//			 NSALDO pago.getFactura().setSaldoMN(saldomn);
			getHibernateTemplate().saveOrUpdate(pago.getFactura());
			//CantidadMonetaria saldo=pago.getFactura().getSaldoMN();
			
		}if(cxp instanceof CXPNCredito){
			CXPNCredito credito=(CXPNCredito)cxp;
			CantidadMonetaria importemn=credito.getTotalMN();
			CantidadMonetaria importe=credito.getTotal();
//			 NSALDO CantidadMonetaria saldo=credito.getFactura().getSaldo().subtract(importe);
//			 NSALDO CantidadMonetaria saldomn=credito.getFactura().getSaldoMN().subtract(importemn);
//			 NSALDO credito.getFactura().setSaldo(saldo);
//			 NSALDO credito.getFactura().setSaldoMN(saldomn);
			getHibernateTemplate().saveOrUpdate(credito.getFactura());
		}
		getHibernateTemplate().delete(cxp);
		if(logger.isDebugEnabled()){
			logger.debug("CXP eliminado: "+cxp);
		}
		
	}

	public void actualizarCXP(CXP cxp) {
		salvarCXP(cxp);
	}

	public CXP buscarCXP(final Long id) {
		return (CXP)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(CXP.class)
				.add(Restrictions.eq("id",id))
				.setMaxResults(1)
				.uniqueResult();
			}			
		});
	}
	
	
	public List<CXP> buscarMovimientos(final String proveedor,final String referencia){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(CXP.class)
				.add(Restrictions.eq("clave",proveedor))
				.add(Restrictions.eq("referencia",referencia.trim()))
				.list();
			}
			
		});
	}

	/**
	 * Facturas con saldo para un proveedor especifico
	 */
	public List<CXPFactura> buscarFacturas(final Proveedor proveedor) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(CXPFactura.class)
				.add(Restrictions.eq("proveedor",proveedor))
				//NSALDO.add(Restrictions.gt("saldo.amount",BigDecimal.ZERO))
				.add(Restrictions.gt("saldoMN",new Double(0)))
				.list();
			}			
		});
	}
	
	public List<CXPFactura> buscarFacturasSinRequisitar(final Proveedor proveedor){
		return buscarFacturasSinRequisitar(proveedor.getClave());
	}
	
	private List<CXPFactura> buscarFacturasSinRequisitar(final String proveedor){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				final String hql="from CXPFactura fac " +
						" where fac.proveedor.clave=:proveedor " +
						"   and fac.fecha>=:fecha " +
						//NSALDO"   and fac.saldo.amount>0"+
						"   and fac.saldo>0"+
						"   and fac not in(select rd.cargo from RequisicionDetalle rd where rd.proveedor=fac.proveedor)" +
						//"  and fac.requisicion is null" +
						"  order by fac.referencia";
				final List<CXPFactura> requisitadas=session.createQuery(hql)
				.setParameter("proveedor",proveedor)
				.setParameter("fecha",SiipapDateUtils.getMXDate("01/01/06"),Hibernate.DATE)
				.list();
				return requisitadas;
			}
			
		});
	}
	
	
	
	public List<CXPFactura> buscarFacturasSinRequisitar(final Proveedor proveedor,final Currency moneda){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				final String hql="from CXPFactura fac " +
						" where fac.proveedor.clave=:proveedor " +
						"   and fac.fecha>=:fecha " +						
						"   and fac.saldo>0 " +
						"   and fac.moneda=:moneda"+						
						"   and fac not in(select rd.cargo from RequisicionDetalle rd where rd.proveedor=fac.proveedor)" +						
						"  order by fac.referencia";
				final List<CXPFactura> requisitadas=session.createQuery(hql)
				.setParameter("proveedor",proveedor.getClave())
				.setParameter("fecha",SiipapDateUtils.getMXDate("01/04/06"),Hibernate.DATE)
				.setParameter("moneda", moneda,Hibernate.CURRENCY)
				.list();
				return requisitadas;
			}
			
		});
	}
	
	/**
	 * Busca todas las facturas con saldo pendiente
	 * @return
	 */
	public List<CXPFactura> buscarFacturasPendietnes(){
		//NSALDOString hql="from CXPFactura f where f.saldo.amount>0 order by f.clave,f.referencia";
		String hql="from CXPFactura f where f.saldo>0 order by f.clave,f.referencia";
		return getHibernateTemplate().find(hql);
	}
	
	public List<CXPFactura> buscarFacturasPendietnes(final String proveedor){
		final String hql="from CXPFactura " +
				"f where f.proveedor.clave=:proveedor " +
				" and f.saldo>0 " +
				" and f.fecha>=:fecha " +
				" and f.requisicion is not null" +
				" order by f.clave,f.referencia";
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter("proveedor",proveedor)
				.setParameter("fecha",SiipapDateUtils.getMXDate("01/01/06"),Hibernate.DATE)
				.list();
			}
			
		});
	}
	
	/**
	 * Busca todas las facturas con saldo pendiente, por proveedor
	 */
	public List<CXPFactura> buscarFacturasPendietnes(final Proveedor p){
		//NSALDOfinal String hql="from CXPFactura f where f.proveedor=:proveedor and f.saldo.amount>0 order by f.clave,f.referencia";
		final String hql="from CXPFactura f where f.proveedor=:proveedor and f.saldo>0 order by f.clave,f.referencia";
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setEntity("proveedor",p)
				.list();
			}
			
		});
	}
	
	public List<CXPPago> buscarPagos(){
		return getHibernateTemplate().find("from CXPPago c ");
	}
	
	public List<CXPPago> buscarPagos(final Proveedor proveedor){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(CXPPago.class)
				.add(Restrictions.eq("proveedor",proveedor))
				.list();
				
			}
			
		});
	}
	
	public List<CXPNCargo> buscarCargos(final Proveedor proveedor){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(CXPNCargo.class)
				.add(Restrictions.eq("proveedor",proveedor))
				.list();
				
			}
			
		});
	}
	
	public List<CXPNCredito> buscarAbonos(final Proveedor proveedor){
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(CXPNCredito.class)
				.add(Restrictions.eq("proveedor",proveedor))
				.list();
				
			}
			
		});
	}
	
	/**
	 * Valida que una nota de credito para un proveedor no sea aplicada 
	 * mas de una vez a la misma factura
	 * 
	 * @param proveedor
	 * @param nota
	 */
	public void validarNotaDeCredito(String factura,String proveedor,String folio){
		final String hql="from CXPNCredito c where c.clave=? and c.factura.referencia=? and c.documento=?";
		String[] vals={proveedor,factura,folio};
		List<CXPNCredito> abonos=getHibernateTemplate().find(hql,vals);
		if(abonos.isEmpty())
			return;
		StringBuffer buf=new StringBuffer();
		buf.append(" La nota de credito: "+folio+" ya se a aplicado a la factura: "+factura);
		buf.append(" Abono encontrado: "+abonos.get(0));
		throw new RuntimeException(buf.toString());
		
	}
	
	
	public CXPNCredito buscarNotaDeCreditoPorRequisicion(final RequisicionDetalle r){
		return (CXPNCredito)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<CXPNCredito> l=session.createCriteria(CXPNCredito.class)
				.add(Restrictions.eq("requisicion",r))
				//.add(Restrictions.eq("ncaplicada",new Boolean(true)))
				.list();
				if(l.isEmpty())
					return null;
				return l.get(0);
			}
			
		});
	}
	
	
	
	public CantidadMonetaria buscarMontoDeAbonos(final CXPFactura fac){
		return (CantidadMonetaria)getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from CXP c where c.factura=:factura";
				List<CXP> abonos=session.createQuery(hql).setParameter("factura",fac,Hibernate.entity(CXPFactura.class))
				.list();
				CantidadMonetaria importeDeLosAbonos=new CantidadMonetaria(0.0,fac.getMoneda());
				for(CXP abono:abonos){
					if((abono instanceof CXPNCredito) || (abono instanceof CXPPago))
						importeDeLosAbonos=importeDeLosAbonos.add(abono.getTotal());
				}
				return importeDeLosAbonos;
			}
			
		});
	}
	
	public Analisis buscarAnalisis(final CXPFactura cargo)	{
		return(Analisis)getHibernateTemplate().execute(new HibernateCallback(){			

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="from Analisis a " +				
				"where a.cargo=:cargo";
				Analisis a=(Analisis) session.createQuery(hql)
					.setParameter("cargo",cargo,Hibernate.entity(CXPFactura.class))
					.setMaxResults(1)
					.uniqueResult();
				System.out.println("Localizamos analisis: "+a);
				return a;
			}			
		});
		
	}
	/*
	public static void main(String[] args) {
		CXPDaoImpl dao=(CXPDaoImpl)ServiceLocator.loadDaoContext().getBean(DaosEnum.CXP_DAO.getId());
		//List facs=dao.buscarFacturasSinRequisitar("A001");
		List facs=dao.buscarFacturasPendietnes("A001");
		System.out.println("Facturas: "+facs.size());
	}
*/
}

