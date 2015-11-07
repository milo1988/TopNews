package com.topnews.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil
{
	private static String topNews = "topnews";
	public static void write(Context context, String key, String value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(topNews,Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(key, value).commit();
	}

	public static void write(Context context, String key, int value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(topNews,Context.MODE_PRIVATE);
		sharedPreferences.edit().putInt(key, value).commit();
	}
	public static void write(Context context, String key, Boolean value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(topNews,Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(key, value).commit();
	}
	
	public static String readString(Context context , String key )
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(topNews,Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}
	
	public static int readInt(Context context , String key )
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(topNews,Context.MODE_PRIVATE);
		return sharedPreferences.getInt(key, 0);
	}
	
	public static Boolean readBoolean(Context context , String key )
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(topNews,Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}
	
	public static void remove(Context context , String key)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(topNews,Context.MODE_PRIVATE);
		sharedPreferences.edit().remove(key).commit();
	}

}
