package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.GetAccessUrlListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.util.ImageLoaderUtil;
import com.weicong.gotravelling.util.TextUtil;

import cn.bmob.v3.datatype.BmobFile;

public class CreatorActivity extends SwipeBackBaseActivity {

    private User mUser;

    private ImageView mHeadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator);

        mUser = MyApplication.getInstance().getUser();

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageView phoneImage = (ImageView) findViewById(R.id.iv_phone);
        phoneImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_phone_android).colorRes(R.color.icon_blue));

        ImageView emailImage = (ImageView) findViewById(R.id.iv_email);
        emailImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_email).paddingDp(1).colorRes(R.color.icon_blue));

        TextView usernameText = (TextView) findViewById(R.id.tv_username);
        usernameText.setText(mUser.getUsername());

        String intro = "(添加一句话介绍自己)";
        if (!TextUtil.isEmpty(mUser.getIntro())) {
            intro = mUser.getIntro();
        }
        TextView introText = (TextView) findViewById(R.id.tv_intro);
        introText.setText(intro);

        int age = 0;
        if (mUser.getAge() != null) {
            age = mUser.getAge();
        }
        TextView ageText = (TextView) findViewById(R.id.tv_age);
        ageText.setText(age + "岁");

        boolean gender = false;
        if (mUser.getGender() != null) {
            gender = mUser.getGender();
        }
        ImageView genderImage = (ImageView) findViewById(R.id.iv_gender);
        if (gender) {
            genderImage.setImageResource(R.drawable.ic_gender_f_g);
        } else {
            genderImage.setImageResource(R.drawable.ic_gender_m_g);
        }

        String city = "广州市";
        if (mUser.getCity() != null) {
            city = mUser.getCity();
        }
        TextView cityText = (TextView) findViewById(R.id.tv_city);
        cityText.setText(city);

        TextView phoneText = (TextView) findViewById(R.id.tv_phone);
        phoneText.setText(mUser.getMobilePhoneNumber());

        TextView emailText = (TextView) findViewById(R.id.tv_email);
        emailText.setText(mUser.getEmail());

        TextView descriptionText = (TextView) findViewById(R.id.tv_description);
        descriptionText.setText(mUser.getDescription());

        FloatingActionButton followButton = (FloatingActionButton) findViewById(R.id.action_follow);
        followButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_visibility).colorRes(R.color.white));
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser();
            }
        });
        User myUser = UserManager.getInstance(this).getUser();
        if (!UserManager.LOGIN || mUser.getObjectId().equals(myUser.getObjectId())) {
            followButton.setVisibility(View.GONE);
        }

        mHeadImage = (ImageView) findViewById(R.id.header_image);

        getImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem homeItem = menu.add(0, 1, 0, R.string.action_home);
        homeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.still, R.anim.out_from_right);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.still, R.anim.out_from_right);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 关注用户
     */
    private void followUser() {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
        progressDialog.show();
        UserManager.getInstance(CreatorActivity.this).followUser(mUser,
                new UserManager.OnUpdateListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        Toast.makeText(CreatorActivity.this, "关注成功",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        progressDialog.dismiss();
                        Toast.makeText(CreatorActivity.this, "您已经关注了该用户",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获取头像
     */
    private void getImage() {
        if (mUser.getImage() != null) {
            BmobProFile.getInstance(this).getAccessURL(mUser.getImage(), new GetAccessUrlListener() {
                @Override
                public void onSuccess(BmobFile bmobFile) {
                    ImageLoaderUtil.displayHeadImage(bmobFile.getUrl(), mHeadImage);
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }
}
