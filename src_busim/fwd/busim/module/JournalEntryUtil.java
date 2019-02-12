package fwd.busim.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fwd.busim.entity.Account;
import fwd.busim.entity.AccountTransaction;
import fwd.busim.entity.Company;
import fwd.busim.entity.JournalEntry;
import fwd.busim.entity.JournalEntryItem;
import lebah.template.DbPersistence;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class JournalEntryUtil {
	
	DbPersistence db = new DbPersistence();
	Company company;
	
	public JournalEntryUtil(Company company) {
		this.company = company;
	}
	
	public void addTransaction(JournalEntry journal, String itemDescription, List<Map> transactions, Date date) throws Exception {
		
		addTransaction(journal, null, transactions, date);
		
	}
	
	public void addTransaction(JournalEntry journal, JournalEntryItem item, String itemDescription, List<Map> transactions, Date date) throws Exception {
				
		boolean add = false;
		if ( item == null ) {
			add = true;
			item = new JournalEntryItem();
			item.setJournalEntry(journal);
		} else {
			
		}
		if ( add ) {
			db.begin();
			journal.getItems().add(item);
			db.commit();
		}
		
		//prepare to add transaction
		if ( !add ) {
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
		//
		
		db.begin();
		
		item.setDate(date);
		item.setDescription(itemDescription);
		
		int i=0;
		for ( Map transaction : transactions ) {
			i++;

			Account account = (Account) transaction.get("account");
			String amountDebit = (String) transaction.get("amountDebit");
			String amountCredit = (String) transaction.get("amountCredit");
			amountDebit = amountDebit.replaceAll(",", "");
			amountCredit = amountCredit.replaceAll(",", "");

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
		
		
		item.setJournalEntry(journal);
		
		db.commit();
		
		
		
	}

}
