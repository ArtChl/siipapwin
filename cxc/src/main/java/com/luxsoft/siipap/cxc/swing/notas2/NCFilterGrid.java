package com.luxsoft.siipap.cxc.swing.notas2;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.uif.AbstractDialog;
import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.TaskUtils;

/**
 * Grid con FilterList para las notas de credito
 * 
 * @author Ruben Cancino
 *
 */
public class NCFilterGrid {
	
	private Logger logger=Logger.getLogger(getClass());	
	private NCMantenimiento mantenimiento;
	private JXTable grid;
	private JTextField cliente=new JTextField();
	private JTextField numero=new JTextField();
	private JTextField tipo=new JTextField();	
	private JLabel periodo;
	private DateMatcherEditor dateEditor=new DateMatcherEditor();
	
	private EventList<NotaDeCredito> notas;	
	private SortedList<NotaDeCredito> sortedNotas;
	private EventSelectionModel<NotaDeCredito> selectionModel;
	
	
	private ValueHolder periodoHolder;
	
	private Action loadAction;
	private Action periodoAction;
	private Action deleteAction;
	private Action reImprimir;
	
	public NCFilterGrid(){		
		Calendar c=Calendar.getInstance();
		Date d1=c.getTime();
		c.add(Calendar.DATE, -90);		 
		periodoHolder=new ValueHolder(new Periodo(c.getTime(),d1));
		periodo=new JLabel(getPeriodo().toString());
		initActions();
	}
	
	private void initActions(){
		loadAction=CommandUtils.createLoadAction(this, "load");
		loadAction.putValue(Action.NAME, "Cargar notas");
		periodoAction=new DispatchingAction(this,"cambiarPeriodo");
		CommandUtils.configAction(periodoAction, "seleccionarPeriodo","images2/calendar.png");
		deleteAction=new DispatchingAction(this,"delete");
		CommandUtils.configAction(deleteAction, CXCActions.EliminarNotasDeCredito.getId(), "images2/application_form_delete.png");
		reImprimir=new DispatchingAction(this,"reimpresion");
		CommandUtils.configAction(reImprimir,CXCActions.ReImprimirNotas.getId(),"images2/printer_add.png");
	}
	
	public Action getLoadAction() {
		return loadAction;
	}
	
	public Action getPeriodoAction(){
		return periodoAction;
	}

	public Action getDeleteAction(){
		return deleteAction;
	}
	public Action getReImprimir(){
		return reImprimir;
	}

	public JXTable getGrid() {
		if(grid==null){
			grid=createGrid();
		}
		return grid;
	}


	private JXTable createGrid(){		
		final JXTable grid=ComponentUtils.getStandardTable();
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addEnterAction(grid, new AbstractAction("enter"){
			public void actionPerformed(ActionEvent e) {
				select();
			}
		});
		grid.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2) select();
			}
		});
		final String[] props={"id","clave","cliente.nombre","fecha","origen","serie","tipo","numero","numeroFiscal","importe"};
		final String[] cols={"Id","Cliente","Nombre","Fecha","Origen","Serie","Tipo","Numero","Numero F.","Importe"};
		final TableFormat<NotaDeCredito> tf=GlazedLists.tableFormat(NotaDeCredito.class,props, cols);
		final EventList<NotaDeCredito> source=createFilterList(getNotas());
		final EventTableModel<NotaDeCredito> tm=new EventTableModel<NotaDeCredito>(source,tf);
		selectionModel=new EventSelectionModel<NotaDeCredito>(source);
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		new TableComparatorChooser<NotaDeCredito>(grid,sortedNotas,true);
		
		return grid;
	}
	
	private EventList<NotaDeCredito> createFilterList(final EventList<NotaDeCredito> source){
		//notas=GlazedLists.threadSafeList(new BasicEventList<NotaDeCredito>());
		sortedNotas=new SortedList<NotaDeCredito>(notas,null);
		//Filtrar por cliente
		//cliente=new JTextField();
		final TextFilterator<NotaDeCredito> clienteFilterator=GlazedLists.textFilterator(new String[]{"cliente.clave","cliente.nombre"});
		final MatcherEditor<NotaDeCredito> clienteEditor=new TextComponentMatcherEditor<NotaDeCredito>(cliente,clienteFilterator);		
		final FilterList<NotaDeCredito> clienteList=new FilterList<NotaDeCredito>(sortedNotas,new ThreadedMatcherEditor<NotaDeCredito>(clienteEditor));
		
		//Filtrar por numero
		//numero=new JTextField();
		final TextFilterator<NotaDeCredito> numeroFilterator=GlazedLists.textFilterator(new String[]{"numero"});
		final MatcherEditor<NotaDeCredito> numeroEditor=new TextComponentMatcherEditor<NotaDeCredito>(numero,numeroFilterator);		
		final FilterList<NotaDeCredito> numeroList=new FilterList<NotaDeCredito>(clienteList,new ThreadedMatcherEditor<NotaDeCredito>(numeroEditor));
		
		//Filtrar por Factura
		//factura=new JTextField();
		final TextFilterator<NotaDeCredito> tipoFilterator=GlazedLists.textFilterator(new String[]{"tipo"});
		final MatcherEditor<NotaDeCredito> tipoEditor=new TextComponentMatcherEditor<NotaDeCredito>(tipo,tipoFilterator);		
		final FilterList<NotaDeCredito> tipoList=new FilterList<NotaDeCredito>(numeroList,new ThreadedMatcherEditor<NotaDeCredito>(tipoEditor));
		
		//Filtrar por fecha
		//dateEditor=new DateMatcherEditor();
		//final FilterList<NotaDeCredito> fechaList=new FilterList<NotaDeCredito>(tipoList,dateEditor);		
		//return fechaList;
		return tipoList;
	}
	
	/**
	 * Thread safe EventList de notas de credito
	 * @return
	 */
	public EventList<NotaDeCredito> getNotas() {
		if(notas==null){
			//notas=createFilterList();
			notas=GlazedLists.threadSafeList(new BasicEventList<NotaDeCredito>());
		}
		return notas;
	}


	public void select(){
		System.out.println("seleccionando");
	}
	
	public void load(){
		if(getMantenimiento()==null) return;
		System.out.println("Cargando notas para el periodo: "+getPeriodo());		
		SwingWorker<List<NotaDeCredito>,String> worker=new SwingWorker<List<NotaDeCredito>, String>(){
			protected List<NotaDeCredito> doInBackground() throws Exception {
				return getMantenimiento().buscarNotas(getPeriodo());
			}
			protected void done() {
				try {
					getNotas().clear();
					getNotas().addAll(get());					
					periodo.setText(getPeriodo().toString());
					getGrid().packAll();
					if(logger.isDebugEnabled()){
						logger.debug("Nots cargadas: "+getNotas().size());
					}
				} catch (Exception e) {
					e.printStackTrace();
					MessageUtils.showError("Error al cargar notas", e);
				}
			}
		};
		TaskUtils.executeSwingWorker(worker);
	}
	
	public void delete(){		
		if(getSelectionModel().getSelected().isEmpty()) return;
		if(getMantenimiento()==null) return;
		final NotaDeCredito n=getSelectionModel().getSelected().get(0);
		boolean res=getMantenimiento().eliminarNota(n);
		if(res)
			getSelectionModel().getSelected().remove(0);
	}
	
	public void dispose(){
		getNotas().clear();
	}
	
	public Periodo getPeriodo(){
		return (Periodo)periodoHolder.getValue();
	}
	
	public JLabel getPeriodoLabel(){
		return this.periodo;
	}
	
	public void reimpresion(){
		if(getSelectionModel().getSelected().isEmpty()) return;
		for(NotaDeCredito nota:getSelectionModel().getSelected()){
			if(getMantenimiento()!=null)
				getMantenimiento().reImprimir(nota);
		}
	}
	
	public void cambiarPeriodo(){
		final ValueModel vm=new ValueHolder(getPeriodo());		
		AbstractDialog dialog=Binder.createPeriodoSelector(vm);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			this.periodoHolder.setValue(vm.getValue());			
			load();
		}		
		dialog.dispose();
	}
	
	public JTextField getCliente() {
		return cliente;
	}
	
	public DateMatcherEditor getDateEditor() {
		return dateEditor;
	}
	
	public JTextField getTipo() {
		return tipo;
	}
	
	public JTextField getNumero() {
		return numero;
	}
	
	public EventSelectionModel<NotaDeCredito> getSelectionModel() {
		return selectionModel;
	}
	
	
	public NCMantenimiento getMantenimiento() {
		return mantenimiento;
	}

	public void setMantenimiento(NCMantenimiento mantenimiento) {
		this.mantenimiento = mantenimiento;
	}
	
	public static class DateMatcherEditor extends AbstractMatcherEditor<NotaDeCredito> implements PropertyChangeListener{
		
		private JXDatePicker picker;
		
		public DateMatcherEditor(){
			picker=new JXDatePicker();
			picker.setFormats("dd/MM/yyyy","dd/MM/yy");
			picker.getEditor().addPropertyChangeListener("value", this);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireChanged(new DateMatcher());			
		}

		public JXDatePicker getPicker(){
			return picker;
		}
		
		private class DateMatcher implements Matcher<NotaDeCredito>{

			public boolean matches(NotaDeCredito item) {
				return item.getFecha().equals(picker.getDate());
			}
			
		}
		
	}

	



	

	
	

	



}
