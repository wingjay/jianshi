package com.wingjay.jianshi.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Toast;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.ui.base.BaseActivity;
import com.wingjay.jianshi.ui.widget.BgColorPickDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.vertical_write) SwitchCompat verticalWrite;
    @BindView(R.id.send_feedback) View sendFeedBack;
    @BindView(R.id.customize_bg_color) View customizeBgColor;

    UserPrefs userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        userPrefs = new UserPrefs(SettingActivity.this);

        verticalWrite.setChecked(userPrefs.getVerticalWrite());

    }

    @OnCheckedChanged(R.id.vertical_write)
    void chooseVerticalWrite() {
        userPrefs.setVerticalWrite(verticalWrite.isChecked());
    }

    @OnClick(R.id.send_feedback)
    void sendFeedback() {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("*/*");
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"yinjiesh@126.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_email_subject));
        i.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.feedback_email_prefix));
        try {
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(i, getResources().getString(R.string.send_email)));
            }
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getResources().getString(R.string.email_client_no_found),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.customize_bg_color)
    void chooseBgColor() {
        BgColorPickDialogFragment bgColorPickDialogFragment = new BgColorPickDialogFragment();
        bgColorPickDialogFragment.setOnBackgroundColorChangedListener(
                new BgColorPickDialogFragment.OnBackgroundColorChangedListener() {
            @Override
            public void onBackgroundColorChanged(int newColorRes) {
                SettingActivity.this.setContainerBgColor(newColorRes);
                userPrefs.setBackgroundColor(newColorRes);
                setResult(RESULT_OK);
            }
        });
        bgColorPickDialogFragment.show(getSupportFragmentManager(), null);
    }


}
