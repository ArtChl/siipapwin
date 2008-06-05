package com.luxsoft.siipap.alt.clientes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;

import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.titledtab.TitledTab;
import net.infonode.util.Direction;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXPanel;

import com.luxsoft.siipap.swing.AbstractView;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.utils.CommandUtils;

/**
 * Consulta rápida de clientes que mantiene infromación
 * relevante de ventas,compras,pagos,notas,devoluciones
 * y articulos vendidos a un cliente
 * 
 * 
 * @author Ruben Cancino
 *
 */
public class AltA extends AbstractView{
	
	
	private TabbedPanel tabPanel;
	//private JTabbedPane tabPanel;
	private final AltAModel model;
	
	private Action reloadAction;
	private Action seleccionarCliente;
	private Action actualizarDatos;
	
	private AltAFacturas2 facturasWindow;
	private JXHeader header;
	private AltAWindow window;
	
	public AltA(final AltAModel model){
		this.model=model;
		initActions();
		ChangeHandler handler=new ChangeHandler();
		model.addBeanPropertyChangeListener("cliente", handler);
	}
	
	private void initActions(){
		reloadAction=CommandUtils.createRefreshAction(this, "refresh");
		seleccionarCliente=new DispatchingAction(this,"seleccionar");
		actualizarDatos=new DispatchingAction(this,"actualizarDatos");
		CommandUtils.configAction(actualizarDatos, "actualizarDatosDeCliente", "images2/database_refresh.png");
		CommandUtils.configAction(seleccionarCliente, "seleccionarCliente", "images2/SalesRep.png");
	}
	
	private void initComponents(){	
		
		tabPanel=new TabbedPanel();
		tabPanel.getProperties().setTabAreaOrientation(Direction.DOWN);		
		tabPanel.setOpaque(false);
		
		//tabPanel=new JTabbedPane();
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		JSplitPane sp=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setOpaque(false);
		sp.setOneTouchExpandable(true);
		sp.setTopComponent(buildHeaderPanel());
		sp.setBottomComponent(buildDocumentPanel());
		//sp.setDividerLocation(-1);
		sp.setResizeWeight(.1);
		return sp;
	}
	
	
	private JComponent buildHeaderPanel(){
		header=new JXHeader();
		//hx.setFont(hx.getFont().deriveFont(18f));
		//hx.setAlpha(.2f);		
		return header;
	}
	
	private JComponent buildDocumentPanel(){
		facturasWindow=new AltAFacturas2(this);
		facturasWindow.registerWorkerListener(new WorkerListener());
		installTabs();		
		return tabPanel;		
	}
	
	private void installTabs(){
		
		tabPanel.addTab(new TitledTab("Ventas",null,facturasWindow.getContent(),null));
		tabPanel.addTab(new TitledTab("CXC",null,new JXPanel(),null));
		tabPanel.addTab(new TitledTab("Productos",null,new JXPanel(),null));
		tabPanel.addTab(new TitledTab("Descuentos",null,new JXPanel(),null));
		tabPanel.addTab(new TitledTab("Históricos",null,new JXPanel(),null));
		
		/**
		tabPanel.addTab("Ventas",null,facturasWindow.getContent());
		tabPanel.addTab("CXC",null,new JXPanel(),null);
		tabPanel.addTab("Productos",null,new JXPanel(),null);
		tabPanel.addTab("Descuentos",null,new JXPanel(),null);
		tabPanel.addTab("Históricos",null,new JXPanel(),null);
		**/
		
	}
	
	public void refresh(){
		facturasWindow.load();
	}
	
	public Action getReloadAction(){
		return reloadAction;
	}
	public Action getSeleccionarClienteAction(){
		return seleccionarCliente;
	}
	
	public void seleccionar(){
		model.setValue("cliente", AltASelectorDeClientes
				.seleccionarCliente(getWindow().getFrame())
				.getCliente());
	}
	
	public Action getActualizarDatos(){
		return actualizarDatos;
	}
	
	public void actualizarDatos(){
		model.actualizarDatos();
		cliente();
	}
	
	private void cliente(){
		String titulo="{0}  ({1})";
		header.setTitle(MessageFormat.format(
				titulo, 
				model.getCliente().getNombre(),
				model.getCliente().getClave()));
		String desc="Saldo: {0}\t Vencido: {1}\t U. Pago: {2}";
		header.setDescription(MessageFormat.format(desc
				, model.getCliente().getCredito().getSaldo()
				,model.getCliente().getCredito().getSaldoVencido()
				,model.getCliente().getCredito().getUltimoPago()
				));
		
	}
	
	
	/**
	 * Handler para detectar el cambio de cliente y/o Periodo
	 * y cargar datos en las listas
	 * 
	 * @author RUBEN
	 *
	 */
	private class ChangeHandler implements PropertyChangeListener{

		public void propertyChange(PropertyChangeEvent evt) {
			refresh();
			cliente();
		}
		
		
	}
	
	private class WorkerListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {			
			if("state".equals(evt.getPropertyName())
					&& SwingWorker.StateValue.DONE.equals(evt.getNewValue())){
					if(getWindow()!=null){
						getWindow().getBusyLabel().setBusy(false);
						getWindow().getBusyLabel().setVisible(false);
					}
						
					
				}else if(SwingWorker.StateValue.STARTED.equals(evt.getNewValue())){
					if(getWindow()!=null){
						getWindow().getBusyLabel().setVisible(true);
						getWindow().getBusyLabel().setText("Cargando datos");
						getWindow().getBusyLabel().setBusy(true);
					}
				}
					
		}
		
	}

	public AltAWindow getWindow() {
		return window;
	}

	public void setWindow(AltAWindow window) {
		this.window = window;
	}

	public void close(){
		facturasWindow.registerWorkerListener(null);
	}

	public AltAModel getModel() {
		return model;
	}
	
}
