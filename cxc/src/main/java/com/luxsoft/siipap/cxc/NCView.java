package com.luxsoft.siipap.cxc;

import java.util.List;

import javax.swing.Action;

import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.managers.CXCManager;
import com.luxsoft.siipap.cxc.model2.NCPorClienteModel;
import com.luxsoft.siipap.cxc.nc.CargosCreditosPorClienteView;
import com.luxsoft.siipap.cxc.nc.ClientesCreditoView;
import com.luxsoft.siipap.cxc.nc.ControladorDeNotas;
import com.luxsoft.siipap.cxc.nc.VentasPorCliente;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.views2.DefaultTaskView;
import com.luxsoft.siipap.swing.views2.InternalTaskTab;
import com.luxsoft.siipap.swing.views2.InternalTaskView;

/**
 * Vista para la administración de notas de credito/cargo
 * de ventas tipo CRE
 * 
 * @author Ruben Cancino
 *
 */
public class NCView extends DefaultTaskView{
	
	private InternalTaskTab clientesTab;
	private ClientesCreditoView clientesView;
	private InternalTaskTab ventasTab;
	private VentasPorCliente ventasView;
	
	private CargosCreditosPorClienteView notasView;
	private InternalTaskTab notasTab;
	
	
	private ControladorDeNotas controlador;	
	private CXCManager manager;
	
	private Action mostrarNotas;
	
	
	/**
	 * Modelo/Estado para algunas de las {@link InternalTaskView}
	 */
	private NCPorClienteModel taskViewsModel;
	
	/**
	 * Inicializa el estado de esta vista, este metodo esta aqui para ser detonado
	 * por Spring
	 *
	 */
	@SuppressWarnings("unused")
	private void init(){
		taskViewsModel=new NCPorClienteModel();
		taskViewsModel.setManager(manager);
	}

	@Override
	protected void instalarTaskElements() {
		if(mostrarNotas==null){
			mostrarNotas=new DispatchingAction(this,"mostrarNotas");
			CommandUtils.configAction(mostrarNotas, "NCView.mostrarNotas", "");
		}
		consultas.add(mostrarNotas);
		
	}
	
	 
	/**
	 * Crea un tab para la vista de clientes
	 *
	 */
	public void showClientesTab(){
		if(clientesTab==null){			
			clientesView=new ClientesCreditoView(){
				
				public List<ClienteCredito> loadData() {
					return controlador.buscarClientesACredito();
				}
				
				public void onSelection(ClienteCredito c) {
					seleccionarCliente(c);
					mostrarVentas();
				}

				@Override
				protected List<NotaDeCredito> getNotasParaImprimir(Periodo p) {
					return getManager().buscarNotasDeDescuentoPorImprimir(p, false);
				}
				
			};			
			clientesTab=createInternalTaskTab(clientesView);
			
		}
		addTab(clientesTab);
		clientesView.load();
	}
	
	/**
	 * Muestra la lista de ventas para el cliente seleccionado
	 * taladrando 
	 *
	 */
	public void mostrarVentas(){
		if(ventasView==null){
			ventasView=new VentasPorCliente(taskViewsModel);
			ventasView.setControlador(getControlador());			
			ventasTab=createInternalTaskTab(ventasView);
			
		}
		addTab(ventasTab);
		ventasView.load();
		
	}
	
	/**
	 * Muestra el detalle de la nota de credito/cargo seleccionada
	 * 
	 *
	 */
	public void mostrarNotas(){
		if(taskViewsModel.getCliente()==null){
			MessageUtils.showMessage("Debe seleccionar un cliente antes de poder mostrar sus notas de crédito y cargo"
					, "Notas de Crédito");
		}else{
			if(notasView==null){
				notasView=new CargosCreditosPorClienteView(this.taskViewsModel,getControlador());
				notasTab=createInternalTaskTab(notasView);
			}
			addTab(notasTab);
			notasView.load();
		}				
	}
	
	
	
	private void seleccionarCliente(final ClienteCredito c){
		taskViewsModel.setCliente(c.getCliente());
		//if(ventasView!=null)
		//ventasView.load();
	}


	@Override
	public void open() {
		showClientesTab();
	}
	
	/**
	 * Cerramos apropiadamente esta vista para regresar algunos recursos al 
	 * sistema
	 * 
	 */
	public void close(){
		taskViewsModel.setCliente(null);
		clientesView.close();
		super.close();
	}

	/** Colaboradores ***/

	public ControladorDeNotas getControlador() {
		return controlador;
	}
	public void setControlador(ControladorDeNotas controlador) {
		this.controlador = controlador;
	}

	public CXCManager getManager() {
		return manager;
	}
	public void setManager(CXCManager manager) {
		this.manager = manager;
	}
	

}
