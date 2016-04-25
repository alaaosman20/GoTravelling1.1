package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.john.waveview.WaveView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.manager.BmobManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.SearchSight;
import com.weicong.gotravelling.util.DialogUtil;

public class SearchDetailsActivity extends SwipeBackBaseActivity {

    private PoiSearch mPoiSearch;
    private WebView mWebView;
    private WaveView mWaveView;

    private double mLongitude;

    private double mLatitude;

    private String mName;
    private String mAddress;
    private String mDetailUrl;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_details);

        initViews();
        initSearch();

        mUid = getIntent().getStringExtra(SearchSight.UID);
        mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                .poiUid(mUid));

        mLongitude = getIntent().getDoubleExtra(SearchSight.LONGITUDE, 0.0);
        mLatitude = getIntent().getDoubleExtra(SearchSight.LATITUDE, 0.0);
        mAddress = getIntent().getStringExtra(SearchSight.ADDRESS);
    }

    /**
     * 初始化View
     */
    private void initViews() {
        mName = getIntent().getStringExtra(SearchSight.NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

        mWaveView = (WaveView) findViewById(R.id.wave_view);

        mWebView = (WebView)findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                mWaveView.setProgress(progress);
                if (progress == 100) {
                    mWaveView.setVisibility(View.GONE);
                }
            }
        });

        FloatingActionButton collectFAB = (FloatingActionButton) findViewById(R.id.fab_collect);
        collectFAB.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_star).colorRes(R.color.white));
        collectFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = DialogUtil.createProgressDialog(SearchDetailsActivity.this);
                progressDialog.show();
                SearchSight searchSight = new SearchSight();
                searchSight.setName(mName);
                searchSight.setUid(mUid);
                searchSight.setLongitude(mLongitude);
                searchSight.setLatitude(mLatitude);
                searchSight.setDetailsUrl(mDetailUrl);
                searchSight.setAddress(mAddress);

                BmobManager.getInstance(SearchDetailsActivity.this).addCollectSight(
                        searchSight, new BmobManager.IBmobResponse() {
                            @Override
                            public void onSuccess(String data) {
                                progressDialog.dismiss();
                                View view = findViewById(R.id.root);
                                Snackbar.make(view, "收藏成功", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                progressDialog.dismiss();
                                View view = findViewById(R.id.root);
                                Snackbar.make(view, "该景点已经收藏", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        if (!UserManager.LOGIN) {
            collectFAB.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化检索操作
     */
    private void initSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                mDetailUrl = poiDetailResult.getDetailUrl();
                mWebView.loadUrl(mDetailUrl);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sight_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(0, R.anim.out_from_right);
        } else if (item.getItemId() == R.id.action_location) {
            Intent intent = new Intent(this, SightShowMapActivity.class);
            intent.putExtra(SearchSight.LONGITUDE, mLongitude);
            intent.putExtra(SearchSight.LATITUDE, mLatitude);
            intent.putExtra(SearchSight.NAME, mName);
            intent.putExtra(SearchSight.ADDRESS, mAddress);
            intent.putExtra(SearchSight.DETAILURL, mDetailUrl);
            intent.putExtra(SearchSight.UID, mUid);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mPoiSearch.destroy();
        super.onDestroy();
    }
}
