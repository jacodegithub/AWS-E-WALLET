package com.project.walletWithdrawFunds;


public class WithdrawFundsRequest {
	
    private String accountId;
    private String username;
    private double amount;
    private double total;
    
    public WithdrawFundsRequest() {}

	public String getAccountId() {
		return accountId;
	}

	public double getAmount() {
		return amount;
	}

	public String getUsername() {
		return username;
	}

	public double getTotal() {
		return total;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	
    
}
