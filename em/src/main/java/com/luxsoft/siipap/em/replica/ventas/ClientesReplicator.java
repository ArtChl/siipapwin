package com.luxsoft.siipap.em.replica.ventas;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.DefaultMapper;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;


@SuppressWarnings("unchecked")
public class ClientesReplicator extends AbstractReplicatorSupport  {
		
	private Map<String,String> propertyColumnMap;
	
	private ClienteDao clienteDao;
	
	public void exportar(Object bean) {
		
		
	}

	/**
	 * Esta implementacion importa con forme lee los registros del dbf ya que el catalogo 
	 * de clientes es muy grande no ser requiere la lista de los importados
	 * la lista que regresa esta vacia
	 */
	public List importar(Periodo periodo) {		
		//El periodo no importa
		final DefaultMapper mapper=new DefaultMapper();
		mapper.setBeanClass(getBeanClass());
		mapper.setOrigen("CLIENTE");
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		
		String sql="select CLICLAVE from CLIENTES";		
		List<String> existentes=getFactory().getJdbcTemplate(new Date()).queryForList(sql, String.class);
		//Set<String> siipap=existentes.
		System.out.println("Clientes en siipap: "+existentes.size());
		
		HibernateTemplate ht=new HibernateTemplate();
		ht.setSessionFactory((SessionFactory)ServiceManager.instance().getContext().getBean("sessionFactory"));
		List<String> siipapwin=ht.find("select c.clave from Cliente c");
		System.out.println("Clientes en siipapwin: "+siipapwin.size());
		
		Collection<String> res=CollectionUtils.subtract(existentes, siipapwin);
		System.out.println("Faltantes: "+res.size());
		for(String clave:res){
			Cliente c=importar(clave);
			persistir(c);
		}
		
		return new ArrayList();
	}
	
	public Cliente importar(String clave){
		
		final DefaultMapper mapper=new DefaultMapper();
		mapper.setBeanClass(getBeanClass());
		mapper.setOrigen("CLIENTE");
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		
		String sql="select * from CLIENTES where CLICLAVE=?";
		//sql=sql.replaceAll("@CLAVE", clave);
		List ll=getFactory().getJdbcTemplate(new Date()).query(sql,new Object[]{clave},new int[]{Types.VARCHAR}, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Cliente c=(Cliente)mapper.mapRow(rs, rs.getRow());
				if(logger.isDebugEnabled()){
					logger.debug("Cliente mapeado: "+c.getClave());
				}			
				return c;
			}
			
		});
		if(ll.isEmpty()) return null;
		return (Cliente)ll.get(0);
		
	}
	
	public void persistir(Object bean) {
		
		Cliente c=(Cliente)bean;		
		ClienteDao dao=getClienteDao();
		Cliente found=dao.buscarPorClave(c.getClave());
		if(found!=null){
			if(logger.isDebugEnabled()){
				String msg=MessageFormat.format("El cliente {0} ya existe copiando propiedades declaradas",c.getClave());
				logger.debug(msg);
				found.setCuentacontable(c.getCuentacontable());
				found.getCredito().setCuenta(c.getCuentacontable());
				found.setCalle(c.getCalle());
				found.setColonia(c.getColonia());
				found.setCiudad(c.getCiudad());
				found.setCedula(c.getCedula());
				found.setNumeroExterior(c.getNumeroExterior());
				found.setNumeroInterior(c.getNumeroInterior());
				found.setCpostal(c.getCpostal());
				found.setEntidad(c.getEntidad());
				found.setEstado(c.getEstado());
				found.setTelefono1(c.getTelefono1());
				
				
				/**
				BeanUtils.copyProperties(c, found, new String[]{"id","clave","version","credito"});
				System.out.println("Actualizando credito");
				if(found.getCredito()!=null){
					found.getCredito().copyFromCliente();
					System.out.println("Credito actualizado");
				}
				**/
				dao.salvar(found);
			}
		}else{
			if(c.getCredito()!=null)
				c.getCredito().updateProperties();
			dao.salvar(c);
		}
			
	}
	
	/**
	 * Actualize las propiedades de los beans Cliente en siipapwin desde siipap dbf
	 *
	 */
	public void actualizar(){
		List<Map<String, String>> claves=getTargetJdbcTemplate().queryForList("select CLAVE from SW_CLIENTES_CREDITO");
		System.out.println("Clientes por actualizar: "+claves.size());
		for(Map<String, String> row:claves){
			String clave=row.get("CLAVE");
			Cliente cliente=importar(clave);			
			persistir(cliente);
		}		
	}
	
	public void actualizar(String clienteClave){
		List<Map<String, String>> claves=getTargetJdbcTemplate().queryForList("select CLAVE from SW_CLIENTES_CREDITO where clave=?",new String[]{clienteClave});		
		for(Map<String, String> row:claves){
			String clave=row.get("CLAVE");
			Cliente cliente=importar(clave);			
			persistir(cliente);
		}		
	}

	/**
	 * Valida que no existan registros en el periodo seleccionado para poder generar una carga bulk nuevaç
	 * 
	 * @param p
	 */
	public void validarBulkImport(Periodo p){	}
	
	/* (non-Javadoc)
	 * @see com.luxsoft.siipap.em.replica.ventas.Importador#bulkImport(com.luxsoft.siipap.domain.Periodo)
	 */
	public void bulkImport(Periodo p){	}	

	/* (non-Javadoc)
	 * @see com.luxsoft.siipap.em.replica.ventas.Importador#validar(com.luxsoft.siipap.domain.Periodo)
	 */
	public List<ReplicaLog> validar(final Periodo periodo){
		return null;
	}
	
	
	
	public Map<String, String> getPropertyColumnMap() {
		return propertyColumnMap;
	}

	public void setPropertyColumnMap(Map<String, String> propertyColumnMap) {
		this.propertyColumnMap = propertyColumnMap;
	}
	
	

	public ClienteDao getClienteDao() {
		return clienteDao;
	}

	public void setClienteDao(ClienteDao clienteDao) {
		this.clienteDao = clienteDao;
	}

	public static void main(String[] args) {
		ClientesReplicator r=(ClientesReplicator)ServiceManager.instance().getReplicador(Replicadores.ClientesReplicator);
		//r.importar(Periodo.hoy());
		//r.actualizar();
		r.actualizar("C050967");
		//Cliente c=r.importar("C050967");
		//r.persistir(c);
		//System.out.println("Plazo: "+c.getPlazo());
	}

}
