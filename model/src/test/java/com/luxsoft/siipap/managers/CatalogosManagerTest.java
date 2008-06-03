package com.luxsoft.siipap.managers;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.luxsoft.siipap.cxc.dao.ClienteDao;


/**
 * 
 * @author Ruben Cancino
 *
 */
public class CatalogosManagerTest extends MockObjectTestCase{
	
	protected CatalogosManager_old manager;
	
	public void setUp(){
		manager=new CatalogosManager_old();
	}
	
	public void testBuscarClientesPorClave(){
		final String CLAVE="U050008";
		Mock mockDao=mock(ClienteDao.class);
		mockDao.expects(once()).method("buscarClientesPorClave").with(this.eq(CLAVE));
		manager.setClienteDao((ClienteDao)mockDao.proxy());		
		manager.buscarClientesPorClave(CLAVE);
	}
	
	public void testBuscarClientesPorNombre(){
		final String NOMBRE="U050008";
		Mock mockDao=mock(ClienteDao.class);
		mockDao.expects(once()).method("buscarPorNombre").with(this.eq(NOMBRE));
		manager.setClienteDao((ClienteDao)mockDao.proxy());		
		manager.buscarClientesPorNombre(NOMBRE);
	}
	
	public void testBuscarClientePorClave(){
		final String CLAVE="U050008";
		Mock mockDao=mock(ClienteDao.class);
		mockDao.expects(once()).method("buscarPorClave").with(this.eq(CLAVE));
		manager.setClienteDao((ClienteDao)mockDao.proxy());		
		manager.buscarClientePorClave(CLAVE);
	}
	
	public void testBuscarClientePorNombre(){
		final String NOMBRE="U050008";
		Mock mockDao=mock(ClienteDao.class);
		mockDao.expects(once()).method("buscarPorNombre").with(this.eq(NOMBRE));
		manager.setClienteDao((ClienteDao)mockDao.proxy());		
		manager.buscarClientesPorNombre(NOMBRE);
	}

}
