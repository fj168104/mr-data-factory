package com.mr.common.util;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ImmediateRefreshHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.framework.http.HttpException;

public class CrawlerUtil {

	public static String getHtmlPage(String url) {
		WebClient wc = createDefaultWebClient();
		try {
			return getHtmlPage(wc, url);
		} finally {
			wc.close();
		}
	}

	public static String getHtmlPage(WebClient wc, String url) {
		try {
			// 模拟浏览器打开一个目标网址
			HtmlPage htmlPage = wc.getPage(url);

			// 为了获取js执行的数据 线程开始沉睡等待
			Thread.sleep(1000);// 这个线程的等待 因为js加载需要时间的

			return htmlPage.asXml();// 以xml形式获取响应文本
		} catch (InterruptedException e) {
			throw new HttpException(e);
		} catch (FailingHttpStatusCodeException e) {
			throw new HttpException(e);
		} catch (MalformedURLException e) {
			throw new HttpException(e);
		} catch (IOException e) {
			throw new HttpException(e);
		}
	}

	/**
	 * 创建默认的爬虫客户端
	 * 
	 * @return
	 */
	public static WebClient createDefaultWebClient() {
		// 设置浏览器版本
		WebClient wc = new WebClient(BrowserVersion.CHROME);
		// 是否使用不安全的SSL
		wc.getOptions().setUseInsecureSSL(true);
		// 启用JS解释器，默认为true
		wc.getOptions().setJavaScriptEnabled(false);
		// 禁用CSS
		wc.getOptions().setCssEnabled(false);
		// js运行错误时，是否抛出异常
		wc.getOptions().setThrowExceptionOnScriptError(false);
		// 状态码错误时，是否抛出异常
		wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
		// 是否允许使用ActiveX
		wc.getOptions().setActiveXNative(false);
		// 等待js时间
		wc.waitForBackgroundJavaScript(600 * 1000);
		// 设置Ajax异步处理控制器即启用Ajax支持
		wc.setAjaxController(new NicelyResynchronizingAjaxController());
		// 设置超时时间，默认30秒
		wc.getOptions().setTimeout(30000);
		// 不跟踪抓取
		wc.getOptions().setDoNotTrackEnabled(false);
		//
		wc.getOptions().setRedirectEnabled(true);
		//
		wc.getCache().clear();
		//
		wc.getCookieManager().clearCookies();
		//
		wc.setRefreshHandler(new ImmediateRefreshHandler());

		return wc;
	}

	/**
	 * 替换HTML空格为文本空格
	 * 
	 * @param text
	 * @return
	 */
	public static String replaceHtmlNbsp(String text) {
		if (text == null) {
			return null;
		}
		return text.replace(Jsoup.parse("&nbsp;").text(), " ").trim();
	}
}
