package android.example.com.qrcodegenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    Button codeGenerator, codeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: started");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codeGenerator = findViewById(R.id.generate_code);
        codeScanner = findViewById(R.id.scan_code);

        codeGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick of codeGenerator");
                Intent codeGeneratorIntent = new Intent(getApplicationContext(), CodeGeneratorActivity.class);
                startActivity(codeGeneratorIntent);
            }
        });

        codeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick of codeScanner");
                Intent codeScannerIntent = new Intent(getApplicationContext(), CodeScannerActivity.class);
                startActivity(codeScannerIntent);
            }
        });

        Log.d(TAG, "onCreate: ended");

    }
}
