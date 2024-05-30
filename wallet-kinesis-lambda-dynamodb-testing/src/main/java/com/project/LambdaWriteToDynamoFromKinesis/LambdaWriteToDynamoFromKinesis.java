package com.project.LambdaWriteToDynamoFromKinesis;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.Record;
//import software.amazon.awssdk.services.kinesis.model.Record;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
//import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LambdaWriteToDynamoFromKinesis implements RequestHandler<KinesisEvent, Void> {

    @Override
    public Void handleRequest(KinesisEvent event, Context context) {
        // Get Kinesis client
//        KinesisClient kinesisClient = getKinesisClient();

        // Iterate over Kinesis event records
        for (KinesisEventRecord record : event.getRecords()) {
            Record kinesisRecord = record.getKinesis();
            String data = kinesisRecord.getData().toString();
            System.out.println("Received data: " + data);

            // Decode the data if necessary
            byte[] decodedData = java.util.Base64.getDecoder().decode(data);
            String decodedString = new String(decodedData, StandardCharsets.UTF_8);
            System.out.println("Decoded data: " + decodedString);
        }

        // ShardId should be dynamic or retrieved from event. This is a static example
        String shardId = "shardId-000000000001";

        // Prepare the shard iterator request
        GetShardIteratorRequest getShardIteratorRequest = GetShardIteratorRequest.builder()
                .streamName("brpl-kinesis-data-stream")
                .shardId(shardId)
                .shardIteratorType(ShardIteratorType.TRIM_HORIZON)
                .build();

//        GetShardIteratorResponse getShardIteratorResponse = kinesisClient.getShardIterator(getShardIteratorRequest);
//        String shardIterator = getShardIteratorResponse.shardIterator();
//
//        // Process records from the shard
//        while (shardIterator != null) {
//            GetRecordsRequest getRecordsRequest = GetRecordsRequest.builder()
//                    .shardIterator(shardIterator)
//                    .limit(5)
//                    .build();
//
//            GetRecordsResponse getRecordsResponse = kinesisClient.getRecords(getRecordsRequest);
//            List<software.amazon.awssdk.services.kinesis.model.Record> records = getRecordsResponse.records();
//            System.out.println("Records count: " + records.size());
//
//            // Log content of each record
//            records.forEach(record -> {
//                String payload = record.data().toString();
//                System.out.println("Payload: " + payload);
//            });
//
//            shardIterator = getRecordsResponse.nextShardIterator();
//        }

        return null;
    }

//     Set up the Kinesis Client
//    private static KinesisClient getKinesisClient() {
//        return KinesisClient.builder()
//                .region(Region.US_EAST_1)
//                .build();
//    }
}
