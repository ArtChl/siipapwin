package com.luxsoft.siipap.swing.form2;

import java.util.Set;

import junit.framework.TestCase;

import com.luxsoft.siipap.domain.Linea;

/**
 * Prueba el correcto funcionamiento de BeanPropertiesExtractorImpl
 * 
 * @author RUBEN
 *
 */
@SuppressWarnings("unchecked")
public class BeanPropertiesExtractorImplTest extends TestCase{
	
	private BeanPropertiesExtractor extractor;
	
	private Class clazz;
	
	public void setUp(){
		extractor=new BeanPropertiesExtractorImpl();
		clazz=Linea.class;
	}
	
	public void testGetProperties(){
		
		
		Set<String> properties=extractor.getUIProperties(clazz);
		assertEquals(2, properties.size());
		assertEquals("nombre", properties.iterator().next());
		
	}
	
	public void testReadOnlyProperties(){
		assertTrue(extractor.isReadOnly(clazz, "id"));
		assertFalse(extractor.isReadOnly(clazz, "nombre"));
	}
	
	public void testLable(){
		assertEquals("Linea", extractor.getLabel(clazz, "nombre"));
		assertEquals("Id", extractor.getLabel(clazz, "id"));
	}

}
