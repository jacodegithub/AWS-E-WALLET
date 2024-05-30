package com.project.LambdaWriteToDynamoFromKinesis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.Record;
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
import software.amazon.ion.IonReader;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.system.IonReaderBuilder;

import org.apache.commons.codec.binary.Base64;


@SpringBootApplication
public class LambdaWriteToDynamoFromKinesis implements RequestHandler<KinesisEvent, Void> {
	
	
//	private static final String TABLE_NAME = "Brpl-DynamoDB";
//
//	@Autowired
//	private final DynamoDbClient dynamoDbClient;
//
//	
//	public LambdaWriteToDynamoFromKinesis() {
//		this.dynamoDbClient = DynamoDbClient.builder().region(Region.US_EAST_1).build();
//	}
//	
//	public static void main(String[] args) {
//        SpringApplication.run(LambdaWriteToDynamoFromKinesis.class, args);
//    }
//
//    public void putItem(String tableName, Map<String, AttributeValue> obj) {
//        PutItemRequest putItemRequest = PutItemRequest.builder()
//                .tableName(tableName)
//                .item(obj)
//                .build();
//
//        try {
//            dynamoDbClient.putItem(putItemRequest);
//            System.out.println("Successfully inserted item into DynamoDB");
//        } catch (DynamoDbException e) {
//            System.err.println("Could not insert item into DynamoDB: " + e.getMessage());
//        }
//    }

	@Override
	public Void handleRequest(KinesisEvent event, Context context) {
		for (KinesisEventRecord record : event.getRecords()) {
            Record base64EncodedData = record.getKinesis();
            String data= base64EncodedData.toString();
            byte[] buf =base64EncodedData.getData().array();       
            Base64 bf = new Base64();
            byte[] decode =bf.decode(buf);
            
            List<Map<String, Object>> deaggregatedRecords = null;
			try {
				deaggregatedRecords = deaggregateRecords(decode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            for (Map<String, Object> rec : deaggregatedRecords) {
                Object ionData = rec.get("ionData");
                if (ionData instanceof IonStruct) {
                    IonStruct ionRecord = (IonStruct) ionData;
                    processIonRecord(ionRecord);
                }
            }
//            char[] charArray = new char[decode.length];
//            for (int i = 0; i < decode.length; i++) {
//                charArray[i] = (char) decode[i];
//            }
//            String decodedString = new String(charArray);
//            System.out.println("Decoded string: " + decodedString);
//            String datas = StandardCharsets.UTF_8.decode(buf).toString();
//            System.out.println("Successfully inserted item into decodedData" + datas);
            
            
        
//            byte[] eecodedData =Base64.decodeBase64(decodedData);
//            System.out.println("Successfully inserted item into eecodedData" + eecodedData);
//            String data = Base64.encodeBase64String(eecodedData);
//            System.out.println("Successfully inserted item into eecodatadedData" + data);
//        
            
        }
		return null;
	}
		
	    List<Map<String, Object>> deaggregateRecords(byte[] decodedData) throws IOException {
	        List<Map<String, Object>> deaggregatedRecords = new ArrayList<>();
	        try (ByteArrayInputStream bais = new ByteArrayInputStream(decodedData);
	             IonReader reader = IonReaderBuilder.standard().build(bais)) {
	        	System.out.println("This is IonReader -> "+reader.next());
	        	
//	            reader.next();
//	            while (reader.next() != null) {
//	                Map<String, Object> deaggregatedRecord = new HashMap<>();
//	                IonType valueType = reader.next();
//	                if (valueType == IonType.STRUCT) {
//	                    IonStruct ionStruct = (IonStruct) reader;
//	                    deaggregatedRecord.put("ionData", ionStruct);
//	                    deaggregatedRecords.add(deaggregatedRecord);
//	                }
//	            }
	        }
	        return deaggregatedRecords;
	    }
	    
	    private void processIonRecord(IonStruct ionRecord) {
	        IonValue recordTypeValue = ionRecord.get("recordType");
	        if (recordTypeValue != null && "REVISION_DETAILS".equals(recordTypeValue.toString())) {
	            IonValue payloadValue = ionRecord.get("payload");
	            if (payloadValue != null && payloadValue instanceof IonStruct) {
	                IonStruct payload = (IonStruct) payloadValue;

	                // Process the payload accordingly
	                // For example, retrieve values from the payload and perform actions
//	                IonValue versionValue = payload.get("revision").get("metadata").get("version");
//	                IonValue idValue = payload.get("revision").get("metadata").get("id");

	                // Extract new values (assuming they exist in the payload structure)
	                IonValue accountIdValue = payload.get("accountId"); // Modify the path if it's different
	                IonValue creditDateValue = payload.get("creditDate"); // Modify the path if it's different
	                IonValue creditTimeValue = payload.get("creditTime"); // Modify the path if it's different
	                IonValue amountValue = payload.get("amount"); // Modify the path if it's different

	                
	                String accountId = accountIdValue != null ? accountIdValue.toString() : null; // Handle null values
                	String creditDate = creditDateValue != null ? creditDateValue.toString() : null; // Handle null values
                	String creditTime = creditTimeValue != null ? creditTimeValue.toString() : null; // Handle null values
                	String amount = String.valueOf(amountValue); // Handle null values (assuming amount is a double)

                	System.out.println("This is the data -> "+accountId+ " "+creditDate+" "+creditTime+" "+amount);
//	                IonValue dataValue = payload.get("revision").get("data");
	                
                	
//                	if (dataValue == null) {
//	                    // Handle as a delete
////	                    String id = idValue.toString();
////	                    String version = versionValue.toString();
//	                    // Call method to delete record from DynamoDB (replace with your implementation)
//	                    // deleteLicence(id, version);
//	                } else {
//	                    
//	                	String accountId = accountIdValue != null ? accountIdValue.toString() : null; // Handle null values
//	                	String creditDate = creditDateValue != null ? creditDateValue.toString() : null; // Handle null values
//	                	String creditTime = creditTimeValue != null ? creditTimeValue.toString() : null; // Handle null values
//	                	String amount = String.valueOf(amountValue); // Handle null values (assuming amount is a double)
//
//	                	System.out.println("This is the data -> "+accountId+ " "+creditDate+" "+creditTime+" "+amount);
//	                        // Call method to update or insert record into DynamoDB (replace with your implementation)
//	                        // updateLicence(id, penaltyPoints, postcode, version, accountId, creditDate, creditTime, amount);
//	                    
//	                }
	            }
	        }
	    }

	



}
