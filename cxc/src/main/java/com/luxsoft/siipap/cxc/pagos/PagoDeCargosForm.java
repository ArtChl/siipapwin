package com.luxsoft.siipap.cxc.pagos;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.VerticalLayout;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.model.PagosFactoryImpl;
import com.luxsoft.siipap.cxc.model2.PagoDeCargosModel;
import com.luxsoft.siipap.cxc.model2.PagoFormModel;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.cxc.utils.CXCEntityShow;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * Forma para  aplicar pagos con 
 * 
 * @author Ruben Cancino
 *
 */
public class PagoDeCargosForm extends AbstractForm{
	
	private JXTable grid;	
	
	
	private EventSelectionModel<Pago> selectionModel;	
	private Action deleteAction;
	

	public PagoDeCargosForm(final PagoFormModel model) {
		super(model);
		getModel().addBeanPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if(grid!=null)grid.packAll();
			}			
		});
	}
	
	public PagoFormModel getModel(){
		return (PagoFormModel)model;
	}
	
	private void initComponents(){		
		
	}

	@Override
	protected JComponent buildFormPanel() {
		initComponents();
		final JPanel panel=new JPanel(new VerticalLayout());
		panel.add(buildMasterPanel());
		panel.add(buildDetailsPanel());
		return panel;
	}
	
	protected JComponent buildMasterPanel(){
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,max(60dlu;p):g(.3) ,2dlu " +
				"l:p,2dlu,max(60dlu;p):g(.7) " +
				"","");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		
		builder.append("Fecha",getControl("fecha"));
		//builder.append("Forma de Pago",getControl("formaDePago"),true);
		getControl("formaDePago").setEnabled(false);
		getControl("referencia").setEnabled(false);
		getControl("banco").setEnabled(false);
		getControl("cuentaDeposito").setEnabled(false);
		
		builder.append("Forma de Pago",getControl("formaDePago"),true);		
		builder.append("Banco",getControl("banco"));
		builder.append("Referencia",getControl("referencia"),true);
		builder.append("Cuenta Depósito",getControl("cuentaDeposito"));
		builder.append("Pago",buildPagosbox(getModel().getModel("depositoRow")));
		builder.append("Importe",getControl("importe"));
		builder.append("Disponible",getControl("disponible"),true);
		final CellConstraints cc=new CellConstraints();		
		builder.append("Comentario");
		builder.appendRow(new RowSpec("17dlu"));
		builder.add(new JScrollPane(getControl("comentario")),
				cc.xywh(builder.getColumn(), builder.getRow(),5,2));
		builder.nextLine(2);
		
		ComponentUtils.decorateSpecialFocusTraversal(builder.getPanel());
		return builder.getPanel();
	}
	
	
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if(property.equals("comentario")){
			JComponent c=BasicComponentFactory.createTextArea(model.getModel("comentario"), false);
			ValidationComponentUtils.setMessageKey(c, "PagoM.comentario");
			return c;
		}if(property.equals("formaDePago")){
			JComboBox c=CXCBindings.createFormaDePagoBinding(model.getModel("formaDePago"));			
			return c;
		}else if(property.equals("disponible")){
			JFormattedTextField c=Binder.createMonetariNumberBinding(model.getModel("disponible"));
			c.setEditable(false);
			c.setFocusable(false);
			return c;
		}else if(property.equals("banco")){
			JComponent c=CXCBindings.createBancosBinding(model.getModel("banco"));
			return c;
		}else if(property.equals("cuentaDeposito")){
			JComponent c=CXCBindings.createCuentasDeposito(model.getModel("cuentaDeposito"));
			return c;
		}
		return null;
	}

	protected JComponent buildDetailsPanel(){
		final TableFormat<Pago> tf=CXCTableFormats.getNotaDeCargoTF();
		final EventList<Pago> pagos=getModel().getPagos();
		final SortedList<Pago> spagos=new SortedList<Pago>(pagos,null);
		final EventTableModel<Pago> tm=new EventTableModel<Pago>(spagos,tf);
		selectionModel=new EventSelectionModel<Pago>(spagos);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		new TableComparatorChooser<Pago>(grid,spagos,true);
		ComponentUtils.decorateActions(grid);		
		ComponentUtils.addDeleteAction(grid, getDeleteAction());
		grid.addMouseListener(new MouseAdapter(){			
			public void mouseClicked(MouseEvent e) {				
				if(e.getClickCount()==2)
					view();
			}			
		});
		/*
		if(getModel().isParaFacturas()){
			grid.getColumn("Desc (%)").setCellRenderer(Renderers.getPorcentageRenderer());
			//grid.getColumn("Desc (%)").setPreferredWidth(100);
			grid.getColumn("Cgo").setCellRenderer(Renderers.getPorcentageRenderer());			
			grid.getColumn("Desc Aplic").setCellRenderer(Renderers.getCantidadNormalTableCellRenderer());			
		}*/
		grid.setColumnControlVisible(false);
		grid.packAll();
		final JScrollPane sp=new JScrollPane(grid);
		sp.setPreferredSize(new Dimension(700,300));
		return sp;
	}
	
	private JComboBox buildPagosbox(final ValueModel vm){
		SelectionInList sl=new SelectionInList(getModel().buscarDepositosDisponibles(),vm);
		return BasicComponentFactory.createComboBox(sl);
	}
	
	@Override
	protected JComponent buildHeader() {
		String desc=MessageFormat.format("{0}  ({1})", model.getValue("cliente.nombre").toString(),model.getValue("cliente.clave").toString());
		return new HeaderPanel("Pago de cargos",desc);
	}

	public void view(){
		if(getSelectedPago()!=null)
			CXCEntityShow.mostrarEntidad(getSelectedPago());
	}
	
	protected Action getDeleteAction(){
		if(deleteAction==null){
			deleteAction=CommandUtils.createDeleteAction(this, "delete");
		}
		return deleteAction;
	}
	
	
	private Pago getSelectedPago(){
		if(selectionModel.getSelected().isEmpty())
			return null;
		else
			return selectionModel.getSelected().get(0);
	}
	
	public void deletePartida(){
		if(!selectionModel.getSelected().isEmpty()){
			final Pago p=selectionModel.getSelected().get(0);
			getModel().getPagos().remove(p);
		}
	}	
	

	public static void main(String[] args) {
		final Cliente c=DatosDePrueba.createClienteDePrueba();
		final EventList<NotaDeCredito> cargos=DatosDePrueba.crearNotasDeCargo(c);
		final PagosFactoryImpl fac=new PagosFactoryImpl();
		final PagoM pago=fac.crearPago(c, cargos);
		pago.setId(40l);
		
		final PagoDeCargosModel model=new PagoDeCargosModel(pago);
		final PagoDeCargosForm form=new PagoDeCargosForm(model);
		form.open();
	}

}
