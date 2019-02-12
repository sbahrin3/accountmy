package fwd.busim.module;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import fwd.busim.entity.AccountTransaction;
import fwd.busim.entity.Company;
import lebah.portal.action.Command;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class IncomeStatementModule extends BusimAppModule {
	
	private String dir = path + "/incomeStatement";
	
	@Override
	public String start() {  
		Company company = db.find(Company.class, "1");
		context.put("company", company);
		
		selectParams();
		
		return dir + "/start.vm";
	}
	
	private void selectParams() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		context.put("selectYear", !"".equals(getParam("selectYear")) ? Integer.parseInt(getParam("selectYear")) : calendar.get(Calendar.YEAR));
		
		int year1 = calendar.get(Calendar.YEAR) - 10;
		context.put("year1", year1);
		int year2 = calendar.get(Calendar.YEAR);
		context.put("year2", year2);
		
		Date today = new Date();
		String year = new SimpleDateFormat("yyyy").format(today);
		context.put("currentYear", year);
	}
	
	@Command("getIncomeStatement")
	public String getIncomeStatement() throws Exception {
		//RV, GA, EX,  LO
		String sql = "";
		String year = getParam("year");
		context.put("year", year);
		
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Date date1 = df.parse("01-01-" + year);
		Date date2 = df.parse("31-12-" + year);
		Hashtable h = new Hashtable();
		h.put("date1", date1);
		h.put("date2", date2);

		
		sql = "select t from JournalEntry j Join j.items i Join i.transactions t where 1 = 1 ";
		sql += "and (i.date between :date1 and :date2) ";
		sql += "and (t.account.type.id = 'RV' or t.account.type.id = 'GA') ";
		sql += "order by i.date, i.id, t.sequence";
		
		List<AccountTransaction> revenues = db.list(sql, h);
		context.put("revenues", revenues);
		
		double totalRevenues = 0.0d;
		for ( AccountTransaction t : revenues ) {
			double amount = Util.decimal(t.getAmount());
			totalRevenues += amount;
		}
		context.put("totalRevenues", totalRevenues);
		
		sql = "select t from JournalEntry j Join j.items i Join i.transactions t where 1 = 1 ";
		sql += "and (i.date between :date1 and :date2) ";
		sql += "and (t.account.type.id = 'EX' or t.account.type.id = 'LO') ";
		sql += "order by i.date, i.id, t.sequence";
		
		List<AccountTransaction> expenses = db.list(sql, h);
		context.put("expenses", expenses);
		
		double totalExpenses = 0.0d;
		for ( AccountTransaction t : expenses ) {
			double amount = Util.decimal(t.getAmount());
			totalExpenses += amount;
		}
		context.put("totalExpenses", totalExpenses);
		
		double totalBalance = totalRevenues - totalExpenses;
		context.put("totalBalance", totalBalance);
		
		return dir + "/incomeStatement.vm";
	}

}
