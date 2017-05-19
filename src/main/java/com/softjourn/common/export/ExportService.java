package com.softjourn.common.export;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface ExportService {

    <T> Workbook export(String name, List<T> entities, List<ExportDefiner> definers) throws NoSuchFieldException, IllegalAccessException;

}
