package com.lk.lankabell.fault.adapter;

import android.app.Activity;
import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AlertDialog {
    Context context;

    public AlertDialog(Context context) {
        this.context=context;
    }

    public void showErrorAlert(String eMessage){
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE).setTitleText("Error!").setContentText(eMessage).show();
    }

    public void showBasicAlert(String eMessage){
        new SweetAlertDialog(context).setTitleText(eMessage).show();
    }

    public void showWarningAlert(String eTitle){
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(eTitle)
                .show();
    }


    public void isHaveReturnDialog(){
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Emergency SOS")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.dismissWithAnimation();
//                        Intent callIntent = new Intent(Intent.ACTION_CALL);
//                        callIntent.setData(Uri.parse("tel:"+number));
//                        if (ActivityCompat.checkSelfPermission(context,
//                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            return;
//                        }
//                        context.startActivity(callIntent);
                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public boolean isShow(){
        return new SweetAlertDialog(context).isShowContentText();
    }

    public void successAlert(final String sResult){
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        dialog.setTitleText(sResult);
        dialog.setConfirmText("OK");
        dialog.setCanceledOnTouchOutside(false);

        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Activity activity = (Activity) context;
                        activity.finish();
                    }
                }).show();
    }


    public void CommonSuccessAlert(final String sResult){
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        dialog.setTitleText(sResult);
        dialog.setConfirmText("OK");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        }).show();

    }

    public void notifiiedSuccess() {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                //.setTitleText("Completed")
                .setContentText("Successfully Notified !!! ")
                .setConfirmText("Okay")
                .show();
    }


//    public void errorQRScanAlert(final String sResult){
//        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
//                .setTitleText(sResult)
//                .setConfirmText("OK")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        return;
//                    }
//                }).show();
//    }

////    Bitmap signatureBitmap;
//    public void showDigialSignature(Bitmap bitmap){
//        final Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.layout_for_feedback);
//        //dialog.setTitle("Customer Signature");
//        ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);
//        imageView.setImageBitmap(bitmap);
//        dialog.show();
//
//    }
}
