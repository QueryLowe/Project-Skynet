package skynet_app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.collection_app.R;

public class UserInfoActivity extends Activity {
    String[] items;
    public UserInfoActivity() {
        items = new String[]{"male", "female"};
        Spinner dropdown = findViewById(R.id.user_gender);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }
}
