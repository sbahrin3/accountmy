package fwd.busim.module;

import java.util.List;

import fwd.busim.entity.User;
import lebah.portal.action.Command;
import lebah.template.DbPersistence;
import lebah.util.PasswordService;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class BusimUsersModule extends BusimAppModule {
	
	private DbPersistence db = new DbPersistence();
	private String dir = path + "/busimUsers";
	
	@Override
	public String start() {  
		
		try {
			
			listUsers();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dir + "/start.vm";
	}
	
	@Command("listUsers")
	public String listUsers() throws Exception {
		
		List<User> users = db.list("select u from User u order by u.name");
		context.put("users", users);
				
		return dir + "/users.vm";
	}
	
	@Command("editUser")
	public String editUser() throws Exception {
		String userId = getParam("userId");
		User user = db.find(User.class, userId);
		if ( user != null )
			context.put("user", user);
		else
			context.remove("user");
		return dir + "/user.vm";
	}
	
	@Command("saveUser")
	public String saveUser() throws Exception {
		boolean success = false;
		String userId = getParam("userId");
		if ( !"".equals(userId)) {
			
			User testUser = (User) db.get("select u from User u where u.login = '" + getParam("login") + "'");
			if ( testUser == null || testUser.getId().equals(userId)) {
				User user = db.find(User.class, userId);
				user.setLogin(getParam("login"));
				user.setName(getParam("name"));
				if ( !"".equals(getParam("password"))) user.setPassword(PasswordService.encrypt(getParam("password")));
				db.begin();
				db.commit();
				success = true;
			}
		} else {
			
			User testUser = (User) db.get("select u from User u where u.login = '" + getParam("login") + "'");
			if ( testUser == null ) {
				User user = new User();
				user.setLogin(getParam("login"));
				user.setName(getParam("name"));
				user.setPassword(PasswordService.encrypt(getParam("password")));
				db.begin();
				db.persist(user);
				db.commit();
				success = true;
			}
		}
		
		if ( success ) {
			context.remove("hasError");
			return listUsers();
		}
		else {
			context.put("hasError", true);
			return dir + "/user.vm";
		}
	}
	
	@Command("deleteUser")
	public String deleteUser() throws Exception {
		String userId = getParam("userId");
		User user = db.find(User.class, userId);
		db.begin();
		db.remove(user);
		db.commit();
		return listUsers();
	}

}
