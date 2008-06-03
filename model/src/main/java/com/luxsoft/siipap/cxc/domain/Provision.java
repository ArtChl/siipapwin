package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.ventas.domain.Venta;
import com.luxsoft.siipap.ventas.domain.VentaDet;
import com.luxsoft.utils.domain.MutableObject;

/**
 *  - Seleccionar todas las ventas que requieren provision
 *  - Para cada una de ellas generar un registro en esta tabla si es que no existe
 *    y si ya existe se actualiza recalculando segun sea el caso
 *  
 *  - Calcular la provision general (Del Maestro):
 * 		
 * 		- Segun el cliente se buscar en DescuentoPorCliente
 * 		  si encontro{
 * 				descuento1 se fija a DescuentoPorCliente.descuento
 * 				descuento2 se fija a DescuentoPorCliente.adicional
 * 		   }else{
 * 				Se calcula el volumen acumulado del cliente para el periodo
 * 				(El mes de la venta)
 * 				Se busca en DescuentoPorVolumen el correspondiente al monto calculado 
 * 				(Que sea menor o igual al valor obtenido)
 * 				y descuento1 se fija este
 * 	 
 * 				Se buscar en DescuentoPorVolumen un descuento adicional
 * 				para este cliente mismo que si se encuentra se asigna 
 * 			  	a descuento2
 * 		   }
 * 	
 *  - Buscar si para esta venta existe un acuerdo especial (Un registro en DescuentoEspecial)
 *  	Si(){
 *  		que lo asigne al descuento2
 *  	}
 *  
 * 	- Calcular la provision a detalle (De las partidas)
 * 
 * 	- Para cada partidad de la venta generar un registro en PartidaDet , si ya existe
 * 		actualizarla
 * 	- Buscar si cada una de las partidas tiene DescuentoPorArticulo
 * 		SI(){
 * 			asignar DescuentoPorArticulo.descuento a descuento de la partida
 * 			y se calcual el importe en funcion de este
 * 		}else{
 * 			asignar a descuento armado de Provision.descuento1 y Provision.descuento2
 * 		}
 * 
 *    
 *    TODO Actualizar la documentacion anterior
 *    TODO Eliminar los camos no necesarios
 *    TODO Eliminar y organizar los procedimientos
 *    TODO Como este bean se carga desde hibernate con las partidas fetched no hay problema en implementar
 *    el metodo del getImporte() y getImporteDescuento1() en funcion de las mismas.
 * 
 */
public class Provision extends MutableObject{
	
	public static final int MAXIMO_DIAS_ATRASO=90;
	
	private Long id;
	
	private double cargoCalculado=0;
	
	//private double cargoAplicado=0;
	
	private Venta venta;
	
	private Date vencimiento;
	
	private double descuento1;
	
	private double descuento2;
	
	private double descuentoConCargo;
	
	private int diasAtraso;
	
	/**
	 * Importe de la provision, Hibernate la calcula en linea con un sub select a SW_PROVISIONDET.IMPORTE
	 */
	private BigDecimal importe;
	
	/**
	 * Importe de la provision sin contemplar el descuento 2,hibernate la calcula en linea cun un sub select
	 */
	private BigDecimal importeDescuento1;	
	
	private CantidadMonetaria provision=CantidadMonetaria.pesos(0);
	
	@SuppressWarnings("unused")
	private boolean aplicado=false;
	
	private List<ProvisionDet> partidas;
	
	private Date creado=new Date();
	
	private Date modificado;
	
	
	
	public Provision(){		
	}

	public Provision(final Venta venta) {		
		setVenta(venta);
		venta.setProvision(this);
		this.vencimiento=venta.getVencimiento();
		actualizarDiasDeAtraso();
		actualizarCargo();		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isAplicado() {
		return Math.abs(venta.getDescuento1())>0;
	}
	public void setAplicado(boolean aplicado) {
		this.aplicado = aplicado;
	}
	
	public void actualizarAplicado(){
		boolean porNota=Math.abs(getVenta().getDescuento1())>0;
		if(porNota)
			setAplicado(true);
		else{
			if(venta.getSaldo()!=null && venta.getSaldo().abs().doubleValue()>0){
				setAplicado(true);
			}
		}		
	}

	

	public double getCargoCalculado() {
		return cargoCalculado;
	}
	public void setCargoCalculado(double cargoCalculado) {
		this.cargoCalculado = cargoCalculado;
	}
	

	public int getDiasAtraso() {
		return diasAtraso;
	}
	public void setDiasAtraso(int diasAtraso) {
		this.diasAtraso = diasAtraso;
	}

	public List<ProvisionDet> getPartidas() {
		if(partidas==null)
			partidas=new ArrayList<ProvisionDet>();
		return partidas;
	}
	
	protected void setPartidas(List<ProvisionDet> partidas) {
		this.partidas = partidas;
	}
	
	public ProvisionDet agregarPartida(VentaDet vdet){
		ProvisionDet pdet=new ProvisionDet(this,vdet);
		getPartidas().add(pdet);
		pdet.setProvision(this);
		return pdet;
	}

	public CantidadMonetaria getProvision() {
		return provision;
	}
	public void setProvision(CantidadMonetaria provision) {
		this.provision = provision;
	}

	public Date getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}

	public Venta getVenta() {
		return venta;
	}
	public void setVenta(Venta venta) {
		this.venta = venta;
	}
	
	
	
	/**
	 * Actualiza los dias de atraso de la venta en funcion del vencimiento
	 * 
	 * @return
	 */
	public int actualizarDiasDeAtraso(){
		Calendar c=Calendar.getInstance();
		Date current=c.getTime();
		long res=current.getTime()-getVenta().getVencimiento().getTime();
		if(res>0){
			long dias=res/(86400*1000);
			setDiasAtraso((int)dias);
			return (int)dias;
		}else{ 
			setDiasAtraso(0);
			return 0;
		}
		
	}
	
	/**
	 * Actualiza el cargo de la provision segun los dias de atraso
	 *
	 */
	public void actualizarCargo(){
		int dias=actualizarDiasDeAtraso();
		/**
		if(dias>MAXIMO_DIAS_ATRASO){
			setDescuento1(0);
			setDescuento2(0);
			setCargoCalculado(0);
			return;
		}
		**/
		if(dias>0){
			int semanas=dias/7;
			
			setCargoCalculado(semanas*.5);
		}else{
			setCargoCalculado(0);
		}
	}
	
	/*** Metodos relacionados con el descuento ***/
	
	public double getDescuento1() {
		return descuento1;
	}
	public void setDescuento1(double descuento1) {
		this.descuento1 = descuento1;
	}

	public double getDescuento2() {
		return descuento2;
	}
	public void setDescuento2(double descuento2) {
		this.descuento2 = descuento2;
	}
	
	public double getDescuentoConCargo() {
		return descuentoConCargo;
	}
	public void setDescuentoConCargo(double descuentoConCargo) {
		this.descuentoConCargo = descuentoConCargo;
	}

	public double getDescuentoNeto1(){
		return getDescuento1Real()-getCargoCalculado();
	}
	
	/**
	 * Descuento calculado a partir de las partidas
	 * 
	 * @return
	 */
	public double getDescuento1Real(){
		double d1=getImporteDescuento1()!=null?getImporteDescuento1().doubleValue():0;
		double val=d1*100;
		val=val/getVenta().getImporteBruto().amount().doubleValue();		
		return val+getCargoCalculado();		
	}
	
	/**
	 * Descuento calculado a partir de las partidas pero con el cargo calculado
	 * 
	 * @return
	 */
	public double getDescuentoFinal(){
		final double vval=(getProvision().amount().doubleValue()/getVenta().getSubTotal().abs().amount().doubleValue())*100;
		BigDecimal des=new BigDecimal(vval,new MathContext(4,RoundingMode.HALF_EVEN));
		//return getProvision().amount().doubleValue()/getVenta().getSubTotal().abs().amount().doubleValue()*100;
		return des.doubleValue();
		/**
		double des=getDescuento1Real();		
		des=(100-(100-des))+((100-des)*des/100);
		return des;
		**/
	}
	
	/** Metodos finales para el acceso a descuentos**/
	
	/**
	 * El descuento calculado para la provision, este es afectado por el posible
	 * cargo provocado por atraso en el pago y una vez pasado de la maxima tolerancia
	 * permitida (90 dias) este es cero
	 * 
	 * El valor de este descuento contempla
	 * 
	 */
	public double getDescuento(){
		if(esValido()){			
			return getDescuentoFinal();
		}else
			return 0;		
	}
	
	public double getDescuentoPactado(){
		return getDescuento1Real();
	}
	
	/**
	 * Actualiza el importe de la provision en funcion de las partidas y los descuentos 
	 * respectivos
	 *
	 */
	public void calcularProvision(){
		if(isAplicado()){			
			return;
		}
		actualizarCargo();
		CantidadMonetaria monto=new CantidadMonetaria(0,getVenta().getTotal().getCurrency());
		
		boolean prorratear=false;
		
		for(ProvisionDet det:getPartidas()){
			det.calcularImporte();	
			if(det.getDescArticulo()!=null)
				prorratear=true;
			if(monto==null || (monto.abs().amount().doubleValue()==0)){
				monto=det.getImporte();
				continue;
			}
			monto=monto.add(det.getImporte());			
		}
		
		if(monto.amount().abs().doubleValue()>0){
			//Calcular el corte y maniobras
			CantidadMonetaria cortes=getVenta().getImporteCortes()!=null?getVenta().getImporteCortes():CantidadMonetaria.pesos(0);
			CantidadMonetaria mo=getVenta().getImporteManiobras();
			if(mo!=null){
				cortes=cortes.add(mo);
			}
			
			//Aplicamos en cascada el descuento1 
			//double desc1=getDescuento1();
			double desc1=getDescuentoProrrateado();
			
			//desc1=desc1-getCargoCalculado();				
			CantidadMonetaria provCortes=cortes.multiply(-1*desc1/100);
			//CantidadMonetaria provCortes=cortes.multiply(desc1);
			monto=monto.add(provCortes);
			
			setProvision(monto);
			
			final double subtotal=getVenta().getSubTotal().abs().amount().doubleValue();
			final double devoluciones=getVenta().getDevolucionesCred()/1.15;
			final double subTotalSinDevoluciones=subtotal+devoluciones;
			final double vval=(monto.amount().abs().doubleValue()/subTotalSinDevoluciones)*100;
			
			setDescuentoConCargo(vval);//des.doubleValue());
			if(!prorratear){
				//setDescuentoConCargo(getDescuento1()-getCargoCalculado());
				setDescuentoConCargo(getDescuento1());
			}
		}
	}
	
	public double getDescuentoProrrateado(){
		//La suma de las partidas
		CantidadMonetaria monto=CantidadMonetaria.pesos(0);
		for(ProvisionDet det:getPartidas()){
			monto=monto.add(det.getImporte().abs());
		}
		
		final CantidadMonetaria devoluciones=getVenta().getDevoluciones().divide(1.15);
		final CantidadMonetaria impBruto=getVenta().getImporteBruto();
		
		final CantidadMonetaria tot=impBruto.add(devoluciones);
		double desc=monto.amount().doubleValue()/tot.amount().doubleValue();
		return desc*100;
		
	}
	
	public boolean debeCalcularDescuento(){
		for(ProvisionDet det:getPartidas()){
			if(det.getDescArticulo()!=null)
				return true;
		}
		return false;
	}
	
	public boolean esValido(){		
		//return getDiasAtraso()<=MAXIMO_DIAS_ATRASO;
		return true;
	}	

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public BigDecimal getImporteDescuento1() {
		return importeDescuento1;
	}

	public void setImporteDescuento1(BigDecimal importeDescuento1) {
		this.importeDescuento1 = importeDescuento1;
	}	
	
	public Date getCreado() {
		return creado;
	}
	public void setCreado(Date creado) {
		this.creado = creado;
	}
	
	public Date getModificado() {
		return modificado;
	}
	public void setModificado(Date modificado) {
		this.modificado = modificado;
	}

	public String toString(){
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
		.append("VentaId:",getVenta().getId())
		.append("Atraso: "+getDiasAtraso())
		.append("Provision: "+getProvision())
		.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(obj==null) return false;
		Provision p=(Provision)obj;
		return new EqualsBuilder()
		.append(getVenta(), p.getVenta())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,35)
		.append(getVenta())
		.toHashCode();
	}

}
