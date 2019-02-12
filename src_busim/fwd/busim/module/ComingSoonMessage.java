package fwd.busim.module;

import lebah.template.DbPersistence;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class ComingSoonMessage extends BusimAppModule {
	
	private String dir = path + "/comingSoon";
	
	@Override
	public String start() {  
		return dir + "/start.vm";
	}

}
