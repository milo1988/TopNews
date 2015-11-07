package com.topnews.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



import com.topnews.bean.CityEntity;
//import com.topnews.bean.NewsClassify;
import com.topnews.bean.NewsItem;
import com.topnews.http.HttpRequest;

public class Constants {
	
	/*
	 * 获取新闻列表
	 */
	public static ArrayList<NewsItem> getNewsList() {
		ArrayList<NewsItem> newsList = new ArrayList<NewsItem>();
		for(int i =0 ; i < 10 ; i++){
			NewsItem news = new NewsItem();
			news.setId(i);
			news.setNewsId(i);
			news.setCollectStatus(false);
			news.setCommentNum(i + 10);
			news.setInterestedStatus(true);
			news.setLikeStatus(true);
			news.setReadStatus(false);
			news.setNewsCategory("推荐");
			news.setNewsCategoryId(1);
			news.setSource_url("http://m.baidu.com");
			news.setTitle("可以用谷歌眼镜做的10件酷事：导航、玩游戏");
			List<String> url_list = new ArrayList<String>();
			if(i%2 == 1){
				String url1 = "http://p2.pstatp.com/list//7905/8279273404";
				String url2 = "http://p6.pstatp.com/list/7892/5386488789";
				String url3 = "http://p.pstatp.com/list//7905/8279273404";
				news.setPicOne(url1);
				news.setPicTwo(url2);
				news.setPicThr(url3);
				news.setSource_url("http://tech.sina.com.cn");
				url_list.add(url1);
				url_list.add(url2);
				url_list.add(url3);
			}else{
				news.setTitle("AA用车:智能短租租车平台");
				String url = "http://r3.sinaimg.cn/2/2014/0417/a7/6/92478595/580x1000x75x0.jpg";
				news.setPicOne(url);
				url_list.add(url);
			}
			news.setPicList(url_list);
			news.setPublishTime(Long.valueOf(i));
			news.setReadStatus(false);
			news.setSource("手机腾讯网");
			news.setSummary("腾讯数码讯（编译：Gin）谷歌眼镜可能是目前最酷的可穿戴数码设备，你可以戴着它去任何地方（只要法律法规允许或是没有引起众怒），作为手机的第二块“增强现实显示屏”来使用。另外，虽然它仍未正式销售，但谷歌近日在美国市场举行了仅限一天的开放购买活动，价格则为1500美元（约合人民币9330元），虽然仍十分昂贵，但至少可以满足一些尝鲜者的需求，也预示着谷歌眼镜的公开大规模销售离我们越来越近了。");
			news.setMark(i);
			if(i == 4){
				news.setTitle("部落战争强势回归");
				news.setLocal("推广");
				news.setIsLarge(true);
				String url = "http://imgt2.bdstatic.com/it/u=3269155243,2604389213&fm=21&gp=0.jpg";
				news.setSource_url("http://games.sina.com.cn/zl/duanpian/2014-05-21/141297.shtml");
				news.setPicOne(url);
				url_list.clear();
				url_list.add(url);
			}else{
				news.setIsLarge(false);
			}
			if(i == 2){
				news.setComment("评论部分，说的非常好。");
			}
			
			if(i <= 2){
				news.setPublishTime(Long.valueOf(DateTools.getTime()));
			}else if(i >2 && i <= 5){
				news.setPublishTime(Long.valueOf(DateTools.getTime()) - 86400);
			}else{
				news.setPublishTime(Long.valueOf(DateTools.getTime()) - 86400 * 2);
			}
			newsList.add(news);
		}
		return newsList;
	}
	
	
	public static ArrayList<NewsItem> getNewsListByTxt(String  newsJsonStr) {
		ArrayList<NewsItem> newsList = new ArrayList<NewsItem>();
//		String newsStr = FileUtil.read("/sdcard/data.txt");
		JSONObject object = JSONObject.fromObject(newsJsonStr);
		JSONArray array = object.getJSONArray("data");
		for(int i = 0 ; i < array.length(); i++ ){
			NewsItem news = new NewsItem();
			JSONObject newObject = (JSONObject) array.get(i);
			Integer newsId = newObject.getInt("item_id");
			String newsAbstract = newObject.getString("abstract");
			String newsTitle = newObject.getString("title");
			//source_url 组内编号
			String sourceUrl = "http://toutiao.com" + newObject.getString("source_url");
			Long publicTime = newObject.getLong("publish_time");
			String keyWords = newObject.getString("keywords");
			String source = newObject.getString("source");
			int commentCount = newObject.getInt("comment_count");
			boolean largeMode = newObject.getBoolean("large_mode");
			boolean hasImage = newObject.getBoolean("has_image");
//			boolean middleImage = newObject.getBoolean("middle_mode");
			if(hasImage){
				JSONArray imgUrlArray = newObject.getJSONArray("image_list");
				if(!imgUrlArray.isEmpty() ){
					///三幅图
					List<String> url_list = new ArrayList<String>();
					for(int j = 0; j< imgUrlArray.length(); j++){
						JSONObject urlObject = imgUrlArray.getJSONObject(j);
						url_list.add(urlObject.getString("url"));
					}
					news.setPicList(url_list);
				}else if(largeMode){
					String largeImageUrl = newObject.getString("large_image_url");
// 					JSONArray largeImageListObject = newObject.getJSONArray("large_image_list");
//					if(largeImageListObject.length() == 1){
//						String largeImageUrl = largeImageListObject.getJSONObject(0).getString("url");
//						news.setPicOne(largeImageUrl);
//					}
					List<String> url_list = new ArrayList<String>();
					url_list.add(largeImageUrl);
					news.setPicList(url_list);
				}else{
					if(newObject.has("image_url")){
						String imgUrlStr = newObject.getString("image_url");
						List<String> url_list = new ArrayList<String>();
						url_list.add(imgUrlStr);
						news.setPicList(url_list);
					}
				}
			}
			
			news.setId(i);
			news.setIsLarge(largeMode);
			news.setNewsId(newsId);
			news.setCollectStatus(false);
			news.setCommentNum(commentCount);
			news.setInterestedStatus(true);
			news.setLikeStatus(true);
			news.setReadStatus(false);
			news.setNewsCategory("推荐");
			news.setNewsCategoryId(1);
			news.setSource_url(sourceUrl);
			news.setNewsAbstract(newsAbstract);
			news.setPublishTime(publicTime);
			news.setTitle(newsTitle);
			news.setSource(source);
			news.setMark(i);
			news.setCollectStatus(false);
			System.out.println( (i+1) +" " + newsTitle + "*****" + newsAbstract);
			newsList.add(news);
		}
		
		Comparator<NewsItem> comparator = new Comparator<NewsItem>(){
			@Override
			public int compare(NewsItem lhs, NewsItem rhs) {
				// TODO Auto-generated method stub
				return (int) (rhs.getPublishTime() - lhs.getPublishTime());
			}
			
		};

		Collections.sort(newsList,comparator);
		return newsList;
	}
	
	/** mark=0 ：推荐 */
	public final static int mark_recom = 0;
	/** mark=1 ：热门 */
	public final static int mark_hot = 1;
	/** mark=2 ：首发 */
	public final static int mark_frist = 2;
	/** mark=3 ：独家 */
	public final static int mark_exclusive = 3;
	/** mark=4 ：收藏 */
	public final static int mark_favor = 4;
	
	/*
	 * 获取城市列表
	 */
	public static ArrayList<CityEntity> getCityList(){
		ArrayList<CityEntity> cityList =new ArrayList<CityEntity>();
		CityEntity city1 = new CityEntity(1, "安吉", 'A');
		CityEntity city2 = new CityEntity(2, "北京", 'B');
		CityEntity city3 = new CityEntity(3, "长春", 'C');
		CityEntity city4 = new CityEntity(4, "长沙", 'C');
		CityEntity city5 = new CityEntity(5, "大连", 'D');
		CityEntity city6 = new CityEntity(6, "哈尔滨", 'H');
		CityEntity city7 = new CityEntity(7, "杭州", 'H');
		CityEntity city8 = new CityEntity(8, "金沙江", 'J');
		CityEntity city9 = new CityEntity(9, "江门", 'J');
		CityEntity city10 = new CityEntity(10, "山东", 'S');
		CityEntity city11 = new CityEntity(11, "三亚", 'S');
		CityEntity city12 = new CityEntity(12, "义乌", 'Y');
		CityEntity city13 = new CityEntity(13, "舟山", 'Z');
		cityList.add(city1);
		cityList.add(city2);
		cityList.add(city3);
		cityList.add(city4);
		cityList.add(city5);
		cityList.add(city6);
		cityList.add(city7);
		cityList.add(city8);
		cityList.add(city9);
		cityList.add(city10);
		cityList.add(city11);
		cityList.add(city12);
		cityList.add(city13);
		return cityList;
	}
	/* 频道中区域 如杭州 对应的栏目ID */
	public final static int CHANNEL_CITY = 3;
	
	
	public static void main(String[] args) throws Exception{
		System.out.println(new File(".").getAbsolutePath());
		String newsStr = FileUtil.read(".//data.txt");
		JSONObject object = JSONObject.fromObject(newsStr);
		JSONArray array = object.getJSONArray("data");
		for(int i = 0 ; i < array.length(); i++ ){
			
			JSONObject newObject = (JSONObject) array.get(i);
			Integer newsId = newObject.getInt("item_id");
			String newsAbstract = newObject.getString("abstract");
			String newsTitle = newObject.getString("title");
			String sourceUrl = "http://toutiao.com" + newObject.getString("source_url");
			Long publicTime = newObject.getLong("publish_time");
			boolean hasImage = newObject.getBoolean("has_image");
			String imgList = newObject.getString("image_list");
//			boolean middleMode = newObject.getBoolean("middle_mode");
//			String middleImage = newObject.getString("middle_image");
			boolean largeMode = newObject.getBoolean("large_mode");
//			String largeImageUrl = newObject.getString("large_image_url");
//			String imgUrlStr = newObject.getString("image_url");
			
					
			NewsItem news = new NewsItem();
			news.setId(i);
			news.setNewsId(newsId);
			news.setCollectStatus(false);
			news.setCommentNum(i + 10);
			news.setInterestedStatus(true);
			news.setLikeStatus(true);
			news.setReadStatus(false);
			news.setNewsCategory("推荐");
			news.setNewsCategoryId(1);
			news.setSource_url(sourceUrl);
			news.setNewsAbstract(newsAbstract);
			news.setTitle("可以用谷歌眼镜做的10件酷事：导航、玩游戏");
			
			System.out.println((i+1) +" " + newsTitle + " " + hasImage + imgList + "  " + largeMode + " "  );
			
		}
		
//		Document document = Jsoup.connect("http://toutiao.com/group/6194906612656177410/").timeout(9000).get();
//		System.out.println(document.toString());
	}
}
