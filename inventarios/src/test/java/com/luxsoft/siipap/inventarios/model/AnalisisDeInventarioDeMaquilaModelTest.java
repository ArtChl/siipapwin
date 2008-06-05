package com.luxsoft.siipap.inventarios.model;

import junit.framework.TestCase;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import ca.odell.glazedlists.BasicEventList;

import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.manager.MaquilaManager;

/**
 * Prueba el estado desempeño del {@link AnalisisDeInventarioDeMaquilaModel}
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeInventarioDeMaquilaModelTest extends MockObjectTestCase{
	
	
	private AnalisisDeInventarioDeMaquilaModelImpl model;
	
	
	public void setUp(){
		model=new AnalisisDeInventarioDeMaquilaModelImpl();
	}
	
	
	/**
	 * Prueba que se delegue la produccion de la lista de entradas de material
	 *  
	 * 
	 */
	public void testBuscarEntradas(){
		
		
		final Mock mock=mock(MaquilaManager.class);
		mock.expects(once()).method("buscarEntradasDeMaterial")
			.withNoArguments()
			.will(returnValue(new BasicEventList<EntradaDeMaterial>()))
			;
		model.setMaquilaManager((MaquilaManager)mock.proxy());
		//assertNotNull(model.getEntradas());
		//assertFalse(model.getEntradas().isEmpty());
		
	}

}
