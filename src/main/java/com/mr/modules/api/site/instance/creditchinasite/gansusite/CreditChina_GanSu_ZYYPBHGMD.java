package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
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
 * @DataTime 2018-07
 * 主题：中药饮片不合格名单
 * 来源：信用中国甘肃
 * 属性：处罚机关、药品品名、标示生产企业或供货单位、生产批号、检品来源、检验依据、检验结果、不合格项目、检验机构、备注
 * 地址：http://www.gscredit.gov.cn/blackList/114802.jhtml
 */
@Slf4j
@Component("creditchina_gansu_zyypbhgmd")
@Scope("prototype")
public class CreditChina_GanSu_ZYYPBHGMD extends SiteTaskExtend_CreditChina{
    @Override
    protected String execute() throws Throwable {
        import114802();
        return super.execute();
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    public void import114802(){
        List<Map<String,Object>> listMaps = new ArrayList<>();
        String[] culmusList = {"drug","productGive","productNo","enterpriseName","validateAccording","punishReason","unregularProject","judgeAuth","note"};

        try {
            String xlsFile_114802 = ResourceUtils.getFile("classpath:initxls/114802.xlsx").getAbsolutePath();
            listMaps = importFromXls(xlsFile_114802,culmusList);
            for(Map<String,Object> map : listMaps){
                map.put("source","信用中国（甘肃）");
                map.put("sourceUrl","http://www.gscredit.gov.cn/blackList/114802.jhtml");
                map.put("subject","中药饮片不合格名单");
                map.put("objectType","01");
                map.put("publishDate","2018/01/05");
                insertDiscreditBlacklist(map);
            }
        } catch (Exception e) {
            log.warn("加载xlsx异常···请检查!"+e.getMessage());
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
