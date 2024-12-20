package org.example.sokcsaccounting.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.sokcsaccounting.data.Socks;
import org.example.sokcsaccounting.dto.*;
import org.example.sokcsaccounting.exception.IllegalOperationException;
import org.example.sokcsaccounting.repository.SockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
class SocksServiceTest {

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private SocksService socksService;

    @Autowired
    private SockRepository sockRepository;

    @Test
    void incomeTest() {
        SocksEditDto dto = new SocksEditDto("red", 85.0, 10);
        socksService.income(dto);

        Optional<Socks> socks = sockRepository.findByColorAndCottonPart("red", 85.0);

        assertTrue(socks.isPresent());
        Socks sock = socks.get();
        assertEquals("red", sock.getColor());
        assertEquals(85.0, sock.getCottonPart());
        assertEquals(10, sock.getQuantity());

        socksService.income(dto);

        socks = sockRepository.findByColorAndCottonPart("red", 85.0);

        assertTrue(socks.isPresent());
        sock = socks.get();
        assertEquals("red", sock.getColor());
        assertEquals(85.0, sock.getCottonPart());
        assertEquals(20, sock.getQuantity());
    }

    @Test
    void outcomePositiveTest() {
        SocksEditDto incomeDto = new SocksEditDto("black", 85.0, 10);
        socksService.income(incomeDto);
        SocksEditDto outcomeDto = new SocksEditDto("black", 85.0, 5);
        socksService.outcome(outcomeDto);

        Optional<Socks> socks = sockRepository.findByColorAndCottonPart("black", 85.0);

        assertTrue(socks.isPresent());
        Socks sock = socks.get();
        assertEquals("black", sock.getColor());
        assertEquals(85.0, sock.getCottonPart());
        assertEquals(5, sock.getQuantity());
    }

    @Test
    void outcomeNegativeTest() {
        SocksEditDto incomeDto = new SocksEditDto("black", 85.0, 10);
        socksService.income(incomeDto);
        SocksEditDto outcomeDto1 = new SocksEditDto("black", 85.0, 25);

        assertThrows(IllegalOperationException.class, () -> socksService.outcome(outcomeDto1));

        SocksEditDto outcomeDto2 = new SocksEditDto("error", 85.0, 15);
        assertThrows(IllegalOperationException.class, () -> socksService.outcome(outcomeDto1));
    }

    @Test
    void updatePositiveTest() {
        SocksEditDto incomeDto = new SocksEditDto("green", 85.0, 10);
        socksService.income(incomeDto);
        Socks socks = sockRepository.findByColorAndCottonPart("green", 85.0).get();
        SocksEditDto outcomeDto = new SocksEditDto("dark-green", 85.5, 9);
        socksService.update(outcomeDto.to(socks.getId()));

        socks = sockRepository.findByColorAndCottonPart("dark-green", 85.5).get();
        assertEquals("dark-green", socks.getColor());
        assertEquals(85.5, socks.getCottonPart());
        assertEquals(9, socks.getQuantity());
    }

    @Test
    void updateNegativeTest() {
        SocksEditDto incomeDto = new SocksEditDto("green", 85.0, 10);
        socksService.income(incomeDto);
        SocksEditDto outcomeDto = new SocksEditDto("dark-green", 85.5, 9);
        assertThrows(IllegalOperationException.class, () -> socksService.update(outcomeDto.to(UUID.randomUUID())));
    }

    @Test
    void getAllWithoutSortTest() {
        socksService.income(new SocksEditDto("white", 40.0, 5));
        socksService.income(new SocksEditDto("white", 35.0, 5));
        socksService.income(new SocksEditDto("white", 75.0, 5));

        SocksFilterDto socksFilterDto = SocksFilterDto.of("white", "EQUAL", List.of(35.0));
        SocksSortDto socksSortDto = SocksSortDto.of(null, null);
        PaginationDto paginationDto = new PaginationDto(0, 10);

        PaginatedListDto<SocksDto> socks = socksService.getAll(socksFilterDto, socksSortDto, paginationDto);

        assertEquals(1, socks.socks().size());
        assertEquals(1, socks.totalCount());
        assertEquals("white", socks.socks().get(0).getColor());
        assertEquals(35.0, socks.socks().get(0).getCottonPart());

        socksFilterDto = SocksFilterDto.of("white", "lessThan", List.of(41.0));
        socks = socksService.getAll(socksFilterDto, socksSortDto, paginationDto);

        assertEquals(2, socks.socks().size());
        assertEquals(2, socks.totalCount());
        assertEquals("white", socks.socks().get(0).getColor());
        assertEquals(40.0, socks.socks().get(0).getCottonPart());
        assertEquals("white", socks.socks().get(1).getColor());
        assertEquals(35.0, socks.socks().get(1).getCottonPart());

        socksFilterDto = SocksFilterDto.of("white", "moreThan", List.of(39.0));
        socks = socksService.getAll(socksFilterDto, socksSortDto, paginationDto);

        assertEquals(2, socks.socks().size());
        assertEquals(2, socks.totalCount());
        assertEquals("white", socks.socks().get(0).getColor());
        assertEquals(40.0, socks.socks().get(0).getCottonPart());
        assertEquals("white", socks.socks().get(1).getColor());
        assertEquals(75.0, socks.socks().get(1).getCottonPart());

        socksFilterDto = SocksFilterDto.of("white", "between", List.of(35.0, 75.0));
        socks = socksService.getAll(socksFilterDto, socksSortDto, paginationDto);

        assertEquals(3, socks.socks().size());
        assertEquals(3, socks.totalCount());
        assertEquals("white", socks.socks().get(0).getColor());
        assertEquals(40.0, socks.socks().get(0).getCottonPart());
        assertEquals("white", socks.socks().get(1).getColor());
        assertEquals(35.0, socks.socks().get(1).getCottonPart());
        assertEquals("white", socks.socks().get(2).getColor());
        assertEquals(75.0, socks.socks().get(2).getCottonPart());
    }

    @Test
    void getAllWithSortTest() {
        socksService.income(new SocksEditDto("black", 40.0, 5));
        socksService.income(new SocksEditDto("black", 35.0, 8));
        socksService.income(new SocksEditDto("black", 75.0, 5));

        SocksFilterDto socksFilterDto = SocksFilterDto.of("black", "between", List.of(35.0, 75.0));
        SocksSortDto socksSortDto = SocksSortDto.of("cottonPart", "ascend");
        PaginationDto paginationDto = new PaginationDto(0, 10);

        PaginatedListDto<SocksDto> socks = socksService.getAll(socksFilterDto, socksSortDto, paginationDto);

        assertEquals(3, socks.socks().size());
        assertEquals(3, socks.totalCount());
        assertEquals("black", socks.socks().get(0).getColor());
        assertEquals(35.0, socks.socks().get(0).getCottonPart());
        assertEquals("black", socks.socks().get(1).getColor());
        assertEquals(40.0, socks.socks().get(1).getCottonPart());
        assertEquals("black", socks.socks().get(2).getColor());
        assertEquals(75.0, socks.socks().get(2).getCottonPart());

        socksSortDto = SocksSortDto.of("cottonPart", "descend");
        socks = socksService.getAll(socksFilterDto, socksSortDto, paginationDto);

        assertEquals(3, socks.socks().size());
        assertEquals(3, socks.totalCount());
        assertEquals("black", socks.socks().get(0).getColor());
        assertEquals(75.0, socks.socks().get(0).getCottonPart());
        assertEquals("black", socks.socks().get(1).getColor());
        assertEquals(40.0, socks.socks().get(1).getCottonPart());
        assertEquals("black", socks.socks().get(2).getColor());
        assertEquals(35.0, socks.socks().get(2).getCottonPart());

        socksSortDto = SocksSortDto.of("quantity", "descend");
        socks = socksService.getAll(socksFilterDto, socksSortDto, paginationDto);

        assertEquals(3, socks.socks().size());
        assertEquals(3, socks.totalCount());
        assertEquals("black", socks.socks().get(0).getColor());
        assertEquals(35.0, socks.socks().get(0).getCottonPart());
        assertEquals("black", socks.socks().get(1).getColor());
        assertEquals(40.0, socks.socks().get(1).getCottonPart());
        assertEquals("black", socks.socks().get(2).getColor());
        assertEquals(75.0, socks.socks().get(2).getCottonPart());
    }

    @Test
    void uploadCsvTest() throws IOException {
        socksService.income(new SocksEditDto("firstCsv", 80.0, 10));
        MultipartFile file = createCsvFile();
        socksService.upload(file);

        Optional<Socks> socks = sockRepository.findByColorAndCottonPart("firstCsv", 80.0);
        assertTrue(socks.isPresent());
        assertEquals(110, socks.get().getQuantity());

        socks = sockRepository.findByColorAndCottonPart("secondCsv", 70.0);
        assertTrue(socks.isPresent());
        assertEquals(200, socks.get().getQuantity());

        socks = sockRepository.findByColorAndCottonPart("thirdCsv", 90.0);
        assertTrue(socks.isPresent());
        assertEquals(300, socks.get().getQuantity());
    }

    @Test
    void uploadXlsxTest() throws IOException {
        socksService.income(new SocksEditDto("firstXlsx", 80.0, 10));
        MultipartFile file = createXlsxFile();
        socksService.upload(file);

        Optional<Socks> socks = sockRepository.findByColorAndCottonPart("firstXlsx", 80.0);
        assertTrue(socks.isPresent());
        assertEquals(110, socks.get().getQuantity());

        socks = sockRepository.findByColorAndCottonPart("secondXlsx", 70.0);
        assertTrue(socks.isPresent());
        assertEquals(200, socks.get().getQuantity());

        socks = sockRepository.findByColorAndCottonPart("thirdXlsx", 90.0);
        assertTrue(socks.isPresent());
        assertEquals(300, socks.get().getQuantity());
    }

    @Test
    void uploadNegativeTest() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> socksService.upload(file));
        assertThrows(IllegalArgumentException.class, () -> socksService.upload(null));
    }


    private static MultipartFile createCsvFile() throws IOException {
        List<String> headers = Arrays.asList("color", "cottonPart", "quantity");
        List<List<String>> data = Arrays.asList(
                Arrays.asList("firstCsv", "80.0", "100"),
                Arrays.asList("secondCsv", "70.0", "200"),
                Arrays.asList("thirdCsv", "90.0", "300")
        );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(byteArrayOutputStream), CSVFormat.DEFAULT.withHeader(headers.toArray(new String[0])));

        for (List<String> row : data) {
            csvPrinter.printRecord(row);
        }
        csvPrinter.flush();

        return new MockMultipartFile("file", "test.csv", "text/csv", byteArrayOutputStream.toByteArray());
    }

    private static MultipartFile createXlsxFile() throws IOException {
        List<List<Object>> data = Arrays.asList(
                Arrays.asList("color", "cottonPart", "quantity"),
                Arrays.asList("firstXlsx", 80.0, 100),
                Arrays.asList("secondXlsx", 70.0, 200),
                Arrays.asList("thirdXlsx", 90.0, 300)
        );

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Socks");

        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex);
            List<Object> rowData = data.get(rowIndex);

            for (int colIndex = 0; colIndex < rowData.size(); colIndex++) {
                row.createCell(colIndex).setCellValue(rowData.get(colIndex).toString());
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        return new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", byteArrayOutputStream.toByteArray());
    }

}


