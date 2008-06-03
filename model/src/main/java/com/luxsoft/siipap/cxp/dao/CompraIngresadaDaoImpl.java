package com.luxsoft.siipap.cxp.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxp.domain.CompraIngresada;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Entrada;


@SuppressWarnings("unchecked")
public class CompraIngresadaDaoImpl extends HibernateDaoSupport implements CompraIngresadaDao{
	
	
	public List<Entrada> buscarComs(final Periodo p){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Entrada.class)
					.add(Restrictions.eq("ALMTIPO","COM"))
					.add(Restrictions.between("ALMFECHA",p.getFechaInicial(),p.getFechaFinal()))
					.list();
			}			
		});
	}
	
	public CompraIngresada generarCom(final Entrada com){		
		CompraIngresada c=new CompraIngresada();
		c.setCom(com);
		c.setCantidadIngresada(com.getALMCANTI());
		c.setModificado(c.getCreado());
		getHibernateTemplate().saveOrUpdate(c);
		return c;
	}
	
	

	public int generarIngresos(final Periodo p) {
		List<Entrada> entradas=buscarComs(p);
		for(Entrada e:entradas){
			generarCom(e);
		}
		return entradas.size();		
	}

	public void actualizarCompraIngresada(final CompraIngresada com) {
		if(com.getAnalisis()!=null){
			com.getCom().setCosto(com.getAnalisis().getNetoMN());
		}
		getHibernateTemplate().saveOrUpdate(com);
		
	}
	
	public List<CompraIngresada> buscarComsSinAnalizar(final String proveedor){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(CompraIngresada.class)
					.createAlias("com","c")
					.createAlias("com.mvcomp","mc")
					.setFetchMode("c.mvcomp",FetchMode.JOIN)
					.add(Restrictions.eq("mc.MVCPROVEE",proveedor))
					.add(Restrictions.isNull("analisis"))
					.list();
			}			
		});
	}
	
	public CompraIngresada localizarCompra(final Integer sucursal,final Long numero,final String clave,final Integer renglon){
		List<CompraIngresada> list=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final String hql=" select c from CompraIngresada c" +
						"  join fetch c.com ce" +
						"  join fetch c.com.mvcomp mv "+
						"  join fetch c.dec cd" +
						" where ce.ALMARTIC=:articulo" +
						"  and ce.ALMSUCUR=:almsucur" +
						"  and ce.ALMNUMER=:numero" +
						"  and ce.ALMTIPO=:tipo " +
						"  and ce.ALMRENGL=:rengl";
				return session.createQuery(hql)
					.setString("articulo",clave)
					.setInteger("almsucur",sucursal)
					.setLong("numero",numero)
					.setInteger("rengl",renglon)
					.setString("tipo","COM")
					.list();
					
			}			
		});
		if(list.isEmpty())
			return null;
		return list.get(0);
	}
	
	public CompraIngresada localizarCompraIngresada(final Integer sucursal,final String proveedor,final Long facrem,final String articulo){
		List<CompraIngresada> list=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final String hql=" select c from CompraIngresada c" +
						"  join fetch c.com ce" +
						"  join fetch c.com.mvcomp mv "+
						"  join fetch c.dec cd" +
						" where ce.ALMARTIC=:articulo" +
						"  and ce.ALMSUCUR=:almsucur" +
						"  and mv.MVCPROVEE=:proveedor" +
						"  and mv.MVCFACREM=:facrem";
				return session.createQuery(hql)
					.setParameter("sucursal",sucursal)
					.setParameter("proveedor",proveedor)
					.setParameter("facrem",facrem)
					.setParameter("articulo",articulo)
					.list();
			}			
		});
		return list.isEmpty()?null:list.get(0);
	}

	@SuppressWarnings("unchecked")
	public Entrada localizarEntrada(final Integer sucursal,final String proveedor,final Long facrem,final String articulo) {
		final String hql="select e from Entrada e " +
		"where e.ALMARTIC=:articulo" +
		"  and e.ALMSUCUR=:sucursal" +
		"  and e.mvcomp.MVCFACREM=:facrem" +
		"  and e.mvcomp.MVCPROVEE=:proveedor";
		List<Entrada> beans=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
				.setParameter("articulo",articulo)
				.setParameter("sucursal",sucursal)
				.setParameter("facrem",facrem)					
				.setParameter("proveedor",proveedor)
				.list();
			}			
		});
		if(beans.isEmpty()){
			logger.warn("No encontro Entrada para la remision: "+facrem +" de sucursal: "+sucursal+" Proveedor:"+proveedor);
			return null;
		}else if(beans.size()>1){
			logger.warn("Encontro mas de una  Entrada para la remision: "+facrem +" de sucursal: "+sucursal+" Proveedor:"+proveedor);
			return beans.get(0);
		}else
			return beans.get(0);
	}
	
		
	
	

}
