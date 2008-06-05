package com.luxsoft.siipap.em.replica.catalogos;

import java.util.Map;

import com.luxsoft.siipap.domain.Familia;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;

public class FamiliasReplicator {
	
	private SiipapJdbcTemplateFactory factory;
	private Class entidad;
	
	
	public Familia importar(Map<String, Object> row){
		return null;
	}

	public Class getEntidad() {
		return entidad;
	}
	public void setEntidad(Class entidad) {
		this.entidad = entidad;
	}

	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}
	
	
}
