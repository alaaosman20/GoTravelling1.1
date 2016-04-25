package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.glomadrian.codeinputlib.CodeInput;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.event.AgeEvent;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.util.DialogUtil;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class SelectBirthYearActivity extends SwipeBackBaseActivity {

    private MenuItem mCompletedItem;

    private CodeInput mCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_birth_year);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.modify_birth_year);
        setSupportActionBar(toolbar);

        mCodeInput = (CodeInput) findViewById(R.id.code_input);
        mCodeInput.setCompletedListener(new CodeInput.onCompletedListener() {
            @Override
            public void onCompleted() {
                mCompletedItem.setEnabled(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completed, menu);
        mCompletedItem = menu.findItem(R.id.action_completed);
        mCompletedItem.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_completed) {
            final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
            progressDialog.show();
            Character[] characters = mCodeInput.getCode();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < characters.length; i++) {
                sb.append(characters[i]);
            }
            int birthYear = Integer.valueOf(sb.toString());
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int age = currentYear - birthYear;
            final String s = age + "岁";

            UserManager.getInstance(this).setAge(age, new UserManager.OnUpdateListener() {
                @Override
                public void onSuccess() {
                    EventBus.getDefault().post(new AgeEvent(s));
                    progressDialog.dismiss();
                    finish();
                    overridePendingTransition(0, R.anim.out_from_right);
                }

                @Override
                public void onError() {
                    progressDialog.dismiss();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
