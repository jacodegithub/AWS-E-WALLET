package com.project.walletaddFunds;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amazon.ion.IonStruct;
import com.amazon.ion.IonSystem;
import com.amazon.ion.system.IonSystemBuilder;

import software.amazon.awssdk.services.qldbsession.QldbSessionClient;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.Result;
import software.amazon.qldb.RetryPolicy;



@SpringBootApplication
public class WalletAddFundsApplication {
	
	private double total = 0;
	
    public static void main(String[] args) {
        SpringApplication.run(WalletAddFundsApplication.class, args);
    }
	
	@Autowired
    public QldbDriver qldbDriver;
	public static IonSystem ionSys = IonSystemBuilder.standard().build();
	
	private final static String LEDGER_NAME = "ledger-database";
	
	// constructor
	public WalletAddFundsApplication() {
		
		// initializing driver
		qldbDriver = QldbDriver.builder()
                .ledger(LEDGER_NAME)
                .transactionRetryPolicy(RetryPolicy
                    .builder()
                    .maxRetries(3)
                    .build())
                .sessionClientBuilder(QldbSessionClient.builder())
                .build();
	}
	
	// post method for adding funds through api
    public ResponseEntity<Double> addFunds(AddFundsRequest request) {
    	LocalTime localTime = LocalTime.now();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
    	String formattedTime = localTime.format(formatter);
    	
    	// calculating the total amount
//    	double amountToAdd = 0.0;
//        Double totalSum = request.getAmount();
//        System.out.println("Amount to add: " + request.getTotal() +" "+request.getAmount());
//        totalSum = totalSum + amountToAdd;
        
    	
//    	final double fsum = totalSum;
        try {
            qldbDriver.execute(txn -> {
            	
//                // Retrieve the previous balance from the database
//                Result previousBalanceResult = txn.execute("SELECT total FROM AddFunds WHERE accountId = ? ORDER BY creditDate DESC, creditTime DESC LIMIT 1", 
//                                                           ionSys.newString(request.getAccountId()));
                
            	// Define a formatter for the time in 12-hour clock format with AM/PM
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            	
            	// Retrieve all records for the given accountId
                Result previousBalanceResult = txn.execute(
                    "SELECT total, creditDate, creditTime FROM AddFunds AS af WHERE af.accountId = ?", 
                    Collections.singletonList(ionSys.newString(request.getAccountId()))
                );

                
                BalanceWrapper balanceWrapper = new BalanceWrapper();             

                // Iterate through the records to find the most recent total
                previousBalanceResult.forEach(record -> {
                    IonStruct recordStruct = (IonStruct) record;
                    
                    String creditDateStr = recordStruct.get("creditDate").toString();
                    String creditTimeStr = recordStruct.get("creditTime").toString();                    
                    
                    LocalDate recordDate = LocalDate.parse(creditDateStr.replaceAll("\"", ""));
                    LocalTime recordTime = LocalTime.parse(creditTimeStr.replaceAll("\"", ""), timeFormatter);
                    LocalDateTime recordDateTime = LocalDateTime.of(recordDate, recordTime);

                    if (recordDateTime.isAfter(balanceWrapper.latestDateTime)) {
                    	balanceWrapper.latestDateTime = recordDateTime;
                    	balanceWrapper.previousBalance = Double.parseDouble(recordStruct.get("total").toString());
                    }
                });
            	
//                if (previousBalanceResult.iterator().hasNext()) {
//                    IonStruct previousBalanceStruct = (IonStruct) previousBalanceResult;
//                    balanceWrapper.previousBalance = Double.parseDouble(previousBalanceStruct.get("total").toString());
//                }

                // Calculate the new total
                double newTotal = (balanceWrapper.previousBalance + request.getAmount());
            	total = newTotal;
                
                
                System.out.println("Inserting a document");
                IonStruct addFunds = ionSys.newEmptyStruct();
                addFunds.put("accountId").newString(request.getAccountId());
                addFunds.put("username").newString(request.getUsername());
                addFunds.put("creditDate").newString(LocalDate.now().toString());
                addFunds.put("creditTime").newString(formattedTime);
                addFunds.put("amount").newInt(request.getAmount());
                addFunds.put("total").newInt(newTotal);
                
                txn.execute("INSERT INTO AddFunds ?", addFunds);
                
            });
//            return "Data added successfully";
            return new ResponseEntity<Double>(total, HttpStatus.OK);
        } catch (Exception e) {
        	System.out.println("Internal error: "+e.getMessage());
            return new ResponseEntity<Double>(total, HttpStatus.OK);
        }
    }
	
 /*   	@Bean
        public QldbDriver setupDb(){       
    		
            qldbDriver = QldbDriver.builder()
                    .ledger(LEDGER_NAME)
                    .transactionRetryPolicy(RetryPolicy
                        .builder()
                        .maxRetries(3)
                        .build())
                    .sessionClientBuilder(QldbSessionClient.builder())
                    .build();
    		
    		// Insert a document
    		qldbDriver.execute(txn -> {
    		    System.out.println("Inserting a document");
    		    IonStruct addFunds = ionSys.newEmptyStruct();
    		    addFunds.put("accoundId").newString("125454");
    		    addFunds.put("creditDate").newString("10-05-2024");
    		    addFunds.put("creditTime").newString("10:16 AM");
    		    addFunds.put("amount").newInt(32000);
    		    txn.execute("INSERT INTO AddFunds ?", addFunds);
    		});
    		
            return qldbDriver;
        }
        */

}

class BalanceWrapper {
	LocalDateTime latestDateTime = LocalDateTime.MIN;
    double previousBalance = 0;
}
