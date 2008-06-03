package com.luxsoft.siipap.cxc.services;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.services.ServiceLocator;

/**
 * Procedimiento para actualizar los descuentos por articulo
 * en funcion de cambios en la lista de precios. Es decir cambios
 * en el catalogo de articulos
 * 
 * @author Ruben Cancinio
 *
 */
public class ActualizarPreciosPorArticulo extends HibernateDaoSupport{
	
	
	public void actualizar(final long id ){
		getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				DescuentoPorArticulo desc=(DescuentoPorArticulo)session.get(DescuentoPorArticulo.class, id);
				if(desc.isPorPrecioKilo())
					desc.calcularDescuentoEnFuncionDePrecioKilo();
				return null;
			}
			
		});
	}
	
	
	public static void main(String[] args) {
		Long id=new Long(0);
		ActualizarPreciosPorArticulo a=new ActualizarPreciosPorArticulo();
		a.setSessionFactory(ServiceLocator.getSessionFactory());
		a.actualizar(id);
	}

}
