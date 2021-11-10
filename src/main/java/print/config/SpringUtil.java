package print.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpringUtil {

    public static HttpServletRequest currentRequest() {
        if (RequestContextHolder.getRequestAttributes() == null)
            return null;
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
    public static HttpServletResponse currentResponse() {
        if (RequestContextHolder.getRequestAttributes() == null)
            return null;
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }

    public static String getAttributeFromSession(String name){
        try{
            HttpServletRequest request = currentRequest();
            if(null!=request){
                HttpSession session = request.getSession();
                if(null!=session){
                    if(null!=session.getAttribute(name)){
                        return (String) session.getAttribute(name);
                    }else{
                        return "";
                    }
                }
            }
        }catch (Exception e){

        }
        return "";
    }

    public static void setAttributeToSession(String name, String value){
        try{
            HttpServletRequest request = currentRequest();
            if(null!=request){
                HttpSession session = request.getSession();
                if(null!=session){
                    session.setAttribute(name, value);
                }
            }
        }catch (Exception e){

        }
    }

    public static String getContextPath(){
        HttpServletRequest request = SpringUtil.currentRequest();
        String contextPath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
        return contextPath;
    }
}
