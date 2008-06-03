/*
 * Created on 17-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.luxsoft.siipap.dao;

import java.util.List;

import com.luxsoft.siipap.domain.Proveedor;


/**
 * @author Reuben
 */

public interface ProveedorDao {

    public void add(Proveedor f);
    public void delete(Proveedor f);
    public void update(Proveedor f);
    public Proveedor buscarPorClave(final String name);
    public Proveedor buscarPorClave(final String name,boolean artfetched);
    public Proveedor fetchArticulos(final Proveedor p);
    public List buscarArticulosNoAsignados(final Proveedor p);
    public List buscarArticulosAsignados(final Proveedor p);
    
    public int contarRegistros();
    public List buscarTodos();
    public List buscarTodos(boolean articulosFetched);
    public void crearProveedores(List proveedores);
    
    public void inicializarProductos(Proveedor p);
    

}
