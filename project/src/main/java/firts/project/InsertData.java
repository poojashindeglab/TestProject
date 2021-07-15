package firts.project;

import java.io.File;
import java.util.Iterator;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class InsertData {
	public static void main(String[] args) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
            .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("Users");

        JsonParser parser = new JsonFactory().createParser(new File("DataFile/data.json"));

        JsonNode rootNode = new ObjectMapper().readTree(parser);
        Iterator<JsonNode> iter = rootNode.iterator();

        ObjectNode currentNode;

        while (iter.hasNext()) {
            currentNode = (ObjectNode) iter.next();

            int id = currentNode.path("ID").asInt();
            String name = currentNode.path("Name").asText();

            try {
                table.putItem(new Item().withPrimaryKey("ID", id, "Name", name).withJSON("info",
                    currentNode.path("info").toString()));
                System.out.println("PutItem succeeded: " + id + " " + name);

            }
            catch (Exception e) {
                System.err.println("Unable to add Users: " + id + " " + name);
                System.err.println(e.getMessage());
                break;
            }
        }
        parser.close();    
	       
	 }
}
