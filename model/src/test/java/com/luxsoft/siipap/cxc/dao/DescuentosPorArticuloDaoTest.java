package com.luxsoft.siipap.cxc.dao;

import java.util.List;

import org.springframework.test.annotation.NotTransactional;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.dao.AbstractDaoTest;

public class DescuentosPorArticuloDaoTest extends AbstractDaoTest{
	
	private DescuentoPorArticuloDao dao;
	
	@NotTransactional
	public void testBuscarDescuentosPorCliente(){
		
		Cliente c=new Cliente();
		String clave="C010651";
		c.setClave(clave);
		int expected=jdbcTemplate.queryForInt("select count(*) from SW_DESC_CTE where tipo=? and CLAVE=?",new Object[]{"A",clave});
		System.out.println("Expected: "+expected);
		List<DescuentoPorArticulo> descs=dao.buscar(clave);
		int actual=descs.size();
		assertEquals(expected, actual);
		

		for(DescuentoPorArticulo d:descs){
			//Articulos not lazy test			
			d.getArticulo().getClave();			
			d.getArticulo().getDescripcion1();
			//Linea no laxy
			d.getArticulo().getLinea().getNombre();
		}
		
		
	}

	public void setDao(DescuentoPorArticuloDao dao) {
		this.dao = dao;
	}
	
	

	
	
	

	

	
	
	
	

}
