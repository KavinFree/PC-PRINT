package print.cloud.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;
import print.config.ConfigProperties;
import print.config.ImageCache;
import print.config.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(path = "cloud")
public class CloudPrintController {
    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private ImageCache imageCache;

    @RequestMapping(path = "form", method = {RequestMethod.GET})
    public String form(Model model, @RequestParam(value = "serialNo", required = true) String serialNo,
        @RequestParam(value = "productId", required = true) String productId,
        @RequestParam(value = "productName", required = false) String productName) {
        if(StringUtils.isNotBlank(serialNo) && StringUtils.isNotBlank(productId)){
            String contextPath = SpringUtil.getContextPath();
            model.addAttribute("action", contextPath+"/cloud/uploadFile");
            model.addAttribute("chooseFile", contextPath+"/chooseFile.png");
            model.addAttribute("submit", contextPath+"/submit.png");
            model.addAttribute("serialNo", serialNo);
            model.addAttribute("productId", productId);
            Boolean flag = imageCache.hasKey(serialNo+productId);
            Map<String,String> data;
            if(flag){
                data = (Map<String, String>) imageCache.get(serialNo+productId);
                model.addAttribute("filePath", data.get("filePath"));
            }else{
                data = new HashMap<String, String>();
            }
            data.put("flag", "false");
            imageCache.set(serialNo+productId, data, 20*60);
        }
        return "cloud/form";
    }

    @PostMapping(path = "uploadFile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map uploadFile(Model model1, @RequestParam("file") MultipartFile file,
        @RequestParam(value = "serialNo", required = true) String serialNo,
        @RequestParam(value = "productId", required = true) String productId) {
        Map result = new HashMap();
        Boolean flag = imageCache.hasKey(serialNo+productId);
        if(StringUtils.isNotBlank(serialNo) && StringUtils.isNotBlank(productId) && flag){
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("msg", "空文件，请重试！");
            }else{
                String fileName = RandomUtils.nextLong(10000L, 10000000) +".png";

                String date = DateFormatUtils.format(new Date().getTime(), "yyyyMMdd");
                String savePath = configProperties.getBasePath()+date+"/"+serialNo+"/"+productId+"/";
                try {
                    File saveFilePath = new File(savePath);
                    if(!saveFilePath.exists()){
                        saveFilePath.mkdirs();
                    }
                    File dest = new File(savePath, fileName);
                    file.transferTo(dest);
                    result.put("msg", "上传成功");

                    Map<String,String> data = (Map<String, String>) imageCache.get(serialNo+productId);
                    data.put("flag", "true");
                    data.put("filePath", SpringUtil.getContextPath()+"/cloud/download?filePath="+date+"/"+serialNo+"/"+productId+"/"+fileName);
                    imageCache.set(serialNo+productId, data, 2*60*60);
                    result.put("success", true);
                    result.put("id", fileName);
                    result.put("filePath", data.get("filePath"));
                } catch (IOException e) {
                    result.put("success", false);
                }
            }
        }else{
            result.put("success", false);
            result.put("msg", "上传超时");
        }
        return result;
    }

    @RequestMapping(path = "appGetImage", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String appGetImage(Model model, @RequestParam(value = "serialNo", required = true) String serialNo,
        @RequestParam(value = "productId", required = true) String productId) {
        JSONObject result = new JSONObject();
        Boolean hasKey_flag = imageCache.hasKey(serialNo+productId);
        if(StringUtils.isNotBlank(serialNo) && StringUtils.isNotBlank(productId) && hasKey_flag){
            Map<String,String> data = (Map<String, String>) imageCache.get(serialNo+productId);
            String flag = data.get("flag");
            if(StringUtils.equals("true", flag)){
                result.put("success", true);
                result.put("filePath", data.get("filePath"));
                result.put("msg", "上传完成");
            }else{
                result.put("success", false);
                result.put("msg", "上传中");
            }
        }else{
            result.put("success", false);
            result.put("msg", "数据不存在");
        }

        return result.toString();
    }

    @RequestMapping(path = "appDelImage", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String appDelImage(Model model, @RequestParam(value = "serialNo", required = true) String serialNo,
        @RequestParam(value = "productId", required = true) String productId) {
        JSONObject result = new JSONObject();
        Boolean hasKey_flag = imageCache.hasKey(serialNo+productId);
        if(StringUtils.isNotBlank(serialNo) && StringUtils.isNotBlank(productId) && hasKey_flag){
            Map<String,String> data = (Map<String, String>) imageCache.get(serialNo+productId);
            String flag = data.get("flag");
            if(StringUtils.equals("true", flag)){
                result.put("success", true);
                String filePath = data.get("filePath");
                if(StringUtils.isNotBlank(filePath)){
                    String clearPath = StringUtils.replace(filePath, SpringUtil.getContextPath()+"/cloud/download?filePath=", configProperties.getBasePath());
                    if(StringUtils.contains(clearPath, "print/images")){
                        data.put("flag", "false");
                        data.put("filePath", "");
                        imageCache.set(serialNo+productId, data, 2*60*60);
                        try {
                            FileUtils.forceDelete(new File(clearPath));
                        } catch (IOException e) {

                        }
                    }
                }
            }else{
                result.put("success", false);
                result.put("msg", "上传中");
            }
        }else{
            result.put("success", false);
            result.put("msg", "数据不存在");
        }
        return result.toString();
    }

    @RequestMapping("/download")
    @ResponseBody
    public String downLoad(@RequestParam(value = "filePath", required = true) String filePath){
        try {
            File file = new File(configProperties.getBasePath(), filePath);
            if(file.exists()){ //判断文件父目录是否存在
                HttpServletResponse response = SpringUtil.currentResponse();
                response.setContentType("application/octet-stream");
                response.setCharacterEncoding("UTF-8");
                String fileName = StringUtils.substringAfterLast(filePath, "/");
                response.setHeader("Content-Disposition", "attachment;fileName=" +   java.net.URLEncoder.encode(fileName,"UTF-8"));
                byte[] buffer = new byte[1024];
                FileInputStream fis = null; //文件输入流
                BufferedInputStream bis = null;

                OutputStream os = null; //输出流
                try {
                    os = response.getOutputStream();
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    int i = bis.read(buffer);
                    while(i != -1){
                        os.write(buffer);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {

                }finally {
                    if(null!=bis){
                        bis.close();
                    }
                    if(null!=fis){
                        fis.close();
                    }
                }
            }
        } catch (IOException e) {

        }
        return null;
    }
}