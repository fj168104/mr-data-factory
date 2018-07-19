package com.mr.common.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import com.mr.common.CrawlerConstants;

/**
 * IP代理工具
 *
 */
public class ProxyIpUtil {
	// private static Logger logger = LoggerFactory.getLogger(ProxyIpUtil.class);

	/**
	 * check proxy
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public static int checkProxy(String ip, int port, String validSite) {
		return checkProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)), validSite);
	}

	/**
	 * check proxy
	 *
	 * @param proxy
	 * @param validSite
	 * @return
	 */
	public static int checkProxy(Proxy proxy, String validSite) {
		try {
			URL url = new URL(validSite != null ? validSite : CrawlerConstants.SITE_BAIDU);

			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
			httpURLConnection.setRequestProperty("User-Agent", CrawlerConstants.USER_AGENT_CHROME);
			httpURLConnection.setConnectTimeout(CrawlerConstants.TIMEOUT_MILLIS_DEFAULT);
			httpURLConnection.setReadTimeout(CrawlerConstants.TIMEOUT_MILLIS_DEFAULT);

			httpURLConnection.connect();
			int statusCode = httpURLConnection.getResponseCode();
			httpURLConnection.disconnect();

			/*
			 * InputStream inputStream = httpURLConnection.getInputStream(); String content
			 * = IOUtil.toString(inputStream, null); if(content.indexOf("百度") == -1){
			 * logger.info(content); return -1; }
			 */

			return statusCode;
		} catch (IOException e) {
			// logger.error(e.getMessage(), e);
			return -2;
		}
	}

	/**
	 * check proxy, repeat 3 times
	 *
	 * @param proxy
	 * @param validSite
	 * @return
	 */
	public static int checkProxyRepeat(Proxy proxy, String validSite) {
		for (int i = 0; i < 3; i++) {
			int statusCode = checkProxy(proxy, validSite);
			if (statusCode > 0) {
				return statusCode;
			}
		}
		return -2;
	}
}
