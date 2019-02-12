package fwd.busim.module;
import java.text.SimpleDateFormat;
import java.util.Date;

import lebah.template.DbPersistence;
import fwd.busim.entity.RefNumPrefix;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class RefNumGenerator {
	
	DbPersistence db = new DbPersistence();
	boolean isSample = false;
	private String matricPrefix = "";
	private int matricDigit = 0;
	

    static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
	

	public String createPrefix(String prefix) {
		Date date = new Date();
		if ( prefix.indexOf("{YY}") > -1 ) {
		    String yr = new SimpleDateFormat("yy").format(date);	
		    prefix = replace(prefix, "{YY}", yr);
		}
		if ( prefix.indexOf("{YYYY}") > -1 ) {
		    String yr = new SimpleDateFormat("yyyy").format(date);	
		    prefix = replace(prefix, "{YYYY}", yr);
		}
		if ( prefix.indexOf("{MM}") > -1 ) {
		    String m = new SimpleDateFormat("MM").format(date);	
		    prefix = replace(prefix, "{MM}", m);
		}
		if ( prefix.indexOf("{M}") > -1 ) {
		    String m = new SimpleDateFormat("M").format(date);	
		    prefix = replace(prefix, "{M}", m);
		}	
		return prefix;
	}
	
	public synchronized String getRefNum() throws Exception {
		//get the prefix from template
		RefNumPrefix template = (RefNumPrefix) db.find(RefNumPrefix.class, "template");
		String prefix = template.getPrefix();
		//TODO: WHAT IF PREFIX NULL?
		if ( prefix == null ) throw new Exception("TEMPLATE PREFIX NOT FOUND!");
		prefix = createPrefix(prefix);
		return getRefNum(prefix);
		
	}
	
	public synchronized String generateRefNumber(String templateId) throws Exception {
		//get the prefix from template
		RefNumPrefix template = (RefNumPrefix) db.find(RefNumPrefix.class, templateId);
		
		if ( template == null ) {
			template = new RefNumPrefix();
			template.setId(templateId);
			template.setPrefix("{YY}{MM}/");
			db.begin();
			db.persist(template);
			db.commit();
		}
		
		String prefix = template.getPrefix();
		//TODO: WHAT IF PREFIX NULL?
		if ( prefix == null ) throw new Exception("TEMPLATE PREFIX NOT FOUND!");
		prefix = createPrefix(prefix);
		return getRefNum(prefix);
	}	
	
	public synchronized String getRefNum(String pre) throws Exception {
		RefNumPrefix matric = (RefNumPrefix) db.find(RefNumPrefix.class, pre);
		if ( matric == null ) {
			db.begin();
			matric = new RefNumPrefix(pre);
			matric.setPrefix(pre);
			matric.setCounter(1);
			matric.setDigit(4);
			db.persist(matric);
			db.commit();
		}

		int count = matric.getCounter();
		int digit = matric.getDigit();
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};

		String matricNumber = matric.getPrefix() + new java.text.DecimalFormat(new String(cf, 0, digit)).format(count);

		if ( !isSample ) {
			db.begin();
			matric.setCounter(++count);
			db.commit();
		}
		
		setMatricPrefix(pre);
		setMatricDigit(digit);
		
		return matricNumber;

	}
	
	public synchronized String letterRefNo(String pre) throws Exception {
		
		if ( pre == null ) {
			return "";
		}
		
		pre = createPrefix(pre);
		
		RefNumPrefix matric = (RefNumPrefix) db.find(RefNumPrefix.class, pre);
		if ( matric == null ) {
			db.begin();
			matric = new RefNumPrefix(pre);
			matric.setPrefix(pre);
			matric.setCounter(1);
			matric.setDigit(2);
			db.persist(matric);
			db.commit();
		}

		int count = matric.getCounter();
		int digit = matric.getDigit();
		char[] cf = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',};

		//String matricNumber = matric.getPrefix() + new java.text.DecimalFormat(new String(cf, 0, digit)).format(count);
		String runningNo = new java.text.DecimalFormat(new String(cf, 0, digit)).format(count);

		if ( !isSample ) {
			db.begin();
			matric.setCounter(++count);
			db.commit();
		}
		
		setMatricPrefix(pre);
		setMatricDigit(digit);
		
		runningNo = pre.replaceAll("&", runningNo);
		return runningNo;

	}	

	public boolean isSample() {
		return isSample;
	}

	public void setSample(boolean isSample) {
		this.isSample = isSample;
	}

	public String getMatricPrefix() {
		return matricPrefix;
	}

	public void setMatricPrefix(String matricPrefix) {
		this.matricPrefix = matricPrefix;
	}

	public int getMatricDigit() {
		return matricDigit;
	}

	public void setMatricDigit(int matricDigit) {
		this.matricDigit = matricDigit;
	}
		
	public static void main(String[] args) throws Exception {
		String template = "ZZ(&/{YY})";
		String refNo = new RefNumGenerator().letterRefNo(template);
		System.out.println(refNo);
	}	

}
