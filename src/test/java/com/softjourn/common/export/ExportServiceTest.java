package com.softjourn.common.export;

import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ExportServiceTest {

    private ExcelExport excelExport;

    private List<ExportDefiner> definers;

    private List<ExportDefiner> primitives;

    private List<Object> entities;

    @Before
    public void setUp() {

        definers = new ArrayList<>();
        ExportDefiner account = new ExportDefiner("account", null);
        account.getDefiners().add(new ExportDefiner("fullName", "Account"));

        ExportDefiner destination = new ExportDefiner("destination", null);
        destination.getDefiners().add(new ExportDefiner("fullName", "Destination"));

        definers.add(account);
        definers.add(new ExportDefiner("amount", "Amount"));
        definers.add(new ExportDefiner("comment", "Comment"));
        definers.add(new ExportDefiner("created", "Created"));
        definers.add(destination);
        definers.add(new ExportDefiner("error", "Error"));
        definers.add(new ExportDefiner("status", "Status"));
        definers.add(new ExportDefiner("type", "Type"));

        primitives = new ArrayList<>();

        primitives.add(new ExportDefiner("byteP", "Byte"));
        primitives.add(new ExportDefiner("charP", "Character"));
        primitives.add(new ExportDefiner("shortP", "Short"));
        primitives.add(new ExportDefiner("intP", "Integer"));
        primitives.add(new ExportDefiner("longP", "Long"));
        primitives.add(new ExportDefiner("booleanP", "Boolean"));

        entities = new ArrayList<>();

        Transaction transaction = new Transaction();

        Account account1 = new Account();
        account1.setFullName("full name");

        transaction.setAccount(account1);
        transaction.setDestination(account1);
        transaction.setComment("comment");
        transaction.setType("TRANSFER");
        transaction.setAmount(new BigDecimal(100));
        transaction.setError("error");
        transaction.setStatus("SUCCESS");
        transaction.setCreated(LocalDateTime.of(2017, 4, 28, 17, 30, 30).toInstant(ZoneOffset.UTC));

        entities.add(transaction);

        excelExport = new ExcelExport();
    }

    @Test
    public void checkHeadersTest() throws ReflectiveOperationException {
        int headersCount = 8;

        Workbook workbook = excelExport.export("some", null, definers);

        assertEquals("some", workbook.getSheetName(0));
        assertEquals(headersCount, workbook.getSheetAt(0).getRow(0).getLastCellNum());

        assertEquals("Account", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        assertEquals("Amount", workbook.getSheetAt(0).getRow(0).getCell(1).getStringCellValue());
        assertEquals("Comment", workbook.getSheetAt(0).getRow(0).getCell(2).getStringCellValue());
        assertEquals("Created", workbook.getSheetAt(0).getRow(0).getCell(3).getStringCellValue());
        assertEquals("Destination", workbook.getSheetAt(0).getRow(0).getCell(4).getStringCellValue());
        assertEquals("Error", workbook.getSheetAt(0).getRow(0).getCell(5).getStringCellValue());
        assertEquals("Status", workbook.getSheetAt(0).getRow(0).getCell(6).getStringCellValue());
        assertEquals("Type", workbook.getSheetAt(0).getRow(0).getCell(7).getStringCellValue());
    }

    @Test
    public void checkContentTest() throws ReflectiveOperationException {
        int headersCount = 8;

        Workbook workbook = excelExport.export("some", entities, definers);

        assertEquals("some", workbook.getSheetName(0));
        assertEquals(headersCount, workbook.getSheetAt(0).getRow(0).getLastCellNum());

        assertEquals("full name", workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue());
        assertEquals(100, workbook.getSheetAt(0).getRow(1).getCell(1).getNumericCellValue(), 0);
        assertEquals("comment", workbook.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
        assertEquals("Fri, 28 Apr 2017 17:30:30 GMT", workbook.getSheetAt(0).getRow(1).getCell(3).getStringCellValue());
        assertEquals("full name", workbook.getSheetAt(0).getRow(1).getCell(4).getStringCellValue());
        assertEquals("error", workbook.getSheetAt(0).getRow(1).getCell(5).getStringCellValue());
        assertEquals("SUCCESS", workbook.getSheetAt(0).getRow(1).getCell(6).getStringCellValue());
        assertEquals("TRANSFER", workbook.getSheetAt(0).getRow(1).getCell(7).getStringCellValue());
    }

    @Test
    public void checkPrimitivesTest() throws ReflectiveOperationException {
        int headersCount = primitives.size();

        Workbook workbook = excelExport.export("some", new ArrayList<Primitives>() {{
            add(new Primitives());
        }}, primitives);

        assertEquals("some", workbook.getSheetName(0));
        assertEquals(headersCount, workbook.getSheetAt(0).getRow(0).getLastCellNum());

        assertEquals(0, workbook.getSheetAt(0).getRow(1).getCell(0).getNumericCellValue(), 0);
        assertEquals("a", workbook.getSheetAt(0).getRow(1).getCell(1).getStringCellValue());
        assertEquals(0, workbook.getSheetAt(0).getRow(1).getCell(2).getNumericCellValue(), 0);
        assertEquals(0, workbook.getSheetAt(0).getRow(1).getCell(3).getNumericCellValue(), 0);
        assertEquals(0, workbook.getSheetAt(0).getRow(1).getCell(4).getNumericCellValue(), 0);
        assertEquals(true, workbook.getSheetAt(0).getRow(1).getCell(5).getBooleanCellValue());
    }

    @Test
    public void getUsingMethodTest() throws ReflectiveOperationException {
        List<ExportDefiner> definers = new ArrayList<>();

        definers.add(new ExportDefiner("getComment", "Comment"));
        definers.add(new ExportDefiner("getSomething", "Something", new Class[]{String.class}, "Something"));
        int headersCount = 2;

        Workbook workbook = excelExport.export("some", entities, definers);

        assertEquals("some", workbook.getSheetName(0));
        assertEquals(headersCount, workbook.getSheetAt(0).getRow(0).getLastCellNum());
    }

    @Test
    public void addSheetTest() {
        Workbook workbook = new HSSFWorkbook();
        String sheetName = "sheet";
        ExcelExport excelExport = new ExcelExport();
        excelExport.addSheet(workbook, sheetName);

        assertEquals(sheetName, workbook.getSheetName(0));
    }

    @Test
    public void addDividerTest() {
        Workbook workbook = new HSSFWorkbook();
        String sheetName = "sheet";
        String divider = "divider";
        ExcelExport excelExport = new ExcelExport();
        excelExport.addSheet(workbook, sheetName);
        excelExport.addDivider(workbook, sheetName, divider, 0, 1);

        assertEquals(sheetName, workbook.getSheetName(0));
        assertEquals(divider, workbook.getSheet(sheetName).getRow(0).getCell(0).getStringCellValue());
    }

    @Test
    public void addHeaderTest() {
        Workbook workbook = new HSSFWorkbook();
        String sheetName = "sheet";
        int headersCount = 8;
        ExcelExport excelExport = new ExcelExport();
        excelExport.addSheet(workbook, sheetName);
        excelExport.addHeader(workbook, sheetName, 0, definers);

        assertEquals(sheetName, workbook.getSheetName(0));
        assertEquals(headersCount, workbook.getSheetAt(0).getRow(0).getLastCellNum());

        assertEquals("Account", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        assertEquals("Amount", workbook.getSheetAt(0).getRow(0).getCell(1).getStringCellValue());
        assertEquals("Comment", workbook.getSheetAt(0).getRow(0).getCell(2).getStringCellValue());
        assertEquals("Created", workbook.getSheetAt(0).getRow(0).getCell(3).getStringCellValue());
        assertEquals("Destination", workbook.getSheetAt(0).getRow(0).getCell(4).getStringCellValue());
        assertEquals("Error", workbook.getSheetAt(0).getRow(0).getCell(5).getStringCellValue());
        assertEquals("Status", workbook.getSheetAt(0).getRow(0).getCell(6).getStringCellValue());
        assertEquals("Type", workbook.getSheetAt(0).getRow(0).getCell(7).getStringCellValue());
    }

    @Test
    public void addContentTest() throws ReflectiveOperationException {
        Workbook workbook = new HSSFWorkbook();
        String sheetName = "sheet";
        int headersCount = 8;
        ExcelExport excelExport = new ExcelExport();
        excelExport.addSheet(workbook, sheetName);
        excelExport.addContent(workbook, sheetName, 0, definers, entities);

        assertEquals(sheetName, workbook.getSheetName(0));
        assertEquals(headersCount, workbook.getSheetAt(0).getRow(0).getLastCellNum());

        assertEquals("full name", workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
        assertEquals(100, workbook.getSheetAt(0).getRow(0).getCell(1).getNumericCellValue(), 0);
        assertEquals("comment", workbook.getSheetAt(0).getRow(0).getCell(2).getStringCellValue());
        assertEquals("Fri, 28 Apr 2017 17:30:30 GMT", workbook.getSheetAt(0).getRow(0).getCell(3).getStringCellValue());
        assertEquals("full name", workbook.getSheetAt(0).getRow(0).getCell(4).getStringCellValue());
        assertEquals("error", workbook.getSheetAt(0).getRow(0).getCell(5).getStringCellValue());
        assertEquals("SUCCESS", workbook.getSheetAt(0).getRow(0).getCell(6).getStringCellValue());
        assertEquals("TRANSFER", workbook.getSheetAt(0).getRow(0).getCell(7).getStringCellValue());
    }

    @Data
    class Primitives {

        private byte byteP = 0;
        private char charP = 'a';
        private short shortP = 0;
        private int intP = 0;
        private long longP = 0;
        private boolean booleanP = true;
    }

    @Data
    class Transaction {
        private Long id;

        private Account account;

        private Account destination;

        private BigDecimal amount;

        private String comment;

        private Instant created;

        private String status;

        private String type;

        private BigDecimal remain;

        private String error;

        private String erisTransactionId;

        public String getSomething(String value) {
            return value;
        }

    }

    @Data
    class Account {
        private String fullName;
    }

}
