package android.example.com.qrcodegenerator;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CodeGeneratorActivity extends AppCompatActivity {

    private static final String TAG = CodeGeneratorActivity.class.getSimpleName();

    EditText editTextName, editTextAge, editTextPhone;
    Button generateButton, saveButton, sendButton;
    ImageView qrCode;
    LinearLayout qrInfoLayout, qrResultLayout;
    Bitmap mQRCodeBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        editTextName = findViewById(R.id.edit_text_name);
        editTextAge = findViewById(R.id.edit_text_age);
        editTextPhone = findViewById(R.id.edit_text_phone);
        generateButton = findViewById(R.id.qrGeneratorBtn);
        qrCode = findViewById(R.id.qrImg);

        saveButton = findViewById(R.id.save_qr_code);
        sendButton = findViewById(R.id.send_qr_code);

        qrInfoLayout = findViewById(R.id.qrCode_info_layout);
        qrResultLayout = findViewById(R.id.qrCode_result_layout);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textName = editTextName.getText().toString().trim();
                String textAge = editTextAge.getText().toString().trim();
                String textPhone = editTextPhone.getText().toString().trim();

                if(TextUtils.isEmpty(textName) || TextUtils.isEmpty(textAge) || TextUtils.isEmpty(textPhone)){
                    Toast.makeText(getBaseContext(), "Enter all the details", Toast.LENGTH_SHORT).show();
                }

                Customer customer = new Customer();
                customer.setName(textName);
                customer.setAge(Integer.parseInt(textAge));
                customer.setPhone(textPhone);

                Gson gson = new Gson();
                String jsonString = gson.toJson(customer);

                Log.d(TAG, "onClick: jsonString :: "+jsonString);

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(jsonString, BarcodeFormat.QR_CODE, 250, 250);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    mQRCodeBitmap = barcodeEncoder.createBitmap(bitMatrix);
                    qrCode.setImageBitmap(mQRCodeBitmap);
                    qrInfoLayout.setVisibility(View.GONE);
                    qrResultLayout.setVisibility(View.VISIBLE);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "saveButton.setOnClickListener : started");
                Bitmap qrCode = mQRCodeBitmap;
                FileOutputStream fileOutputStream = null;
                String extDirPath = Environment.getExternalStorageDirectory().toString();
                Log.d(TAG, "onClick: "+extDirPath);
                try {
                    //fileOutputStream = new FileOutputStream("qrcode");
                    File file = new File(extDirPath, "qrcode"+editTextPhone.getText().toString()+".jpeg");
                    fileOutputStream = new FileOutputStream(file);
                    qrCode.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream); 
                    // PNG is a lossless format, the compression factor (100) is ignored
                    Log.d(TAG, "onClick: file saved");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "sendButton.setOnClickListener : started");
                Bitmap qrCode = mQRCodeBitmap;
                Intent shareQRCode = new Intent(Intent.ACTION_SEND);
                shareQRCode.setType("image/jpeg");
                /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                qrCode.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "QR_Code");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);


                OutputStream outstream;
                try {
                    outstream = getContentResolver().openOutputStream(uri);
                    qrCode.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                    outstream.close();
                } catch (Exception e) {
                    System.err.println(e.toString());
                }

                shareQRCode.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareQRCode, "Share Image"));
            }
        });

    }
}
