package ca.ralphsplace.djindex.service;

import ca.ralphsplace.djindex.model.ClientTradeData;
import ca.ralphsplace.djindex.model.TradeDataRecord;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class TradeDataService {

    private static final String ID = "clientId";
    private final MongoTemplate mt;

    public TradeDataService(MongoTemplate mt) {
        this.mt = mt;
    }

    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<TradeDataRecord> save(final ClientTradeData ctd) {
        return CompletableFuture
                .supplyAsync(() -> mt.save(ctd, ClientTradeData.WEEKLY_TRADE_DATA).toTradeDataRecord());
    }

    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<Collection<TradeDataRecord>> bulkSave(final List<ClientTradeData> ctdList) {
        return CompletableFuture
                .supplyAsync(() -> mt.insert(ctdList, ClientTradeData.WEEKLY_TRADE_DATA)
                        .stream().map(ClientTradeData::toTradeDataRecord).collect(Collectors.toList()));
    }

    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<Collection<TradeDataRecord>> findByStock(final String clientId, final String stock) {
        return CompletableFuture.supplyAsync(() -> (new Query()).addCriteria(Criteria.where(ID).is(clientId)
                        .andOperator(Criteria.where("stock").is(stock))))
                .thenApply(q -> mt.find(q, ClientTradeData.class, ClientTradeData.WEEKLY_TRADE_DATA)
                        .stream().map(ClientTradeData::toTradeDataRecord).collect(Collectors.toList()));
    }
}
