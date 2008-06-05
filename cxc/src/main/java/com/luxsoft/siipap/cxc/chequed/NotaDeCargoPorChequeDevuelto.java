package com.luxsoft.siipap.cxc.chequed;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.MessageFormat;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.FormatUtils;

/**
 * Forma para la generacion de Nota de Cargo
 * 
 * @author Ruben Cancino
 *
 */
public class NotaDeCargoPorChequeDevuelto extends SXAbstractDialog{
	
	private final SolicitudDeCargoPorChequeDevuelto model;
	
	private JComponent fecha;
	private JCheckBox tipo;
	private JFormattedTextField importe;
	private JFormattedTextField porcentaje;
	private JFormattedTextField cheque;
	
	private JTextField comentario;
	//private JXTable grid;
	
	public NotaDeCargoPorChequeDevuelto(final SolicitudDeCargoPorChequeDevuelto model){
		super("Nota de Cargo");
		this.model=model;
		model.getValidationModel().addPropertyChangeListener(new ValidationHandler());
		
	}

	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	private JComponent buildMainPanel(){
		final FormLayout layout=new FormLayout(
				"p","p,5dlu,p,2dlu,40dlu");
		final PanelBuilder builder=new PanelBuilder(layout);
		final CellConstraints cc=new CellConstraints();
		builder.add(buildFormPanel(),cc.xy(1,1));		
		builder.add(ValidationResultViewFactory.createReportList(model.getValidationModel()),cc.xy(1,5));
		return builder.getPanel();
	}
	
	
	private void initComponents(){		
		
		fecha=Binder.createDateComponent(model.getModel("fecha"));
		importe=Binder.createCantidadMonetariaBinding(model.getModel("importe"));
		
		
		tipo=BasicComponentFactory.createCheckBox(model.getComponentModel("porPorcentaje"), "Por Porcentaje?");
		
		tipo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {				
				porcentaje.setEnabled(tipo.isSelected());
			}			
		});
		porcentaje=Binder.createDescuentoBinding(model.getModel("porcentaje"));
		comentario=BasicComponentFactory.createTextField(model.getModel("comentario"), false);
		importe.setEnabled(!model.isPorPorcentaje());
		model.getComponentModel("porPorcentaje").addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {	
				System.out.println("% "+model.getPorcentaje());
				importe.setEnabled(!model.isPorPorcentaje());
				
			}			
		});
		cheque=new JFormattedTextField(FormatUtils.getBigDecimalMoneyFormatterFactory());
		cheque.setValue( model.getImporteDelCheque());
		
	}
	
	private JComponent buildFormPanel(){
		initComponents();
		
		final FormLayout layout=new FormLayout(
				"l:50dlu,2dlu,f:max(p;60dlu) ,2dlu " +
				"l:50dlu,2dlu,f:max(p;60dlu) ,2dlu "+
				"l:50dlu,2dlu,f:max(p;60dlu)"
				,""
				);
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);		
		builder.append("Fecha",fecha);
		builder.append("Tipo",tipo,true);
		builder.append("Cheque ($)",cheque);
		builder.append("%",porcentaje,true);
		
		builder.append("Importe",importe,true);
		
		
		builder.append("Comentario",comentario,7);		
		
		return builder.getPanel();
	}
	
	protected JComponent buildHeader(){		
		HeaderPanel header=new HeaderPanel(getClienteHeader(),"Nota de cargo por cheque devuelto");		
		return header;
	}
	
	private String getClienteHeader(){
		if(model.getCliente()!=null){
			String pattern="{0} ({1})";
			return MessageFormat.format(pattern, 
					model.getCliente().getNombre(),model.getCliente().getClave());
		}
		return "";
	}
	
	
	
	@Override
	protected void onWindowOpened() {
		model.validate();
		System.out.println("Has Errors: "+model.getValidationModel().hasErrors());
	}



	private class ValidationHandler implements PropertyChangeListener{
		public void propertyChange(PropertyChangeEvent evt) {
			getOKAction().setEnabled(!model.getValidationModel().hasErrors());			
		}		
	}
	
	public NotaDeCredito getCargo(){
		final NotaDeCredito nota=NotasUtils.generarNotaDeCargoPorChequeDevuelto(model);
		return nota;
	}
	
	public static void main(String[] args) {
		final Cliente c=new Cliente("U050008","Union de credito");
		final ChequeDevuelto cheque=new ChequeDevuelto();
		cheque.setCliente(c);
		cheque.setBanco("BANAMEX");
		cheque.setImporte(BigDecimal.valueOf(50000));
		SolicitudDeCargoPorChequeDevuelto model=new SolicitudDeCargoPorChequeDevuelto(cheque);
		
		NotaDeCargoPorChequeDevuelto form=new NotaDeCargoPorChequeDevuelto(model);
		form.open();
		System.out.println(form.getCargo());
		
	
	}

}
