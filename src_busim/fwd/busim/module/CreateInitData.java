package fwd.busim.module;

import lebah.template.DbPersistence;
import fwd.busim.entity.AccountType;
import fwd.busim.entity.Agent;
import fwd.busim.entity.BusinessType;
import fwd.busim.entity.CompanyType;
import fwd.busim.entity.RefNumPrefix;
import fwd.busim.entity.Term;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class CreateInitData {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		//createCompanyType(db);
		//createBusinessType(db);
		createAccountType(db);
		//createTerm(db);
		//createAgent(db);
		//createRefNum(db);
		
	}
	
	private static void createRefNum(DbPersistence db) throws Exception {
		RefNumPrefix p = new RefNumPrefix();
		p.setId("quotation");
		p.setCounter(1);
		p.setDigit(4);
		p.setPrefix("QTN/");
		db.begin();
		db.persist(p);
		db.commit();
	}

	private static void createCompanyType(DbPersistence db) throws Exception {
		
		db.begin();
		db.executeUpdate("delete from CompanyType t");
		db.commit();
		
		String[][] types = {
				{"SP","Sole Proprietor"}, 
				{"PS","Partnerships"}, 
				{"PL","Private Limited Company"}, 
				{"LC","Public Limited Company"}};
		
		db.begin();
		int i = 0;
		for ( String[] t : types ) {
			i++;
			CompanyType ty = new CompanyType();
			ty.setId(t[0]);
			ty.setCode(t[0]);
			ty.setName(t[1]);
			ty.setSequence(i);
			db.persist(ty);
		}
		db.commit();
	}
	
	
	private static void createBusinessType(DbPersistence db) throws Exception {
		
		db.begin();
		db.executeUpdate("delete from BusinessType t");
		db.commit();
	
		
		String[][] types = {{"FS","Food and Services"}, 
				{"RT","Retail"}, 
				{"WS","Wholesale"}, 
				{"MF","Manufacturing"},
				{"MK","Marketing"},
				{"IT","Information Technology Services"}};
		
		db.begin();
		int i = 0;
		for ( String[] t : types ) {
			i++;
			BusinessType ty = new BusinessType();
			ty.setId(t[0]);
			ty.setCode(t[0]);
			ty.setName(t[1]);
			ty.setSequence(i);
			db.persist(ty);
		}
		db.commit();
		
	}

	private static void createAccountType(DbPersistence db) throws Exception {
		
		db.begin();
		db.executeUpdate("delete from AccountType t");
		db.commit();
	

		/*
		 * AT,LB,EQ,RV,EX,GA,LO : Assets, Liabilities, Equity, Revenue, Expenses, Gains, Loses
		 */
		
		String[][] types = {
				{"AT","Assets", "1000"}, 
				{"LB","Liabilities", "2000"}, 
				{"EQ","Equity", "3000"}, 
				{"RV","Revenue", "4000"},
				{"EX","Expenses", "5000"},
				{"GA","Gain", "6000"},
				{"LO","Losses", "7000"}};
		
		db.begin();
		int i = 0;
		for ( String[] t : types ) {
			i++;
			AccountType ty = new AccountType();
			ty.setId(t[0]);
			ty.setCode(t[0]);
			ty.setName(t[1]);
			ty.setNumber(Integer.parseInt(t[2]));
			ty.setSequence(i);
			db.persist(ty);
		}
		db.commit();
		
	}
	
	private static void createTerm(DbPersistence db) throws Exception {
		
		db.begin();
		db.executeUpdate("delete from Term t");
		db.commit();
		
		String[][] terms = {{"1","30", "30 Days"}, 
				{"2","60", "60 Days"}, 
				{"3","120", "120 Days"}};
		
		db.begin();
		int i = 0;
		for ( String[] t : terms ) {
			i++;
			Term ty = new Term();
			ty.setId(t[0]);
			ty.setCode(t[0]);
			ty.setName(t[1]);
			db.persist(ty);
		}
		db.commit();
		
	}
	
	private static void createAgent(DbPersistence db) throws Exception {
		
		db.begin();
		db.executeUpdate("delete from Agent t");
		db.commit();
		
		String[][] terms = {{"1","A1", "Agent 1"}, 
				{"2","A2", "Agent 2"}, 
				{"3","A3", "Agent 3"}};
		
		db.begin();
		int i = 0;
		for ( String[] t : terms ) {
			i++;
			Agent ty = new Agent();
			ty.setId(t[0]);
			ty.setCode(t[0]);
			ty.setName(t[1]);
			db.persist(ty);
		}
		db.commit();
		
	}	

}
