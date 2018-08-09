package youngmlee.com.astronomypictureoftheday.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {

    private final static String dateFormat = "yyyy-MM-dd";

    public static String subtractDays(String currentDate, int days)  {
        Log.d("DATE TEST" , "CURRENT: " + currentDate);
        DateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
        Date date = null;
        try {
            date = (Date) formatter.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);

        Date newDate = calendar.getTime();
        Log.d("DATE TEST" , "THIRTY DATES BEFORE: " + formatter.format(newDate));
        return formatter.format(newDate);
    }


}
