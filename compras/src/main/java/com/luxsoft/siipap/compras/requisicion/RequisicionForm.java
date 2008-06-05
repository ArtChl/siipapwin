package com.luxsoft.siipap.compras.requisicion;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;


import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;


import com.luxsoft.siipap.compras.ComprasBinding;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class RequisicionForm {
	
	private IRequisicionModel model;
	
	protected JXTable grid;	
	
	private JComponent fecha;
	private JComponent sucursal;
	private JComponent metodo;
	private JComponent comentario;
	private JComponent periodo;
	
	/***GlazedList's*/
	private EventSelectionModel selectionModel;
	
	public RequisicionForm(final IRequisicionModel model){
		this.model=model;
	}
	
	private void initComponents(){
		fecha=Binder.createDateComponent(model.getModel("fecha"));
		sucursal=ComprasBinding.createSucursalesBinding(model.getModel("sucursal"));
		periodo=Binder.createPeriodoBinder(model.getModel("periodo"));
		metodo=ComprasBinding.createMetodoDeAsignacionBinding(model.getModel("metodo"));		
		comentario=BasicComponentFactory.createTextArea(model.getComponentModel("comentario"),false);		
	}
	
	private void initDecorateComponents(){		
		ValidationComponentUtils.setMessageKey(comentario, "Requisicion.comentario");		
	}
	
	private void initEventHandling(){
		
	}
	
	public JComponent getForm(){
		initComponents();
		initDecorateComponents();
		initEventHandling();
		final FormLayout layout=new FormLayout(
				"max(p;380dlu)"
				,"p,3dlu,220dlu,3dlu,50dlu");
		CellConstraints cc=new CellConstraints();
		PanelBuilder builder=new PanelBuilder(layout);
		builder.add(createMasterPanel(),cc.xy(1, 1));
		builder.add(createDetailPanel(),cc.xy(1, 3));
		builder.add(createValidationView(),cc.xy(1,5));
		//model.updateValidation();
		//updateComponentTreeMandatoryAndSeverity(getModel().getValidationModel().getResult(), builder.getPanel());
		return builder.getPanel();
	}
	
	private JComponent createMasterPanel(){
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,f:max(p;50dlu):g(.3) ,3dlu " +
				"l:p,2dlu,f:max(p;50dlu):g(.3) "
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.setRowGroupingEnabled(true);
		builder.append("Fecha",fecha);
		builder.append("Sucursal",sucursal,true);
		builder.append("Método",metodo,true);
		//builder.append("Periodo",periodo,true);
		
		final CellConstraints cc=new CellConstraints();		
		
		//builder.append("Periodo");
		builder.appendRow(new RowSpec("17dlu"));
		//periodo.setBorder(BorderFactory.createEmptyBorder());
		builder.add(periodo,
				cc.xywh(builder.getColumn(), builder.getRow(),5,2));
		builder.nextLine(2);
		
				
		builder.append("Comentario");
		builder.appendRow(new RowSpec("17dlu"));
		builder.add(new JScrollPane(comentario),
				cc.xywh(builder.getColumn(), builder.getRow(),5,2));
		
		return builder.getPanel();
	}
	
	@SuppressWarnings("unchecked")
	private JComponent createDetailPanel(){
		
		final String[] props={
				"clave","descripcion","sugerido"
				};
		final String[] labels={
				"Artículo","Descripción","Cantidad"
				};
		
		final TableFormat tf=GlazedLists.tableFormat(props,labels);
		final EventList pagos=new BasicEventList();
		final SortedList spagos=new SortedList(pagos,null);
		final EventTableModel tm=new EventTableModel(spagos,tf);
		selectionModel=new EventSelectionModel(spagos);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		new TableComparatorChooser(grid,spagos,true);
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addInsertAction(grid,getAction(this, "inser", "insertAction"));
		ComponentUtils.addDeleteAction(grid, getAction(this, "delete", "deleteAction"));
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {				
				if(e.getClickCount()==2)
					edit();
			}			
		});
		final JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	private JComponent createValidationView(){
		return ValidationResultViewFactory.createReportList(model.getValidationModel());
	}
	
	/*** Process Flow ****/
	
	public void iniciarCaptura(){
		model.validate();
	}
	
	public void edit(){
		
	}
	
	/**
	 * Genera una accion que delega su comportamiento a el objeto target
	 * en el metodo de nombre asignado. Normalmente Stateless 
	 * 
	 * @param id
	 * @return
	 */
	public Action getAction(final Object target,final String methodName,final String id){
		if(id.startsWith("insert"))
			return CommandUtils.createInsertAction(target, methodName);
		else if(id.startsWith("delete"))
			return CommandUtils.createDeleteAction(target, methodName);
		else{
			final Action da=new DispatchingAction(target,methodName);
			CommandUtils.configAction(da, id, "images2/exclamation.png");
			return da;
		}
	}
	
	
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		final IRequisicionModel model=new RequisicionModel();
		final RequisicionForm form=new RequisicionForm(model);
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){

			@Override
			protected JComponent buildContent() {
				return form.getForm();
			}
			
		};
		dialog.open();
		System.exit(0);
	}

}
