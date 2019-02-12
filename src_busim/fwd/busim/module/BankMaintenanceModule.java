package fwd.busim.module;

import java.util.Date;
import java.util.List;

import lebah.portal.action.Command;
import lebah.template.DbPersistence;
import fwd.busim.entity.Account;
import fwd.busim.entity.Bank;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class BankMaintenanceModule extends BusimAppModule {
	
	private String dir = path + "/bank";
	
	public void preProcess() {
		super.preProcess();
		context.put("accounts", db.list("select a from Account a order by a.number"));
	}
	
	@Override
	public String start() {  
		
		List<Bank> banks = db.list("select c from Bank c order by c.name");
		context.put("banks", banks);
		return dir + "/start.vm";
	}
	
	@Command("find")
	public String find() throws Exception {
		String findName = getParam("findName");
		List<Bank> banks = db.list("select c from Bank c where  c.name LIKE '%" + findName + "%'");
		context.put("banks", banks);
		return dir + "/list.vm";
	}
	
	@Command("back")
	public String back() throws Exception {
		List<Bank> banks = db.list("select c from Bank c order by c.name");
		context.put("banks", banks);		
		return dir + "/search.vm";
	}
	
	@Command("newBank")
	public String newBank() throws Exception {
		return dir + "/bank.vm";
	}
	
	@Command("edit")
	public String edit() throws Exception {
		String bankId = getParam("bankId");
		Bank bank = db.find(Bank.class, bankId);
		context.put("bank", bank);
		return dir + "/bank.vm";
	}
	
	@Command("save")
	public String save() throws Exception {
		String bankId = getParam("bankId");
		Bank bank = null;
		if ( !"".equals(bankId)) {
			bank = db.find(Bank.class, bankId);
		}
		else {
			bank = new Bank();
		}
		
		bank.setName(getParam("name"));
		bank.setCode(getParam("code"));
		bank.setAddress1(getParam("address1"));
		bank.setAddress2(getParam("address2"));
		bank.setPostcode(getParam("postcode"));
		bank.setState(getParam("state"));
		bank.setTelephone(getParam("telephone"));
		bank.setFax(getParam("fax"));
		bank.setContactName(getParam("contactName"));
		
		Account account = db.find(Account.class, getParam("accountId"));
		
		bank.setAccount(account);
		
		db.begin();
		if ( "".equals(bankId)) {
			bank.setCreateDate(new Date());
			db.persist(bank);
		}
		db.commit();
		
		context.put("bank", bank);	
		return dir + "/bank.vm";
	}


}
