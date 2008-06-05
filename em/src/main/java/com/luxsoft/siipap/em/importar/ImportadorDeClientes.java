package com.luxsoft.siipap.em.importar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

/**
 * Capaz de importar uno o mas cliente del catalogo de clientes
 * 
 * @author Ruben Cancino
 *
 */
@SuppressWarnings("unchecked")
public class ImportadorDeClientes implements Importador{
	
	
	private Logger logger=Logger.getLogger(getClass());
	private RowMapper rowMapper;
	private SiipapJdbcTemplateFactory factory;

	public Object[] importar(Object... params) {
		final List<Cliente> clientes=new ArrayList<Cliente>();
		for(Object s:params){
			Cliente c=leerCliente(s.toString());
			clientes.add(c);
		}
		return clientes.toArray();
	}	
	
	public Cliente leerCliente(final String clave){
		final String sql="select * from CLIENTES where CLICLAVE=?";
		List<Cliente> l=getFactory().getJdbcTemplate(new Date()).query(sql, new String[]{clave}, getRowMapper());
		if(l.isEmpty())	return null;
		if(logger.isDebugEnabled()){
			logger.debug("Cliente existosamente importado: "+l.get(0));
		}
		return l.get(0);
	}
	

	public RowMapper getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper rowMapper) {
		this.rowMapper = rowMapper;
	}

	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}

	
	
	

}
