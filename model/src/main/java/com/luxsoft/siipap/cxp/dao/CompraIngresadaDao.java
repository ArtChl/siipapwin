package com.luxsoft.siipap.cxp.dao;

import java.util.List;

import com.luxsoft.siipap.cxp.domain.CompraIngresada;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Entrada;

public interface CompraIngresadaDao {
	
	public int generarIngresos(Periodo p);
	
	public List<Entrada> buscarComs(final Periodo p);
	
	public CompraIngresada generarCom(final Entrada com);
	
	public void actualizarCompraIngresada(CompraIngresada com);
	
	public Entrada localizarEntrada(Integer sucursal,String proveedor,Long facrem,String articulo);
	
	public CompraIngresada localizarCompraIngresada(Integer sucursal,String proveedor,Long facrem,String articulo);
	
	public CompraIngresada localizarCompra(Integer sucursal,Long numero,String clave,Integer renglon);
	
	public List<CompraIngresada> buscarComsSinAnalizar(final String proveedor);
	
	

}
