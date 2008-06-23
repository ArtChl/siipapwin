package com.luxsoft.siipap.em.importar;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.util.Assert;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.GroupingList;

import com.luxsoft.siipap.cxc.dao.DepositosDao;
import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.dao.SiipapJdbcTemplateFactory;
import com.luxsoft.siipap.em.managers.EMServiceLocator;
import com.luxsoft.siipap.em.replica.ReplicationUtils;
import com.luxsoft.siipap.utils.DateUtils;

public class ImportadorDeDepositos  implements Importador{
	
	private Logger logger=Logger.getLogger(getClass());
	
	protected SiipapJdbcTemplateFactory factory;
	private DepositosDao depositosDao;
	public static String CUENTA="SCOTTIA   (1691945)";
	
	/**
	 * Mapeo entre las cuentas de Tesoreria y los bancos registrados en Siipap DBF
	 */
	private static Map<Integer, String> cuentasMapper=new HashMap<Integer, String>();
	static{
		cuentasMapper.put(1, "BANAMEX   (1858193)");
		cuentasMapper.put(2, "BANCOMER   (116228100)");
		cuentasMapper.put(4, "HSBC	    (4019118074)");
		cuentasMapper.put(32,"SCOTTIA   (1691945)");
		cuentasMapper.put(9, "SANTANDER (92000395043)");
	}

	/**
	 * Genera un grupo de depositos para ser persistidos en oracle
	 */
	public Object[] importar(Object... params) {
		Assert.notEmpty(params,"Se requiere la fecha como parametro");
		final Date fecha=(Date)params[0];
		getDepositosDao().eliminarDepositosImportados(fecha);
		List<Deposito> depositos=new ArrayList<Deposito>();
		List<Deposito> normales=crearDepositos(fecha);
		List<Deposito> autorizados=crearDepositosAutorizados(fecha);
		depositos.addAll(normales);
		depositos.addAll(autorizados);
			
		List<Deposito> write=new ArrayList<Deposito>();
		for(Deposito d:depositos){
			try {
				System.out.println(d);
				Deposito ok=getDepositosDao().save(d);
				write.add(ok);
			} catch (Exception e) {
				logger.error("No se pudo salvar el deposito:\n "+d,e);
				continue;
			}			
		}
		return write.toArray();
	}
	
	@SuppressWarnings("unchecked")
	private List<Deposito> crearDepositos(final Date fecha){
		final String sql="select * from "+ReplicationUtils.resolveTable("DEPOSI", fecha)+ " where DPFECOBRAN=?";		
		SqlParameterValue param1=new SqlParameterValue(Types.DATE,fecha);
		logger.debug("Ejecutando query: "+sql);
		//getFactory().getJdbcTemplate(fecha).query(sql, new Object[]{param1}, rowMapper)
		List<Map> res=getFactory().getJdbcTemplate(fecha).queryForList(sql, new Object[]{param1});
		final EventList source=GlazedLists.eventList(res);
		EventList grupos=new GroupingList(source,new Comparator(){
			public int compare(Object o1, Object o2) {
				
				Map m1=(Map)o1;
				Map m2=(Map)o2;
				Number suc1=(Number)m1.get("DPSUCURSA");
				Number suc2=(Number)m2.get("DPSUCURSA");
				Integer s1=suc1.intValue();
				Integer s2=suc2.intValue();
				if(s1.intValue()!=s2.intValue())
					return s1.compareTo(s2);
				else{
								
					Comparable val1=(Comparable)m1.get("DPCONSECUT");
					Comparable val2=(Comparable)m2.get("DPCONSECUT");
					if(val1==null){
						return -1;
					}else if(val2==null){
						return 1;
					}
					return val1.compareTo(val2);
				}				
			}
			
		});
		
		final List<Deposito> depositos=new ArrayList<Deposito>();
		for(Object grupo:grupos){
			//System.out.println(grupo.getClass().getName());
			final List g=(List)grupo;
			System.out.println(" Procesando grupo.......");
			Deposito dep=new Deposito();
			dep.setCuentaDestino(CUENTA);
			dep.setCuenta(CUENTA);
			
			double importe=0;
			for(Object row:g){
				Map reg=(Map)row;
				Object v=reg.get("DPTIPO");
				if(v==null)
					continue;
				final FormaDePago fp=FormaDePago.valueOf(v.toString());
				dep.setFormaDePago(fp);
				dep.setSucursalId( ((Number)reg.get("DPSUCURSA")).intValue());
				dep.setOrigen(reg.get("DPDDEPOSIT").toString());
				Number depVal=((Number)reg.get("DPDETIMP"));				
				importe+=( depVal!=null?depVal.doubleValue():0);
				dep.setFecha((Date)reg.get("DPFECOBRAN"));
				Number fol=((Number)reg.get("DPCONSECUT"));
				if(fol!=null){
					dep.setFolio(fol.intValue());	
				}
				//dep.setSucursal(Sucursales.v)
			}
			dep.setImporte(CantidadMonetaria.pesos(importe));
			dep.resolverCuenta();
			System.out.println("Deposito generado: "+dep);
			depositos.add(dep);

		}
		return depositos;
	}
	
	@SuppressWarnings("unchecked")
	private List<Deposito> crearDepositosAutorizados(final Date fecha){
		String sql=ReplicationUtils.resolveSQL(new Periodo(fecha), "AUTDEP", "DEPFECAUT");
		List<Map<String, Object>> rows=getFactory().getJdbcTemplate(fecha).queryForList(sql);
		final List<Deposito> depositos=new ArrayList<Deposito>();
		
		for(Map<String, Object> reg:rows){
			Deposito dep=new Deposito();
			
			Number banco=(Number)reg.get("DEPCVEBAN");
			String cuentaId=cuentasMapper.get(banco.intValue());
			dep.setCuentaDestino(cuentaId);
			
			double importe=0;
			Number imp=(Number)reg.get("DEPIMPDEP");
			if(imp!=null)
				importe=imp.doubleValue();
			dep.setImporte(CantidadMonetaria.pesos(importe));
			
			String fpv=(String)reg.get("DEPFORMDEP");			
			FormaDePago fp=FormaDePago.valueOf(fpv);
			if(fp.equals(FormaDePago.T))
				fp=FormaDePago.N;
			else
				fp=FormaDePago.O;
			dep.setFormaDePago(fp);
			
			Number sucursalId=(Number)reg.get("DEPSUCURSA");
			dep.setSucursalId( sucursalId.intValue());
			dep.setOrigen("CON");
							
			Date f=(Date)reg.get("DEPFECAUT");
			dep.setFecha(f);
			
			Number fol=(Number)reg.get("DEPNUMERO");
			if(fol!=null){
				dep.setFolio(fol.intValue());	
			}			
			dep.resolverCuenta();
			depositos.add(dep);
		}
		
		return depositos;
	}
	
	public DepositosDao getDepositosDao() {
		return depositosDao;
	}
	public void setDepositosDao(DepositosDao depositosDao) {
		this.depositosDao = depositosDao;
	}

	public SiipapJdbcTemplateFactory getFactory() {
		return factory;
	}

	public void setFactory(SiipapJdbcTemplateFactory factory) {
		this.factory = factory;
	}
	
	
	public static void main(String[] args) {
		ImportadorDeDepositos imp=(ImportadorDeDepositos)EMServiceLocator.instance().getContext().getBean("importadorDeDepositos");
		imp.importar(DateUtils.obtenerFecha("31/05/2008"));
	}
	
	

}
