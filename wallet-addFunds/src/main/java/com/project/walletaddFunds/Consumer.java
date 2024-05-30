package com.project.walletaddFunds;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.ShardIteratorType;

@Component
public class Consumer {

    @Autowired
    private AmazonKinesis kinesis;
    
    private GetShardIteratorResult shardIterator;

    public void consumeWithKinesis() {
        GetRecordsRequest recordsRequest = new GetRecordsRequest();
        recordsRequest.setShardIterator(shardIterator.getShardIterator());
        recordsRequest.setLimit(25);

        GetRecordsResult recordsResult = kinesis.getRecords(recordsRequest);
        while (!recordsResult.getRecords().isEmpty()) {
            recordsResult.getRecords()
                .stream()
                .map(record -> new String(record.getData()
                    .array()))
                .forEach(System.out::println);

            recordsRequest.setShardIterator(recordsResult.getNextShardIterator());
            recordsResult = kinesis.getRecords(recordsRequest);
        }
    }

    @PostConstruct
    private void buildShardIterator() {
        GetShardIteratorRequest readShardsRequest = new GetShardIteratorRequest();
        readShardsRequest.setStreamName("brpl-kinesis-data-stream");
        readShardsRequest.setShardIteratorType(ShardIteratorType.LATEST);

        this.shardIterator = kinesis.getShardIterator(readShardsRequest);
    }
}
