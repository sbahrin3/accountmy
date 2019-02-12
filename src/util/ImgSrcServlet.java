package util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import lebah.servlets.IServlet;
import lebah.template.DbPersistence;

public abstract class ImgSrcServlet implements IServlet {
	
	protected DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		try {
			ServletOutputStream out = res.getOutputStream();
			res.setContentType("image/jpeg");
			byte[] bytes = getBinaryData(req);
			if ( bytes != null ) out.write(bytes);
			out.flush();
			out.close();			
		} catch (Exception e ) {
			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			e.printStackTrace(out);
			out.flush();
			out.close();
			e.printStackTrace();
		}		
	}

	public abstract byte[] getBinaryData(HttpServletRequest req);
}
