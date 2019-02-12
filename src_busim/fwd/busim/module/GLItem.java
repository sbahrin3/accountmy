package fwd.busim.module;

import java.util.Date;

import fwd.busim.entity.AccountTransaction;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalib
 * @version 1
 */
public class GLItem {
	
	private Date date;
	private String code;
	private String description;
	private double amount;
	private int side;
	private double balance;
	private AccountTransaction t;
	
	public GLItem() {
		
	}
	
	
	public GLItem(Date date, String description, int side, double amount, double balance) {
		this.date = date;
		this.description = description;
		this.side = side;
		this.amount = amount;
		this.balance = balance;
		this.t = null;
	}
	
	public GLItem(String code, Date date, String description, int side, double amount, double balance) {
		this.code = code;
		this.date = date;
		this.description = description;
		this.side = side;
		this.amount = amount;
		this.balance = balance;
		this.t = null;
	}
	
	
	public GLItem(AccountTransaction t, double balance) {
		this.t = t;
		this.date = t.getEntryItem().getDate();
		this.description = t.getEntryItem().getDescription();
		this.side = t.getSide();
		this.amount = t.getAmount();
		this.balance = balance;		
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public AccountTransaction getTransaction() {
		return t;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}
	
	

}
