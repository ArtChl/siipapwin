package com.luxsoft.siipap.swing.form2;

import com.jgoodies.validation.ValidationResult;
import com.luxsoft.siipap.domain.Linea;

import junit.framework.TestCase;

/**
 * Prueba que la implementacion estandar de BeanValidator
 * opere correctamente
 * 
 * @author Ruben Cancino
 *
 */
public class BeanValidationImplTest extends TestCase{
	
	
	public void testValidate(){
		BeanValidator validation=new BeanValidationImpl();
		
		Linea l=new Linea();
		assertNull(l.getNombre());
		
		ValidationResult r=validation.validate(l);
		assertTrue(r.hasErrors());
		
		l.setNombre("BAD");		
		assertTrue(validation.validate(l).hasErrors());
		
		l.setNombre("NAME OK");
		assertTrue(validation.validate(l).hasErrors());
	}

}
