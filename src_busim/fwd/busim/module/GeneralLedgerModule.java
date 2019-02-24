package fwd.busim.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import fwd.busim.entity.Account;
import fwd.busim.entity.AccountTransaction;
import fwd.busim.entity.Company;
import fwd.busim.entity.JournalEntryItem;
import lebah.portal.action.Command;

public class GeneralLedgerModule  extends BusimAppModule {
	
	private String dir = path + "/generalLedger";
	
	@Override
	public String start() {  
		Company company = db.find(Company.class, "1");
		context.put("company", company);
		
		selectParams();
		
		List<Account> accounts = db.list("select c from Account c where c.company.id = '" + company.getId() + "' order by c.number");
		context.put("accounts", accounts);
		
		return dir + "/start.vm";
	}


	private void selectParams() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		context.put("selectYear", !"".equals(getParam("selectYear")) ? Integer.parseInt(getParam("selectYear")) : calendar.get(Calendar.YEAR));
		context.put("selectMonth", !"".equals(getParam("selectMonth")) ? Integer.parseInt(getParam("selectMonth")) : calendar.get(Calendar.MONTH));
		
		int year1 = calendar.get(Calendar.YEAR) - 10;
		context.put("year1", year1);
		int year2 = calendar.get(Calendar.YEAR);
		context.put("year2", year2);
	}
	
	@Command("list")
	public String list() throws Exception {
		Company company = db.find(Company.class, "1");
		context.put("company", company);
		
		context.put("selectAccountId", getParam("selectAccountId"));
		
		selectParams();
		
		List<Account> accounts = db.list("select c from Account c where c.company.id = '" + company.getId() + "' and order by c.number");
		context.put("accounts", accounts);
		
		return dir + "/accounts.vm";
	}
	
	@Command("back")
	public String back() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		return dir + "/accounts.vm";
	}
	
	@Command("getAccount")
	public String getAccount() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		int year = Integer.parseInt(getParam("year"));
		context.put("year", year);
		int day1 = 1;
		int month1 = Integer.parseInt(getParam("month"));
		context.put("month", month1);
		int month2 = month1;
		
		if ( month1 == 99 ) {
			month1 = 0;
			month2 = 11;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month1);
		calendar.set(Calendar.DAY_OF_MONTH, day1);
		
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(calendar.getTime());
		calendar1.add(Calendar.DAY_OF_MONTH, -1);
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(Calendar.YEAR, year);
		calendar2.set(Calendar.MONTH, month2);
		int day2 = calendar2.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar2.set(Calendar.DAY_OF_MONTH, day2);
		calendar2.add(Calendar.DAY_OF_MONTH, 1);
		
		
		
		Hashtable h2 = new Hashtable();
		h2.put("date1", calendar1.getTime());
		h2.put("date2", calendar2.getTime());
		
		
		
		//list all accounts involved in journal entry
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		context.put("accountList", accountList);
		System.out.println("List of Account:");
		List<Account> accounts = db.list("select distinct a from JournalEntryItem i Join i.transactions t Join t.account a where i.date >= :date1 and i.date <= :date2", h2);
		for ( Account acc : accounts ) {
			System.out.println(acc.getNumber() + " - " + acc.getTitle());
			int accountNumber = acc.getNumber();

			Map<String, Object> map = getGLReport(calendar, h2, accountNumber);
			map.put("account", acc);
			accountList.add(map);
			
		}
		
		
		System.out.println("====");
		
		
		
		return dir + "/getAccount.vm";
	}


	private Map<String, Object>  getGLReport(Calendar calendar, Hashtable params, int accountNumber) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		//balance brought forward
		boolean includeBfw = getParam("includeBfw").equals("yes");
		
		String sql = "";
		double fwdBalance = 0;
		if ( includeBfw ) {
			
			Hashtable h = new Hashtable();
			h.put("date", calendar.getTime());
			
			//calculate previous debits total
			double debit = 0.0d;
			sql = "select sum(t.amount) from JournalEntry j Join j.items i Join i.transactions t where 1 = 1 ";
			sql += "and t.account.number = " + accountNumber + " ";
			sql += "and t.side = 0 and i.date < :date";
			
			List<Double> debits = db.list(sql, h);
			if ( debits != null && debits.size() > 0 ) debit = debits.get(0) != null ? debits.get(0) : 0.0d;
			
			//calculate previos credits total
			double credit = 0.0d;
			sql = "select sum(t.amount) from JournalEntry j Join j.items i Join i.transactions t where 1 = 1 ";
			sql += "and t.account.number = " + accountNumber + " ";
			sql += "and t.side = 1 and i.date < :date";
			List<Double> credits = db.list(sql, h);
			if (credits != null && credits.size() > 0 ) credit = credits.get(0) != null ? credits.get(0) : 0.0d;	
			
			//calculate balance brought forward
			fwdBalance = debit - credit;
		
		}
		
		//list selected journal entries 
		sql = "select t from JournalEntry j Join j.items i Join i.transactions t where 1 = 1 ";
		sql += "and t.account.number = " + accountNumber + " ";
		sql += "and (i.date > :date1 and i.date < :date2) ";
		sql += "order by i.date, i.id, t.sequence";
		
		List<AccountTransaction> transactions = db.list(sql, params);
		//map.put("transactions", transactions);
		
		List<GLItem> items = new ArrayList<GLItem>();
		map.put("items", items);
		
		double balance = 0.0;
		double totalDebit = 0.0;
		double totalCredit = 0.0;
		
		balance = fwdBalance;
		if ( fwdBalance != 0.0d ) {
			
			items.add(new GLItem(calendar.getTime(), "Balance brought forward.", fwdBalance > 0 ? 0 : 1, fwdBalance > 0 ? balance : -1*balance, balance));
			
			totalDebit = fwdBalance > 0 ? fwdBalance : 0;
			totalCredit = fwdBalance < 0 ? -1*fwdBalance : 0;
		}
		
		for ( AccountTransaction t : transactions ) {
			if ( t.getSide() == 0 ) {
				//balance = t.getAmount() - balance;
				balance = balance + t.getAmount();
				totalDebit += t.getAmount();
			}
			else if ( t.getSide() == 1 ) {
				//balance = balance - t.getAmount();
				balance = balance - t.getAmount();
				totalCredit += t.getAmount();
			}
			items.add(new GLItem(t, balance));
		}
		
		double total = totalDebit - totalCredit;
		map.put("totalDebit", totalDebit);
		map.put("totalCredit", totalCredit);
		map.put("total", total);
		
		return map;
	}
	
	@Command("getTransaction")
	public String getTransaction() throws Exception {
		String itemId = getParam("itemId");
		JournalEntryItem item = db.find(JournalEntryItem.class, itemId);
		context.put("item", item);
		
		String transactionId = getParam("transactionId");
		AccountTransaction t = db.find(AccountTransaction.class, transactionId);
		context.put("transaction", t);
		
		return dir + "/getTransaction.vm";
	}

}
