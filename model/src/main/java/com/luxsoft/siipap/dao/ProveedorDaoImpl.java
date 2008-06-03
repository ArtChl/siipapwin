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
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.Proveedor;

/**
 * @author Reuben
 */
public class ProveedorDaoImpl extends HibernateDaoSupport 
		implements ProveedorDao{
    

    public void add(Proveedor p) {
        getHibernateTemplate().save(p);

    }
    public void delete(Proveedor p) {
        getHibernateTemplate().delete(p);
    }
    
    public void update(Proveedor p) {
    	//p.setDireccion(null);
        getHibernateTemplate().saveOrUpdate(p);
    }
    
    
    
    public List buscarArticulosNoAsignados(final Proveedor p){        
        /*
        String hq="select articulo " +
		"from Articulo articulo " 
		+"where articulo not in( select ap.articulo " +
				"from ArticuloPorProveedor ap where ap.proveedor=:proveedor)";

        return getHibernateTemplate().findByNamedParam(hq,"proveedor",p,Hibernate.entity(Proveedor.class));
        */
        //Externalizamos el query a Proveedor.hbm.xml
        final String qn="articulosNoAsignados";
        return getHibernateTemplate()
        	.findByNamedQueryAndNamedParam(qn,"proveedor",p);
    }
    
    public Proveedor fetchArticulos(final Proveedor p){
        getHibernateTemplate().execute(new HibernateCallback(){

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.lock(p,LockMode.NONE);
                //Hibernate.initialize(p.getArticulosAsignados());
                getHibernateTemplate().initialize(p);
                return null;
            }
            
        });
        
        return p;
    }    
    
    public Proveedor buscarPorClave(final String clave,boolean artfetched){
        if(!artfetched) 
            return buscarPorClave(clave);
        
        String hq="select p from Proveedor p " +
        		"left  join fetch p.articulosAsignados " +
        		"where p.clave=:clave";
        List l=getHibernateTemplate()
    		.findByNamedParam(hq,"clave",clave);
        if(l.size()>0)
            return (Proveedor)l.get(0);
		return null;
    }
    
    /**
     * Encuentra una y solo una ocurrencia de un proveedor
     * buscando por clave, Si no encuentra o encuentra mas
     * de una regresa nulo
     */
    public Proveedor buscarPorClave(final String clave) {
        String hq="from Proveedor p where p.clave=:clave";
        List l=getHibernateTemplate()
        	.findByNamedParam(hq,"clave",clave);
        if(l.size()==1)
            return (Proveedor)l.get(0);
		return null;
    }
    
	public List buscarArticulosAsignados(Proveedor p) {
		final String hql="select ap.articulo " +
				"from ArticuloPorProveedor ap " +
				"where ap.proveedor=:prov";
		return getHibernateTemplate()
			.findByNamedParam(hql,"prov",p);
	}
	
	
	public int contarRegistros() {
		return ((Integer)getHibernateTemplate()
				.find("select  count(*) from Proveedor")
				.get(0)).intValue();
	}
	
	public List buscarTodos() {
		return getHibernateTemplate().find("from Proveedor p order by p.clave");
	}
	
	public List buscarTodos(boolean articulosFetched){
		if(articulosFetched)
			return getHibernateTemplate().find("from Proveedor p  left join fetch p.articulosAsignados  order by p.clave ");
		return buscarTodos();
	}
	
	public void crearProveedores(final List proveedores) {
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				for(int i=0;i<proveedores.size();i++){
					session.save(proveedores.get(i));
					if(i%20==0){
						session.flush();
						session.clear();
					}
				}
				return null;
			}			
		});
	}
	
	public void inicializarProductos(final Proveedor p){
		getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(p,LockMode.NONE);
				Hibernate.initialize(p.getArticulosAsignados());
				return null;
			}
			
		});
	}
}
