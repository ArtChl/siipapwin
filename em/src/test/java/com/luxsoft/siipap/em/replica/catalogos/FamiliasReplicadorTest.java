package com.luxsoft.siipap.em.replica.catalogos;

import java.util.List;
import java.util.Map;

import com.luxsoft.siipap.domain.Familia;
import com.luxsoft.siipap.em.replica.AbstractReplicaTest;

/**
 * Integration test para el replicador de familias
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public abstract class FamiliasReplicadorTest extends AbstractReplicaTest{
	
	
	private FamiliasReplicator replicador;
	
	/**
	 * Prueba la creación de un objeto Familia
	 * a partir de una fila de la tabla FAMARTIC.DBF
	 */
	
	public void testImportarFamilia(){
		/*
		String sql="select * from FAMARTIC";		
		List<Map<String, Object>> rows=factory.getJdbcTemplate(2006).queryForList(sql);
		for(Map<String,Object> row:rows){
			Familia f=replicador.importar(row);
			assertNotNull(f);
		}
		*/
	}

	
	
	public void setReplicador(FamiliasReplicator replicador) {
		this.replicador = replicador;
	}

	

}
