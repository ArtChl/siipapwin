package com.luxsoft.siipap.cxc.procesos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.Application;
import com.luxsoft.siipap.swing.actions.SWXAction;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils.BusyHeader;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Modificar para utilizar nueva infraestructura de VentasManager
 * y mostrar una barra de avance
 * Cambia nombre y nombres de metodos
 * 
 * @author Ruben Cancino
 *
 */
public class ActualizarVentas extends SWXAction{

	@Override
	protected void execute() {
		int res=JOptionPane.showConfirmDialog(
				getParent(), "Actualizar las todas las ventas con saldo y su provision","Actualizar ventas ?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
		if(res==JOptionPane.OK_OPTION){
			actualizarProvision();
		}	
	}
	
	private JFrame getParent(){
		if(Application.isLoaded())
			return Application.instance().getMainFrame();
		else
			return null;
	}
	
	/**
	 * 
	 * TODO Modificar para mostrar una barra de avance
	 */
	private void actualizarProvision(){
		
		final ProgressBarTaskPanel dialog=new ProgressBarTaskPanel("Actualizando ventas","");
		
		final SwingWorker<String, String> worker=new SwingWorker<String, String>(){
			@Override
			protected String doInBackground() throws Exception {
				publish("Cargando dagos");
				final List<Long> ventas=getManager().getListaDeVentasACreditoConSaldo();				
				int current=0;
				for(Long id:ventas){
					if(!isCancelled()){
						publish("Procesando venta: "+id);
						try {
							//SystemUtils.sleep(50);
							getManager().actualizarVenta(id);							
							++current;					
							setProgress(100 * current / ventas.size());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}				
				return "OK";
			}

			@Override
			protected void process(List<String> chunks) {
				for(String s:chunks){
					dialog.setDescripcion(s);
				}
			}			
		};
		
		worker.addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				//Kilometro 31,
				if("progress".equals(evt.getPropertyName())){
					dialog.getProgressBar().setValue((Integer)evt.getNewValue());
				}
			}
		});		
		worker.execute();
		dialog.open();	
		if(dialog.hasBeenCanceled()){
			System.out.println("Cancelando actualizacion");
			worker.cancel(true);
		}
		
	}
	
	public class ProgressBarTaskPanel extends SXAbstractDialog {
		
		private JProgressBar progressBar;
		private String descripcion;
		private BusyHeader header;
		

		public ProgressBarTaskPanel(final String titulo,final String descripcion) {
			super(titulo);
			this.descripcion=descripcion;
		}
		
		private void initComponents(){
			progressBar=new JProgressBar();
			progressBar.setStringPainted(true);
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
		}

		@Override
		protected JComponent buildContent() {
			initComponents();
			FormLayout layout=new FormLayout("f:max(p;90dlu):g","");
			DefaultFormBuilder builder=new DefaultFormBuilder(layout);
			builder.append(progressBar);
			return builder.getPanel();
		}

		@Override
		protected JComponent buildHeader() {
			header=new TaskUtils.BusyHeader(getTitle(),getDescripcion());
			return header;
		}

		public JProgressBar getProgressBar() {
			return progressBar;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
			if(header!=null)
				header.setDescription(descripcion);
		}
		
		public void setMin(int min){
			progressBar.setMinimum(min);
		}
		public void setMax(int max){
			progressBar.setMaximum(max);
		}
		
		
		
	}
	
	private VentasManager manager;

	public VentasManager getManager() {
		if(manager==null)
			manager=ServiceLocator.getVentasManager();
		return manager;
	}
	
	public static void main(String[] args) {
		ActualizarVentas a=new ActualizarVentas();
		a.execute();
		System.exit(0);
	}

}
