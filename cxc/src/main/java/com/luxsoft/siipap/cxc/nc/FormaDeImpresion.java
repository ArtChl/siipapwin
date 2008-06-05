package com.luxsoft.siipap.cxc.nc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.JXTable;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.ComponentUtils;

/**
 * Dialogo para mostra la lista una lista de notas de credito para imprimir
 * Permite eliminar las que no se desea imprimir
 *  
 * 
 * @author Ruben Cancino
 *
 */
public class FormaDeImpresion extends SXAbstractDialog implements ActionListener{
	
	private JXTable grid;
	private JFormattedTextField inputField;
	private final EventList<NotaDeCredito> notas;
	private SortedList<NotaDeCredito> sortedNotas;
	private Long next=null;
	
	public FormaDeImpresion(final List<NotaDeCredito> notas,final Long next){
		super("Impresión de Notas de Crédito");
		this.notas=GlazedLists.eventList(notas);
		this.next=next;
	}
	
	public FormaDeImpresion(final List<NotaDeCredito> notas){
		super("Impresión de Notas de Crédito");
		this.notas=GlazedLists.eventList(notas);
	}
	
	private void initComponents(){
		final NumberFormat nf=NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(false);
		final NumberFormatter formatter=new NumberFormatter(nf);
		formatter.setValueClass(Long.class);
		formatter.setMinimum(new Long(1));
		inputField=new JFormattedTextField(nf);
		inputField.addActionListener(this);		
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		final JPanel panel=new JPanel(new BorderLayout());
		panel.add(buildMainPanel(),BorderLayout.CENTER);
		panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
		return panel;
	}
	
	private JComponent buildMainPanel(){
		final JPanel mpanel=new JPanel(new BorderLayout());
		mpanel.add(buildInputPanel(),BorderLayout.NORTH);
		mpanel.add(buildGridPanel(),BorderLayout.CENTER);
		return mpanel;
	}
	
	private JComponent buildInputPanel(){
		return ComponentUtils.buildInputFilterPanel(inputField, "Consecutivo:");
	}
	
	private JComponent buildGridPanel(){
		
		final String[] props={"id","numero","tipo","serie","clave","fecha","total","comentario"};
		final String[] names={"Id","Número","T","S","Clave","Fecha","Total","Comentario"};
		final TableFormat<NotaDeCredito> tf=GlazedLists.tableFormat(NotaDeCredito.class, props,names);
		
		final Comparator<NotaDeCredito> c1=GlazedLists.beanPropertyComparator(NotaDeCredito.class, "clave");
		final Comparator<NotaDeCredito> c2=GlazedLists.beanPropertyComparator(NotaDeCredito.class, "fecha");
		final List<Comparator<NotaDeCredito>> comparators=new ArrayList<Comparator<NotaDeCredito>>();
		comparators.add(c1);
		comparators.add(c2);
		
		final Comparator<NotaDeCredito> c=GlazedLists.chainComparators(comparators);
		sortedNotas=new SortedList<NotaDeCredito>(notas,c);
		
		final EventTableModel<NotaDeCredito> tm=new EventTableModel<NotaDeCredito>(sortedNotas,tf);
		grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		
		final JScrollPane sp=new JScrollPane(grid);
		return sp;
	}

	public void actionPerformed(ActionEvent e) {
		long numero=(Long)inputField.getValue();
		actualizarConsecutivo(numero);
	}
	
	private void actualizarConsecutivo(final long next){
		long numero=next;		
		for(int index=0;index<sortedNotas.size();index++){
			NotaDeCredito nota=sortedNotas.get(index);
			nota.setNumero(numero++);
			for(NotasDeCreditoDet det:nota.getPartidas()){
				det.setNumero(nota.getNumero());
			}
			sortedNotas.set(index, nota);
		}
	}

	@Override
	protected void onWindowOpened() {
		if(next!=null)
			actualizarConsecutivo(next);
	}
	
	
	

}
