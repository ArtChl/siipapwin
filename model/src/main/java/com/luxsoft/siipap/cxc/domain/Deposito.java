package com.luxsoft.siipap.cxc.domain;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.domain.Sucursales;
import com.luxsoft.utils.domain.MutableObject;

public class Deposito extends MutableObject{
	
	private Long id;	
	
	private Sucursales sucursal=Sucursales.OFICINAS;
	
	private int sucursalId=1;
	
	private String origen="CRE";
	
	private FormaDePago formaDePago=FormaDePago.H;
	
	private Date fecha=new Date();
	
	private CantidadMonetaria importe=CantidadMonetaria.pesos(0);
	
	private String cuentaDestino;
	
	private String banco;
	
	private String cuenta;
	
	private int folio;
	
	private Set<DepositoUnitario> partidas=new HashSet<DepositoUnitario>();
	
	private Long cobradorId;
	
	private String cobrador;
	
	private Boolean noenviar;
	
	private String comentario;
	
	private Long cuentaId;

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		Object oldValue=this.fecha;
		this.fecha = fecha;
		getPropertyChangeSupport().firePropertyChange("fecha", oldValue, fecha);
	}
	

	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CantidadMonetaria getImporte() {
		return importe;
	}

	public void setImporte(CantidadMonetaria importe) {
		Object oldValue=this.importe;
		this.importe = importe;
		getPropertyChangeSupport().firePropertyChange("importe", oldValue, importe);
	}	
	
	public FormaDePago getFormaDePago() {
		return formaDePago;
	}
	public void setFormaDePago(FormaDePago formaDePago) {
		this.formaDePago = formaDePago;		
	}
	
	public Collection<DepositoUnitario> getPartidas() {
		return Collections.unmodifiableCollection(partidas);
	}
	
	public boolean agregarPartida(final DepositoUnitario du){
		du.setDeposito(this);
		return partidas.add(du);
	}
	
	public boolean eliminarPartida(final DepositoUnitario du){
		boolean res=partidas.remove(du);
		if(res)
			du.setDeposito(null);
		return res;
	}
	
	public Sucursales getSucursal() {
		return sucursal;
	}
	public void setSucursal(Sucursales sucursal) {
		this.sucursal = sucursal;
	}
	
	public String getCuentaDestino() {
		return cuentaDestino;
	}
	public void setCuentaDestino(String cuentaDestino) {
		Object oldValue=this.cuentaDestino;
		this.cuentaDestino = cuentaDestino;
		getPropertyChangeSupport().firePropertyChange("cuentaDestino", oldValue, cuentaDestino);
	}
	
	public void actualizarImporte(){
		BigDecimal imp=BigDecimal.ZERO;
		for(DepositoUnitario d:getPartidas()){
			imp=imp.add(d.getImporte());
		}
		setImporte(CantidadMonetaria.pesos(imp.doubleValue()));
	}
	
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Deposito other = (Deposito) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;	
	}	
	
	
	
	public String toString(){		
		//return ToStringBuilder
		//.reflectionToString(this,ToStringStyle.SHORT_PREFIX_STYLE);
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)		
		.append(getFecha())
		.append(getBanco())
		.append("Cuenta:",getCuenta())
		.append("Importe:",getImporteAsDouble())
		.append("TipoP:",getFormaDePago())
		.toString();
	}
	
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	
	
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	
	public void resolverCuenta(){
		if(!StringUtils.isEmpty(getCuentaDestino())){
			String cta=getCuentaDestino();
			int index=cta.indexOf('(');
			String banco=cta.substring(0, index-1).trim();
			int index2=cta.indexOf(')');
			String cuenta=cta.substring(index+1,index2).trim();
			setBanco(banco);
			setCuenta(cuenta);
		}
		
	}
	
	public static final int MAXIMO_CHEQUES=5;
	
	@SuppressWarnings("unchecked")
	public void agrupar(){
		final Set<DepositoUnitario> registros=new HashSet<DepositoUnitario>();
		registros.addAll(getPartidas());
		
		final Collection<DepositoUnitario> locales=CollectionUtils.select(registros, new Predicate(){
			public boolean evaluate(Object object) {
				DepositoUnitario du=(DepositoUnitario)object;
				return du.getBanco().equalsIgnoreCase(getBanco());
			}			
		});
		final Collection<DepositoUnitario> foraneos=CollectionUtils.select(registros, new Predicate(){
			public boolean evaluate(Object object) {				
				DepositoUnitario du=(DepositoUnitario)object;
				return !du.getBanco().equalsIgnoreCase(getBanco());
			}			
		});
		int last=agrupar(locales,0);
		agrupar(foraneos,last);
	}
	
	public int agrupar(final Collection<DepositoUnitario> registros,int start){
		int buff=0;
		int grupo=start;
		for(DepositoUnitario d:registros){
			if(buff++%5==0){
				grupo++;
			}
			d.setGrupo(grupo);
		}
		return grupo;
	}
	
	public int getSucursalId() {
		return sucursalId;
	}
	public void setSucursalId(int sucursalId) {
		this.sucursalId = sucursalId;
	}
	
	public String getCuentaContable(){
		String cta=getBanco();
		if(cta.startsWith("BANCOMER")){
			return "102-0001-000";
		}else if(cta.startsWith("BANAMEX")){
			return "102-0002-000";
		}else if(cta.startsWith("HSBC")){
			return "102-0004-000";
		}else if(cta.startsWith("SCOTTIA")){
			return "102-0005-000";
		}else if(cta.startsWith("SANTANDER")){
			return "102-0008-000";
		}else
			return "ERRROR";
	}
	
	public String getConcepto(){
		String pattern="{0}{1} {2} ";
		return MessageFormat.format(pattern, ""
				,getBanco()
				,getId()!=null?"Dep:"+getId():"F.P: "+StringUtils.substring(getFormaDePago().toString(), 0,7));
	}
	
	public double getImporteAsDouble(){
		return getImporte().amount().doubleValue();
	}
	public int getFolio() {
		return folio;
	}
	public void setFolio(int folio) {
		this.folio = folio;
	}
	
	public void actualizarDatos(){
		for(DepositoUnitario d:partidas){
			d.setFormaDePagoDesc(formaDePago.getDesc());
		}
	}
	
	public Long getCobradorId() {
		return cobradorId;
	}
	public void setCobradorId(Long cobradorId) {
		this.cobradorId = cobradorId;
	}
	
	public String getCobrador() {
		return cobrador;
	}
	public void setCobrador(String cobrador) {
		String old=this.cobrador;
		this.cobrador = cobrador;
		propertyChangeSupport.firePropertyChange("cobrador", old, cobrador);
	}
	public Boolean getNoenviar() {
		return noenviar;
	}
	public void setNoenviar(Boolean noenviar) {
		this.noenviar = noenviar;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public Long getCuentaId() {
		return cuentaId;
	}
	public void setCuentaId(Long cuentaId) {
		this.cuentaId = cuentaId;
	}
	
	
	

}
