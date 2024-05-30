package com.project.walletaddFunds;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;

import java.nio.ByteBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    private final AmazonKinesis kinesisClient;

    @Autowired
    public Producer(AmazonKinesis kinesisClient) {
        this.kinesisClient = kinesisClient;
    }

    public void publishToKinesis(String accountId, double amount) {
        // Construct the data to publish to Kinesis
        String data = String.format("{\"accountId\": \"%s\", \"amount\": %.2f}", accountId, amount);

        // Create a PutRecordRequest and publish the data to the Kinesis Data Stream
        PutRecordRequest putRecordRequest = new PutRecordRequest()
                .withStreamName("brpl-kinesis-data-stream")
                .withData(ByteBuffer.wrap(data.getBytes()));

        kinesisClient.putRecord(putRecordRequest);
    }
}

