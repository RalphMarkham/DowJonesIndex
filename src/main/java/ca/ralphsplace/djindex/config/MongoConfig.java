package ca.ralphsplace.djindex.config;

import com.mongodb.ConnectionString;
import com.mongodb.ReadConcern;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

    @Lazy
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        TransactionOptions transactionOptions = TransactionOptions.builder()
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.W1)
                .build();
        return new MongoTransactionManager(dbFactory, transactionOptions);
    }

    @Lazy
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory dbFactory) {
        return new MongoTemplate(dbFactory);
    }

    @Lazy
    @Bean
    public MongoDatabaseFactory dbFactory() {
        final ConnectionString connectionString = new ConnectionString("mongodb://mongoDB:27017/tradeData");
        return new SimpleMongoClientDatabaseFactory(connectionString);
    }
}
