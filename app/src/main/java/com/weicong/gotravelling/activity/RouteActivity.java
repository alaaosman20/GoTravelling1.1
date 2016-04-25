package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.RouteAdapter;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.util.TextUtil;
import com.weicong.gotravelling.view.FlatButton;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class RouteActivity extends SwipeBackBaseActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private RouteAdapter mAdapter;
    private List<Route> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.my_route);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RouteActivity.this, RouteDetailActivity.class);
                intent.putExtra(RouteManager.POSITION, position);
                startActivity(intent);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRouteData();
            }
        });

        FloatingActionButton newButton = (FloatingActionButton) findViewById(R.id.action_new);
        newButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_open_in_new).colorRes(R.color.white).paddingDp(2));
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoute();
            }
        });

        FloatingActionButton copyButton = (FloatingActionButton) findViewById(R.id.action_copy);
        copyButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_content_copy).colorRes(R.color.white).paddingDp(2));
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteActivity.this, CollectRouteActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 获取路线数据
     */
    private void getRouteData() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        BmobQuery<Route> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.addWhereEqualTo("creator", UserManager.getInstance(this).getUser());
        query.findObjects(this, new FindListener<Route>() {
            @Override
            public void onSuccess(List<Route> list) {
                if (list.size() == 0) {
                    findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
                }
                RouteManager.getInstance(RouteActivity.this).addRoutes(list);
                mItems = list;
                mAdapter = new RouteAdapter(RouteActivity.this, mItems);
                mListView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(RouteActivity.this, s, Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 增加路线
     */
    private void addRoute() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_route, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_route).setView(v);
        final AlertDialog alertDialog = builder.create();

        final FlatButton okButton = (FlatButton) v.findViewById(R.id.btn_ok);
        okButton.setEnabled(false);
        final EditText routeNameText = (EditText) v.findViewById(R.id.et_route_name);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = DialogUtil.createProgressDialog(RouteActivity.this);
                progressDialog.show();
                final Route route = new Route();
                route.setName(routeNameText.getText().toString());
                RouteManager.getInstance(RouteActivity.this).addRoute(route,
                        new RouteManager.OnRouteListener() {
                            @Override
                            public void onSuccess(String s) {
                                progressDialog.dismiss();
                                alertDialog.dismiss();
                                findViewById(R.id.tv_empty).setVisibility(View.GONE);
                                mItems.add(0, route);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(String s) {
                                progressDialog.dismiss();
                                Toast.makeText(RouteActivity.this, "存在相同的路线名称", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        routeNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtil.isEmpty(s.toString().trim())) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            Intent intent = new Intent(this, DeleteRouteActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRouteData();
    }
}
