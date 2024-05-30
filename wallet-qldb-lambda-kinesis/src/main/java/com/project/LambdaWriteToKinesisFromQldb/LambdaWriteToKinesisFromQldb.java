package com.project.LambdaWriteToKinesisFromQldb;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.services.qldbsession.QldbSessionClient;
import software.amazon.awssdk.services.qldbsession.model.QldbSessionException;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.Result;
import software.amazon.qldb.RetryPolicy;

import com.amazon.ion.IonStruct;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class LambdaWriteToKinesisFromQldb implements RequestHandler<Object, List<String>> {

    private static final String STREAM_NAME = "brpl-kinesis-data-stream";
    
    @Autowired
    private QldbDriver qldbDriver;
    
    
    public LambdaWriteToKinesisFromQldb() {
        String ledgerName = "ledger-database";

        // Create and return QldbSession
        qldbDriver = QldbDriver.builder()
				.ledger(ledgerName)
				.transactionRetryPolicy(RetryPolicy
						.builder()
						.maxRetries(3)
						.build())
				.sessionClientBuilder(QldbSessionClient.builder())
				.build();
    }

    @Override
    public List<String> handleRequest(Object input, Context context) {
        List<String> result = new ArrayList<>();
        try {
        	qldbDriver.execute(txn -> {
                // Your QLDB query to retrieve data goes here
                // For example:
                Result qldbData = txn.execute("SELECT * FROM AddFunds");
                
                // Replace this with actual QLDB query results
//                List<String> qldbData = new ArrayList<>();
                
                // Push each record to Kinesis
                qldbData.forEach(record ->  {
                	IonStruct data = (IonStruct) record;
                    pushRecordToKinesis(data.toString());
                    result.add("Pushed record to Kinesis: " + record);
                });
            });
        } catch (QldbSessionException e) {
            // Handle exceptions
            result.add("Error: " + e.getMessage());
        }
        return result;
    }


    private void pushRecordToKinesis(String record) {
        AmazonKinesis kinesisClient = AmazonKinesisClientBuilder.defaultClient();
        ByteBuffer data = ByteBuffer.wrap(record.getBytes());

        PutRecordRequest putRecordRequest = new PutRecordRequest()
                .withStreamName(STREAM_NAME) // Replace with your partition key
                .withPartitionKey("partitionKey")
                .withData(data);

        PutRecordResult putRecordResult = kinesisClient.putRecord(putRecordRequest);
    }
}
