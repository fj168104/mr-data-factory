package com.mr.modules.api.site.instance.creditchinasite.gansusite;

import com.mr.common.util.ExcelUtil;
import com.mr.modules.api.site.SiteTaskExtend_CreditChina;
import com.mr.modules.api.site.instance.creditchinasite.CreditChinaSite;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * 信用中国（甘肃）-全国电子商务领域严重失信企业黑名单（第一批）
 * <p>
 * http://www.gscredit.gov.cn/blackList/91855.jhtml
 *
 * @author pxu 2018年7月20日
 */
@Slf4j
@Component("creditchina-gansu-black-91855")
@Scope("prototype")
public class CreditChinaGansuBlack91855 extends SiteTaskExtend_CreditChina {
    private String url = CreditChinaSite.GANSU.getBaseUrl() + "/blackList/91855.jhtml";

    /**
     * 抓取页面数据
     */
    @Override
    protected String execute() throws Throwable {
        //discreditBlacklistMapper.deleteAllByUrl(url);// 删除该URL下的全部数据
        log.info("开始导入url={}的图片数据", url);
        importFromExcel();
        log.info("导入url={}的图片数据结束！", url);
        return null;
    }

    /**
     * 从本地excel导入数据
     */
    public void importFromExcel() {

        String[] colNameList = {"enterpriseName", "enterpriseCode2", "enterpriseCode3", "enterpriseCode1", "discreditType"};
        try {
            //获取class路径下的资源
            ClassPathResource resource = new ClassPathResource("initxls/91855.xlsx");
            //创建临时目录
            String tempPath =System.getProperty("java.io.tmpdir") +"91855.xlsx";
            File f = new File(tempPath);
            IOUtils.copy(resource.getInputStream(),new FileOutputStream(f));

            //String xlsFile_91855 = ResourceUtils.getFile("classpath:initxls/91855.xlsx").getAbsolutePath();
            int lineNum = 0;
            List<Map<String, Object>> listMaps = ExcelUtil.importFromXls(tempPath, colNameList);
            for (Map<String, Object> map : listMaps) {
                if (map.get("enterpriseName").toString().contains("企业名称")) {
                    continue;
                }
                lineNum++;
                map.put("source", CreditChinaSite.GANSU.getSiteName());
                map.put("sourceUrl", url);
                map.put("subject", "电子商务领域严重失信黑名单");
                map.put("objectType", "01");
                map.put("publishDate", "2017/11/01");
                insertDiscreditBlacklist(map);
            }
            log.info("共计导入数据{}行", lineNum);
        } catch (Exception e) {
            log.warn("加载xls异常···请检查!", e);
        }
    }
}
