package com.luxsoft.siipap.compras.altI;

import java.util.List;

import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Articulo;

/**
 * PresentationModel de estado y comportamiento para AltI
 * 
 * @author Ruben Cancino
 *
 */
public class AltIModel {
	
	private ArticuloDao articuloDao;
	
	public List<Articulo> buscarArticulos(){
		return getArticuloDao().buscarTodosLosActivos();
	}

	public ArticuloDao getArticuloDao() {
		return articuloDao;
	}

	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}
	
	
	

}
