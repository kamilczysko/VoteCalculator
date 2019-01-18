package com.kamil.VoteCalculator.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    private static long convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyMMdd");
            Date date = (Date) formatter.parse(str_date);
            return date.getTime();
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return 0;
        }
    }

    public static boolean isAdult(String bDay) {
        long bDatTimeStamp = (long) convertStringToTimestamp(bDay);
        long present = new Date().getTime();
        long diff = present - bDatTimeStamp;

        if ((diff / 1000 / 60 / 60 / 24 / 365) >= 18)
            return true;
        return false;
    }
}