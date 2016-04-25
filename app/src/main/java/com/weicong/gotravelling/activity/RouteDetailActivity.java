package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.manager.DailyManager;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.Daily;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.util.TextUtil;
import com.weicong.gotravelling.view.FlatButton;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class RouteDetailActivity extends SwipeBackBaseActivity {

    private RouteManager mRouteManager;

    private TextView mDescriptionText;

    private Route mRoute;

    private int mPosition;

    private LinearLayout mDailyList;

    private List<Daily> mDailys;

    private DailyManager mDailyManager;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mDayText;

    private ProgressDialog mProgressDialog;

    private FloatingActionButton mStatusButton;

    private int mRouteStatus;

    private TextView mStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        mRouteManager = RouteManager.getInstance(this);
        mPosition = getIntent().getIntExtra(RouteManager.POSITION, 0);
        mRoute = mRouteManager.getRoute(mPosition);
        Integer status = mRoute.getStatus();
        if (status == null) {
            mRouteStatus = Route.PLANNING;
        } else {
            mRouteStatus = status.intValue();
        }

        if (mRouteStatus == Route.FINISHED) {
            showFinishDialog();
        }

        mDailyManager = DailyManager.getInstance(this);

        mProgressDialog = DialogUtil.createProgressDialog(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mRoute.getName());
        setSupportActionBar(toolbar);

        Integer status = mRoute.getStatus();
        String s;
        if (status == null || status == Route.PLANNING) {
            s = "Planning";
        } else if (status == Route.TRAVELLING) {
            s = "Travelling";
        } else {
            s = "Finished";
        }

        mStatusText = (TextView) findViewById(R.id.tv_route_status);
        mStatusText.setText(s);

        Integer day = mRoute.getDays();
        if (day == null) {
            s = "0天";
        } else {
            s = day.toString()+"天";
        }
        mDayText = (TextView) findViewById(R.id.tv_route_day);
        mDayText.setText(s);

        mStatusButton = (FloatingActionButton) findViewById(R.id.fab_status);
        if (mRouteStatus == Route.PLANNING) {
            mStatusButton.setImageDrawable(new IconicsDrawable(this,
                    GoogleMaterial.Icon.gmd_navigation).colorRes(R.color.white));
        } else if (mRouteStatus == Route.TRAVELLING) {
            mStatusButton.setImageDrawable(new IconicsDrawable(this,
                    GoogleMaterial.Icon.gmd_cancel).colorRes(R.color.white));
        } else {
            mStatusButton.setImageDrawable(new IconicsDrawable(this,
                    GoogleMaterial.Icon.gmd_more).colorRes(R.color.white));
        }
        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteStatus == Route.PLANNING) {
                    startRoute();
                } else if (mRouteStatus == Route.TRAVELLING){
                    cancelRoute();
                } else if (mRouteStatus == Route.FINISHED) {
                    Intent intent = new Intent(RouteDetailActivity.this, RouteFinishedActivity.class);
                    MyApplication.getInstance().setRoute(mRouteManager.getRoute(mPosition));
                    startActivity(intent);
                }
            }
        });

        mDescriptionText = (TextView) findViewById(R.id.tv_route_description);
        mDescriptionText.setText(mRoute.getDescription());
        mDescriptionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteStatus != Route.FINISHED) {
                    Intent intent = new Intent(RouteDetailActivity.this, RouteDescriptionActivity.class);
                    intent.putExtra(RouteManager.POSITION, mPosition);
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.in_from_right, R.anim.still);
                }
            }
        });
        ImageView addDailyImage = (ImageView) findViewById(R.id.iv_add);
        addDailyImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_add_circle_outline).colorRes(R.color.colorPrimary));
        addDailyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteStatus != Route.FINISHED)
                    addDaily();
            }
        });

        mDailyList = (LinearLayout) findViewById(R.id.ll_daily_list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_refresh);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        getDaily();
    }

    /**
     * 获取日程数据
     */
    private void getDaily() {
        BmobQuery<Daily> query = new BmobQuery<>();
        query.addWhereEqualTo("route", new BmobPointer(mRoute));
        query.order("day");
        query.findObjects(this, new FindListener<Daily>() {
            @Override
            public void onSuccess(List<Daily> list) {
                mSwipeRefreshLayout.setRefreshing(false);
                mDailys = list;
                mDailyManager.setDailys(list);
                for (int i = 0; i < list.size(); i++) {
                    addDailyItem(list.get(i), i);
                }
            }

            @Override
            public void onError(int i, String s) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String description = data.getStringExtra("text");
            mDescriptionText.setText(description);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.delete_route)
                    .setMessage("确定删除该路线？")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRoute();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null);
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 删除路线
     */
    private void deleteRoute() {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
        progressDialog.show();
        ArrayList<BmobObject> routes = new ArrayList<>();
        routes.add(mRoute);
        mRouteManager.deleteRoutes(routes, new RouteManager.OnRouteListener() {
            @Override
            public void onSuccess(String s) {
                progressDialog.dismiss();
                finish();
                overridePendingTransition(0, R.anim.out_from_right);
            }

            @Override
            public void onError(String s) {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 添加日程
     */
    private void addDaily() {
        View v = getLayoutInflater().inflate(R.layout.dialog_add_daily, null);

        final FlatButton okButton = (FlatButton) v.findViewById(R.id.btn_ok);
        okButton.setEnabled(false);
        final EditText remarkText = (EditText) v.findViewById(R.id.et_remark);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_daily)
                .setView(v);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        remarkText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isEmpty(s.toString().trim()) || s.length() > 30) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                mProgressDialog.show();
                final Daily daily = new Daily();
                daily.setRemark(remarkText.getText().toString());
                daily.setRoute(mRoute);
                daily.setDay(mDailys.size());
                mDailyManager.addDaily(daily, new DailyManager.OnDailyListener() {
                    @Override
                    public void onSuccess() {
                        int day = 0;
                        Integer temp = mRoute.getDays();
                        if (temp != null) {
                            day = temp.intValue();
                        }
                        day++;
                        mRoute.setDays(day);
                        mRouteManager.addDay(mRoute, new RouteManager.OnRouteListener() {
                            @Override
                            public void onSuccess(String s) {
                                mProgressDialog.dismiss();
                                mDailys.add(daily);
                                addDailyItem(daily, mDailys.size() - 1);
                                mDayText.setText(mRoute.getDays().toString() + "天");
                            }

                            @Override
                            public void onError(String s) {
                                mProgressDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        mProgressDialog.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 添加日程视图
     */
    private void addDailyItem(Daily daily, final int position) {
        View view = getLayoutInflater().inflate(R.layout.item_daily, mDailyList, false);
        TextView dailyText = (TextView) view.findViewById(R.id.tv_daily_name);
        dailyText.setText("第" + (position + 1) + "天日程");
        TextView remarkText = (TextView) view.findViewById(R.id.tv_remark);
        remarkText.setText(daily.getRemark());
        int count = mDailyList.getChildCount();
        if (count != 0) {
            View line = getLayoutInflater().inflate(R.layout.line_separator, mDailyList, false);
            mDailyList.addView(line);
        }
        mDailyList.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean modify = mRouteStatus != Route.FINISHED;
                Intent intent = new Intent(RouteDetailActivity.this, DailyDetailActivity.class);
                intent.putExtra(DailyManager.POSITION, position);
                intent.putExtra(RouteManager.STATUS, modify);
                startActivity(intent);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mRouteStatus != Route.FINISHED) {
                    showContextDialog(position);
                }
                return true;
            }
        });
    }

    /**
     * 更新日程视图
     */
    private void updateDailyItem() {
        mDailys.clear();
        mDailys = mDailyManager.getDailys();
        mDailyList.removeAllViews();
        for (int i = 0; i < mDailys.size(); i++) {
            addDailyItem(mDailys.get(i), i);
        }
    }

    /**
     * 显示日程操作对话框
     *
     * @param position 位置
     */
    private void showContextDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_daily_operation, null);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();
        View moveUpView = v.findViewById(R.id.tv_move_up);
        moveUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                mDailyManager.changeDaily(position, position - 1, new DailyManager.OnDailyListener() {
                    @Override
                    public void onSuccess() {
                        updateDailyItem();
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onError() {
                        mProgressDialog.dismiss();
                    }
                });
                alertDialog.dismiss();
            }
        });

        View moveDownView = v.findViewById(R.id.tv_move_down);
        moveDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                mDailyManager.changeDaily(position, position + 1, new DailyManager.OnDailyListener() {
                    @Override
                    public void onSuccess() {
                        updateDailyItem();
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onError() {
                        mProgressDialog.dismiss();
                    }
                });
                alertDialog.dismiss();
            }
        });

        View deleteView = v.findViewById(R.id.tv_delete);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                mDailyManager.deleteDaily(position, new DailyManager.OnDailyListener() {
                    @Override
                    public void onSuccess() {
                        mRoute.setDays(mRoute.getDays() - 1);
                        mRouteManager.addDay(mRoute, new RouteManager.OnRouteListener() {
                            @Override
                            public void onSuccess(String s) {
                                updateDailyItem();
                                mProgressDialog.dismiss();
                                mDayText.setText(mRoute.getDays().toString() + "天");
                            }

                            @Override
                            public void onError(String s) {
                                mProgressDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
                alertDialog.dismiss();
            }
        });
        if (mDailys.size() == 1) {
            moveUpView.setVisibility(View.GONE);
            moveDownView.setVisibility(View.GONE);
        } else if (position == 0) {
            moveUpView.setVisibility(View.GONE);
        } else if (position == mDailys.size()-1) {
            moveDownView.setVisibility(View.GONE);
        }
        alertDialog.show();
    }

    /**
     * 开始路线
     */
    private void startRoute() {
        if (TextUtil.isEmpty(mRoute.getDescription())) {
            Snackbar.make(findViewById(R.id.root), "请添加路线描述", Snackbar.LENGTH_SHORT).show();
        } else if (mRoute.getDays() == null || mRoute.getDays() == 0) {
            Snackbar.make(findViewById(R.id.root), "请添加日程", Snackbar.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.start_route_title)
                    .setMessage(R.string.start_route_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sureStartRoute();
                        }
                    })
                    .setNegativeButton(R.string.no, null);
            builder.create().show();
        }
    }

    /**
     * 确定开始路线
     */
    private void sureStartRoute() {
        Boolean status = UserManager.getInstance(RouteDetailActivity.this).getStartRoute();
        if (status == null || !status) {
            mProgressDialog.show();
            mRoute.setStatus(Route.TRAVELLING);
            mRouteManager.startRoute(mRoute, new RouteManager.OnRouteListener() {
                @Override
                public void onSuccess(String s) {
                    mRouteStatus = Route.TRAVELLING;
                    mProgressDialog.dismiss();
                    mStatusText.setText("Travelling");
                    mStatusButton.setImageDrawable(new IconicsDrawable(RouteDetailActivity.this,
                            GoogleMaterial.Icon.gmd_cancel).colorRes(R.color.white));
                    Snackbar.make(findViewById(R.id.root), R.string.start_route_success, Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String s) {
                    mProgressDialog.dismiss();
                }
            });

        } else {
            Snackbar.make(findViewById(R.id.root), R.string.start_route_error, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * 取消路线
     */
    private void cancelRoute() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cancel_route_title)
                .setMessage(R.string.cancel_route_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sureCancelRoute();
                    }
                })
                .setNegativeButton(R.string.no, null);
        builder.create().show();
    }

    /**
     * 确定取消路线
     */
    private void sureCancelRoute() {
        mProgressDialog.show();
        mRoute.setStatus(Route.PLANNING);
        mRouteManager.cancelRoute(mRoute, new RouteManager.OnRouteListener() {
            @Override
            public void onSuccess(String s) {
                mRouteStatus = Route.PLANNING;
                mProgressDialog.dismiss();
                mStatusText.setText("Planning");
                mStatusButton.setImageDrawable(new IconicsDrawable(RouteDetailActivity.this,
                        GoogleMaterial.Icon.gmd_navigation).colorRes(R.color.white));
                Snackbar.make(findViewById(R.id.root), R.string.cancel_route_success, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String s) {
                mProgressDialog.dismiss();
            }
        });
    }

    /**
     * 显示路线已结束的通知
     */
    private void showFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.route_finish_notice)
                .setMessage(R.string.route_finish_notice_message)
                .setPositiveButton(R.string.know, null);
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().setRoute(null);
        super.onDestroy();
    }
}
