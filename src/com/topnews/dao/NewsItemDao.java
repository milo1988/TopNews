package com.topnews.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.topnews.bean.NewsItem;
import com.topnews.db.SQLHelper;

public class NewsItemDao
{

	private SQLHelper dbHelper;

	public NewsItemDao(Context context)
	{
		dbHelper = new SQLHelper(context);
	}

	public void add(NewsItem newsItem)
	{
		String sql = "insert into newsItem (itemId,title,sourceUrl,largePic,content,newstype,publicTime,sourceFrom,commentCount,picList) " +
				"values(?,?,?,?,?,?,?,?,?,?) ;";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(sql,new Object[] { newsItem.getNewsId(), newsItem.getTitle(), newsItem.getSource_url(), newsItem.getIsLarge(), newsItem.getNewsAbstract(),newsItem.getNewsCategoryId(),
				newsItem.getPublishTime(),newsItem.getSource(),newsItem.getCommentNum(),newsItem.getPicListString()});
		db.close();
	}

	public void deleteAll(int newsType)
	{
		String sql = "delete from newsItem where newstype = ?";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(sql, new Object[] { newsType });
		db.close();
	}

	public void add(List<NewsItem> newsItems)
	{
		for (NewsItem newsItem : newsItems)
		{
			add(newsItem);
		}
	}


	public List<NewsItem> list(int newsType )
	{

//		Logger.e(newsType + "  newsType" + currentPage + "  currentPage");
		// 0 -9 , 10 - 19 ,
		List<NewsItem> newsItems = new ArrayList<NewsItem>();
		try
		{
//			int offset = 10 * (currentPage - 1);
			String sql = "select title,sourceUrl,largePic,content,newstype,publicTime,sourceFrom,commentCount,picList from newsItem where newstype = ? limit ?,? ";
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor c = db.rawQuery(sql, new String[] { newsType + "", 20 + "", "" + 0 });

			NewsItem newsItem = null;

			while (c.moveToNext())
			{
				newsItem = new NewsItem();
				String title = c.getString(0);
				String largePic = c.getString(1);
				String content = c.getString(2);
				String imgLink = c.getString(3);
				String newstype = c.getString(4);
				Integer publicTime = c.getInt(5);

				newsItem.setTitle(title);

				newsItems.add(newsItem);

			}
			c.close();
			db.close();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newsItems;

	}

}
