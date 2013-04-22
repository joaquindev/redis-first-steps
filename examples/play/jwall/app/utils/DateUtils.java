package utils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/18/13
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class DateUtils {

    static public Date unixToDate(Long timeStamp){
        Date time = new java.util.Date((long)timeStamp*1000);
        return time;
    }

    static public String unixToStr(Long timeStamp){
        Date time = DateUtils.unixToDate(timeStamp);
        return time.toString();
    }
}
