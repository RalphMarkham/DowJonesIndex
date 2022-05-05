package ca.ralphsplace.djindex.service;

import ca.ralphsplace.djindex.model.ClientTradeData;
import ca.ralphsplace.djindex.model.TradeDataRecord;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class TradeDataService {

    private static final String RECORDS = "records";
    private static final String ID = "id";
    private final MongoTemplate mt;

    public TradeDataService(MongoTemplate mt) {
        this.mt = mt;
    }

    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<UpdateResult> save(final ClientTradeData ctd) {
        return CompletableFuture
                .supplyAsync(() -> mt.findById(ctd.getId(), ClientTradeData.class))
                .thenApply(qCtd -> {
                    var q = new Query();
                    var u = new Update();
                    q.addCriteria(Criteria.where(ID).is(ctd.getId()));
                    u.addToSet(RECORDS, ctd.getRecords().iterator().next());
                    return mt.upsert(q, u, ClientTradeData.class);
                });
    }

    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<UpdateResult> bulkSave(final ClientTradeData ctd) {
        return CompletableFuture
                .supplyAsync(() -> mt.findById(ctd.getId(), ClientTradeData.class))
                .thenApply(qCtd -> {
                    var q = new Query();
                    var u = new Update();
                    var records = ctd.getRecords().toArray(new TradeDataRecord[ctd.getRecords().size()]);
                    q.addCriteria(Criteria.where(ID).is(ctd.getId()));
                    u.push(RECORDS).each(records);
                    return mt.upsert(q, u, ClientTradeData.class);
                });
    }


    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<Set<TradeDataRecord>> findByStock(final String clientId, final String stock) {
        return CompletableFuture.supplyAsync(() -> (new Query()).addCriteria(Criteria.where(ID).is(clientId)
                        .andOperator(Criteria.where(RECORDS).elemMatch(Criteria.where("stock").is(stock)))))
                .thenApply(q -> mt.findOne(q, ClientTradeData.class))
                .thenApply(ClientTradeData::getRecords);
    }
}
