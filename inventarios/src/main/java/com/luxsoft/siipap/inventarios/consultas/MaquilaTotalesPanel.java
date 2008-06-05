package com.luxsoft.siipap.inventarios.consultas;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.inventarios.model.TotalizadorDeMaquila;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.AbstractControl;

/**
 * Panel para presentar los totales relacionados con maquila
 * 
 * @author Ruben Cancino
 *
 */
public class MaquilaTotalesPanel extends AbstractControl{
	
	
	
	private final PresentationModel model;
	
	
	private JFormattedTextField entradaBobina;
	private JFormattedTextField importeEntradas;
	
	private JFormattedTextField cortesBobinas;
	private JFormattedTextField importeCortes;	
	
	private JFormattedTextField cortesProceso;
	private JFormattedTextField importeProceso;	
	
	private JFormattedTextField trasladosBobinas;
	private JFormattedTextField importeTraslados;
	
	private JFormattedTextField salidasDirectas;
	private JFormattedTextField importeSalidas;
	
	private JFormattedTextField inventarioKilos;
	private JFormattedTextField importeInventarioKilos;
	
	
	
	private JFormattedTextField totalEntradaDeMillares;
	private JFormattedTextField totalSalidaDeMillares;	
	private JFormattedTextField inventarioHojas;
	
	private JFormattedTextField importeEntradaEnMillares;
	private JFormattedTextField importeSalidaEnMillares;
	private JFormattedTextField inventarioFinalEnHojas;
	
	private JFormattedTextField inventario;
	

	public MaquilaTotalesPanel(final TotalizadorDeMaquila totalizador){		
		model=new PresentationModel(totalizador);
		
	}
	
	private void initComponents(){
		entradaBobina=Binder.createGramosBinding(model.getComponentModel("entradasEnKilos"));
		decoreate(entradaBobina);
		importeEntradas=Binder.createCantidadMonetariaBinding(model.getModel("importeEntradasEnKilos"));
		decoreate(importeEntradas);
		cortesBobinas=Binder.createGramosBinding(model.getModel("salidasACorteEnKilos"));
		decoreate(cortesBobinas);
		importeCortes=Binder.createCantidadMonetariaBinding(model.getModel("importeSalidasACorteEnKilos"));
		decoreate(importeCortes);
		cortesProceso=Binder.createGramosBinding(model.getModel("procesoEnKilos"));
		decoreate(cortesProceso);
		importeProceso=Binder.createCantidadMonetariaBinding(model.getModel("importeProceso"));
		decoreate(importeProceso);
		
		trasladosBobinas=Binder.createGramosBinding(model.getModel("trasladosEnKilos"));
		decoreate(trasladosBobinas);
		importeTraslados=Binder.createCantidadMonetariaBinding(model.getModel("importeTraslados"));
		decoreate(importeTraslados);
		
		salidasDirectas=Binder.createGramosBinding(model.getModel("salidaDirectasEnKilos"));
		decoreate(salidasDirectas);
		importeSalidas=Binder.createCantidadMonetariaBinding(model.getModel("importeSalidaDirectasEnKilos"));
		decoreate(importeSalidas);
		
		inventarioKilos=Binder.createGramosBinding(model.getModel("inventarioKilos"));
		decoreate(inventarioKilos);
		importeInventarioKilos=Binder.createCantidadMonetariaBinding(model.getModel("inventarioFinalEnKilos"));
		decoreate(importeInventarioKilos);
		final Font bold=inventarioKilos.getFont().deriveFont(Font.BOLD);
		inventarioKilos.setFont(bold);
		importeInventarioKilos.setFont(bold);
		
		
		totalEntradaDeMillares=Binder.createGramosBinding(model.getModel("totalEntradaDeMillares"));
		decoreate(totalEntradaDeMillares);
		importeEntradaEnMillares=Binder.createCantidadMonetariaBinding(model.getModel("importeEntradaEnMillares"));
		decoreate(importeEntradaEnMillares);
		
		totalSalidaDeMillares=Binder.createGramosBinding(model.getModel("totalSalidaDeMillares"));
		decoreate(totalSalidaDeMillares);
		importeSalidaEnMillares=Binder.createCantidadMonetariaBinding(model.getModel("importeSalidaEnMillares"));
		decoreate(importeSalidaEnMillares);
		
		inventarioHojas=Binder.createGramosBinding(model.getModel("inventarioHojas"));
		decoreate(inventarioHojas);
		inventarioFinalEnHojas=Binder.createCantidadMonetariaBinding(model.getModel("inventarioFinalEnHojas"));
		decoreate(inventarioFinalEnHojas);
		inventarioHojas.setFont(bold);
		inventarioFinalEnHojas.setFont(bold);
		
		inventario=Binder.createCantidadMonetariaBinding(model.getModel("inventario"));
		decoreate(inventario);
		inventario.setFont(bold);
		
		
	}
	
	private void decoreate(final JTextField tf){
		tf.setFocusable(false);
		tf.setEditable(false);
		tf.setHorizontalAlignment(SwingConstants.TRAILING);		
		tf.setBorder(null);
		tf.setOpaque(false);
	}

	@Override
	protected JComponent buildContent() {
		initComponents();
		final FormLayout l=new FormLayout(
				"l:40dlu,2dlu,f:max(p;70dlu),2dlu,f:max(p;70dlu):g,10dlu"
				,
				" c:15dlu,2dlu" +
				",c:10dlu,5dlu" +
				",p,3dlu" +
				",p,3dlu" +
				",p,3dlu" +
				",p,3dlu" +
				",p,3dlu" +
				",p,1dlu" +
				",c:20dlu,3dlu"+  //Total kg
				",c:15dlu,2dlu"+   //Titulo hojas
				",p,3dlu" +
				",p,3dlu" +
				",c:20dlu,3dlu" + //Total hojas
				",c:20dlu,3dlu" 
				);
		
		final PanelBuilder builder=new PanelBuilder(l);
		final CellConstraints cc=new CellConstraints();
		final int kilosCol=3;
		final int pesosCol=5;
		/** Colocamos los titulos **/
		builder.addSeparator("Inventario Bobinas", 5);		
		builder.add(new JLabel("Unidades (Kg)"),cc.xy(kilosCol, 3,CellConstraints.CENTER,CellConstraints.CENTER));
		builder.add(new JLabel("Costo"),cc.xy(pesosCol, 3,CellConstraints.CENTER,CellConstraints.CENTER));		
		
		int row=5;
		builder.add(new JLabel("Entradas"),cc.xy(1, row));
		builder.add(entradaBobina,cc.xy(kilosCol, row));
		builder.add(importeEntradas,cc.xy(pesosCol, row));		
		row+=2;
		builder.add(new JLabel("Cortes"),cc.xy(1, row));
		builder.add(cortesBobinas,cc.xy(kilosCol, row));
		builder.add(importeCortes,cc.xy(pesosCol, row));		
		row+=2;
		builder.add(new JLabel("Proceso"),cc.xy(1, row));
		builder.add(cortesProceso,cc.xy(kilosCol, row));
		builder.add(importeProceso,cc.xy(pesosCol, row));
		row+=2;
		builder.add(new JLabel("Traslados"),cc.xy(1, row));
		builder.add(trasladosBobinas,cc.xy(kilosCol, row));
		builder.add(importeTraslados,cc.xy(pesosCol, row));
		row+=2;
		builder.add(new JLabel("Salidas"),cc.xy(1, row));
		builder.add(salidasDirectas,cc.xy(kilosCol, row));
		builder.add(importeSalidas,cc.xy(pesosCol, row));
		row+=2;
		//builder.addSeparator("",cc.xyw(3, row,3));
		row+=2;
		
		builder.add(new JLabel("Inventario"),cc.xy(1, row));
		builder.add(inventarioKilos,cc.xy(kilosCol, row));
		builder.add(importeInventarioKilos,cc.xy(pesosCol, row));
		
		row+=2;
		builder.addSeparator("Inventario Hojas",cc.xyw(1, row,5));
		row+=2;
		builder.add(new JLabel("Entradas"),cc.xy(1, row));
		builder.add(totalEntradaDeMillares,cc.xy(kilosCol, row));
		builder.add(importeEntradaEnMillares,cc.xy(pesosCol, row));
		row+=2;
		builder.add(new JLabel("Salidas"),cc.xy(1, row));
		builder.add(totalSalidaDeMillares,cc.xy(kilosCol, row));
		builder.add(importeSalidaEnMillares,cc.xy(pesosCol, row));
		row+=2;
		builder.add(new JLabel("Inventario"),cc.xy(1, row));
		builder.add(inventarioHojas,cc.xy(kilosCol, row));
		builder.add(inventarioFinalEnHojas,cc.xy(pesosCol, row));
		
		row+=2;
		builder.add(new JLabel("Total"),cc.xy(1, row));
		builder.add(inventario,cc.xy(pesosCol, row));
		
		builder.getPanel().setOpaque(false);
		return builder.getPanel();
		
	}
	
	
	
}
