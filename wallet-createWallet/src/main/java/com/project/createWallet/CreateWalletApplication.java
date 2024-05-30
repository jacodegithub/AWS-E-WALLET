package com.project.createWallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.amazon.ion.IonSystem;
import com.amazon.ion.system.IonSystemBuilder;

import software.amazon.awssdk.services.qldbsession.QldbSessionClient;
import software.amazon.qldb.QldbDriver;
import software.amazon.awssdk.regions.Region;
import software.amazon.qldb.RetryPolicy;

import java.io.IOException;
import java.nio.ByteBuffer;


@SpringBootApplication
public class CreateWalletApplication {

	public static void main(String[] args) {
        SpringApplication.run(CreateWalletApplication.class, args);
    }
	
	@Autowired
    public QldbDriver qldbDriver;
	
	
	private final static String LEDGER_NAME = "ledger-database";
	
    	@Bean
        public QldbDriver setupDb(){
            System.out.println("Initializing the driver");
            qldbDriver = QldbDriver.builder()
                    .ledger(LEDGER_NAME)
                    .transactionRetryPolicy(RetryPolicy
                            .builder()
                            .maxRetries(3)
                            .build())
                    .sessionClientBuilder(QldbSessionClient.builder().region(Region.of("us-east-1")))
                    .build();
            
         // Create a table and an index in the same transaction
            qldbDriver.execute(txn -> {
                System.out.println("Creating a table and an index");
                txn.execute("CREATE TABLE AddFunds");
                txn.execute("CREATE TABLE GetTransactionHistory");
                txn.execute("CREATE TABLE WithdrawFunds");
                txn.execute("CREATE TABLE GetFunds");
            });
            
            return qldbDriver;
        }
}
