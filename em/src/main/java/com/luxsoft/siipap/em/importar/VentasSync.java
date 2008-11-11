package com.luxsoft.siipap.em.importar;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Se encarga de buscar ventas existentes en siipap
 * y si no existen en siipapw las importa y persiste
 * 
 * @author Ruben Cancino
 *
 */
public class VentasSync {
	
	private Logger logger=Logger.getLogger(getClass());
	
	private VentasSupport support;
	private ImportadorDeVentas importadorDeVentas;
	private VentasDao ventasDao;
	private VentasManager ventasManager;
	
	public void sincronizar(){
		sinconizar(new Date());
	}
	
	/**
	 * Sincroniza las ventas de dia importando las faltantes de siipap
	 * y persistiendolas
	 * 
	 * @param dia
	 */
	public void sinconizar(final Date dia){
		importarVentas(dia);
		eliminarVentasSobrantes(dia);
			
	}
	
	private void importarVentas(final Date dia){
		logger.info("Importando ventas a SiipapWin");
		final List<Venta> ventas=this.support.buscarVentasEnSiipap(dia);		
		actualizarClientesCredito(ventas);
		if(!ventas.isEmpty()){
			for(Venta v:ventas){
				try {					
					Venta source=getImportadorDeVentas().importarVenta(v);
					Venta target=getVentasManager().getVentasDao().buscarVenta(source.getSucursal(), source.getSerie(),source.getTipo(),source.getNumero());
					if(target!=null){
						support.copyVenta(source, target);
						getVentasManager().getVentasDao().salvar(target);
						logger.info("Venta ACTUALIZADA..."+target.getId());
					}else{
						getVentasManager().actualizarVenta(source);
						logger.info("Venta GENERADA......: "+source.getId());
					}
				} catch (Exception e) {
					logger.error("No se pudo salvar/actualizar la venta: "+v.getId(),e);
				}
			}
		}
		logger.info("No hay ventas por importar");
	}
	
	private void actualizarClientesCredito(final List<Venta> ventas){
		CollectionUtils.forAllDo(ventas, new Closure(){
			public void execute(Object input) {
				Venta v=(Venta)input;
				Cliente c=v.getCliente();
				if(c!=null && c.getId()==null){
					//Cliente nuevo
					if(v.getOrigen().equals("CRE")){
						c.generarCredito();
						c.getCredito().copyFromCliente();
					}
					ServiceLocator.getClienteDao().salvar(c);
				}
			}			
		});
	}
	
	private void eliminarVentasSobrantes(final Date dia){
		logger.info("Procesando Sobrantes (Borrados) en SiipapWin.......");
		Collection<Venta> sobrantes=sobrantes(dia);
		if(!sobrantes.isEmpty()){
			for(Venta v:sobrantes){
				try {
					eliminarVenta(v);
				} catch (Exception e) {
					logger.error("No se pudo eliminar la venta: "+v.getId(),e);
					continue;
				}
				
			}
		}
		
	}
	
	
	
	/**
	 * Elimina una venta en siipapwin
	 * 
	 * @param v
	 */
	private void eliminarVenta(final Venta v){
		getVentasManager().eliminarVenta(v.getId());
		logger.debug("SW - Venta eliminada: "+v.getId());
	}
	
	
	/**
	 * Busca las ventas eliminadas en siipap
	 * 
	 * @param dia
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<Venta> sobrantes(final Date dia){
		List<Venta> win=buscarVentasEnSW(dia);
		List<Venta> siipap=getSupport().buscarVentasEnSiipap(dia);
		Collection<Venta> c=CollectionUtils.subtract(win,siipap);
		logger.info("Ventas sobrantes (No existentes en siipap DBF: "+c.size());
		return c;
	}
	
	/**
	 * Busca las ventas sobrantes del dia
	 * 
	 * @param dia
	 * @return
	 */
	public  List<Venta> buscarVentasEnSW(Date dia){
		List<Venta> ventas=getVentasDao().buscarVentas(dia);
		String pattern="Ventas end SW2 para {0} :{1}";
		logger.info(MessageFormat.format(pattern, dia,ventas.size()));
		return ventas;
	}
	
	/**
	 * Comodity para localizar las ventas en siipap dbf
	 * 
	 * @param dia
	 * @return
	 */
	public List<Venta> buscarVentasEnSiipap(final Date dia){
		return support.buscarVentasEnSiipap(dia);
	}
	
	public List<VentaDet> buscarVentasDetEnSiipap(final Date dia){
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT * FROM ");
		sql.append(ReplicationUtils.resolveTable("ALMACE", dia));		
		sql.append(ReplicationUtils.resolveWherePart(new Periodo(dia), "ALMFECHA"));
		String where=" AND  ALMTIPO=\'FAC\'";
		sql.append(where);
		if(logger.isDebugEnabled()){
			logger.debug("Query generado: "+sql.toString());
		}
		System.out.println("Det SQL: "+sql.toString());
		List<VentaDet> res= getSupport().getFactory().getJdbcTemplate().query(sql.toString(),getImportadorDeVentas().getPartidasMapper() );
		System.out.println("Res: "+res.size());
		return res;
	}
	

	public VentasSupport getSupport() {
		return support;
	}

	public void setSupport(VentasSupport support) {
		this.support = support;
	}

	public VentasDao getVentasDao() {
		return ventasDao;
	}

	public void setVentasDao(VentasDao ventasDao) {
		this.ventasDao = ventasDao;
	}	
	

	public ImportadorDeVentas getImportadorDeVentas() {
		return importadorDeVentas;
	}

	public void setImportadorDeVentas(ImportadorDeVentas importadorDeVentas) {
		this.importadorDeVentas = importadorDeVentas;
	}

	public VentasManager getVentasManager() {
		return ventasManager;
	}

	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}
	
}
