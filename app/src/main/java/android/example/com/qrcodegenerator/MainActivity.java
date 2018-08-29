package android.example.com.qrcodegenerator;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    EditText editTextName, editTextAge, editTextPhone;
    Button generateButton;
    ImageView qrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.edit_text_name);
        editTextAge = findViewById(R.id.edit_text_age);
        editTextPhone = findViewById(R.id.edit_text_phone);
        generateButton = findViewById(R.id.qrGeneratorBtn);
        qrCode = findViewById(R.id.qrImg);

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
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    qrCode.setImageBitmap(bitmap);
                    qrCode.setVisibility(View.VISIBLE);
                    findViewById(R.id.information).setVisibility(View.GONE);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
