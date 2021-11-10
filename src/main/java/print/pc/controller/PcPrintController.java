package print.pc.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;
import print.config.ConfigProperties;
import print.pc.util.ImagePrint;
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
@RequestMapping(path = "pcPrint")
public class PcPrintController {
    @Autowired
    private ConfigProperties configProperties;

    @RequestMapping(path = "form", method = {RequestMethod.GET})
    public String form(Model model, @RequestParam(value = "serialNo", required = true) String serialNo,
        @RequestParam(value = "productId", required = true) String productId) {
        model.addAttribute("serialNo", serialNo);
        model.addAttribute("productId", productId);
        return "pcPrint/form";
    }

    @PostMapping(path = "uploadFile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file,
        @RequestParam(value = "serialNo", required = true) String serialNo,
        @RequestParam(value = "productId", required = true) String productId) {
        JSONObject result = new JSONObject();
        if (file.isEmpty()) {
            result.put("success", false);
            return result.toString();
        }
        String fileName = RandomUtils.nextLong() +"-print.png";

        String date = DateFormatUtils.format(new Date().getTime(), "yyyyMMdd");
        String filePath = configProperties.getBasePath()+date+"/"+serialNo+"/"+productId+"/";
        File dest = new File(filePath, fileName);
        try {
            File saveFilePath = new File(filePath);
            if(!saveFilePath.exists()){
                saveFilePath.mkdirs();
            }
            file.transferTo(dest);
            result.put("msg", "上传成功");
            ImagePrint.imagePrint(filePath+fileName, configProperties.getPrintx(), configProperties.getPrintw());
            result.put("msg", "传输打印成功");

            result.put("success", true);
            result.put("id", fileName);
        } catch (IOException e) {
            result.put("success", false);
        }

        return result.toString();
    }

    @RequestMapping(path = "printStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String printStatus(@RequestParam("id") String id){
        JSONObject result = new JSONObject();
        result.put("success", false);

        return result.toString();
    }
}