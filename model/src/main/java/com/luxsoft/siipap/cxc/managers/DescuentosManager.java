package com.luxsoft.siipap.cxc.managers;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.dao.DescuentoEspecialDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorArticuloDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorClienteDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorVolumenDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoEspecial;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.cxc.domain.DescuentoPorCliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorVolumen;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.inventarios.dao.CostoPromedioDao;
import com.luxsoft.siipap.inventarios.domain.CostoPromedio;
import com.luxsoft.siipap.services.ServiceLocator;

public class DescuentosManager {
	
	private Logger logger=Logger.getLogger(getClass());
	
	private ClienteDao clienteDao;
	private ArticuloDao articuloDao;
	private CostoPromedioDao costoPromedioDao;
	
	private DescuentoPorArticuloDao descuentoPorArticuloDao;
	private DescuentoPorClienteDao descuentoPorClienteDao;
	private DescuentoPorVolumenDao descuentoPorVolumenDao;
	private DescuentoEspecialDao descuentoEspecialDao;
	
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public DescuentoPorCliente salvar(DescuentoPorCliente d){
		validar(d);
		d.setClave(d.getCliente().getClave());
		d.setNombre(d.getCliente().getNombre());
		if(!d.isActivo()) d.setBaja(new Date());
		d.setModificado(new Date());
		getDescuentoPorClienteDao().salvar(d);
		if(logger.isDebugEnabled()){
			logger.debug("Descuento por cliente salvado/actualizado id: "+d.getId());
		}
		return d;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public DescuentoEspecial salvar(DescuentoEspecial d){
		d.setCreado(new Date());
		getDescuentoEspecialDao().salvar(d);
		ServiceLocator.getVentasManager().actualizarVenta(d.getVenta());
		return d;
		
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=false)
	public void validar(DescuentoPorCliente d){
		if(d.getId()==null){
			DescuentoPorCliente dd=getDescuentoPorClienteDao().buscar(d.getCliente());
			if(dd!=null)
				throw new RuntimeException("Ya existe un descuento fijo :"+dd);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void salvar(final List<DescuentoPorArticulo> list){
		for(DescuentoPorArticulo d:list){
			d.calcularPrecioKiloEnFuncionDeDescuento();
			getDescuentoPorArticuloDao().salvar(d);
		}
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public DescuentoPorCliente buscarDecuentoPorCliente(Cliente c){
		return getDescuentoPorClienteDao().buscar(c);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<DescuentoPorCliente> buscarDescuentosPorCliente() {		
		return getDescuentoPorClienteDao().buscarDescuentos();
	}
	public List<DescuentoPorArticulo> buscarDescuentosPorArticulo(Cliente c){
		return getDescuentoPorArticuloDao().buscar(c.getClave());
	}
	
	public List<DescuentoEspecial> buscarDescuentosPorVenta(){
		return getDescuentoEspecialDao().buscar();
	}
	
	public List<DescuentoPorVolumen> buscarDescuentosPorVolumen(){
		return getDescuentoPorVolumenDao().buscar();
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=false)
	public Cliente buscarCliente(String clave){
		return getClienteDao().buscarPorClave(clave);
	}
	
	public List<Articulo> buscarArticulos(){
		return getArticuloDao().buscarTodosLosActivos();
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public void asignarCostos(List<DescuentoPorArticulo> list){		
		for(DescuentoPorArticulo d:list){
			try {
				CostoPromedio cp=getCostoPromedioDao().buscarCostoMasReciente(d.getArticulo());			
				d.setCosto(cp.getCosto());
				d.setUltimoCosto(getCostoPromedioDao().buscarUltimoCosto(d.getArticulo()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void eliminarDescuento(DescuentoPorArticulo d){		
		getDescuentoPorArticuloDao().eliminar(d);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=false)
	public void eliminarDescuento(DescuentoPorCliente d){
		getDescuentoPorClienteDao().eliminar(d);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=false)
	public void eliminarDescuento(DescuentoPorVolumen d){
		getDescuentoPorVolumenDao().eliminar(d);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=false)
	public void eliminarDescuento(DescuentoEspecial d){
		getDescuentoEspecialDao().eliminar(d);
	}
	
	
	
	/*** Colaboradores ***/

	public ClienteDao getClienteDao() {
		return clienteDao;
	}
	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}

	public DescuentoPorArticuloDao getDescuentoPorArticuloDao() {
		return descuentoPorArticuloDao;
	}

	public void setDescuentoPorArticuloDao(
			DescuentoPorArticuloDao descuentoPorArticuloDao) {
		this.descuentoPorArticuloDao = descuentoPorArticuloDao;
	}

	public DescuentoEspecialDao getDescuentoEspecialDao() {
		return descuentoEspecialDao;
	}

	public void setDescuentoEspecialDao(DescuentoEspecialDao descuentoEspecialDao) {
		this.descuentoEspecialDao = descuentoEspecialDao;
	}

	public DescuentoPorClienteDao getDescuentoPorClienteDao() {
		return descuentoPorClienteDao;
	}

	public void setDescuentoPorClienteDao(
			DescuentoPorClienteDao descuentoPorClienteDao) {
		this.descuentoPorClienteDao = descuentoPorClienteDao;
	}

	public DescuentoPorVolumenDao getDescuentoPorVolumenDao() {
		return descuentoPorVolumenDao;
	}

	public void setDescuentoPorVolumenDao(
			DescuentoPorVolumenDao descuentoPorVolumenDao) {
		this.descuentoPorVolumenDao = descuentoPorVolumenDao;
	}

	public ArticuloDao getArticuloDao() {
		return articuloDao;
	}

	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}

	public CostoPromedioDao getCostoPromedioDao() {
		return costoPromedioDao;
	}

	public void setCostoPromedioDao(CostoPromedioDao costoPromedioDao) {
		this.costoPromedioDao = costoPromedioDao;
	}
	
	
		 

}
