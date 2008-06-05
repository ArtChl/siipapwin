package com.luxsoft.siipap.cxc.nc;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.VerticalLayout;

import ca.odell.glazedlists.swing.EventTableModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.NotasDeCreditoDet;
import com.luxsoft.siipap.swing.form2.BasicBindingFactory;
import com.luxsoft.siipap.swing.form2.DefaultForm;
import com.luxsoft.siipap.swing.utils.ComponentUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;

/**
 * Forma para presentar una nota de credito de solo lectura 

 * @author Ruben Cancino
 *
 */
public class RONotaDeCreditoForm extends DefaultForm{
	
	private JTextField[] devoFields;
	
	public RONotaDeCreditoForm(RONotaDeCreditoFormModel model) {
		super(model, "Nota de Crédito");
	}
	
	@Override
	protected void initComponents() {
		devoFields=new JTextField[3];
		devoFields[0]=new JTextField();
		devoFields[1]=new JTextField();
		devoFields[2]=new JTextField();
		getROModel().installDevolucionData(devoFields);
	}

	@Override
	protected JComponent createFormPanel() {
		JPanel panel=new JPanel(new VerticalLayout(3));
		
		JComponent c=super.createFormPanel();
		panel.add(c);		
		panel.add(buildGridPanel());
		return panel;
	}
	
	

	/**
	 * Sobre escribimos el layot del panel maestro
	 * 
	 */
	@Override
	protected FormLayout buildLayout() {		
		final FormLayout layout=new FormLayout(
				"l:p,3dlu,f:70dlu ,3dlu " +
				"l:p,3dlu,f:70dlu, 3dlu " +
				"l:p,3dlu,f:max(p;100dlu):g"
				,"");
		return layout;
	}

	@Override
	protected void insertComponents(DefaultFormBuilder builder) {
		//((JTextField)getControl("cliente.nombre").getControl()).setColumns(40);
		builder.appendSeparator("Datos Generales");
		builder.nextLine();
		builder.append("Cliente",getControl("clave").getControl());
		builder.append("Nombre",new JTextField(getROModel().getNombre()),5);
		builder.nextLine();
		builder.append("Número",getControl("numero").getControl());
		builder.append("Fecha",getControl("fecha").getControl());
		builder.append("Id",getControl("id").getControl(),true);
		builder.append("Importe",getControl("importe").getControl(),true);
		builder.append("Impuesto",getControl("iva").getControl(),true);
		builder.append("Total",getControl("totalAsMoneda").getControl());
		builder.append("Saldo",getControl("saldoAsMoneda").getControl(),true);
		builder.append("Comentario",getControl("comentario").getControl(),9);
		builder.appendSeparator("Devolución");
		builder.append("Id:",devoFields[0]);
		builder.append("Fecha:",devoFields[1]);
		builder.append("Desc:",devoFields[2]);
		builder.appendSeparator("Partidas");
		
	}	
	
	private JComponent buildGridPanel(){
		final EventTableModel<NotasDeCreditoDet> tm=new EventTableModel<NotasDeCreditoDet>(getROModel().getPartidas(),getROModel().getTableFormat());
		final JXTable grid=ComponentUtils.getStandardTable();
		grid.setModel(tm);
		grid.setColumnControlVisible(false);
		grid.packAll();
		JScrollPane sp=new JScrollPane(grid);
		Border border=BorderFactory.createCompoundBorder(
				Borders.DIALOG_BORDER, sp.getBorder());
		sp.setBorder(border);
		sp.setPreferredSize(new Dimension(650,250));
		return sp;
	}
	
	private RONotaDeCreditoFormModel getROModel(){
		return (RONotaDeCreditoFormModel)model;
	}

	
	public static void main(String[] args) {		
		SWExtUIManager.setup();
		
		//RONotaDeCreditoFormModel model=new RONotaDeCreditoFormModel(DatosDePrueba.buscarNotasConDevoluciones());
		RONotaDeCreditoFormModel model=new RONotaDeCreditoFormModel();
		RONotaDeCreditoForm form=new RONotaDeCreditoForm(model);
		form.setReadOnly(true);
		form.setBindingFactory(new BasicBindingFactory());
		form.open();
	}

}
