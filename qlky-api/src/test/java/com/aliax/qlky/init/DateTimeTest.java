package com.aliax.qlky.init;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeTest {

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date parse = sdf.parse("Wed May 14 2025 08:29:06 GMT+0800");
        System.out.println(parse.getTime());
    }
}
