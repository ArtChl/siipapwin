package com.luxsoft.siipap.cxc.pagos;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.jgoodies.binding.value.ValueHolder;
import com.luxsoft.siipap.cxc.catalogos.ComentarioDeCreditoForm;
import com.luxsoft.siipap.cxc.consultas.ConsultaUtils;
import com.luxsoft.siipap.cxc.domain.Anticipo;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.cxc.domain.Provision;
import com.luxsoft.siipap.cxc.managers.NotasManager;
import com.luxsoft.siipap.cxc.managers.PagosManager;
import com.luxsoft.siipap.cxc.model.CXCFiltros;
import com.luxsoft.siipap.cxc.model.NotasUtils;
import com.luxsoft.siipap.cxc.model.PagosFactory;
import com.luxsoft.siipap.cxc.model2.DefaultPagoFormModelImpl;
import com.luxsoft.siipap.cxc.model2.PagoConOtrosModelImpl;
import com.luxsoft.siipap.cxc.model2.PagoFormModel;
import com.luxsoft.siipap.cxc.nc.NCDescuentoForm;
import com.luxsoft.siipap.cxc.nc.NCDescuentoFormModel;
import com.luxsoft.siipap.cxc.selectores.Selectores;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.cxc.task.ActualizarNumeroFiscal;
import com.luxsoft.siipap.services.ServiceLocator;
import com.luxsoft.siipap.swing.binding.Binder;
import com.luxsoft.siipap.swing.controls.SXAbstractDialog;
import com.luxsoft.siipap.swing.utils.MessageUtils;
import com.luxsoft.siipap.swing.utils.SWExtUIManager;
import com.luxsoft.siipap.swing.utils.TaskUtils;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaACredito;
import com.luxsoft.siipap.ventas.managers.VentasManager;

/**
 * Facade para manejo de pagos
 * 
 * @author Ruben Cancino
 *
 */
public class ControladorDePagosImpl implements IControladorDePagos {
	
	
	private VentasManager ventasManager;
	private PagosManager pagosManager;
	private NotasManager notasManager;
	private PagosFactory pagosFactory;
	
	private Logger logger=Logger.getLogger(getClass());
	
	
	public PagoM registrarPagoCreditoProvisionables(final Cliente c,final List<Venta> ventas){
		if(ventas==null || ventas.isEmpty())
			return null;
		CXCFiltros.filtrarVentasConSaldo(ventas);
		if(ventas.isEmpty()){
			MessageUtils.showMessage("Todas las ventas seleccionadas estan pagadas en su totalidad", "Pagos");
			return null;
		}
		final String tipo=ventas.get(0).getTipo();
		CXCFiltros.filtrarVentasParaUnTipo(ventas, tipo);
		final PagoForm form=getForm(c, tipo);
		form.getModel().generarPagos(ventas);
		form.open();
		if(!form.hasBeenCanceled()){
			final PagoM pago=form.getModel().getPagoM();
			getPagosManager().salvarGrupoDePagos(pago);			
			return form.getModel().getPagoM();
		}
		return null;
		
	}
	
	private PagoForm getForm(final Cliente c, String tipo ){
		final PagosModel model=new PagosModelImpl(c,tipo);
		final PagoForm form=new PagoForm(model);	
		return form;
	}
	
	/**
	 * Proporciona un UI para registrar pagos con forma de pago S Otros productos
	 * 
	 * @param c
	 * @param ventas
	 * @return
	 */
	public void registrarPagoConOtros(final Cliente c,final List<Venta> ventas){
		// Buscar PagoM disponibles
		SwingWorker<List<PagoM>, String> worker=new SwingWorker<List<PagoM>, String>(){
			@Override
			protected List<PagoM> doInBackground() throws Exception {				
				return getPagosManager().buscarSaldosAFavor(c);
			}
			@Override
			protected void done() {
				try {
					List<PagoM> ps=get();
					if(!ps.isEmpty()){
						procedePagoConOtros(c, ps,ventas);
					}else
						MessageUtils.showMessage("El cliente no tiene saldos a favor disponibles", "Pagos");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		TaskUtils.executeSwingWorker(worker);
		
	}
	
	/**
	 * Procede con el proceso de pago con otros una vez que se localizaron los pagos con disponible
	 *  
	 * @param c
	 * @param pagos
	 * @param ventas
	 */
	private void procedePagoConOtros(final Cliente c,final List<PagoM> pagos,final List<Venta> ventas){	
		CXCFiltros.filtrarVentasConSaldo(ventas);
		final PagoM origen=Selectores.seleccionarPagoM(c, pagos);
		if(origen!=null){
			
			final PagoConOtros pago=getPagosFactory().crearPago(origen, ventas);
			if(pago!=null){
				pago.setOrigen(origen);
				pago.setCliente(origen.getCliente());
				final PagoConOtrosModelImpl model=new PagoConOtrosModelImpl(true,pago);
				final PagoConOtrosForm form=new PagoConOtrosForm(model);
				form.open();
				if(!form.hasBeenCanceled()){
					getPagosManager().salvarGrupoDePagos(pago);
					final List<Venta> ventasResultantes=PagosUtils.extraerVentas(pago);
					for(Venta vv:ventasResultantes){
						getVentasManager().refresh(vv);
					}
					completar(pago);
				}
			}else{
				MessageUtils.showMessage("La selección de ventas no califica para el pago", "Pagos");
			}
		}
		
		
	}
	
	/**
	 * Genera un pago con {@link NotaDeCredito} a un grupo de ventas 
	 * 
	 * @param c
	 * @param ventas
	 */
	public List<Venta> registrarPagoConNota(final Cliente c,final List<Venta> ventas){
		
		//Filtramos las ventas con saldo
		CXCFiltros.filtrarVentasConSaldo(ventas);
		final List<Venta> afectadas=new ArrayList<Venta>();
		
		if(ventas.isEmpty()){
			MessageUtils.showMessage("El grupo de ventas selccionadas no tiene saldo", "Pago con nota");
			return ventas;
		}
		
		//Obtener una lista de las posibles notas de credito para el pago para que el usuario seleccione una
		final EventList<NotaDeCredito> notas=GlazedLists.eventList(getNotasManager().buscarNotasDeCreditoDisponibles(c));
		
		if(notas.isEmpty()){
			MessageUtils.showMessage(MessageFormat.format("El cliente {0} ({1})\n No tiene notas disponibles para usar como forma de pago"
					, c.getNombre(),c.getClave()), "Notas disponibles");
			return afectadas;
		}		
		final NotaDeCredito origen=Selectores.seleccionarNotaDeCredito(c, notas);
		//Procedemos con el pago
		if(origen!=null){
			final PagoConNota pago=getPagosFactory().crearPagoConNota(origen, ventas);
			final PagoFormModel model=new DefaultPagoFormModelImpl(pago);
			final PagoConNotaForm form=new PagoConNotaForm(model);
			form.open();
			if(!form.hasBeenCanceled()){
				try {
					getPagosManager().salvarGrupoDePagos(pago);					
					final List<Venta> ventasResultantes=PagosUtils.extraerVentas(pago);
					for(Venta vv:ventasResultantes){
						getVentasManager().refresh(vv);
					}
					afectadas.addAll(ventasResultantes);
					completar(pago);
				} catch (Exception e) {
					MessageUtils.showError("Error al salvar pago", e);
				}
			}
		}
		return afectadas;
	}
	
	/**
	 * Completa el proceso de pago genrando descuentos y realizando pagos automaticos 
	 * cuando estos apliquen
	 * 
	 * @param ventas
	 */
	public List<Venta> completar(final PagoM pago){
		final List<Venta> ventasResultantes=PagosUtils.extraerVentas(pago);
		if(!ventasResultantes.isEmpty() && procedeDescuento(pago)){
			List<NotaDeCredito> descuentos=aplicarNotaDeDescuento(pago.getCliente(), ventasResultantes, pago.getFecha());
			/**
			if(!descuentos.isEmpty()){
				List<NotaDeCredito> resNotas=Browsers.seleccionarNotasParaSuImpresion(descuentos);
				for(NotaDeCredito nota:resNotas){
					getNotasManager().imprimirNotaDeDescuento(nota);
				}
			}
			**/
		}if(!ventasResultantes.isEmpty() && (!pago.getTipoDeDocumento().equals("X")) ){
			registrarPagoAutomatico(ventasResultantes);
		}
		return ventasResultantes;
	}

	
	/**
	 * Controla el proceso de aplicacion de pagos automaticos sobre un grupo de facturas
	 * 
	 * 
	 * @param ventas
	 */
	public void registrarPagoAutomatico(final List<Venta> ventas){
		CXCFiltros.filtrarVentasConSaldo(ventas);
		CXCFiltros.filtrarParaPagoAutomatico(ventas);
		if(!ventas.isEmpty() ){			
			final String tipo=ventas.get(0).getTipo();
			CXCFiltros.filtrarVentasParaUnTipo(ventas, tipo);
			final PagosAutomaticosForm form=new PagosAutomaticosForm(ventas);
			form.open();
			if(!form.hasBeenCanceled()){
				final PagoM pago=getPagosFactory().crearPagoAutomatico(form.getVentas());
				getPagosManager().salvarGrupoDePagos(pago);
			}
			form.dispose();
		}
		return;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.pagos.IControladorDePagos#diferenciaCambiaria(java.util.List)
	 */
	public void diferenciaCambiaria(final List<Venta> ventas){
		CXCFiltros.filtrarVentasConSaldo(ventas);
		CXCFiltros.filtrarParaDiferenciaCambiara(ventas);
		if(!ventas.isEmpty()){
			final PagosDifCambiariaForm form=new PagosDifCambiariaForm(ventas);			
			form.open();
			if(!form.hasBeenCanceled()){
				final PagoM pago=getPagosFactory().crearPagoParaDiferienciaCambiaria(form.getVentas());
				getPagosManager().salvarGrupoDePagos(pago);
			}
			form.dispose();
		}else
			MessageUtils.showMessage("No ha seleccionado ventas", "Diferencia cambiaria");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.pagos.IControladorDePagos#diferenciaCambiaria(com.luxsoft.siipap.cxc.domain.PagoM)
	 */
	public boolean diferenciaCambiaria(final PagoM pago){
		if(pago.getTipoDeDocumento().equals("X")
				||pago.getTipoDeDocumento().equals("M")){
			if(pago.getDisponible().doubleValue()>0){
				if(MessageUtils.showConfirmationMessage("Pago de facturas en dolares. Es el disponible diferencia cambiaria?", "Diferencia cambiaria")){
					PagoConOtros pp=getPagosFactory().crearPagoParaDiferienciaCambiaria(pago);
					getPagosManager().salvarGrupoDePagos(pp);
					MessageUtils.showMessage("Pago a diferencia cambiaria generado:\n"+pp.toString(), "Pago autmático");
					return true;
				}
			}else{				
				diferenciaCambiaria(pago.getVentasAplicadas());
			}
		}
		return false;
	}
	
	
	/**
	 * Actualiza las ventas 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void actualizarVentas(List<Venta> ventas) {		
		for(Venta v:ventas){			
			getVentasManager().actualizarVenta(v);			
		}
		/**
		Collection<Venta> seleccionadas=CollectionUtils.select(ventas, new Predicate(){
			public boolean evaluate(Object object) {
				Venta vv=(Venta)object;
				return vv.getSaldo().doubleValue()>0;
			}			
		});
		
		for(Venta v:seleccionadas){
			if(v.getSaldo().doubleValue()>0)
				getVentasManager().actualizarVenta(v);
			if(logger.isDebugEnabled()){
				String pattern="Venta actualizada {0} saldo resultante: {1}";
				logger.debug(MessageFormat.format(pattern, v.getId(),v.getSaldo()));
			}
		}
		**/
	}
	
	/**
	 * Elimina la {@link VentaACredito} y la {@link Provision}
	 * de las facturas seleccionadas
	 * 
	 * @param venta
	 */
	public void eliminarCreditoYProvision(final List<Venta> ventas){
		for(Venta v:ventas){
			getVentasManager().eliminarVentaCredito(v);
		}
	}

	/**
	 * 
	 */
	public void mostrarVenta(Venta v) {
		ConsultaUtils.mostrarVenta(v);
		
	}
	
	/**
	 * Actualiza el comentario de CXC para el catalogo de clientes de credito
	 * @param c
	 */
	public void actualizarComentarioDeCxC(final ClienteCredito c){
		ComentarioDeCreditoForm form=new ComentarioDeCreditoForm(c);
		form.open();
		if(!form.hasBeenCanceled()){
			ServiceLocator.getCXCManager().actualizarCliente(c);
			
		}
	}
	
	/**
	 * Actualiza el numero fiscal de una factura
	 * 
	 * @param venta
	 */
	public void actualizarNumeroFiscal(final Venta venta){
		ActualizarNumeroFiscal form=new ActualizarNumeroFiscal(venta);
		form.open();
		if(!form.hasBeenCanceled()){
			getVentasManager().actualizarVenta(venta);
		}
	}
	
	/**
	 * Intenta eliminar un pago tipo PagoM aplicado
	 * 
	 * @param pago
	 * @return
	 */
	public boolean eliminarPagoM(final PagoM pago){
		final String pattern="Elminar Pago aplicado: {0}\n (Tiene {1} pagos de facturas con descuentos aplicados)";
		int aplicados=0;
		getPagosManager().refresh(pago);
		for(Pago p:pago.getPagos()){
			if(p.getVenta()!=null && p.getVenta().getDescuentos()!=0)
				aplicados++;
		}
		boolean res=MessageUtils.showConfirmationMessage(
				MessageFormat.format(pattern, pago.getId(),aplicados)
				, "Eliminación de pagos");
		if(res){
			try {
				getPagosManager().eliminarPagoM(pago);
				return true;
			} catch (Exception e) {
				MessageUtils.showError("Error al elimiar pago", e);
				return false;
			} 
		}
		return false;
	}
	
	/**
	 * Intenta eliminar un pago unitario 
	 * 
	 * @param pago
	 * @return
	 */
	public boolean eliminarPago(final Pago pago){
		final String pattern="Elminar Pago aplicado: {0}\n ({1} tiene descuento aplicado)";
		String ok=pago.getVenta().getDescuentos()!=0?"Ya":"No";
		boolean res=MessageUtils.showConfirmationMessage(
				MessageFormat.format(pattern, pago.getId(),ok), "Eliminación de pagos");
		if(res){
			try {
				getPagosManager().eliminarPago(pago);
				return true;
			} catch (Exception e) {
				MessageUtils.showError("Error al elimiar pago", e);
				return false;
			} 
		}
		return false;
	}

	/**
	 * Genera las notas de credito de descuento para las facturas que
	 * califiquen
	 *
	 */
	public List<NotaDeCredito> aplicarNotaDeDescuento(final Cliente c,final List<Venta> ventas,final Date fecha){
		return aplicarNotaDeDescuento(c, ventas, fecha,false);
	}
	
	/**
	 * Genera notas de credito de descuento por cobranza
	 * 
	 * califiquen
	 * 
	 * @param c
	 * @param ventas
	 */
	public List<NotaDeCredito> aplicarNotaDeDescuento(final Cliente c,final List<Venta> ventas,final Date fecha,boolean condonar){
		NotasUtils.validarMismoCliente(c, ventas);
		if(MessageUtils.showConfirmationMessage(
				"Una o mas de las facturas acredita  nota de descuento" +
				"\nDesea generarlas?", "Descuentos")){
			
			final NCDescuentoFormModel model=new NCDescuentoFormModel(c,ventas);
			model.setFecha(fecha);
			model.setCondonar(condonar);
			final NCDescuentoForm form=new NCDescuentoForm(model);
			form.open();
			if(!form.hasBeenCanceled() ){
				final List<NotaDeCredito> notas=model.procesar();
				for(NotaDeCredito nota:notas){
					getNotasManager().salvarNotaCre(nota);
				}
				return notas;
			}
			
		}
				
		return null;
	}

	
	/**
	 * Verifica si para este pago proceden notas de credito de descuento
	 * 
	 * 
	 * @param pago
	 * @return
	 */
	public boolean procedeDescuento(PagoM pago) {
		boolean val=(pago.getTipoDeDocumento().equals("E")
				||pago.getTipoDeDocumento().equals("S")
				);
		if(!val) 
			return val;
		for(Pago p:pago.getPagos()){
			if(p.getVenta().getDescuento1()==0 && p.getVenta().getBonificaciones()==0){
				return true;
			}
				
		}
		return false;
	}
	
	/**
	 * Registra un anticipo para el cliente especificado
	 * 
	 * @param cliente
	 * @return
	 */
	public PagoM registrarAnticipo(ClienteCredito cliente) {
		//final PagoM pago=new PagoM(cliente.getCliente(),"K");
		final Anticipo pago=new Anticipo(cliente.getCliente(),"K");
		final AnticipoFormModel model=new AnticipoFormModel(pago);
		final AnticipoForm form=new AnticipoForm(model);
		form.open();
		if(!form.hasBeenCanceled()){
			getPagosManager().salvarGrupoDePagos(pago);
			return pago;
		}
		return null;
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see com.luxsoft.siipap.cxc.pagos.IControladorDePagos#trsladoAJuridico(java.util.List)
	 */
	public boolean trsladoAJuridico(List<Venta> ventas) {
		CXCFiltros.filtrarVentasConSaldo(ventas);
		if(!ventas.isEmpty()){
			final ValueHolder fh=new ValueHolder();
			final SXAbstractDialog dialog=Binder.createDateSelector(fh);
			dialog.setTitle("Fehca de traspaso");
			dialog.open();
			if(!dialog.hasBeenCanceled()){
				for(Venta v:ventas){
					ServiceLocator.getJuridicoManager().transferirJuridico(v,(Date)fh.getValue());
				}				
				MessageUtils.showMessage("Traspaso terminado", "Jurídico");
				return true;
			}			
		}
		return false;
	}

	/*********** Colaboradores **************/

	public PagosFactory getPagosFactory() {
		return pagosFactory;
	}
	public void setPagosFactory(PagosFactory pagosFactory) {
		this.pagosFactory = pagosFactory;
	}

	public VentasManager getVentasManager() {
		return ventasManager;
	}
	public void setVentasManager(VentasManager ventasManager) {
		this.ventasManager = ventasManager;
	}

	public PagosManager getPagosManager() {
		return pagosManager;
	}
	public void setPagosManager(PagosManager pagosManager) {
		this.pagosManager = pagosManager;
	}

	public NotasManager getNotasManager() {
		return notasManager;
	}
	public void setNotasManager(NotasManager notasManager) {
		this.notasManager = notasManager;
	}

	public static void main(String[] args) throws Exception{
		SWExtUIManager.setup();
		
		ClassPathXmlApplicationContext ctx=new ClassPathXmlApplicationContext(new String[]{"classpath:swx-cxc-views.xml"},ServiceLocator.getDaoContext());
		IControladorDePagos c=(IControladorDePagos)ctx.getBean("controladorDePagos");
				
		final List<Venta> ventas=DatosDePrueba.buscarVentaParaPagoAutomatico();
		/**
		final String clave="U050008";
		final List<Venta> ventas=DatosDePrueba.buscarVentasConSaldoEnDB(clave);
		for(Venta v:ventas){
			ServiceLocator.getVentasManager().actualizarVenta(v);			
		}
		c.registrarPagoCreditoProvisionables(ventas.get(0).getCliente(),ventas);
		**/
		//IControladorDePagos c=new ControladorDePagosImpl();
		c.registrarPagoAutomatico(ventas);
	}

	

}
