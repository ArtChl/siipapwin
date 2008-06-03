package com.luxsoft.siipap.ventas.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.validator.Length;

import com.luxsoft.siipap.cxc.domain.ClienteHolder;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.utils.DateUtils;
import com.luxsoft.utils.domain.MutableObject;

public class VentaACredito extends MutableObject implements ClienteHolder{
	
	private Long id;
	
	private Venta venta;
	
	private Date vencimiento;
		
	private Date fechaRevision;
	
	private Date fechaRevisionCxc;
	
	/**
	 * Recepción de facturas de sucursal pendientes de recibir o de
	 * algún documento para su revision/cobro
	 * 
	 */
	@Length(max=50, message="El tamaño maximo del comentario es de 50 caracteres")
	private String comentario;
	
	private int plazo;
	
	private boolean revision;
	
	private boolean recibidaCXC;
	
	private Date fechaRecepcionCXC;
	
	private boolean revisada;
	
	private Date diaPago;
	
	private Date reprogramarPago;
	
	/**
	 * Comentario de los cobradores como resultado de
	 *  la revision/cobro de la factura
	 * 
	 */
	@Length(max=255, message="El tamaño maximo del comentario es de 255 caracteres")
	private String comentarioRepPago;
	
	private boolean retrasoTolerado;
	
	private double pagos;
	
	public double descuento;
	
	private double descuento2;
	
	private double cargo;
	
	
	public VentaACredito(){
		
	}
	
	public VentaACredito(final Venta v){
		this.venta=v;
		setPlazo(v.getCliente().getCredito().getPlazo());
		v.setCredito(this);
		setRevision("V".equalsIgnoreCase(v.getCliente().getTipo_vencimiento()));
		actualizar();
		actualizarDescuentoPrecioNetoProchemex();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		Object oldValue=this.comentario;
		this.comentario = comentario;
		getPropertyChangeSupport().firePropertyChange("comentario", oldValue, comentario);
	}
	
	public Date getFechaRevision() {
		return fechaRevision;
	}
	public void setFechaRevision(Date fechaRevision) {
		this.fechaRevision = fechaRevision;
	}
	public Venta getVenta() {
		return venta;
	}
	public void setVenta(Venta venta) {
		this.venta = venta;
	}		

	public int getPlazo() {
		return plazo;
	}

	public void setPlazo(int plazo) {
		int oldValue=this.plazo;
		this.plazo = plazo;
		getPropertyChangeSupport().firePropertyChange("plazo", oldValue, plazo);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((venta == null) ? 0 : venta.getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass())
			return false;
		final VentaACredito other = (VentaACredito) obj;
		if (venta == null) {
			if (other.venta != null)
				return false;
		} else if (!venta.getId().equals(other.venta.getId()))
			return false;
		return true;
	}

	public boolean isRevision() {
		return revision;
	}

	public void setRevision(boolean revision) {
		this.revision = revision;
	}

	public Date getFechaRevisionCxc() {
		return fechaRevisionCxc;
	}

	public void setFechaRevisionCxc(Date fechaRevisionCxc) {
		this.fechaRevisionCxc = fechaRevisionCxc;
	}

	

	public boolean isRecibidaCXC() {
		return recibidaCXC;
	}

	public void setRecibidaCXC(boolean recibidaCXC) {
		boolean old=this.recibidaCXC;
		this.recibidaCXC = recibidaCXC;
		getPropertyChangeSupport().firePropertyChange("recibidaCXC",old,recibidaCXC);
	}

	public Date getDiaPago() {
		return diaPago;
	}

	public void setDiaPago(Date diaPago) {
		this.diaPago = diaPago;
	}
	
	

	public Date getReprogramarPago() {
		return reprogramarPago;
	}

	public void setReprogramarPago(Date reprogramarPago) {
		Object old=this.reprogramarPago;
		this.reprogramarPago = reprogramarPago;
		getPropertyChangeSupport().firePropertyChange("reprogramarPago", old, reprogramarPago);
	}
	
	
	
	public Date getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}
	
	

	public Date getFechaRecepcionCXC() {
		return fechaRecepcionCXC;
	}

	public void setFechaRecepcionCXC(Date fechaRecepcionCXC) {
		this.fechaRecepcionCXC = fechaRecepcionCXC;
	}
	
	public int getAtrasoOperativo(){
		Date today=new Date();		
			long res=today.getTime()-getVencimiento().getTime();
			if(res>0){				
				long dias=(res/(86400*1000));			
				return (int)dias;
			}else{
				return 0;
			}
		
		
	}
	
	
	
	public void actualizar(){
		actualizar(new Date());
	}

	/**
	 * Entre otras cosas este metodo actualiza el vencimiento en funcion de la ulimta fecha de revision registrada por
	 * el usuario de CXC, propiedad revisionCxc
	 *
	 */
	public void actualizar(final Date fechaSistema){
		
		//Calcular la fecha de revision  original de la venta (Servira para calcular el plazo efectivo del credito)
		Date dRev=getVenta().getFecha();
		dRev=DateUtils.calcularFechaMasProxima(getVenta().getFecha(),getVenta().getDiaRevision(), false);		
		setFechaRevision(dRev);
		
		//Actualizar revision mas proxima si no se ha mandado a revisa (ej getRevisada()=false) o si se trata de un registro nuevo
		if(!isRevisada()){
			//Calcular la proxima fecha			
			final Date proxima=DateUtils.calcularFechaMasProxima(fechaSistema,getVenta().getDiaRevision(), false);
			setFechaRevisionCxc(proxima);
		} 
		
		//En el caso de una venta a credit nueva se fija la fecha revision cxc a la fecha revision del sistema (VentaACredito.fechaRevision)
		//if(getId()==null)
			//setFechaRevisionCxc(dRev);
		
		//Calcular el vencimiento
		
		final Date inicio;
		if(isRevision())
			inicio=getFechaRevisionCxc();
		else			
			inicio=getVenta().getFecha();
		
		/**RCR 18/12/2007 Cambio requerido en revisiones **/
		//int plazo=getVenta().getCliente().getCredito().getPlazo();		
		//setPlazo(plazo);
		
		Calendar c=Calendar.getInstance();
		c.setTime(inicio);
		c.add(Calendar.DATE, plazo);
		final Date vence=c.getTime();
		
		setVencimiento(vence);
		//Ajuste para las tipo G
		if(getVenta().getTipo().equals("G")){
			final Calendar c2=Calendar.getInstance();
			c2.setTime(getVenta().getFecha());
			c2.add(Calendar.DATE, 30);
			final Date vtoG=c2.getTime();			
			setVencimiento(vtoG);
		}
		calcularFechaDePago();
	}

	private void calcularFechaDePago(){
		
		Date vencimiento=getVencimiento();
		int dia=getVenta().getDiaPago();
		Date pago=DateUtils.calcularFechaMasProxima(vencimiento,dia, true);
		if(!isRevisada())
			setDiaPago(pago);
		setReprogramarPago(pago);
		if(getVenta().getSaldo()!=null){
			if(getVenta().getSaldo().abs().doubleValue()>1){
				Date hoy=new Date();
				if(hoy.after(vencimiento)){
					Date proximoPago=DateUtils.calcularFechaMasProxima(hoy, dia, true);
					setReprogramarPago(proximoPago);
				}
			}
		}
		
		
	}
	
	/**
	 * Metodo para programar pagos
	 *  
	 * @param vencimiento
	 */
	public void calcularProximaFechaDePago(final Date vencimiento){
		int dia=getVenta().getDiaPago();
		Date pago=DateUtils.calcularFechaMasProxima(vencimiento,dia, true);
		if(!isRevisada())
			setDiaPago(pago);
		setReprogramarPago(pago);
		if(getVenta().getSaldo()!=null){
			if(getVenta().getSaldo().abs().doubleValue()>1){
				Date hoy=new Date();
				if(hoy.after(vencimiento)){
					Date proximoPago=DateUtils.calcularFechaMasProxima(hoy, dia, true);
					setReprogramarPago(proximoPago);
				}
			}
		}		
	}
	
	
	
	public double getPagos() {
		return pagos;
	}

	public void setPagos(double pagos) {
		this.pagos = pagos;
	}

	public boolean isRevisada() {
		return revisada;
	}

	public void setRevisada(boolean revisada) {
		this.revisada = revisada;
	}
	
	

	public String getComentarioRepPago() {
		return comentarioRepPago;
	}

	public void setComentarioRepPago(String comentarioRepPago) {
		Object oldValue=this.comentarioRepPago;
		this.comentarioRepPago = comentarioRepPago;
		getPropertyChangeSupport().firePropertyChange("comentarioRepPago", oldValue, comentarioRepPago);
	}
	
	

	public boolean isRetrasoTolerado() {
		return retrasoTolerado;
	}

	public void setRetrasoTolerado(boolean retrasoTolerado) {
		this.retrasoTolerado = retrasoTolerado;
	}	

	/**
	 * Descuento originalmente pactado para la venta
	 * 
	 * @return
	 */
	public double getDescuento() {		
		return descuento;
	}
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public double getDescuento2() {
		return descuento2;
	}

	public void setDescuento2(double descuento2) {
		this.descuento2 = descuento2;
	}
	
	
	public void actualizarDescuentoPrecioNetoProchemex(){		
		Venta v=getVenta();
		if(v.getOrigen().equals("CRE")){
			if(v.getTipo().endsWith("G")){
				double desc=Math.abs(v.getDescuentoFacturado());				
				v.getCredito().setDescuento(desc);
			}			
			else if(v.getTipo().endsWith("N")||v.getTipo().endsWith("P")||v.getTipo().endsWith("X")){				
				CantidadMonetaria importeF=CantidadMonetaria.pesos(0);
				CantidadMonetaria importeL=CantidadMonetaria.pesos(0);
				
				for(VentaDet det:v.getPartidas()){
					importeL=importeL.add(CantidadMonetaria.pesos(det.getImporteBruto()));
					importeF=importeF.add(CantidadMonetaria.pesos(det.getImporteSemiNeto()));
				}
				double cs=importeF.amount().doubleValue()/importeL.amount().doubleValue();
				double desc=cs*100;
				desc=100-desc;				
				System.out.println("Descuento: "+desc+" venta_id"+getId());
				BigDecimal val=BigDecimal.valueOf(desc).setScale(2,RoundingMode.HALF_EVEN).abs();				
				v.getCredito().setDescuento(val.doubleValue());
			}				
		}		
}

	public String getClave() {
		return getVenta().getClave();
	}

	public double getCargo() {
		return cargo;
	}

	public void setCargo(double cargo) {
		this.cargo = cargo;
	}
	

}
