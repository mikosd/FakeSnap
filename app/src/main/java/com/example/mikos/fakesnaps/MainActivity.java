package com.example.mikos.fakesnaps;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.github.clans.fab.FloatingActionButton;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.clans.fab.FloatingActionMenu;


public class MainActivity extends Activity {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private DrawingView imgView;
    private boolean isRectangle = true;
    private boolean isStroke = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (DrawingView) findViewById(R.id.imgView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        imgView.init(metrics);


        FloatingActionMenu menu = (FloatingActionMenu)findViewById(R.id.fam);
        FloatingActionButton colorpickerFAB = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        colorpickerFAB.setOnClickListener(new ColorListener());
        FloatingActionButton lineWidthPicker = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);
        lineWidthPicker.setOnClickListener(new WidthListener());
        FloatingActionButton styleSwitcher = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item4);
        styleSwitcher.setOnClickListener(new StyleListener());

    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG
                    && data != null) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                imgView.clear();
                imgView.setmLoadBit(Bitmap.createScaledBitmap(bitmap,metrics.widthPixels,metrics.heightPixels,false));
                imgView.invalidate();
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private class ColorListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            ColorPickerDialogBuilder
                .with(MainActivity.this )
                .setTitle("Choose color")
                .initialColor(Color.WHITE)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Toast.makeText(getApplicationContext(),"Color Selected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        Toast.makeText(getApplicationContext(),"Color Selected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                        imgView.setCurrentColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .build().show();
        }
    }

    private class WidthListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder widthDialog = new AlertDialog.Builder(MainActivity.this);
            View dialongView = getLayoutInflater().inflate(R.layout.seekbar_dialog, null);
            //widthDialog.setContentView(R.layout.seekbar_dialog);
            widthDialog.setView(dialongView);
            widthDialog.setTitle("Select Line Width");
            widthDialog.setCancelable(true);

            final TextView textView = dialongView.findViewById(R.id.width_size);
            final SeekBar seekBar =(SeekBar)dialongView.findViewById(R.id.width_seekBar);


            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText("Line Width: " + i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            widthDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                imgView.setStrokeWidth(seekBar.getProgress());
                }
            });

            widthDialog.create();
            widthDialog.show();

            }
        }

    private class StyleListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(isRectangle){
                isRectangle = false;
                isStroke = true;
                imgView.setIsStroke(true);
                imgView.setIsRect(false);
            }else if(isStroke){
                isRectangle = true;
                isStroke = false;
                imgView.setIsStroke(false);
                imgView.setIsRect(true);
            }
        }
    }
}

