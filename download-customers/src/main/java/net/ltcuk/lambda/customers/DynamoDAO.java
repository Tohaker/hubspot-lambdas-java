package net.ltcuk.lambda.customers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class DynamoDAO {
    private AmazonDynamoDB client;

    /**
     * Creates an AmazonDynamoDB Client based on the default
     * settings in ~/.aws/credentials.
     */
    public DynamoDAO() {
        this(AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(Regions.DEFAULT_REGION)
                .build());
    }

    /**
     * Creates a Dynamo DB Data Access Object (DAO) given am
     * AmazonDynamoDB Client.
     *
     * @param client Your own AmazonDynamoDB Client.
     */
    public DynamoDAO(AmazonDynamoDB client) {
        this.client = client;
    }

    public Item getCustomer(String userId) {
        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("customers");
        return table.getItem("UserId", userId);
    }
}
