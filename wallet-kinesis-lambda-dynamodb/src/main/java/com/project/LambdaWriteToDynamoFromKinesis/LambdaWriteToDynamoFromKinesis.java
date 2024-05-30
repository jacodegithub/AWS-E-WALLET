package com.project.LambdaWriteToDynamoFromKinesis;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import org.apache.commons.codec.binary.Base64;


@SpringBootApplication
public class LambdaWriteToDynamoFromKinesis implements RequestHandler<KinesisEvent, Void> {
	
	
	private static final String TABLE_NAME = "Brpl-DynamoDB";

	@Autowired
	private final DynamoDbClient dynamoDbClient;

	
	public LambdaWriteToDynamoFromKinesis() {
		this.dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
	}
	
	public static void main(String[] args) {
        SpringApplication.run(LambdaWriteToDynamoFromKinesis.class, args);
    }

    public void putItem(String tableName, Map<String, AttributeValue> obj) {
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(obj)
                .build();

        try {
            dynamoDbClient.putItem(putItemRequest);
            System.out.println("Successfully inserted item into DynamoDB");
        } catch (DynamoDbException e) {
            System.err.println("Could not insert item into DynamoDB: " + e.getMessage());
        }
    }
	

    @Override
    public Void handleRequest(KinesisEvent event, Context context) {
    	
    	
        for (KinesisEventRecord record : event.getRecords()) {
        	
        	
        	
            String payload = new String(record.getKinesis().getData().array(), StandardCharsets.UTF_8);
//            String payload = StandardCharsets.UTF_8.decode(record.getKinesis().getData()).toString();
        	
            
//        	String jsonMessage = new String(record.getKinesis().getData().array());
            JSONObject jsonObject = new JSONObject(payload);
            String data = jsonObject.toString();

        	// Assuming record.kinesis.data is a string containing base64 encoded data
//        	ByteBuffer dataBuffer = record.getKinesis().getData();
//        	String payload = "";
        	
        	Map<String, AttributeValue> obj = null;
        	byte[] byteData = null;
        	byte[] decodedBytes = null;
        	JsonNode jsonNode = null;
        	Map<String, Object> dataMap = null;
        	Account account = null;
        	Map<String, Object> jsonData = null;
        	
			try {
//				byteData = new byte[dataBuffer.remaining()];
//				dataBuffer.get(byteData);
				
//				decodedBytes = Base64.decodeBase64(byteData);
				
//				payload = new String(decodedBytes, StandardCharsets.UTF_8);
				
	            // Parse the JSON string into a JSON object
//	            ObjectMapper objectMapper = new ObjectMapper();
//	            jsonNode = objectMapper.readTree(payload);
				
//				obj = parsePayload(payload);
//	            ObjectMapper objectM = new ObjectMapper();
//	            dataMap = objectMapper.readValue(payload, Map.class);
//	            Map<String, AttributeValue> item = new HashMap<>();
//	            dataMap.forEach((key, value) -> item.put(key, AttributeValue.builder().s(value.toString()).build()));
//	            obj = item;
	            
	            ObjectMapper mapper = new ObjectMapper();
	            jsonData = mapper.readValue(data, Map.class);
	            
	            Map<String, AttributeValue> itemAttributes = new HashMap<>();
	            itemAttributes.put("accountId", AttributeValue.builder().s((String)jsonData.get("accountId")).build());
	            itemAttributes.put("username", AttributeValue.builder().s((String)jsonData.get("username")).build());
	            itemAttributes.put("creditDate", AttributeValue.builder().s((String)jsonData.get("creditDate")).build());
	            itemAttributes.put("creditTime", AttributeValue.builder().s((String)jsonData.get("creditTime")).build());
	            itemAttributes.put("amount", AttributeValue.builder().s(String.valueOf(jsonData.get("amount"))).build());
	            
	            obj = itemAttributes;
	            
			} catch (Exception e) {
				System.err.println("private kye is "+Base64.encodeBase64String(byteData));
				System.err.println("Key, Value ->"+jsonData.get("accountId")+" "+jsonData.get("amount"));
				System.err.println("Object, String ->"+String.valueOf(jsonData.get("amount")));
				System.out.println("This is the account -> "+account);
				System.err.println("Decoded -> "+decodedBytes);
//				System.err.println("Processed json data: " + jsonObject.toString(2));
				System.err.println("This is the event -> "+event);
				// TODO Auto-generated catch block
				System.err.println("This is the json to string data -> "+data);
				System.err.println("This is the json to string jsonData -> "+jsonData);
				System.err.println("There is some problem in parsePayload method.! ");
				System.err.println("This is the jsonObject -> "+jsonObject);
				System.err.println("This is the byteData -> "+byteData);
				System.err.println("This is the object -> "+obj);
				System.err.println("This is the byteData -> "+byteData);
				System.err.println("Not properly decoding ->"+ e.getMessage());
				e.printStackTrace();
			} 

            this.putItem(TABLE_NAME, obj);
            
            
            
            
            
        }

        return null;
    }

    private Map<String, AttributeValue> parsePayload(String payload) throws JsonMappingException, JsonProcessingException {
        // Parse your payload into a Map<String, AttributeValue>
        // This will depend on the structure of your payload

    
  /*      ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> rawMap = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
        Map<String, AttributeValue> attributeValueMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
            Object value = entry.getValue();
            AttributeValue attributeValue;

            if (value instanceof String) {
                attributeValue = AttributeValue.builder().s((String) value).build();
            } else if (value instanceof Number) {
                attributeValue = AttributeValue.builder().n(value.toString()).build();
            } else if (value instanceof Boolean) {
                attributeValue = AttributeValue.builder().bool((Boolean) value).build();
            } else {
                // Add more type conversions as needed
                attributeValue = AttributeValue.builder().s(value.toString()).build();
            }

            attributeValueMap.put(entry.getKey(), attributeValue);
        }
    	return attributeValueMap;
            */
    	
//         ObjectMapper objectMapper = new ObjectMapper();
//         Map<String, Object> dataMap = objectMapper.readValue(payload, Map.class);
//         System.out.println("This is the datamap -> "+dataMap);
         Map<String, AttributeValue> item = new HashMap<>();
//         dataMap.forEach((key, value) -> item.put(key, AttributeValue.builder().s(value.toString()).build()));
         return item;


    }

}
