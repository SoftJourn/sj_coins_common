package com.softjourn.common.export;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.softjourn.common.utils.ReflectionUtil.tryToCastValue;
import static com.softjourn.common.utils.Util.instantToRFC_1123_DATE_TIME;

@Service
public class ExcelExport {

    /**
     * Method creates excel sheet and writes transactions into sheet
     *
     * @param name     - sheet name
     * @param entities - data
     * @param definers - defines data to be recorded
     * @return Workbook
     */
    public <T> Workbook export(String name, List<T> entities, List<ExportDefiner> definers)
            throws ReflectiveOperationException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createSheet(name);

        addHeader(workbook, name, 0, definers);

        addContent(workbook, name, 1, definers, entities);

        return workbook;
    }

    /**
     * Method adds sheet to existing workbook
     *
     * @param workbook - wordbook
     * @param name     - sheet name
     */
    public void addSheet(Workbook workbook, String name) {
        workbook.createSheet(name);
    }

    /**
     * Method adds divider to sheet(it is some text) and merges cells on which divider is situated
     *
     * @param workbook     - wordbook
     * @param sheetName    - sheetName
     * @param divider      - divider
     * @param rowNumber    - rowNumber
     * @param cellsToMerge - cellsToMerge
     */
    public void addDivider(Workbook workbook, String sheetName, String divider, Integer rowNumber, Integer cellsToMerge) {
        Sheet sheet = workbook.getSheet(sheetName);
        Row row = sheet.createRow(rowNumber);
        Cell cell = row.createCell(0);
        cell.setCellValue(divider);
        cell.setCellStyle(getDefaultStyle(workbook));
        sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, cellsToMerge));
    }

    /**
     * Method adds header into specific row
     *
     * @param workbook  - wordbook
     * @param sheetName - sheetName
     * @param rowNumber - rowNumber
     * @param definers  - definer
     */
    public void addHeader(Workbook workbook, String sheetName, Integer rowNumber, List<ExportDefiner> definers) {
        Sheet sheet = workbook.getSheet(sheetName);
        // header
        if (definers != null) {
            Row header = sheet.createRow(rowNumber);
            prepareHeaders(header, getDefaultStyle(workbook), 0, definers);
        }
    }

    /**
     * Method adds content to sheet
     *
     * @param workbook  - wordbook
     * @param sheetName - sheetName
     * @param rowNumber - rowNumber
     * @param definers  - definer
     * @param entities  - entities
     * @param <T>       - any entity
     * @return Integer - row number where method stopped adding new rows
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public <T> Integer addContent(Workbook workbook, String sheetName, Integer rowNumber,
                                  List<ExportDefiner> definers, List<T> entities) throws ReflectiveOperationException {
        Sheet sheet = workbook.getSheet(sheetName);

        int finalPosition = 0;
        int columns = 0;
        // main content
        if (entities != null) {
            finalPosition = rowNumber + entities.size();
            for (int i = 0; i < entities.size(); i++) {
                Row content = sheet.createRow(rowNumber + i);
                columns = prepareContent(content, getDefaultStyle(workbook), 0, definers, entities.get(i));
            }
        }

        // auto size columns width
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }

        return finalPosition;
    }

    /**
     * Method gets default style
     *
     * @param workbook - wordbook
     * @return CellStyle
     */
    public CellStyle getDefaultStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Calibri");

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);

        return style;
    }

    /**
     * Method prepares cell and sets data and style into cell
     *
     * @param row    - row where is needed to add content
     * @param style  - content style
     * @param column - column index
     * @param value  - value to set
     * @return Cell
     */
    private Cell prepareCell(Row row, CellStyle style, Integer column, Object value) {
        Cell cell = row.createCell(column);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Instant)
            cell.setCellValue(instantToRFC_1123_DATE_TIME((Instant) value, ZoneId.of("+0")));
        else if (value instanceof Number || value.getClass().isPrimitive())
            cell.setCellValue((Double) tryToCastValue(Double.class, value));
        else if (value.getClass().isInstance(true))
            cell.setCellValue((Boolean) tryToCastValue(Boolean.class, value));
        else cell.setCellValue(value.toString());

        cell.setCellStyle(style);

        return cell;
    }

    /**
     * Method adds header into row reading definer of entity
     *
     * @param row      - row where is needed to add content
     * @param style    - content style
     * @param index    - number of column from where is needed to start filling
     * @param definers - entity definer
     * @return -  int - is needed for recursion
     */
    private int prepareHeaders(Row row, CellStyle style, int index, List<ExportDefiner> definers) {
        for (ExportDefiner definer : definers) {
            if (definer.getDefiners().size() == 0 && definer.getHeader() != null) {
                prepareCell(row, style, index++, definer.getHeader());
            } else {
                index = prepareHeaders(row, style, index, definer.getDefiners());
            }
        }
        return index;
    }

    /**
     * Method adds content into row reading entity and definer of entity
     *
     * @param row      - row where is needed to add content
     * @param style    - content style
     * @param index    - number of column from where is needed to start filling
     * @param definers - entity definer
     * @param entity   - data
     * @param <T>      - data class
     * @return -  int - is needed for recursion
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private <T> int prepareContent(Row row, CellStyle style, int index, List<ExportDefiner> definers, T entity) throws ReflectiveOperationException {
        for (ExportDefiner definer : definers) {
            if (entity == null) {
                prepareCell(row, style, index++, null);
            } else {
                Object value = recognize(definer, entity);
                if (definer.getDefiners().size() == 0 && definer.getHeader() != null) {
                    prepareCell(row, style, index++, value);
                } else {
                    index = prepareContent(row, style, index, definer.getDefiners(), value);
                }
            }
        }
        return index;
    }

    /**
     * Method try to recognize method or field and gets value from there
     *
     * @param definer - definer
     * @param entity  - entity
     * @param <T>     - class
     * @return Object
     * @throws ReflectiveOperationException
     */
    private <T> Object recognize(ExportDefiner definer, T entity) throws ReflectiveOperationException {
        Class<?> aClass = entity.getClass();
        List<Field> fields = Arrays.asList(aClass.getDeclaredFields());
        Optional<Field> possibleField = fields.stream().filter(f -> f.getName().equals(definer.getName())).findFirst();
        if (possibleField.isPresent()) {
            Field field = possibleField.get();
            field.setAccessible(true);
            return field.get(entity);
        } else {
            try {
                if (definer.getParameters() == null) {
                    return aClass.getDeclaredMethod(definer.getName()).invoke(entity);
                } else {
                    return aClass.getDeclaredMethod(definer.getName(), definer.getClasses()).invoke(entity, definer.getParameters());
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                throw new ReflectiveOperationException("There is no such field or method!");
            }
        }
    }

}
