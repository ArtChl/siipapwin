package com.luxsoft.siipap.em.replica.cmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.springframework.util.Assert;

import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.em.replica.Replicadores;
import com.luxsoft.siipap.em.replica.TablasReplicables;
import com.luxsoft.siipap.em.replica.devo.VinculacionDeDevoluciones;
import com.luxsoft.siipap.em.replica.service.ServiceManager;

/**
 * Interfas de linea de comando para la ejecucion de tareas de replica 
 * 
 * @author Ruben Cancino
 *
 */
public class Commander {
	
	//private static String[] TAREAS={"QUIT","IMPORTAR","VINCULAR","REPLICAR"};
	private static String[] TAREAS={"REPLICAR"};
	
	private static DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
	
	
	
	public static void importarCarter(Periodo p){
		ServiceManager.instance().getCarterReplicator().bulkImport(p);
	}
	public static void importarPagcre(Periodo p){
		ServiceManager.instance().getPagcreReplicator().bulkImport(p);
	}
	public static void importarDdocre(Periodo p){
		ServiceManager.instance().getDdocreReplicator().bulkImport(p);
	}
	
	
	
	public static void main(String[] args) {
		
		while(true){
			String s=prompt("Seleccione la tarea. Validos:  "+Arrays.toString(TAREAS));
			/*
			if(s.equalsIgnoreCase(TAREAS[0])){
				exit();
			}else if(s.equalsIgnoreCase(TAREAS[1])){				
				importarTabla();
			}else if(s.equalsIgnoreCase(TAREAS[2])){				
				vincularTabla();
			}else if(s.equalsIgnoreCase(TAREAS[3])){				
				replicarPeriodo();
			}else{
				System.out.println("Fin de sesion");
				System.out.println("Tarea invalida: "+s);
			}
			*/
			if(s.equalsIgnoreCase(TAREAS[0]))
				replicarPeriodo();
		}
		
		
	}
	
	public static Periodo obtenerPeriodo(){
		try {
			Date f1=df.parse(prompt("Fecha Inicial:  "));
			Date f2=df.parse(prompt("Fecha Final:    "));
			Assert.isTrue(f1.getTime()<=f2.getTime(),"La fecha inicial no puede ser mayor a "+df.format(f2));
			return new Periodo(f1,f2);
		} catch (Exception e) {
			System.out.println("Fecha Incorrecta :"+e.getMessage());
			String s=prompt("Desea terminar ?");
			if(s.startsWith("s"))
				exit();
			return obtenerPeriodo();
		}
	}
	
	public static String obtenerTabla(){
		//String tabla=prompt("Seleccione la tabla ["+Arrays.toString(TABLAS)+"]");
		String tabla=prompt("Seleccione la tabla ["+Arrays.toString(TablasReplicables.values())+"]");
		return tabla;
		
	}
	
	public static void importarTabla(){
		Periodo p=obtenerPeriodo();
		System.out.println(" Importando : "+p);
		String tabla=obtenerTabla();
		try {
			if(tabla.equalsIgnoreCase(TablasReplicables.VENTAS.toString())){
				ServiceManager.instance().getReplicador(Replicadores.VentasReplicator).bulkImport(p);
				exit();
			}			
			else if(tabla.equalsIgnoreCase(TablasReplicables.VENTASDET.toString())){
				//ServiceManager.instance().getReplicador(Replicadores.VentasReplicator).bulkImport(p);
				ServiceManager.instance().getReplicador(Replicadores.VentasDetReplicator).bulkImport(p);
				exit();
			}			
			else if(tabla.equalsIgnoreCase(TablasReplicables.NOTAS.toString())){
				ServiceManager.instance().getReplicador(Replicadores.NotasDeCreditoReplicator).bulkImport(p);
				exit();
			}			
			else if(tabla.equalsIgnoreCase(TablasReplicables.NOTASDET.toString())){
				ServiceManager.instance().getReplicador(Replicadores.NotasDeCreditoDetReplicator).bulkImport(p);
				exit();
			}			
			else if(tabla.equalsIgnoreCase(TablasReplicables.PAGCRE.toString())){
				importarPagcre(p);
				exit();
			}
			
			else if(tabla.equalsIgnoreCase(TablasReplicables.CARTER.toString())){
				importarCarter(p);
				exit();
			}
			
			else if(tabla.equalsIgnoreCase(TablasReplicables.DDOCRE.toString())){
				importarDdocre(p);
				exit();
				
			}else if(tabla.equalsIgnoreCase(TablasReplicables.MOVCHE.toString())){
				ServiceManager.instance().getReplicador(Replicadores.ChequesDevueltosReplicator).bulkImport(p);
				exit();
				
			}else if(tabla.equalsIgnoreCase(TablasReplicables.MOVJUR.toString())){
				ServiceManager.instance().getReplicador(Replicadores.JuridicoReplicator).bulkImport(p);
				exit();
				
			}else if(tabla.equalsIgnoreCase(TablasReplicables.PAGOS.toString())){
				ServiceManager.instance().getReplicador(Replicadores.PagosReplicator).bulkImport(p);
				exit();
				
			}else
				Assert.isTrue(false,"No implementado");
				
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al importar: "+e.getMessage());
			
			String s=prompt("Desea terminar ?");
			if(s.startsWith("s"))
				exit();
			importarTabla();
		}
		
		exit();
	}
	
	public static void vincularTabla(){
		Periodo p=obtenerPeriodo();
		System.out.println(" Vinculando para el periodo: "+p);
		String tabla=obtenerTabla();
		try {
			if(tabla.equalsIgnoreCase(TablasReplicables.DEVO.toString())){				
				VinculacionDeDevoluciones.vincular(p);
				exit();
			}			
			else
				Assert.isTrue(false,"No implementado");
				
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al vincular: "+e.getMessage());
			exit();
		}		
		exit();
	}
	
	private static void replicarPeriodo(){
		Periodo p=obtenerPeriodo();
		System.out.println(" Replicanjdo toda la informacion del periodo: "+p);
		ServiceManager.instance().getReplicationManager().replicar(p);
	}
	
	private static void exit(){
		System.exit(0);
	}
	
	public static String prompt(String msg){
		if(msg==null || msg.length()!=0){
			System.out.println(msg.trim()+ "");
		}
		String s="";
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		try{
			s=br.readLine();
		}catch (Exception e) {
			
		}
		return s;
	}

}
