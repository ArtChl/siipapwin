package com.luxsoft.siipap.services;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.managers.DescuentosManager;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Clase abstracta para simplificar la creación de procesos programados
 * 
 * 
 * @author Ruben Cancino
 *
 */
@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
public class AbstractServiceTask extends HibernateDaoSupport{
	
	protected Logger logger=Logger.getLogger(getClass());
	
	private ClienteDao clienteDao;
	private VentasManager ventasManager;
	private NotasManager notasManager;
	private DescuentosManager descuentosManager;
	private JdbcTemplate jdbcTemplate;
	
	public ClienteDao getClienteDao() {
		return clienteDao;
	}
	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public VentasManager getVentasManager() {
		return ventasManager;
	}
	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}
	public DescuentosManager getDescuentosManager() {
		return descuentosManager;
	}
	public void setDescuentosManager(DescuentosManager descuentosManager) {
		this.descuentosManager = descuentosManager;
	}
	public NotasManager getNotasManager() {
		return notasManager;
	}
	public void setNotasManager(NotasManager notasManager) {
		this.notasManager = notasManager;
	}
	
	

}
