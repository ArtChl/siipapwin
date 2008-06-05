package com.luxsoft.siipap.cxc.swing.cobranza;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.GlossPainter.GlossPosition;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.Renderers;

public class CobranzaUtils {
	
	/**
	 * TODO Arreglar nuevo metodo de colocar Highliters
	 * @param grid
	 */
	public static void decorateGridForFacturasPendientes(final JXTable grid){
		
		//Color primer aviso
		Color b5=Color.YELLOW;
		Color b10=Color.ORANGE;
		Color b15=new Color(255, 20, 20);
		
		Color f1=Color.WHITE;
		grid.addHighlighter(new ColorHighlighter(b5,f1,new AtrasoPredicate(0,10)));
		grid.addHighlighter(new ColorHighlighter(b10,f1,new AtrasoPredicate(10,20)));
		grid.addHighlighter(new ColorHighlighter(b15,f1,new AtrasoPredicate(20,1000)));
	}
	
	private static class AtrasoPredicate implements HighlightPredicate{
		
		final int de;
		final int hasta;
		
		public AtrasoPredicate(final int de,final int hasta){
			this.de=de;
			this.hasta=hasta;
		}

		public boolean isHighlighted(Component arg0, ComponentAdapter adapter) {
			int row=adapter.row;
			Object val=adapter.getValueAt(row, 9);
			arg0.setForeground(Color.BLACK);			
			int atraso=(Integer)val;
			boolean res=(atraso>de && atraso<=hasta);			
			return res;
		}
		
	}
	
	public static JComponent createNiceClienteHeader(final PresentationModel model){
		
		GlossPainter glos=new GlossPainter(new Color(.1f,.1f,.10f,.1f),GlossPosition.BOTTOM);
		final PinstripePainter stripes=new PinstripePainter();
		stripes.setPaint(new Color(1.0f,1.0f,1.0f,.17f));
		stripes.setSpacing(9.0);
		
		MattePainter mp=new MattePainter(new Color(11,51,51));
		JXPanel p=new JXPanel();
		p.setBackgroundPainter(new CompoundPainter(mp,stripes,glos));
		
		JLabel clave=BasicComponentFactory.createLabel(model.getModel("clave"));
		Font f=clave.getFont().deriveFont(Font.BOLD).deriveFont(15f);
		clave.setForeground(Color.white);
		clave.setFont(f);
		
		JLabel nombre=BasicComponentFactory.createLabel(model.getModel("nombre"));
		nombre.setForeground(Color.white);
		nombre.setFont(f);
		
		FormLayout layout=new FormLayout("f:p:g","p,3dlu,p");
		p.setLayout(layout);
		CellConstraints cc=new CellConstraints();
		p.add(clave, cc.xy(1, 1));
		p.add(nombre,cc.xy(1, 3));
		p.setBorder(Borders.DIALOG_BORDER);
		return p;
	}
	
	
	public static void main(String[] args) {
		SXAbstractDialog dialog=new SXAbstractDialog("Test"){

			@Override
			protected JComponent buildContent() {
				Cliente c=new Cliente();
				c.setClave("U050008");
				c.setNombre("Union de Credito");
				PresentationModel model=new PresentationModel(c);				
				return createNiceClienteHeader(model);
			}
			
		};
		dialog.open();
	}
	
	public static class FacturasGrid extends JXTable{
		
		private TableCellRenderer moneyR=Renderers.getCantidadMonetariaTableCellRenderer();

		@Override
		protected void createDefaultRenderers() {
			// TODO Auto-generated method stub
			super.createDefaultRenderers();
		}

		@Override
		public TableCellRenderer getCellRenderer(int row, int col) {
			
			Class clazz=getModel().getColumnClass(col);
			if(clazz.equals(CantidadMonetaria.class)){
				System.out.println("Seleccionando renderer");
				return moneyR;
			}else
				return super.getCellRenderer(row, col);
		}
		
	}

}
