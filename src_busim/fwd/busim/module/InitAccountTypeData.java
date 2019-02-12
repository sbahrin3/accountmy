/**
 * 
 */
package fwd.busim.module;

import java.util.List;

import lebah.template.DbPersistence;
import fwd.busim.entity.AccountType;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class InitAccountTypeData {

	public static void create(DbPersistence db) {		

		try {
			/*
		db.begin();
		db.executeUpdate("delete from AccountType t");
		db.commit();
			 */

			addAccountType(db, "Assets", "AT", 10000, 0);
			addAccountType(db, "Liabilities", "LB", 20000, 0);
			addAccountType(db, "Equity", "EQ", 30000, 0);
			addAccountType(db, "Revenue", "RV", 40000, 0);
			addAccountType(db, "Expenses", "EX", 50000, 0);
			addAccountType(db, "Gain", "GA", 60000, 0);
			addAccountType(db, "Losses", "LO", 70000, 0);

			List<AccountType> accountTypes = db.list("select t from AccountType t order by t.sequence");
			for ( AccountType t : accountTypes ) {
				System.out.println(t.getCode() + ", " + t.getName() + ", " + t.getNumber());
			}

		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	private static void addAccountType(DbPersistence db, String accountName, String code, int number, int side) throws Exception {
		if ( db.list("select t from AccountType t where t.name = '" + accountName + "'").size() == 0 ) {
			Integer sequence = (Integer) db.get("select max(t.sequence) from AccountType t");
			int seq = sequence != null ? sequence.intValue() : 0;
			seq++;
			db.begin();
			AccountType t = new AccountType();
			t.setId(code);
			t.setCode(code);
			t.setName(accountName);
			t.setSide(side);
			t.setNumber(number);
			t.setSequence(seq);
			db.persist(t);
			db.commit();
		}
	}
}
