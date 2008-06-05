package com.luxsoft.siipap.cxc.depositos;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;

import com.jgoodies.validation.util.PropertyValidationSupport;
import com.luxsoft.siipap.cxc.domain.Deposito;
import com.luxsoft.siipap.cxc.domain.DepositoUnitario;
import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.swing.form2.DefaultFormModel;

public class DepositoFormModel extends DefaultFormModel{
	
	private EventList<DepositoUnitario> partidas;

	public DepositoFormModel() {
		super(Deposito.class);
		
	}
	
	public DepositoFormModel(final Deposito bean) {
		super(bean);		
	}
	
	public Deposito getDeposito(){
		return (Deposito)getBaseBean();
	}
	
	protected void init(){
		partidas=new BasicEventList<DepositoUnitario>();
		partidas=new ObservableElementList<DepositoUnitario>(partidas,GlazedLists.beanConnector(DepositoUnitario.class));
		for(DepositoUnitario d:getDeposito().getPartidas()){
			partidas.add(d);
		}
	}
	
	public EventList<DepositoUnitario> getPartidas(){
		return partidas;
	}
	
	public boolean agregarPartida(DepositoUnitario du){
		boolean res=getDeposito().agregarPartida(du);
		if(res){
			partidas.add(du);
			actualizarImporte();
		}
		return res;
	}
	
	public boolean eliminarPartida(final DepositoUnitario du){
		boolean res=getDeposito().eliminarPartida(du);
		if(res){
			partidas.remove(du);
			actualizarImporte();
		}
		return res;
	}

	@Override
	protected void addValidation(PropertyValidationSupport support) {		
		if(getDeposito().getImporte().getAmount().doubleValue()<=0){			
			support.addError("importe", "El importe es incorrecto");
		}
	}
	
	
	protected void actualizarImporte(){
		CantidadMonetaria importe=CantidadMonetaria.pesos(0);
		for(DepositoUnitario du:partidas){
			importe=importe.add(CantidadMonetaria.pesos(du.getImporte().doubleValue()));
		}
		getDeposito().setImporte(importe);
	}
	
	public void commit(){
		getDeposito().setSucursalId(getDeposito().getSucursal().getNumero());
		getDeposito().resolverCuenta();
		getDeposito().agrupar();
	}
	

}
