package com.luxsoft.siipap.alt.clientes;

import com.jgoodies.binding.PresentationModel;
import com.luxsoft.siipap.cxc.domain.Cliente;
import com.luxsoft.siipap.cxc.services.ActualizadorDeClientes;
import com.luxsoft.siipap.cxc.services.ActualizadorDeClientesImpl;
import com.luxsoft.siipap.domain.Periodo;
import com.luxsoft.siipap.ventas.managers.VentasManager;

public class AltAModel extends PresentationModel{
	
	private Cliente cliente;
	private Periodo periodo;
	private ActualizadorDeClientes actualizador;
	private VentasManager manager;
	
	public AltAModel(){
		super(null);
		setBean(this);
	}
		

	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		Object old=this.cliente;
		this.cliente = cliente;
		firePropertyChange("cliente", old, cliente);
	}

	public Periodo getPeriodo() {
		if(periodo==null){
			periodo=Periodo.getPeriodo(-90);
		}
		return periodo;
	}

	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}
	
	public void actualizarDatos(){
		getManager().actualizarVentasYtd(getCliente());
		getActualizador().actualizarCliente(getCliente().getCredito());
	}
	

	public ActualizadorDeClientes getActualizador() {
		return actualizador;
	}


	public void setActualizador(ActualizadorDeClientes actualizador) {
		this.actualizador = actualizador;
	}


	public void setActualizador(ActualizadorDeClientesImpl actualizador) {
		this.actualizador = actualizador;
	}


	public VentasManager getManager() {
		return manager;
	}


	public void setManager(VentasManager manager) {
		this.manager = manager;
	}
	
	

}
