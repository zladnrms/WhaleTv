package zladnrms.defytech.kim.BroadcastTv;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by kim on 2017-03-23.
 */

public class CustomDialog {

    Context context;
    boolean flash = false;
    boolean response = false;

    public CustomDialog(Context context){
        this.context = context;
    }

    public boolean showFlashDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("카메라 설정");
        builder.setMessage("CameraCMAMEar");

        String positiveText = "ON";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flash = true;
                    }
                });

        String negativeText = "OFF";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flash = false;
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

        return flash;
    }

    public boolean showRecordOffDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("방송하기");
        builder.setMessage("방송을 종료하시겠습니까?");

        String positiveText = "확인";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        response = true;
                    }
                });

        String negativeText = "취소";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        response = false;
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

        return response;
    }

    public boolean showRecordOnDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("방송하기");
        builder.setMessage("방송을 시작하시겠습니까?");

        String positiveText = "확인";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        response = true;
                    }
                });

        String negativeText = "취소";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        response = false;
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

        return response;
    }

}
