package moe.shizuku.phonesms.entity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import moe.shizuku.phonesms.R;


public class UpdateDialog {
    private final Activity mActivity;
    private final UpdateEntity mEntity;

    public UpdateDialog(Activity activity, UpdateEntity entity) {
        this.mActivity = activity;
        this.mEntity = entity;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(String.format("%s:%s", activity.getString(R.string.Version_update), entity.getVersionName()))
                .setMessage(entity.getIntroduction())
                .setPositiveButton(activity.getString(R.string.web_update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mEntity.getDownload()));
                        mActivity.startActivity(intent);
                    }
                }).setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }
}
