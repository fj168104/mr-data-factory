package com.mr.common.base.controller;

import com.mr.framework.core.io.IoUtil;
import com.mr.framework.core.io.resource.ClassPathResource;
import com.mr.framework.core.util.CharsetUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * SpringBoot jar方式打包时,下载jar包内的文件和响应jar包内文本文件内容的例子
 * Created by JK on 2017/6/28.
 */
@RestController("demoController2")
public class DemoController {

    /***************************spring版****************************************/
    /**
     * 下载文件
     * @return
     */
    @GetMapping("/getFile1")
    public ResponseEntity<byte[]> getFile() throws IOException {

        Resource resource=  new org.springframework.core.io.ClassPathResource ("quartz.properties");

        byte[] b = IoUtil.readBytes(resource.getInputStream());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "quartz.properties");
        return new ResponseEntity<byte[]>(b, headers, HttpStatus.OK);
    }

    /**
     * 读取文件内容并响应
     * @return
     * @throws IOException
     */
    @GetMapping("/getFileToString1")
    public ResponseEntity<String> getFileToSting() throws IOException {

        Resource resource=  new org.springframework.core.io.ClassPathResource("quartz.properties");

        String result = IoUtil.read(resource.getInputStream(), CharsetUtil.CHARSET_UTF_8);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }


    /***************************Hutool版****************************************/

    /**
     * 下载文件
     * @return
     * @throws IOException
     */
    @GetMapping("/getFile2")
    public ResponseEntity<byte[]> getFile2() throws IOException {
        ClassPathResource resource=  new ClassPathResource("quartz.properties");

        byte[] b = IoUtil.readBytes(resource.getStream());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "quartz.properties");
        return new ResponseEntity<byte[]>(b, headers, HttpStatus.OK);
    }

    /**
     * 读取文件内容并响应
     * @return
     * @throws IOException
     */
    @GetMapping("/getFileToString2")
    public ResponseEntity<String> getFileToString2() throws IOException {

        ClassPathResource resource=  new ClassPathResource("quartz.properties");

        String result = IoUtil.read(resource.getStream(), CharsetUtil.CHARSET_UTF_8);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/testUploadFile", method = RequestMethod.POST)
    public ModelMap testUploadFile(HttpServletRequest req,
                               MultipartHttpServletRequest multiReq) {
        // 获取上传文件的路径
        String uploadFilePath = multiReq.getFile("file1").getOriginalFilename();
        System.out.println("uploadFlePath:" + uploadFilePath);
        // 截取上传文件的文件名
        String uploadFileName = uploadFilePath.substring(
                uploadFilePath.lastIndexOf('\\') + 1, uploadFilePath.indexOf('.'));
        System.out.println("multiReq.getFile()" + uploadFileName);
        // 截取上传文件的后缀
        String uploadFileSuffix = uploadFilePath.substring(
                uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
        System.out.println("uploadFileSuffix:" + uploadFileSuffix);
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fis = (FileInputStream) multiReq.getFile("file1").getInputStream();
            fos = new FileOutputStream(new File("/home/fengjiang/Documents/" + uploadFileName
                    + ".")
                    + uploadFileSuffix);
            byte[] temp = new byte[1024];
            int i = fis.read(temp);
            while (i != -1){
                fos.write(temp,0,temp.length);
                fos.flush();
                i = fis.read(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		ModelMap map = new ModelMap();
		map.addAttribute("result_code", "success");
		return map;
    }


}
