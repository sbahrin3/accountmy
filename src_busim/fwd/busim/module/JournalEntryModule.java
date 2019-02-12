package fwd.busim.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fwd.busim.entity.Account;
import fwd.busim.entity.AccountTransaction;
import fwd.busim.entity.Company;
import fwd.busim.entity.JournalEntry;
import fwd.busim.entity.JournalEntryItem;
import lebah.portal.action.Command;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class JournalEntryModule  extends BusimAppModule {
	
	private String dir = path + "/journalEntry";
	
	public void preProcess() {
		super.preProcess();
		context.remove("deleteMessage");
		context.remove("dataSaved");
		context.remove("unbalance");
		
		System.out.println("---" + command);
	}
	
	
	@Override
	public String start() {  
		Company company = db.find(Company.class, "1");
		context.put("company", company);
		//list
		List<JournalEntry> journals = db.list("select c from JournalEntry c where c.company.id = '" + company.getId() + "' order by c.date desc");
		context.put("journals", journals);
		return dir + "/start.vm";
	}
	
	@Command("list")
	public String list() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		//list
		List<JournalEntry> journals = db.list("select c from JournalEntry c where c.company.id = '" + company.getId() + "' order by c.date desc, c.sequence desc");
		context.put("journals", journals);
		return dir + "/journals.vm";
	}
	
	@Command("edit")
	public String edit() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String journalId = getParam("journalId");
		if ( !"".equals(journalId)) {
			JournalEntry journal = db.find(JournalEntry.class, journalId);
			context.put("journal", journal);
		}
		else {
			context.remove("journal");
			context.put("date", new Date());
			
			String monthCode = new SimpleDateFormat("MM-yy").format(new Date());
			String title = new SimpleDateFormat("MMM yyyy").format(new Date());
			String description = "Journal entry for the month of " + new SimpleDateFormat("MMMM, yyyy").format(new Date());
			
			List<JournalEntry> list = db.list("select j from JournalEntry j where j.monthCode = '" + monthCode + "' order by j.sequence desc");
			int n = list.size() > 0 ? list.get(0).getSequence() + 1 : 1;
			
			String code = monthCode + "(" + n + ")";
			
			JournalEntry journal = new JournalEntry();
			journal.setCompany(company);
			journal.setCode(code);
			journal.setMonthCode(monthCode);
			journal.setTitle(title);
			journal.setDescription(description);
			journal.setDate(new Date());
			journal.setSequence(n);
			db.begin();
			db.persist(journal);
			db.commit();
			context.put("journal", journal);
			
		}
		
		context.put("accounts", db.list("select c from Account c where c.company.id = '" + company.getId() + "' order by c.number"));
		
		return dir + "/journal.vm";
	}
	
	@Command("delete")
	public String delete() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String journalId = getParam("journalId");
		
		if ( db.list("select t from JournalEntryItem t where t.journalEntry.id = '" + journalId + "'").size() == 0 ) {
			JournalEntry journal = db.find(JournalEntry.class, journalId);
			db.begin();
			db.remove(journal);
			db.commit();
		} else {
			context.put("deleteMessage", "Can't delete!");
		}
		
		//list
		List<JournalEntry> journals = db.list("select c from JournalEntry c where c.company.id = '" + company.getId() 
										+ "' order by c.date desc, c.sequence desc");
		context.put("journals", journals);
		
		return dir + "/journals.vm";
	}
	
	
	@Command("save")
	public String save() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String journalId = getParam("journalId");
		boolean dateChanged = false;
		JournalEntry journal = null;
		if ( !"".equals(journalId)) journal = db.find(JournalEntry.class, journalId);
		else {
			journal = new JournalEntry();
		}
		
		String journalDate = getParam("journalDate");
		Date date = new Date();
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(journalDate);
		} catch ( Exception e ) { }
		
		if ( journal.getDate() == null ) dateChanged = true;
		if ( !dateChanged ) {
			if ( !new SimpleDateFormat("dd-MM-yyyy").format(journal.getDate()).equals(new SimpleDateFormat("dd-MM-yyyy").format(date) )) {
				dateChanged = true;
			}
		}
		
		String code = getParam("code");
		String description = getParam("description");
		
		String monthCode = new SimpleDateFormat("MM-yy").format(date);
		description = "".equals(description) ? "Journal entry for the month of " + new SimpleDateFormat("MMMM, yyyy").format(date) : description;
		
		List<JournalEntry> list = db.list("select j from JournalEntry j where j.monthCode = '" + monthCode + "' order by j.sequence desc");
		int n = list.size() > 0 ? list.get(0).getSequence() + 1 : 1;
		code = "".equals(code) ? monthCode + "(" + n + ")" : code;
		
		if ( dateChanged ) {
			journal.setMonthCode(monthCode);
			journal.setSequence(n);
			journal.setDate(date);
		}
		journal.setCompany(company);
		journal.setCode(code);
		journal.setDescription(description);


		db.begin();
		if ( "".equals(journalId)) db.persist(journal);
		db.commit();
		
		context.put("journal", journal);
		context.put("dataSaved", true);
		
		return dir + "/journal.vm";
	}
	
	@Command("newTransaction")
	public String newTransaction() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String journalId = getParam("journalId");
		JournalEntry journal = db.find(JournalEntry.class, journalId);
		context.put("journal", journal);
		
		context.remove("item");
		
		context.put("accounts", db.list("select c from Account c where c.company.id = '" + company.getId() + "' order by c.number"));

		return dir + "/transaction.vm";
	}
	
	@Command("editTransaction")
	public String editTransaction() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String journalId = getParam("journalId");
		JournalEntry journal = db.find(JournalEntry.class, journalId);
		context.put("journal", journal);
		
		String itemId = getParam("itemId");
		JournalEntryItem item = db.find(JournalEntryItem.class, itemId);
		context.put("item", item);
		
		context.put("accounts", db.list("select c from Account c where c.company.id = '" + company.getId() + "' order by c.number"));

		return dir + "/transaction.vm";
	}
	
	@Command("addTransactions")
	public String addTransactions() throws Exception {
		
		
		
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String journalId = getParam("journalId");
		JournalEntry journal = db.find(JournalEntry.class, journalId);
		context.put("journal", journal);
		
		String itemId = getParam("itemId");
		JournalEntryItem journalEntryItem = null;
		if ( !"".equals(itemId) ) journalEntryItem = db.find(JournalEntryItem.class, itemId);

		String itemDate = getParam("itemDate");
		Date date = new Date();
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(itemDate);
		} catch ( Exception e ) { }
		
		List<Map> transactions = new ArrayList<Map>();
		for ( int i=1;i<5;i++) {
			String accountId = getParam("accountId_" + i);
			Account account = db.find(Account.class, accountId);
			String amountDebit = getParam("amountDebit_" + i).replaceAll(",", "");
			String amountCredit = getParam("amountCredit_" + i).replaceAll(",", "");
			
			Map<String, Object> transaction = new HashMap<String, Object>();
			transaction.put("account", account);
			transaction.put("amountDebit", amountDebit);
			transaction.put("amountCredit", amountCredit);
			
			transactions.add(transaction);
			
		}
		String transactionDescription = getParam("itemDescription");
		JournalEntryUtil util = new JournalEntryUtil(company);
		util.addTransaction(journal, journalEntryItem, transactionDescription, transactions, date);
		
		
		return dir + "/items.vm";
	}
	
	@Command("addTransactions2")
	public String addTransactions2() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String journalId = getParam("journalId");
		JournalEntry journal = db.find(JournalEntry.class, journalId);
		context.put("journal", journal);

		String itemDate = getParam("itemDate");
		Date date = new Date();
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(itemDate);
		} catch ( Exception e ) { }
		
		String itemId = getParam("itemId");
		
		JournalEntryItem item = null;
		if ( !"".equals(itemId) ) item = db.find(JournalEntryItem.class, itemId);
		else {
			item = new JournalEntryItem();
			item.setJournalEntry(journal);
		}
		
		item.setDate(date);
		item.setDescription(getParam("itemDescription"));
		
		if ( "".equals(itemId) ) {
			db.begin();
			journal.getItems().add(item);
			db.commit();
		}
		
		if ( !"".equals(itemId)) {
			List<AccountTransaction> list = new ArrayList<AccountTransaction>();
			for ( AccountTransaction t : item.getTransactions() ) {
				list.add(t);
			}
			db.begin();
			for ( AccountTransaction t : list ) {
				item.getTransactions().remove(t);
				db.remove(t);
			}
			db.commit();
		}
		
		db.begin();
		for ( int i=1;i<4;i++) {
			String accountId = getParam("accountId_" + i);
			Account account = db.find(Account.class, accountId);
			String amountDebit = getParam("amountDebit_" + i).replaceAll(",", "");
			String amountCredit = getParam("amountCredit_" + i).replaceAll(",", "");
			double debit = 0.0d;
			double credit = 0.0d;
			try {
				debit = Double.parseDouble(amountDebit);
			} catch ( Exception e ) { }
			try {
				credit = Double.parseDouble(amountCredit);
			} catch ( Exception e ) { }
			
			if ( !"".equals(amountDebit) && "".equals(amountCredit) ) {
				//debit
				AccountTransaction t = new AccountTransaction();
				t.setEntryItem(item);
				t.setAccount(account);
				t.setSide(0);
				t.setAmount(debit);
				t.setSequence(i);
				item.getTransactions().add(t);
			} else if ( !"".equals(amountCredit) && "".equals(amountDebit) ) {
				//credit
				AccountTransaction t = new AccountTransaction();
				t.setEntryItem(item);
				t.setAccount(account);
				t.setSide(1);
				t.setAmount(credit);
				t.setSequence(i);
				item.getTransactions().add(t);
			} else if ( !"".equals(amountDebit) && !"".equals(amountCredit)) {
				//debit & credit
				AccountTransaction t1 = new AccountTransaction();
				t1.setEntryItem(item);
				t1.setAccount(account);
				t1.setSide(0);
				t1.setAmount(debit);
				t1.setSequence(i);
				item.getTransactions().add(t1);

				AccountTransaction t2 = new AccountTransaction();
				t2.setEntryItem(item);
				t2.setAccount(account);
				t2.setSide(1);
				t2.setAmount(credit);
				t2.setSequence(i);
				item.getTransactions().add(t2);

			} else if ( "".equals(amountDebit) && "".equals(amountCredit)) {
				//no data
			}
		}
		db.commit();
		
		//context.put("accounts", db.list("select c from Account c where c.company.id = '" + company.getId() + "' order by c.number"));

		return dir + "/items.vm";
	}
	
	@Command("deleteTransaction")
	public String deleteTransaction() throws Exception {
		String companyId = getParam("companyId");
		Company company = db.find(Company.class, companyId);
		context.put("company", company);
		
		String journalId = getParam("journalId");
		JournalEntry journal = db.find(JournalEntry.class, journalId);
		context.put("journal", journal);
		
		String itemId = getParam("itemId");
		JournalEntryItem item = db.find(JournalEntryItem.class, itemId);

		db.begin();
		journal.getItems().remove(item);
		db.remove(item);
		db.commit();
		
		return dir + "/items.vm";
	}

}
