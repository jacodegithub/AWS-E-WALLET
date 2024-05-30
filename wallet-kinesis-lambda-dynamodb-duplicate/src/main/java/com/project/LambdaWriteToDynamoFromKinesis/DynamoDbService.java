//package com.project.LambdaWriteToDynamoFromKinesis;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
//import software.amazon.awssdk.services.dynamodb.model.*;
//
//@Service
//public class DynamoDbService {
//
//	@Autowired
//    private final DynamoDbClient dynamoDbClient;
//
//    public DynamoDbService() {
//        this.dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
//    }
//
//    public void putItem(String tableName, Map<String, AttributeValue> item) {
//        PutItemRequest putItemRequest = PutItemRequest.builder()
//                .tableName(tableName)
//                .item(item)
//                .build();
//
//        try {
//            dynamoDbClient.putItem(putItemRequest);
//            System.out.println("Successfully inserted item into DynamoDB");
//        } catch (DynamoDbException e) {
//            System.err.println("Could not insert item into DynamoDB: " + e.getMessage());
//        }
//    }
//}
