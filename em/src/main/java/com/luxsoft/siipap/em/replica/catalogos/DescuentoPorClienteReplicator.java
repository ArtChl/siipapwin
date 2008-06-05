package com.luxsoft.siipap.em.replica.catalogos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.luxsoft.siipap.cxc.dao.ClienteDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorArticuloDao;
import com.luxsoft.siipap.cxc.dao.DescuentoPorClienteDao;
import com.luxsoft.siipap.cxc.domain.AbstractDescuento;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.DescuentoPorArticulo;
import com.luxsoft.siipap.cxc.domain.DescuentoPorCliente;
import com.luxsoft.siipap.dao.ArticuloDao;
import com.luxsoft.siipap.dao.FamiliaDao;
import com.luxsoft.siipap.domain.Articulo;
import com.luxsoft.siipap.domain.Familia;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.domain.Articulo.Estado;
import com.luxsoft.siipap.em.replica.AbstractReplicatorSupport;
import com.luxsoft.siipap.em.replica.DefaultMapper;
import com.luxsoft.siipap.em.replica.Replicador;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.domain.ReplicaLog;
import com.luxsoft.siipap.em.replica.service.ServiceManager;
import com.luxsoft.siipap.utils.ArticuloEstadoUserType;

@SuppressWarnings("unchecked")
public class DescuentoPorClienteReplicator extends AbstractReplicatorSupport{
	
	private ClienteDao clienteDao;
	private ArticuloDao articuloDao;
	private FamiliaDao familiaDao;
	private DescuentoPorArticuloDao descuentoPorArticuloDao;
	
	public List importar(Periodo periodo) {
		getDescDao().eliminarDescuentosOrigenSiipap();
		List<AbstractDescuento> desc=cargarDescuentos(periodo);
		List<DescuentoPorCliente> porClientes=new ArrayList<DescuentoPorCliente>();
		List<DescuentoPorArticulo> porArticulos=new ArrayList<DescuentoPorArticulo>();
		for(AbstractDescuento d:desc){
			
			//Asignamos el cliente
			if(d.getClave()!=null){
				Cliente c=getClienteDao().buscarPorClave(d.getClave().trim());
				if(c==null){
					System.out.println("No encontre al cliente: "+d.getClave());
				}
				d.setCliente(c);
				d.setNombre(c.getNombre());
			}
			
			if(d.getComentario().equals("F")){
				DescuentoPorCliente ad=new DescuentoPorCliente();
				BeanUtils.copyProperties(d, ad);
				porClientes.add(ad);
			}else{
				DescuentoPorArticulo dd=(DescuentoPorArticulo)d;
				Familia f=getFamiliaDao().buscarFamilia(dd.getClaveFamilia());
				dd.setFamilia(f);
				porArticulos.add(dd);
				//good.add(d); TEMPORAL QUITAR COMENTARIO
				//parcheEspecial(dd);
			}
			if(d.getBaja()!=null){
				Date today=new Date();
				if(!d.getBaja().after(today))
					d.setActivo(false);
				//if(d.getBaja().before(d.getA))
			}			
			
				
		}
		
		for(DescuentoPorCliente dd:porClientes){
			System.out.println(dd);			
			getDescDao().salvar(dd);
		}
		for(DescuentoPorArticulo dd:porArticulos){
			//getDescuentoPorArticuloDao().salvar(dd);
			parcheEspecial(dd);
		}
		
		return null;
	}
	
	
	
	private void descuentosPorFamilia(final DescuentoPorArticulo dd,final String familia,final double min,final double max){
		
			getDao().getHibernateTemplate().execute(new HibernateCallback(){

				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					String hql="from Articulo a left join fetch a.familia f where f.clave in(?,?,?,?) and a.gramos between ? and ? and a.estado=?";
					
					List<Articulo> articulos=session.createQuery(hql)
					.setString(0, familia)					
					.setDouble(4, min)
					.setDouble(5, max)
					.list();
					
					for(Articulo a:articulos){
						DescuentoPorArticulo desc=new DescuentoPorArticulo();
						desc.setArticulo(a);
						desc.setFamilia(a.getFamilia());
						desc.setPrecioNeto(false);
						desc.setClaveArticulo(a.getClave());
						desc.setClaveFamilia(a.getFamilia().getClave());
						desc.setGramMin(90);
						desc.setGramMax(400);
						desc.setFechaAutorizacion(new Date());						
						session.saveOrUpdate(desc);
					}
					return null;
				}
				
			});
		
	}
	
	/**
	 * ELIMINAR
	 *
	 */
	private void parcheEspecial(final DescuentoPorArticulo d){
		String s=d.getDescSiipap();
		
		if(s.equals("COUCH")){
			final String[] cuches={"0104010000","0104020000","0109010000","0109020000"};
			getDao().getHibernateTemplate().execute(new HibernateCallback(){

				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					String hql="from Articulo a left join fetch a.familia f where f.clave in(?,?,?,?) and a.gramos between ? and ? and a.estado=?";
					
					List<Articulo> articulos=session.createQuery(hql)
					.setString(0, cuches[0])
					.setString(1, cuches[1])
					.setString(2, cuches[2])
					.setString(3, cuches[3])					
					.setDouble(4, 90)
					.setDouble(5, 400)
					.setParameter(6, Articulo.Estado.getEstado("A"),Hibernate.custom(ArticuloEstadoUserType.class))
					.list();
					
					for(Articulo a:articulos){
						DescuentoPorArticulo desc=new DescuentoPorArticulo();
						BeanUtils.copyProperties(d, desc);
						desc.setArticulo(a);
						desc.setFamilia(a.getFamilia());
						desc.setPrecioNeto(false);
						desc.setClaveArticulo(a.getClave());
						desc.setClaveFamilia(a.getFamilia().getClave());
						desc.setGramMin(90);
						desc.setGramMax(400);
						desc.setFechaAutorizacion(new Date());
						desc.calcularPrecioKiloEnFuncionDeDescuento();						
						session.saveOrUpdate(desc);
					}
					return null;
				}
				
			});
			
		}
	}

	public List<ReplicaLog> validar(Periodo periodo) {
		
		return null;
	}

	public void bulkImport(Periodo p) {
		//No soportado
	}
	
	@Override
	public void persistir(Object bean) {		
		getDescDao().salvar((DescuentoPorCliente)bean);
	}
	
	private DescuentoPorClienteDao getDescDao(){
		return (DescuentoPorClienteDao)getDao();
	}
	
	
	private List<AbstractDescuento> cargarDescuentos(Periodo p){
		//Importar los descuentos por credito
		
		String sql="select * from TADDESCT";
		DefaultMapper mapper=new DefaultMapper();
		mapper.setBeanClass(getBeanClass());
		mapper.setPropertyColumnMap(getPropertyColumnMap());
		List<AbstractDescuento> desc=getFactory().getJdbcTemplate(p).query(sql,mapper);
		//injectYearMonth(p, desc);
		return desc;
	}
	
	
	public ArticuloDao getArticuloDao() {
		return articuloDao;
	}

	public void setArticuloDao(ArticuloDao articuloDao) {
		this.articuloDao = articuloDao;
	}

	public ClienteDao getClienteDao() {
		return clienteDao;
	}
	
	

	public FamiliaDao getFamiliaDao() {
		return familiaDao;
	}

	public void setFamiliaDao(FamiliaDao familiaDao) {
		this.familiaDao = familiaDao;
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



	public static void main(String[] args) {
		Replicador r=ServiceManager.instance().getReplicador(Replicadores.DescuentosPorCliente);		
		r.importar(new Periodo("01/02/2007","01/04/2007"));
	}
	

}
