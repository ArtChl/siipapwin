package com.luxsoft.siipap.maquila.manager;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.luxsoft.siipap.maquila.dao.RecepcionDeMaterialDao;
import com.luxsoft.siipap.maquila.dao.SalidaDeMaterialDao;
import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.InventarioMaq;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;


/**
 * Implementación de {@link MaquilaManager}
 * @author RUBEN
 *
 */
@SuppressWarnings("unchecked")
public class MaquilaManagerImpl extends HibernateDaoSupport implements MaquilaManager{
	
	private RecepcionDeMaterialDao recepcionDeMaterialDao;
	private SalidaDeMaterialDao salidaDeMaterialDao;
	
	
	/**
	 * 
	 * Genera el inventario de maquila para la entrada id (MOVIMIENTO_ID) especificadao
	 * 
	 * @param entradaId
	 * @param fecha
	 * @return
	 */
	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public InventarioMaq cargarInventario(final Long entradaId,final Date fecha){
		EntradaDeMaterial e=(EntradaDeMaterial)getHibernateTemplate().get(EntradaDeMaterial.class, entradaId);
		return cargarInventario(e, fecha);
	}

	@Transactional (propagation=Propagation.REQUIRED, readOnly=false)
	public InventarioMaq cargarInventario(final EntradaDeMaterial entrada,final Date fecha) {
		if(logger.isDebugEnabled()){
			logger.debug("Analizando historia de entrada:"+entrada.getId()+" Hasta la fecha: "+fecha);
		}
		final InventarioMaq inv=new InventarioMaq();
		inv.setEntrada(entrada);
		inv.setFecha(fecha);
		getHibernateTemplate().execute(new HibernateCallback(){

			
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				final List<SalidaACorte> cortes=session.createQuery(
						"from SalidaACorte s " +
						" left join fetch s.orden o" +
						" where s.entrada=:entrada " )						
						.setEntity("entrada", entrada)						
						.list();
				inv.setCortes(cortes);
				final List<EntradaDeHojas> entradash=session.createQuery(
						"from EntradaDeHojas e " +
						"  where e.origen.entrada=:entrada")
						.setEntity("entrada", entrada)						
						.list();
				inv.setEntradasDeHojas(entradash);
				final List<SalidaDeHojas> salidah=session.createQuery(
						"from SalidaDeHojas s " +
						" left join fetch s.destino d " +
						"  where s.origen.origen.entrada=:entrada")
						.setEntity("entrada", entrada)						
						.list();
				inv.setSalidaDeHojas(salidah);
				return null;
			}
			
		});
		return inv;
	}

	public List<EntradaDeMaterial> buscarEntradasDeMaterial() {
		return getRecepcionDeMaterialDao().buscarEntradas();
	}
	
	public List<SalidaACorte> buscarSalidasACorte() {
		return getSalidaDeMaterialDao().buscarSalidas();
	}
	
	public List<SalidaDeBobinas> buscarSalidasDeBobina() {
		return getSalidaDeMaterialDao().buscarSalidasDeBobinas();
	}
	
	public List<SalidaDeMaterial> buscarSalidasDeMaterial() {
		return getSalidaDeMaterialDao().buscarSalidasDeMaterial();
	}
	
	/**
	 * Regresa una lista unica de todas las SalidaDeHojas existentes
	 * 
	 * @return
	 */
	public List<SalidaDeHojas> buscarSalidaDeHojas(){
		final String hql="from SalidaDeHojas s" +				
		" left join fetch s.origen o" +
		" left join fetch o.origen eh" +
		" left join fetch eh.entrada em " +
		" left join fetch s.destino d";
		List<SalidaDeHojas>  list=getHibernateTemplate().find(hql);		
		return list;
	}
	
	public List<EntradaDeHojas> buscarEntradasDeHojas() {
		final String hql="from EntradaDeHojas s" +
		" left join fetch s.origen eh" +
		" left join fetch eh.entrada em " ;
		List<EntradaDeHojas>  list=getHibernateTemplate().find(hql);		
		return list;
	}

	/**** Dependency injected collaborators ****/

	public RecepcionDeMaterialDao getRecepcionDeMaterialDao() {
		return recepcionDeMaterialDao;
	}
	public void setRecepcionDeMaterialDao(
			RecepcionDeMaterialDao recepcionDeMaterialDao) {
		this.recepcionDeMaterialDao = recepcionDeMaterialDao;
	}
	
	public SalidaDeMaterialDao getSalidaDeMaterialDao() {
		return salidaDeMaterialDao;
	}
	public void setSalidaDeMaterialDao(SalidaDeMaterialDao salidaDeMaterialDao) {
		this.salidaDeMaterialDao = salidaDeMaterialDao;
	}

	public static void main(String[] args) {
		/**
		RecepcionDeMaterialDao dao=(RecepcionDeMaterialDao)ServiceLocator.getDaoContext().getBean("recepcionDeMaterialDao");
		EntradaDeMaterial e=dao.buscarEntrada(9691L);
		
		InventarioMaq maq=ServiceLocator.getMaquilaManager().cargarInventario(e, DateUtils.obtenerFecha("23/08/2007"));
		System.out.println("Maq: "+maq);		
		//ServiceLocator.manager
		 
		 **/
		ServiceLocator.getMaquilaManager().buscarSalidaDeHojas();
		
	}

	

	

	
}
