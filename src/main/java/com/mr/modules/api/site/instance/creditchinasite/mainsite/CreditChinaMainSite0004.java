package com.mr.modules.api.site.instance.creditchinasite.mainsite;

import com.google.common.collect.Lists;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.io.FileUtil;
import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.xls.importfile.FileImportExecutor;
import com.mr.modules.api.xls.importfile.domain.MapResult;
import com.mr.modules.api.xls.importfile.domain.common.Configuration;
import com.mr.modules.api.xls.importfile.domain.common.ImportCell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @auther
 * 1.信用中国主站
 * 2.url:http://www.creditchina.gov.cn/xinxigongshi/?navPage=4
 * 3.需求：2017 年第一季度国家重点监控企业主要污染物排放严重超标和处罚情况
 * 4.提取内容：企业名称、行政区划、处罚情况、整改情况、日期
 */
@Slf4j
@Component("creditchinamainsite0004")
@Scope("prototype")
public class CreditChinaMainSite0004 extends SiteTaskExtend_CreditChina {
    protected OCRUtil ocrUtil = SpringUtils.getBean(OCRUtil.class);
    @Autowired
    AdminPunishMapper adminPunishMapper;

    String url ="http://www.creditchina.gov.cn/xinxigongshi/huanbaolingyu/201804/t20180418_113468.html";
    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    @Override
    protected String execute() throws Throwable {
        importCreditchinamainsite0004();
        return null;
    }


    public void importCreditchinamainsite0004(){
        List<Map<String,Object>> listMaps = new ArrayList<>();
        String[] culmusList = {"subject","administrativeArea","enterpriseName","punishResult","updateResult"};

        try {
            //获取class路径下的资源
            ClassPathResource resource = new ClassPathResource("initxls/chinamainsite0004.xlsx");
            //创建临时目录
            String tempPath =System.getProperty("java.io.tmpdir")+"chinamainsite0004.xlsx";
            File f = new File(tempPath);
            InputStream  inputStream = resource.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            IOUtils.copy(inputStream,fileOutputStream);
            inputStream.close();
            fileOutputStream.close();

            listMaps = importFromXls(tempPath,culmusList);

            for( int i=0;i<listMaps.size();i++){
                Map<String,String> map = new HashMap<>();
                map.put("subject",listMaps.get(i).get("subject").toString());
                map.put("enterpriseName",listMaps.get(i).get("enterpriseName").toString());
                map.put("punishResult",listMaps.get(i).get("punishResult").toString());
                map.put("source","信用中国");
                map.put("sourceUrl",url);
                map.put("objectType","01");
                map.put("publishDate","2018/04/18");
                map.put("judgeAuth","生态环境部");
                adminPunishInsert(map);
            }

        } catch (Exception e) {
            log.warn("加载xlsx异常···请检查!"+e.getMessage());
            e.printStackTrace();
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
