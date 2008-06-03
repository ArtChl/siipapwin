package com.luxsoft.siipap.em.replica.dao;

import java.util.List;


import com.luxsoft.siipap.em.replica.domain.ReplicaLog;

public interface ReplicaLogDao {
	
	public void salvar(ReplicaLog log);
	public void borrar(ReplicaLog log);
	public ReplicaLog buscar(Long id);	
	public ReplicaLog buscar(String entity,String tabla,int year,int mes);
	public List<ReplicaLog> buscar(String entity);

}
