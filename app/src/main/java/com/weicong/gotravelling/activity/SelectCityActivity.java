package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.SortAdapter;
import com.weicong.gotravelling.city.CharacterParser;
import com.weicong.gotravelling.city.CitySortModel;
import com.weicong.gotravelling.city.PinyinComparator;
import com.weicong.gotravelling.event.CityEvent;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class SelectCityActivity extends SwipeBackBaseActivity {

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private CharacterParser characterParser;
    private List<CitySortModel> SourceDateList;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        mProgressDialog = DialogUtil.createProgressDialog(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.select_city);
        setSupportActionBar(toolbar);

        characterParser = CharacterParser.getInstance();

        sideBar = (SideBar) findViewById(R.id.sideBar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.listView);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mProgressDialog.show();
                String city = ((CitySortModel) adapter.getItem(position)).getName();
                UserManager.getInstance(SelectCityActivity.this).setCity(city,
                        new UserManager.OnUpdateListener() {
                            @Override
                            public void onSuccess() {
                                EventBus.getDefault().post(new CityEvent());
                                mProgressDialog.dismiss();
                                finish();
                                overridePendingTransition(0, R.anim.out_from_right);
                            }

                            @Override
                            public void onError() {
                                mProgressDialog.dismiss();
                            }
                        });
            }
        });

        SourceDateList = filledData(getResources().getStringArray(R.array.citys));
        Collections.sort(SourceDateList, new PinyinComparator());
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);
    }

    private List<CitySortModel> filledData(String[] date) {
        List<CitySortModel> mSortList = new ArrayList<>();
        ArrayList<String> indexString = new ArrayList<>();

        for (int i = 0; i < date.length; i++) {
            CitySortModel sortModel = new CitySortModel();
            sortModel.setName(date[i]);
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {

                //对重庆多音字做特殊处理
                if (pinyin.startsWith("zhongqing")) {
                    sortString = "C";
                    sortModel.setSortLetters("C");
                } else {
                    sortModel.setSortLetters(sortString.toUpperCase());
                }

                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            }

            mSortList.add(sortModel);
        }
        Collections.sort(indexString);
        sideBar.setIndexText(indexString);
        return mSortList;
    }
}
