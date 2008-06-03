package com.luxsoft.siipap.maquila.dao;


import java.util.List;

import com.luxsoft.siipap.dao.GenericDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.maquila.domain.Bobina;

public interface BobinaDao extends GenericDao<Bobina,Long>{
	
	public Bobina buscarPorClave(String clave);
	public Bobina buscarPorClave(String clave,boolean fetchUnidades);
	public Bobina buscarPorClave(String clave,boolean fetchUnidades,boolean fetchFamilia);
	
	public List<Articulo> buscarSulfatada();
	
	
	

}
