package com.luxsoft.siipap.swing.form;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.luxsoft.siipap.domain.ZonaPostal;
import com.luxsoft.siipap.services.ServiceLocator;

public class DireccionUtils {
	
	private static Logger logger=Logger.getLogger(DireccionUtils.class);
	
	public static List<String> cargarEstados(){
		return cargarArchivo("estados.txt");
	}
	
	public static List<String> cargarCiudades(){
		return cargarArchivo("ciudades.txt");
	}
	public static List<String> cargarMunicipios(){
		return cargarArchivo("municipios.txt");
	}
	public static List<String> cargarColoniad(){
		return cargarArchivo("colonias.txt");
	}
	
	public static List<String> cargarArchivo(String file){
		List<String> datos=new ArrayList<String>();
		//try {
			InputStream in=null;
			BufferedReader buff=null;
			try {
				in=Thread.currentThread().getContextClassLoader().getResourceAsStream(file);				
				InputStreamReader r=new InputStreamReader(in);			
				buff=new BufferedReader(r);
				String line;				 
				while ((line = buff.readLine()) != null) {
					datos.add(line);
				}				
			} catch (Exception e) {			
				e.printStackTrace();
				
			}finally{
				try {
					if(buff!=null) buff.close();
					if(in!=null) in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}		
		return datos;
	}
	
	@SuppressWarnings("unchecked")
	public static List<ZonaPostal> cargarZonas(String cp){
		SessionFactory sf=ServiceLocator.getSessionFactory();
		HibernateTemplate tm=new HibernateTemplate(sf);
		List<ZonaPostal> zonas=tm.find("from ZonaPostal z where z.cp=?", cp);
		if(zonas.isEmpty()){
			return null;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Zonas encontradas:"+zonas);
		}
		return zonas;
	}

}
