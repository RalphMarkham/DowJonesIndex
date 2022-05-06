package ca.ralphsplace.djindex.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "weeklyTradeData")
public class ClientTradeData extends TradeDataRecord {

    public static final String WEEKLY_TRADE_DATA = "weeklyTradeData";
    


    @Id
    private String id;
    private String clientId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public TradeDataRecord toTradeDataRecord() {
        TradeDataRecord trd = new TradeDataRecord();

        trd.setQuarter(this.getQuarter());
        trd.setStock(this.getStock());
        trd.setDate(this.getDate());
        trd.setOpen(this.getOpen());
        trd.setHigh(this.getHigh());
        trd.setLow(this.getLow());
        trd.setClose(this.getClose());
        trd.setVolume(this.getVolume());
        trd.setPercentChangePrice(this.getPercentChangePrice());
        trd.setPercentChangeVolumeOverLastWk(this.getPercentChangeVolumeOverLastWk());
        trd.setPreviousWeeksVolume(this.getPreviousWeeksVolume());
        trd.setNextWeeksOpen(this.getNextWeeksOpen());
        trd.setNextWeeksClose(this.getNextWeeksClose());
        trd.setPercentChangeNextWeeksPrice(this.getPercentChangeNextWeeksPrice());
        trd.setDaysToNextDividend(this.getDaysToNextDividend());
        trd.setPercentReturnNextDividend(this.getPercentReturnNextDividend());

        return trd;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ClientTradeData that = (ClientTradeData) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
