package com.luxsoft.siipap.cxc.swing.cobranza;

import org.apache.log4j.Logger;

import com.luxsoft.siipap.cxc.catalogos.ComentarioDeRevisionForm;
import com.luxsoft.siipap.ventas.domain.VentaACredito;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Soporte adicional para la funcionalidad de RevisionView
 * 
 * @author Ruben Cancino
 *
 */
public class RevisionSupport {
	
	private Logger logger=Logger.getLogger(getClass());
	
	private VentasManager manager;
	
	public RevisionSupport(){
		
	}
	
	
	
	public void actualizarComentarios(final VentaACredito v){
		final ComentarioDeRevisionForm form=new ComentarioDeRevisionForm(v);
		form.open();
		if(!form.hasBeenCanceled()){
			getManager().actualizarVenta(v.getVenta());
			getManager().refresh(v.getVenta());
		}
	}
	
	
	/**
	 * Prepara una lista de notas de credito para descuento que se requieren para facturas en especifico para las
	 * cuales se autoriza su impresion para mandar a revision
	 * 
	 * @param ventas
	 * @return
	 
	public void generarNotasPorAnticipado(final EventList<Venta> ventasTodas,final Date fecha){
		final EventList<Venta> ventas=filtrar(ventasTodas);
		final Comparator<NotasDeCreditoDet> c=GlazedLists.beanPropertyComparator(NotasDeCreditoDet.class, "clave");
		final SortedList<NotasDeCreditoDet> dets=new SortedList<NotasDeCreditoDet>(new BasicEventList<NotasDeCreditoDet>(),c);
		
		for(Venta v:ventas){
			if(v.getCliente().getCredito().isNotaAnticipada()){
				NotasDeCreditoDet det=NotasUtils.getNotaDet(v);
				dets.add(det);
			}
		}
		final GroupingList<NotasDeCreditoDet> grupos=new GroupingList<NotasDeCreditoDet>(dets,c);
		
		final List<NotaDeCredito> res=new ArrayList<NotaDeCredito>(); 
		
		for(int i=0;i<grupos.size();i++){
			final List<NotasDeCreditoDet> partidas=grupos.get(i);
			NotasUtils.ordenarPorFactura(partidas);
			final List<NotaDeCredito> notas=NotasUtils.getNotasFromDetalles(partidas);
			for(NotaDeCredito nota:notas){
				NotasUtils.configurarParaDescuento(nota);
				
			}
			res.addAll(notas);
		}
		NotasUtils.aplicarProvision(res);
		NotasUtils.actualizarFecha(res, fecha);
		if(logger.isDebugEnabled()){
			logger.debug("Notas generadas: "+res.size());
		}
		
		NotasPorAnticipado dialog=new NotasPorAnticipado(res);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			salvarNotas(res);
			MessageUtils.showMessage("Verifique que en el panel de control de windows la\n impresora seleccionada sea la correcta", "Impresión de Notas");
			imprimirNotas(res);
		}
	}
	
	
	public void salvarNotas(final List<NotaDeCredito> notas){
		ServiceLocator.getNotasManager().salvarNotasCre(notas);
		for(NotaDeCredito nota:notas){
			for(NotasDeCreditoDet det:nota.getPartidas()){
				//Actualizamos la provision de la venta
				getManager().aplicarProvision(det.getFactura());
			}
		}
	}
	
	public void imprimirNotas(final List<NotaDeCredito> notas){
		for(NotaDeCredito nota:notas){
			final Map<String, Object> params=new HashMap<String, Object>();
			params.put("NOTA_ID", nota.getId());
			params.put("IMPORTE_LETRA", ImporteALetra.aLetra(nota.getTotalAsMoneda()));
			ReportUtils.printReport("reportes/"+CXCReportes.NotaDeCredito.name()+".jasper"
					, params, false);
		}
	}
	
	
	private EventList<Venta> filtrar(final EventList<Venta> ventas){
		final Matcher<Venta> matcher=new Matcher<Venta>(){
			public boolean matches(Venta item) {
				return ((item.getSaldo().doubleValue()>0)  && (item.getProvision()!=null) && (!item.getProvision().isAplicado())
						);
			}
			
		};
		final FilterList<Venta> filtro=new FilterList<Venta>(ventas,matcher);
		return filtro;
	}

*/	

	public VentasManager getManager() {
		return manager;
	}
	public void setManager(VentasManager manager) {
		this.manager = manager;
	}
	
	

}
