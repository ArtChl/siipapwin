/*
 * Created on 30/03/2005
 *
 * TODO Colocar informacion de licencia
 */
package com.luxsoft.siipap.dao;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.ListaDePrecios;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Precio;
import com.luxsoft.siipap.domain.Proveedor;

/**
 * @author Ruben Cancino
 */
public interface ListaDePreciosDao {
    
	/**
	 * Salva una lista de precios que existia en Old Siipa Win
	 * es decir con la propiedad de numero ya establecida
	 * @param l
	 * @return
	 */
    public ListaDePrecios salvar(final ListaDePrecios l);
    
    /**
     * Crea una lista de precios totalmente nueva, el campo 
     * de numero es generado automaticamente
     * @param l
     * @return
     */
	public ListaDePrecios crear(final ListaDePrecios l);
	
    public ListaDePrecios modificar(final ListaDePrecios l);
    public void borrar(ListaDePrecios l);
    public ListaDePrecios clonar(final ListaDePrecios l);
    
    public ListaDePrecios buscarPorId(Long id);
    public ListaDePrecios buscarPorNumero(long numero,Proveedor p);
    public ListaDePrecios buscarPorNumeroFetched(long numero,Proveedor p);
    
    public List<ListaDePrecios> buscarListas(final Proveedor p);
    
    public List<ListaDePrecios> buscarListaVigente(final Proveedor p);
    
    /**
     * Busca las listas de precios vigentes para el proveedor determinado en la fecha estipulada
     * @param p
     * @param fecha
     * @return
     */
    public List<ListaDePrecios> buscarListaVigente(final Proveedor p,final Date fecha);
    
    public void inicializarPrecios(final ListaDePrecios lp);
    
    public List buscarPrecios(ListaDePrecios lp);
    
    public List buscarTodasLasListas();
    
    public List buscarTodasLasListas(final Date desde);
    
    public CantidadMonetaria buscarPrecio(String proveedor ,String  artclave,final Date fecha);
    public CantidadMonetaria buscarPrecio(String proveedor ,String  artclave,final Date fecha,Currency moneda);
    public Precio precio(String proveedor ,String  artclave,final Date fecha,Currency moneda);
    public CantidadMonetaria buscarPrecio(String  artclave,final Date fecha);
    
    public Precio precioMN(String proveedor ,String  artclave,final Date fecha);    
    public Precio precioMN(String  artclave,final Date fecha);
    
    /**
     * Busca algun precio para el articulo especificado 
     * 
     * @param articulo
     * @return
     */
    public Precio precioMN(final String articulo);
    
    /**
     * Busca los precios vigentes
     *  
     */
	public List<Precio> buscarPreciosVigentes(final Date fecha);
	
	/**
	 * Busca todos los precios para el proveedor y articulo seleccionado
	 * 
	 * @param proveedor
	 * @param articulo
	 * @return
	 */
	public List<Precio> buscarPrecios(final String proveedor,final String articulo);
	
	/**
	 * Usado en los filtros de browser
	 * @param o
	 * @return
	 */
	public List<Precio> buscarPorFiltro(final Object o);
	
	/**
	 * Buscar todos los precios del articulo 
	 * @param articulo
	 * @return
	 */
	public List<Precio> buscarPrecios(String articulo);
	
	
	/**
	 * Busca las listas de precios de un  proveedor en forma adecuada para un browser
	 * 
	 * @param proveedor
	 * @return
	 */
	public List<Object[]> buscarListasDePrecios(final Proveedor proveedor);
	
	
	/**
	 * Inicializa el Precio indicado
	 * @param p
	 */
	public void inicializarPrecio(final Precio p);
	
	
	public List<Object[]> browse();

}
