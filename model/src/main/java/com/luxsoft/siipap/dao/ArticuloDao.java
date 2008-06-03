/*
 * Created on 17-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.dao;



import java.util.List;

import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.ArticuloRow;
import com.luxsoft.siipap.domain.Familia;
import com.luxsoft.siipap.domain.UnidadesPorArticulo;

/**
 * @author Ruben Cancino
 *
 */
public interface ArticuloDao extends GenericDao<Articulo,Long>{
	
	

    public Articulo buscarPorClave(final String name);
    
    public Articulo buscarPorClaveConUnidades(final String name);
    
    public Articulo buscarPorClaveConFamilias(final String name);
    
    public List<Articulo> buscarTodos();
    
    public List<Articulo> buscarTodosLosActivos();
    
    public List<Articulo> buscarTodosConFamilia();
    
    public List<Articulo> buscarPorFamilia(Familia familia);
    
    public void agregarArticulos(List<Articulo> articulos);
    public int contarRegistros();
    
    
    public void salvarUnidadesPorArticulo(List<UnidadesPorArticulo> unidades);
    public int contarUnidadsPorArticulo();
    public List<UnidadesPorArticulo> buscarTodasLasUnidadesPorArticulo();
    
    public List<ArticuloRow> browse();
    public void initProxy(final Object proxy);
    
    
    public List<Articulo> buscarRango(Articulo a1,Articulo a2);
    
    

}
