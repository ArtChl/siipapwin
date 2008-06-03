package com.luxsoft.siipap.inventarios2.dao;

import com.luxsoft.siipap.dao.AbstractDaoTest;
import com.luxsoft.siipap.inventarios2.domain.Concepto;

/**
 * Integration test para MoviDao 
 * 
 * @author Ruben Cancino
 *
 */
public class ConcpetoDaoTest extends AbstractDaoTest{
	
	private ConceptoDao dao;
	
	public void testAddDelete(){
		final Concepto c=new Concepto();
		c.setClave("INI");
		c.setDescripcion("Inventario inicial");
		c.setCuenta("000");
		c.setTipo("E");
		dao.salvar(c);
		
		final Concepto c2=new Concepto();
		c2.setClave("INI");
		c2.setDescripcion("Inventario inicial");
		c2.setCuenta("000");
		c2.setTipo("S");
		dao.salvar(c2);
		
		setComplete();
	}

	public void setDao(ConceptoDao dao) {
		this.dao = dao;
	}

	
	
	
	
	

}
