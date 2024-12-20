package org.example.sokcsaccounting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.sokcsaccounting.dto.*;
import org.example.sokcsaccounting.data.Socks;
import org.example.sokcsaccounting.exception.IllegalOperationException;
import org.example.sokcsaccounting.exception.ParseFileException;
import org.example.sokcsaccounting.repository.SockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocksService {

    private final SockRepository sockRepository;

    public void income(SocksEditDto socksEditDto) {
        Optional<Socks> socksOpt = sockRepository.findByColorAndCottonPart(socksEditDto.getColor(), socksEditDto.getCottonPart());
        if (socksOpt.isPresent()) {
            Socks socks = socksOpt.get();
            sockRepository.updateSocksById(socks.getId(), socks.increaseQuantity(socksEditDto.getQuantity()).getQuantity());
            log.info(
                    "Socks increased. Color: {}, CottonPart: {}, Quantity: {}",
                    socks.getColor(),
                    socks.getCottonPart(),
                    socks.getQuantity()
            );
        } else {
            Socks socks = Socks.of(
                    socksEditDto.getColor(),
                    socksEditDto.getCottonPart(),
                    socksEditDto.getQuantity());
            sockRepository.save(socks);
            log.info(
                    "Socks created. Color: {}, CottonPart: {}, Quantity: {}",
                    socks.getColor(),
                    socks.getCottonPart(),
                    socks.getQuantity()
            );
        }
    }

    public void outcome(SocksEditDto socksEditDto) {
        Optional<Socks> socksOpt = sockRepository.findByColorAndCottonPart(socksEditDto.getColor(), socksEditDto.getCottonPart());
        if (socksOpt.isPresent()) {
            Socks socks = socksOpt.get();
            sockRepository.updateSocksById(socks.getId(), socks.decreaseQuantity(socksEditDto.getQuantity()).getQuantity());
            log.info(
                    "Socks decreased. Color: {}, CottonPart: {}, Quantity: {}",
                    socks.getColor(),
                    socks.getCottonPart(),
                    socks.getQuantity()
            );
        } else {
            throw new IllegalOperationException("Socks not found. Color: " + socksEditDto.getColor() + ", CottonPart: " + socksEditDto.getCottonPart());
        }
    }

    public void update(SocksDto socksDto) {
        Optional<Socks> socksOpt = sockRepository.findById(socksDto.getId());
        if (socksOpt.isPresent()) {
            Socks socks = socksOpt.get();
            socks = socks.update(
                    socksDto.getColor(),
                    socksDto.getCottonPart(),
                    socksDto.getQuantity()
            );
            sockRepository.updateSocksById(
                    socks.getId(),
                    socks.getColor(),
                    socks.getCottonPart(),
                    socks.getQuantity()
            );
            log.info(
                    "Socks updated. Id: {}, Color: {}, CottonPart: {}, Quantity: {}",
                    socks.getId(),
                    socks.getColor(),
                    socks.getCottonPart(),
                    socks.getQuantity()
            );
        } else {
            throw new IllegalOperationException("Socks not found. Id: " + socksDto.getId());
        }
    }

    public void upload(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("File is null");
        }
        try {
            if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
                processCsvFile(file);
            } else if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                processExcelFile(file);
            } else {
                throw new IllegalArgumentException("Invalid file format. Only CSV or Excel (XLSX) files are allowed.");
            }
            log.info("File uploaded. File name: {}", file.getOriginalFilename());
        } catch (IOException e) {
            throw new ParseFileException(e);
        }
    }

    public PaginatedListDto<SocksDto> getAll(SocksFilterDto socksFilterDto, SocksSortDto socksSortDto, PaginationDto paginationDto) {
        Sort sort;
        switch (socksSortDto.orderType()) {
            case UNORDERED -> sort = Sort.unsorted();
            case ASCENDING -> sort = Sort.by(Sort.Order.by(socksSortDto.parameter().getValue()).with(Sort.Direction.ASC));
            case DESCENDING -> sort = Sort.by(Sort.Order.by(socksSortDto.parameter().getValue()).with(Sort.Direction.DESC));
            default -> throw new IllegalArgumentException("Invalid order type: " + socksSortDto.orderType());
        }
        Pageable pageable = PageRequest.of(paginationDto.page(), paginationDto.size(), sort);

        Page<Socks> socks;
        switch (socksFilterDto.comparisonOperatorType()) {
            case GREATER -> socks = sockRepository.findAllByColorAndCottonPartGreaterThan(
                                    socksFilterDto.color(),
                                    socksFilterDto.parameter().get(0),
                                    pageable
                            );
            case LESS -> socks = sockRepository.findAllByColorAndCottonPartLessThan(
                                    socksFilterDto.color(),
                                    socksFilterDto.parameter().get(0),
                                    pageable
                            );
            case EQUAL -> socks = sockRepository.findAllByColorAndCottonPart(
                                    socksFilterDto.color(),
                                    socksFilterDto.parameter().get(0),
                                    pageable
                            );
            case BETWEEN -> socks = sockRepository.findAllByColorAndCottonPartBetween(
                                    socksFilterDto.color(),
                                    socksFilterDto.parameter().get(0),
                                    socksFilterDto.parameter().get(1),
                                    pageable
                            );
            default -> throw new IllegalArgumentException("Invalid comparison operator: " + socksFilterDto.comparisonOperatorType());
        }
        log.info(
                "Socks found. Color: {}, CottonPart: {}, Quantity: {}, Total count: {}",
                socksFilterDto.color(),
                socksFilterDto.parameter().get(0),
                socks.getTotalElements(),
                socks.getTotalElements()
        );
        return new PaginatedListDto<>(
                socks.stream().map(SocksDto::from).toList(),
                socks.getTotalElements()
        );
    }

    private void processExcelFile(MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rows = sheet.iterator();
        rows.next();

        while (rows.hasNext()) {
            Row row = rows.next();
            String color = row.getCell(0).getStringCellValue();
            Double cottonPart = Double.parseDouble(row.getCell(1).getStringCellValue());
            Integer quantity = Integer.parseInt(row.getCell(2).getStringCellValue());

            income(new SocksEditDto(color, cottonPart, quantity));
        }
    }

    private void processCsvFile(MultipartFile file) throws IOException {
        Reader reader = new InputStreamReader(file.getInputStream());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .parse(reader);

        for (CSVRecord record : records) {
            String color = record.get("color");
            Double cottonPart = Double.parseDouble(record.get("cottonPart"));
            Integer quantity = Integer.parseInt(record.get("quantity"));

            income(new SocksEditDto(color, cottonPart, quantity));
        }
    }

}
