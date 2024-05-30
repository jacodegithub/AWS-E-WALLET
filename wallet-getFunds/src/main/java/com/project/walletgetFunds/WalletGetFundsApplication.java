package com.project.walletgetFunds;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
public class WalletGetFundsApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(WalletGetFundsApplication.class, args);
    }
	
	@Autowired
    public QldbDriver qldbDriver;
	public static IonSystem ionSys = IonSystemBuilder.standard().build();
	
	private final static String LEDGER_NAME = "ledger-database";
	
	public WalletGetFundsApplication() {
		qldbDriver = QldbDriver.builder()
				.ledger(LEDGER_NAME)
				.transactionRetryPolicy(RetryPolicy
						.builder()
						.maxRetries(3)
						.build())
				.sessionClientBuilder(QldbSessionClient.builder())
				.build();
	}
	
    	
	// to get the data from the qldb
    public ResponseEntity<List<WalletFundsData>> getWalletFunds(){  
    	
    	List<WalletFundsData> fundsDataList = new ArrayList<>();
    	
    	qldbDriver.execute(txn -> {
            System.out.println("Querying the table");
            Result result = txn.execute("SELECT * FROM AddFunds");
            
            result.forEach(record -> {
            	IonStruct data = (IonStruct) record;
                WalletFundsData fundsData = new WalletFundsData();
                fundsData.setAccountId(data.get("accountId").toString());
                fundsData.setCreditDate(data.get("creditDate").toString());
                fundsData.setCreditTime(data.get("creditTime").toString());
                fundsData.setAmount(data.get("amount").toString());
                fundsData.setTotal(data.get("total").toString());
                fundsDataList.add(fundsData);
            });
        });

        return new ResponseEntity<>(fundsDataList, HttpStatus.OK);
    }

}
