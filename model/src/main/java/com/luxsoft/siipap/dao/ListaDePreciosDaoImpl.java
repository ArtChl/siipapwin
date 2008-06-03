/*
 * Created on 30/03/2005
 *
 * TODO Colocar informacion de licencia
 */
package com.luxsoft.siipap.dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang.Validate;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.ListaDePrecios;
import com.luxsoft.siipap.domain.Precio;
import com.luxsoft.siipap.domain.Proveedor;
import com.luxsoft.siipap.utils.SiipapDateUtils;

/**
 * Implementacion de ListaDePreciosDao para mantenimiento
 * de las listas de precios de los proveedores
 * 
 * @author Ruben Cancino
 */
@SuppressWarnings("unchecked")
public class ListaDePreciosDaoImpl extends HibernateDaoSupport implements
        ListaDePreciosDao {
    
	/**
	 * Salva una lista de precios que existia en Old Siipa Win
	 * es decir con la propiedad de numero ya establecida
	 * @param l
	 * @return
	 */
    public ListaDePrecios salvar(final ListaDePrecios l){
    	Validate.notNull(l.getProveedor(),
		"Proveedor nulo, no se puede genera lista de precios");    	
    	l.setModificado(l.getCreado());
    	getHibernateTemplate().execute(new HibernateCallback(){
    		public Object doInHibernate(Session session) throws HibernateException, SQLException {
    			session.save(l);
    			return null;
    		}	
    	});
    	return l;
    }
    
    /**
     * Crea una lista de precios totalmente nueva, el campo 
     * de numero es generado automaticamente
     * @param l
     * @return
     */
    public ListaDePrecios crear(final ListaDePrecios l) {
    	Validate.notNull(l.getProveedor(),
    			"Proveedor nulo, no se puede genera lista de precios");
        l.setCreado(new Date());
        l.setModificado(l.getCreado());
        getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="select count(*) from ListaDePrecios lp " +
						" where lp.proveedor=:proveedor";
				long n=((Integer)session.createQuery(hql)
						.setEntity("proveedor",l.getProveedor())
						.list().get(0)).longValue();
				l.setNumero(n+1l);
				session.save(l);
				return null;
			}
        	
        });
        return l;
    }
    
    public ListaDePrecios modificar(ListaDePrecios l) {
        l.setModificado(new Date());
        getHibernateTemplate().update(l);
        return l;
    }
    
    public void borrar(ListaDePrecios l) {
        getHibernateTemplate().delete(l);
    }
    
    public ListaDePrecios buscarPorId(Long id) {
        Object o=getHibernateTemplate().get(ListaDePrecios.class,id);
        return (ListaDePrecios)o;
    }
    
	public ListaDePrecios buscarPorNumero(final long numero,final Proveedor p) {		
		Object o=getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(ListaDePrecios.class)
						.add(Expression.eq("numero",new Long(numero)))
						.add(Expression.eq("proveedor",p))
						.uniqueResult();
			}			
		});
		if(o!=null)return (ListaDePrecios)o;
		return null;
	}
	
	public ListaDePrecios buscarPorNumeroFetched(final long numero,
			final Proveedor p) {		
		Object o=getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(ListaDePrecios.class)
						.setFetchMode("precios",FetchMode.JOIN)
						.add(Expression.eq("numero",new Long(numero)))
						.add(Expression.eq("proveedor",p))
						.uniqueResult();
			}
			
		});
		if(o!=null)return (ListaDePrecios)o;
		return null;
	}
	
    public void inicializarPrecios(final ListaDePrecios lp){
        if(Hibernate.isInitialized(lp.getPrecios())){            
            return;
        }        
        getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                System.out.println("TRATANDO Inicializando");
                session.lock(lp,LockMode.NONE);
                getHibernateTemplate().initialize(lp.getPrecios());
                return null;
            }            
        });
    }
    
    
    public List buscarListas(Proveedor p) {
        String hq="from ListaDePrecios lp where lp.proveedor=:prov";
        return getHibernateTemplate().findByNamedParam(hq,"prov",p);
    }
    
    
	public List buscarPrecios(ListaDePrecios lp) {
		final String hql="from Precio p " +
				"where p.lista=:lista";
		return getHibernateTemplate().findByNamedParam(hql,"lista",lp);
	}
	
	public List buscarTodasLasListas() {
		final String hql="from ListaDePrecios lp join fetch lp.proveedor";
		return getHibernateTemplate().find(hql);
	}
	
	public List buscarTodasLasListas(final Date desde){
		final String hql="from ListaDePrecios lp join fetch lp.proveedor where lp.periodo.fechaFinal>?";
		return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(hql)
					.setParameter(0,desde,Hibernate.DATE)
					.list();
			}			
		});
	}

	public List<ListaDePrecios> buscarListaVigente(Proveedor p) {
        return buscarListaVigente(p,Calendar.getInstance().getTime());
    }
	
	/**
     * Busca las listas de precios vigentes para el proveedor determinado en la fecha estipulada
     * @param p
     * @param fecha
     * @return
     */    
	public List<ListaDePrecios> buscarListaVigente(final Proveedor p,final Date fecha){
    	List<ListaDePrecios> list=getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final String hql=" select lp from ListaDePrecios lp " +
    			"left join fetch lp.precios p " +
    			"where :fecha between lp.periodo.fechaInicial and lp.periodo.fechaFinal" +
    			"  and lp.proveedor=:proveedor " +
    			"order by lp.periodo.fechaInicial desc ";
				return session.createQuery(hql)
					.setParameter("proveedor",p,Hibernate.entity(Proveedor.class))
					.setParameter("fecha",fecha,Hibernate.DATE)
					.list();
			}    		
    	});
    	return SetUniqueList.decorate(list);
    }
	
    public ListaDePrecios clonar(ListaDePrecios l) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public CantidadMonetaria buscarPrecio(final String proveedor ,final String  artclave,final Date fecha){
    	CantidadMonetaria precio=null;
    	final String hql=" select p.neto from ListaDePrecios lp " +
    			"left join  lp.precios p " +
    			"where :fecha between lp.periodo.fechaInicial and lp.periodo.fechaFinal" +
    			"  and lp.proveedor.clave=:proveedor " +
    			"  and p.articuloProveedor.articulo.clave=:articulo " +
    			"  and p.neto.currency=:currency " +
    			"order by lp.id ";    			
    	List list=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List ll=session.createQuery(hql)
							.setParameter("fecha",fecha,Hibernate.DATE)
							.setString("proveedor",proveedor)
							.setString("articulo",artclave)
							.setParameter("currency",Currency.getInstance(new Locale("es","mx")),Hibernate.CURRENCY)
							.list();							
						return ll;				
			}
    		
    	});
    	if(list.size()>0){
    		precio=(CantidadMonetaria)list.get(0);
    	}
    	return precio;
    }
    
    public CantidadMonetaria buscarPrecio(final String proveedor ,final String  artclave,final Date fecha,final Currency moneda){
    	CantidadMonetaria precio=null;
    	final String hql=" select p.neto from ListaDePrecios lp " +
    			"left join  lp.precios p " +
    			"where :fecha between lp.periodo.fechaInicial and lp.periodo.fechaFinal" +
    			"  and lp.proveedor.clave=:proveedor " +
    			"  and p.articuloProveedor.articulo.clave=:articulo " +
    			"  and p.neto.currency=:currency " +
    			"order by lp.id ";    			
    	List list=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List ll=session.createQuery(hql)
							.setParameter("fecha",fecha,Hibernate.DATE)
							.setString("proveedor",proveedor)
							.setString("articulo",artclave)
							.setParameter("currency",moneda,Hibernate.CURRENCY)
							.list();							
						return ll;				
			}
    		
    	});
    	if(list.size()>0){
    		precio=(CantidadMonetaria)list.get(0);
    	}
    	return precio;
    }
    
    public Precio precio(final String proveedor ,final String  artclave,final Date fecha,final Currency moneda){
    	final String hql=" select p from ListaDePrecios lp " +
		"left join  lp.precios p " +
		"where :fecha between lp.periodo.fechaInicial and lp.periodo.fechaFinal" +
		"  and lp.proveedor.clave=:proveedor " +
		"  and p.articuloProveedor.articulo.clave=:articulo " +
		"  and p.neto.currency=:currency " +
		"order by lp.id ";    			
    	List<Precio> list=getHibernateTemplate().executeFind(new HibernateCallback(){

    		public Object doInHibernate(Session session) throws HibernateException, SQLException {
    			List ll=session.createQuery(hql)
					.setParameter("fecha",fecha,Hibernate.DATE)
					.setString("proveedor",proveedor)
					.setString("articulo",artclave)
					.setParameter("currency",moneda,Hibernate.CURRENCY)
					.list();							
				return ll;				
    		}
	
    	});
    	return list.isEmpty()?null:list.get(0);
    }
    
    public CantidadMonetaria buscarPrecio(final String  artclave,final Date fecha){
    	CantidadMonetaria precio=null;
    	final String hql=" select p.neto from ListaDePrecios lp " +
    			"left join  lp.precios p " +
    			"where :fecha between lp.periodo.fechaInicial and lp.periodo.fechaFinal" +    			
    			"  and p.articuloProveedor.articulo.clave=:articulo " +
    			"  and p.neto.currency=:currency " +
    			"order by lp.id ";    			
    	List list=getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List ll=session.createQuery(hql)
							.setParameter("fecha",fecha,Hibernate.DATE)
							.setString("articulo",artclave)
							.setParameter("currency",Currency.getInstance(new Locale("es","mx")),Hibernate.CURRENCY)
							.list();							
						return ll;				
			}
    		
    	});
    	if(list.size()>0){
    		Iterator iter=list.iterator();    		
    		while(iter.hasNext()){
    			CantidadMonetaria val=(CantidadMonetaria)iter.next();
    			if(precio==null){
    				precio=val;
    				continue;
    			}
    			if(val.compareTo(precio)<0)
    				precio=val;
    			
    		}
    		
    	}
    	return precio;
    }
    
    @SuppressWarnings("unchecked")
	public Precio precioMN(final String proveedor ,final String  artclave,final Date fecha){
    	Precio precio=null;
    	final String hql=" select p from ListaDePrecios lp " +
		"left join  lp.precios p " +
		"where :fecha between lp.periodo.fechaInicial and lp.periodo.fechaFinal" +
		"  and lp.proveedor.clave=:proveedor " +
		"  and p.articuloProveedor.articulo.clave=:articulo " +
		"  and p.neto.currency=:currency " +
		"order by lp.id ";    			
    	List<Precio> list=getHibernateTemplate().executeFind(new HibernateCallback(){
    		public Object doInHibernate(Session session) throws HibernateException {
    				List ll=session.createQuery(hql)
					.setParameter("fecha",fecha,Hibernate.DATE)
					.setString("proveedor",proveedor)
					.setString("articulo",artclave)
					.setParameter("currency",Currency.getInstance(new Locale("es","mx")),Hibernate.CURRENCY)
					.list();							
    				return ll;				
    		}
	
    	});
    	if(list.size()>0){
    		precio=list.get(0);
    	}
    	return precio;
    }
    
    
    @SuppressWarnings("unchecked")
	public Precio precioMN(final String  artclave,final Date fecha){
    	Precio precio=null;
    	final String hql=" select p from ListaDePrecios lp " +
		"left join  lp.precios p " +
		"where :fecha between lp.periodo.fechaInicial and lp.periodo.fechaFinal" +    			
		"  and p.articuloProveedor.articulo.clave=:articulo " +
		"  and p.neto.currency=:currency " +
		"order by lp.id ";    			
    	List<Precio> list=getHibernateTemplate().executeFind(new HibernateCallback(){
    		public Object doInHibernate(Session session) throws HibernateException {
    			List ll=session.createQuery(hql)
					.setParameter("fecha",fecha,Hibernate.DATE)
					.setString("articulo",artclave)
					.setParameter("currency",Currency.getInstance(new Locale("es","mx")),Hibernate.CURRENCY)
					.list();							
				return ll;				
    		}	
		});
    	if(list.size()>0){
    		for(Precio p:list){
    			CantidadMonetaria val=p.getNeto();
    			if(precio==null){
    				precio=p;
    				continue;
    			}
    			if(val.compareTo(precio.getNeto())<0)
    				precio=p;
    		}
    	}
    	return precio;
    }
    
    /**
     * Busca algun precio para el articulo especificado 
     * 
     * @param articulo
     * @return
     */
    public Precio precioMN(final String articulo){
    	Object found=getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session sess) throws HibernateException, SQLException {
				List res=sess.createCriteria(Precio.class)					
					.createAlias("articuloProveedor","ap")
					.createAlias("ap.articulo","a")
					.add(Restrictions.eq("a.clave",articulo))
					.setMaxResults(1)
					.list();
				if(res.size()>0)
					return res.get(0);
				return null;
			}    		
    	});
    	return (Precio)found;
    }
    
    /**
     * Busca los precios vigentes
     *  
     */
    @SuppressWarnings("unchecked")
	public List<Precio> buscarPreciosVigentes(final Date fecha){
    	return getHibernateTemplate().executeFind(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Precio.class)
					.createAlias("articuloProveedor","ap")
					.createAlias("lista","l")
					.setFetchMode("articuloProveedor",FetchMode.JOIN)
					.add(Restrictions.gt("l.periodo.fechaFinal",fecha))
					.list();
			}    		
    	});
    }
    
    /**
	 * Busca todos los precios para el proveedor y articulo seleccionado
	 * 
	 * @param proveedor
	 * @param articulo
	 * @return
	 */
	public List<Precio> buscarPrecios(final String proveedor,final String articulo){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Precio.class)
					.createAlias("articuloProveedor","ap")
					.createAlias("ap.articulo","a")
					.createAlias("ap.proveedor","p")
					.createAlias("lista","l")
					.setFetchMode("articuloProveedor",FetchMode.JOIN)
					.setFetchMode("l",FetchMode.JOIN)
					.add(Restrictions.eq("a.clave",articulo))
					.add(Restrictions.eq("p.clave",proveedor))
					.list();
			}			
		});
	}
    
    /**
	 * Usado en los filtros de browser
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Precio> buscarPorFiltro(final Object o){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Example ej=Example.create(o);
				ej.enableLike();
				return session.createCriteria(Precio.class)
				.createAlias("articuloProveedor","ap")
				.createAlias("lista","l")
				.setFetchMode("articuloProveedor",FetchMode.JOIN)
				.add(ej)
				.list();
			}			
		});
	}
	
	/**
	 * Buscar todos los precios del articulo 
	 * @param articulo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Precio> buscarPrecios(final String articulo){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createCriteria(Precio.class)
				.createAlias("articuloProveedor","ap")
				.createAlias("ap.articulo","a")
				.createAlias("lista","l")
				.setFetchMode("articuloProveedor",FetchMode.JOIN)				
				.add(Restrictions.like("a.clave",articulo,MatchMode.END))
				.list();
			}			
		});
	}
	
	
	/**
	 * Busca las listas de precios de un  proveedor en forma adecuada para un browser
	 * 
	 * @param proveedor
	 * @return
	 */
	public List<Object[]> buscarListasDePrecios(final Proveedor proveedor){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="select a.clave,a.descripcion1,p.precio,p.neto,lp.periodo,p.descuento1,p.descuento2,p.descuento3,p.descuento4, " +
						" ap.codigoDelProveedor,ap.descripcionDelProveedor "+
						"  from Precio p " +
						" join fetch p.lista lp " +
						" join fetch p.articuloProveedor ap"+
						" join fetch ap.articulo a" +
						" where lp.proveedor=:proveedor ";
				return session.createQuery(hql)
					.setParameter("proveedor",proveedor,Hibernate.entity(Proveedor.class))
					.list();
			}			
		});
	}
	
	/**
	 * Inicializa el Precio indicado
	 * @param p
	 */
	public void inicializarPrecio(final Precio p){
		if(p==null)return;
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.lock(p,LockMode.NONE);
				Hibernate.initialize(p.getLista());
				Hibernate.initialize(p.getArticuloProveedor());
				Hibernate.initialize(p.getArticuloProveedor().getArticulo());
				Hibernate.initialize(p.getArticuloProveedor().getProveedor());
				return null;
			}			
		});
	}
    
	public List<Object[]> browse(){
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hql="select l.id,l.proveedor.clave,l.periodo,l.descripcion from ListaDePrecios l" +
						" where l.periodo.fechaInicial>? order by l.proveedor.clave,l.id";
				return session
				.createQuery(hql)					
				.setParameter(0,SiipapDateUtils.getMXDate("31/12/05"))
				.list();
			}			
		});
	}
}
