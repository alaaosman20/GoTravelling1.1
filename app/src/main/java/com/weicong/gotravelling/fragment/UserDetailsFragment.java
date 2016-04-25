package com.weicong.gotravelling.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.event.AgeEvent;
import com.weicong.gotravelling.event.CityEvent;
import com.weicong.gotravelling.event.DescriptionEvent;
import com.weicong.gotravelling.event.EmailEvent;
import com.weicong.gotravelling.event.GenderEvent;
import com.weicong.gotravelling.event.HeadImageEvent;
import com.weicong.gotravelling.event.PhoneEvent;
import com.weicong.gotravelling.manager.UserManager;

import de.greenrobot.event.EventBus;

public class UserDetailsFragment extends Fragment {

    private UserManager mUserManager;

    private ImageView mHeadImageView;
    private TextView mAgeText;
    private ImageView mGenderImage;
    private TextView mDescriptionText;
    private TextView mPhoneText;
    private TextView mEmailText;
    private TextView mCityText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserManager = UserManager.getInstance(getActivity());
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        ImageView phoneImage = (ImageView) view.findViewById(R.id.iv_phone);
        phoneImage.setImageDrawable(new IconicsDrawable(getActivity(),
                GoogleMaterial.Icon.gmd_phone_android).colorRes(R.color.icon_blue));

        ImageView emailImage = (ImageView) view.findViewById(R.id.iv_email);
        emailImage.setImageDrawable(new IconicsDrawable(getActivity(),
                GoogleMaterial.Icon.gmd_email).paddingDp(1).colorRes(R.color.icon_blue));

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

        //城市
        mCityText = (TextView) view.findViewById(R.id.tv_city);
        mCityText.setText(mUserManager.getCity());

        //电话
        mPhoneText = (TextView) view.findViewById(R.id.tv_phone);
        mPhoneText.setText(mUserManager.getPhone());

        //邮箱
        mEmailText = (TextView) view.findViewById(R.id.tv_email);
        mEmailText.setText(mUserManager.getEmail());

        //个人介绍
        mDescriptionText = (TextView) view.findViewById(R.id.tv_description);
        mDescriptionText.setText(mUserManager.getDescription());

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

    public void onEventMainThread(DescriptionEvent event) {
        mDescriptionText.setText(mUserManager.getDescription());
    }

    public void onEventMainThread(PhoneEvent event) {
        mPhoneText.setText(mUserManager.getPhone());
    }

    public void onEventMainThread(EmailEvent event) {
        mEmailText.setText(mUserManager.getEmail());
    }

    public void onEventMainThread(CityEvent event) {
        mCityText.setText(mUserManager.getCity());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
