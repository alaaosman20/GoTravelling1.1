package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.SearchAdapter;
import com.weicong.gotravelling.adapter.SearchRouteAdapter;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.SearchSight;
import com.weicong.gotravelling.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SearchActivity extends SwipeBackBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private EditText mSearchText;
    private ImageView mClearImage;

    private TextView mSightText;
    private TextView mRouteText;

    private ListView mListView;

    private Drawable mBackground;

    private SuggestionSearch mSuggestionSearch;

    private ArrayList<SearchSight> mSights = new ArrayList<>();
    private SearchAdapter mSightAdapter;

    private ArrayList<Route> mRoutes = new ArrayList<>();
    private SearchRouteAdapter mRouteAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * 搜索类型：0为景点，1为路线
     */
    private int mSearchType = 0;

    /**
     * 搜索的关键字
     */
    private String mSearchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initSearch();

        initViews();

        //显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSightText = (TextView) findViewById(R.id.tv_sight);
        mBackground = mSightText.getBackground();
        setSightText();

        mRouteText = (TextView) findViewById(R.id.tv_route);

        mSightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRouteText();
                setSightText();
                mSearchType = 0;
                mListView.setAdapter(null);
                if (!TextUtil.isEmpty(mSearchKey)) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(mSearchKey)
                            .city("全国"));
                }
            }
        });

        mRouteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSightText();
                setRouteText();
                mSearchType = 1;
                mListView.setAdapter(null);
                if (!TextUtil.isEmpty(mSearchKey)) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    searchRoute(mSearchKey);
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL);

        View customView = getLayoutInflater().inflate(R.layout.layout_search, null);
        mSearchText = (EditText) customView.findViewById(R.id.et_search);
        mClearImage = (ImageView) customView.findViewById(R.id.iv_clear);
        mClearImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_clear).colorRes(R.color.white));
        mClearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchKey = s.toString();
                if (s.length() > 0) {
                    mClearImage.setVisibility(View.VISIBLE);
                    if (mSearchType == 0) { //景点查询
                        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                                .keyword(s.toString())
                                .city("全国"));
                    } else { //路线查询
                        searchRoute(s.toString());
                    }
                } else {
                    mClearImage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        actionBar.setCustomView(customView, params);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setEnabled(false);

        mListView = (ListView) findViewById(R.id.search_list);
        mSightAdapter = new SearchAdapter(this, mSights);
        mRouteAdapter = new SearchRouteAdapter(this, mRoutes);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSearchType == 0) {
                    SearchSight searchSight = mSightAdapter.getItem(position);
                    Intent intent = new Intent(SearchActivity.this, SearchDetailsActivity.class);
                    intent.putExtra(SearchSight.UID, searchSight.getUid());
                    intent.putExtra(SearchSight.NAME, searchSight.getName());
                    intent.putExtra(SearchSight.ADDRESS, searchSight.getAddress());
                    intent.putExtra(SearchSight.LONGITUDE, searchSight.getLongitude());
                    intent.putExtra(SearchSight.LATITUDE, searchSight.getLatitude());
                    startActivity(intent);
                } else {
                    MyApplication.getInstance().setRoute(mRoutes.get(position));
                    Intent intent = new Intent(SearchActivity.this, SearchRouteDetailActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 初始化搜索操作
     */
    private void initSearch() {

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }
                mSights.clear();
                SearchSight searchSight;
                for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
                    if (info.key != null && !TextUtil.isEmpty(info.city)) {
                        searchSight = new SearchSight();
                        searchSight.setName(info.key);
                        searchSight.setAddress(info.city + info.district);
                        searchSight.setUid(info.uid);
                        searchSight.setLongitude(info.pt.longitude);
                        searchSight.setLatitude(info.pt.latitude);
                        mSights.add(searchSight);
                    }
                    mListView.setAdapter(mSightAdapter);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void setSightText() {
        mSightText.setTextColor(getResources().getColor(R.color.white));
        mSightText.setBackgroundResource(R.color.pressed_color);
    }

    private void resetSightText() {
        mSightText.setTextColor(getResources().getColor(R.color.text_color));
        mSightText.setBackgroundDrawable(mBackground);
    }

    private void setRouteText() {
        mRouteText.setTextColor(getResources().getColor(R.color.white));
        mRouteText.setBackgroundResource(R.color.pressed_color);
    }

    private void resetRouteText() {
        mRouteText.setTextColor(getResources().getColor(R.color.text_color));
        mRouteText.setBackgroundDrawable(mBackground);
    }

    /**
     * 搜索路线
     *
     * @param key 关键字
     */
    private void searchRoute(String key) {
        BmobQuery<Route> query1 = new BmobQuery<>();
        query1.addWhereContains("name", key);
        BmobQuery<Route> query2 = new BmobQuery<>();
        query2.addWhereContains("description", key);
        List<BmobQuery<Route>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        BmobQuery<Route> query = new BmobQuery<>();
        query.or(queries);
        query.include("creator");
        query.findObjects(this, new FindListener<Route>() {
            @Override
            public void onSuccess(List<Route> list) {
                mRoutes.clear();
                for (Route route : list) {
                    mRoutes.add(route);
                }
                mListView.setAdapter(mRouteAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(0, R.anim.out_from_right);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mSuggestionSearch.destroy();
        MyApplication.getInstance().setRoute(null);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {

    }
}
