package fr.flagadajones.media.util;

import java.util.Formatter;
import java.util.Locale;

public class StringUtils {

    private final static StringBuilder sFormatBuilder = new StringBuilder();

    private final static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    private static final Object[] sTimeArgs = new Object[5];

    /**
     * @param context
     * @param secs
     * @return time String
     */
    public static String makeTimeString(long secs) {

        String durationformat;
        if (secs < 3600)
            durationformat = "%2$d:%5$02d";
        else
            durationformat = "%1$d:%3$02d:%5$02d";

        /*
         * Provide multiple arguments so the format can be changed easily by modifying the xml.
         */
        sFormatBuilder.setLength(0);

        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600;
        timeArgs[1] = secs / 60;
        timeArgs[2] = secs / 60 % 60;
        timeArgs[3] = secs;
        timeArgs[4] = secs % 60;

        return sFormatter.format(durationformat, timeArgs).toString();
    }

    public static Long makeLongFromStringTime(String time) {

        String timeSplit[] = time.split(":");
        Long timeValue = 0L;

        timeValue += Long.valueOf(timeSplit[0]) * 60L * 60L ;//* 1000L;
        timeValue += Long.valueOf(timeSplit[1]) * 60L ;//* 1000L;

        if (timeSplit.length == 3) {
            timeValue += Long.valueOf(timeSplit[2]) ;//* 1000L;

        }
        return timeValue;
    }

  
}
