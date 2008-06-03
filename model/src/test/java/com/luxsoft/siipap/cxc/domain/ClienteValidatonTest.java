package com.luxsoft.siipap.cxc.domain;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

import junit.framework.TestCase;

/**
 * Valida las propiedades de un Cliente
 * 
 * @author Ruben Cancino
 *
 */
public class ClienteValidatonTest extends TestCase{
	
	public void testCliente(){
		ClassValidator validator=new ClassValidator(Cliente.class);
		
		Cliente c=new Cliente();
		c.setClave("GOOD");
		
		InvalidValue[] iv=validator.getInvalidValues(c);
		for(InvalidValue v:iv){
			System.out.println(v.getMessage());
		}
		assertTrue(iv.length==0);
	}

}
