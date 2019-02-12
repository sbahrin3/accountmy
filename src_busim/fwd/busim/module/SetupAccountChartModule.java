package fwd.busim.module;

import java.util.List;

import lebah.portal.action.Command;
import lebah.template.DbPersistence;
import fwd.busim.entity.Account;
import fwd.busim.entity.AccountType;
import fwd.busim.entity.Company;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class SetupAccountChartModule extends BusimAppModule {
	
	private String dir = path + "/setupAccountChart";
	
	public void preProcess() {
		super.preProcess();
		context.remove("deleteMessage");
		context.remove("dataSaved");
	}
	
	
	@Override
	public String start() { 
		
		try {
			InitAccountTypeData.create(db);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Company company = db.find(Company.class, "1");
		context.put("company", company);
		//list of account charts
		List<Account> accounts = db.list("select c from Account c where c.company.id = '" + company.getId() + "' and c.parent is null order by c.number");
		context.put("accounts", accounts);
		
		return dir + "/start.vm";
	}
	
	@Command("list")
	public String list() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		//list of account charts
		List<Account> accounts = db.list("select c from Account c where c.company.id = '" + company.getId() + "' and c.parent is null order by c.number");
		context.put("accounts", accounts);
		return dir + "/accounts.vm";
	}
	
	@Command("edit")
	public String edit() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String accountId = getParam("accountId");
		if ( !"".equals(accountId)) {
			Account account = db.find(Account.class, accountId);
			context.put("account", account);
		}
		else {
			context.remove("account");
			String parentId = getParam("parentId");
			if ( !"".equals(parentId)) {
				Account parent = db.find(Account.class, parentId);
				context.put("parent", parent);
			}
			else {
				context.remove("parent");	
			}
		}
			
		List<AccountType> accountTypes = db.list("select t from AccountType t order by t.sequence");
		System.out.println("size = " + accountTypes.size());
		context.put("accountTypes", accountTypes);
		
		return dir + "/accountChart.vm";
	}
	
	@Command("save")
	public String save() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
				
		AccountType accountType = null;
		Account parent = null;
		String parentId = getParam("parentId");
		if ( !"".equals(parentId)) {
			parent = db.find(Account.class, parentId);
			context.put("parent", parent);
			accountType = parent.getType();
		} else {
			context.remove("parent");
			String accountTypeId = getParam("accountTypeId");
			accountType = db.find(AccountType.class, accountTypeId);
		}
		
		int acctnum = accountType.getNumber();
		
		String accountId = getParam("accountId");
		Account account = null;
		if ( !"".equals(accountId)) {
			account = db.find(Account.class, accountId);
		}
		else {
			account = new Account();
			if ( parent != null ) {
				parent.getChilds().add(account);
				account.setParent(parent);
				int level = parent.getLevel() + 1;
				account.setLevel(level);
			}
		}
		
		account.setCompany(company);
		account.setType(accountType);
		account.setTitle(getParam("title"));
		account.setDescription(getParam("description"));
		int number = Integer.parseInt(getParam("number"));

		//acctnum = acctnum + number;
		account.setNumber(number);

		db.begin();
		if ( "".equals(accountId)) db.persist(account);
		db.commit();
		
		context.put("account", account);
		context.put("accountTypes", db.list("select t from AccountType t order by t.number"));
		context.put("dataSaved", true);
		
		return dir + "/accountChart.vm";
	}
	
	@Command("delete")
	public String delete() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String accountId = getParam("accountId");
		if ( db.list("select t from AccountTransaction t where t.account.id = '" + accountId + "'").size() == 0 ) {
			Account account = db.find(Account.class, accountId);
			Account parent = account.getParent();
			
			db.begin();
			if ( parent != null ) {
				parent.getChilds().remove(account);
			}
			db.remove(account);
			db.commit();
		} else {
			context.put("deleteMessage", "Can't delete!");
		}
		
		//list of account charts
		List<Account> accounts = db.list("select c from Account c where c.company.id = '" + company.getId() + "' and c.parent is null order by c.number");
		context.put("accounts", accounts);
		
		return dir + "/accounts.vm";
	}
	
	@Command("addSubAccount")
	public String addSubAccount() throws Exception {
		
		return dir + "/accounts.vm";
	}


}
