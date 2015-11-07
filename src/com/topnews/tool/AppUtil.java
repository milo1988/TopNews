package com.topnews.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;

public class AppUtil
{

	public static String getRefreashTime(Context context, String newType)
	{
		String timeStr = PreferenceUtil.readString(context, "NEWS_" + newType);
		if (TextUtils.isEmpty(timeStr))
		{
			return "";
		}
		return timeStr;
	}
	
	
	public static void setRefreashTime(Context context, String newType)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		PreferenceUtil.write(context, "NEWS_" + newType,df.format(new Date()));
	}
}
