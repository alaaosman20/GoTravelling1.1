package com.weicong.gotravelling.manager;

import android.content.Context;

import com.weicong.gotravelling.model.Daily;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.Sight;
import com.weicong.gotravelling.model.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 路线管理类，单例模式
 */
public class RouteManager {

    public static final String POSITION = "route_manager_position";
    public static final String STATUS = "route_manager_status";

    private Context mContext;
    private List<Route> mRoutes = new ArrayList<>();

    private boolean mFlag;

    private static RouteManager sRouteManager = null;

    private RouteManager(Context context) {
        mContext = context;
    }

    public static RouteManager getInstance(Context context) {
        if (sRouteManager == null) {
            sRouteManager = new RouteManager(context);
        }

        return sRouteManager;
    }

    /**
     * 复制路线
     *
     * @param route 路线
     * @param listener 回调
     */
    public void copyRoute(final Route route, final OnRouteListener listener) {
        final Route myRoute = new Route();
        myRoute.setCreator(UserManager.getInstance(mContext).getUser());
        myRoute.setName(route.getName());
        myRoute.setDescription(route.getDescription());
        myRoute.setStatus(Route.PLANNING);
        myRoute.setDays(route.getDays());
        myRoute.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                UserManager.getInstance(mContext).addRoute(new UserManager.OnUpdateListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
                BmobQuery<Daily> query = new BmobQuery<>();
                query.addWhereEqualTo("route", new BmobPointer(route));
                query.order("day");
                query.findObjects(mContext, new FindListener<Daily>() {
                    @Override
                    public void onSuccess(List<Daily> list) {
                        copyDaily(myRoute, list, listener);
                    }

                    @Override
                    public void onError(int i, String s) {
                        listener.onError(s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }

    /**
     * 复制日程
     *
     * @param route 路线
     * @param dailies 日程数据
     * @param listener 回调
     */
    private void copyDaily(final Route route, final List<Daily> dailies, final OnRouteListener listener) {

        List<BmobObject> myDailys = new ArrayList<>();
        Daily daily;
        for (int i = 0; i < dailies.size(); i++) {
            daily = new Daily();
            daily.setRoute(route);
            daily.setRemark(dailies.get(i).getRemark());
            daily.setDay(dailies.get(i).getDay());
            myDailys.add(daily);
        }
        new BmobObject().insertBatch(mContext, myDailys, new SaveListener() {
            @Override
            public void onSuccess() {
                BmobQuery<Daily> query = new BmobQuery<>();
                query.addWhereEqualTo("route", new BmobPointer(route));
                query.order("day");
                query.findObjects(mContext, new FindListener<Daily>() {
                    @Override
                    public void onSuccess(List<Daily> list) {
                        if (!list.isEmpty()) {
                            copySight(list, dailies, 0, listener);
                        } else {
                            listener.onSuccess(null);
                        }

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }

    /**
     * 复制景点
     *
     * @param myDailys 日程列表
     * @param dailys 日程列表
     * @param current 当前位置
     * @param listener 回调
     */
    private void copySight(final List<Daily> myDailys, final List<Daily> dailys,
                           final int current, final OnRouteListener listener) {
        BmobQuery<Sight> query = new BmobQuery<>();
        query.addWhereEqualTo("daily", new BmobPointer(dailys.get(current)));
        query.order("index");
        query.findObjects(mContext, new FindListener<Sight>() {
            @Override
            public void onSuccess(List<Sight> list) {
                List<BmobObject> sights = new ArrayList<>();
                Sight sight;
                for (int i = 0; i < list.size(); i++) {
                    sight = new Sight();
                    sight.setDaily(myDailys.get(current));
                    sight.setName(list.get(i).getName());
                    sight.setAddress(list.get(i).getAddress());
                    sight.setLongitude(list.get(i).getLongitude());
                    sight.setLatitude(list.get(i).getLatitude());
                    sight.setDetailUrl(list.get(i).getDetailUrl());
                    sight.setUid(list.get(i).getUid());
                    sight.setIndex(list.get(i).getIndex());
                    sights.add(sight);
                }
                new BmobObject().insertBatch(mContext, sights, new SaveListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
                if (current+1 != myDailys.size()) {
                    copySight(myDailys, dailys, current + 1, listener);
                } else {
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(s);
            }
        });
    }

    /**
     * 添加路线
     *
     * @param route    路线
     * @param listener 回调
     */
    public void addRoute(final Route route, final OnRouteListener listener) {
        BmobQuery<Route> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("name", route.getName());
        BmobQuery<Route> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("creator", UserManager.getInstance(mContext).getUser());
        List<BmobQuery<Route>> andQuerys = new ArrayList<>();
        andQuerys.add(query1);
        andQuerys.add(query2);

        BmobQuery<Route> mainQuery = new BmobQuery<>();
        mainQuery.and(andQuerys);
        mainQuery.findObjects(mContext, new FindListener<Route>() {
            @Override
            public void onSuccess(List<Route> list) {
                if (list.size() > 0) {
                    listener.onError(null);
                } else {
                    saveRoute(route, listener);
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(null);
            }
        });
    }

    /**
     * 保存路线
     *
     * @param route    路线
     * @param listener 回调
     */
    private void saveRoute(final Route route, final OnRouteListener listener) {
        route.setCreator(UserManager.getInstance(mContext).getUser());
        route.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                UserManager.getInstance(mContext).addRoute(new UserManager.OnUpdateListener() {
                    @Override
                    public void onSuccess() {
                        listener.onSuccess(null);
                        mRoutes.add(0, route);
                    }

                    @Override
                    public void onError() {
                        listener.onError(null);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }

    public Route getRoute(int position) {
        return mRoutes.get(position);
    }

    public void addRoutes(List<Route> items) {
        mRoutes.clear();
        mRoutes.addAll(items);
    }

    public List<Route> getRoutes() {
        ArrayList<Route> routes = new ArrayList<>();
        for (Route route : mRoutes) {
            routes.add(route);
        }
        return routes;
    }

    /**
     * 批量删除路线
     *
     * @param routes   要删除的路线列表
     * @param listener 回调
     */
    public void deleteRoutes(final List<BmobObject> routes, final OnRouteListener listener) {
        mFlag = false;
        Route route;
        int finishedNum = 0;
        for (int i = 0; i < routes.size(); i++) {
            route = (Route) routes.get(i);
            if (route.getStatus() != null && route.getStatus() == Route.TRAVELLING) {
                mFlag = true;
            } else if (route.getStatus() != null && route.getStatus() == Route.FINISHED) {
                finishedNum++;
            }
        }
        final int routeNum = routes.size();
        final int num = finishedNum;
        new BmobObject().deleteBatch(mContext, routes, new DeleteListener() {
            @Override
            public void onSuccess() {
                UserManager.getInstance(mContext).deleteRoute(routeNum,
                        new UserManager.OnUpdateListener() {
                            @Override
                            public void onSuccess() {
                                if (mFlag) {
                                    UserManager.getInstance(mContext).setStartRoute(false,
                                            new UserManager.OnUpdateListener() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            });
                                }

                                if (num != 0) {
                                    UserManager.getInstance(mContext).deleteFinishRoute(num,
                                            new UserManager.OnUpdateListener() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            });
                                }

                                listener.onSuccess(null);
                                mRoutes.removeAll(routes);

                            }

                            @Override
                            public void onError() {
                                listener.onError(null);
                            }
                        });
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(null);
            }
        });
    }

    /**
     * 增加天数
     *
     * @param route    路线
     * @param listener 回调
     */
    public void addDay(Route route, final OnRouteListener listener) {
        Route r = new Route();
        r.setDays(route.getDays());
        r.update(mContext, route.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess(null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(null);
            }
        });
    }

    public int getSize() {
        return mRoutes.size();
    }

    public interface OnRouteListener {
        void onSuccess(String s);

        void onError(String s);
    }

    /**
     * 开始路线
     *
     * @param route    路线
     * @param listener 回调
     */
    public void startRoute(Route route, final OnRouteListener listener) {
        route.update(mContext, route.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                UserManager.getInstance(mContext).setStartRoute(true, new UserManager.OnUpdateListener() {
                    @Override
                    public void onSuccess() {
                        listener.onSuccess(null);
                    }

                    @Override
                    public void onError() {
                        listener.onError(null);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }

    /**
     * 添加评论
     *
     * @param route 路线
     * @param listener 回调
     */
    public void addComment(final Route route, final OnRouteListener listener) {
        int commentNum = 0;
        if (route.getCommentNum() != null) {
            commentNum = route.getCommentNum();
        }
        commentNum++;
        route.setCommentNum(commentNum);
        route.update(mContext, route.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                UserManager.getInstance(mContext).addComment(route,
                        new UserManager.OnUpdateListener() {
                            @Override
                            public void onSuccess() {
                               listener.onSuccess(null);
                            }

                            @Override
                            public void onError() {
                                listener.onError(null);
                            }
                        });
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }

    /**
     * 取消路线
     *
     * @param route    路线
     * @param listener 回调
     */
    public void cancelRoute(Route route, final OnRouteListener listener) {
        route.update(mContext, route.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                UserManager.getInstance(mContext).setStartRoute(false, new UserManager.OnUpdateListener() {
                    @Override
                    public void onSuccess() {
                        listener.onSuccess(null);
                    }

                    @Override
                    public void onError() {
                        listener.onError(null);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }

    /**
     * 路线点赞
     *
     * @param route 路线
     * @param listener 回调
     */
    public void good(Route route, final OnRouteListener listener) {
        int favor = 0;
        if (route.getFavor() != null) {
            favor = route.getFavor().intValue();
        }
        favor++;
        route.setFavor(favor);
        route.update(mContext, route.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess(null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }

    /**
     * 路线收藏
     *
     * @param route 路线
     * @param listener 回调
     */
    public void collect(final Route route, final OnRouteListener listener) {
        User user = UserManager.getInstance(mContext).getUser();
        BmobQuery<Route> query = new BmobQuery<>();
        query.addWhereRelatedTo("collectRoutes", new BmobPointer(user));
        query.findObjects(mContext, new FindListener<Route>() {
            @Override
            public void onSuccess(final List<Route> list) {
                int i = 0;
                for (; i < list.size(); i++) {
                    if (route.getObjectId().equals(list.get(i).getObjectId())) {
                        break;
                    }
                }
                if (i == list.size()) {
                    UserManager.getInstance(mContext).addCollectRoute(route, new UserManager.OnUpdateListener() {
                        @Override
                        public void onSuccess() {
                            listener.onSuccess(null);
                        }

                        @Override
                        public void onError() {
                            listener.onError(null);
                        }
                    });
                } else {
                    listener.onError("您已经收藏了该路线");
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 关注路线
     *
     * @param route 路线
     * @param listener 回调监听
     */
    public void followRoute(final Route route, final OnRouteListener listener) {
        User user = UserManager.getInstance(mContext).getUser();
        BmobQuery<Route> query = new BmobQuery<>();
        query.addWhereRelatedTo("followRoutes", new BmobPointer(user));
        query.findObjects(mContext, new FindListener<Route>() {
            @Override
            public void onSuccess(final List<Route> list) {
                int i = 0;
                for (; i < list.size(); i++) {
                    if (route.getObjectId().equals(list.get(i).getObjectId())) {
                        break;
                    }
                }
                if (i == list.size()) {
                    UserManager.getInstance(mContext).addFollowRoute(route, new UserManager.OnUpdateListener() {
                        @Override
                        public void onSuccess() {
                            int followNum = 0;
                            if (route.getFollowNum() != null)
                                followNum = route.getFollowNum();
                            followNum++;
                            route.setFollowNum(followNum);
                            route.update(mContext, route.getObjectId(),
                                    new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            listener.onSuccess(null);
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            listener.onError(s);
                                        }
                                    });
                        }

                        @Override
                        public void onError() {
                            listener.onError(null);
                        }
                    });
                } else {
                    listener.onError("您已经关注了该路线");
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 查看路线
     *
     * @param route 路线
     * @param listener 回调
     */
    public void watch(Route route, final OnRouteListener listener) {
        int watchNum = 0;
        if (route.getWatchNum() != null) {
            watchNum = route.getWatchNum().intValue();
        }
        watchNum++;
        route.setWatchNum(watchNum);
        route.update(mContext, new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess(null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }

    /**
     * 清除
     */
    public static void clear() {
        sRouteManager = null;
    }
}
