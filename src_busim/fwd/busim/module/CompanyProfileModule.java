package fwd.busim.module;

import fwd.busim.entity.BusinessType;
import fwd.busim.entity.Company;
import fwd.busim.entity.CompanyType;
import lebah.portal.action.Command;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class CompanyProfileModule extends BusimAppModule {
	
	private String dir = path + "/companyProfile";
	
	@Override
	public String start() {  
		//--
		System.out.println("In Company Profile Module start");
		//
		context.put("companyTypes", db.list("select t from CompanyType t order by t.sequence"));
		context.put("businessTypes", db.list("select t from BusinessType t order by t.sequence"));
		
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
		
		return dir + "/start.vm";
	}
	
	@Command("undoCompany")
	public String undoCompany() throws Exception {
		Company company = db.find(Company.class, "1");
		context.put("company", company);
		
		context.put("companyTypes", db.list("select t from CompanyType t order by t.sequence"));
		context.put("businessTypes", db.list("select t from BusinessType t order by t.sequence"));

		return dir + "/company.vm";
	}
	
	@Command("saveCompany")
	public String saveCompany() throws Exception {
		
		CompanyType ct = db.find(CompanyType.class, getParam("companyType"));
		BusinessType bt = db.find(BusinessType.class, getParam("businessType"));
		
		Company company = db.find(Company.class, "1");
		db.begin();
		company.setName(getParam("name"));
		company.setRegistrationNumber(getParam("registrationNumber"));
		company.setAddress1(getParam("address1"));
		company.setAddress2(getParam("address2"));
		company.setTelephone(getParam("telephone"));
		company.setFax(getParam("fax"));
		company.setState(getParam("state"));
		company.setPostcode(getParam("postcode"));
		company.setCompanyType(ct);
		company.setBusinessType(bt);
		db.commit();
		return dir + "/companySaved.vm";
	}
	
	
}
