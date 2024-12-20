package org.example.sokcsaccounting.endPoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sokcsaccounting.dto.*;
import org.example.sokcsaccounting.service.SocksService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/socks")
@AllArgsConstructor
public class SocksEndPoint {


    private SocksService socksService;

    @PostMapping("/income")
    @Operation(summary = "Register new socks income", description = "Registers an income batch of socks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Income successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<?> income(
            @Valid @RequestBody SocksEditDto request
    ) {
        socksService.income(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Register socks outcome", description = "Registers an outcome batch of socks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Socks successfully removed"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or not enough socks")
    })
    @PostMapping("/outcome")
    public ResponseEntity<?> outcome(
            @Valid @RequestBody SocksEditDto request
    ) {
        socksService.outcome(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get filtered socks", description = "Fetch a paginated list of socks based on filter criteria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list of filtered and ordered socks"),
            @ApiResponse(responseCode = "400", description = "Invalid filter or pagination parameters")
    })
    @GetMapping("")
    public ResponseEntity<?> getAll(
            @RequestParam String color,
            @RequestParam String comparisonOperator,
            @RequestParam Double parameter,
            @RequestParam(required = false) Double parameter2,
            @RequestParam(required = false) String sortParameter,
            @RequestParam(required = false) String sortType,
            @RequestParam int page,
            @RequestParam int size
    ) {
        SocksFilterDto socksFilterDto = SocksFilterDto.of(
                color,
                comparisonOperator,
                List.of(parameter, parameter2)
        );
        SocksSortDto socksSortDto = SocksSortDto.of(
                sortParameter,
                sortType
        );
        PaginationDto paginationDto = new PaginationDto(page, size);
        PaginatedListDto<SocksDto> response = socksService.getAll(
                socksFilterDto,
                socksSortDto,
                paginationDto
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update socks by ID", description = "Allows updating socks properties")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Socks successfully updated"),
            @ApiResponse(responseCode = "400", description = "Socks not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @Valid @RequestBody SocksEditDto request
    ) {
        SocksDto socksDto = request.to(id);
        socksService.update(socksDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Upload a batch file", description = "Uploads an Excel or CSV file containing socks batches")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File uploaded and processed successfully"),
            @ApiResponse(responseCode = "400", description = "File format error"),
            @ApiResponse(responseCode = "500", description = "Parse file error")
    })
    @PostMapping("/batch")
    public ResponseEntity<?> batch(@RequestParam("file") MultipartFile file) {
        socksService.upload(file);
        return ResponseEntity.ok().build();
    }
}