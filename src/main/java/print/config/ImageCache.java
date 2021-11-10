package print.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ImageCache {
    private Map<String, Map<String, String>> cache = new ConcurrentHashMap<String, Map<String, String>>();

    public boolean hasKey(String key){
        if(null!=cache && cache.containsKey(key)){
            return true;
        }else{
            return false;
        }
    }

    public Map<String,String> get(String key){
        Map<String,String> data = null;
        if(hasKey(key)){
            return cache.get(key);
        }else{
            data = new HashMap<String, String>();
            set(key, data, 20*60);
            return data;
        }
    }

    public void set(String key, Map<String,String> data, long time){
        cache.put(key, data);
    }

}
