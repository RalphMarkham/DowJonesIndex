package ca.ralphsplace.djindex;

import com.mongodb.bulk.BulkWriteResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeDataService {

    private final MongoTemplate mongoTemplate;

    public TradeDataService(@Lazy MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public TradeDataRecord save(TradeDataRecord tradeDataRecord) {
        return mongoTemplate.save(tradeDataRecord);
    }

    public int save(List<TradeDataRecord> tradeDataRecords) {
        BulkWriteResult bwr = mongoTemplate
                .bulkOps(BulkOperations.BulkMode.ORDERED, TradeDataRecord.class)
                .insert(tradeDataRecords)
                .execute();

        return bwr.getInsertedCount();
    }

    public List<TradeDataRecord> findByStock(String stock) {
        Query query = new Query();
        query.addCriteria(Criteria.where("stock").is(stock));

        return mongoTemplate.find(query, TradeDataRecord.class);
    }
}
