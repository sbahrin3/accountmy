package fwd.main;

import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class App extends LebahModule {
	
	private String path = "app";

	@Override
	public String start() {

		return path + "/start.vm";
	}
	
	@Command("loginForm")
	public String loginForm() throws Exception {
		
		return path + "/form-login.vm";
	}

}
