package com.luxsoft.siipap.managers;

import java.util.List;

import com.luxsoft.siipap.cxc.dao.CobradorDao;
import com.luxsoft.siipap.cxc.domain.Cobrador;
import com.luxsoft.siipap.cxc.domain.Vendedor;
import com.luxsoft.siipap.dao.LineaDao;
import com.luxsoft.siipap.dao.MarcaDao;
import com.luxsoft.siipap.domain.Linea;
import com.luxsoft.siipap.domain.Marca;
import com.luxsoft.siipap.services.ServiceLocator;

/**
 * Manager para la persistencia de Catalogos 
 * 
 * @author Ruben Cancino
 *
 */
public class CatalogosManager {
	
	private LineaDao lineaDao;
	private MarcaDao marcaDao;
	private CobradorDao cobradorDao;
	
	public List<Linea> buscarLineas(){
		return getLineaDao().buscarTodas();
	}
	
	public void salvarLinea(final Linea l){
		getLineaDao().salvar(l);
	}
	
	public void eliminarLinea(final Linea l){
		getLineaDao().eliminar(l);
	}
	public List<Marca> buscarMarcas(){
		return getMarcaDao().buscarTodas();
	}
	public void eliminarMarca(final Marca m){
		getMarcaDao().eliminar(m);
	}
	public void salvarMarca(final Marca m){
		getMarcaDao().salvar(m);
	}
	
	public List<Cobrador> buscarCobradores(){
		return getCobradorDao().buscar();
	}
	
	public void salvarCobrador(final Cobrador c){
		getCobradorDao().salvar(c);
	}
	public void actualizarCobrador(final Cobrador c){
		getCobradorDao().actualizar(c);
	}
	
	@SuppressWarnings("unchecked")
	public List<Vendedor> buscarVendedores(){
		return ServiceLocator.getUniversalDao().getAll(Vendedor.class);
	}
	
	public void salvarVendedor(final Vendedor v){
		ServiceLocator.getUniversalDao().save(v);
	}
	public void actualizarVendedor(final Vendedor v){
		ServiceLocator.getUniversalDao().save(v);
	}
	
	/** Injected dependencies ***/

	public LineaDao getLineaDao() {
		return lineaDao;
	}
	public void setLineaDao(LineaDao lineaDao) {
		this.lineaDao = lineaDao;
	}

	public MarcaDao getMarcaDao() {
		return marcaDao;
	}

	public void setMarcaDao(MarcaDao marcaDao) {
		this.marcaDao = marcaDao;
	}
	public CobradorDao getCobradorDao() {
		return cobradorDao;
	}

	public void setCobradorDao(CobradorDao cobradorDao) {
		this.cobradorDao = cobradorDao;
	}
	

}
