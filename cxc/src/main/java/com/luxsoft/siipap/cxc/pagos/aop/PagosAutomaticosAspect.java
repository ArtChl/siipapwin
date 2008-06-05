package com.luxsoft.siipap.cxc.pagos.aop;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.aspectj.lang.ProceedingJoinPoint;
import org.jdesktop.swingx.JXHeader;

import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.managers.PagosManager;
import com.luxsoft.siipap.cxc.pagos.PagoForm;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.ventas.domain.Venta;

/**
 * Aspecto para generar el pago automatico de ventas con saldo menores
 * a 100
 * 
 * @author Ruben Cancino
 *
 */
public class PagosAutomaticosAspect {
	
	private PagosManager manager;
	
	public void procesar(final ProceedingJoinPoint pp){		
		PagoForm form=(PagoForm)pp.getTarget();		
		aplicarPagoAutomatico(form.getModel().getPagoM());		
		try {
			pp.proceed();
		} catch (Throwable e) {			
			e.printStackTrace();
		}
	}
	
	private void aplicarPagoAutomatico(final PagoM pagoM){
		final List<Venta> automaticos=getManager().buscarPosiblesPagosAutomaticos(pagoM);//PagosUtils.detectarPosiblesPagosAutomaticos(pagoM);
		if(!automaticos.isEmpty()){
			final DeMenosDialog dialog=new DeMenosDialog(automaticos);
			dialog.open();
			if(!dialog.hasBeenCanceled()){
				
			}
		}
	}
	

	public PagosManager getManager() {
		return manager;
	}

	public void setManager(PagosManager manager) {
		this.manager = manager;
	}	
	
	private class DeMenosDialog extends SXAbstractDialog{
		
		private final List<Venta> ventas;

		public DeMenosDialog(final List<Venta> ventas) {
			super("Pago automático");
			this.ventas=ventas;
			
		}

		@Override
		protected JComponent buildContent() {
			JPanel panel=new JPanel(new BorderLayout());
			JXHeader header=new JXHeader();
			header.setTitle("Ventas con saldo menor a $100.00");
			header.setDescription(getVentasMessage());
			panel.add(header,BorderLayout.CENTER);
			panel.add(buildButtonBarWithOKCancel(),BorderLayout.SOUTH);
			return panel;
		}
		
		private String getVentasMessage(){
			StringBuffer buff=new StringBuffer();
			for(Venta v:ventas){
				buff.append("\n");
				buff.append(v.toString());
			}
			return buff.toString();
		}
	}




}
