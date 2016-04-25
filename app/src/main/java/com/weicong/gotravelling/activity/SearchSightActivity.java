package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.SearchAdapter;
import com.weicong.gotravelling.model.SearchSight;
import com.weicong.gotravelling.model.Sight;
import com.weicong.gotravelling.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchSightActivity extends SwipeBackBaseActivity {

    private EditText mSearchText;
    private ImageView mClearImage;

    private ListView mListView;

    private SuggestionSearch mSuggestionSearch;

    private SearchAdapter mSightAdapter;
    private List<SearchSight> mSights = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sight);

        initSearch();

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL);

        View customView = getLayoutInflater().inflate(R.layout.layout_search, null);
        mSearchText = (EditText) customView.findViewById(R.id.et_search);
        mSearchText.setHint("搜索景点");
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
                if (s.length() > 0) {
                    mClearImage.setVisibility(View.VISIBLE);
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(s.toString())
                            .city("全国"));
                } else {
                    mClearImage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        actionBar.setCustomView(customView, params);

        mListView = (ListView) findViewById(R.id.listView);
        mSightAdapter = new SearchAdapter(this, mSights);
        mListView.setAdapter(mSightAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchSight searchSight = mSights.get(position);
                Intent intent = new Intent();
                intent.putExtra(Sight.NAME, searchSight.getName());
                intent.putExtra(Sight.ADDRESS, searchSight.getAddress());
                intent.putExtra(Sight.LONGITUDE, searchSight.getLongitude());
                intent.putExtra(Sight.LATITUDE, searchSight.getLatitude());
                intent.putExtra(Sight.DETAILURL, searchSight.getDetailUrl());
                intent.putExtra(Sight.UID, searchSight.getUid());
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.still, R.anim.out_from_right);
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
                    mSightAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(0, R.anim.out_from_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mSuggestionSearch.destroy();
        super.onDestroy();
    }
}
