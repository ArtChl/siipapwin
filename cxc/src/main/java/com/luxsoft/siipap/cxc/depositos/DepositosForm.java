package com.luxsoft.siipap.cxc.depositos;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.VerticalLayout;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.DepositoUnitario;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;

public class DepositosForm extends AbstractForm{
	
	private HeaderPanel header;
	private DateFormat df=new SimpleDateFormat("dd/MM/yyyy");

	public DepositosForm(DepositoFormModel model) {
		super(model);
		model.getModel("fecha").addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {				
				updateHeader();				
			}			
		});
	}
	
	private DepositoFormModel getDepositoModel(){
		return (DepositoFormModel)model;
	}
	

	@Override
	protected JComponent buildFormPanel() {		
		final JPanel formPanel=new JPanel(new VerticalLayout(10));
		formPanel.add(buildMasterPanel());
		formPanel.add(buildDetallePanel());
		return formPanel;
	}
	
	protected JComponent buildMasterPanel() {	
		getControl("importe").setEnabled(false);
		FormLayout layout=new FormLayout(
				"p,2dlu,max(p;70dlu), 3dlu" 
				+",p,2dlu,max(p;70dlu) "
				,""
				);
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.append("Fecha",getControl("fecha"));
		builder.append("Sucursal",getControl("sucursal"));
		builder.append("Forma de pago",getControl("formaDePago"));
		builder.append("Destino",getControl("cuentaDestino"));
		builder.append("Total",getControl("importe"));
		ComponentUtils.decorateSpecialFocusTraversal(builder.getPanel());
		return builder.getPanel();
	}
	
	protected JComponent createCustomComponent(final String property){
		if("sucursal".equals(property)){
			JComponent c=Binder.createSucursalesBinding(model.getModel(property));
			return c;
		}if("formaDePago".equals(property)){
			JComboBox c=CXCBindings.createFormaDePagoBinding(model.getModel(property));
			return c;
		}if("cuentaDestino".equals(property)){
			JComboBox c=CXCBindings.createCuentasDeposito(model.getModel(property));
			return c;
		}
		return null;
	}
	
	private void updateHeader(){
		if(header==null){
			header=new HeaderPanel("Registro de depositos","");			
		}		
		header.setDescription("Fecha: "+df.format((Date)model.getValue("fecha")));
	}
	
	
	@Override
	protected JComponent buildHeader() {
		updateHeader();
		return header;
	}
	
	private JComponent buildDetallePanel(){
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildToolbar(),BorderLayout.NORTH);
		panel.add(buildGrid(),BorderLayout.CENTER);
		return panel;
	}
	
	private JXTable grid;
	private EventList<DepositoUnitario> source;
	private SortedList<DepositoUnitario> sortedList;
	private EventSelectionModel<DepositoUnitario> selectionModel;
	
	private JComponent buildGrid(){
		grid=ComponentUtils.getStandardTable();		
		// GlazedList
		source=getDepositoModel().getPartidas();
		sortedList=new SortedList<DepositoUnitario>(source,null);
		final String[] props={"banco","numero","importe"};
		final String[] names={"Banco","Número","Importe"};
		final TableFormat<DepositoUnitario> tf=GlazedLists.tableFormat(DepositoUnitario.class, props, names);
		final EventTableModel<DepositoUnitario> tm=new EventTableModel<DepositoUnitario>(sortedList,tf);
		selectionModel=new EventSelectionModel<DepositoUnitario>(sortedList);
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		ComponentUtils.addInsertAction(grid, insertAction);
		new TableComparatorChooser<DepositoUnitario>(grid,sortedList,true);
		JScrollPane sc=new JScrollPane(grid);
		return sc;
	}
	private Action insertAction;
	
	private JComponent buildToolbar(){
		
		final ToolBarBuilder builder=new ToolBarBuilder();
		insertAction=CommandUtils.createInsertAction(this, "insert");
		builder.add(insertAction);
		builder.add(CommandUtils.createDeleteAction(this, "delete"));
		builder.add(CommandUtils.createEditAction(this, "edit"));
		builder.add(CommandUtils.createViewAction(this, "view"));
		builder.add(CommandUtils.createPrintAction(this, "print"));
		return builder.getToolBar();
	}
	
	//**** Comportamiento del detalle ***///
	
	public void insert(){
		DepositoUnitario du=DepositoUnitarioForm.editarDeposito();		
		if(du!=null){
			boolean res=getDepositoModel().agregarPartida(du);
			if(!res){
				MessageUtils.showMessage("Imposible registrar el pago/cheque, es probable que este ya este registrado", "Depositos unitarios");
			}
		}
	}
	
	public void delete(){
		if(!selectionModel.getSelected().isEmpty()){
			List<DepositoUnitario> deps=new ArrayList<DepositoUnitario>();
			deps.addAll(selectionModel.getSelected());
			for(DepositoUnitario du:deps){
				getDepositoModel().eliminarPartida(du);
			}
		}
	}
	
	public void view(){
		if(!selectionModel.getSelected().isEmpty()){
			DepositoUnitario du=selectionModel.getSelected().get(0);
			DepositoUnitarioForm.mostrarDeposito(du);
		}
	}
	
	public void edit(){
		if(!selectionModel.getSelected().isEmpty()){
			DepositoUnitario du=selectionModel.getSelected().get(0);
			if(DepositoUnitarioForm.editarDeposito(du)!=null){				
				getDepositoModel().actualizarImporte();
			}
		}
	}
	
	public static void main(String[] args) {
		//final Deposito d=new Deposito();
		final DepositoFormModel model=new DepositoFormModel();
		DepositosForm form=new DepositosForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			model.commit();
			System.out.println("Deposito : "+model.getDeposito());
			ServiceLocator.getUniversalDao().save(model.getDeposito());
		}
		
	}

}
