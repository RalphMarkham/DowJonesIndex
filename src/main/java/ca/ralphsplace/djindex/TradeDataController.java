package ca.ralphsplace.djindex;

import com.opencsv.bean.CsvToBeanBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trade-data")
public class TradeDataController {

    @Autowired
    private final TradeDataService tradeDataService;

    public TradeDataController(TradeDataService tradeDataService) {
        this.tradeDataService = tradeDataService;
    }

    @Operation(summary = "Get weekly trade data for a stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found trade data",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema( schema = @Schema(implementation = TradeDataRecord.class))) }),
            @ApiResponse(responseCode = "400", description = "Invalid stock supplied", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")})
    @GetMapping(value = "/{stock}", produces = {"application/json"}, headers = {"X-client_id"})
    public ResponseEntity<List<TradeDataRecord>> getTradeDataTicker(@PathVariable String stock) {
        return ResponseEntity.ok(tradeDataService.findByStock(stock));
    }

    @Operation(summary = "Create weekly trade data for a stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trade data created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDataRecord.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid trade data supplied", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")})
    @PostMapping(value = "/", consumes = {"application/json"}, produces = {"application/json"}, headers = {"X-client_id"})
    public ResponseEntity<TradeDataRecord> createTradeData(@RequestBody TradeDataRecord tradeDataRecord) {
        TradeDataRecord created = tradeDataService.save(tradeDataRecord.setId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "CSV file upload, for bulk creation of weekly trade data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trade data created",
                    content = { @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid trade data supplied", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")})
    @PostMapping(value = "/bulk-insert", consumes = {"multipart/form-data"}, produces = {"application/json"}, headers = {"X-client_id"})
    public ResponseEntity<String> bulkUpdate(@RequestParam MultipartFile file) {
        // TODO: Validation and detailed success / error messaging
        try {
            List<TradeDataRecord> records = new CsvToBeanBuilder<TradeDataRecord>(
                                                new BufferedReader(new InputStreamReader(file.getInputStream())))
                    .withType(TradeDataRecord.class)
                    .build()
                    .stream()
                    .map(TradeDataRecord::setId)
                    .collect(Collectors.toList());

            int recCount = tradeDataService.save(records);
            return ResponseEntity.status(HttpStatus.CREATED).body("trade data records created:"+recCount);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
