package com.luxsoft.siipap.cxc.chequed;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.Matchers;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.Juridico;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.model.CXCFiltros;
import com.luxsoft.siipap.cxc.model.PagosFactory;
import com.luxsoft.siipap.cxc.model.PagosFactoryImpl;
import com.luxsoft.siipap.cxc.model2.DefaultPagoFormModelImpl;
import com.luxsoft.siipap.cxc.model2.PagoDeCargosModel;
import com.luxsoft.siipap.cxc.model2.PagoFormModel;
import com.luxsoft.siipap.cxc.nc.ImpresionDeNotas;
import com.luxsoft.siipap.cxc.pagos.PagoConNotaForm;
import com.luxsoft.siipap.cxc.pagos.PagoDeCargosForm;
import com.luxsoft.siipap.cxc.selectores.Selectores;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.swing.views2.AbstractInternalTaskView;
import com.luxsoft.siipap.swing.views2.DefaultTaskView;
import com.luxsoft.siipap.swing.views2.InternalTaskTab;

public class ChequesDevueltosView extends DefaultTaskView{
	
	private CargosView view;

	@Override
	public void open() {
		view=new CargosView();
		final InternalTaskTab tab=new InternalTaskTab(view);
		addTab(tab);
		view.load();
		super.open();
	}

	protected void instalarTaskPanels(final JXTaskPaneContainer container){
		container.remove(consultas);
		container.remove(procesos);
	}

	
	
	public class CargosView extends AbstractInternalTaskView{
		
		private EventList<ChequeDevuelto> source;
		private SortedList<ChequeDevuelto> sortedSource;
		private EventSelectionModel<ChequeDevuelto> selectionModel;
		
		private EventList<Pago> pagosSource;		
		private EventList<Pago> pagosSorted;
		private EventSelectionModel<Pago> pagosSelectionModel;
		
		private EventList<NotaDeCredito> cargosSource;
		private EventList<NotaDeCredito> cargosSorted;
		private EventSelectionModel<NotaDeCredito> cargosSelectionModel;
		
		private EventList<Juridico> chequesJuridico;
		private SortedList<Juridico> chequesJuridicoSorted;
		private EventSelectionModel<Juridico> chequesJuridicoSelectionModel;
		
		private EventList<Juridico> cargosJuridico;
		private SortedList<Juridico> cargosJuridicoSorted;
		private EventSelectionModel<Juridico> cargosJuridicoSelectionModel;
		
		private JTabbedPane tabPanel;
		private JXTable chequesView;
		private JXTable juridicoChequesView;
		private JXTable juridicoCargosView;
		
		private Action delete;
		private Action select;
		
		private Action eliminarPagos;
		
		Action registrar;
		Action cancelar;
		Action pagar;
		Action generarCargo;
		Action  jurAction;
		
		private Action pagarCargoNormal;
		private Action pagarCargoConNota;
		private Action pagarCargoConDisponible;
		 
		
		private JXTable pagosGrid;
		private JXTable cargosGrid;

		public CargosView() {
			setTitle("Cheques Devueltos");
		}

		public JComponent getControl() {
			initGlazedList();
			initActions();
			final JPanel panel=new JPanel(new BorderLayout());	
			
			tabPanel=new JTabbedPane();	
			tabPanel.addTab("Cheques",createDevueltosView() );
			tabPanel.addTab("Pagos", createPagosView());
			tabPanel.addTab("Cargos", createCargosView());
			tabPanel.addTab("Jurídico", createJuridicoView());
			tabPanel.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {					
					eneableChequesAction(tabPanel.getSelectedIndex()==0);
					eneablePagosAction(tabPanel.getSelectedIndex()==1);
					eneableCargosAction(tabPanel.getSelectedIndex()==2);
					eneableJuridicoAction(tabPanel.getSelectedIndex()==0 ||tabPanel.getSelectedIndex()==2);
				}				
			});			
			panel.add(tabPanel,BorderLayout.CENTER);
			panel.add(new HeaderPanel("Cheques Devueltos","Cartera de cheques devueltos"),BorderLayout.NORTH);
			return panel;
		}
		
		public JComponent createDevueltosView(){
			final TableFormat<ChequeDevuelto> tf=GlazedLists.tableFormat(ChequeDevuelto.class
					, new String[]{"id","fecha","origen.id","cliente.clave","cliente.nombre","numero","banco","importe","saldo","pagosAplicados","cargosAplicados","porcentajeAplicado"}
					, new String[]{"Id","Fecha","Origen","Cliente","Nombre","Número","Banco","Importe","Saldo","Pagos","Cargos (Apl)","% Aplic"}
			);
			final EventTableModel<ChequeDevuelto> tm=new EventTableModel<ChequeDevuelto>(sortedSource,tf);
			chequesView=ComponentUtils.getStandardTable();
			chequesView.setModel(tm);
			selectionModel=new EventSelectionModel<ChequeDevuelto>(sortedSource);
			chequesView.setSelectionModel(selectionModel);
			new TableComparatorChooser<ChequeDevuelto>(chequesView,sortedSource,true);
			ComponentUtils.decorateActions(chequesView);
			ComponentUtils.addDeleteAction(chequesView, delete);
			ComponentUtils.addEnterAction(chequesView, select);
			chequesView.addMouseListener(new MouseAdapter(){				
				public void mouseClicked(MouseEvent e) {					
					if(e.getClickCount()==2){
						select();
					}
				}				
			});
			chequesView.getColumn(11).setCellRenderer(Renderers.getPorcentageRenderer());			
			final JScrollPane sp=new JScrollPane(chequesView);
			return sp;
		}
		
		public JComponent createPagosView(){
			pagosGrid=ComponentUtils.getStandardTable();
			final String[] cols={"id","clave","cliente.nombre","fecha","formaDePago","cheque.numero","cheque.id","importe","comentario"};
			final String[] names={"Id","Cliente","Nombre","Fecha","FP","Cheque","ChequeId","Importe","comentario"};
			final TableFormat<Pago> tf=GlazedLists.tableFormat(Pago.class, cols, names);
			final EventTableModel<Pago> tm=new EventTableModel<Pago>(pagosSorted,tf);
			pagosSelectionModel=new EventSelectionModel<Pago>(pagosSorted);
			pagosGrid.setModel(tm);
			pagosGrid.setSelectionModel(pagosSelectionModel);
			ComponentUtils.decorateActions(pagosGrid);
			final JScrollPane sp=new JScrollPane(pagosGrid);
			return sp;
		}
		
		public JComponent createCargosView(){
			cargosGrid=ComponentUtils.getStandardTable();
			final String[] cols={"id","numero","tipo","serie","origen","fecha","clave","cliente.nombre","saldoUnificado","comentario2","comentario","descuento"};
			final String[] names={"Id","Nota","T","S","Origen","Fecha","Cliente","Nombre","Saldo","Desc","Comentario","%Cargo"};
			final TableFormat<NotaDeCredito> tf=GlazedLists.tableFormat(NotaDeCredito.class, cols, names);
			final EventTableModel<NotaDeCredito> tm=new EventTableModel<NotaDeCredito>(cargosSorted,tf);
			cargosSelectionModel=new EventSelectionModel<NotaDeCredito>(cargosSorted);			
			cargosGrid.setModel(tm);
			cargosGrid.setSelectionModel(cargosSelectionModel);
			ComponentUtils.decorateActions(cargosGrid);
			final JScrollPane sp=new JScrollPane(cargosGrid);
			return sp;
		}
		
		public JComponent createJuridicoView(){
			final FormLayout layout=new FormLayout("f:p:g"
					,"c:10dlu,3dlu,120dlu, 3dlu,c:10dlu,3dlu,110dlu");
			final CellConstraints cc=new CellConstraints();
			final PanelBuilder builder=new PanelBuilder(layout);
			builder.addSeparator("Cheques Devueltos", cc.xy(1, 1));
			builder.add(createChequesJuridicoView(),cc.xy(1, 3));
			builder.addSeparator("Notas de Cargo ", cc.xy(1, 5));
			builder.add(createCargosJuridicoView(),cc.xy(1, 7));
			builder.setDefaultDialogBorder();
			return builder.getPanel();
		}
		
		public JComponent createChequesJuridicoView(){
			final TableFormat<Juridico> tf=GlazedLists.tableFormat(Juridico.class
					, new String[]{"id","fechaTraspaso","comentarios","cheque.cliente.clave","cheque.cliente.nombre","cheque.numero","cheque.banco"
				,"cheque.importe","cheque.saldo","cheque.pagosAplicados"}
			, new String[]{"id","F.Traspaso","Comentario","Cliente","Nombre","Cheq.#","Banco"
				,"Importe","Saldo","Pagos"}
			);
			final EventTableModel<Juridico> tm=new EventTableModel<Juridico>(chequesJuridicoSorted,tf);
			juridicoChequesView=ComponentUtils.getStandardTable();
			juridicoChequesView.setModel(tm);
			chequesJuridicoSelectionModel=new EventSelectionModel<Juridico>(chequesJuridicoSorted);
			juridicoChequesView.setSelectionModel(chequesJuridicoSelectionModel);
			new TableComparatorChooser<Juridico>(juridicoChequesView,chequesJuridicoSorted,true);
			ComponentUtils.decorateActions(juridicoChequesView);
			//juridicoChequesView.getColumn(11).setCellRenderer(Renderers.getPorcentageRenderer());			
			final JScrollPane sp=new JScrollPane(juridicoChequesView);
			return sp;
		}
		
		public JComponent createCargosJuridicoView(){
			final TableFormat<Juridico> tf=GlazedLists.tableFormat(Juridico.class
					, new String[]{"id","fechaTraspaso","comentarios","nota.clave","nota.cliente.nombre","nota.fecha","nota.numero","nota.saldoDelCargo","nota.comentario"}
					, new String[]{"id","F.Traspaso","Comentario","Cliente","Nombre","Fecha (N)","Número","Saldo","Comentario (Nota)"}
			);
			final EventTableModel<Juridico> tm=new EventTableModel<Juridico>(cargosJuridicoSorted,tf);
			juridicoCargosView=ComponentUtils.getStandardTable();
			juridicoCargosView.setModel(tm);
			cargosJuridicoSelectionModel=new EventSelectionModel<Juridico>(cargosJuridicoSorted);
			juridicoCargosView.setSelectionModel(cargosJuridicoSelectionModel);
			new TableComparatorChooser<Juridico>(juridicoCargosView,cargosJuridicoSorted,true);
			ComponentUtils.decorateActions(juridicoCargosView);
			//juridicoChequesView.getColumn(11).setCellRenderer(Renderers.getPorcentageRenderer());			
			final JScrollPane sp=new JScrollPane(juridicoCargosView);
			return sp;
		}
		
		private void initGlazedList(){
			
			//Cheques Devueltos
			source=GlazedLists.threadSafeList(new BasicEventList<ChequeDevuelto>());
			final EventList<MatcherEditor<ChequeDevuelto>> editors=new BasicEventList<MatcherEditor<ChequeDevuelto>>();
			
			final MatcherEditor<ChequeDevuelto> e0=GlazedLists.fixedMatcherEditor(new Matcher<ChequeDevuelto>(){
				public boolean matches(ChequeDevuelto item) {
					return !item.isJuridico();
				}				
			});
			editors.add(e0);
			
						
			final TextFilterator<ChequeDevuelto> filterator1=GlazedLists.textFilterator(new String[]{"cliente.clave","cliente.nombre"});
			final TextComponentMatcherEditor<ChequeDevuelto> e1=new TextComponentMatcherEditor<ChequeDevuelto>(cliente,filterator1);
			editors.add(e1);
			
			final CompositeMatcherEditor<ChequeDevuelto> compositeEditor=new CompositeMatcherEditor<ChequeDevuelto>(editors);			
			final FilterList<ChequeDevuelto> filterSource=new FilterList<ChequeDevuelto>(source,new ThreadedMatcherEditor<ChequeDevuelto>(compositeEditor));
			sortedSource=new SortedList<ChequeDevuelto>(filterSource,null);
			
			//Pagos
			pagosSource=GlazedLists.threadSafeList(new BasicEventList<Pago>());
			final EventList<MatcherEditor<Pago>> pagosEditors=new BasicEventList<MatcherEditor<Pago>>();
			final TextFilterator<Pago> pagosFilter1=GlazedLists.textFilterator(new String[]{"cliente.clave","cliente.nombre"});
			final TextComponentMatcherEditor<Pago> pagosEditor1=new TextComponentMatcherEditor<Pago>(cliente,pagosFilter1);
			pagosEditors.add(pagosEditor1);
			final CompositeMatcherEditor<Pago> compositePagosEditor=new CompositeMatcherEditor<Pago>(pagosEditors);
			
			final FilterList<Pago> filterPagos=new FilterList<Pago>(pagosSource,new ThreadedMatcherEditor<Pago>(compositePagosEditor));
			pagosSorted=new SortedList<Pago>(filterPagos,null);
			
			//Cargos
			cargosSource=GlazedLists.threadSafeList(new BasicEventList<NotaDeCredito>());
			final EventList<MatcherEditor<NotaDeCredito>> cargosEditors=new BasicEventList<MatcherEditor<NotaDeCredito>>();
			final TextFilterator<NotaDeCredito> cargosFilter1=GlazedLists.textFilterator(new String[]{"cliente.clave","cliente.nombre"});
			final TextComponentMatcherEditor<NotaDeCredito> cargosEditor1=new TextComponentMatcherEditor<NotaDeCredito>(cliente,cargosFilter1);
			cargosEditors.add(cargosEditor1);
			final CompositeMatcherEditor<NotaDeCredito> copositeCargosEditor=new CompositeMatcherEditor<NotaDeCredito>(cargosEditors);
			final FilterList<NotaDeCredito> filterCargos=new FilterList<NotaDeCredito>(cargosSource,copositeCargosEditor);
			cargosSorted=new SortedList<NotaDeCredito>(filterCargos,null);
			
			//Juridico
			chequesJuridico=GlazedLists.threadSafeList(new BasicEventList<Juridico>());
			//final MatcherEditor<ChequeDevuelto> juridicoEditor1=GlazedLists.fixedMatcherEditor(Matchers.beanPropertyMatcher(ChequeDevuelto.class, "juridico", Boolean.TRUE));
			final FilterList<Juridico> juridicoFilter1=new FilterList<Juridico>(chequesJuridico, new Matcher<Juridico>(){
				public boolean matches(Juridico item) {					
					return item.getCheque()!=null;
				}				
			});
			chequesJuridicoSorted=new SortedList<Juridico>(juridicoFilter1,null);
			
			cargosJuridico=GlazedLists.threadSafeList(new BasicEventList<Juridico>());
			final FilterList<Juridico> juridicoFilter2=new FilterList<Juridico>(chequesJuridico, new Matcher<Juridico>(){
				public boolean matches(Juridico item) {					
					return item.getCheque()==null;
				}				
			});
			cargosJuridicoSorted=new SortedList<Juridico>(juridicoFilter2,null);
			
		}
		
		private void initActions(){
			delete=CommandUtils.createDeleteAction(this, "cancelar");
			select=new DispatchingAction(this,"select");
		}
		
		public void eneablePagosAction(boolean val){
			eliminarPagos.setEnabled(val);
		}
		
		public void eneableChequesAction(boolean val){
			registrar.setEnabled(val);
			cancelar.setEnabled(val);
			pagar.setEnabled(val);
			generarCargo.setEnabled(val);
		}
		
		public void eneableCargosAction(boolean val){
			pagarCargoConNota.setEnabled(val);
			pagarCargoConDisponible.setEnabled(val);
			pagarCargoNormal.setEnabled(val);
		}
		
		public void eneableJuridicoAction(boolean val){
			jurAction.setEnabled(val);
		}
		
		
		
		@Override
		public void instalOperacionesAction(JXTaskPane operaciones) {
			operaciones.add(CommandUtils.createRefreshAction(this, "load"));
			registrar=new DispatchingAction(this,"registrar");
			CommandUtils.configAction(registrar, CXCActions.RegistrarChequeDevuelto.getId(), "");
			operaciones.add(registrar);
			
			cancelar=new DispatchingAction(this,"cancelar");
			CommandUtils.configAction(cancelar, CXCActions.CancelarChequeDevuelto.getId(), "");
			operaciones.add(cancelar);
			
			pagar=new DispatchingAction(this,"pagar");
			CommandUtils.configAction(pagar, CXCActions.PagarChequeDevuelto.getId(),"");
			operaciones.add(pagar);
			
			generarCargo=new DispatchingAction(this,"generarCargo");
			CommandUtils.configAction(generarCargo, CXCActions.CrearCargoCRE.getId(), "");
			generarCargo.putValue(Action.SHORT_DESCRIPTION, "Generar Cargo");
			generarCargo.putValue(Action.LONG_DESCRIPTION, "Generar Cargo");
			generarCargo.putValue(Action.NAME, "Generar Cargo");
			operaciones.add(generarCargo);
			
			eliminarPagos=new DispatchingAction(this,"eliminarPago");
			//CommandUtils.configAction(eliminarPagos, "","");
			eliminarPagos.putValue(Action.NAME, "Eliminar Pago");
			operaciones.add(eliminarPagos);
			eneablePagosAction(false);
			
			//Acciones para cargos
			
			pagarCargoNormal=new DispatchingAction(this,"pagarCargoNormal");
			pagarCargoNormal.putValue(Action.NAME, "Pagar cargo");			
			operaciones.add(pagarCargoNormal);
			
			pagarCargoConNota=new DispatchingAction(this,"pagarCargoConNota");
			pagarCargoConNota.putValue(Action.NAME, "Pagar cargo con Nota");
			operaciones.add(pagarCargoConNota);
			
			pagarCargoConDisponible=new DispatchingAction(this,"pagarCargoConDisponible");
			pagarCargoConDisponible.putValue(Action.NAME, "Pagar cargo con Saldo");
			operaciones.add(pagarCargoConDisponible);			
			
			pagarCargoConNota.setEnabled(false);
			pagarCargoConDisponible.setEnabled(false);
			pagarCargoNormal.setEnabled(false);
			
			//Transferir a juridico
			jurAction=new DispatchingAction(this,"transpasoJuridico");
			CommandUtils.configAction(jurAction, CXCActions.TransferirAJuridico.getId(), "");
			operaciones.add(jurAction);
			
		}
		
		
		
		@Override
		public void installFiltrosPanel(JXTaskPane filtros) {
			filtros.add(getFiltrosPanel());
		}



		private JPanel filterPanel;
		private JTextField cliente=new JTextField(10);
		private JTextField cheque=new JTextField(10);
		
		public JComponent getFiltrosPanel(){
			if(filterPanel==null){
				FormLayout layout=new FormLayout(
						"l:p,2dlu,f:max(p;130dlu)","");
				DefaultFormBuilder builder=new DefaultFormBuilder(layout);			
				builder.append("Cliente",cliente,true);
				builder.append("Cheque",cheque,true);
				filterPanel= builder.getPanel();			
				filterPanel.setOpaque(false);			
			}
			return filterPanel;		
		}
		
		public void registrar(){
			DefaultFormModel model=new DefaultFormModel(new ChequeDevuelto());
			ChequeDevueltoForm form=new ChequeDevueltoForm(model);
			form.open();
			if(! form.hasBeenCanceled()){				
				try {
					final ChequeDevuelto ch=(ChequeDevuelto)model.getBaseBean();
					ChequeDevuelto res=ServiceLocator.getChequesDevManager().salvar(ch);
					res=ServiceLocator.getChequesDevManager().refresh(res);
					source.add(res);
					chequesView.packAll();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
				
			}
		}

		/**
		 * Elimina el cheque devuelto, sus pagos y cargos
		 *
		 */
		public void cancelar(){
			if(!selectionModel.getSelected().isEmpty()){
				ChequeDevuelto ch=selectionModel.getSelected().get(0);
				boolean res=MessageUtils.showConfirmationMessage(MessageFormat.format("Eliminar registro {0}\n Si tiene pagos o cargos aplicados estos serán eliminados",ch.toString())
						, "Cheques devueltos");
				if(res){
					try {
						ServiceLocator.getChequesDevManager().cancelar(ch);
						refreshPagos();
						source.remove(ch);
						
					} catch (Exception e) {
						MessageUtils.showError("Error al eliminar el cargo", e);
					}
				}
			}
		}
		
		public void generarCargo(){
			if(!selectionModel.getSelected().isEmpty()){
				ChequeDevuelto ch=selectionModel.getSelected().get(0);
				if(ch.getCargosAplicados().doubleValue()>0){
					if(!MessageUtils.showConfirmationMessage("El cheque devuelto ya tiene cargos aplicados, desa contiunar?", "Cargos"))
						return;
				}
				final SolicitudDeCargoPorChequeDevuelto solicitud=new SolicitudDeCargoPorChequeDevuelto(ch);
				final NotaDeCargoPorChequeDevuelto form=new NotaDeCargoPorChequeDevuelto(solicitud);
				form.open();
				if(!form.hasBeenCanceled()){
					final NotaDeCredito nota=form.getCargo();
					ServiceLocator.getNotasManager().salvarNotaCre(nota);
					ImpresionDeNotas.imprimir(nota);
					load();
				}
			}
		}
		
		public void pagar(){
			if(!selectionModel.getSelected().isEmpty()){
				ChequeDevuelto ch=selectionModel.getSelected().get(0);
				if(ch.getSaldo().doubleValue()>0){
					try {
						final PagoM res=ChequeDevueltoPagoForm.pagar(ch);
						if(res!=null){
							final ChequeDevuelto fresh=ServiceLocator.getChequesDevManager().refresh(ch);
							int index=source.indexOf(ch);
							source.set(index, fresh);
							refreshPagos();
							
						}
					} catch (Exception e) {
						MessageUtils.showError("Error generando pago",e);
					}
				}				
			}else{
				System.out.println("No hay seleccion");
			}
		}
		
		public void eliminarPagos(){
			if(!pagosSelectionModel.getSelected().isEmpty()){
				Pago p=pagosSelectionModel.getSelected().get(0);
				ServiceLocator.getPagosManager().eliminarPagoM(p.getPagoM().getId());
				refreshPagos();
			}
		}
		
		
		public void select(){
			if(!selectionModel.getSelected().isEmpty()){
				@SuppressWarnings("unused")
				final ChequeDevuelto ch=selectionModel.getSelected().get(0);
				//TODO Mostrar detalles del cheque devuelto (Consulta)
			}			
		}
		
		public void load(){
			
			final SwingWorker<List, String> worker=new SwingWorker<List, String>(){				
				@SuppressWarnings("unchecked")
				protected List doInBackground() throws Exception {
					final List res=new ArrayList();
					res.add(0,ServiceLocator.getUniversalDao().getAll(ChequeDevuelto.class));
					//res.add(1,ServiceLocator.getChequesDevManager().buscarPagosDeCheques());
					return res;
				}
				protected void done() {					
					try {
						List res=get();						
						source.clear();
						source.addAll((Collection<? extends ChequeDevuelto>) res.get(0));
						chequesView.packAll();
						/*
						pagosSource.clear();
						for(ChequeDevuelto c:source){
							pagosSource.addAll(c.getPagos());
						}
						pagosGrid.packAll();
						*/
						refreshPagos();
						refreshCargos();
						refreshJuridico();
					} catch (Exception e) {
						MessageUtils.showError("Error", e);
					}
				}
				
			};
			
			TaskUtils.executeSwingWorker(worker);
		}
		
		public void refreshPagos(){
			
			final SwingWorker<List, String> worker=new SwingWorker<List, String>(){				
				@SuppressWarnings("unchecked")
				protected List doInBackground() throws Exception {
					final List res=new ArrayList();					
					res.add(0,ServiceLocator.getChequesDevManager().buscarPagosDeCheques());
					return res;
				}
				protected void done() {					
					try {
						List res=get();						
						pagosSource.clear();
						pagosSource.addAll((Collection<? extends Pago>) res.get(0));
						pagosGrid.packAll();
					} catch (Exception e) {
						MessageUtils.showError("Error", e);
					}
				}
				
			};
			worker.execute();
			//TaskUtils.executeSwingWorker(worker);
		}
		
		public void refreshCargos(){
			
			final SwingWorker<List<NotaDeCredito>, String> worker=new SwingWorker<List<NotaDeCredito>, String>(){				
				@SuppressWarnings("unchecked")
				protected List<NotaDeCredito> doInBackground() throws Exception {
					return ServiceLocator.getNotasManager().buscarCargosCheque();
				}
				protected void done() {					
					try {						
						cargosSource.clear();
						cargosSource.addAll(get());
						cargosGrid.packAll();
					} catch (Exception e) {
						MessageUtils.showError("Error", e);
					}
				}				
			};
			worker.execute();			
		}
		
		public void refreshJuridico(){
			
			final SwingWorker<List<Juridico>, String> worker=new SwingWorker<List<Juridico>, String>(){				
				@SuppressWarnings("unchecked")
				protected List<Juridico> doInBackground() throws Exception {
					return ServiceLocator.getJuridicoManager().buscarChequesDeJuridico();
				}
				protected void done() {					
					try {						
						chequesJuridico.clear();
						chequesJuridico.addAll(get());
						juridicoChequesView.packAll();
					} catch (Exception e) {
						MessageUtils.showError("Error", e);
					}
				}				
			};
			worker.execute();			
		}
		
		public void pagarCargoNormal(){
			final List<NotaDeCredito> cargos=new ArrayList<NotaDeCredito>();
			cargos.addAll(cargosSelectionModel.getSelected());
			CXCFiltros.filtrarCargosConSaldo(cargos);
			CXCFiltros.filtrarCargosMismoCliente(cargos);
			if(cargos!=null){
				final PagosFactory fac=new PagosFactoryImpl();				
				final PagoM pago=fac.crearPago(cargos.get(0).getCliente(), cargos);
				final PagoDeCargosModel model=new PagoDeCargosModel(pago);
				final PagoDeCargosForm form=new PagoDeCargosForm(model);
				form.open();
				if(!form.hasBeenCanceled()){
					pago.setComentario("Pago de Cargo por ch. dev");
					for(Pago p:pago.getPagos()){
						p.setOrigen("CHE");						
					}
					ServiceLocator.getPagosManager().salvarGrupoDePagos(pago);
					for(NotaDeCredito c:cargos){
						int index=cargosSource.indexOf(c);
						cargosSource.set(index, c);
						load();
					}
				}
			}
		}
		
		public void pagarCargoConNota(){
			
			final List<NotaDeCredito> cargos=new ArrayList<NotaDeCredito>();
			cargos.addAll(cargosSelectionModel.getSelected());
			CXCFiltros.filtrarCargosConSaldo(cargos);
			CXCFiltros.filtrarCargosMismoCliente(cargos);
			if(cargos!=null){				
				final Cliente c=cargos.get(0).getCliente();
				final EventList<NotaDeCredito> notas=GlazedLists.eventList(ServiceLocator.getNotasManager().buscarNotasDeCreditoDisponibles(c));
				if(notas.isEmpty()){
					MessageUtils.showMessage(MessageFormat.format("El cliente {0} ({1})\n No tiene notas disponibles para usar como forma de pago"
							, c.getNombre(),c.getClave()), "Notas disponibles");
					return ;
				}
				final NotaDeCredito origen=Selectores.seleccionarNotaDeCredito(c, notas);
				//Procedemos con el pago
				if(origen!=null){
					final PagosFactory fac=new PagosFactoryImpl();
					final PagoConNota pago=fac.crearPagoDeCargoConNota(origen, cargos);					
					final PagoFormModel model=new DefaultPagoFormModelImpl(pago,false);	
					final PagoConNotaForm form=new PagoConNotaForm(model);
					form.open();
					if(!form.hasBeenCanceled()){
						pago.setComentario("Pago de Cargo por ch. dev");
						for(Pago p:pago.getPagos()){
							p.setOrigen("CHE");
						}
						ServiceLocator.getPagosManager().salvarGrupoDePagos(pago);
						load();
					}
				}
			}	
			
		}
		
		/*
		public void pagarCargoConDisponible(){
			
		}*/
		
		public void transpasoJuridico(){
			if(tabPanel.getSelectedIndex()==0){
				transferirCheque();
			}else if(tabPanel.getSelectedIndex()==2){
				transferirCargo();
			}
		}
		
		private void transferirCheque(){
			final List<ChequeDevuelto> selected=new ArrayList<ChequeDevuelto>();
			selected.addAll(selectionModel.getSelected());
			if(!selected.isEmpty()){
				final ValueHolder fh=new ValueHolder();
				final SXAbstractDialog dialog=Binder.createDateSelector(fh);
				dialog.setTitle("Fehca de traspaso ( Cheques )");
				dialog.open();
				if(!dialog.hasBeenCanceled()){
					for(ChequeDevuelto c:selected){
						ServiceLocator.getJuridicoManager().transferirJuridico(c, (Date)fh.getValue());
					}
					MessageUtils.showMessage("Traspaso terminado", "Jurídico");
					load();
				}
			}
		}
		
		private void transferirCargo(){
			final List<NotaDeCredito> cargos=new ArrayList<NotaDeCredito>();
			cargos.addAll(cargosSelectionModel.getSelected());
			if(!cargos.isEmpty()){
				final ValueHolder fh=new ValueHolder();
				final SXAbstractDialog dialog=Binder.createDateSelector(fh);
				dialog.setTitle("Fehca de traspaso ( Cargos )");
				dialog.open();
				if(!dialog.hasBeenCanceled()){
					for(NotaDeCredito cargo:cargos){
						ServiceLocator.getJuridicoManager().transferirJuridico(cargo, (Date)fh.getValue());
					}
					MessageUtils.showMessage("Traspaso terminado", "Jurídico");
					load();
				}
			}
		}
		
	}

}
