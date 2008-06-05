package com.luxsoft.siipap.cxc.nc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.luxsoft.siipap.cxc.model2.SolicitudDeNotaDeCargo;
import com.luxsoft.siipap.cxc.model2.SolicitudDeNotaDeCargo2;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Forma para la generacion de Nota de Cargo
 * 
 * @author Ruben Cancino
 *
 */
public class NotaDeCargoForm extends SXAbstractDialog{
	
	private final SolicitudDeNotaDeCargo2 model;
	
	private JComponent fecha;
	private JCheckBox tipo;
	private JFormattedTextField importe;
	private JFormattedTextField iva;
	private JFormattedTextField total;
	private JFormattedTextField porcentaje;
	private JFormattedTextField totalFacturas;
	private JTextField comentario;
	private JXTable grid;
	
	public NotaDeCargoForm(final SolicitudDeNotaDeCargo2 model){
		super("Nota de Cargo");
		this.model=model;
		model.getValidationModel().addPropertyChangeListener(new ValidationHandler());
		model.getModel("importe").addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {				
				grid.packAll();
			}			
		});
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
		builder.add(buildGridPanel(),cc.xy(1,3));
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
				total.setEnabled(!tipo.isSelected());				
				model.aplicarCargo(0);
			}			
		});
		porcentaje=Binder.createDescuentoBinding(model.getModel("porcentaje"));
		comentario=BasicComponentFactory.createTextField(model.getModel("comentario"), false);
		importe.setEnabled(false);
		model.getComponentModel("importe").addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if(model.isPorPorcentaje()){					
					final CantidadMonetaria imp=(CantidadMonetaria)evt.getNewValue();
					iva.setValue(MonedasUtils.calcularImpuesto(imp).amount().doubleValue());
					total.setValue(MonedasUtils.calcularTotal(imp).amount().doubleValue());
				}
			}			
		});
		
		total=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
		total.addPropertyChangeListener("value", new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if(!model.isPorPorcentaje()){
					Number val=(Number)evt.getNewValue();
					if(val!=null){						
						final CantidadMonetaria tot=CantidadMonetaria.pesos(val.doubleValue());
						System.out.println("Total: "+tot);
						final CantidadMonetaria imp=MonedasUtils.calcularImporteDelTotal(tot);
						model.setValue("importe", imp);
						final CantidadMonetaria imps=MonedasUtils.calcularImpuesto(imp);
						iva.setValue(imps.amount().doubleValue());
					}
				}
				
			}			
		});
		total.setEnabled(false);
		iva=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
		iva.setEnabled(false);
		
		totalFacturas=Binder.createCantidadMonetariaBinding(model.getModel("totalFacturas"));
		totalFacturas.setEnabled(false);
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
		builder.append("%",porcentaje,true);
		builder.append("Importe",importe);		
		builder.append("Impuesto",iva,true);
		builder.append("Total (Cargo)",total);		
		builder.append("Total (Fac)",totalFacturas,true);
		
		builder.append("Comentario",comentario,7);		
		
		return builder.getPanel();
	}
	
	private JComponent buildGridPanel(){
		
		final EventList<Venta> source=model.getVentas();
		
		final String[] props={"id","sucursal","numero","fecha","vencimiento","total","saldo","descuentoTemporal","pago"};
		final String[] names={"id","Suc","Numero","Fecha","Vence","Total","Saldo","Cargo","Pago"};
		final boolean[] edits={false,false,false,false,false,false,false,true,false};
		final TableFormat<Venta> tf=GlazedLists.tableFormat(Venta.class, props,names,edits);
		
		final EventTableModel<Venta> tm=new EventTableModel<Venta>(source,tf);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSortable(true);
		grid.setColumnControlVisible(false);
		
		grid.packAll();
		grid.getColumn(6).setPreferredWidth(100);
		grid.getColumn(7).setPreferredWidth(100);
		grid.getColumn(8).setPreferredWidth(100);
		
		ComponentUtils.decorateActions(grid);
		final JScrollPane sp=new JScrollPane(grid);		
		return sp;
	}
	
	protected JComponent buildHeader(){		
		HeaderPanel header=new HeaderPanel(getClienteHeader(),"Alta de nota de cargo");		
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
	/*
	public static void main(String[] args) {
		Cliente c=new Cliente("U050008","Union de Credito");
		SolicitudDeNotaDeCargo model=new SolicitudDeNotaDeCargo(c,DatosDePrueba.ventasDePrueba());
		NotaDeCargoForm form=new NotaDeCargoForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			List<NotaDeCredito> notas=NotasUtils.generarNotasDeCargo(model);
			FormaDeImpresion f2=new FormaDeImpresion(GlazedLists.eventList(notas));
			f2.open();
		}
	
	}*/

}
