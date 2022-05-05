package ca.ralphsplace.djindex.controller;

import ca.ralphsplace.djindex.model.TradeDataRecord;
import ca.ralphsplace.djindex.service.TradeDataService;
import com.opencsv.bean.CsvToBeanBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trade-data")
public class TradeDataController {

    private static final String ERROR_MSG = "error caught exception: ";
    private static final Logger LOG = LoggerFactory.getLogger(TradeDataController.class);

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
    @Async("controllerAsyncExecutor")
    public CompletableFuture<ResponseEntity<List<TradeDataRecord>>> getTradeDataTicker(@PathVariable final String stock) {
        return tradeDataService.findByStock(stock)
                .thenApply(ResponseEntity::ok)
                .exceptionally(t -> {
                    LOG.error(ERROR_MSG, t);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @Operation(summary = "Create weekly trade data for a stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trade data created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDataRecord.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid trade data supplied", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")})
    @PostMapping(value = "/", consumes = {"application/json"}, produces = {"application/json"}, headers = {"X-client_id"})
    @Async("controllerAsyncExecutor")
    public CompletableFuture<ResponseEntity<TradeDataRecord>> createTradeData(@RequestBody final TradeDataRecord tradeDataRecord) {
        return tradeDataService.save(tradeDataRecord.setId())
                .thenApply(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                .exceptionally(t -> {
                    LOG.error(ERROR_MSG, t);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @Operation(summary = "CSV file upload, for bulk creation of weekly trade data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trade data created",
                    content = { @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid trade data supplied", content = @Content),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED")})
    @PostMapping(value = "/bulk-insert", consumes = {"multipart/form-data"}, produces = {"application/json"}, headers = {"X-client_id"})
    @Async("controllerAsyncExecutor")
    public CompletableFuture<ResponseEntity<String>> bulkUpdate(@RequestParam final MultipartFile file) {
        CompletableFuture<List<TradeDataRecord>> cfRecords = CompletableFuture.supplyAsync(() -> {
                    try {
                        return new CsvToBeanBuilder<TradeDataRecord>(
                                new BufferedReader(new InputStreamReader(file.getInputStream())))
                                        .withType(TradeDataRecord.class)
                                        .build()
                                        .stream()
                                        .map(TradeDataRecord::setId)
                                        .collect(Collectors.toList());
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                });

        List<TradeDataRecord> records = new ArrayList<>();
        try {
            records = cfRecords.join();
        } catch (CompletionException e) {
            LOG.error(ERROR_MSG, e);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        }

        return tradeDataService.save(records)
                .thenApply(c -> ResponseEntity.status(HttpStatus.CREATED).body("trade data records created:"+c))
                .exceptionally(t -> {
                    LOG.error("error caught exception: ", t);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
}
