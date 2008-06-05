package com.luxsoft.siipap.cxc.pagos;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.Date;

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
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.model.PagosFactoryImpl;
import com.luxsoft.siipap.cxc.model2.DefaultPagoFormModelImpl;
import com.luxsoft.siipap.cxc.model2.PagoFormModel;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.cxc.utils.CXCEntityShow;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para  aplicar pagos con Nota de crédito
 * 
 * @author Ruben Cancino
 *
 */
public class PagoConNotaForm extends AbstractForm{
	
	private JXTable grid;	
	private JTextField disponible;
	
	private EventSelectionModel<Pago> selectionModel;	
	private Action deleteAction;
	

	public PagoConNotaForm(final PagoFormModel model) {
		super(model);
		model.addBeanPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if(grid!=null)grid.packAll();
			}			
		});
	}
	
	private PagoFormModel getModel(){
		return (PagoFormModel)model;
	}
	
	private void initComponents(){		
		//disponible=new JTextField(getModel().getDisponible().toString());
		disponible=new JTextField(getModel().getDisponible().toString());
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
			ValidationComponentUtils.setMessageKey(c, "PagoConNota.comentario");
			return c;
		}if(property.equals("formaDePago")){
			JTextField c=new JTextField(model.getValue(property).toString());
			c.setFocusable(false);
			c.setEditable(false);
			return c;
		}if("referencia".equals(property)){
			JTextField c=new JTextField(getModel().getValue("nota.id").toString());
			c.setFocusable(false);
			c.setEditable(false);
			return c;
		}
		return null;
	}

	protected JComponent buildDetailsPanel(){
		final TableFormat<Pago> tf=getModel().isParaFacturas()
			?CXCTableFormats.getPagoConVentaTF()
			:CXCTableFormats.getNotaDeCargoTF();
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
		return new HeaderPanel("Pago con Nota de Crédito",desc);
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
		final NotaDeCredito nota=new NotaDeCredito(){

			@Override
			public double getSaldo() {
				return -45000;
			}
			
		};
		Cliente c=DatosDePrueba.createClienteDePrueba();
		nota.setId(new Long(23));
		nota.setCliente(c);
		nota.setClave(c.getClave());

		/**
		final EventList<Venta> ventas=DatosDePrueba.ventasDePrueba();
		final Cliente c=ventas.get(0).getCliente();
		
		
		final PagosFactoryImpl fac=new PagosFactoryImpl();
		final PagoConNota pago=fac.crearPagoConNota(nota, ventas);
		**/
		final EventList<NotaDeCredito> cargos=DatosDePrueba.createCargosDePrueba();
		final PagosFactoryImpl fac=new PagosFactoryImpl();
		final PagoConNota pago=fac.crearPagoDeCargoConNota(nota, cargos);
		pago.setFecha(new Date());		
		final PagoFormModel model=new DefaultPagoFormModelImpl(pago,false){
			
		};		
		final PagoConNotaForm form=new PagoConNotaForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			//System.out.println("Pago: "+);
		}
		
	}

}
