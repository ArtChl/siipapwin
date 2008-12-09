package com.luxsoft.siipap.inventarios.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.luxsoft.siipap.domain.CantidadMonetaria;
import com.luxsoft.siipap.inventarios.domain.InventarioMensual;
import com.luxsoft.siipap.services.ServiceLocator;

public class InentarioMensual_Parche1 extends HibernateDaoSupport{
	
	/**
	 * Verifica que el costo inicial de un {@link InventarioMensual} sea igual al final del
	 * periodo anterior
	 *
	 */
	@SuppressWarnings("unused")
	public void verificarCostoInicialContraFinal(){
		getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				List<InventarioMensual> list=session.createQuery("from InventarioMensual i  " +						
						" order by i.clave,i.year,i.mes")
						.list();
				
				BigDecimal cuadre=BigDecimal.ZERO;
				for(int index=1;index<list.size();index++){
					InventarioMensual actual=list.get(index);
					InventarioMensual anterior=anterior=list.get(index-1);
					
					BigDecimal dif=actual.getCostoInicial().subtract(anterior.getCosto()).amount();
					if(!actual.getClave().equals(anterior.getClave()))
						continue;
					if( dif.doubleValue()!=0){						
						String pattern="Ini:{0}";
						System.out.println("Diferencias en:"+actual.getClave()+ "Dif: "+dif+" Per:"+actual.getMes()+ "Year:"+actual.getYear());
						/**
						System.out.println(MessageFormat.format(pattern, 
								 actual.getCostoInicial().amount()
								,anterior.getCosto().amount()
								,dif,actual.getYear()
								,actual.getMes()
								,actual.getClave()));
						cuadre=cuadre.add(dif);
						**/
					}
					
					
				}
				System.out.println("\nCuadre: "+cuadre);
				
				return null;
			}			
		});
	}
	
	/**
	 * Ajusta el costo promedio para casos en el que existe saldo sin costo promedio para el mismso y en un mes posterior 
	 * existe el calculo de CostoPromedio. Normalmente es util para casos en los que se vende sin existencia pero no siempre
	 * 
	 * DEBE SER EJECUTADO ANTES DEL FORWARDCOSTO
	 *
	 */
	public void execute2(){
		getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				List<InventarioMensual> list=session.createQuery("from InventarioMensual i  " +						
						//" where i.clave=? " +
						"order by i.clave,i.year,i.mes ")
						//.setString(0, "CAP100140")						
						.list();				
				
				for(int index=0;index<list.size()-1;index++){					
					InventarioMensual actual=list.get(index);
					
					InventarioMensual posterior=list.get(index+1);
					if(!actual.getClave().equals(posterior.getClave()))
						continue;
					if( (actual.getSaldo().doubleValue()!=0 ||
							actual.getVentas().doubleValue()!=0) &&
								actual.getCostoPromedio().amount().doubleValue()==0){
						if(posterior.getCostoCxp().amount().doubleValue()!=0){
							System.out.println("Requiere ajuste.. "+actual+ "Puede usar: "+posterior.getCostoPromedio());
							System.out.println("Saldo: "+actual.getSaldo()+"Promedio a usar: "+posterior.getCostoPromedio());
							
							actual.setCostoPromedio(posterior.getCostoPromedio());
							actual.setCosto(actual.getCostoPromedio().multiply(actual.getSaldo()));
							//actual.actualizar();
							posterior.setCostoInicial(actual.getCosto());
							
							//session.update(actual);
							System.out.println("Res: "+actual.getCosto()+" CP: "+actual.getCostoPromedio());
						}
						
					}
					
				}
				return null;
			}			
		});
	}
	
	/**
	 * Imprime un mensaje para beans InventarioMensual con costo promedio == 0
	 *
	 */
	public void analizadasCXPSinCosto(){
		getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				List<InventarioMensual> list=session.createQuery("from InventarioMensual i  " +						
						" order by i.clave,i.year,i.mes")						
						.list();
				for(InventarioMensual im:list){
					if(im.getCostoCxp().abs().getAmount().doubleValue()!=0){
						if(im.getCostoPromedio().amount().abs().doubleValue()==0){
							System.out.println("Problema :"+im);
						}
					}
				}
				return null;
			}			
		});
	}
	
	/**
	 * Verifica que el una vez que exista el costo promedio este se mantenga
	 * si en meses posteriores no existiera costo
	 *
	 */
	public void fordwarCosto(){
		getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				List<InventarioMensual> list=session.createQuery("from InventarioMensual i  " +
						//" where i.clave in (?)" +						
						" order by i.clave,i.year,i.mes")
						//.setString(0, "SBS210212")
						//.setString(1, "CAP10214")
						//.setString(2, "CAP635214")
						//.setString(3, "CAP487320")
						
						.list();
				
				String currentClave=null;
				CantidadMonetaria currentCostoP=null;
				
				for(InventarioMensual im:list){					
					if(currentClave==null ||!currentClave.equals(im.getClave())){
						//System.out.println("Procesando :"+currentClave);
						currentClave=im.getClave();
						currentCostoP=im.getCostoPromedio();
						continue;
					}
					if(currentCostoP.amount().abs().doubleValue()==0){
						currentCostoP=im.getCostoPromedio();
						continue;
					}
					if(im.getClave().equals(currentClave) &&
							currentCostoP.abs().amount().doubleValue()!=0){
						
						if(im.getCostoPromedio().abs().amount().doubleValue()==0){
							System.out.println("Ajustando..."+im.getClave());
							im.setCostoPromedio(currentCostoP);
							im.actualizarCostos();
						}
						
					}
					
				}
				return null;
			}			
		});
	}
	
	/**
	 * Actualiza el CP tomando el valor del catalogo de articulos.
	 * Se aplica:
	 * 
	 * 	 El costo promedio ==0
	 * 	 Existan movimientos en el periodo (FACS)
	 * 	 El saldo/costo inicial ==0
	 * 
	 */
	public void actualizaElCostoPromedioUsandoElCatalogoDeArticulos(){
		getHibernateTemplate().execute(new HibernateCallback(){
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				
				List<InventarioMensual> list=session.createQuery("from InventarioMensual i  " +
						" where i.year=2008" +						
						" order by i.clave,i.year,i.mes")						
						.list();				
				
				for(int index=0;index<list.size()-1;index++){					
					InventarioMensual im=list.get(index);
					if(im.getCostoPromedio().amount().doubleValue()==0){
						if(im.getMovimientos().doubleValue()!=0){
							if(im.getInicial().doubleValue()==0){
								CantidadMonetaria costoP=CantidadMonetaria.pesos(im.getArticulo().getCostoP().doubleValue());
								im.setCostoPromedio(costoP);
								im.actualizarCostos();
								System.out.println("Fix: "+im);
							}
						}
						
					}
					
				}
				return null;
			}			
		});
	}
	
	
	public static void main(String[] args) {
		InentarioMensual_Parche1 test=new InentarioMensual_Parche1();
		test.setSessionFactory(ServiceLocator.getSessionFactory());
		//test.verificarCostoInicialContraFinal();
		
		//EJECUCION DE PARCHES
		//test.execute2();
		test.fordwarCosto();
		//test.actualizaElCostoPromedioUsandoElCatalogoDeArticulos();
		
	}

}
