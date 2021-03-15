package com.android.photostudio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity
{
    private ImageView userImage;
    private Button btnImageCapture;
    private static final int CAPTURE_IMAGE_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userImage = (ImageView) findViewById(R.id.user_image_display);
        btnImageCapture = (Button)findViewById(R.id.btn_usercapture);

        //Requesting Permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        {
            /* If the permission is already Granted allow the user to take a photo*/
            capturePhoto();

        }
        else
            {
                //Proceed to request runtime permission using Dexter
                btnImageCapture.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //Requesting Permission
                        Dexter.withActivity(MainActivity.this)
                                .withPermission(Manifest.permission.CAMERA)
                                .withListener(new PermissionListener()
                                {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse)
                                    {
                                        /*If permission is Granted
                                        /allow user to take photo
                                        /call method */

                                                capturePhoto();
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse pdresponse)
                                    {
                                        /*If permission is Denied
                                        /Check if its permanently denied
                                         */
                                        if (pdresponse.isPermanentlyDenied())
                                        {
                                            /*Displaying Alert Dialog */
                                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                            dialogBuilder.setTitle("CAMERA PERMISSION");
                                            dialogBuilder.setMessage("Permission to access camera is Denied. To continue you must go to settings and allow it");
                                            dialogBuilder.setNegativeButton("Cancel", null);
                                            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    /*Sending the user to phone's settings*/
                                                    Intent settingsIntent = new Intent();
                                                    settingsIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    settingsIntent.setData(Uri.fromParts("package",getPackageName(), null));
                                                    startActivity(settingsIntent);

                                                }
                                            }).show();

                                        } /*If permission is NOT permanently denied*/
                                        else if (!pdresponse.isPermanentlyDenied())
                                        {
                                            //Display Toast
                                            Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken)
                                    {
                                        permissionToken.continuePermissionRequest();

                                    }
                                }).check();

                    }
                });
            }

    }



    //Method for capturing the image
    private void capturePhoto()
    {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(captureIntent, CAPTURE_IMAGE_REQUEST);
    }

    //Loading the Image taken by the camera onto the image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //Setting Validations to ensure that the user has captured the Image
        if (requestCode==CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK)
        {
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            userImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            userImage.setImageBitmap(bitmap);
        }
        else
            {

                /*if the user didn't click the button to capture image
                /Display a toast */

                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
    }
}