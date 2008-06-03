package com.luxsoft.siipap.maquila.dao;


import java.util.List;

import com.luxsoft.siipap.dao.GenericDao;
import com.luxsoft.siipap.maquila.domain.Almacen;
import com.luxsoft.siipap.maquila.domain.Maquilador;

public interface MaquiladorDao extends GenericDao<Maquilador,Long>{
	
	public Maquilador buscarPorClave(String clave);
	public List<Maquilador> buscarMaquiladores();
	public List<Almacen> buscarAlmacenes();
	public Almacen buscarAlmacen(final String nombre);
	
	

}
