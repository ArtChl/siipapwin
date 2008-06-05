package com.luxsoft.siipap.cxc.nc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;

import com.luxsoft.siipap.cxc.dao.NotaDeCreditoDao;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.utils.MessageUtils;

/**
 * Clase para imprimir notas de credito, funciona como facade para
 * toda la aplicacion al momento de querer imprimir notas 
 * 
 * @author Ruben Cancino
 *
 */
public class ImpresionDeNotas {
	
	public static final void imprimir(final List<NotaDeCredito> notas){
		if(notas.isEmpty())
			return;
		final String tipo=notas.get(0).getTipo();
		final FormaDeImpresion impresor=new FormaDeImpresion(notas,ServiceLocator.getNotasManager().nextNumero(tipo));
		impresor.open();
		if(!impresor.hasBeenCanceled()){
			MessageUtils.showMessage("Prepare su impresora ", "Impresión de Notas");
			Runtime r=Runtime.getRuntime();
			final SortedList<NotaDeCredito> notas2=new SortedList<NotaDeCredito>(GlazedLists.eventList(notas),GlazedLists.beanPropertyComparator(NotaDeCredito.class, "numero"));
			for(NotaDeCredito n:notas2){
				if(n.getTipo().equals("H") || n.getTipo().equals("I")|| n.getTipo().equals("J")){
					ServiceLocator.getNotasManager().imprimirNotaDevolucion(n);
				}else
					ServiceLocator.getNotasManager().imprimirNotaDeDescuento(n);					
				try {
					Process p=r.exec(new String[]{"IMPRNOTA.BAT"});
					int res=p.waitFor();
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
		
		/**
		SwingWorker<List<NotaDeCredito>,String> worker=new SwingWorker<List<NotaDeCredito>, String>(){

			@Override
			protected List<NotaDeCredito> doInBackground() throws Exception {
				return notas;
			}
			protected void done() {
				try {
					final List<NotaDeCredito> notas=get();
					System.out.println("Notas a imprimir: "+notas.size());
					final ImpresorDeNotas impresor=new ImpresorDeNotas(notas);
					impresor.open();
					if(!impresor.hasBeenCanceled()){
						Runtime r=Runtime.getRuntime();
						
						for(NotaDeCredito n:notas){
							//ServiceLocator.getNotasManager().imprimirNotaDeDescuento(n);
							com.luxsoft.siipap.cxc.managers.ImpresorDeNotas.imprimir(n,n.getId()+".txt" );
							Process p=r.exec(new String[]{"IMPRNOTA.BAT"});
							//InputStream is = p.getInputStream(); 

							int res=p.waitFor();
							//System.out.println("Terminacion:" +res);
						}
					
					}
				} catch (Exception e) {
					e.printStackTrace();
					MessageUtils.showError("Error al cargar notas", e);
				}
			}
			
		};
		worker.execute();
		*/		
	}
	
	public static final void imprimir(final NotaDeCredito nota){
		final List<NotaDeCredito> notas=new ArrayList<NotaDeCredito>();
		notas.add(nota);
		imprimir(notas);
	}
	
	public static void main(String[] args) throws IOException {
		
		NotaDeCreditoDao dao=(NotaDeCreditoDao)ServiceLocator.getDaoContext().getBean("notasDeCreditoDao");		
		NotaDeCredito nota=dao.buscar(114231L);
		ServiceLocator.getNotasManager().refresh(nota);
		ImpresionDeNotas.imprimir(nota);		
		
		
	}

}
