package com.luxsoft.siipap.clipper.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.luxsoft.siipap.clipper.domain.Mvalma;
import com.luxsoft.siipap.dao.GenericDao;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.inventarios.domain.Movimiento;

/**
 * DAO para instancias de 
 * 
 * @author Ruben Cancino
 *
 */
public interface MvalmaDao extends GenericDao<Mvalma,Long>{
	
	/**
	 * Enumeracion para definir los tipos en Almace que tienen como
	 * mestro una instancia de Mvalma
	 * 
	 * @author Ruben Cancino
	 *
	 */
	public enum ALMTIPOS{
		AJA("AJA"),
		AJU("AJU"),
		CIM("CIM"),
		CIS("CIS"),
		MER("MER"),
		OIM("OIM"),
		RAU("RAU"),
		REC("REC"),
		REF("REF"),
		REM("REM"),
		RMC("RMC"),
		RMD("RMD"),
		TPE("TPE"),
		TPS("TPS"),
		TRS("TRS"),
		TRV("TRV"),
		VIR("VIR"),
		XCO("XCO"),
		XOI("XOI"),
		XRM("XRM");
		
		private final String id;
		
		private ALMTIPOS(String key){
			id=key;
		}
		public String getId(){
			return id;
		}
		public String toString(){
			return id;
		}
		
		public static List<String> tipos(){
			List<String> tipos=new ArrayList<String>();
			for(ALMTIPOS tip:values()){
				tipos.add(tip.getId());
			}
			return tipos;
		}
	}
	
	/**
	 * Busca las instancias de Mvalma para la fecha 
	 * especificada
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Mvalma> buscarPorFecha(final Date fecha);
	
	public List<Mvalma> buscar(final Periodo p,String tipo);
	
	/**
	 * Busca los movimientos pendientes de asignar
	 * maestro de tipo MVALMA
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Movimiento> buscarPendientes(final Date fecha);
	
	/**
	 * Busca todos los movimientos pendientes de asignar mvalma
	 * @return
	 */
	public List<Movimiento> buscarPendientes();
	
	
	/**
	 * Vincula todos los TPS con TPE
	 * 
	 * @param fecha
	 * @return
	 */
	public void enlazarTraslados(final Date fecha);
	
	/**
	 * Busca los movimientos TPE y TPS 
	 * 
	 * @param fecha
	 * @return
	 */
	public List<Movimiento> buscarTraslados(final Date fecha);
	
	public Mvalma buscarDocumento(final Integer sucursal,final String tipo,final Long numero);

}
