/**
 * 
 */
package setup;

import java.io.IOException;
import java.io.PrintWriter;

import edb.CreateDb;
import lebah.db.Db;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class SetupDbModule extends LebahModule {

	private String path = "createdb";
	
	@Override
	public String start() {
		PrintWriter out = null;
		Db db = null;
		try {
			out = response.getWriter();
			db = new Db();
			db.getStatement().executeQuery("select * from users");
		} catch ( Exception e ) {
			try {
				CreateDb.run();
			} catch (Exception e1) {
				e1.printStackTrace(out);
			}
			
		} finally {
			if ( db != null ) db.close();
		}
		return path + "/start.vm";
	}

}
