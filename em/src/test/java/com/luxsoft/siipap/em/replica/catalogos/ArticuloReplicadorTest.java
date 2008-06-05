package com.luxsoft.siipap.em.replica.catalogos;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;


import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.em.replica.AbstractReplicaTest;

/**
 * Integration test para el replicador de familias
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public  class ArticuloReplicadorTest extends AbstractReplicaTest{
	
	private ArticulosReplicator replicator;
	
	
	public void testReplication(){
		
		JdbcTemplate template=factory.getJdbcTemplate(new Date());
		int expected=template.queryForInt("select count(*) from ARTICULO");
		List beans=replicator.importar(null);
		assertEquals(expected, beans.size());
		setComplete();
		
	}

	public void setReplicator(ArticulosReplicator replicator) {
		this.replicator = replicator;
	}
	
	
	
	
	
	
	
	

}
