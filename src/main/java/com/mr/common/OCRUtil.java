package com.mr.common;

import com.mr.framework.core.io.FileUtil;
import com.mr.framework.ocr.OcrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class OCRUtil {

	AtomicInteger atomicCount = new AtomicInteger();
	@Value("${download-dir}")
	private String downloadDir;
	private OcrUtils ocrUtils;

	public static String DOWNLOAD_DIR = System.getProperty("java.io.tmpdir");

	@PostConstruct
	public void postConfig() {
		DOWNLOAD_DIR = downloadDir;
		ocrUtils = new OcrUtils(DOWNLOAD_DIR);
	}

	/**
	 * 从img识别文本内容并返回，包含pdf扫描图片识别
	 *
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String getTextFromImg(String fileName) throws Exception {
		return ocrUtils.getTextFromImg(fileName);
	}

	/**
	 * @param filePath
	 * @Title: getTextFromPdf
	 * @Description: 读取pdf文件内容
	 * @return: 读出的pdf的内容
	 */
	public String getTextFromPdf(String filePath) throws Exception {
		return ocrUtils.getTextFromPdf(filePath);
	}

	/**
	 * @param filePath
	 * @Title: getTextFromDoc
	 * @Description: 读取doc文本内容
	 * @return: 读出的doc的内容
	 */
	public String getTextFromDoc(String filePath) throws Exception {
		String entirePath = DOWNLOAD_DIR + File.separator + filePath;
		InputStream in = new FileInputStream(entirePath);
		String bodyText = "";
		try {
			//转换成  PushbackinputStream
			if (!in.markSupported()) {
				in = new PushbackInputStream(in, 8);
			}
			//其他word版本
			if (POIFSFileSystem.hasPOIFSHeader(in)) {
				HWPFDocument document = new HWPFDocument(in);
				WordExtractor extractor = new WordExtractor(document);
				bodyText = extractor.getText();
			} else {
				//07 版本
				XWPFDocument document = new XWPFDocument(in);
				XWPFWordExtractor extractor = new XWPFWordExtractor(document);
				bodyText = extractor.getText();
				System.out.println(bodyText);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			FileUtil.del(entirePath);
		}

		return bodyText;
	}


	/**
	 * 解析的dirName下的所有图片
	 * dirName  目录名 = 下载的文件所在目录 + 文件名
	 * 如：下载文件名为 test.pdf,下载的目录为 /home/fengjiang/Documents,
	 * 则新建目录test, dirName = /home/fengjiang/Documents/test, 里面为 test.pdf 转成的若干图片，如0.png, 1.png
	 *
	 * @param dirName
	 */
	public String recognizeTexts(String dirName) throws Exception {
		return ocrUtils.recognizeTexts(dirName);
	}

	private final static String LANG_OPTION = "-l";

	//line.separator 行分隔符
	private final static String EOL = System.getProperty("line.separator");

	//tesseract文件放在项目文件里了
	private final static String tesseractPath = new File(".").getAbsolutePath();

	/**
	 * 此方法功能：识别图片中的文字并返回到指定txt文件中
	 *
	 * @param image 输入一张图片（这里放在了项目目录）
	 */
	public String recognizeText(File image) throws Exception {
		return ocrUtils.recognizeText(image);
	}

	/**
	 * @param fileName 下载的文件名,不是全路径名
	 *                 将文件移到文件夹内，并改名.
	 * @return
	 */
	public String image2Dir(String fileName) {
		return ocrUtils.image2Dir(fileName);
	}

	/**
	 * 将pdf转化为png格式的image
	 *
	 * @param pdfName
	 */
	public void pdf2image(String pdfName) {
		ocrUtils.pdf2image(pdfName);
	}

	/**
	 * 移动文件
	 *
	 * @param src
	 * @param to
	 */
	public void renameTo(String src, String to) {
		ocrUtils.renameTo(src, to);
	}


	/**
	 * @param file 原文件名字
	 * @return 生成的文本文件名字
	 * @throws Exception
	 */
	public String readPdf(String file) throws Exception {
		return ocrUtils.readPdf(file);
	}

	public static void main(String[] args) {
		try {
			OCRUtil ocrUtil = new OCRUtil();
			ocrUtil.DOWNLOAD_DIR = "/home/fengjiang/Documents";
			ocrUtil.ocrUtils = new OcrUtils(DOWNLOAD_DIR);
//			ocrUtil.renameTo("/home/fengjiang/Documents/nginx.conf", "/home/fengjiang/Documents/projdoc/nginx.conf");
//			ocrUtil.pdf2image(DOWNLOAD_DIR + File.separator + "P020171222593212170499.pdf");
//			ocrUtil.image2Dir("P020171222593212170499.pdf");
			//ocrUtil.image2Dir("434324.png");

//			log.info(ocrUtil.recognizeTexts(ocrUtil.image2Dir("P020171222593212170499.pdf")));

//			log.info("" + ocrUtil.getTextFromImg("20170407174712_xt53gi6w3u.pdf"));
			log.info("" + ocrUtil.getTextFromPdf("a3019d9a-e904-4b76-be5a-a4a5873977cc.pdf"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}



