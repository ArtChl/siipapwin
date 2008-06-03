package com.luxsoft.siipap.maquila.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.MaterialHojeado;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;

@SuppressWarnings("unchecked")
public class HojeadoDaoImpl extends HibernateDaoSupport implements HojeadoDao{

	public void create(final MaterialHojeado mat) {
		if(mat instanceof SalidaDeHojas){
			SalidaDeHojas s=(SalidaDeHojas)mat;
			Assert.isTrue(s.getCantidad().doubleValue()<=s.getOrigen().getDisponible().doubleValue(),
					"La cantidad no puede se mayor a lo disponible en la entrada de hojas origen");
			
			//Ajustando lo analizado
			AnalisisDeEntrada ae=s.getDestino();
			ae.setAnalizada(ae.getAnalizada().subtract(s.getCantidad()));
			getHibernateTemplate().saveOrUpdate(s);			
		}else if(mat instanceof EntradaDeHojas ){
			
			//Garantizar que los kilos y m2 de la SalidaACorte  origen de la entrada de hojas sea negativa
			EntradaDeHojas ee=(EntradaDeHojas)mat;
			ee.getOrigen().setKilos(ee.getOrigen().getKilos().multiply(BigDecimal.valueOf(-1)));
			ee.getOrigen().setMetros2(ee.getOrigen().getMetros2().multiply(BigDecimal.valueOf(-1)));
			
			//Actualizando el area del articulo
			//TODO: Temporal			
			getHibernateTemplate().saveOrUpdate(mat.getArticulo());
			getHibernateTemplate().saveOrUpdate(mat);
		}		
	}

	public void delete(final MaterialHojeado mat) {
		getHibernateTemplate().delete(mat);
	}

	public void update(MaterialHojeado mat) {
		if(mat instanceof EntradaDeHojas){
//			Garantizar que los kilos y m2 de la SalidaACorte  origen de la entrada de hojas sea negativa
			EntradaDeHojas ee=(EntradaDeHojas)mat;
			ee.getOrigen().setKilos(ee.getOrigen().getKilos().multiply(BigDecimal.valueOf(-1)));
			ee.getOrigen().setMetros2(ee.getOrigen().getMetros2().multiply(BigDecimal.valueOf(-1)));
			
			//Actualizando el area del articulo
			//TODO: Temporal			
			getHibernateTemplate().saveOrUpdate(mat.getArticulo());
			getHibernateTemplate().saveOrUpdate(mat);
		}
	}

	public MaterialHojeado buscarPorId(Long id) {
		return (MaterialHojeado)getHibernateTemplate().get(MaterialHojeado.class,id);
	}
	
	
	public List<EntradaDeHojas> buscarEntradas(){
		final String hql="from EntradaDeHojas e " +
				" left join fetch e.origen s" +
				" left join fetch e.articulo a " +
				" left join fetch e.origen.articulo oa" +
				" left join fetch e.origen.orden o" +
				" left join fetch e.origen.orden.almacen oa" +
				" left join fetch e.origen.orden.almacen.maquilador omm" +
				" left join fetch e.origen.entrada oe";
		return getHibernateTemplate().find(hql);
	}

	public List<EntradaDeHojas> buscarDisponibles() {
		final String hql="from EntradaDeHojas e " +
		" left join fetch e.origen s" +
		" left join fetch e.articulo a " +
		" left join fetch e.origen.articulo oa" +
		" left join fetch e.origen.orden o " +
		" left join fetch e.origen.orden.almacen oa" +
		" left join fetch e.origen.orden.almacen.maquilador omm" +
		" left join fetch e.origen.entrada oe " +
		" where e.disponible>0" ;
		
		return getHibernateTemplate().find(hql);
	}
	
	public void recalcularCostoDeCom(final SalidaDeHojas s){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(s,LockMode.NONE);
				
				CantidadMonetaria cm=s.getOrigen().getOrigen().getCostoDefinitivo();
				CantidadMonetaria ch=s.getOrigen().getCosto();
				CantidadMonetaria cf=s.getCosto();
				CantidadMonetaria gastos=s.getGastos();
				CantidadMonetaria costo=cm.add(ch).add(cf).add(gastos);
				s.getDestino().getCom().setCosto(costo);
				session.update(s.getDestino().getCom());
				session.flush();
				return null;
			}
			
		});
	}

	/**
	 * Regresa una lista con todas las salidas de hojas existentes
	 *  
	 * 
	 * @return
	 */
	public List<SalidaDeHojas> buscarSalidas() {
		final String hql="from SalidaDeHojas s" +
				" left join fetch s.origen o" +
				" left join fetch s.destino d";
		return getHibernateTemplate().find(hql);
	}
	
	public void inicializarSalida(final SalidaDeHojas sal){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(sal.getOrigen().getOrigen(),LockMode.NONE);
				session.lock(sal.getOrigen().getOrigen().getEntrada(),LockMode.NONE);
				session.lock(sal.getOrigen().getOrigen().getOrden().getAlmacen(),LockMode.NONE);
				Hibernate.initialize(sal.getOrigen().getOrigen());
				Hibernate.initialize(sal.getOrigen().getOrigen().getEntrada());
				Hibernate.initialize(sal.getOrigen().getOrigen().getOrden().getAlmacen());
				
				
				
				return null;
			}
			
		});
	}
	
	
	public List<AnalisisDeEntrada> buscarComs(final String proveedor){
		String hql="from AnalisisDeEntrada a " +
				" join fetch a.com c " +
				" where a.PROVCLAVE=:proveedor " +
				"   and (a.ingresada-a.analizadoHojas)>0 " +
				" order by a.FENT desc";
		return getHibernateTemplate().findByNamedParam(hql,"proveedor",proveedor);
	}
	
	public List<AnalisisDeEntrada> buscarComs(final String proveedor,final String articulo){
		String hql="from AnalisisDeEntrada a " +
		" join fetch a.com c " +
		" where a.PROVCLAVE=:proveedor " +
		"   and a.clave=:clave " +
		"   and (a.ingresada-a.analizadoHojas)>0 " +
		" order by a.FENT desc";
		String[] names={"proveedor","clave"};
		Object[] vals={proveedor,articulo};
		return getHibernateTemplate().findByNamedParam(hql,names,vals);		
	}

}
