package com.luxsoft.siipap.inventarios.utils;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.maquila.domain.EntradaDeHojas;
import com.luxsoft.siipap.maquila.domain.EntradaDeMaterial;
import com.luxsoft.siipap.maquila.domain.SalidaACorte;
import com.luxsoft.siipap.maquila.domain.SalidaDeBobinas;
import com.luxsoft.siipap.maquila.domain.SalidaDeHojas;
import com.luxsoft.siipap.maquila.domain.SalidaDeMaterial;

/**
 * 
 * Metodos utilitarios para la UI de Inventarios
 * 
 * @author Ruben Cancino
 *
 */
public class InventarioUtils {
	
	/**
	 * Regresa un {@link TableFormat}  estandar para el bean {@link EntradaDeMaterial}
	 * 
 	 * @return
	 */
	public static TableFormat<EntradaDeMaterial> getStandardTableFormat(){
		final String[] cols={"id","entradaDeMaquilador","articulo.clave","articulo.descripcion1","fecha","kilos","metros2","factorDeConversion"
				,"precioPorM2","precioPorKilo","importe","trasladoOrigen.id","disponibleKilos"};
		final String[] labels={"Id","Entrada Maq","Bobina","Descripción","Fecha","Kilos","M2","Factor"
				,"Precio M2","Precio Kg","Importe","Origen","Saldo Kg"};
		final TableFormat<EntradaDeMaterial> tf=GlazedLists.tableFormat(EntradaDeMaterial.class, cols, labels);
		return tf;
	}
	
	
	/**
	 * Regresa un {@link TableFormat}  estandar para el bean {@link SalidaACorte}
	 * 
 	 * @return
	 */
	public static TableFormat<SalidaACorte> getSalidaACorteTF(){
		final String[] cols={
				"id"
				,"entrada.id"
				,"entrada.entradaDeMaquilador"
				,"orden.id"
				,"orden.fecha"
				,"fecha"
				,"articulo.clave"
				,"articulo.descripcion1"
				,"kilos"
				,"metros2"
				,"costo"};
		
		final String[] labels={
				"Id"
				,"Origen"
				,"Entrada (Maq)"
				,"Orden C."
				,"Fecha O.C"
				,"Fecha R."
				,"Bobina"
				,"Descripción"
				,"Kilos"
				,"M2"
				,"Costo"};
		final TableFormat<SalidaACorte> tf=GlazedLists.tableFormat(SalidaACorte.class, cols, labels);
		return tf;
	}
	
	
	public static TableFormat<SalidaDeMaterial> getTrasladosTF(){
		final String[] cols={
				"id"
				,"entrada.id"
				,"entrada.entradaDeMaquilador"				
				,"fecha"
				,"articulo.clave"
				,"articulo.descripcion1"
				,"kilos"
				,"metros2"
				,"costo"};
		
		final String[] labels={
				"Id"
				,"Origen"
				,"Entrada (Maq)"				
				,"Fecha R."
				,"Bobina"
				,"Descripción"
				,"Kilos"
				,"M2"
				,"Costo"};
		final TableFormat<SalidaDeMaterial> tf=GlazedLists.tableFormat(SalidaDeMaterial.class, cols, labels);
		return tf;
		
	}
	
	public static TableFormat<SalidaDeBobinas> getSalidaDeBobinasTF(){
		final String[] cols={
				"id"
				,"entrada.id"
				,"entrada.entradaDeMaquilador"
				,"destino.COM"
				,"destino.FENT"	
				,"fecha"
				,"entrada.articulo.clave"
				,"entrada.articulo.descripcion1"
				,"kilos"
				,"metros2"
				,"costo"};
		
		final String[] labels={
				"Id"
				,"Origen"
				,"Entrada (Maq)"
				,"COM"
				,"Fecha COM"
				,"Fecha (S)"
				,"Bobina"
				,"Descripción"
				,"Kilos"
				,"M2"
				,"Costo"};
		final TableFormat<SalidaDeBobinas> tf=GlazedLists.tableFormat(SalidaDeBobinas.class, cols, labels);
		return tf;
	}
	
	public static TableFormat<SalidaDeHojas> getSalidaDeHojasTF(){
		final String[] cols={
				"id"
				,"origen.origen.entrada.id"
				,"origen.origen.entrada.entradaDeMaquilador"
				,"fecha","clave","descripcion","cantidad","metros2","origen.costo","costo","destino.COM","destino.FENT","precioPorKiloFlete"
				};
		final String[] labels={
				"Id"
				,"Ent Maq"
				,"Origen (EM)"				
				,"Fecha","Articulo","Descripcion","Cantidad","M2","Precio (MIL)","Costo","Com","Fecha Com","Gastos"};
		final TableFormat<SalidaDeHojas> tf=GlazedLists.tableFormat(SalidaDeHojas.class, cols,labels);
		return tf;
	}
	
	public static TableFormat<EntradaDeHojas> getEntradaDeHojasTF(){
		final String[] cols={
				"id"
				,"origen.entrada.id"
				,"origen.entrada.entradaDeMaquilador"
				,"origen.id","fecha","clave","descripcion","cantidad","metros2","merma"
				,"costo"
				,"costoCalculado"
				};
		final String[] labels={
				"Id"
				,"Ent Maq"
				,"Origen (EM)"
				,"OrigenId","Fecha","Articulo","Descripcion","Cantidad","M2","Merma"
				,"Precio (MIL)"
				,"Costo"
				};
		final TableFormat<EntradaDeHojas> tf=GlazedLists.tableFormat(EntradaDeHojas.class, cols,labels);
		return tf;
	}
	
	
	
	

}
