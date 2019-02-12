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
public class BalanceSheetModule extends BusimAppModule {
	
	private String dir = path + "/balanceSheet";
	
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
	
	@Command("getBalanceSheet")
	public String getBalanceSheet() throws Exception {
		//AT, LB, EQ
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
		sql += "and (t.account.type.id = 'AT') ";
		sql += "order by i.date, i.id, t.sequence";
		
		List<AccountTransaction> assets = db.list(sql, h);
		context.put("assets", assets);
		
		double totalAssets = 0.0d;
		for ( AccountTransaction t : assets ) {
			double amount = Util.decimal(t.getAmount());
			totalAssets += amount;
		}
		context.put("totalAssets", totalAssets);
		
		sql = "select t from JournalEntry j Join j.items i Join i.transactions t where 1 = 1 ";
		sql += "and (i.date between :date1 and :date2) ";
		sql += "and (t.account.type.id = 'EQ' or t.account.type.id = 'LB') ";
		sql += "order by i.date, i.id, t.sequence";
		
		List<AccountTransaction> liabilities = db.list(sql, h);
		context.put("liabilities", liabilities);
		
		double totalLiabilities = 0.0d;
		for ( AccountTransaction t : liabilities ) {
			double amount = Util.decimal(t.getAmount());
			totalLiabilities += amount;
		}
		context.put("totalLiabilities", totalLiabilities);
		
		double totalBalance = totalAssets - totalLiabilities;
		context.put("totalBalance", totalBalance);
		
		return dir + "/balanceSheet.vm";
	}

}
