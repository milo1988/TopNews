package com.topnews.fragment;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.IXListViewLoadMore;
import me.maxwin.view.IXListViewRefreshListener;
import me.maxwin.view.XListView;

import org.w3c.dom.Text;

import com.topnews.CityListActivity;
import com.topnews.DetailsActivity;
import com.topnews.R;
import com.topnews.adapter.NewsAdapter;
import com.topnews.bean.NewsItem;
import com.topnews.dao.NewsItemDao;
import com.topnews.http.HttpRequest;
import com.topnews.tool.AppUtil;
import com.topnews.tool.Constants;
import com.topnews.tool.DateTools;
import com.topnews.tool.NetUtil;
import com.topnews.view.HeadListView;
import com.topnews.view.TopToastView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewsFragment extends Fragment {
	private final static String TAG = "NewsFragment";
	Activity activity;
	ArrayList<NewsItem> newsList = new ArrayList<NewsItem>();
	XListView mListView;
	NewsAdapter mAdapter;
	String text;
	int channel_id;
	private String category = "__all__";
	ImageView detail_loading;
	public final static int SET_NEWSLIST = 0;
	//Toast提示框
	private RelativeLayout notify_view;
	private TextView notify_view_text;
	
	private NewsItemDao mNewsItemDao;
	private boolean isConnNet;
	private static final int LOAD_MORE = 0x110;
	private static final int LOAD_REFREASH = 0x111;
	private static final int TIP_ERROR_NO_NETWORK = 0X112;
	private static final int TIP_ERROR_SERVER = 0X113;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle args = getArguments();
		text = args != null ? args.getString("text") : "";
		channel_id = args != null ? args.getInt("id", 0) : 0;
		Log.i(TAG, " text " + text + " channel_id " + channel_id);
		if(text.equals("热点")){
			category = "news_hot";
		}else if(text.equals("社会")){
			category = "news_society";
		}else if(text.equals("娱乐")){
			category = "news_entertainment";
		}else if(text.equals("科技")){
			category = "news_tech";
		}else if(text.equals("汽车")){
			category = "news_car";
		}else if(text.equals("美文")){
			category = "news_essay";
		}else if(text.equals("养生")){
			category = "news_regimen";
		}else if(text.equals("故事")){
			category = "news_story";
		}
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		super.onAttach(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mNewsItemDao = new NewsItemDao(getActivity());
		mListView = (XListView) getView().findViewById(R.id.mListView);
		TextView item_textview = (TextView) getView().findViewById(R.id.item_textview);
		detail_loading = (ImageView) getView().findViewById(R.id.detail_loading);
		// Toast提示框
		notify_view = (RelativeLayout) getView().findViewById(R.id.notify_view);
		notify_view_text = (TextView) getView().findViewById(R.id.notify_view_text);
		item_textview.setText(text);
		mAdapter = new NewsAdapter(activity, newsList);
		// 判断是不是城市的频道
		if (channel_id == Constants.CHANNEL_CITY) {
			// 是城市频道
			mAdapter.setCityChannel(true);
			initCityChannel();
		}
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		// mListView.setPinnedHeaderView(LayoutInflater.from(activity).inflate(R.layout.list_item_section, mListView, false));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Log.i(TAG, " position " + position +" channel_id " + channel_id);
				Intent intent = new Intent(activity, DetailsActivity.class);
				if (channel_id == Constants.CHANNEL_CITY) {
					if (position != 0) {
						intent.putExtra("news", mAdapter.getItem(position - 1));
						startActivity(intent);
						activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
					}
				} else {
					intent.putExtra("news", mAdapter.getItem(position - 1));
					startActivity(intent);
					activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
				}
			}
		});
		mListView.setPullRefreshEnable(new IXListViewRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new LoadDatasTask().execute(LOAD_REFREASH);
			}

		});
		mListView.setPullLoadEnable(new IXListViewLoadMore() {
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				new LoadDatasTask().execute(LOAD_MORE);
			}

		});
		mListView.NotRefreshAtBegin();

	}

	private void initData() {
		new LoadDatasTask().execute(LOAD_REFREASH);
	}
		
	/* 初始化选择城市的header*/
	public void initCityChannel() {
		View headview = LayoutInflater.from(activity).inflate(R.layout.city_category_list_tip, null);
		TextView chose_city_tip = (TextView) headview.findViewById(R.id.chose_city_tip);
		chose_city_tip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(activity, CityListActivity.class);
				startActivity(intent);
			}
		});
		mListView.addHeaderView(headview);
	}
	
	/* 初始化通知栏目*/
	private void initNotify() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				notify_view_text.setText(String.format(getString(R.string.ss_pattern_update), 10));
				notify_view.setVisibility(View.VISIBLE);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						notify_view.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}, 1000);
	}
	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.d("onDestroyView", "channel_id = " + channel_id);
		mAdapter = null;
	}
	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "channel_id = " + channel_id + " text= " + text);
	}
	
	
	
	/**
	 * 记载数据的异步任务
	 * 
	 * @author zhy
	 * 
	 */
	class LoadDatasTask extends AsyncTask<Integer, Void, Integer>
	{

		@Override
		protected Integer doInBackground(Integer... params)
		{
			switch (params[0])
			{
			case LOAD_MORE:
				loadMoreData();
				break;
			case LOAD_REFREASH:
				return refreashData();
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			Log.i(TAG, " onPostExecute " + result + " channel_id " + channel_id);
			switch (result)
			{
			case TIP_ERROR_NO_NETWORK:
				Toast.makeText(getActivity(), "没有网络连接！", 1).show();
//				mAdapter.setDatas(mDatas);
//				mAdapter.notifyDataSetChanged();
				break;
			case TIP_ERROR_SERVER:
				Toast.makeText(getActivity(), "服务器错误！", 1).show();
				break;
			default:
				break;
			}
			
			if(channel_id == 1){
				initNotify();
			}
			detail_loading.setVisibility(View.GONE);
			mAdapter.notifyDataSetChanged();
			mListView.setRefreshTime(AppUtil.getRefreashTime(getActivity(), category));
			mListView.stopRefresh();
			mListView.stopLoadMore();
		}

	}

	/**
	 * 下拉刷新数据
	 */
	public Integer refreashData()
	{

		if (NetUtil.checkNet(getActivity()))
		{
			isConnNet = true;
			// 获取最新数据
			try
			{
				String args = "source=2&count=20&category=" + category;// + "&min_behot_time="+ DateTools.getThreeDaysAgoTime();
				String newsStr = HttpRequest.sendGet("http://toutiao.com/api/article/recent/", args);
//				Log.i(TAG, " refreashData completed " + category + newsStr);
				newsList = Constants.getNewsListByTxt(newsStr);
				mAdapter.setNewsList(newsList);
//				isLoadingDataFromNetWork = true;
//				// 设置刷新时间
//				AppUtil.setRefreashTime(getActivity(), newsType);
				// 清除数据库数据
				mNewsItemDao.deleteAll(channel_id);
				// 存入数据库
				mNewsItemDao.add(newsList);
			} catch (Exception e){
				e.printStackTrace();
//				isLoadingDataFromNetWork = false;
				return TIP_ERROR_SERVER;
			}
		} else{
			Log.e("xxx", "no network");
			isConnNet = false;
//			isLoadingDataFromNetWork = false;
			// TODO从数据库中加载
			List<NewsItem> newsItems = mNewsItemDao.list(channel_id );
			mAdapter.setNewsList(newsItems);
			return TIP_ERROR_NO_NETWORK;
		}

		return -1;

	}

	/**
	 * 会根据当前网络情况，判断是从数据库加载还是从网络继续获取
	 */
	public void loadMoreData()
	{
		String args = "source=2&count=20&category=" + category;// + "&min_behot_time="+ DateTools.getThreeDaysAgoTime();
		String newsStr = HttpRequest.sendGet("http://toutiao.com/api/article/recent/", args);
//		Log.i(TAG, " refreashData completed " + category + newsStr);
		newsList = Constants.getNewsListByTxt(newsStr);
		mAdapter.setNewsList(newsList);
		// 当前数据是从网络获取的
//		if (isLoadingDataFromNetWork)
//		{
//			currentPage += 1;
//			try
//			{
//				List<NewsItem> newsItems = mNewsItemBiz.getNewsItems(newsType, currentPage);
//				mNewsItemDao.add(newsItems);
//				mAdapter.addAll(newsItems);
//			} catch (CommonException e)
//			{
//				e.printStackTrace();
//			}
//		} else
//		// 从数据库加载的
//		{
//			currentPage += 1;
//			List<NewsItem> newsItems = mNewsItemDao.list(newsType, currentPage);
//			mAdapter.addAll(newsItems);
//		}

	}
}
