package fwd.busim.module;

import fwd.busim.entity.Company;
import lebah.portal.action.LebahModule;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class WelcomeModule extends BusimAppModule {
	

	@Override
	public String start() {  
		Company company = db.find(Company.class, "1");
		try {
			if ( company == null ) {
				db.begin();
				company = new Company();
				company.setId("1");
				company.setSequence(1);
				company.setName("Company Name");
				company.setRegistrationNumber("123");
				db.persist(company);
				db.commit();
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		context.put("company", company);
		return path + "/welcome/start.vm";
	}
	

}
