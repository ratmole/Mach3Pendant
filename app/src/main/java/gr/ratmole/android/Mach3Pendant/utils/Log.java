package gr.ratmole.android.Mach3Pendant.utils;

import android.text.TextUtils;

public final class Log {
    private final static String TAG = "Mach3Pendant";


    public static void v(String msg) {
        android.util.Log.v(TAG, getLocation() + msg);
    }

    public static void d(String msg) {
        android.util.Log.d(TAG, getLocation() + msg);
    }

    public static void e(String msg) {
        android.util.Log.e(TAG, getLocation() + msg);
    }

    public static void e(String msg, Throwable tr) {
        android.util.Log.e(TAG, getLocation() + msg, tr);
    }

    private static String getLocation() {
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                }
                else if (trace.getClassName().startsWith(className)) {
                    found = true;
                    continue;
                }
            }
            catch (ClassNotFoundException e) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }
}