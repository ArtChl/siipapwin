package com.luxsoft.siipap.cxc.utils;

import java.awt.BorderLayout;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;

import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.selectores.AbstractSelector;
import com.luxsoft.siipap.cxc.selectores.Selector;
import com.luxsoft.siipap.cxc.selectores.Selectores;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;


/**
 * Despliega algunos browsers de uso comun sin uso en proceso
 * 
 * @author Ruben Cancino
 *
 */
public class Browsers {
	
	/**
	 * Muestra una lista de solo lectura de las notas disponibles para usra como forma de pago
	 * 
	 * @param c
	 */
	public static void mostrarNotasDisponibles(final Cliente c){
		final EventList<NotaDeCredito> notas=GlazedLists.eventList(ServiceLocator.getNotasManager().buscarNotasDeCreditoDisponibles(c));
		if(notas.isEmpty()){
			MessageUtils.showMessage(MessageFormat.format("El cliente {0} ({1})\n No tiene notas disponibles para usar como forma de pago"
					, c.getNombre(),c.getClave()), "Notas disponibles");
			return ;
		}else		
			Selectores.seleccionarNotaDeCredito(c, notas);
	}
	
	/**
	 * Muestra un Grid con los Pagos con disponible
	 * 
	 * @param c
	 */
	public static void mostrarSaldosAFavor(final Cliente c){
		final List<PagoM> otros=ServiceLocator.getPagosManager().buscarSaldosAFavor(c);
		if(otros.isEmpty()){
			MessageUtils.showMessage(MessageFormat.format("El cliente {0} ({1})\n No tiene pagos con  disponibles para usar como forma de pago"
					, c.getNombre(),c.getClave()), "Pagos con  disponible");
			return ;
		}else			
			Selectores.seleccionarPagoM(c, otros);
	}
	
	/**
	 * Muestra en un grid todos los saldos a favor
	 *
	 */
	public static void mostrarSaldosAFavor(){
		final SwingWorker<List<Map<String, Object>>, String> worker=new SwingWorker<List<Map<String,Object>>, String>(){
			
			@Override
			protected List<Map<String, Object>> doInBackground() throws Exception {
				return ServiceLocator.getPagosManager().buscarSaldosAFavor();
			}

			@Override
			protected void done() {
				try {
					final List<Map<String, Object>> otros=get();
					if(!otros.isEmpty()){
						Selectores.seleccionarPagoM(otros);
					}					
				} catch (Exception e) {
					MessageUtils.showError(e.getLocalizedMessage(),e);
					e.printStackTrace();
				}
				
			}
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	/**
	 * Muestra en un dialog una lista de notas
	 * 
	 * @param source
	 */
	public static List<NotaDeCredito> seleccionarNotasParaSuImpresion(final List<NotaDeCredito> source){
		final EventList<NotaDeCredito> notas=GlazedLists.eventList(source);
		if(!notas.isEmpty()){
			//final Cliente c=notas.get(0).getCliente();
			final Selector<NotaDeCredito> selector=new AbstractSelector<NotaDeCredito>(notas,"Imprimir ?"
					,"Notas de Crédito para impresión","Imprimir la siguiente lista de notas?"){
							
				@Override
				protected TableFormat<NotaDeCredito> getTableFormat() {
					return CXCTableFormats.getNotaDeCreditoTF();
				}
				
				protected JComponent buildContent() {
					JPanel panel=new JPanel(new BorderLayout());
					JComponent c=super.buildContent();
					panel.add(c,BorderLayout.CENTER);
					panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
					return panel;
				}
				
			};
			selector.open();
			if(!selector.hasBeenCanceled()){
				return  notas;
			}
			
		}
		return null;
	}
	public static void main(String[] args) {
		mostrarSaldosAFavor();
	}

}
