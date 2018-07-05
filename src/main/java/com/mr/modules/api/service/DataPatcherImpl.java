package com.mr.modules.api.service;

import com.google.common.collect.Maps;
import com.mr.common.OCRUtil;
import com.mr.common.util.SpringUtils;
import com.mr.framework.core.collection.CollectionUtil;
import com.mr.framework.core.date.DateUtil;
import com.mr.framework.core.io.FileUtil;
import com.mr.framework.core.thread.ThreadUtil;
import com.mr.framework.core.util.StrUtil;
import com.mr.framework.json.JSONArray;
import com.mr.framework.json.JSONObject;
import com.mr.framework.json.JSONUtil;
import com.mr.modules.api.mapper.FinanceMonitorPunishMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feng on 18-6-13
 */

@Service
@Slf4j
public class DataPatcherImpl implements DataPatcher {

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
	private AtomicInteger incrementer = new AtomicInteger(20000);

	private volatile String currentDate = DateUtil.format(DateUtil.date(), "yyyyMMdd");

	@Autowired
	protected FinanceMonitorPunishMapper financeMonitorPunishMapper;

	@Override
	public int updateALlIcNames() throws Exception {
		ThreadUtil.execute(new Runnable() {
			@Override
			public void run() {
				int startNo = 0;
				int size = 100;
				int finishNum = 0;
				while (true) {
					try {
						List<FinanceMonitorPunish> financeMonitorPunishes = financeMonitorPunishMapper.selectSegmentData(startNo, size);
						if (CollectionUtil.isEmpty(financeMonitorPunishes)) break;
						for (FinanceMonitorPunish financeMonitorPunish : financeMonitorPunishes) {
							setICName(financeMonitorPunish);
							finishNum += financeMonitorPunishMapper.updateByPrimaryKey(financeMonitorPunish);
						}
						startNo = startNo + size;
					} catch (Throwable e) {
						e.printStackTrace();
					}

				}
				writeIcFailLog("urls", "<<<", "finishNum = " + finishNum);
				log.warn("finishNum = " + finishNum);
			}
		});

		return 0;
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

}
