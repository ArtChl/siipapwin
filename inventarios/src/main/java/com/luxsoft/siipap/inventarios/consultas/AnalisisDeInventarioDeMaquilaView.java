package com.luxsoft.siipap.inventarios.consultas;


import javax.swing.Action;

import com.luxsoft.siipap.inventarios.consultas.maq.ResumenDeSalidasDeHojas;
import com.luxsoft.siipap.inventarios.model.AnalisisDeInventarioDeMaquilaModel;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.views2.DefaultTaskView;
import com.luxsoft.siipap.swing.views2.InternalTaskTab;

/**
 * Vista de primer nivel para analizar los inventarios de maquila
 * 
 * @author Ruben Cancino
 *
 */
public class AnalisisDeInventarioDeMaquilaView extends DefaultTaskView{
	
	private AnalisisDeInventarioDeMaquilaModel model;
	private EntradasDeMaterialView entradasView;
	private InternalTaskTab entradasTab;
	private ResumenDeSalidasDeHojas salidasDeHojasView;
	private InternalTaskTab salidaDeHojasTab;
	
	
	
	protected void instalarTaskElements(){
		final Action showEntradas=new DispatchingAction(this,"mostrarEntradas");
		CommandUtils.configAction(showEntradas,"AnalisisDeInventarioDeMaquilaView.mostrarEntradas", null);
		this.consultas.add(showEntradas);
		
		final Action showSalidasDeHojas=new DispatchingAction(this,"mostrarSalidasHojeadas");
		CommandUtils.configAction(showSalidasDeHojas, "AnalisisDeInventarioDeMaquilaView.mostrarSalidasHojeadas", null);
		this.consultas.add(showSalidasDeHojas);
		
	}
	
	
	public void mostrarEntradas(){
		if(entradasView==null){
			entradasView=new EntradasDeMaterialView(getModel()){
				@Override
				public void pack() {
					super.pack();
					salidasDeHojasView.pack();
				}
			};
			entradasTab=createInternalTaskTab(entradasView);
			entradasView.load();
		}
		addTab(entradasTab);
	}
	
	public void mostrarSalidasHojeadas(){
		if(salidasDeHojasView==null){
			salidasDeHojasView=new ResumenDeSalidasDeHojas();
			salidasDeHojasView.setSalidas(model.getSalidasDeHojasSource());
			salidaDeHojasTab=createInternalTaskTab(salidasDeHojasView);			
		}
		addTab(salidaDeHojasTab);
	}
		

	/**Spring Collaborators**/

	public AnalisisDeInventarioDeMaquilaModel getModel() {
		return model;
	}
	public void setModel(AnalisisDeInventarioDeMaquilaModel model) {
		this.model = model;
	}

}
