package com.softjourn.common.export;

import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;

public interface ExportService {

  <T> Workbook export(
      String name, List<T> entities, List<ExportDefiner> definers
  ) throws NoSuchFieldException, IllegalAccessException;
}
