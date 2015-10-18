package com.codepath.apps.tumblrsnap.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.codepath.apps.tumblrsnap.ImageFilterProcessor;
import com.codepath.apps.tumblrsnap.R;
import com.codepath.apps.tumblrsnap.TumblrClient;
import com.codepath.apps.tumblrsnap.models.User;
import com.codepath.libraries.androidviewhelpers.SimpleProgressDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class PreviewPhotoActivity extends FragmentActivity {
    private Bitmap photoBitmap;
    private Bitmap photoBitmapPreview;
    private Bitmap processedBitmap;
    private SimpleProgressDialog dialog;
    private ImageView ivPreview;
    private ImageFilterProcessor filterProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        photoBitmap = getIntent().getParcelableExtra("photo_bitmap");
        photoBitmapPreview = Bitmap.createScaledBitmap(photoBitmap, 80, 80, true);

        filterProcessor = new ImageFilterProcessor(photoBitmap);
        redisplayPreview(ImageFilterProcessor.NONE);

        ImageView ivOriginal = (ImageView) findViewById(R.id.ivOriginal);
        ivOriginal.setImageBitmap(redisplayPreviewFilter(photoBitmapPreview, ImageFilterProcessor.NONE));

        ImageView ivBlur = (ImageView) findViewById(R.id.ivBlur);
        ivBlur.setImageBitmap(redisplayPreviewFilter(photoBitmapPreview, ImageFilterProcessor.BLUR ));

        ImageView ivGrayscale = (ImageView) findViewById(R.id.ivGrayscale);
        ivGrayscale.setImageBitmap(redisplayPreviewFilter(photoBitmapPreview, ImageFilterProcessor.GRAYSCALE ));

        ImageView ivCrystallize = (ImageView) findViewById(R.id.ivCrystallize);
        ivCrystallize.setImageBitmap(redisplayPreviewFilter(photoBitmapPreview, ImageFilterProcessor.CRYSTALLIZE ));

        ImageView ivSolarize = (ImageView) findViewById(R.id.ivSolarize);
        ivSolarize.setImageBitmap(redisplayPreviewFilter(photoBitmapPreview, ImageFilterProcessor.SOLARIZE ));

        ImageView ivGlow = (ImageView) findViewById(R.id.ivGlow);
        ivGlow.setImageBitmap(redisplayPreviewFilter(photoBitmapPreview, ImageFilterProcessor.GLOW ));

    }

    private void redisplayPreview(int effectId) {
        processedBitmap = filterProcessor.applyFilter(effectId);
        ivPreview.setImageBitmap(processedBitmap);
    }

    private Bitmap redisplayPreviewFilter(Bitmap image, int effectId) {
        image = filterProcessor.applyFilter(effectId);
        return image;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.preview_photo, menu);
        return true;
    }

    public void filter(View v){

        int effectId = 0;

        switch (Integer.parseInt(v.getTag().toString())) {
            case 0:
                effectId = ImageFilterProcessor.NONE;
                break;
            case 1:
                effectId = ImageFilterProcessor.BLUR;
                break;
            case 2:
                effectId = ImageFilterProcessor.GRAYSCALE;
                break;
            case 3:
                effectId = ImageFilterProcessor.CRYSTALLIZE;
                break;
            case 4:
                effectId = ImageFilterProcessor.SOLARIZE;
                break;
            case 5:
                effectId = ImageFilterProcessor.GLOW;
                break;
            default:
                effectId = ImageFilterProcessor.NONE;
                break;
        }
        redisplayPreview(effectId);
    }

    public void onSaveButton(MenuItem menuItem) {
        dialog = SimpleProgressDialog.build(this);
        dialog.show();

        TumblrClient client = ((TumblrClient) TumblrClient.getInstance(TumblrClient.class, this));
        client.createPhotoPost(User.currentUser().getBlogHostname(), processedBitmap, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, String arg1) {
                dialog.dismiss();
                PreviewPhotoActivity.this.finish();
            }

            @Override
            public void onFailure(Throwable arg0, String arg1) {
                dialog.dismiss();
            }
        });
    }
}
