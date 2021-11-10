package print.cloud.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import print.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FixedPrintTask {
    @Autowired
    private ConfigProperties configProperties;

    //@Scheduled(cron = "*/15 * * * * ?")
    public void cleanTimeOutFor2Hours() {
        LocalDateTime localDateTime = LocalDateTime.now();
        String date = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String clearPath = configProperties.getBasePath()+date;
        if(StringUtils.contains(clearPath, "print/images")){
            File clearPathFile = new File(clearPath);
            if(clearPathFile.exists() && clearPathFile.isDirectory()){
                File[] serialNoFiles = clearPathFile.listFiles();
                for(File serialNoFile:serialNoFiles){
                    if(!serialNoFile.isDirectory()){
                        deleteFile(serialNoFile);
                    }else{
                        File[] productFiles = serialNoFile.listFiles();
                        for(File productFile:productFiles){
                            if(!productFile.isDirectory()){
                                deleteFile(productFile);
                            }else{
                                File[] imagesFiles = productFile.listFiles();
                                for(File imageFile:imagesFiles){
                                    deleteFile(imageFile);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void deleteFile(File file){
        try {
            FileTime fileTime  = (FileTime) Files.readAttributes(file.toPath(), "creationTime").get("creationTime");
            long creationTime = fileTime.toMillis();
            long last2Hour = LocalDateTime.now().minusHours(2).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            if(creationTime<last2Hour){
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {

        }
    }

    //@Scheduled(cron = "* */15 * * * ?")
    public void cleanTimeOutFor2Days() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(2);
        String date = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String clearPath = configProperties.getBasePath()+date;
        if(StringUtils.contains(clearPath, "print/images")){
            File clearPathFile = new File(clearPath);
            if(clearPathFile.exists() && clearPathFile.isDirectory()){
                try {
                    FileUtils.deleteDirectory(clearPathFile);
                } catch (IOException e) {

                }
            }
        }
    }

}