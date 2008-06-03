package com.luxsoft.siipap.maquila.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxp.domain.AnalisisDeEntrada;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;

@SuppressWarnings("unchecked")
public class SalidaDeMaterialDaoImpl extends HibernateDaoSupport implements SalidaDeMaterialDao{

	
	public void salvar(final SalidaDeMaterial s) {	
		BigDecimal kilos=s.getKilos().doubleValue()>0?s.getKilos().multiply(BigDecimal.valueOf(-1.0)):s.getKilos();
		BigDecimal m2=s.getMetros2().doubleValue()>0?s.getMetros2().multiply(BigDecimal.valueOf(-1.0)):s.getMetros2();
		
		//Validamos los kilos si se trata de una salida de bobinas y actualizamos lo analizado en CXP
		if(s instanceof SalidaDeBobinas){
			SalidaDeBobinas sb=(SalidaDeBobinas)s;
			BigDecimal porAnalizar=sb.getDestino().getPorAnalizar();
			Assert.isTrue(porAnalizar.doubleValue()>=kilos.abs().doubleValue(),"No se puede generar una salida de " +
					"bobina por mas cantidad que la originalmente contenida en el com\n Por analizar (COM): "+porAnalizar.abs()+
					" \nKilos en la salida: "+kilos.abs());
			sb.getDestino().setAnalizada(sb.getKilos().abs());
		}		
		s.setMetros2(m2);
		s.setKilos(kilos);		
		CantidadMonetaria importe=new CantidadMonetaria(s.getKilos().abs().doubleValue()*s.getPrecioPorKilo(),s.getImporte().getCurrency());
		s.setImporte(importe);		
		getHibernateTemplate().saveOrUpdate(s);
	}

	public void eliminar(final SalidaDeMaterial s) {
		if(s instanceof SalidaDeBobinas){
			SalidaDeBobinas ss=(SalidaDeBobinas)s;
			BigDecimal cantidad=ss.getKilos().abs();
			BigDecimal analizados=ss.getDestino().getAnalizada();
			AnalisisDeEntrada ae=ss.getDestino();
			ae.setAnalizada(analizados.subtract(cantidad));
			getHibernateTemplate().update(ss.getDestino());
		}else{
			getHibernateTemplate().delete(s);
		}
	}

	public void actualizar(final SalidaDeMaterial s) {		
		salvar(s);
	}

	public SalidaDeMaterial get(final Long id) {
		return (SalidaDeMaterial)getHibernateTemplate().get(SalidaDeMaterial.class,id);
	}
	
	
	public List<AnalisisDeEntrada> buscarEntradasPorAsignar(){
		String hql="from AnalisisDeEntrada e where e.unidad=? and e.porAnalizar>0";
		return getHibernateTemplate().find(hql,"KGS");
		
	}
	
	public List<AnalisisDeEntrada> buscarEntradasPorAsignar(final String articulo){
		String hql="from AnalisisDeEntrada e where e.unidad=? and e.porAnalizar>0 and e.clave=?";
		Object[] vals={"KGS",articulo};
		return getHibernateTemplate().find(hql,vals);
	}
	
	public List<SalidaDeBobinas> buscarSalidasDeBobinas(){
		String hql="from SalidaDeBobinas s " +
				" left join fetch s.entrada e" +
				" left join fetch s.destino d" +
				" left join fetch s.entrada.articulo ";
		return getHibernateTemplate().find(hql);
	}
	
	public List<AnalisisDeEntrada> buscarDisponiblesKilos(final String proveedor){
		String hql="from AnalisisDeEntrada a join fetch a.com c where a.PROVCLAVE=:proveedor and (a.ingresada-a.analizadoHojas)>0 order by a.FENT desc";
		return getHibernateTemplate().findByNamedParam(hql,"proveedor",proveedor);
		
	}
	
	public List<AnalisisDeEntrada> buscarDisponiblesKilos(final String proveedor,final String articulo){		
		String hql="from AnalisisDeEntrada a " +
				" join fetch a.com c " +
				" where a.PROVCLAVE=:proveedor " +
				"   and a.clave=:clave " +
				"   and (a.ingresada-a.analizadoBobinas)>0 " +
				" order by a.FENT desc";
		String[] names={"proveedor","clave"};
		Object[] vals={proveedor,articulo};
		return getHibernateTemplate().findByNamedParam(hql,names,vals);
	}

	public List<SalidaACorte> buscarSalidas() {
		return getHibernateTemplate().find("from SalidaACorte s " +
				" left join fetch s.articulo a" +
				" left join fetch s.entrada e" +
				" left join fetch s.orden o");
	}

	public List<SalidaDeMaterial> buscarSalidasDeMaterial() {		
		String hql="from SalidaDeMaterial s " +
				" left join fetch s.entrada e" +				
				" left join fetch s.entrada.articulo ";
		return getHibernateTemplate().find(hql);
		
	}
	
	
	

}
