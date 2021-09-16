package de.iclipse.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp getTimestampForDays(int days) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(System.currentTimeMillis());
        currentDate.add(Calendar.DATE, days);
        return new Timestamp(currentDate.getTimeInMillis());
    }

    public static Timestamp getTimestampForDays(int days, Timestamp base) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(base.getTime());
        currentDate.add(Calendar.DATE, days);
        return new Timestamp(currentDate.getTimeInMillis());
    }

    public static Timestamp getTimestampForVariable(String var) {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(System.currentTimeMillis());
        if (TypeUtils.isInt(var)) {
            result.add(Calendar.SECOND, Integer.parseInt(var));
            return new Timestamp(result.getTimeInMillis());
        }
        String time = var.substring(0, var.length() - 1);
        if (TypeUtils.isInt(time)) {
            if (var.endsWith("s")) {
                result.add(Calendar.SECOND, Integer.parseInt(time));
                return new Timestamp(result.getTimeInMillis());
            } else if (var.endsWith("m")) {
                result.add(Calendar.MINUTE, Integer.parseInt(time));
                return new Timestamp(result.getTimeInMillis());
            } else if (var.endsWith("h")) {
                result.add(Calendar.HOUR, Integer.parseInt(time));
                return new Timestamp(result.getTimeInMillis());
            } else if (var.endsWith("d")) {
                result.add(Calendar.DATE, Integer.parseInt(time));
                return new Timestamp(result.getTimeInMillis());
            }
        }
        throw new IllegalArgumentException("Invalid parameter " + var);
    }

    public static String convertTimestamp(Timestamp time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return formatter.format(cal.getTime());
    }

    public static String formatTime(long time) {
        if (time < 0) return "-";
        long days = TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" day").append(days != 1 ? "s " : ", ");
        }
        if (hours > 0) {
            sb.append(hours).append(" hour").append(hours != 1 ? "s " : ", ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" minute").append(minutes != 1 ? "s " : ", ");
        }
        if (seconds > 0) {
            sb.append(seconds).append(" second").append(seconds != 1 ? "s " : " ");
        }
        return sb.toString().trim();
    }

    public static String buildTimeString(int time, int digits) {
        String output = "";
        int hour = time / 3600;
        int minute = time % 3600 / 60;
        int second = time % 60;
        output = hour >= 10 ? output.concat("" + hour) : output.concat("0" + hour);
        output = minute >= 10 ? output.concat(":" + minute) : output.concat(":0" + minute);
        output = second >= 10 ? output.concat(":" + second) : output.concat(":0" + second);
        if (digits == -1) {
            while (output.length() > 4 && (output.charAt(0) == 48 || output.charAt(0) == 58)) {
                output = output.substring(1);
            }
        } else {
            output = output.substring(output.length() - digits);
        }

        return output;
    }


}
