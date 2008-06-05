package com.luxsoft.siipap.cxc.swing.notas2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ComponentValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.util.UIFactory;
import com.jgoodies.validation.ValidationResult;
import com.luxsoft.siipap.cxc.domain.ConceptoDeBonificacion;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.NotasFactory;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.swing.form.AbstractForm;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;


public class NCBonificacionForm extends AbstractForm{

	private Action insertAction;
	private Action viewAction;
	private Action deleteAction;
	private Action editAction;
	private NotasManager manager;
	
	private JXTable partidasGrid;
	
	private EventSelectionModel<NotasDeCreditoDet> selectionModel;
	
	public NCBonificacionForm(NotaDeCredito nota) {
		super(new NCFormModel(nota));
		
	}
	
	protected JComponent buildFormPanel() {
		PanelBuilder builder=new PanelBuilder(new FormLayout("p:g","p,2dlu,p"));
		CellConstraints cc=new CellConstraints();
		builder.add(buildEditorPanel(),cc.xy(1, 1));
		builder.add(buildPartidasPanel(),cc.xy(1, 3));
		return builder.getPanel();
	}

	
	protected JComponent buildEditorPanel() {
		FormLayout layout=new FormLayout(
				"l:max(40dlu;p),2dlu,max(p;60dlu) ,3dlu, " +
				"l:max(40dlu;p),2dlu,max(p;60dlu):g " 
				,"");
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Cliente",add("cliente"),5);
		builder.nextLine();
		builder.append("Fecha",add("fecha"));		
		builder.append("Concepto",add("bonificacion"));
		builder.append("Importe",add("importe"),true);		
		builder.append("Iva",add("iva"),true);
		builder.append("Total",add("totalAsMoneda"),true);
		builder.append("Comentario 1",add("comentario"),5);
		builder.nextLine();
		builder.append("Comentario 2",add("comentario2"),5);
		return builder.getPanel();
	}
	
	private JComponent buildPartidasPanel(){
		JPanel p=new JPanel(new BorderLayout());
		p.add(buildToolbar(),BorderLayout.NORTH);
		p.add(buildGridPanel(),BorderLayout.CENTER);
		return p;
	}
	private JComponent buildToolbar(){
		ToolBarBuilder builder=new ToolBarBuilder();
		builder.add(getInsertAction());
		builder.add(getDeleteAction());
		builder.add(getEditAction());
		builder.add(getViewAction());
		return builder.getToolBar();
	}
	private JComponent buildGridPanel(){
		partidasGrid=ComponentUtils.getStandardTable();
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class, getPartidasProps(), getPartidasLabels());
		final SortedList<NotasDeCreditoDet> sortedList=new SortedList<NotasDeCreditoDet>(getNCModel().getPartidasList(),null);
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(sortedList,tf);
		selectionModel=new EventSelectionModel<NotasDeCreditoDet>(sortedList);
		partidasGrid.setModel(tm);
		partidasGrid.setSelectionModel(selectionModel);
		ComponentUtils.addEnterAction(partidasGrid, getViewAction());
		ComponentUtils.addDeleteAction(partidasGrid, getDeleteAction());
		ComponentUtils.addInsertAction(partidasGrid, getInsertAction());
		ComponentUtils.decorateActions(partidasGrid);
		new TableComparatorChooser<NotasDeCreditoDet>(partidasGrid,sortedList,true);
		JComponent c=UIFactory.createTablePanel(partidasGrid);
		c.setPreferredSize(new Dimension(400,250));
		return c;
	}
	
	protected String[] getPartidasProps(){
		return new String[]{"factura.numero","factura.fecha","factura.total","factura.saldo","importe","comentario"};
	}
	protected String[] getPartidasLabels(){
		return new String[]{"Venta","Fecha Fac","Total Fac","Saldo Fac","Importe (N.C)","Comentario"};
	}
	
	public void insert(){
		NotasDeCreditoDet det=getNCModel().getPartida();
		AbstractForm form=getDetalleForm(det);
		FormDialog dialog=new FormDialog(form);
		dialog.setTitle("Agregar detalle");
		dialog.setDescription("Partida de la nota de credito");
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			System.out.println(NotasFactory.toString(det));
			getNCModel().addPartida(det);
			partidasGrid.packAll();
		}
		dialog.dispose();
	}
	
	public void view(){
		System.out.println("View...");
	}
	
	protected AbstractForm getDetalleForm(final NotasDeCreditoDet det){
		NCForm form=new NCForm(det);
		form.setManager(getManager());
		return form;
	}
	
	public Action getInsertAction(){
		if(insertAction==null){
			insertAction=CommandUtils.createInsertAction(this, "insert");
		}
		return insertAction;
	}
	
	private Action getViewAction(){
		if(viewAction==null){
			viewAction=CommandUtils.createViewAction(this,"view");
		}
		return viewAction;
	}
	
	public Action getDeleteAction(){
		if(deleteAction==null){
			deleteAction=CommandUtils.createDeleteAction(this, "delete");
		}
		return deleteAction;
	}
	private Action getEditAction(){
		if(editAction==null){
			editAction=CommandUtils.createEditAction(this,"edit");
		}
		return editAction;
	}
	
	@Override
	protected JComponent getCustomComponent(String property, ComponentValueModel vm) {
		if("bonificacion".equals(property)){
			SelectionInList sl=new SelectionInList(ConceptoDeBonificacion.values(),vm);
			return BasicComponentFactory.createComboBox(sl);
		}
		return super.getCustomComponent(property, vm);
	}
	
	
	
	public NCFormModel getNCModel(){
		return (NCFormModel)getFormModel();
	}

	public NotasManager getManager() {
		return manager;
	}

	public void setManager(NotasManager manager) {
		this.manager = manager;
	}
	
	public void commit(){
		getNCModel().commit();
	}

	/**
	 * Modelo para el estado y comportamiento de las notas de credito
	 * 
	 * @author Ruben Cancino
	 *@deprecated User NCFormModel
	 */
	public static class NCBonificacionFormModel extends AbstractGenericFormModel<NotaDeCredito, Long>{
		
		private EventList<NotasDeCreditoDet> notasDet;
		private ListEventListener sincronizador;

		public NCBonificacionFormModel(NotaDeCredito bean) {
			super(bean);			
			notasDet=GlazedLists.eventList(new BasicEventList<NotasDeCreditoDet>());
			//Posiblemente hablitar para mantenimiento
			sincronizador=GlazedLists.syncEventListToList(notasDet, bean.getPartidas());
			notasDet.addListEventListener(new PartidasHandler());
			
		}
		
		/**
		 * Actualiza los cambios del cliente limpiando la lista de partidas de la nota
		 * esto por que no puede haber partidas de notas que no sean del mismo cliente
		 * que el maestro
		 *
		 */
		private void updateCliente(){
			notasDet.clear();
		}

		@Override
		protected void initModels() {			
			super.initModels();
			getComponentModel("importe").setEditable(false);			
			getComponentModel("iva").setEditable(false);
			getComponentModel("totalAsMoneda").setEditable(false);			
		}
		

		@Override
		protected void initEventHandling() {			
			super.initEventHandling();
			getModel("cliente").addValueChangeListener(new ClienteHandler());
		}

		@Override
		public ValidationResult validate() {			
			ValidationResult r=super.validate();
			if(notasDet.size()==0)
				r.addError("No se han asignado ventas");
			return r;
		}
		
		/**
		 * Actualiza las partidas, principamente informa al bean que recalcule 
		 * el importe de la nota de credito en funcion de sus partidas, asi como 
		 * informar al ValidationModel
		 *
		 */
		private void updatePartidas(){			
			getFormBean().actualizar();
			updateValidation();
		}
		
		/**
		 * Acceso a la lista de las partidas, misma que esta sincronizada con las partidas
		 * del bean NotaDeCredito
		 * 
		 * @return
		 */
		public EventList<NotasDeCreditoDet> getPartidasList(){
			return notasDet;
		}
		
		/**
		 * Regresa una NotasDeCreditoDet adecuada para la nota en edicion
		 * Utiliza NotasFactory
		 * @return
		 */
		public NotasDeCreditoDet getPartida(){
			NotasDeCreditoDet det=NotasFactory.getPartidaDeNota(getFormBean());			
			det.setNota(getFormBean());
			return det;
		}
		
		/**
		 * Agrega una partidad a la lista de partidas, este metodo puede ser intercepatdo por AOP
		 * 
		 * @param det
		 */
		public void addPartida(final NotasDeCreditoDet det){						
			notasDet.add(det);
		} 

		@SuppressWarnings("unchecked")
		@Override
		public void commit() {			
			super.commit();
			notasDet.removeListEventListener(sincronizador);
		}



		/**
		 * Informa de cambios en la lista que mantiene las partidas de la nota
		 * 
		 * @author Ruben Cancino
		 *
		 */
		private class PartidasHandler implements ListEventListener<NotasDeCreditoDet>{

			public void listChanged(ListEvent<NotasDeCreditoDet> listChanges) {
				while(listChanges.hasNext()){
					listChanges.next();
					updatePartidas();
					
				}
			}			
		}
		
		/**
		 * Actualiza el cambio de cliente 
		 * 
		 * @author Ruben Cancino
		 *
		 */
		private class ClienteHandler implements PropertyChangeListener{
			public void propertyChange(PropertyChangeEvent evt) {
				updateCliente();
			}			
		}
		
	}
	
	/*
	public static void main(String[] args) {
		
		NotaDeCredito n=NotasFactory.getNotaDeCreditoBonificacionCRE();
		Cliente c=new Cliente();
		c.setNombre("Union de credito");
		c.setClave("U050008");
		n.setCliente(c);
		n.setClave(c.getClave());
		NCBonificacionForm form=new NCBonificacionForm(n);
		form.setManager((NotasManager)ServiceLocator.getDaoContext().getBean("notasManager"));
		FormDialog dialog=new FormDialog(form);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			NotaDeCredito nt=(NotaDeCredito)form.getBean();
			System.out.println(NotasFactory.toString(nt));
			NotasManager manager=(NotasManager)ServiceLocator.getDaoContext().getBean("notasManager");
			manager.salvarNotaCre(nt);
		}
		System.exit(0);
	}
	*/
	

}
