package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.Sight;
import com.weicong.gotravelling.util.TextUtil;

public class SightDetailActivity extends SwipeBackBaseActivity {

    private WebView mWebView;
    private WaveView mWaveView;

    private PoiSearch mPoiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_details);

        initSearch();
        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        String title = getIntent().getStringExtra(Sight.NAME);
        String detailUrl = getIntent().getStringExtra(Sight.DETAILURL);
        String uid = getIntent().getStringExtra(Sight.UID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

        findViewById(R.id.fab_collect).setVisibility(View.GONE);

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
        if (TextUtil.isEmpty(detailUrl)) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(uid));
        } else {
            mWebView.loadUrl(detailUrl);
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
                String detailUrl = poiDetailResult.getDetailUrl();
                mWebView.loadUrl(detailUrl);
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
        mPoiSearch.destroy();
        super.onDestroy();
    }
}
