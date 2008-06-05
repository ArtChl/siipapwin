package com.luxsoft.siipap.cxc.nc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.NumberEditorExt;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.domain.TiposDeNotas;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.cxc.model2.NotaDeCreditoFormModel;
import com.luxsoft.siipap.cxc.model2.NotaDeCreditoFormModelImp;
import com.luxsoft.siipap.cxc.swing.binding.CXCBindings;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.model.MonedasUtils;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.utils.Renderers;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;


public class NotaDeCreditoForm extends AbstractForm{
	
	private JXTable grid;
	private JCheckBox selector;	
	private EventSelectionModel<NotasDeCreditoDet> selectionModel;
	private JFormattedTextField totalCaptura;
	
	private static List<String> BONIFICACIONES=new ArrayList<String>();
	 
	static{
		BONIFICACIONES.add("C");
		BONIFICACIONES.add("F");
		BONIFICACIONES.add("L");
		BONIFICACIONES.add("Y");
	}
	
	public NotaDeCreditoForm(NotaDeCreditoFormModel model ) {
		super(model);
		
	}
	
	private void initModel(){
		getControl("iva").setEnabled(false);
		getControl("totalAsMoneda").setEnabled(false);
		getNotaModel().getModel("importe").addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getNewValue()!=null && getNotaModel().isPorDescuento()){
					final CantidadMonetaria importe=(CantidadMonetaria)evt.getNewValue();
					final CantidadMonetaria total=MonedasUtils.calcularTotal(importe);
					totalCaptura.setValue(total.amount().doubleValue());
				}
			}			
		});
		getControl("importe").setEnabled(false);
		//getControl("importe").setEnabled(!getNotaModel().isPorDescuento());
		
		totalCaptura=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
		totalCaptura.addPropertyChangeListener("value",new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				Number val=(Number)evt.getNewValue();
				if(val!=null){
					if(!getNotaModel().isPorDescuento()){
						final CantidadMonetaria tot=CantidadMonetaria.pesos(val.doubleValue());					
						model.getModel("importe").setValue(MonedasUtils.calcularImporteDelTotal(tot));
					}
					
				}
			}			
		});
		totalCaptura.setEnabled(!getNotaModel().isPorDescuento());
		
	}
	
	@Override
	protected JComponent buildFormPanel() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMasterPanel(),BorderLayout.NORTH);
		panel.add(buildDetailPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	
	
	@Override
	protected JComponent createCustomComponent(String property) {
		if(property.equals("descuento"))
			return Binder.createDescuentoBinding(model.getModel(property));
		else if(property.equals("bonificacion")){
			if(BONIFICACIONES.contains(getNotaModel().getNota().getTipo()))
				return CXCBindings.createConceptosDeBonificacionBinding(model.getModel(property));
			else
				return CXCBindings.createConceptosDeFinancierosBinding(model.getModel(property));
		}
		else
			return super.createCustomComponent(property);
	}



	private JComponent getPorDescuento(){
		if(selector==null){
			selector=new JCheckBox(" % ",true);
			selector.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					/** ANTES del 31/10/2007
					getNotaModel().setPorDescuento(selector.isSelected());
					model.getModel("importe").setValue(CantidadMonetaria.pesos(0));
					getNotaModel().actualizar();
					getControl("descuento").setEnabled(selector.isSelected());
					getControl("importe").setEnabled(!selector.isSelected());					
					**/
					
					getNotaModel().setPorDescuento(selector.isSelected());
					//model.getModel("importe").setValue(CantidadMonetaria.pesos(0));
					totalCaptura.setValue(new Double(0));					
					getNotaModel().actualizar();
					getControl("descuento").setEnabled(selector.isSelected());
					//getControl("importe").setEnabled(!selector.isSelected());
					totalCaptura.setEnabled(!selector.isSelected());
					
				}				
			});
			if(getNotaModel().getNota().getTipo().equals("V"))
				selector.setEnabled(false);
		}
		return selector;
	}
	
	private JComponent buildMasterPanel(){
		initModel();		
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,75dlu, 3dlu " +
				"l:p,2dlu,max(p;75dlu),50dlu:g "
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append("Fecha",getControl("fecha"),true);
		builder.append("Descuento",getControl("descuento"));
		builder.append("Por descuento",getPorDescuento(),true);
		builder.append("Importe",getControl("importe"));
		builder.append("Impuesto",getControl("iva"),true);
		//builder.append("Total",getControl("totalAsMoneda"),true);
		builder.append("Total",totalCaptura,true);
		if(!BONIFICACIONES.contains(getNotaModel().getNota().getTipo())){
			getControl("aplicable").setEnabled(false);
		}
			builder.append("Aplicable",getControl("aplicable"));
			builder.append("Concepto",getControl("bonificacion"),true);
		//}
		builder.append("Comentario",getControl("comentario"),6);
		ComponentUtils.decorateSpecialFocusTraversal(builder.getPanel());
		return builder.getPanel();
	}
	
	private JComponent buildDetailPanel(){
		final TableFormat<NotasDeCreditoDet> tf=getNotaModel().getNota().getTipo().equals("V")
			?CXCTableFormats.getNCTableFormatParaDF():CXCTableFormats.getNCTableFormatParaBonificacion();
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(getNotaModel().getPartidas(),tf);
		selectionModel=new EventSelectionModel<NotasDeCreditoDet>(getNotaModel().getPartidas());
		
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addDeleteAction(grid, new DispatchingAction(this,"delete"));
		grid.packAll();
		
		NumberFormat nf=NumberFormat.getInstance();
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(0);
		if(getNotaModel().getNota().getTipo().equals("V")){
			grid.getColumn(7).setCellRenderer(Renderers.getPorcentageRenderer());
			grid.getColumn(7).setCellEditor(new NumberEditorExt(nf));
		}		
		return new JScrollPane(grid);
	}
	
	protected JComponent buildHeader(){		
		return ComponentUtils.getBigHeader(getClienteHeader(),getDescripcion());		

	}
	private String getClienteHeader(){
		if(model.getValue("cliente")!=null){
			String pattern="{0} ({1})";
			return MessageFormat.format(pattern, 
					model.getValue("cliente.nombre").toString(),model.getValue("cliente.clave").toString());
		}
		return "";
	}
	private String getDescripcion(){
		String tipo=model.getValue("tipo").toString().trim();
		return TiposDeNotas.valueOf(tipo).getDesc();
	}
	
	
	public void delete(){
		if(!selectionModel.getSelected().isEmpty()){
			List<NotasDeCreditoDet> partidas=new ArrayList<NotasDeCreditoDet>();
			partidas.addAll(selectionModel.getSelected());
			for(NotasDeCreditoDet d:partidas){
				getNotaModel().getPartidas().remove(d);				
			}
		}
	}
	
	public NotaDeCreditoFormModel getNotaModel(){
		return (NotaDeCreditoFormModel)model;
	}
	
	public static void main(String[] args) {
		SWExtUIManager.setup();
		
		final NotaDeCredito nota=NotasUtils.getNotaDeCreditoBonificacionCRE(DatosDePrueba.createClienteDePrueba());
		//final NotaDeCredito nota=NotasUtils.getNotaDeCreditoDescFinancieroCRE(DatosDePrueba.createClienteDePrueba());
		final NotaDeCreditoFormModel model=new NotaDeCreditoFormModelImp(nota,DatosDePrueba.ventasDePrueba());
		final NotaDeCreditoForm form=new NotaDeCreditoForm(model);
		
		form.open();
		if(!form.hasBeenCanceled()){
			System.out.println(NotasUtils.toString(nota));
		}
	}
	
	
	
}
