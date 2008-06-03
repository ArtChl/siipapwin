package com.luxsoft.siipap.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.luxsoft.siipap.domain.ZonaPostal;

public class ImportadorDeCodigosPostales {
	
	private HibernateTemplate template;
	
	public void salvar(ZonaPostal z){
		if(template==null){
			SessionFactory sf=ServiceLocator.getSessionFactory();
			template=new HibernateTemplate(sf);
		}
		template.saveOrUpdate(z);
	}
	
	private void borrar(){
		if(template==null){
			SessionFactory sf=ServiceLocator.getSessionFactory();
			template=new HibernateTemplate(sf);
		}
		template.execute(new HibernateCallback(){

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.createQuery("delete ZonaPostal z").executeUpdate();
				return null;
			}
			
		});
	}
	
	public ImportadorDeCodigosPostales importar() throws IOException{
		//borrar();
		InputStream in=null;
		BufferedReader buff=null;		
		PrintWriter estadoWriter=null;
		PrintWriter ciudadWriter=null;
		PrintWriter mpoWriter=null;
		try {
			in=new FileInputStream("C:\\luxsoftnet\\ZIPCODES.TXT");
			final Charset cs=Charset.forName("UTF-8");
			InputStreamReader r=new InputStreamReader(in,cs);			
			buff=new BufferedReader(r);
			
			estadoWriter=new PrintWriter(new FileWriter("src/main/resources/estados.txt"));			
			final Set<String> estados=new TreeSet<String>();
			
			ciudadWriter=new PrintWriter(new FileWriter("src/main/resources/ciudades.txt"));
			final Set<String> ciudades=new TreeSet<String>();
			
			mpoWriter=new PrintWriter(new FileWriter("src/main/resources/municipios.txt"));
			final Set<String> mpos=new TreeSet<String>();
			
			String line;
			int row=0;
			int lzip=0;
			int lcolonia=0;
			int lmpo=0;
			
			int lestado=0;
			int lciudad=0;
			 
			while ((line = buff.readLine()) != null) {
								
				line=line.replace('|', ':');				
				String data[]=line.split(":");
				
				String zip=data[0];
				if(zip.length()>lzip)lzip=zip.length();
				
				String colonia=data[1];
				if(colonia.length()>lcolonia)lcolonia=colonia.length();
				
				String mpo=data[3];
				mpos.add(mpo);
				if(mpo.length()>lmpo)lmpo=mpo.length();
				
				
				String estado=data[4];
				estados.add(estado);
				if(estado.length()>lestado)lestado=estado.length();
				
				String ciudad=data[5];
				ciudades.add(ciudad);
				if(ciudad.length()>lciudad)lciudad=ciudad.length();
				
				//String msg=MessageFormat.format("{0},{1},{2},{3},{4}", zip,colonia,mpo,estado,ciudad);				
				row++;
				//System.out.println("row: "+row+" - "+msg);
				ZonaPostal z=new ZonaPostal(zip,colonia,mpo,estado,ciudad);
				//salvar(z);
			}
			String msg=MessageFormat.format("Zip: {0} Col: {1}  Mpo: {2} Estado: {3} Ciudad: {4}"
					, lzip,lcolonia,lmpo,lestado,lciudad);
			System.out.println(msg);
			
			for(String s:estados){
				if(!StringUtils.isEmpty(s))
					estadoWriter.println(s);
			}
			estadoWriter.flush();
			
			for(String s:ciudades){
				if(!StringUtils.isEmpty(s))
					ciudadWriter.println(s);
			}
			ciudadWriter.flush();
			
			for(String s:mpos){
				if(!StringUtils.isEmpty(s))
					mpoWriter.println(s);
			}
			mpoWriter.flush();
			
			return this;
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
			return this;
		}finally{
			if(buff!=null) buff.close();
			if(in!=null) in.close();
			if(estadoWriter!=null) estadoWriter.close();
			if(ciudadWriter!=null) ciudadWriter.close();
			if(mpoWriter!=null) mpoWriter.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		/*Set<Entry<String, Charset>> s=Charset.availableCharsets().entrySet();
		for(Entry<String, Charset> ss:s){
			System.out.println(ss.getKey()+"  "+ss.getValue());
		}
		System.out.println(Charset.defaultCharset());
		*/
		new ImportadorDeCodigosPostales().importar();
		//String ss="00505|Colonia";
		//System.out.println(ss.split("|")[0]);
	}

}
