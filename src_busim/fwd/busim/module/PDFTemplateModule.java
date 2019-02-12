package fwd.busim.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.Template;

import lebah.portal.velocity.VTemplate;
import lebah.template.DbPersistence;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public abstract class PDFTemplateModule extends VTemplate {
	
	protected String vm = "";
	protected String path = "";
	protected DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		 
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
        
        String uri = request.getRequestURI();
        String appname = uri.substring(1);
        appname = appname.substring(0, appname.indexOf("/"));
        context.put("appUrl", serverUrl.concat("/").concat(appname));     
        
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("nf", new DecimalFormat("#,###,###.00"));
		context.put("util", new lebah.util.Util());
		Template template = engine.getTemplate(getPDFTemplate());	
		return template;		
	}


	public abstract String getPDFTemplate();

}
