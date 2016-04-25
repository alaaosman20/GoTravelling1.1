package com.weicong.gotravelling.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.SearchSight;
import com.weicong.gotravelling.model.Sight;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.SPUtil;
import com.weicong.gotravelling.util.TextUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * User管理类，单例模式
 */
public class UserManager {

    /**
     * 用于判断当前是否已经登录
     */
    public static boolean LOGIN = false;

    /**
     * 管理的User对象
     */
    private User mUser;

    /**
     * 上下文
     */
    private Context mContext;

    private static UserManager sUserManager = null;

    private Bitmap mHeadImage;

    /**
     * 不允许调用构造函数
     */
    private UserManager(Context context) {
        mContext = context;
        mUser = BmobUser.getCurrentUser(mContext, User.class);
        if (mUser != null)
            initHeadImage();

        LOGIN = mUser != null;//[truyayong]这么写容易有歧义
    }

    /**
     * 获取UserManager对象
     *
     * @return UserManager
     */
    public static UserManager getInstance(Context context) {
        if (sUserManager == null) {
            sUserManager = new UserManager(context);
        }
        return sUserManager;//[truyayong]单例写的太加单会不会不安全
    }

    /**
     * 获取User对象
     *
     * @return User对象
     */
    public User getUser() {
        return mUser;
    }

    /**
     * 登出
     *
     * @param context 上下文
     */
    public static void logout(Context context) {
        LOGIN = false;
        BmobUser.logOut(context);
        sUserManager = null;
    }

    public String getUsername() {
        return mUser.getUsername();
    }

    public String getIntro() {
        if (TextUtil.isEmpty(mUser.getIntro())) {
            return "(添加一句话介绍自己)";
        } else {
            return mUser.getIntro();
        }
    }

    public String getAge() {
        if (mUser.getAge() == null) {
            return "0岁";
        } else {
            return mUser.getAge().toString() + "岁";
        }
    }

    public boolean getGender() {
        if (mUser.getGender() == null) {
            return false;
        } else {
            return mUser.getGender();
        }
    }

    public String getRouteNum() {
        if (mUser.getRouteNum() == null) {
            return "0";
        } else {
            return mUser.getRouteNum().toString();
        }
    }

    public String getFollowNum() {
        if (mUser.getFollowNum() == null) {
            return "0";
        } else {
            return mUser.getFollowNum().toString();
        }
    }

    public String getCommentRouteNum() {
        if (mUser.getCommentRoutesNum() == null) {
            return "0";
        } else {
            return mUser.getCommentRoutesNum().toString();
        }
    }

    public String getSignSightNum() {
        if (mUser.getSignSightsNum() == null) {
            return "0";
        } else {
            return mUser.getSignSightsNum().toString();
        }
    }

    public String getFinishedRouteNum() {
        if (mUser.getFinishedRouteNum() == null) {
            return "0";
        } else {
            return mUser.getFinishedRouteNum().toString();
        }
    }

    public String getCity() {
        if (TextUtil.isEmpty(mUser.getCity())) {
            return SPUtil.get(mContext, MapAPIManager.CITY, "广州市").toString();
        } else {
            return mUser.getCity();
        }
    }

    public String getPhone() {
        return mUser.getMobilePhoneNumber();
    }

    public String getEmail() {
        return mUser.getEmail();
    }

    public String getDescription() {
        return mUser.getDescription();
    }

    public boolean getPhoneValidate() {
        return mUser.getMobilePhoneNumberVerified();
    }

    public Boolean getEmailValidate() {
        return mUser.getEmailVerified();
    }

    /**
     * 设置头像
     *
     * @param imageView ImageView实例
     */
    public void setHeadImage(final ImageView imageView) {
        if (mHeadImage != null) {
            imageView.setImageBitmap(mHeadImage);
        } else {

            BmobManager.getInstance(mContext).download(mUser.getImage(),
                    new BmobManager.IBmobResponse() {
                        @Override
                        public void onSuccess(String data) {
                            mHeadImage.recycle();
                            mHeadImage = BitmapFactory.decodeFile(data);
                            imageView.setImageBitmap(mHeadImage);
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
        }
    }

    /**
     * 登录成功，重新获取用户信息
     */
    public void loginSuccess() {

        mUser = BmobUser.getCurrentUser(mContext, User.class);
        //位置信息上传
        MapAPIManager.getInstance(mContext).uploadInfo();

        initHeadImage();
    }

    /**
     * 初始化头像
     */
    private void initHeadImage() {
        String image = mUser.getImage();
        if (!TextUtil.isEmpty(image)) {
            BmobManager.getInstance(mContext).download(image, new BmobManager.IBmobResponse() {
                @Override
                public void onSuccess(String data) {
                    mHeadImage = BitmapFactory.decodeFile(data);
                }

                @Override
                public void onError(String error) {

                }
            });
        } else {
            mHeadImage = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.default_avatar);
        }
    }

    /**
     * 设置头像图片的文件名
     *
     * @param imageName 文件名
     * @param localPath 本地路径
     * @param listener  回调函数
     */
    public void setImageName(String imageName, String localPath,
                             final OnUpdateListener listener) {

        final String image = mUser.getImage();
        mUser.setImage(imageName);
        if (!TextUtil.isEmpty(image)) {
            BmobManager.getInstance(mContext).delete(image);
        }

        mHeadImage = BitmapFactory.decodeFile(localPath);

        User user = new User();
        user.setImage(imageName);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 设置年龄
     *
     * @param age      年龄
     * @param listener 回调函数
     */
    public void setAge(Integer age, final OnUpdateListener listener) {
        User user = new User();
        user.setAge(age);
        mUser.setAge(age);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 设置性别
     *
     * @param gender   false为男，true为女
     * @param listener 回调函数
     */
    public void setGender(Boolean gender, final OnUpdateListener listener) {
        User user = new User();
        user.setGender(gender);
        mUser.setGender(gender);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 设置一句话介绍
     *
     * @param intro    一句话介绍
     * @param listener 回调函数
     */
    public void setIntro(String intro, final OnUpdateListener listener) {
        User user = new User();
        user.setIntro(intro);
        mUser.setIntro(intro);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 设置个人介绍
     *
     * @param description 个人介绍
     * @param listener    回调函数
     */
    public void setDescription(String description, final OnUpdateListener listener) {
        User user = new User();
        user.setDescription(description);
        mUser.setDescription(description);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param listener    回调函数
     */
    public void changePassword(String oldPassword, String newPassword, final OnUpdateListener listener) {
        BmobUser.updateCurrentUserPassword(mContext, oldPassword, newPassword, new UpdateListener() {

            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int code, String msg) {
                listener.onError();
            }
        });
    }

    /**
     * 设置手机号
     *
     * @param phone    手机号
     * @param listener 回调函数
     */
    public void setPhone(String phone, final OnUpdateListener listener) {
        User user = new User();
        user.setMobilePhoneNumber(phone);
        user.setMobilePhoneNumberVerified(true);
        mUser.setMobilePhoneNumber(phone);
        mUser.setMobilePhoneNumberVerified(true);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                listener.onError();
            }
        });
    }

    /**
     * 设置邮箱
     *
     * @param email    邮箱
     * @param listener 回调函数
     */
    public void setEmail(String email, final OnUpdateListener listener) {
        User user = new User();
        user.setEmail(email);
        mUser.setEmail(email);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                listener.onError();
            }
        });
    }

    /**
     * 设置城市
     *
     * @param city     城市
     * @param listener 回调函数
     */
    public void setCity(String city, final OnUpdateListener listener) {
        User user = new User();
        user.setCity(city);
        mUser.setCity(city);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                if (listener != null) {
                    listener.onError();
                }
            }
        });
    }

    /**
     * 清除
     */
    public static void clear() {
        sUserManager = null;
    }

    /**
     * 添加收藏景点
     *
     * @param searchSight 景点
     * @param listener    回调函数
     */
    public void addCollectSight(SearchSight searchSight, final OnUpdateListener listener) {
        BmobRelation collectSights = new BmobRelation();
        collectSights.add(searchSight);
        User user = new User();
        mUser.setCollectSights(collectSights);
        user.setCollectSights(collectSights);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 收藏路线
     *
     * @param route    路线
     * @param listener 回调
     */
    public void addCollectRoute(Route route, final OnUpdateListener listener) {
        User user = new User();
        BmobRelation relation = new BmobRelation();
        relation.add(route);
        mUser.setCollectRoutes(relation);
        user.setCollectRoutes(relation);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 关注路线
     *
     * @param route    路线
     * @param listener 回调
     */
    public void addFollowRoute(Route route, final OnUpdateListener listener) {
        User user = new User();
        BmobRelation relation = new BmobRelation();
        relation.add(route);
        mUser.setFollowRoutes(relation);
        user.setFollowRoutes(relation);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 新增路线
     *
     * @param listener 回调
     */
    public void addRoute(final OnUpdateListener listener) {
        Integer temp = mUser.getRouteNum();
        int routeNum = 0;
        if (temp != null) {
            routeNum = temp.intValue();
        }
        routeNum++;
        mUser.setRouteNum(routeNum);
        User user = new User();
        user.setRouteNum(routeNum);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 删除路线
     *
     * @param routeNum 路线的数目
     * @param listener 回调
     */
    public void deleteRoute(int routeNum, final OnUpdateListener listener) {
        int temp = mUser.getRouteNum();
        temp = temp - routeNum;
        mUser.setRouteNum(temp);
        User user = new User();
        user.setRouteNum(temp);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 获取是否已经开始了路线信息
     *
     * @return 路线状态
     */
    public Boolean getStartRoute() {
        return mUser.getStartRoute();
    }

    /**
     * 设置路线状态
     *
     * @param status   路线状态
     * @param listener 回调
     */
    public void setStartRoute(Boolean status, final OnUpdateListener listener) {
        User user = new User();
        mUser.setStartRoute(status);
        user.setStartRoute(status);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 结束路线
     *
     * @param listener 回调
     */
    public void overRoute(final OnUpdateListener listener) {
        int finishRouteNum = 0;
        if (mUser.getFinishedRouteNum() != null) {
            finishRouteNum = mUser.getFinishedRouteNum().intValue();
        }
        finishRouteNum++;
        User user = new User();
        user.setStartRoute(false);
        user.setFinishedRouteNum(finishRouteNum);

        mUser.setStartRoute(false);
        mUser.setFinishedRouteNum(finishRouteNum);

        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });

    }

    /**
     * 删除结束的路线
     *
     * @param finishNum 路线的数量
     * @param listener  回调
     */
    public void deleteFinishRoute(int finishNum, final OnUpdateListener listener) {
        User user = new User();
        int finishRouteNum = mUser.getFinishedRouteNum();

        mUser.setFinishedRouteNum(finishRouteNum - finishNum);
        user.setFinishedRouteNum(finishRouteNum - finishNum);

        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 取消收藏景点
     *
     * @param sight    景点
     * @param listener 回调
     */
    public void notCollectSight(SearchSight sight, final OnUpdateListener listener) {
        BmobRelation relation = new BmobRelation();
        relation.remove(sight);
        mUser.setCollectSights(relation);
        User user = new User();
        user.setCollectSights(relation);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 取消收藏路线
     *
     * @param route    路线
     * @param listener 回调
     */
    public void notCollectRoute(Route route, final OnUpdateListener listener) {
        BmobRelation relation = new BmobRelation();
        relation.remove(route);
        mUser.setCollectRoutes(relation);
        User user = new User();
        user.setCollectRoutes(relation);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 取消关注路线
     *
     * @param route 路线
     * @param listener 回调
     */
    public void notFollowRoute(Route route, final OnUpdateListener listener) {
        BmobRelation relation = new BmobRelation();
        relation.remove(route);
        mUser.setFollowRoutes(relation);
        User user = new User();
        user.setFollowRoutes(relation);
        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 取消关注用户
     *
     * @param user 用户
     * @param listener 回调
     */
    public void notFollowUser(User user, final OnUpdateListener listener) {
        BmobRelation relation = new BmobRelation();
        relation.remove(user);
        int followNum = mUser.getFollowNum();
        followNum--;
        mUser.setFollowNum(followNum);
        mUser.setFollowUsers(relation);

        User temp = new User();
        temp.setFollowNum(followNum);
        temp.setFollowUsers(relation);
        temp.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 关注用户
     *
     * @param user     用户
     * @param listener 回调监听
     */
    public void followUser(final User user, final OnUpdateListener listener) {

        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("followUsers", new BmobPointer(mUser));
        query.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                int i = 0;
                for (; i < list.size(); i++) {
                    if (user.getObjectId().equals(list.get(i).getObjectId())) {
                        break;
                    }
                }
                if (i == list.size()) {
                    User user1 = new User();

                    BmobRelation relation1 = new BmobRelation();
                    relation1.add(user);
                    user1.setFollowUsers(relation1);
                    int followNum = 0;
                    if (mUser.getFollowNum() != null) {
                        followNum = mUser.getFollowNum();
                    }
                    followNum++;
                    user1.setFollowNum(followNum);
                    mUser.setFollowUsers(relation1);
                    mUser.setFollowNum(followNum);
                    user1.update(mContext, mUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            listener.onSuccess();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            listener.onError();
                        }
                    });
                } else {
                    listener.onError();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 增加评论过的路线
     *
     * @param route 路线
     * @param listener 回调
     */
    public void addComment(final Route route, final OnUpdateListener listener) {

        BmobQuery<Route> query = new BmobQuery<>();
        query.addWhereRelatedTo("commentRoutes", new BmobPointer(mUser));
        query.findObjects(mContext, new FindListener<Route>() {
            @Override
            public void onSuccess(List<Route> list) {
                int i = 0;
                for (; i < list.size(); i++) {
                    if (list.get(i).getObjectId().equals(route.getObjectId())) {
                        break;
                    }
                }
                if (i == list.size()) {
                    BmobRelation relation = new BmobRelation();
                    relation.add(route);

                    int commentNum = 0;
                    if (mUser.getCommentRoutesNum() != null) {
                        commentNum = mUser.getCommentRoutesNum();
                    }
                    commentNum++;

                    mUser.setCommentRoutes(relation);
                    mUser.setCommentRoutesNum(commentNum);

                    User user = new User();
                    user.setCommentRoutesNum(commentNum);
                    user.setCommentRoutes(relation);
                    user.update(mContext, mUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            listener.onSuccess();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            listener.onError();
                        }
                    });
                } else {
                    listener.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError();
            }
        });

    }

    /**
     * 景点签到
     *
     * @param sight    景点
     * @param listener 回调
     */
    public void sightCheck(Sight sight, final OnUpdateListener listener) {
        User user = new User();
        int signNum = 0;
        if (mUser.getSignSightsNum() != null) {
            signNum = mUser.getSignSightsNum().intValue();
        }
        signNum++;
        user.setSignSightsNum(signNum);
        BmobRelation relation = new BmobRelation();
        relation.add(sight);
        user.setSignSights(relation);

        mUser.setSignSights(relation);
        mUser.setSignSightsNum(signNum);

        user.update(mContext, mUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

public interface OnUpdateListener {
    void onSuccess();

    void onError();
}
}
