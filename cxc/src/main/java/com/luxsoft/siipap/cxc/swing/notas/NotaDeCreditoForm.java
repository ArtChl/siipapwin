package com.luxsoft.siipap.cxc.swing.notas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.domain.DescuentoPorCliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.binding.TiposDeFacturaBinding;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class NotaDeCreditoForm extends AbstractControl{
	
	
	private JComponent cliente;
	private JComponent tipo;
	private JComponent fecha;
	private JComponent comentario;
	
	private JLabel infoLabel;
    private JTextArea infoArea;
    private JComponent infoAreaPane;
	
	
	private PresentationModel model;
	
	
	public NotaDeCreditoForm(final PresentationModel model){
		this.model=model;
	}
	
	@Override
	protected JComponent buildContent() {
		initComponents();		
		decorateComponents();
		initEventHandling();
		
		FormLayout layout = new FormLayout(
                "fill:450dlu:grow",
                "pref, 6dlu, pref, 3dlu, min(p;200dlu),3dlu,max(14dlu;pref)");

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();
        //builder.addSeparator("Descuento",    cc.xy(1, 1));
        builder.add(buildEditorPanel(), cc.xy(1, 3));
        builder.add(buildGridPanel(), cc.xy(1, 5));
        builder.add(buildInfoAreaPane(),  cc.xy(1, 7));
        return builder.getPanel();
	}


	protected JComponent buildEditorPanel() {
		
		FormLayout layout=new FormLayout(
				" l:max(50;p),3dlu,max(p;70dlu):g(.5),3dlu" +
				",l:max(50;p),3dlu,max(p;70dlu):g(.5)"
				,""); //NO ROWS
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		builder.append("Cliente ",cliente,5);
		builder.nextLine();		
		builder.append("Tipo",tipo);
		builder.append("Fecha",fecha);
		
		
		CellConstraints cc=new CellConstraints();
		builder.appendRow(builder.getLineGapSpec());
		builder.appendRow("top:30dlu");
		builder.nextLine(2);
		builder.append("Comentario");
		builder.add(new JScrollPane(comentario),cc.xyw(builder.getColumn(), builder.getRow(),5,"fill,fill"));
		
		
		ValidationComponentUtils.updateComponentTreeMandatoryBackground(builder.getPanel());
		return builder.getPanel();
	}
	
	private JComponent buildGridPanel(){
		JPanel panel=new JPanel(new BorderLayout());
		EventList source=new BasicEventList();
		final String[] props=new String[]{"factura.sucursal","factura.tipo","factura.numero","factura.fecha","factura.total","factura.saldo","descuento","importe","saldo"};
		final String[] cols=new String[]{"Sucursal","Tipo","Número","Fecha","Importe","Saldo","% Aplic","$ Aplic","Saldo Nvo"};
		TableFormat tf=GlazedLists.tableFormat(props,cols);
		EventTableModel tm=new EventTableModel(source,tf);
		JTable grid=new JTable(tm);
		panel.add(new JScrollPane(grid),BorderLayout.CENTER);
		//Toolbar
		
		Action insert=CommandUtils.createInsertAction(this, "insert");
		Action delete=CommandUtils.createDeleteAction(this, "delete");
		Action edit=CommandUtils.createEditAction(this,"insert");
		Action show=CommandUtils.createViewAction(this,"show");
		
		ToolBarBuilder builder=new ToolBarBuilder();
		builder.add(insert);
		builder.add(delete);
		builder.add(edit);
		builder.add(show);
		panel.add(builder.getToolBar(),BorderLayout.NORTH);
		
		return panel;
	}
	
	private void initComponents(){
		cliente=new JTextField();		
		tipo=CXCBindings.createBindingDeNotasGenerales(model.getModel("tipo"));
		fecha=Binder.createDateComponent(model.getModel("fecha"));
		comentario=new JTextArea(3,10);
	}
	
	private void decorateComponents(){
		ValidationComponentUtils.setMandatory(cliente, true);
	}
	
	private void initEventHandling(){
		 KeyboardFocusManager.getCurrentKeyboardFocusManager()
         .addPropertyChangeListener(new FocusChangeHandler());
	}
	
	private JComponent buildInfoAreaPane() {
        infoLabel = new JLabel(ValidationResultViewFactory.getInfoIcon());
        infoArea = new JTextArea(1, 38);
        infoArea.setEditable(false);
        infoArea.setOpaque(false);

        FormLayout layout = new FormLayout("pref, 2dlu, default", "pref");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();
        builder.add(infoLabel, cc.xy(1, 1));
        builder.add(infoArea,  cc.xy(3, 1));

        infoAreaPane = builder.getPanel();
        infoAreaPane.setVisible(false);
        return infoAreaPane;
    }
	
	/**
     * Displays an input hint for components that get the focus permanently.
     */
    private final class FocusChangeHandler implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if (!"permanentFocusOwner".equals(propertyName))
                return;

            Component focusOwner = KeyboardFocusManager
                    .getCurrentKeyboardFocusManager().getFocusOwner();

            String focusHint = (focusOwner instanceof JComponent)
                    ? (String) ValidationComponentUtils
                            .getInputHint((JComponent) focusOwner)
                    : null;

            infoArea.setText(focusHint);
            infoAreaPane.setVisible(focusHint != null);
        }
    }
    
    public static void main(String[] args) {
    	SWExtUIManager.setup();
		show();
	}
	
	public static void show() {
		
		SXAbstractDialog dialog=new SXAbstractDialog("Prueba"){
			
			PresentationModel model=new PresentationModel(new NotaDeCredito());

			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());
				
				NotaDeCreditoForm form=new NotaDeCreditoForm(model);
				p.add(form.getControl(),BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}

			@Override
			protected JComponent buildHeader() {
				return new HeaderPanel("Nota de Credito"," Mantenimiento y consulta de Notas de Credito/Cargo ");
			}

			@Override
			public void doApply() {				
				super.doApply();
				System.out.println("Salvar los cambios a bean: "+model.getBean());
			}
			
			
			
			 
			
		};
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.open();
	}

}
