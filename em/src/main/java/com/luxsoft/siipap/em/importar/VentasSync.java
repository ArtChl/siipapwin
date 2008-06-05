package com.luxsoft.siipap.em.importar;

import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.services.ServicesUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.dao.VentasDao;
import com.luxsoft.siipap.ventas.domain.Venta;
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
	private PropertyChangeSupport propertySupport=new PropertyChangeSupport(this);
	private VentasSupport support;
	private ImportadorDeVentas importadorDeVentas;
	private VentasDao ventasDao;
	private VentasManager ventasManager;
	private EventList<Venta> buffer;
	private Date dia;
	private DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
	
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
		logger.info("\n\t\tDB: "+ServicesUtils.getDataBaseLocationInfo()+"\n");
		logger.info("Sincronizando ventas :"+TaskUtils.getHora()+" para: "+dia);
		siipapToWin(dia);
		winToSiipap(dia);
			
	}
	
	private void siipapToWin(final Date dia){
		logger.info("Procesando faltantes en SiipapWin.......");
		int year=Periodo.obtenerYear(dia);
		String target="H:\\"+year;
		SafeCopia.execute("G:\\SIIPAP\\ARCHIVOS",target);
		SafeCopia.execute("G:\\SIIPAP\\ARCHIVOS\\DATO2008",target);
		List<Venta> faltantes=new ArrayList<Venta>(faltantes(dia));
		
		///Buscar los clientes nuevos y persistirlos
		CollectionUtils.forAllDo(faltantes, new Closure(){
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
		if(!faltantes.isEmpty()){
			int ok=0;
			for(Venta v:faltantes){
				persistirVenta(v);
				ok++;
			}
			logger.info("Ventas persistidas exitosamente: "+ok+"\t Hora:"+TaskUtils.getHora());
		}
		logger.info("No hay ventas por sincronizar");
		
	}
	
	private void winToSiipap(final Date dia){
		logger.info("Procesando Sobrantes (Borrados) en SiipapWin.......");
		List<Venta> sobrantes=new ArrayList<Venta>(sobrantes(dia));
		if(!sobrantes.isEmpty()){
			int ok=0;
			for(Venta v:sobrantes){
				eliminarVenta(v);
				ok++;
			}
			logger.info("Ventas eliminada exitosamente: "+ok+"\t Hora:"+TaskUtils.getHora());
		}
		logger.info("No hay ventas por eliminar");
	}
	
	/**
	 * Salva las ventas en siipapw
	 * @param v
	 */
	public void persistirVenta(final Venta v){
		try {
			getImportadorDeVentas().importarVenta(v);
			getVentasManager().actualizarVenta(v);
			buffer.add(v);
			if(logger.isDebugEnabled()){
				logger.debug("Venta existosamente importada: "+v.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	/**
	 * Elimina una venta en siipapwin
	 * 
	 * @param v
	 */
	private void eliminarVenta(final Venta v){
		try {			
			getVentasManager().eliminarVenta(v.getId());
			buffer.remove(v);
			if(logger.isDebugEnabled()){
				logger.debug("Venta existosamente eliminada: "+v.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}
	
	/**
	 * Busca las ventas faltantes en siipapw
	 * @param dia
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<Venta> faltantes(final Date dia){
		if(this.dia!=dia){
			//getVentas();//Inicializar el buffer			
			setDia(dia);
		}
		List<Venta> siipap=getSupport().buscarVentasEnSiipap(dia);
		logger.info("Ventas DBF: "+siipap.size());
		List<Venta> win=getVentas();
		logger.info("Ventas beans: "+win.size());
		Collection<Venta> c=CollectionUtils.subtract(siipap, win);
		logger.info("Faltantes: "+c.size());
		return c;
	}
	
	/**
	 * Busca las ventas eliminadas en siipap
	 * 
	 * @param dia
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Collection<Venta> sobrantes(final Date dia){
		List<Venta> win=getVentas();
		logger.info("Ventas beans: "+win.size());
		
		List<Venta> siipap=getSupport().buscarVentasEnSiipap(dia);
		logger.info("Ventas DBF: "+siipap.size());
		
		Collection<Venta> c=CollectionUtils.subtract(win,siipap);
		logger.info("Sobrantes: "+c.size());
		return c;
	}
	
	
	/**
	 * Cargas las ventas actuales en el dia de siipapw en un buffer
	 * para reducir el acceso a la base de datos
	 * 
	 *
	 */
	private void loadBuffer(){
		buffer.clear();
		buffer.addAll(getVentasDao().buscarVentas(dia));
	}
	
	/**
	 * Ventas del dia en siipapwin
	 * @return
	 */
	private List<Venta> getVentas(){
		if(buffer==null){
			buffer=GlazedLists.threadSafeList(new BasicEventList<Venta>());
			loadBuffer();
		}
		return buffer;
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

	public Date getDia() {
		return dia;
	}

	public void setDia(Date dia) {
		Object old=this.dia;
		this.dia = dia;
		propertySupport.firePropertyChange("dia", old, dia);
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
