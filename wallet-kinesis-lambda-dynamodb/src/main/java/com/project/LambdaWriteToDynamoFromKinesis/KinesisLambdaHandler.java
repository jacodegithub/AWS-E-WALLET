//package com.project.LambdaWriteToDynamoFromKinesis;
//
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
//
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KinesisLambdaHandler implements RequestHandler<KinesisEvent, Void> {
//
//    @Autowired
//    private DynamoDbService dynamoDbService;
//    
//    private static final String TABLE_NAME = "Brpl-DynamoDB";
//    
//
//    @Override
//    public Void handleRequest(KinesisEvent event, Context context) {
//    	
//    	
//    	
//        for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
//        	
//        	
//        	
//            String payload = new String(record.getKinesis().getData().array(), StandardCharsets.UTF_8);
//            Map<String, AttributeValue> item = null;
//			try {
//				item = parsePayload(payload);
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//            dynamoDbService.putItem(TABLE_NAME, item);
//        }
//
//        return null;
//    }
//
//    private Map<String, AttributeValue> parsePayload(String payload) throws JsonMappingException, JsonProcessingException {
//        // Parse your payload into a Map<String, AttributeValue>
//        // This will depend on the structure of your payload
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> rawMap = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
//        Map<String, AttributeValue> attributeValueMap = new HashMap<>();
//
//        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
//            Object value = entry.getValue();
//            AttributeValue attributeValue;
//
//            if (value instanceof String) {
//                attributeValue = AttributeValue.builder().s((String) value).build();
//            } else if (value instanceof Number) {
//                attributeValue = AttributeValue.builder().n(value.toString()).build();
//            } else if (value instanceof Boolean) {
//                attributeValue = AttributeValue.builder().bool((Boolean) value).build();
//            } else {
//                // Add more type conversions as needed
//                attributeValue = AttributeValue.builder().s(value.toString()).build();
//            }
//
//            attributeValueMap.put(entry.getKey(), attributeValue);
//        }
//
//        return attributeValueMap;
//    }
//}
//
