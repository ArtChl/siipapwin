package com.luxsoft.siipap.cxc.swing.catalogos;

import com.luxsoft.siipap.cxc.domain.Abogado;
import com.luxsoft.siipap.swing.form.AbstractGenericFormModel;

public class AbogadosFormModel extends AbstractGenericFormModel<Abogado, Long>{
	
	
	public static void main(String[] args) {
		AbogadosFormModel model=new AbogadosFormModel();
		model.setValue("abosuspension", "L");
		//model.getModel("abosuspension").setValue("L" );		
		model.updateValidation();
		System.out.println(model.getValidationModel().getResult());
	}
	

}
