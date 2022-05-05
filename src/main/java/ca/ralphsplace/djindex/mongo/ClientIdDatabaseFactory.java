package ca.ralphsplace.djindex.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.Objects;

public class ClientIdDatabaseFactory extends SimpleMongoClientDatabaseFactory {

    public ClientIdDatabaseFactory(ConnectionString connectionString) {
        super(connectionString);
    }

    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {

        ConnectionString connectionString = new ConnectionString(ClientConnectionString.getClientConnectionStr());
        return super.doGetMongoDatabase(Objects.requireNonNull(connectionString.getDatabase()));
    }
}
