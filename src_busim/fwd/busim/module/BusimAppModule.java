package fwd.busim.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import fwd.busim.entity.Company;
import fwd.busim.entity.User;
import lebah.portal.action.LebahModule;
import lebah.template.DbPersistence;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class BusimAppModule extends LebahModule {
	
	protected DbPersistence db = new DbPersistence();
	protected String path = "app_busim";
	protected User user;
	protected Company company;
	
	public void preProcess() {
		user = (User) request.getSession().getAttribute("__user");
		context.put("user", user);
		
		company = db.find(Company.class, "1");
		
		context.put("df", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("nf", new DecimalFormat("#,###,###.00"));
		
		
		if ( user == null ) {
			/*
			try {
				response.sendRedirect("../expired.jsp");
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}

	}

	@Override
	public String start() {
		
		return path + "/start.vm";
	}

}
