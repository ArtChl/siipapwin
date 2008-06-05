package com.luxsoft.siipap.cxc.catalogos;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.uifextras.util.UIFactory;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.form2.BasicBindingFactory;
import com.luxsoft.siipap.swing.form2.BindingFactory;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;
import com.luxsoft.siipap.swing.form2.FormControl;
import com.luxsoft.siipap.swing.form2.IFormModel;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

/**
 * Mantenimiento al catalogo de clientes
 * 
 * @author Ruben Cancino
 *
 */
public class ClienteForm extends SXAbstractDialog{
	
	
	
	private Map<String, JComponent> components=new HashMap<String, JComponent>();
	private final IFormModel model;
	private final IFormModel cmodel;
	private BindingFactory factory;
	private JButton btnRevisiones;
	private JButton btnPagos;
 
	public ClienteForm(final IFormModel model) {
		super("Catalogo de crédito");
		this.model=model;
		model.getValidationModel().addPropertyChangeListener(ValidationResultModel.PROPERTYNAME_RESULT,new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {				
				updateComponentTreeMandatoryAndSeverity();
			}			
		});
		cmodel=new DefaultFormModel(model.getValue("cliente"));
		model.getModel("calendario").addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				updateCalendarioControls();
			}			
		});
	}

	@Override
	protected JComponent buildContent() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		if(model.isReadOnly()){
			panel.add(buildButtonBarWithClose(),BorderLayout.SOUTH);			
		}else
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	private JComponent buildMainPanel(){
		final FormLayout layout=new FormLayout("p","p,2dlu,40dlu");
		final PanelBuilder builder=new PanelBuilder(layout);
		final CellConstraints cc=new CellConstraints();
		builder.add(buildFormPanel(),cc.xy(1, 1));
		if(!model.isReadOnly())
			builder.add(buildValidationPanel(),cc.xy(1,3));
		return builder.getPanel();
	}
	
	private JComponent getControl(final String property){
		JComponent c=components.get(property);
		if(c==null){
			c=createNewComponent(property);			
			components.put(property, c);
		}
		return c;
	}
	
	protected JComponent createNewComponent(final String property){
		if(property.equals("comentarioCxc")){ 
			JComponent c= BasicComponentFactory.createTextArea(model.getModel(property),false);
			ValidationComponentUtils.setMessageKey(c, "ClienteCredito.comentarioCxc");
			return c;
		}
		else if(property.equals("cobrador"))
			return CXCBindings.createCobradorBinding(model.getModel(property));
		else if(property.equals("operador"))
			return CXCBindings.createOperadorBinding(model.getModel(property));
		else if(property.equals("dia_pago") || property.equals("dia_revision"))
			return Binder.createDiaDeLaSemanaBinding(model.getModel(property));
		else if("comisionCobrador".equals(property))
			return Binder.createDescuentoBinding(model.getComponentModel(property));
		else if("comisionVendedor".equals(property))
			return Binder.createDescuentoBinding(model.getComponentModel(property));
		
		FormControl fc=getFactory().getFormControl(property, model);		
		ValidationComponentUtils.setMessageKey(fc.getControl(), fc.getLabelKey());
		return fc.getControl();
	}
	
	
	 protected void updateComponentTreeMandatoryAndSeverity() {
		 ValidationComponentUtils.updateComponentTreeMandatoryAndBlankBackground(getContentPane());
	     ValidationComponentUtils.updateComponentTreeSeverityBackground(getContentPane(),model.getValidationModel().getResult());
	     getOKAction().setEnabled(!model.getValidationModel().hasErrors());
	 }
	 
	 private BindingFactory getFactory(){
			if(factory==null)
				factory=new BasicBindingFactory();
			return factory;
	}
	 
	protected JComponent buildValidationPanel(){
		JComponent c=ValidationResultViewFactory.createReportList(model.getValidationModel());		
		return c;
	}
	
	/**
	 * 
	 * @return
	 */
	private JComponent buildFormPanel(){		
		
		final FormLayout layout=new FormLayout(
				"l:p,3dlu,60dlu,3dlu " +
				"l:p,3dlu,60dlu" +
				"","");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		builder.append("Cobrador",getControl("cobrador"));		
		builder.append("Operador",getControl("operador"),true);
		
		builder.append("Dia pago",getControl("dia_pago"));
		builder.append("Dia revisión",getControl("dia_revision"),true);		
		
		builder.append("Plazo",getControl("plazo"));
		builder.append("Nota anticipada",getControl("notaAnticipada"),true);
		
		
		builder.append("Cheque Postfechado", getControl("chequep"));	
		builder.append("Orden de Compra", getControl("ordenDeCompra"),true);
		
		builder.append("Vencimiento Fecha Fac.",getControl("tipo_vencimiento"));
		builder.append("Pagaré",getControl("pagare"),true);
		builder.append("Suspender Descuento",getControl("suspenderDescuento"),true);
		builder.append("Email",BasicComponentFactory.createTextField(cmodel.getModel("correoelectronico1")),5);
		
		builder.nextLine();
		builder.append("Comision Venta",getControl("comisionVendedor"),true);
		//builder.nextLine();
		builder.append("Comision Cobrador",getControl("comisionCobrador"),true);
		/*
		
		
		*/
		btnPagos=new JButton("Pagos");
		btnPagos.addActionListener(EventHandler.create(ActionListener.class, this, "mostrarPagos"));
		
		btnRevisiones=new JButton("Revisiones");
		btnRevisiones.addActionListener(EventHandler.create(ActionListener.class, this, "mostrarRevisiones"));
		
		updateCalendarioControls();
		
		builder.append("Calendario",getControl("calendario"),true);
		builder.append("Revisiones",btnRevisiones);
		builder.append("Pagos",btnPagos);
		builder.nextLine();
		
		builder.append("Corporativo",getControl("corporativo"),5);
		
		final CellConstraints cc=new CellConstraints();		
		builder.append("Comentario");
		builder.appendRow(new RowSpec("30dlu"));
		builder.add(new JScrollPane(getControl("comentarioCxc")),
				cc.xywh(builder.getColumn(), builder.getRow(),5,2));
		builder.nextLine(4);
		//builder.append("Comentario",getControl("comentarioCxc"),5);
		ComponentUtils.decorateSpecialFocusTraversal(builder.getPanel());
		return builder.getPanel();
	}

	@Override
	protected JComponent buildHeader() {
		final String header=MessageFormat.format("{0} ({1})"
				,model.getValue("nombre"),model.getValue("clave"));		
		return new HeaderPanel(header,"Mantenimiento al catálogo");
	}	
	
	private void updateCalendarioControls(){
		btnPagos.setEnabled((Boolean)model.getValue("calendario"));
		btnRevisiones.setEnabled((Boolean)model.getValue("calendario"));
	}
	
	public void mostrarRevisiones(){
		FechaDialog dialog=new FechaDialog("Revisiones"){			
			protected boolean insertIntoModel(Date fecha) {
				ClienteCredito c=(ClienteCredito)model.getBaseBean();
				return c.agregarFechaRevision(fecha);
			}
			protected boolean removeFromModel(Date fecha) {
				ClienteCredito c=(ClienteCredito)model.getBaseBean();
				return c.removerFechaRevision(fecha);
			}
			@Override
			protected void agregarExistenties(final EventList<Date> list) {
				ClienteCredito c=(ClienteCredito)model.getBaseBean();
				list.addAll(c.getFechasRevision());
				
			}			
		};
		dialog.open();
	}
	
	public void mostrarPagos(){
		FechaDialog dialog=new FechaDialog("Pagos"){			
			protected boolean insertIntoModel(Date fecha) {
				ClienteCredito c=(ClienteCredito)model.getBaseBean();
				return c.agregarFechaPago(fecha);
			}
			protected boolean removeFromModel(Date fecha) {
				ClienteCredito c=(ClienteCredito)model.getBaseBean();
				return c.removerFechaPago(fecha);
			}
			@Override
			protected void agregarExistenties(final EventList<Date> list) {
				ClienteCredito c=(ClienteCredito)model.getBaseBean();
				list.addAll(c.getFechasPago());
				
			}			
		};
		dialog.open();
	}
	
	public abstract class FechaDialog extends SXAbstractDialog{
		
		JXTable table;
		EventList<Date> source;
		EventSelectionModel<Date> selectionModel;
		Action insertAction;
		Action deleteAction;

		public FechaDialog(String title) {
			super(title);
			insertAction=CommandUtils.createInsertAction(this, "insert");
			deleteAction=CommandUtils.createDeleteAction(this, "delete");
		}

		@Override
		protected JComponent buildContent() {
			table=ComponentUtils.getStandardTable();			
			final TableFormat<Date> tf=new TableFormat<Date>(){
				final DateFormat df=new SimpleDateFormat("dd/MM/yyyy");
				public int getColumnCount() {					
					return 1;
				}
				public String getColumnName(int column) {					
					return "Fecha";
				}
				public Object getColumnValue(Date baseObject, int column) {
					return df.format(baseObject);
				}				
			};
			source=new BasicEventList<Date>();
			selectionModel=new EventSelectionModel<Date>(source);
			final EventTableModel<Date> model=new EventTableModel<Date>(source,tf);
			table.setModel(model);
			table.setSelectionModel(selectionModel);
			ComponentUtils.decorateActions(table);
			ComponentUtils.addDeleteAction(table, deleteAction);
			ComponentUtils.addInsertAction(table, insertAction);
			final JPanel panel=new JPanel(new BorderLayout());
			panel.add(buildToolbar(),BorderLayout.NORTH);
			panel.add(UIFactory.createTablePanel(table),BorderLayout.CENTER);
			return panel;
		}
		
		private JToolBar buildToolbar(){
			final ToolBarBuilder builder=new ToolBarBuilder();
			builder.add(insertAction);
			builder.add(deleteAction);
			return builder.getToolBar();
		}
		
		public void insert(){
			final ValueHolder holder=new ValueHolder(new Date());
			final SXAbstractDialog dialog=Binder.createDateSelector(holder);
			dialog.open();
			if(!dialog.hasBeenCanceled()){
				final Date date=(Date)holder.getValue();
				if(insertIntoModel(date));
					source.add(date);
			}			
		}
		
		@Override
		protected void onWindowOpened() {
			agregarExistenties(source);
		}

		protected abstract boolean insertIntoModel(final Date fecha);
		
		protected abstract boolean removeFromModel(final Date fecha);
		
		protected abstract void agregarExistenties(final EventList<Date> list);
		
		public void delete(){
			if(!selectionModel.getSelected().isEmpty()){
				for(Date fecha:selectionModel.getSelected()){
					if(removeFromModel(fecha))
						source.remove(fecha);
				}
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		final ClienteCredito cred=new ClienteCredito();		
		cred.setClave("U050008");
		cred.setNombre("UNION DE CREDITO");
		final Cliente c=new Cliente(cred.getClave(),cred.getNombre());
		cred.setCliente(c);
		cred.setDia_pago(1);
		cred.setDia_revision(4);
		cred.setOperador(2);
		cred.setPlazo(25);
		cred.setNotaAnticipada(true);
		cred.setOrdenDeCompra(true);
		DefaultFormModel model=new DefaultFormModel(cred);
		model.setReadOnly(false);
		ClienteForm form=new ClienteForm(model);
		form.setDefaultButton(null);
		form.open();
		System.out.println(cred);
	}
	

}
