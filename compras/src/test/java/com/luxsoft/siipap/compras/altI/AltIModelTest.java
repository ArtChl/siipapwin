package com.luxsoft.siipap.compras.altI;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.luxsoft.siipap.dao.ArticuloDao;

/**
 * Pruebas para AltIModel
 * 
 * @author Ruben Cancino
 *
 */
public class AltIModelTest extends MockObjectTestCase{
	
	private AltIModel model;
	
	public void setUp(){
		model=new AltIModel();
	}
	
	public void testListaDeArticulos(){
		Mock mock=mock(ArticuloDao.class);
		mock.expects(this.once());
		model.setArticuloDao((ArticuloDao)mock.proxy());
		model.buscarArticulos();
	}

}
