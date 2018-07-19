package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import com.google.common.collect.Lists;
import com.mr.framework.core.io.FileUtil;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.xls.importfile.FileImportExecutor;
import com.mr.modules.api.xls.importfile.domain.MapResult;
import com.mr.modules.api.xls.importfile.domain.common.Configuration;
import com.mr.modules.api.xls.importfile.domain.common.ImportCell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther zjxu
 * @DateTime 2018-07
 * 来源：信用中国（甘肃）
 * 主题：电子商务领域严重失信黑名单
 * 属性：企业名称、工商注册号、组织机构代码、统一信用代码、失信领域
 */
@Scope("prototype")
@Component("creditchina_gansu_blacklist_dzswlyyzsxhmd")
@Slf4j
public class CreditChina_GanSu_BlackList_DZSWLYYZSXHMD extends SiteTaskExtend_CreditChina{

    @Override
    protected String execute() throws Throwable {
        import91855();
        return null;
    }
    public void import91855(){
        List<Map<String,Object>>  listMaps = new ArrayList<>();
        String[] culmusList = {"enterpriseName","enterpriseCode2","enterpriseCode3","enterpriseCode1","punishResult"};

        try {
            String xlsFile_93512_1 = ResourceUtils.getFile("classpath:initxls/91855.xlsx").getAbsolutePath();
            listMaps = importFromXls(xlsFile_93512_1,culmusList);
            for(Map<String,Object> map : listMaps){
                map.put("source","信用中国（甘肃）");
                map.put("sourceUrl","http://www.gscredit.gov.cn/blackList/91855.jhtml");
                map.put("subject","电子商务领域严重失信黑名单");
                map.put("objectType","01");
                map.put("publishDate","2017/11/01");
                insertDiscreditBlacklist(map);
            }
        } catch (Exception e) {
            log.error("加载xlsx异常···请检查!"+e.getMessage());
        }
    }
    /**
     * 把excel导入，变成map
     *
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> importFromXls(String xlsName, String[] columeNames) throws Exception {
        File importFile = new File(xlsName);
        Configuration configuration = new Configuration();

        configuration.setStartRowNo(1);
        List<ImportCell> importCells = Lists.newArrayList();
        for (int i = 0; i < columeNames.length; i++) {
            importCells.add(new ImportCell(i, columeNames[i]));
        }
        configuration.setImportCells(importCells);
        configuration.setImportFileType(Configuration.ImportFileType.EXCEL);

        MapResult mapResult = (MapResult) FileImportExecutor.importFile(configuration, importFile, importFile.getName());
        List<Map<String, Object>> maps = mapResult.getResult();
        FileUtil.del(importFile);
        return maps;
    }
}
