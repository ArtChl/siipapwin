package com.luxsoft.siipap.inventarios.services;

import java.sql.Types;
import java.util.List;

import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.utils.DateUtils;

public class ParcheParaActualizarPonderoza {
	
	
	public void execute(){
		String sql="SELECT DISTINCT CLAVE FROM V_COMS X WHERE PROVCLAVE=? AND FENT >?";
		Object[] vals=new Object[]{"G012",DateUtils.obtenerFecha("31/03/2008")};
		int[] types={Types.VARCHAR,Types.DATE};
		List<String> claves=ServiceLocator.getJdbcTemplate().queryForList(sql,vals,types,String.class);
		InventariosManager manager=ServiceLocator.getInventariosManager();
		System.out.println("A procesar:"+claves.size());
		for(String clave:claves){
			System.out.println("\nProcesando: "+clave);
			for(int i=4;i<=12;i++){				
				manager.actualizarInventario(2008, i, clave);
				//System.out.print(","+i);
			}
		}
	}
	
	public static void main(String[] args) {
		new ParcheParaActualizarPonderoza().execute();
	}

}
