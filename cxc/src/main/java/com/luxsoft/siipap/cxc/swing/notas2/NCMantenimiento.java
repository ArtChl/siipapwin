package com.luxsoft.siipap.cxc.swing.notas2;

import java.text.MessageFormat;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.luxsoft.siipap.cxc.CXCActions;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.NotasFactory;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.cxc.swing.notas2.NCDevolucionForm.NCDevolucionFormModel;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.Application;
import com.luxsoft.siipap.swing.actions.DispatchingAction;
import com.luxsoft.siipap.swing.form.FormDialog;
import com.luxsoft.siipap.swing.utils.CommandUtils;
import com.luxsoft.siipap.swing.utils.MessageUtils;

/**
 * 
 * @author Ruben Cancino
 *
 */
public class NCMantenimiento {
	
	
	private NotasManager manager;
	//private Action porBonificacion;
	private Action porDevolucion;
	private Action porDescuento;
	private Action notaDeCargo;
	
	public NCMantenimiento(){
		initActions();
	}
	
	private void initActions(){
		
		porDevolucion=new DispatchingAction(this,"altaDevolucion");
		CommandUtils.configAction(porDevolucion, CXCActions.CrearNotaPorDevolucionCRE.getId(), "images2/lorry_delete.png");
		porDescuento=new DispatchingAction(this,"altaDescuento");
		CommandUtils.configAction(porDescuento, CXCActions.CrearNotaPorDescuentoCRE.getId(), "images2/page_delete.png");
		notaDeCargo=new DispatchingAction(this,"altaCargo");
		CommandUtils.configAction(notaDeCargo, CXCActions.CrearCargoCRE.getId(), "images2/money_add.png");
	}
	
	public NotaDeCredito altaBonificacion(){
		NotaDeCredito n=new NotaDeCredito();
		NCBonificacionForm form=new NCBonificacionForm(n);
		form.setManager((NotasManager)ServiceLocator.getDaoContext().getBean("notasManager"));
		FormDialog dialog=new FormDialog(form);
		dialog.setTitle("Alta de N.C");
		dialog.setDescription("Nota de Crédito por Bonificación");
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			NotaDeCredito nt=(NotaDeCredito)form.getBean();
			System.out.println(NotasFactory.toString(nt));
			NotasManager manager=(NotasManager)ServiceLocator.getDaoContext().getBean("notasManager");
			manager.salvarNotaCre(nt);
			return n;
		}
		return null;
	}
	
	public void altaDevolucion(){
		NotaDeCredito nc=NotasFactory.getNotaDeCreidotDevo();
		NCDevolucionFormModel model=new NCDevolucionFormModel(nc);
		model.setManager(getManager());
		NCDevolucionForm form=new NCDevolucionForm(model);
		FormDialog dialog=new FormDialog(form);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			NotaDeCredito nt=(NotaDeCredito)form.getBean();
			//getManager().salvarNCDevoCRE(nt);
		}
		
	}
		
	public void altaDescuento(){
		
	}
	public void altaCargo(){
		
	}
	
	public void reImprimir(final NotaDeCredito nota){
		//NotasUtils.reImprimirNota(nota);
	}
	
	public Action getNotaDeCargo() {
		return notaDeCargo;
	}

	

	public Action getPorDescuento() {
		return porDescuento;
	}

	public Action getPorDevolucion() {
		return porDevolucion;
	}

	public List<NotaDeCredito> buscarNotas(final Periodo p){
		return getManager().buscarNotasCre(p);
	}
	
	private String resolveDeleteMesage(final NotaDeCredito nc){
		String msg=MessageFormat.format("Seguro que desa eliminar la nota de credito {0} por un importe de {1}", nc.getNumero(),nc.getImporte());
		return msg;
	}
	
	public boolean eliminarNota(final NotaDeCredito nc){
		int res=JOptionPane.showConfirmDialog(getFrame(), resolveDeleteMesage(nc),"Eliminación de Notas",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
		if(res!=JOptionPane.OK_OPTION) return false;
		try {
			getManager().eliminar(nc);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.showError("Error al tratar de eliminar la nota :"+nc.getId(), e);
			return false;
		}
	}

	public NotasManager getManager() {
		return manager;
	}

	public void setManager(NotasManager manager) {
		this.manager = manager;
	}
	
	public JFrame getFrame(){
		if(Application.isLoaded())
			return Application.instance().getMainFrame();
		return null;
	}

}
