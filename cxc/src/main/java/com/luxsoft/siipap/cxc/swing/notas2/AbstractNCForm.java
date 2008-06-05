package com.luxsoft.siipap.cxc.swing.notas2;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
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
import com.luxsoft.siipap.cxc.domain.ConceptoDeBonificacion;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.swing.form.AbstractForm;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;


public abstract class AbstractNCForm extends AbstractForm{

	private Action insertAction;
	private Action viewAction;
	private Action deleteAction;
	private Action editAction;
	private NotasManager manager;
	
	protected JXTable partidasGrid;
	
	private EventSelectionModel<NotasDeCreditoDet> selectionModel;
	
	public AbstractNCForm(NotaDeCredito nota) {
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
		if(!getNCModel().getFormBean().getTipo().equals("L"))
			builder.append("Concepto",add("bonificacion"));
		else builder.nextLine();
		appendComponents(builder);
		//builder.nextLine();
		builder.append("Importe",add("importe"),true);		
		builder.append("Iva",add("iva"),true);
		builder.append("Total",add("totalAsMoneda"),true);
		builder.append("Comentario 1",add("comentario"),5);
		builder.nextLine();
		builder.append("Comentario 2",add("comentario"),5);
		return builder.getPanel();
	}
	
	/**
	 * Agregar componentes especificos para una nota de credito
	 * 
	 * @param builder
	 */
	protected void appendComponents(final DefaultFormBuilder builder){
		
	}
	
	protected JComponent buildPartidasPanel(){
		JPanel p=new JPanel(new BorderLayout());
		p.add(buildToolbar(),BorderLayout.NORTH);
		p.add(buildGridPanel(),BorderLayout.CENTER);
		return p;
	}
	
	protected JComponent buildToolbar(){
		ToolBarBuilder builder=new ToolBarBuilder();
		builder.add(getInsertAction());
		builder.add(getDeleteAction());
		builder.add(getEditAction());
		builder.add(getViewAction());
		return builder.getToolBar();
	}
	
	protected JComponent buildGridPanel(){
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
		if(getDetalleForm(det)==null)
			return;
		AbstractForm form=getDetalleForm(det);
		FormDialog dialog=new FormDialog(form);
		dialog.setTitle("Agregar detalle");
		dialog.setDescription("Partida de la nota de credito");
		dialog.open();
		if(!dialog.hasBeenCanceled()){			
			getNCModel().addPartida(det);
			partidasGrid.packAll();
		}
		dialog.dispose();
	}
	
	public void view(){
		
	}
	
	protected abstract AbstractForm getDetalleForm(final NotasDeCreditoDet det);
	
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
	
	

}
