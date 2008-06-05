package com.luxsoft.siipap.cxc.pagos;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.VerticalLayout;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.model.PagosFactoryImpl;
import com.luxsoft.siipap.cxc.model2.PagoConOtrosModel;
import com.luxsoft.siipap.cxc.model2.PagoConOtrosModelImpl;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.cxc.utils.CXCEntityShow;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.form2.IFormModel;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para  aplicar pagos con 
 * 
 * @author Ruben Cancino
 *
 */
public class PagoConOtrosForm extends AbstractForm{
	
	private JXTable grid;	
	private JTextField disponible;
	
	private EventSelectionModel<Pago> selectionModel;	
	private Action deleteAction;
	

	public PagoConOtrosForm(final IFormModel model) {
		super(model);
		getModel().addBeanPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if(grid!=null)grid.packAll();
			}			
		});
	}
	
	private PagoConOtrosModel getModel(){
		return (PagoConOtrosModel)model;
	}
	
	private void initComponents(){		
		disponible=new JTextField(model.getValue("origen.disponible").toString());
		disponible.setFocusable(false);
		disponible.setEditable(false);
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
		builder.append("Fecha",getControl("fecha"));
		builder.append("F. Pago",getControl("formaDePago"),true);
		builder.append("Origen",getControl("referencia"),true);
		//builder.append("Cuenta Depósito",getControl("cuentaDeposito"),true);
		builder.append("Importe",getControl("importe"));
		builder.append("Disponible",disponible,true);
		final CellConstraints cc=new CellConstraints();		
		builder.append("Comentario");
		builder.appendRow(new RowSpec("17dlu"));
		builder.add(new JScrollPane(getControl("comentario")),
				cc.xywh(builder.getColumn(), builder.getRow(),5,2));
		builder.nextLine(2);
		if(getModel().isParaFacturas())
			builder.append("Condonar",getControl("condonar"));		
		ComponentUtils.decorateSpecialFocusTraversal(builder.getPanel());
		return builder.getPanel();
	}
	
	
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if(property.equals("comentario")){
			JComponent c=BasicComponentFactory.createTextArea(model.getModel("comentario"), false);
			ValidationComponentUtils.setMessageKey(c, "PagoConOtros.comentario");
			return c;
		}if(property.equals("formaDePago")){
			JTextField c=new JTextField(FormaDePago.S.getDesc());
			c.setFocusable(false);
			c.setEditable(false);
			return c;
		}if("referencia".equals(property)){
			JTextField c=new JTextField(getModel().getValue("origen.id").toString());
			c.setFocusable(false);
			c.setEditable(false);
			return c;
		}
		return null;
	}

	protected JComponent buildDetailsPanel(){
		final TableFormat<Pago> tf=getModel().isParaFacturas()
			?CXCTableFormats.getPagoConVentaTF()
			:CXCTableFormats.getPagoConNotaTF();
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
		
		if(getModel().isParaFacturas()){
			grid.getColumn("Desc (%)").setCellRenderer(Renderers.getPorcentageRenderer());
			//grid.getColumn("Desc (%)").setPreferredWidth(100);
			grid.getColumn("Cgo").setCellRenderer(Renderers.getPorcentageRenderer());			
			grid.getColumn("Desc Aplic").setCellRenderer(Renderers.getCantidadNormalTableCellRenderer());			
		}
		grid.setColumnControlVisible(false);
		grid.packAll();
		final JScrollPane sp=new JScrollPane(grid);
		sp.setPreferredSize(new Dimension(700,300));
		return sp;
	}
	
	
	
	@Override
	protected JComponent buildHeader() {
		String desc=MessageFormat.format("{0}  ({1})", model.getValue("cliente.nombre").toString(),model.getValue("cliente.clave").toString());
		return new HeaderPanel("Pago con otros productos",desc);
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
		final EventList<Venta> ventas=DatosDePrueba.ventasDePrueba();
		final Cliente c=ventas.get(0).getCliente();
		
		final PagoM origen=new PagoM();
		origen.setCliente(c);
		//origen.setDisponible(CantidadMonetaria.pesos(500));
		origen.setId(40l);
		
		final PagosFactoryImpl fac=new PagosFactoryImpl();
		final PagoConOtros pago=fac.crearPago(origen, ventas);
		pago.setOrigen(origen);
		pago.setCliente(origen.getCliente());
		final PagoConOtrosModelImpl model=new PagoConOtrosModelImpl(true,pago);
		final PagoConOtrosForm form=new PagoConOtrosForm(model);
		form.open();
	}

}
