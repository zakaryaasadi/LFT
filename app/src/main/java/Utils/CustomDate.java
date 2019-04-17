package Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDate {
    public static String format(Date date){

        long different = new Date().getTime() - date.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long day = different / daysInMilli;
        different = different % daysInMilli;

        long hour = different / hoursInMilli;
        different = different % hoursInMilli;

        long minutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long seconds = different / secondsInMilli;

        if(day > 7){
            SimpleDateFormat dates = new SimpleDateFormat("EEE, dd MMMM yyyy . hh:mm");
            return dates.format(date);
        }

        if(day > 1){
            SimpleDateFormat dates = new SimpleDateFormat("EEEE");
            return dates.format(date) + " ago";
        }


        if(day == 1)
            return "yesterday ago";

        if(hour >= 1)
            return hour + " hour ago";

        if(minutes >= 1)
            return minutes + " mins. ago";

        return "Just now";
    }

    public static String event(Date date){
        SimpleDateFormat dates = new SimpleDateFormat("dddd, dd MMMM yyyy");
        return dates.format(date);
    }
}
