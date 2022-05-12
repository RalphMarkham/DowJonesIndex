package ca.ralphsplace.djindex.service;

import ca.ralphsplace.djindex.model.ClientStockData;
import ca.ralphsplace.djindex.model.StockDataRecord;
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
public class StockDataService {

    private static final String ID = "clientId";
    private final MongoTemplate mt;

    public StockDataService(MongoTemplate mt) {
        this.mt = mt;
    }

    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<StockDataRecord> save(final ClientStockData ctd) {
        return CompletableFuture
                .supplyAsync(() -> mt.save(ctd, ClientStockData.WEEKLY_STOCK_DATA).toTradeDataRecord());
    }

    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<Collection<StockDataRecord>> bulkSave(final List<ClientStockData> ctdList) {
        return CompletableFuture.supplyAsync(() -> mt.insert(ctdList, ClientStockData.WEEKLY_STOCK_DATA)
                        .stream().map(ClientStockData::toTradeDataRecord).collect(Collectors.toList()));
    }

    @Transactional
    @Async("serviceAsyncExecutor")
    public CompletableFuture<Collection<StockDataRecord>> findByStock(final String clientId, final String stock) {
        return CompletableFuture.supplyAsync(() -> (new Query()).addCriteria(Criteria.where(ID).is(clientId)
                        .andOperator(Criteria.where("stock").is(stock))))
                .thenApply(q -> mt.find(q, ClientStockData.class, ClientStockData.WEEKLY_STOCK_DATA)
                        .stream().map(ClientStockData::toTradeDataRecord).collect(Collectors.toList()));
    }
}
