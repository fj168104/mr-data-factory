package com.mr.modules.api.site;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mr.common.IdempotentOperator;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.date.DateUtil;
import com.mr.framework.core.io.FileUtil;
import com.mr.framework.core.util.RandomUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.framework.json.JSONArray;
import com.mr.framework.json.JSONObject;
import com.mr.framework.json.JSONUtil;
import com.mr.modules.api.mapper.FinanceMonitorPunishMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.xls.export.FileExportor;
import com.mr.modules.api.xls.export.domain.common.ExportCell;
import com.mr.modules.api.xls.export.domain.common.ExportConfig;
import com.mr.modules.api.xls.export.domain.common.ExportResult;
import com.mr.modules.api.xls.importfile.FileImportExecutor;
import com.mr.modules.api.xls.importfile.domain.MapResult;
import com.mr.modules.api.xls.importfile.domain.common.Configuration;
import com.mr.modules.api.xls.importfile.domain.common.ImportCell;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feng on 18-3-16
 */

@Slf4j
@Component
public abstract class SiteTaskExtend extends SiteTask {

	private static String XLS_EXPORT_PATH = OCRUtil.DOWNLOAD_DIR + File.separator + "export";

	protected RestTemplate restTemplate = SpringUtils.getBean(RestTemplate.class);

	private RestTemplate noProxyRestTemplate;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@PostConstruct
	public void initNoProxyRestTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		noProxyRestTemplate = restTemplateBuilder
				.setReadTimeout(30000) //ms
				.setConnectTimeout(15000) //ms
				.requestFactory(factory)
				.build();

	}

	@Value("${ic-url}")
	private String icQueryUrl;

	//自增ID
	private AtomicInteger incrementer = new AtomicInteger(RandomUtil.randomInt(100000));

	private volatile String currentDate = DateUtil.format(DateUtil.date(), "yyyyMMdd");

	@Autowired
	protected FinanceMonitorPunishMapper financeMonitorPunishMapper;

	/**
	 * 适配单条处理, 子类需要覆盖此方法
	 *
	 * @return
	 * @throws Throwable
	 */
	@Override
	protected String executeOne() throws Throwable {
		return null;
	}

	/**
	 * 带着重试次数
	 *
	 * @param url
	 * @return
	 */
	protected String getData(String url, int retryTimes) {
		return new IdempotentOperator<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return restTemplate.getForObject(url, String.class);
			}
		}).execute(retryTimes);
	}

	/**
	 * GET 请求
	 *
	 * @param url
	 * @return
	 */
	protected String getData(String url) {
		return new IdempotentOperator<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return restTemplate.getForObject(url, String.class);
			}
		}).execute();
	}

	protected String getData(String url, String charSet) {
		return new IdempotentOperator<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return new String(restTemplate.getForObject(url, byte[].class),
						Charset.forName(charSet.toUpperCase()));
			}
		}).execute();
	}

	protected String getData(String url, Charset charSet) {
		return new IdempotentOperator<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return new String(restTemplate.getForObject(url, byte[].class), charSet);
			}
		}).execute();
	}

	protected String getData(String url, Map<String, String> requestParams) {
		return new IdempotentOperator<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return restTemplate.getForObject(url + showParams(requestParams), String.class);
			}
		}).execute();
	}

	protected String getData(String url, Map<String, String> requestParams, Map<String, String> headParams) {

		return new IdempotentOperator<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				HttpHeaders requestHeaders = new HttpHeaders();
				if (headParams.size() > 0) {
					for (Map.Entry<String, String> entry : headParams.entrySet()) {
						requestHeaders.add(entry.getKey(), entry.getValue());
					}
				}
				HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
				ResponseEntity<String> response = restTemplate.exchange(url + showParams(requestParams), HttpMethod.GET, requestEntity, String.class);
				return response.getBody();
			}
		}).execute();
	}

	private String showParams(Map<String, String> requestParams) {
		if (requestParams == null || requestParams.size() == 0) return "";

		StringBuilder sb = new StringBuilder();
		for (Map.Entry entry : requestParams.entrySet()) {

			if (!sb.toString().equals("")) {
				sb.append("&");
			}
			sb.append(entry.getKey() + "=" + entry.getValue());
		}

		return "?" + sb;
	}

	protected String postData(String url, Map<String, String> requestParams, Map<String, String> headParams) {
		HttpHeaders headers = new HttpHeaders();
		//  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		if (headParams.size() > 0) {
			for (Map.Entry<String, String> entry : headParams.entrySet()) {
				headers.add(entry.getKey(), entry.getValue());
			}
		}

		//  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		//  也支持中文
		params.setAll(requestParams);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
		//  执行HTTP请求
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		//	输出结果
		return response.getBody();
	}

	/**
	 * POST 请求
	 *
	 * @param url
	 * @param requestParams
	 * @return
	 */
	protected String postData(String url, Map<String, String> requestParams) {
		return postData(url, requestParams, Maps.newHashMap());
	}


	/**
	 * POST 请求
	 *
	 * @param url
	 * @param requestParams
	 * @param retryTimes
	 * @return
	 */
	protected String postData(String url, Map<String, String> requestParams, int retryTimes) {
		return new IdempotentOperator<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return postData(url, requestParams, Maps.newHashMap());
			}
		}).execute(retryTimes);
	}


	protected String downLoadFile(String targetUri) throws IOException, URISyntaxException {
		return downLoadFile(targetUri, null);
	}

	/**
	 * @param targetUri
	 * @return 文件名
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	protected String downLoadFile(String targetUri, String fName) throws URISyntaxException, IOException {
		String fileName = targetUri.substring(targetUri.lastIndexOf("/") + 1);
		if (!Objects.isNull(fName))
			fileName = fName;
//		String targetUri = "http://www.neeq.com.cn/uploads/1/file/public/201802/20180226182405_lc6vjyqntd.pdf";
		// 小文件
		RequestEntity requestEntity = RequestEntity.get(new URI(targetUri)).build();
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
		byte[] downloadContent = responseEntity.getBody();
		OutputStream out = new FileOutputStream(OCRUtil.DOWNLOAD_DIR + File.separator + fileName);
		out.write(downloadContent);
		out.close();
		return fileName;

		// 大文件
		//		FileOutputStream f=new FileOutputStream()
		//		ResponseExtractor<ResponseEntity<File>> responseExtractor = new ResponseExtractor<ResponseEntity<File>>() {
		//			@Override
		//			public ResponseEntity<File> extractData(ClientHttpResponse response) throws IOException {
		//				File rcvFile = File.createTempFile("rcvFile", "zip");
		//				FileCopyUtils.copy(response.getBody(), new FileOutputStream(rcvFile));
		//				return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(rcvFile);
		//			}
		//		};
		//		ResponseEntity<File> getFile = restTemplate.execute(targetUri, HttpMethod.GET, (RequestCallback) null, responseExtractor);

	}

	/**
	 * 提取原字符串中的中文内容
	 *
	 * @param s
	 * @return
	 */
	protected String extracterZH(String s) {
		String str = s;
		String reg = "[^\u4e00-\u9fa5]";
		str = str.replaceAll(reg, "");

		return str;
	}

	/**
	 * 设置过滤字符，
	 * 用法：
	 * List<String> keywords = new ArrayList<String>();
	 * keywords.add("宋体");
	 * keywords.add("黑体");
	 * keywords.add("楷体");
	 * keywords.add("仿宋");
	 * keywords.add("普通表格");
	 * keywords.add("当前位置");
	 * keywords.add("首页");
	 * keywords.add("政务公开");
	 * keywords.add("发布时间");
	 * keywords.add("页脚");
	 * keywords.add("页眉");
	 * keywords.add("页码");
	 * keywords.add("年月日日期版权所有中国银行业监督管理委员会备号访问量次当前页访问量次");
	 *
	 * @param src
	 * @param keywords 需要过滤的字符
	 * @return
	 */
	protected String filter(String src, List<String> keywords) {
		String to = src;


		for (String key : keywords) {
			to = to.replace(key, "");
		}
		return to;
	}


	/**
	 * 导出为Excel
	 */
	protected int exportToXls(String xlsName, List<?> siteObjects) throws Exception {
		FileUtil.mkdir(XLS_EXPORT_PATH);
		ExportConfig exportConfig = new ExportConfig();
		exportConfig.setFileName(xlsName);

		List<ExportCell> exportCells = Lists.newArrayList();
		if (siteObjects.get(0) instanceof LinkedHashMap) {
			for (String key : ((LinkedHashMap<String, String>) siteObjects.get(0)).keySet()) {
				exportCells.add(new ExportCell(key));
			}
		} else {
			for (Field field : siteObjects.get(0).getClass().getDeclaredFields()) {
				exportCells.add(new ExportCell(field.getName()));
			}
		}


		exportConfig.setExportCells(exportCells);

		ExportResult exportResult = FileExportor.getExportResult(exportConfig, siteObjects);
		//输出文件在d盘根目录，系统是win
//        OutputStream outputStream = new FileOutputStream("d://output.xlsx");
		//系统mac
		OutputStream outputStream = new FileOutputStream(XLS_EXPORT_PATH + File.separator + xlsName);
		exportResult.export(outputStream);
		return 1;
	}

	/**
	 * 把excel导入，变成map
	 *
	 * @throws Exception
	 */
	public List<Map<String, Object>> importFromXls(String xlsName, String[] columeNames) throws Exception {
		File importFile = new File(OCRUtil.DOWNLOAD_DIR + File.separator + xlsName);
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

	/**
	 * 设置业务主键 格式：punish_no|punish_title|punish_institution|punish_date
	 *
	 * @return primaryKey
	 */
	public static String buildFinanceMonitorPunishBizKey(FinanceMonitorPunish financeMonitorPunish) {
		String punishNo = StringUtils.isEmpty(financeMonitorPunish.getPunishNo())
				? "NULL" : financeMonitorPunish.getPunishNo();
		String punishTitle = StringUtils.isEmpty(financeMonitorPunish.getPunishTitle())
				? "NULL" : financeMonitorPunish.getPunishTitle();
		String punishDate = StringUtils.isEmpty(financeMonitorPunish.getPunishDate())
				? "NULL" : financeMonitorPunish.getPunishDate();
//		String party = StringUtils.isEmpty(financeMonitorPunish.getPartyPerson())
//				? financeMonitorPunish.getPartyInstitution() : financeMonitorPunish.getPartyPerson();
//		party = StringUtils.isEmpty(party) ? "NULL" : party;

		String punishInstitution = StringUtils.isEmpty(financeMonitorPunish.getPunishInstitution())
				? "NULL" : financeMonitorPunish.getPunishInstitution();

		financeMonitorPunish.setPrimaryKey(String.format("%s|%s|%s|%s",
				punishNo, punishTitle, punishInstitution, punishDate));

		return financeMonitorPunish.getPrimaryKey();
	}

	/**
	 * 设置单条抓取的更新日期
	 */
	protected void initDate() {
		try {
			//通过source查找
			FinanceMonitorPunish originFinanceMonitorPunish = financeMonitorPunishMapper
					.selectByUrl(oneFinanceMonitorPunish.getUrl());
			if (Objects.isNull(originFinanceMonitorPunish)) {
				oneFinanceMonitorPunish.setCreateTime(new Date());
				oneFinanceMonitorPunish.setUpdateTime(new Date());
			} else {
				oneFinanceMonitorPunish.setCreateTime(originFinanceMonitorPunish.getCreateTime());
				oneFinanceMonitorPunish.setUpdateTime(new Date());
			}

			//通过url先删除，确保不产生多余数据
			financeMonitorPunishMapper.deleteByUrl(oneFinanceMonitorPunish.getUrl());
		} catch (Exception e) {
			log.error(keyWords + ">>>" + e.getMessage());
		}

	}

	/**
	 * 保存抓取结果
	 *
	 * @return ture 保存成功		false 保存失败（如系统中已存在该记录）
	 */
	protected Boolean saveOne(FinanceMonitorPunish financeMonitorPunish, Boolean isForce) {
		String primaryKey = buildFinanceMonitorPunishBizKey(financeMonitorPunish);
		log.debug("primaryKey:" + primaryKey);
		try {
			if (isForce || Objects.isNull(financeMonitorPunishMapper.selectByUrl(financeMonitorPunish.getUrl()))) {
				//获取工商名
				setICName(financeMonitorPunish);
				insertOrUpdate(financeMonitorPunish);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.warn("saveOne warn >>>" + e.getMessage() + "\n financeMonitorPunish>>>" + financeMonitorPunish);
		}
		return true;
	}

	/**
	 * 调用工商信息查询接口，设置工商信息名
	 */
	protected void setICName(FinanceMonitorPunish financeMonitorPunish) {
		if (StrUtil.isBlank(financeMonitorPunish.getPartyInstitution())) {
			financeMonitorPunish.setCompanyFullName("NULL");
			return;
		}
		//将partyInstitution 名字中的英文括号改为中文括号
		financeMonitorPunish.setPartyInstitution(
				financeMonitorPunish.getPartyInstitution()
						.replace("(", "（")
						.replace(")", "）")
		);

		JSONObject jsonObject = fetchDataByICUrl(financeMonitorPunish.getPartyInstitution());
		String code = jsonObject.get("code", String.class);
		if (code.equals("0000")) {
			JSONArray array = jsonObject.getJSONArray("data");
			if (array.size() == 1) {
				financeMonitorPunish.setCompanyFullName(array.getJSONObject(0).get("orgName", String.class));
			} else {
				financeMonitorPunish.setCompanyFullName("NULL");
				for (int i = 0; i < array.size(); i++) {
					String orgName = array.getJSONObject(0).get("orgName", String.class);
					if (orgName.equals(financeMonitorPunish.getPartyInstitution())) {
						financeMonitorPunish.setCompanyFullName(orgName);
						break;
					}
				}
				if (financeMonitorPunish.getCompanyFullName().equals("NULL")) {
					writeIcFailLog(financeMonitorPunish.getUrl(), financeMonitorPunish.getPartyInstitution(), array.toString());
				}

			}

		} else {
			financeMonitorPunish.setCompanyFullName("NULL");
			writeIcFailLog(financeMonitorPunish.getUrl(), financeMonitorPunish.getPartyInstitution(), jsonObject.get("msg", String.class));
		}

	}

	/**
	 * 通过工商URL获取数据
	 *
	 * @return
	 */
	private JSONObject fetchDataByICUrl(String partyInstitution) {
		Map<String, String> map = Maps.newHashMap();

		String json = "{" + String.format("\"key\":\"%s\"", partyInstitution) + "}";
		map.put("uid", "sd0001");
		map.put("msgid", getMsgId());
		map.put("api", "N001_QY00100_V001");
		map.put("args", "{json}");
		map.put("querymode", "0");
		String jsonStr = noProxyRestTemplate.getForObject(icQueryUrl + showParams(map), String.class, json);

		JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
		String code = jsonObject.get("code", String.class);
		//msgId重复
		if (code.equals("0112")) {
			incrementer.set(incrementer.intValue() + 100);
			jsonObject = fetchDataByICUrl(partyInstitution);
		}
		return jsonObject;

	}


	/**
	 * 写工商转化错误日志
	 *
	 * @param icMsg
	 */
	private void writeIcFailLog(String url, String partyInstitution, String icMsg) {
		String icPath = OCRUtil.DOWNLOAD_DIR + "/ic_fail.txt";
		if (FileUtil.exist(icPath)) {
			String icContent = FileUtil.readString(icPath, "utf-8");
			//不重复记录
			if (icContent.contains(url)) return;
		}


		BufferedWriter bw = FileUtil.getWriter(icPath, "utf-8", true);
		try {
			bw.write(url + "\t" + partyInstitution + "\t" + icMsg + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!Objects.isNull(bw)) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}


	private String getMsgId() {
		String ss = "014014";
		String yy = "21";
		String date = DateUtil.format(DateUtil.date(), "yyyyMMdd");

		String creNo = String.format("%010d", incrementer.incrementAndGet());
		ss += date + yy + creNo;
		if (!date.equals(currentDate)) {
			synchronized (this) {
				currentDate = date;
				incrementer.set(110000000);
			}

		}
		return ss;

	}

	/**
	 * 保存单条记录
	 *
	 * @param financeMonitorPunish
	 * @return
	 */
	private FinanceMonitorPunish insertOrUpdate(FinanceMonitorPunish financeMonitorPunish) {
		if (StringUtils.isEmpty(financeMonitorPunish.getPrimaryKey())) {
			buildFinanceMonitorPunishBizKey(financeMonitorPunish);
		}
		//punishDate 过滤
		handlePunishDate(financeMonitorPunish);

		try {
			financeMonitorPunishMapper.deleteByBizKey(financeMonitorPunish.getPrimaryKey());
			//设置createTime
			if (StringUtils.isEmpty(financeMonitorPunish.getCreateTime())) {
				financeMonitorPunish.setCreateTime(new Date());
				financeMonitorPunish.setUpdateTime(new Date());
			}
			if (StrUtil.isEmpty(financeMonitorPunish.getCompanyFullName())) {
				financeMonitorPunish.setCompanyFullName(financeMonitorPunish.getPartyInstitution());
			}
			financeMonitorPunishMapper.insert(filterPlace(financeMonitorPunish));
		} catch (Exception e) {
			log.error(keyWords + ">>>" + e.getMessage());
		}
		return financeMonitorPunish;
	}

	protected void handlePunishDate(FinanceMonitorPunish financeMonitorPunish){
		if (StrUtil.isNotEmpty(financeMonitorPunish.getPunishDate())) {
			String pDate = financeMonitorPunish.getPunishDate().replaceAll("\\s*", "");
			if (extracterZH(pDate).equals("年月日")) {
				pDate = pDate.replace("年", "-").replace("月", "-").replace("日", "");
				Date date = DateUtil.parse(pDate, "yyyy-MM-dd");
				String format = DateUtil.format(date, "yyyy-MM-dd");
				financeMonitorPunish.setPunishDate(format);
			} else if (pDate.contains("〇")) {
				String sYear = pDate.substring(0, pDate.indexOf("年"));
				String tyear = sYear.replace("一", "1")
						.replace("二", "2")
						.replace("三", "3")
						.replace("四", "4")
						.replace("五", "5")
						.replace("六", "6")
						.replace("七", "7")
						.replace("八", "8")
						.replace("九", "9")
						.replace("〇", "0");

				String sMonth = pDate.substring(pDate.indexOf("年") + 1, pDate.indexOf("月"));
				String tMonth = sMonth.replace("十一", "11")
						.replace("十二", "12")
						.replace("一", "1")
						.replace("二", "2")
						.replace("三", "3")
						.replace("四", "4")
						.replace("五", "5")
						.replace("六", "6")
						.replace("七", "7")
						.replace("八", "8")
						.replace("九", "9")
						.replace("十", "10");

				String sDay = pDate.substring(pDate.indexOf("月") + 1, pDate.indexOf("日"));
				String tDay = sDay.replace("三十一", "31")
						.replace("三十", "30")
						.replace("二十九", "29")
						.replace("二十八", "28")
						.replace("二十七", "27")
						.replace("二十六", "26")
						.replace("二十五", "25")
						.replace("二十四", "24")
						.replace("二十三", "23")
						.replace("二十二", "22")
						.replace("二十一", "21")
						.replace("二十", "20")
						.replace("十九", "19")
						.replace("十八", "18")
						.replace("十七", "17")
						.replace("十六", "16")
						.replace("十五", "15")
						.replace("十四", "14")
						.replace("十三", "13")
						.replace("十二", "12")
						.replace("十一", "11")
						.replace("十", "10")
						.replace("九", "9")
						.replace("八", "8")
						.replace("七", "7")
						.replace("六", "6")
						.replace("五", "5")
						.replace("四", "4")
						.replace("三", "3")
						.replace("二", "2")
						.replace("一", "1");
				pDate = tyear + "-" + tMonth + "-" + tDay;
				financeMonitorPunish.setPunishDate(pDate);
			}

		}
	}

	protected FinanceMonitorPunish filterPlace(FinanceMonitorPunish financeMonitorPunish) throws Exception {
		if (Objects.isNull(financeMonitorPunish)) return financeMonitorPunish;
		Field[] fields = FinanceMonitorPunish.class.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			if (fieldName.toLowerCase().equals("details")) continue;
			PropertyDescriptor prop = new PropertyDescriptor(fieldName, FinanceMonitorPunish.class);

			// 获取getter方法，反射获取field值
			Object obj = prop.getReadMethod().invoke(financeMonitorPunish);

			if (Objects.isNull(obj) || obj instanceof Date) {
				continue;
			}
			String str = String.valueOf(obj);
			// 获取setter方法，反射赋值
			prop.getWriteMethod().invoke(financeMonitorPunish,
					str.replaceAll("\\s*", "")
							.replace("　", "")
							.replace(" ", "")
							.replace("　　", "")
							.replace("\n", "").trim());

		}
		if (StrUtil.isNotEmpty(financeMonitorPunish.getDetails())) {
			financeMonitorPunish.setDetails(financeMonitorPunish.getDetails()
					.replaceAll("\\s*", ""));
		}
		return financeMonitorPunish;
	}

	protected String filterErrInfo(String s) {
		return s.replace("〇", "").replace("/、\\", "小")
				.replace("－2－", "'")
				.replace("－3－", "")
				.replace("－4－", "")
				.replace("－5－", "")
				.replace("－6－", "")
				.replace("－7－", "")
				.replace("－8－", "")
				.replace("－9－", "")
				.replace("'", "")
				.replace("- 2 -", "'")
				.replace("- 3 -", "")
				.replace("- 4 -", "")
				.replace("— 1 —", "")
				.replace("— 2 —", "")
				.replace("— 3 —", "")
				.replace("— 4 —", "")
				.replace("— 5 —", "")
				.replace("— 6 —", "")
				.replace("— 7 —", "")
				.replace("— 8 —", "")
				.replace("— 9 —", "")
				.replace("- 1 -", "")
				.replace("- 2 -", "")
				.replace("- 3 -", "")
				.replace("- 4 -", "")
				.replace("- 5 -", "")
				.replace("- 6 -", "")
				.replace("- 7 -", "")
				.replace("- 8 -", "")
				.replace("- 9 -", "")
				.replace("－1－", "")
				.replace("－2－", "")
				.replace("－3－", "")
				.replace("－4－", "")
				.replace("－5－", "")
				.replace("—1—", "")
				.replace("—2—", "")
				.replace("—3—", "")
				.replace("'", "");
	}

	protected boolean doFetchForRetry(FinanceMonitorPunish financeMonitorPunish,
									  Boolean isForce) throws Exception {
		return new IdempotentOperator<Boolean>(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return doFetch(financeMonitorPunish, false);
			}
		}).execute(3);
	}

	protected boolean doFetch(FinanceMonitorPunish financeMonitorPunish,
							  Boolean isForce) throws Exception {
		return false;
	}

	/**
	 * 写错误日志
	 *
	 * @param err
	 */
	protected void writeBizErrorLog(String url, String err) {
		String logPath = OCRUtil.DOWNLOAD_DIR + "/log.txt";
		if (FileUtil.exist(logPath)) {
			String logContent = FileUtil.readString(logPath, "utf-8");
			//不重复报错
			if (logContent.contains(url)) return;
		}


		BufferedWriter bw = FileUtil.getWriter(logPath, "utf-8", true);
		try {
			bw.write(url + "\t" + err + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!Objects.isNull(bw)) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}


	}

}
