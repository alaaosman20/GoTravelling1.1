package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.kale.activityoptions.transition.TransitionCompat;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.util.ImageLoaderUtil;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowPhotoActivity extends BaseActivity {

	public static final String PATH = "path";
	public static final String NATIVE = "native";
	
	private PhotoViewAttacher mAttacher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_photo);

		TransitionCompat.startTransition(this, R.layout.activity_show_photo);
		String path = getIntent().getStringExtra(PATH);
		if (getIntent().getBooleanExtra(NATIVE, true)) {
			path = ImageDownloader.Scheme.FILE.wrap(path);
		}
		ImageView imageView = (ImageView) findViewById(R.id.iv_photo);

		ImageLoaderUtil.displayImage(path, imageView, null);
		
		mAttacher = new PhotoViewAttacher(imageView);
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();

        // Need to call clean-up
        mAttacher.cleanup();
    }

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		TransitionCompat.finishAfterTransition(this);
	}

}
