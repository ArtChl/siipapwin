package com.luxsoft.siipap.cxc.swing.descuentos_old;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.domain.DescuentoPorVolumen;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.AbstractControl;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

public class DescuentoPorVolumenForm extends AbstractControl{
	
	
	private JComponent cliente;
		
	private JFormattedTextField descuento;	
	private JFormattedTextField maximo;
	private JTextField origen;
	private JComponent alta;
	private JComponent baja;
	private JCheckBox activo;
	private JComponent fechaAutorizacion;			
	private JTextField autorizo;
	private JComponent year;
	private JComponent mes;	
	private JTextArea comentario;
	
	private JLabel infoLabel;
    private JTextArea infoArea;
    private JComponent infoAreaPane;
	
	
	private PresentationModel model;
	
	
	public DescuentoPorVolumenForm(final PresentationModel model){
		this.model=model;
	}
	
	@Override
	protected JComponent buildContent() {
		initComponents();		
		decorateComponents();
		initEventHandling();
		
		FormLayout layout = new FormLayout(
                "fill:default:grow",
                "pref, 6dlu, pref, 3dlu, max(14dlu;pref)");

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();
        builder.addSeparator("Descuento",    cc.xy(1, 1));
        builder.add(buildEditorPanel(), cc.xy(1, 3));
        builder.add(buildInfoAreaPane(),  cc.xy(1, 5));
        return builder.getPanel();
	}


	protected JComponent buildEditorPanel() {
		
		FormLayout layout=new FormLayout(
				"l:max(50;p),3dlu,max(p;70dlu):g(.5),3dlu" +
				",l:max(50;p),3dlu,max(p;70dlu):g(.5)"
				,""); //NO ROWS
		DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		//builder.setDefaultDialogBorder();
		builder.append("Cliente ",cliente,5);
		builder.nextLine();		
		builder.append("Descuento",descuento);
		builder.append("Límite ",maximo);
		builder.nextLine();
		
		builder.append("Origen",origen);
		builder.append("Activo",activo);
		builder.nextLine();
		
		builder.append("Alta",alta);
		builder.append("Baja",baja);
		builder.nextLine();
		
				
		builder.append("Año",year);
		builder.append("Mes",mes);
		builder.nextLine();
		
		builder.appendSeparator("Autorización");
		builder.append("Ejecutivo",autorizo);
		builder.append("Fecha ",fechaAutorizacion);
		
		CellConstraints cc=new CellConstraints();
		builder.appendRow(builder.getLineGapSpec());
		builder.appendRow("top:30dlu");
		builder.nextLine(2);
		builder.append("Comentario");
		builder.add(new JScrollPane(comentario),cc.xyw(builder.getColumn(), builder.getRow(),5,"fill,fill"));
		//private JTextArea comentario;
		
		ValidationComponentUtils.updateComponentTreeMandatoryBackground(builder.getPanel());
		return builder.getPanel();
	}
	
	private void initComponents(){
		cliente=new JTextField();
		descuento=Binder.createDescuentoBinding(model.getComponentModel("descuento"));			
		maximo=Binder.createNumberBinding(model.getComponentModel("maximo"), 2);
		origen=BasicComponentFactory.createTextField(model.getComponentModel("origen"));	
		alta=Binder.createDateComponent(model.getComponentModel("alta"));
		baja=Binder.createDateComponent(model.getComponentModel("baja"));
		activo=BasicComponentFactory.createCheckBox(model.getComponentModel("activo"), "");	
		year=Binder.createMesBinding(model.getComponentModel("year"));	
		mes=Binder.createYearBinding(model.getComponentModel("mes"));
		autorizo=BasicComponentFactory.createTextField(model.getComponentModel("autorizo"));
		fechaAutorizacion=Binder.createDateComponent(model.getComponentModel("fechaAutorizacion"));
		comentario=BasicComponentFactory.createTextArea(model.getComponentModel("comentario"));	
		
		
	}
	
	private void decorateComponents(){
		ValidationComponentUtils.setMandatory(cliente, false);		
		ValidationComponentUtils.setInputHint(descuento, "Monto del descuento 1 (1%-100%");
		
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
		SXAbstractDialog dialog=new SXAbstractDialog("Prueba"){
			
			PresentationModel model=new PresentationModel(new DescuentoPorVolumen());

			@Override
			protected JComponent buildContent() {
				JPanel p=new JPanel(new BorderLayout());
				
				DescuentoPorVolumenForm form=new DescuentoPorVolumenForm(model);
				p.add(form.getControl(),BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
				return p;
			}

			@Override
			protected JComponent buildHeader() {
				return new HeaderPanel("Descuentos Por Volumen"," Mantenimiento y consulta a descuento por volumen ");
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
