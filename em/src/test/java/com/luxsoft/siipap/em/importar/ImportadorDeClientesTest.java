package com.luxsoft.siipap.em.importar;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

public class ImportadorDeClientesTest extends AbstractDependencyInjectionSpringContextTests{
	
	private SiipapJdbcTemplateFactory factory;
	private ImportadorDeClientes importador;
	
	
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"em-import-ctx.xml"};							 
	}
	
	
	public void testImportar(){
		final String clave="U050008";
		List<Map<String, Object>> rows=factory.getJdbcTemplate(new Date()).queryForList("select * from CLIENTES where CLICLAVE=?" , new String[]{clave});
		assertTrue("Debe existir un registro de prueba para CLICLAVE: "+clave,rows.size()==1);
		final Object[] clientes=importador.importar(clave);
		assertTrue("Debe generar unicamente un cliente",clientes.length==1);
		
		Map<String, Object> row=rows.get(0);
		Cliente c=(Cliente)clientes[0];
		
		final int plazo=((Number)row.get("CLIPLAZO")).intValue();
		final String nombre=row.get("CLINOMBRE").toString();
		assertEquals(plazo, c.getPlazo());
		assertEquals(nombre.trim(),c.getNombre().trim());
		
	}


	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}


	public void setImportador(ImportadorDeClientes importador) {
		this.importador = importador;
	}
	
	

}
