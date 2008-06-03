package com.luxsoft.siipap.dao;

import com.luxsoft.siipap.domain.Unidad;

public interface UnidadDao extends GenericDao<Unidad,Long>{
	
	public Unidad findByClave(String clave);

}
