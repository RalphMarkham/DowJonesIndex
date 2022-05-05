package ca.ralphsplace.djindex.service;

import ca.ralphsplace.djindex.model.TradeDataRecord;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TradeDataService {

    private final MongoTemplate mongoTemplate;

    public TradeDataService(@Lazy MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Async("serviceAsyncExecutor")
    public CompletableFuture<TradeDataRecord> save(final TradeDataRecord tradeDataRecord) {
        return CompletableFuture
                .supplyAsync(() -> mongoTemplate.save(tradeDataRecord));
    }

    @Async("serviceAsyncExecutor")
    public CompletableFuture<Integer> save(final List<TradeDataRecord> tradeDataRecords) {
        return CompletableFuture.supplyAsync(() -> {
            BulkWriteResult bwr = mongoTemplate
                    .bulkOps(BulkOperations.BulkMode.ORDERED, TradeDataRecord.class)
                    .insert(tradeDataRecords)
                    .execute();
            return bwr.getInsertedCount();
        });
    }

    @Async("serviceAsyncExecutor")
    public CompletableFuture<List<TradeDataRecord>> findByStock(final String stock) {
        return CompletableFuture.supplyAsync(() -> (new Query()).addCriteria(Criteria.where("stock").is(stock)))
                .thenApply(q -> mongoTemplate.find(q, TradeDataRecord.class));
    }
}
