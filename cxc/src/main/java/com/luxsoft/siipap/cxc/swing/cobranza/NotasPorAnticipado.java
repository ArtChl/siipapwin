package com.luxsoft.siipap.cxc.swing.cobranza;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTable;
import org.springframework.util.Assert;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.Renderers;

/**
 * Previo de las notas de credito a generar/imprimir
 * Permite asignar el numero consecutivo adecuado
 *  
 * 
 * @author Ruben Cancino
 *
 */
public class NotasPorAnticipado extends SXAbstractDialog{
	
	private final EventList<NotaDeCredito> notas;
	private final EventList<NotasDeCreditoDet> partidas;
	private ValueModel consecutivoModel;
	private JFormattedTextField consecutivo;

	public NotasPorAnticipado(final List<NotaDeCredito> notas) {
		super("Notas de Descuento por anticipado");
		this.notas=GlazedLists.eventList(notas);
		partidas=new BasicEventList<NotasDeCreditoDet>();		
	}

	@Override
	protected JComponent buildContent() {
		JPanel panel=new JPanel(new BorderLayout(2,4));
		panel.add(buildConsecutivoPanel(),BorderLayout.NORTH);
		panel.add(createMainPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);		
		return panel;
	}
	
	private JComponent buildConsecutivoPanel(){
		consecutivo=BasicComponentFactory.createIntegerField(getConsecutivoModel(), 0);
		final FormLayout layout=new FormLayout("l:60dlu,3dlu,50dlu","p");
		PanelBuilder builder=new PanelBuilder(layout);
		final CellConstraints cc=new CellConstraints();
		builder.add(DefaultComponentFactory.getInstance().createTitle("Consecutivo: "),cc.xy(1,1));
		builder.add(consecutivo,cc.xy(3, 1));
		return builder.getPanel();
	}

	@Override
	protected JComponent buildHeader() {
		return new HeaderPanel("Notas por descuento"
				,"Generación e impresion de notas de credito por descuento" +
				"\npara clientes con autorización especial");
	}
	
	private JComponent createMainPanel(){
		FormLayout layout=new FormLayout("max(p;200dlu)"
				,"min(p;150dlu):g,2dlu,p,2dlu,min(p;150dlu)");
		
		PanelBuilder builder=new PanelBuilder(layout);
		CellConstraints cc=new CellConstraints();
		builder.add(createMasterPanel(),cc.xy(1, 1));
		builder.addSeparator("Partidas",cc.xy(1, 3));
		builder.add(createDetailPanel(),cc.xy(1, 5));
		
		return builder.getPanel();
	}
	
	private JComponent createMasterPanel(){		
		final String[] props={"numero","fecha","cliente.clave","cliente.nombre","totalAsMoneda"};
		final String[] labels={"Numero","Fecha","Cliente","Nombre","Total"};
		final TableFormat<NotaDeCredito> tf=GlazedLists.tableFormat(NotaDeCredito.class, props,labels);
		final JXTable grid=ComponentUtils.getStandardTable();
		final EventTableModel<NotaDeCredito> tm=new EventTableModel<NotaDeCredito>(notas,tf);
		grid.setModel(tm);
		final EventSelectionModel<NotaDeCredito> selection=new EventSelectionModel<NotaDeCredito>(notas);
		selection.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()){
					if(!selection.getSelected().isEmpty()){
						select(selection.getSelected().get(0));
					}
					
				}
			}			
		});
		grid.setSelectionModel(selection);
		grid.getColumn(0).setCellRenderer(Renderers.buildIntegerRenderer());
		grid.packAll();
		JScrollPane sp=new JScrollPane(grid);
		return sp;
	}
	
	private JXTable partidasGrid;
	
	private JComponent createDetailPanel(){		
		final String[] props={"nota.clave","factura.numero","factura.serie","factura.tipo","factura.total","factura.saldo","descuento","importe"};
		final String[] labels={"Cliente","Factura","T","S","Total","Saldo","Descuento","Importe N.C."};
		final TableFormat<NotasDeCreditoDet> tf=GlazedLists.tableFormat(NotasDeCreditoDet.class, props,labels);
		ComponentUtils.getStandardTable();
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(partidas,tf);
		
		partidasGrid=ComponentUtils.getStandardTable();
		partidasGrid.setModel(tm);
		partidasGrid.packAll();		
		partidasGrid.getColumn(6).setCellRenderer(Renderers.getPorcentageRenderer());
		ComponentUtils.decorateActions(partidasGrid);
		final JScrollPane sp=new JScrollPane(partidasGrid);
		return sp;
	}
	
	private ValueModel getConsecutivoModel(){
		if(consecutivoModel==null){
			consecutivoModel=new ValueHolder();
			consecutivoModel.addValueChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					Integer val=(Integer)evt.getNewValue();
					actualizarConsecutivo(val.longValue());
				}
				
			});
		}
		return consecutivoModel;
			
	}
	
	public void doAccept(){
		for(NotaDeCredito nota:notas){
			if(nota.getNumero()==0){				
				MessageUtils.showMessage("Existe una o mas notas de credito sin numero", "Nota de Credito");
				return;
			}
		}
		super.doAccept();
	}
	
	private void actualizarConsecutivo(final long numero){
		NotasUtils.asignarNumeroConsecutivo(notas, numero);
		for(int i=0;i<notas.size();i++){
			NotaDeCredito n=notas.get(i);
			notas.set(i, n);
		}
	}
	
	private void select(final NotaDeCredito nota){
		partidas.clear();
		partidas.addAll(nota.getPartidas());
		partidasGrid.packAll();
		
	}

}
