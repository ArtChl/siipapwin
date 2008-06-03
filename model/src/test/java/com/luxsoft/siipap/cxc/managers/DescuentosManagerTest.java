package com.luxsoft.siipap.cxc.managers;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.luxsoft.siipap.cxc.dao.DescuentoPorArticuloDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;

public class DescuentosManagerTest extends MockObjectTestCase{
	
	public void testBuscarDescuentosPorCliente(){
	
		DescuentosManager manager=new DescuentosManager();
		Mock mockDao=mock(DescuentoPorClienteDao.class);
		mockDao.expects(once()).method("buscarDescuentos");
		manager.setDescuentoPorClienteDao((DescuentoPorClienteDao)mockDao.proxy());
		manager.buscarDescuentosPorCliente();
	}
	
	public void testBuscarDescuentosPorArticulo(){
		DescuentosManager manager=new DescuentosManager();
		Cliente c=new Cliente();
		c.setClave("U050008");		
		Mock mockDao=mock(DescuentoPorArticuloDao.class);
		mockDao.expects(once()).method("buscar").with(this.same(c));
		manager.setDescuentoPorArticuloDao((DescuentoPorArticuloDao)mockDao.proxy());
		manager.buscarDescuentosPorArticulo(c);
	}

}
