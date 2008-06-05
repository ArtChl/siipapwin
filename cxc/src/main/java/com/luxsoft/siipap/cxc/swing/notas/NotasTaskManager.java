package com.luxsoft.siipap.cxc.swing.notas;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.jgoodies.uifextras.panel.HeaderPanel;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;

public class NotasTaskManager {

	public static void imprimirNcPorDescuento() {
		ImpresionDeNotasPorDescuentoModel model = new ImpresionDeNotasPorDescuentoModel();
		final ImpresionDeNotasPorDescuentos form = new ImpresionDeNotasPorDescuentos(
				model);
		SXAbstractDialog dialog = new SXAbstractDialog("Prueba") {

			@Override
			protected JComponent buildContent() {
				JPanel p = new JPanel(new BorderLayout());

				
				p.add(form.getControl(), BorderLayout.CENTER);
				p.add(buildButtonBarWithOKCancel(), BorderLayout.SOUTH);
				return p;
			}

			@Override
			protected JComponent buildHeader() {
				return new HeaderPanel("Notas Por Descuento ",
						" Generación de Notas de Crédito por descuento");
			}

			@Override
			public void doApply() {
				super.doApply();
			}

		};
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.open();
	}

}
