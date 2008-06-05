package com.luxsoft.siipap.cxc.chequed;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.AssertTrue;

import com.jgoodies.binding.beans.Model;
import com.luxsoft.siipap.cxc.domain.ChequeDevuelto;
import com.luxsoft.siipap.cxc.domain.FormaDePago;
import com.luxsoft.siipap.cxc.domain.NotaDeCredito;
import com.luxsoft.siipap.cxc.domain.Pago;
import com.luxsoft.siipap.cxc.domain.PagoConNota;
import com.luxsoft.siipap.cxc.domain.PagoConOtros;
import com.luxsoft.siipap.cxc.domain.PagoM;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

/**
 * PresentationModel para la generacion de pagos de cheque devueltos
 * 
 * @author Ruben Cancino
 *
 */
public class ChequeDevueltoPagoFormModel extends DefaultFormModel{
	
	public ChequeDevueltoPagoFormModel(final ChequeDevuelto origen){
		super(new SolicitudDePago(origen));
		getComponentModel("cuentaDestino").setEnabled(false);
	}
	
	
	protected SolicitudDePago getSolicitud(){
		return (SolicitudDePago)getBaseBean();
	}
	
	public PagoM procesar(){
		final PagoM pago;
		switch (getSolicitud().getFormaDePago()) {
		case T:
			pago=new PagoConNota();
			((PagoConNota)pago).setNota(getSolicitud().getNota());
			break;
		case S:
			pago=new PagoConOtros();
			((PagoConOtros)pago).setOrigen(getSolicitud().getOtros());
			break;
		default:
			pago=new PagoM();
		}		
		pago.setCliente(getSolicitud().getCheque().getCliente());
		pago.setClave(getSolicitud().getCheque().getCliente().getClave());
		pago.setComentario(getSolicitud().getComentario());
		pago.setCondonar(true);
		pago.setFecha(getSolicitud().getFecha());
		pago.setBanco(getSolicitud().getBanco());
		pago.setReferencia(getSolicitud().getReferencia());
		pago.setCuentaDeposito(getSolicitud().getCuentaDestino());
		pago.setFormaDePago(getSolicitud().getFormaDePago());
		pago.setImporte(getSolicitud().getImporte());
		pago.setMes(Periodo.obtenerMes(getSolicitud().getFecha()));
		pago.setYear(Periodo.obtenerYear(getSolicitud().getFecha()));		
		pago.setTipoDeDocumento("M");
		final Pago det=new Pago();
		det.setClave(pago.getClave());
		det.setCliente(pago.getCliente());
		det.setComentario(pago.getComentario());
		
		det.setReferencia(String.valueOf(getSolicitud().getNumero()));
		//det.setDescReferencia(descReferencia);
		det.setFormaDePago2(getSolicitud().getFormaDePago());
		det.setFecha(getSolicitud().getFecha());
		det.setImporte(getSolicitud().getImporte());
		det.setMes(pago.getMes());		
		det.setNotaDelPago(getSolicitud().getNota());
		det.setNumero(getSolicitud().getNumero());
		det.setOrigen("CHE");
		det.setSucursal(1);
		det.setTipoDocto("H");
		det.setCheque(getSolicitud().getCheque());
		pago.agregarPago(det);
		
		return pago;
	}


	/**
	 * Bean que encapsula el comportamiento para generar un pago de cheque devuelto
	 * 
	 * @author Ruben Cancino
	 *
	 */
	public static class SolicitudDePago extends Model{
		
		private final ChequeDevuelto cheque;
		
		private Date fecha=new Date();
		
		private FormaDePago formaDePago=FormaDePago.H;
		
		private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		
		private String referencia;
		
		private String banco;
		
		
		private String comentario;
		
		private String cuentaDestino="SCOTTIA   (1691945)";
		
		private PagoM otros;
		
		private NotaDeCredito nota;
		
		private String notaRef;

		public SolicitudDePago(ChequeDevuelto cheque) {			
			this.cheque = cheque;
		}
		
		public ChequeDevuelto getCheque() {
			return cheque;
		}
		

		public String getComentario() {
			return comentario;
		}
		public void setComentario(String comentario) {
			Object old=this.comentario;
			this.comentario = comentario;
			firePropertyChange("comentario", old, comentario);
		}

		public Date getFecha() {
			return fecha;
		}
		public void setFecha(Date fecha) {
			Object old=this.fecha;
			this.fecha = fecha;
			firePropertyChange("fecha", old, fecha);
		}

		public FormaDePago getFormaDePago() {
			return formaDePago;
		}
		public void setFormaDePago(FormaDePago formaDePago) {
			Object old=this.formaDePago;
			this.formaDePago = formaDePago;
			firePropertyChange("formaDePago", old, formaDePago);
		}

		public CantidadMonetaria getImporte() {
			return importe;
		}
		public void setImporte(CantidadMonetaria importe) {
			Object old=this.importe;
			this.importe = importe;
			firePropertyChange("importe", old, importe);
		}

		public String getBanco() {
			return banco;
		}
		public void setBanco(String banco) {
			Object oldValue=this.banco;
			this.banco = banco;
			firePropertyChange("banco", oldValue, banco);
		}

		public String getReferencia() {
			return referencia;
		}
		public void setReferencia(String referencia) {
			Object oldValue=this.referencia;
			this.referencia = referencia;
			firePropertyChange("referencia", oldValue, referencia);
		}

		public String getCuentaDestino() {
			return cuentaDestino;
		}
		public void setCuentaDestino(String cuentaDestino) {
			this.cuentaDestino = cuentaDestino;
		}
		
		public int getNumero(){
			return getCheque().getNumero();
		}
		public String getCliente(){
			return MessageFormat.format("{0} ({1})", getCheque().getCliente().getNombre(),getCheque().getCliente().getClave());
		}
		public Date getFechaCheque(){
			return getCheque().getFecha();
		}
		
		
		/**
		 * Dummy solo para que en la forma el binder de fecha no reclame que la propiedad es de solo lectura
		 * @param fecha
		 */
		public void setFechaCheque(final Date fecha){	}
		
		public CantidadMonetaria getImporteCheque(){
			return CantidadMonetaria.pesos(getCheque().getSaldo().doubleValue());
		}
		
		public String toString(){
			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}
		
		/**
		 * Regresa verdadero si el importe del pago es valido
		 * @return
		 */
		@AssertTrue (message="El importe del pago es incorrecto debe<= imp cheque/disponible(Nota/Otroe)")
		public boolean validarImporteCorrecto(){
			switch (getFormaDePago()) {
			case T:
				return getImporte().amount().doubleValue()<=getNota().getSaldoUnificado().abs().amount().doubleValue();
			case S:
				return getImporte().amount().doubleValue()<=getOtros().getDisponible().doubleValue();
			default:
				return getImporte().amount().doubleValue()<=cheque.getSaldo().doubleValue();
			}
			
		}
		@AssertTrue (message="\nEl importe del pago debe ser mayor a 0.0")
		public boolean validarImporteMínimo(){
			return getImporte().amount().doubleValue()>0;
		}
		
		/**
		 * 
		 * @return
		 */
		@AssertTrue (message="Se requiere el banco")
		public boolean validarBanco(){
			switch (getFormaDePago()) {
			case H:
			case N:
			case C:
			case B:
			case Q:
			case Y:
				return !StringUtils.isBlank(getBanco());
			default:
				return true;
			}
		}
		
		@AssertTrue (message="Se requiere la referencia")
		public boolean validarReferencia(){
			switch (getFormaDePago()) {
			case H:
			case N:
			case C:
			case B:
			case Q:
			case Y:
				return !StringUtils.isBlank(getBanco());
			default:
				return true;
			}
		}
		
		@AssertTrue (message="El pago con nota requiere seleccionar la nota de credito")
		public boolean validarFormaDePago(){
			if(FormaDePago.T.equals(getFormaDePago())){
				return getNota()!=null;
			}
			return true;
		}

		public NotaDeCredito getNota() {
			return nota;
		}
		public void setNota(NotaDeCredito nota) {
			Object oldValue=this.nota;
			this.nota = nota;
			firePropertyChange("nota", oldValue, nota);
			if(nota!=null){
				setNotaRef("Nota #: "+ nota.getNumero()+" Saldo: "+nota.getSaldoAsMoneda());
			}
		}

		public String getNotaRef() {
			return notaRef;
		}
		public void setNotaRef(String notaRef) {
			Object oldValue=this.notaRef;
			this.notaRef = notaRef;
			firePropertyChange("notaRef", oldValue, notaRef);
		}

		public PagoM getOtros() {
			return otros;
		}
		public void setOtros(PagoM otros) {
			Object oldValue=this.otros;
			this.otros = otros;
			firePropertyChange("otros", oldValue, otros);
			if(otros!=null){
				setNotaRef("Origen: "+otros.getId()+" Disponible: "+otros.getDisponible());
			}
		}

		
		
		public CantidadMonetaria getSaldo(){
			return CantidadMonetaria.pesos(getCheque().getSaldo().doubleValue());
		}
		
	}

}
