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
 * 主题：涉金融严重失信行为人名单
 * 属性：严重失信债务人名单、非法集资名单（企业）、非法集资名单（自然人）、其他严重违法名单（企业）、其他严重违法名单（自然人）、企业所在省份、企业名称、统一社会信用代码、组织机构代码、案号、执行法院、法定代表人、自然人所在省份、自然人姓名、自然人身份证号、罪名、案号、判决作出机构、

 */
@Scope("prototype")
@Component("creditchina_gansu_blacklist_sjryzsxxwmd")
@Slf4j
public class CreditChina_GanSu_BlackList_SJRYZSXXWMD extends SiteTaskExtend_CreditChina{

    @Override
    protected String execute() throws Throwable {
        import93512_1();
        import93512_2();
        import93512_3();
        import93512_4();
        return null;
    }
    public void import93512_1(){
        List<Map<String,Object>>  listMaps = new ArrayList<>();
        String[] culmusList = {"serialNo","province","enterpriseName","enterpriseCode1","enterpriseCode3","judgeNo","judgeAuth","personName"};

        try {
            String xlsFile_93512_1 = ResourceUtils.getFile("classpath:initxls/93512_1.xlsx").getAbsolutePath();
            listMaps = importFromXls(xlsFile_93512_1,culmusList);
            for(Map<String,Object> map : listMaps){
                map.put("source","信用中国（甘肃）");
                map.put("sourceUrl","http://www.gscredit.gov.cn/blackList/93512.jhtml");
                map.put("subject","涉金融严重失信行为人名单");
                map.put("objectType","01");
                map.put("publishDate","2017/11/15");
                insertDiscreditBlacklist(map);
            }
        } catch (Exception e) {
            log.warn("加载xlsx异常···请检查!"+e.getMessage());
        }
    }
    public void import93512_2(){
        List<Map<String,Object>>  listMaps = new ArrayList<>();
        String[] culmusList = {"serialNo","province","personName","persionId","punishReason","judgeNo","judgeAuth"};

        try {
            String xlsFile_93512_2 = ResourceUtils.getFile("classpath:initxls/93512_2.xlsx").getAbsolutePath();
            listMaps = importFromXls(xlsFile_93512_2,culmusList);
            for(Map<String,Object> map : listMaps){
                map.put("source","信用中国（甘肃）");
                map.put("sourceUrl","http://www.gscredit.gov.cn/blackList/93512.jhtml");
                map.put("subject","涉金融严重失信行为人名单");
                map.put("objectType","02");
                map.put("publishDate","2017/11/15");
                insertDiscreditBlacklist(map);
            }
        } catch (Exception e) {
            log.warn("加载xlsx异常···请检查!"+e.getMessage());
        }
    }
    public void import93512_3(){
        List<Map<String,Object>>  listMaps = new ArrayList<>();
        String[] culmusList = {"serialNo","province","enterpriseName","enterpriseCode1","enterpriseCode3","punishReason","judgeNo","judgeAuth"};

        try {
            String xlsFile_93512_3 = ResourceUtils.getFile("classpath:initxls/93512_3.xlsx").getAbsolutePath();
            listMaps = importFromXls(xlsFile_93512_3,culmusList);
            for(Map<String,Object> map : listMaps){
                map.put("source","信用中国（甘肃）");
                map.put("sourceUrl","http://www.gscredit.gov.cn/blackList/93512.jhtml");
                map.put("subject","涉金融严重失信行为人名单");
                map.put("objectType","01");
                map.put("publishDate","2017/11/15");
                insertDiscreditBlacklist(map);
            }
        } catch (Exception e) {
            log.warn("加载xlsx异常···请检查!"+e.getMessage());
        }
    }
    public void import93512_4(){
        List<Map<String,Object>>  listMaps = new ArrayList<>();
        String[] culmusList = {"serialNo","province","enterpriseName","enterpriseCode1","enterpriseCode3","punishReason","judgeNo","judgeAuth","personName"};

        try {
            String xlsFile_93512_4 = ResourceUtils.getFile("classpath:initxls/93512_4.xlsx").getAbsolutePath();
            listMaps = importFromXls(xlsFile_93512_4,culmusList);
            for(Map<String,Object> map : listMaps){
                map.put("source","信用中国（甘肃）");
                map.put("sourceUrl","http://www.gscredit.gov.cn/blackList/93512.jhtml");
                map.put("subject","涉金融严重失信行为人名单");
                map.put("objectType","01");
                map.put("publishDate","2017/11/15");
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
