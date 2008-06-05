package com.luxsoft.siipap.cxc.swing.notas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
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
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif.builder.ToolBarBuilder;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.TiposDeNotas;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class ImpresionDeNotasPorDescuentos extends AbstractControl{
	
	
	private JComponent cliente;
	private JComponent fecha;
	private JComponent consecutivo;
	private JComponent tipo;
	
	private JLabel infoLabel;
    private JTextArea infoArea;
    private JComponent infoAreaPane;
	
	
	private final ImpresionDeNotasPorDescuentoModel model;
	
	
	public ImpresionDeNotasPorDescuentos(final ImpresionDeNotasPorDescuentoModel model){
		this.model=model;
	}
	
	@Override
	protected JComponent buildContent() {
		initComponents();		
		decorateComponents();
		initEventHandling();
		
		FormLayout layout = new FormLayout(
                "fill:p:grow",
                "pref, 6dlu, pref, 3dlu, p,3dlu,max(14dlu;pref)");

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
				" l:max(50;p),3dlu,max(p;70dlu),3dlu" +
				",l:max(50;p),3dlu,max(p;100dlu)"
				,""); //NO ROWS
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		builder.append("Fecha Cobranza",fecha);		
		builder.append("Tipo",tipo);
		builder.nextLine();
		builder.append("Consecutivo",consecutivo);		
		builder.append("Cliente ",cliente);
		
		ValidationComponentUtils.updateComponentTreeMandatoryBackground(builder.getPanel());
		return builder.getPanel();
	}
	
	private JComponent buildGridPanel(){
		JPanel panel=new JPanel(new BorderLayout());
		
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
		
		//Grid 
		EventList source=new BasicEventList();
		final String[] props=new String[]{"numero","cliente.nombre","importe"};
		final String[] cols=new String[]{"Numero","Cliente","Importe"};
		
		final TableFormat tf=GlazedLists.tableFormat(props,cols);
		final EventTableModel tm=new EventTableModel(source,tf);
		JTable grid=new JTable(tm);		
		grid.getColumnModel().getColumn(1).setPreferredWidth(200);
		final JScrollPane sp=new JScrollPane(grid);
		
		//		Grid 1
		EventList source1=new BasicEventList();
		final String[] props1=new String[]{"factura.sucursal","factura.tipo","factura.numero","factura.fecha","factura.total","factura.saldo","descuento","importe"};
		final String[] cols1=new String[]{"Sucursal","Tipo","Número","Fecha","Importe","Saldo","% Aplic","$ Aplic"};
		
		final TableFormat tf1=GlazedLists.tableFormat(props1,cols1);
		final EventTableModel tm1=new EventTableModel(source1,tf1);
		final JTable grid1=new JTable(tm1);
		grid1.setModel(tm1);
		
		final JScrollPane sp1=new JScrollPane(grid1);
		
		FormLayout layout=new FormLayout("250dlu,3dlu,350dlu","p");
		PanelBuilder pb=new PanelBuilder(layout);		
		CellConstraints cc=new CellConstraints();
		pb.add(sp,cc.xy(1, 1));
		pb.add(sp1,cc.xy(3, 1));
		
		
		panel.add(pb.getPanel(),BorderLayout.CENTER);
		
		
		
		return panel;
	}
	
	private void initComponents(){
		cliente=new JTextField();	
		consecutivo=new JTextField();
		fecha=Binder.createDateComponent(model.getModel("fecha"));		
		tipo=CXCBindings.createBindingDeTipoDeNotasPorDescuentoParaImpresion(model.getModel("tipo"));
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

        FormLayout layout = new FormLayout("pref, 2dlu, p", "pref");
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
		NotasTaskManager.imprimirNcPorDescuento();
	}

}
