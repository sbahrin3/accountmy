package lebah.app;

import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import lebah.template.DbPersistence;
import lebah.util.PasswordService;
import fwd.busim.entity.User;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class Main extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "app";
	
	public void preProcess() {
		System.out.println("main >> " + command);
		context.remove("loginError");
		context.remove("signUpError");
	}

	@Override
	public String start() {
		User user = (User) request.getSession().getAttribute("__user");
		if ( user != null ) 
			context.put("user", user);
		else
			context.remove("user");
		return path + "/start.vm";
	} 
	
	@Command("signUpForm")
	public String signUpForm() throws Exception {
		return path + "/form-signUp.vm";
	}
	
	@Command("loginForm")
	public String loginForm() throws Exception {
		return path + "/form-login.vm";
	}	
	
	@Command("loginSubmit")
	public String loginSubmit() throws Exception {
		/*
		try {
			Thread.sleep(1000);
		} catch ( Exception e ) {}
		*/
		
		if ( "".equals(getParam("login")) || "".equals(getParam("password")) ) {
			context.put("loginError", "Login/Password is required.");
			return path + "/form-login.vm";
		}
		
		String login = getParam("login");
		String password = PasswordService.encrypt(getParam("password"));
		
		User user = (User) db.get("select u from User u where u.login = '" + login + "'");
		if ( user == null ) {
			context.put("loginError", "Login not found. Please try again.");
			return path + "/form-login.vm";
		}
		if ( !user.getPassword().equals(password)) {
			context.put("loginError", "Wrong password. Please try again.");
			return path + "/form-login.vm";
		}
		
		context.put("user", user);
		request.getSession().setAttribute("__user", user);
		
		return path + "/loginSubmit.vm";
	}
	
	@Command("getTopMenu")
	public String getTopMenu() throws Exception {
		User user = (User) request.getSession().getAttribute("__user");
		if ( user != null ) 
			context.put("user", user);
		else
			context.remove("user");
		
		return path + "/top-menu.vm";
	}
	
	@Command("logout")
	public String logout() throws Exception {
		request.getSession().invalidate();
		context.remove("user");
		return path + "/start.vm";
	}
	
	@Command("signUp")
	public String signUp() throws Exception {
		
		String fullName = getParam("fullName");
		String login = getParam("login");
		String password = getParam("password");
		String confirmPassword = getParam("confirmPassword");
		
		if ( !password.equals(confirmPassword)) {
			context.put("signUpError", "Password does not match.");
			return path + "/form-signUp.vm";
		}
		
		User user = null;
		user = db.find(User.class, login);
		if ( user != null ) {
			context.put("signUpError", "Login already exist.");
			return path + "/form-signUp.vm";
		}
		user = new User();
		user.setId(login);
		user.setLogin(login);
		user.setPassword(PasswordService.encrypt(password));
		user.setName(fullName);
		
		db.begin();
		db.persist(user);
		db.commit();
		
		return path + "/signUpSuccess.vm";
	}

}
