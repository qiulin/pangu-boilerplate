package org.pf9.pangu.boilerplate.rest;

import org.apache.commons.io.FileUtils;
import org.pf9.pangu.boilerplate.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    @Autowired
    private ApplicationProperties applicationProperties;


    /**
     * 单个文件上传
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("uploadfile") CommonsMultipartFile file,
                             HttpServletRequest request) {

        List<String> allowedExt = applicationProperties.getFile().getAllowedExt();
        // MultipartFile是对当前上传的文件的封装，当要同时上传多个文件时，可以给定多个MultipartFile参数(数组)
        if (!file.isEmpty()) {
            String fileExt = file.getOriginalFilename().substring(
                    file.getOriginalFilename().lastIndexOf("."));// 取文件格式后缀名
            if (allowedExt.size() > 0) {
                for (String ext : allowedExt) {
                    if (!ext.startsWith(".")) {
                        ext = "." + ext;
                    }

                    if (!ext.equals(fileExt)) {
                        return "failure";
                    }
                }
            }
            String filename = System.currentTimeMillis() + fileExt;// 取当前时间戳作为文件名
            String path = request.getSession().getServletContext()
                    .getRealPath("/upload/" + filename);// 存放位置
            File destFile = new File(path);
            try {
                // FileUtils.copyInputStreamToFile()这个方法里对IO进行了自动操作，不需要额外的再去关闭IO流
                FileUtils
                        .copyInputStreamToFile(file.getInputStream(), destFile);// 复制临时文件到指定目录下
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "success";
        } else {
            return "failure";
        }
    }


    /**
     * 多文件上传
     *
     * @param files
     * @param request
     * @return
     */
    @PostMapping("/uploads")
    public HashMap<String, String> uploadMultiFiles(@RequestParam("upload") CommonsMultipartFile[] files,
                                                    HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>();
        if (files != null) {

            List<String> allowedExt = applicationProperties.getFile().getAllowedExt();
            String fileUrl = "";

            for (int i = 0; i < files.length; i++) {
                String type = files[i].getOriginalFilename().substring(
                        files[i].getOriginalFilename().lastIndexOf("."));// 取文件格式后缀名
                if (allowedExt.size() > 0) {
                    if(allowedExt.stream().filter(ext -> ext.equals(type)).findFirst()==null){
                            map.put("message", "上传失败，文件格式不符合");
                            return map;
                    }
                }
                String finalName = System.currentTimeMillis() + "OMSFILE" + files[i].getOriginalFilename();// 取当前时间戳作为文件名

                String path = applicationProperties.getUploadPath() + "/" + finalName;
                File destFile = new File(path);

                try {
                    FileUtils.copyInputStreamToFile(files[i].getInputStream(), destFile);// 复制临时文件到指定目录下
                } catch (IOException e) {
                    map.put("message", "failure");
                    e.printStackTrace();
                    return map;
                }
//                fileUrl = applicationProperties.getIpAddress().trim()+"\\"+fileUrl + path;
                fileUrl = fileUrl.trim() + finalName;
                map.put("fileUrl", fileUrl);
                map.put("uploaded", "1");
                map.put("fileName", finalName);
                map.put("url", "/api/v1/file/download?filename=" + finalName);
            }
        } else {
            map.put("message", "failure");

        }
        return map;
    }


    /**
     * 文件下载
     *
     * @throws IOException
     */
    @GetMapping("/download")
    public void download(@RequestParam(value = "filename") String filename,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        InputStream bis = null;
        BufferedOutputStream out = null;
        try {
            //模拟文件，myfile.txt为需要下载的文件
            String sysPath = applicationProperties.getUploadPath() + "/" + filename;
            //String path = request.getSession().getServletContext().getRealPath(sysPath)+"/"+filename;
            //获取输入流
            bis = new BufferedInputStream(new FileInputStream(new File(sysPath)));
            //转码，免得文件名中文乱码
            filename = URLEncoder.encode(filename, "UTF-8");
            //设置文件下载头
            response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            //设置文件ContentType类型，这样设置，会自动判断下载文件类型
            response.setContentType("multipart/form-data");
            out = new BufferedOutputStream(response.getOutputStream());
            int len = 0;
            byte bytes[] = new byte[1024];
            while ((len = bis.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (out != null) {
                    out.flush();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}