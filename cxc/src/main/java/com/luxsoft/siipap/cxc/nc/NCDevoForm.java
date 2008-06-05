package com.luxsoft.siipap.cxc.nc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.util.UIFactory;
import com.luxsoft.siipap.cxc.domain.TiposDeNotas;
import com.luxsoft.siipap.cxc.model2.NotaDeDevolucionFormModel;
import com.luxsoft.siipap.cxc.model2.NotaDeDevolucionFormModelImpl;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.cxc.utils.CXCTableFormats;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.form2.AbstractForm;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.FormatUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.ventas.domain.Devolucion;
import com.luxsoft.siipap.ventas.domain.DevolucionDet;

/**
 * Forma para la generacion de notas de crédito por devolucion
 * 
 * @author Ruben Cancino
 *
 */
public class NCDevoForm extends AbstractForm{
	
	private JXTable grid;	
	private EventSelectionModel<DevolucionDet> selectionModel;
	
	

	public NCDevoForm(NotaDeDevolucionFormModel model) {
		super(model);
	}
	
	private NotaDeDevolucionFormModel getNotaModel(){
		return (NotaDeDevolucionFormModel)model;
	}

	@Override
	protected JComponent buildFormPanel() {
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMasterPanel(),BorderLayout.NORTH);
		panel.add(buildDetailPanel(),BorderLayout.CENTER);
		return panel;
	}
	
	/** Datos de la factura **/
	
	private JTextField fId;
	private JTextField fNumero;
	private JFormattedTextField fFactura;
	private JFormattedTextField fVencimiento;
	private JTextField fTotal;
	private JTextField fSaldo;
	private JTextField fDescuentos;	
	private JFormattedTextField fDescuentosP;
	private JTextField fBonificaciones;	
	private JFormattedTextField fBonificacionesP;
	private JTextField fCortes;
	private JFormattedTextField fPrecioCorte;
	private JFormattedTextField fImporteCortes;
	
	/** Datos de la devolucion **/
	
	private JTextField devoId;
	private JTextField devoNumero;
	private JTextField devoDesc;
	private JFormattedTextField devoFecha;
	private JTextField devoSucur;
	
		
	
	private void init(){
		
		fCortes=new JTextField(model.getValue("venta.cortes").toString());
		fImporteCortes=new JFormattedTextField(FormatUtils.getMoneyFormatterFactory());
		fImporteCortes.setValue(model.getValue("venta.importeCortes.amount"));
		
		fPrecioCorte=new JFormattedTextField();
		fPrecioCorte.setValue(model.getValue("venta.precioCorte"));
		
		fId=new JTextField(model.getValue("venta.id").toString());
		fNumero=new JTextField(model.getValue("venta.numero").toString());
		fFactura=new JFormattedTextField(FormatUtils.getBasicDateFormatterFactory());
		fFactura.setValue(model.getValue("venta.fecha"));			
		fVencimiento=new JFormattedTextField(FormatUtils.getBasicDateFormatterFactory());
		fVencimiento.setValue(model.getValue("venta.vencimiento"));
		fTotal=new JTextField(model.getValue("venta.total").toString());
		fSaldo=new JTextField(model.getValue("venta.saldo").toString());
		fDescuentos=new JTextField(model.getValue("venta.descuentos").toString());
		fDescuentosP=new JFormattedTextField(FormatUtils.getPorcentageFormatterFactory());
		fDescuentosP.setValue(getFacDescuentos());
		fBonificaciones=	new JTextField(model.getValue("venta.bonificaciones").toString());
		fBonificacionesP=new JFormattedTextField(FormatUtils.getPorcentageFormatterFactory());
		fBonificacionesP.setValue(getFacBonificaciones());
		
		devoId=new JTextField(model.getValue("id").toString());
		devoNumero=new JTextField(model.getValue("numero").toString());
		devoDesc=new JTextField(model.getValue("comentario").toString());
		devoFecha=new JFormattedTextField(FormatUtils.getBasicDateFormatterFactory());
		devoFecha.setValue(model.getValue("fecha"));		
		devoSucur=new JTextField(model.getValue("sucursal").toString());
		
		
		
	}
	
	private JComponent buildMasterPanel(){
		init();
		final FormLayout layout=new FormLayout(
				"p,10dlu,p"
				,"t:p,4dlu,t:p");
		final PanelBuilder builder=new PanelBuilder(layout);
		//builder.setDefaultDialogBorder();
		CellConstraints cc=new CellConstraints();
		builder.add(buildFacturaPanel(),cc.xy(1, 1));
		builder.add(buildDescPanel(),cc.xy(3, 1));
		builder.add(buildDevoPanel(),cc.xy(1, 3));
		builder.add(buildRmdPanel(),cc.xy(3, 3));
		return builder.getPanel();
	}
	
	private JComponent buildDevoPanel(){
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,f:max(p;50dlu), 2dlu " +
				"l:p,2dlu,50dlu " 
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		
		getControl("importe").setEnabled(false);
		getControl("impCortes").setEnabled(false);
		getControl("impuesto").setEnabled(false);
		getControl("total").setEnabled(false);
		
		builder.setRowGroupingEnabled(true);
		builder.appendSeparator("Devolución");		
		builder.append("Fecha",getControl("fecha"));
		builder.append("Cortes",getControl("cortes"),true);
		builder.append("Desc 1",getControl("descuento1"));
		builder.append("Desc 2",getControl("descuento2"),true);
		builder.append("Sub Total",getControl("importe"));
		builder.append("Imp (Cts)",getControl("impCortes"),true);
		builder.append("Iva",getControl("impuesto"));
		builder.append("Total",getControl("total"),true);
		JTextField comentario=BasicComponentFactory.createTextField(getNotaModel().getComentarioModel(), false);
		builder.append("Comentario",comentario,5);
		ComponentUtils.decorateSpecialFocusTraversal(builder.getPanel());
		return builder.getPanel();
	}
	
	
	
	@Override
	protected void onWindowOpened() {
		String tipo=model.getValue("venta.tipo").toString();
		if(tipo.equals("G")  ){
			fDescuentos.setText(model.getValue("venta.credito.descuento").toString());
			model.setValue("descuento1", model.getValue("venta.credito.descuento"));			
		}
		super.onWindowOpened();
	}

	private JComponent buildFacturaPanel(){
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,f:max(p;50dlu), 2dlu " +
				"l:p,2dlu,f:max(p;50dlu)" 
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		//builder.setDefaultDialogBorder();
		builder.setRowGroupingEnabled(true);
		builder.appendSeparator("Facura");		
		builder.append("Id",fId);
		builder.append("Número",fNumero,true);
		builder.append("Fecha",fFactura);
		builder.append("Vence",fVencimiento,true);
		builder.append("Total",fTotal);
		builder.append("Saldo",fSaldo,true);
		
		ComponentUtils.disableComponents(builder.getPanel());
		return builder.getPanel();
	}
	
	private JComponent buildDescPanel(){
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,f:max(p;50dlu), 2dlu " +
				"l:p,2dlu,f:max(p;50dlu), " 
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);		
		builder.appendSeparator("Descuentos y Cortes");
		builder.append("Descuentos",fDescuentos);
		builder.append("%",fDescuentosP,true);
		builder.append("Bonificaciones",fBonificaciones);
		builder.append("%",fBonificacionesP,true);
		builder.append("Cortes",fCortes);		
		builder.append("P. Corte",fPrecioCorte);
		builder.append("Imp Corte",fImporteCortes,true);
		
		builder.nextLine();		
		ComponentUtils.disableComponents(builder.getPanel());
		return builder.getPanel();
	}
	
	private JComponent buildRmdPanel(){
		final FormLayout layout=new FormLayout(
				"l:p,2dlu,f:max(p;50dlu), 2dlu " +
				"l:p,2dlu,f:max(p;50dlu), " 
				,"");
		final DefaultFormBuilder builder=new DefaultFormBuilder(layout);
		//builder.setDefaultDialogBorder();
		builder.appendSeparator("Rmd");
		builder.append("Id",devoId);
		builder.append("Rmd",devoNumero,true);		
		builder.append("Fecha",devoFecha);
		builder.append("Sucursal",devoSucur);
		builder.append("Comentario",devoDesc,5);
		builder.nextLine();
		
		builder.nextLine();		
		ComponentUtils.disableComponents(builder.getPanel());
		return builder.getPanel();
	}
	
	
	private JComponent buildDetailPanel(){
		final TableFormat<DevolucionDet> tf=CXCTableFormats.getNCTableFormatParaDevolucion();
		final EventTableModel<DevolucionDet> tm=new EventTableModel<DevolucionDet>(getNotaModel().getPartidas(),tf);
		selectionModel=new EventSelectionModel<DevolucionDet>(getNotaModel().getPartidas());
		
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setSelectionModel(selectionModel);
		ComponentUtils.decorateActions(grid);
		ComponentUtils.addDeleteAction(grid, new DispatchingAction(this,"delete"));
		grid.packAll();
		grid.setFocusable(false);
		JComponent c=UIFactory.createTablePanel(grid);
		c.setPreferredSize(new Dimension(400,250));
		c.setFocusable(false);
		return c;
	}
	
	protected JComponent buildHeader(){		
		return ComponentUtils.getBigHeader(getClienteHeader(),getDescripcion());		

	}
	private String getClienteHeader(){
		if(model.getValue("cliente")!=null){
			String pattern="{0} ({1})";
			return MessageFormat.format(pattern, 
					model.getValue("venta.nombre").toString(),model.getValue("venta.clave").toString());
		}
		return "";
	}
	private String getDescripcion(){
		String tf=model.getValue("venta.origen").toString().trim();
		if(tf.equals("CRE")){
			return TiposDeNotas.J.getDesc();
		}else if(tf.equals("CAM")){
			return TiposDeNotas.I.getDesc();
		}else
			return TiposDeNotas.H.getDesc();
	}
	
	
	
	
	protected JComponent createCustomComponent(String property) {
		if(property.startsWith("desc"))
			return Binder.createDescuentoBinding(model.getModel(property));
		else if(property.equals("comentario")){
			JTextField tf=BasicComponentFactory.createTextField(model.getModel(property), false);
			return tf;
		}else if("fecha".equals(property)){
			return Binder.createDateComponent(getNotaModel().getFechaModel());
		}else
			return super.createCustomComponent(property);
	}
	
	private double getFacDescuentos(){
		CantidadMonetaria total=(CantidadMonetaria)model.getValue("venta.total");
		double  desc=(Double)model.getValue("venta.descuentos");
		return desc/total.amount().doubleValue();
	}
	private double getFacBonificaciones(){
		CantidadMonetaria total=(CantidadMonetaria)model.getValue("venta.total");
		double  desc=(Double)model.getValue("venta.bonificaciones");
		return desc/total.amount().doubleValue();
	}
	
	public static void main(String[] args) {
		SWExtUIManager.setup();		
		Devolucion d=DatosDePrueba.buscarDevolucion(25817L);
		System.out.println("Desc: "+d.getVenta().getCredito().getDescuento());
		final NotaDeDevolucionFormModel model=new NotaDeDevolucionFormModelImpl(d);
		final NCDevoForm form=new NCDevoForm(model);
		form.open();
		if(form.hasBeenCanceled()){
			
		}
	}

}
