package ca.ralphsplace.djindex.model;

import ca.ralphsplace.djindex.StockDataRecordUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClientTradeDataTest {
    StockDataRecord t1 = StockDataRecordUtil.buildTradeDataRecord("1","AA","1/14/2011","$16.71","$16.71","$15.64","$15.97","242963398","-4.42849","1.380223028","239655616","$16.19","$15.79","-2.47066","19","0.187852");

    @Test
    void toTradeDataRecord() {
        ClientStockData c1 = t1.toClientTradeData("abc123");
        Assertions.assertEquals(t1.getDate(), c1.toTradeDataRecord().getDate());
        Assertions.assertEquals(t1.getStock(), c1.toTradeDataRecord().getStock());
        Assertions.assertEquals(t1.getClose(), c1.toTradeDataRecord().getClose());
        Assertions.assertEquals(t1.getDaysToNextDividend(), c1.toTradeDataRecord().getDaysToNextDividend());
        Assertions.assertEquals(t1.getHigh(), c1.toTradeDataRecord().getHigh());
        Assertions.assertEquals(t1.getLow(), c1.toTradeDataRecord().getLow());
        Assertions.assertEquals(t1.getNextWeeksClose(), c1.toTradeDataRecord().getNextWeeksClose());
        Assertions.assertEquals(t1.getNextWeeksOpen(), c1.toTradeDataRecord().getNextWeeksOpen());
        Assertions.assertEquals(t1.getOpen(), c1.toTradeDataRecord().getOpen());
        Assertions.assertEquals(t1.getPercentChangeNextWeeksPrice(), c1.getPercentChangeNextWeeksPrice());
        Assertions.assertEquals(t1.getPercentChangePrice(), c1.toTradeDataRecord().getPercentChangePrice());
    }
}
