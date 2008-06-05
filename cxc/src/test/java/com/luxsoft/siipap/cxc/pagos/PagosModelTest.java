package com.luxsoft.siipap.cxc.pagos;

import org.jmock.MockObjectTestCase;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.validation.ValidationResultModel;
import com.luxsoft.siipap.cxc.domain.Cliente;

/**
 * Pruebas para PagosModel requerido en el proceso
 * de pagos a facturas 
 * 
 * @author Ruben Cancino 
 *
 */
public class PagosModelTest extends MockObjectTestCase{
	
	private PagosModel model;
	
	/**
	 * Validar que se proporcione de forma correcta
	 * un PresentationModel para el bean PagoM
	 *
	 */
	public void testModelParaPagoM(){
		Cliente c=new Cliente();
		model=new PagosModelImpl(c,"E");
		PresentationModel pm=model.getPagoMPModel();
		assertNotNull(pm);
		
	}
	
	/**
	 * Prueba que el modelo proporcione un ValidationResultModel
	 * apropiado
	 *
	 */
	public void testValidationModel(){
		Cliente c=new Cliente();
		model=new PagosModelImpl(c,"E");
		ValidationResultModel vm=model.getValidationModel();
		assertNotNull(vm);
		
	}
	
	public void testPagosList(){
		Cliente c=new Cliente();
		model=new PagosModelImpl(c,"E");
		assertNotNull(model.getPagos());
	}

}
