package com.mr.common.util;

import com.google.common.collect.Lists;
import com.mr.modules.api.xls.importfile.FileImportExecutor;
import com.mr.modules.api.xls.importfile.domain.MapResult;
import com.mr.modules.api.xls.importfile.domain.common.Configuration;
import com.mr.modules.api.xls.importfile.domain.common.ImportCell;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author pxu 2018/7/20 11:01
 */
public class ExcelUtil {

    /**
     * 将excel文件内容生成List<Map<String, Object>>
     *
     * @param xlsPath     excel文件路径
     * @param columnNames 列名数组
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> importFromXls(String xlsPath, String[] columnNames) throws Exception {
        File importFile = new File(xlsPath);
        Configuration configuration = new Configuration();
        configuration.setStartRowNo(1);
        List<ImportCell> importCells = Lists.newArrayList();
        for (int i = 0; i < columnNames.length; i++) {
            importCells.add(new ImportCell(i, columnNames[i]));
        }
        configuration.setImportCells(importCells);
        configuration.setImportFileType(Configuration.ImportFileType.EXCEL);

        MapResult mapResult = (MapResult) FileImportExecutor.importFile(configuration, importFile, importFile.getName());
        return mapResult.getResult();
    }
}
