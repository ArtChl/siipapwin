/*
 * Created on 17-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.dao;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.ArticuloRow;
import com.luxsoft.siipap.domain.Familia;
import com.luxsoft.siipap.domain.UnidadesPorArticulo;
import com.luxsoft.siipap.utils.ArticuloEstadoUserType;

/**
 * @author Ruben Cancino
 *
* TODO Documentar
 */

@SuppressWarnings("unchecked")
public class ArticuloDaoImpl extends GenericHibernateDao<Articulo,Long> 
		implements ArticuloDao{

    
     
    public ArticuloDaoImpl() {
		super(Articulo.class);
	}
    
    
	public Articulo buscarPorClave(final String clave) {
    	List<Articulo> found=findByCriteria(Expression.eq("clave",clave));
    	if(found.size()==1)
    		return found.get(0);
    	return null;
    }
	
	public Articulo buscarPorClaveConUnidades(final String name){
		String hql="select a from Articulo a " +
				"left join fetch a.unidades u " +
				"left join fetch a.unidad uu " +
				"where a.clave=?";
		List found=getHibernateTemplate().find(hql,name);
		System.out.println(found.size());
		return (Articulo)getHibernateTemplate().find(hql,name).get(0);
	}
	
	public Articulo buscarPorClaveConFamilias(final String name){
		String hql="select a from Articulo a " +
		"left join fetch a.familia  " +
		"left join fetch a.unidad  " +
		"where a.clave=?";		
		return (Articulo)getHibernateTemplate().find(hql,name).get(0);
	}
    
    @SuppressWarnings("unchecked")
	public List<Articulo> buscarTodos() {
        return getHibernateTemplate().find("from Articulo a order by a.clave");
    }
    
	@SuppressWarnings("unchecked")
	public List<Articulo> buscarPorFamilia(final Familia familia) {
		final String hql="from Articulo a left join fetch a.familia f" +
				" where a.familia=:familia";
		return getHibernateTemplate()
		.findByNamedParam(hql,"familia",familia);
	}
	
	
	
	public void agregarArticulos(List<Articulo> articulos){
		getHibernateTemplate().saveOrUpdateAll(articulos);
	}
	
	public int contarRegistros() {
		return ((Integer)getHibernateTemplate()
			.find("select  count(*) from Articulo")
			.get(0)).intValue();
	}
	
	public void salvarUnidadesPorArticulo(final List<UnidadesPorArticulo> unidades){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException {
				int i=0;
				for(UnidadesPorArticulo ua:unidades){
					session.save(ua);
					i++;
					if(i%20==0){
						session.flush();
						session.clear();
					}
				}
				return null;
			}
		});
	}
	
	
	public int contarUnidadsPorArticulo() {
		return ((Integer)getHibernateTemplate()
				.find("select  count(*) from UnidadesPorArticulo")
				.get(0)).intValue();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<UnidadesPorArticulo> buscarTodasLasUnidadesPorArticulo() {
		return getHibernateTemplate().find("from UnidadesPorArticulo");
	}

	@SuppressWarnings("unchecked")
	public List<Articulo> buscarTodosConFamilia() {
		final String hql="select a from Articulo a left join fetch a.familia";
		return getHibernateTemplate().find(hql);
	} 
	
	@SuppressWarnings("unchecked")
	public List<ArticuloRow> browse(){
		/*
		final String hql=" select new com.luxsoft.siipap.domain.ArticuloRow(" +
				"a.ancho,a.calibre,a.clave,a.descripcion1,f.descripcion||\'  \'||trim(f.clave),a.gramos,a.kilos,a.largo,u.clave,a.area" +
				") from Articulo a " +
				" join fetch a.familia f " +
				" join fetch a.unidad u";
				*/
		
		final String hql=" select new com.luxsoft.siipap.domain.ArticuloRow(" +
		"a.clave,a.descripcion1,a.kilos,a.gramos,a.familia.clave,a.familia.descripcion" +
		") from Articulo a ";// +
		//" join fetch a.familia f ";
		
		
		/*		
		final String hql=" select a.ancho,a.calibre,a.clave,a.descripcion1,f.clave,a.gramos,a.kilos,a.largo,u.clave" +
		" from Articulo a " +
		"join fetch a.familia f " +
		"join fetch a.unidad u";
		*/
		return getHibernateTemplate().find(hql);
	}
	
	
	public List<Articulo> buscarRango(final Articulo a1,final Articulo a2){
		String hql="from Articulo a where a.clave between ? and ?";
		Object[] vals={a1.getClave(),a2.getClave()};
		return getHibernateTemplate().find(hql,vals);
	}


	public List<Articulo> buscarTodosLosActivos() {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {				
				return session.createQuery("from Articulo a where a.estado=:activo")
				.setParameter("activo", Articulo.Estado.getEstado("A"),Hibernate.custom(ArticuloEstadoUserType.class)).list();
			}			
		});
	}
	
	
	
	
	
}

