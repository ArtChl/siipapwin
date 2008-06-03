package com.luxsoft.siipap.cxc.domain;

import junit.framework.TestCase;

public class PagoTest extends TestCase{
	
	public void testEequalsHashCode(){
		Pago p1=new Pago();
		Pago p2=new Pago();
		assertFalse(p1.equals(p2));
		
		p1.setId(10L);
		p2.setId(10L);
		assertFalse(p1.equals(p2));
		
		assertFalse(p1.getCreado().equals(p2.getCreado()));
		
		
	}

}
