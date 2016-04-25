package com.weicong.gotravelling.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.activity.CommentRouteActivity;
import com.weicong.gotravelling.activity.FollowActivity;
import com.weicong.gotravelling.activity.MyRouteFinishedActivity;
import com.weicong.gotravelling.activity.MySignedSightActivity;
import com.weicong.gotravelling.activity.RouteActivity;
import com.weicong.gotravelling.activity.RouteSignActivity;
import com.weicong.gotravelling.event.AgeEvent;
import com.weicong.gotravelling.event.GenderEvent;
import com.weicong.gotravelling.event.HeadImageEvent;
import com.weicong.gotravelling.event.IntroEvent;
import com.weicong.gotravelling.manager.UserManager;

import de.greenrobot.event.EventBus;

public class UserHomeFragment extends Fragment {

    private UserManager mUserManager;

    private ImageView mHeadImageView;

    private TextView mAgeText;
    private ImageView mGenderImage;
    private TextView mIntroText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserManager = UserManager.getInstance(getActivity());
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        //头像
        mHeadImageView = (ImageView) view.findViewById(R.id.iv_head_image);
        mUserManager.setHeadImage(mHeadImageView);

        //年龄
        mAgeText = (TextView) view.findViewById(R.id.tv_age);
        mAgeText.setText(mUserManager.getAge());

        //性别
        mGenderImage = (ImageView) view.findViewById(R.id.iv_gender);
        if (mUserManager.getGender()) {
            mGenderImage.setImageResource(R.drawable.ic_gender_f_g);
        } else {
            mGenderImage.setImageResource(R.drawable.ic_gender_m_g);
        }

        //一句话介绍
        mIntroText = (TextView) view.findViewById(R.id.tv_description);
        mIntroText.setText(mUserManager.getIntro());

        //我的路线
        TextView routeNumText = (TextView) view.findViewById(R.id.tv_route_num);
        routeNumText.setText(mUserManager.getRouteNum());

        //我关注的人
        TextView followNumText = (TextView) view.findViewById(R.id.tv_follow_num);
        followNumText.setText(mUserManager.getFollowNum());

        //评论过的路线
        TextView commentRouteNum = (TextView) view.findViewById(R.id.tv_comment_route_num);
        commentRouteNum.setText(mUserManager.getCommentRouteNum());

        //评论过的景点
        TextView commentSightNum = (TextView) view.findViewById(R.id.tv_finished_route_num);
        commentSightNum.setText(mUserManager.getFinishedRouteNum());

        //签到的景点
        TextView signSightNumText = (TextView) view.findViewById(R.id.tv_sign_sight_num);
        signSightNumText.setText(mUserManager.getSignSightNum());

        View myRoute = view.findViewById(R.id.ll_my_route);
        myRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RouteActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        View followMe = view.findViewById(R.id.ll_follow_me);
        followMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        View travellingRoute = view.findViewById(R.id.rl_travelling);
        travellingRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RouteSignActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        View finishedRoute = view.findViewById(R.id.rl_finished);
        finishedRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyRouteFinishedActivity.class);
                startActivity(intent);
            }
        });

        View commentRoute = view.findViewById(R.id.rl_comment);
        commentRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CommentRouteActivity.class);
                startActivity(intent);
            }
        });

        View signSight = view.findViewById(R.id.rl_sign);
        signSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MySignedSightActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void onEventMainThread(HeadImageEvent event) {
        mUserManager.setHeadImage(mHeadImageView);
    }

    public void onEventMainThread(AgeEvent event) {
        mAgeText.setText(event.getAge());
    }

    public void onEventMainThread(GenderEvent event) {
        if (mUserManager.getGender()) {
            mGenderImage.setImageResource(R.drawable.ic_gender_f_g);
        } else {
            mGenderImage.setImageResource(R.drawable.ic_gender_m_g);
        }
    }

    public void onEventMainThread(IntroEvent event) {
        mIntroText.setText(mUserManager.getIntro());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
