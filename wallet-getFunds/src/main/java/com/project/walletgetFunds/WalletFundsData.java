package com.project.walletgetFunds;

import java.math.BigDecimal;


public class WalletFundsData {
		
    private String accountId;
    private String username;
    private String creditDate;
    private String creditTime;
    private String amount;
    private String total;
    
    public WalletFundsData() {}
    
    public WalletFundsData(String accountId, String amount, 
    		String username, String creditDate, String creditTime, String total) {
    	this.accountId = accountId;
    	this.amount = amount;
    	this.username = username;
    	this.creditDate = creditDate;
    	this.creditTime = creditTime;
    	this.total = total;
    }

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}


	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreditDate() {
		return creditDate;
	}

	public void setCreditDate(String creditDate) {
		this.creditDate = creditDate;
	}

	public String getCreditTime() {
		return creditTime;
	}

	public void setCreditTime(String creditTime) {
		this.creditTime = creditTime;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	
	

}
