package com.ebay.dataquality;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.time.Duration;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class DateTimeUtils {

    public static TimeZone MTS_TIMEZONE = TimeZone.getTimeZone("MST");
    public static final FastDateFormat MTS_YYYYMMDD = FastDateFormat.getInstance("yyyyMMdd", MTS_TIMEZONE);
    public static final FastDateFormat MTS_YYYY_MM_DD = FastDateFormat.getInstance("yyyy-MM-dd", MTS_TIMEZONE);
    public static final FastDateFormat MTS_YYYYMMDDHHMMSS_1 = FastDateFormat.getInstance("yyyyMMdd HH:mm:ss", MTS_TIMEZONE);
    public static final FastDateFormat MTS_YYYY_WW = FastDateFormat.getInstance("yyyy-ww", MTS_TIMEZONE);
    public static final FastDateFormat MTS_YYYYMM = FastDateFormat.getInstance("yyyyMM", MTS_TIMEZONE);
    public static final FastDateFormat MTS_YYYYMMDDHH = FastDateFormat.getInstance("yyyyMMdd HH", MTS_TIMEZONE);

    static private Map<String, FastDateFormat> unitToFormat = new HashMap<String, FastDateFormat>() {{
        put("h", MTS_YYYYMMDDHH);
        put("d", MTS_YYYYMMDD);
        put("w", MTS_YYYY_WW);
        put("m", MTS_YYYYMM);
    }};

    static private Map<String, Integer> unitToFieldNum = new HashMap<String, Integer>() {{
        put("h", Calendar.HOUR);
        put("d", Calendar.DATE);
        put("w", Calendar.WEEK_OF_YEAR);
        put("m", Calendar.MONTH);
    }};

    /**
     * floor to the nearest time based on the duration unit.
     * duration should be equal or less than 1 day and bigger than 1 sec
     */
    public static long floor(long ts, Duration duration) {
        try {
            long durationMs = duration.getSeconds() * 1000;
            long startOfToday = MTS_YYYYMMDD.parse(MTS_YYYYMMDD.format(ts)).getTime();
            return startOfToday + durationMs * ((ts - startOfToday) / durationMs);
        } catch (ParseException e) {
            throw new IllegalStateException("parse error: " + e.getMessage(), e);
        }
    }

    public static String floor(String time, Duration duration) {
        try {
            long ts = MTS_YYYYMMDDHHMMSS_1.parse(time).getTime();
            long floorTs = floor(ts, duration);
            return MTS_YYYYMMDDHHMMSS_1.format(floorTs);
        } catch (ParseException e) {
            throw new IllegalStateException("parse error: " + e.getMessage(), e);
        }
    }

    public static long floorToMs(String time, String pattern, String timeUnit) {
        try {
            FastDateFormat format = FastDateFormat.getInstance(pattern, MTS_TIMEZONE);
            long ts = format.parse(time).getTime();
            if (!unitToFormat.containsKey(timeUnit))
                throw new IllegalArgumentException(unitToFormat.keySet().stream().collect(Collectors.joining("|")) + "is expected as timeUnit");
            FastDateFormat floorFormat = unitToFormat.get(timeUnit);
            return floorFormat.parse(floorFormat.format(ts)).getTime();
        } catch (ParseException e) {
            throw new IllegalStateException("parse error: " + e.getMessage(), e);
        }
    }

    public static String format(long ts, String pattern) {
        FastDateFormat fmt = FastDateFormat.getInstance(pattern, MTS_TIMEZONE);
        return fmt.format(ts);
    }

    public static long[] calculateStartEndDatesInMs(String time, String pattern, int ago, int duration, String unit) {
        long flooredLaunchTimeMs = DateTimeUtils.floorToMs(time, pattern, unit);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(flooredLaunchTimeMs);

        Integer timeUnit = unitToFieldNum.get(unit);
        if (timeUnit == null) throw new IllegalArgumentException("unsupported time unit:" + unit);

        calendar.add(timeUnit, -ago);
        long startMs = calendar.getTimeInMillis();
        calendar.add(timeUnit, duration);
        long endMs = calendar.getTimeInMillis();
        return new long[]{startMs, endMs};
    }
}

