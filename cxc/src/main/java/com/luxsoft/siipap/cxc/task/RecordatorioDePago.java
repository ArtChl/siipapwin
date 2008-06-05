package com.luxsoft.siipap.cxc.task;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.Assert;

import com.luxsoft.siipap.cxc.domain.ClienteCredito;
import com.luxsoft.siipap.cxc.swing.cobranza.DatosDePrueba;
import com.luxsoft.siipap.ventas.domain.Venta;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class RecordatorioDePago {
	
	
	public void execute(final List<Venta> ventas) throws MessagingException{
		
		Assert.notEmpty(ventas,"La seleccion de ventas no puede estar vacía");
		
		final JavaMailSenderImpl sender=new JavaMailSenderImpl();
		sender.setHost("smtp.papelsa.com.mx");
		sender.setUsername("siipap_win@papelsa.com.mx");
		sender.setPassword("alamilla");
		
		final String text=crearRecordatorio(ventas);
		final ClienteCredito c=ventas.get(0).getCliente().getCredito();
		Assert.isTrue(StringUtils.isNotBlank(c.getEmail()),"El cliente no cuenta con correo electónico");
		if(confirmar(text,c)){
			final MimeMessagePreparator preparator=new MimeMessagePreparator(){
				public void prepare(MimeMessage mimeMessage) throws Exception {
					final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
					helper.setTo(c.getEmail());
					helper.setFrom("Papel S.A. Credito y cobranzas <credito@papelsa.com.mx>");
					helper.setCc("credito@papelsa.com.mx");
					helper.setSubject("Estado de cuenta");				
					helper.setText(text,true);
					final DefaultResourceLoader loader=new DefaultResourceLoader();
					helper.addInline("papelLogo", loader.getResource("images/siipapwin/papelLogo.jpg"));
				}				
			};
			System.out.println("Sending message: "+preparator);
			System.out.println(text);
			System.out.println("To: "+c.getEmail());
			sender.send(preparator);
		}		
	}
	
	private void corregirVentas(final List<Venta> ventas){
		for(Venta v:ventas){
			
		}
		
	}
	
	public static void main(String[] args) throws MessagingException {
		new RecordatorioDePago().execute(DatosDePrueba.ventasDePrueba());
	}
	
	
	@SuppressWarnings("unchecked")
	public  String crearRecordatorio(final List<Venta> ventas) {		
		
		Configuration cfg=new Configuration();
		cfg.setDateFormat("dd/MM/yyyy");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setClassForTemplateLoading(getClass(), "/");
		final String nombre=ventas.get(0).getNombre();		
		Map root=new HashMap();
		root.put("cliente", nombre);
		root.put("facturas", ventas);
		root.put("fecha", new Date());
		
		try {
			final Template temp=cfg.getTemplate("templates/recordatorio1.ftl");			
			StringWriter out=new StringWriter();
			temp.process(root, out);			
			return out.toString();
		} catch (Exception e) {
			throw new RuntimeException("Imposible cargar recordatorio",e);
		}
	}
	
	public  boolean confirmar(final String text,final ClienteCredito c){
		
		final RecordatorioPreview dialog=new RecordatorioPreview(text,c);
		dialog.open();
		if(!dialog.hasBeenCanceled()){
			return true;
		}else{
			return false;
		}
	};

}
