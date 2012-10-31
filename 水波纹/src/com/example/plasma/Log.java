package com.example.plasma;

import android.util.Config;

public class Log {
	public final static String LOGTAG = "ad";

	private static final boolean DEBUG = true;
	//private static final boolean DEBUG = false;
	static final boolean LOGV = DEBUG ? Config.LOGD : Config.LOGV;

	public static void v(String msg) {
		if (LOGV) {
			android.util.Log.v(LOGTAG, msg);
		}
	}

	public static void e(String msg) {
		android.util.Log.e(LOGTAG, msg);
	}
	
	public static void d(String msg) {
		android.util.Log.d(LOGTAG, msg);
	}
}