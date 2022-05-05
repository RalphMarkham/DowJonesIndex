package ca.ralphsplace.djindex.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.Set;

@Document(collection = "weeklyTradeData")
public class ClientTradeData {

    @Id
    private String id;

    private Set<TradeDataRecord> records;

    public ClientTradeData() {}

    public ClientTradeData(String id, Set<TradeDataRecord> records) {
        this.id = id;
        this.records = records;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<TradeDataRecord> getRecords() {
        return records;
    }

    public void setRecords(Set<TradeDataRecord> records) {
        this.records = records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientTradeData that = (ClientTradeData) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
