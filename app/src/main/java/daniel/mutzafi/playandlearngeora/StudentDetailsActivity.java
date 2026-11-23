package daniel.mutzafi.playandlearngeora;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentDetailsActivity extends BaseActivity {

    private EditText etFirstName, etLastName, etClass;
    private HelperDB helperDB;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        setupMenu(); // Inherited from BaseActivity

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etClass = findViewById(R.id.etClass);
        Button bSubmit = findViewById(R.id.bSubmitStudentDetails);
        helperDB = new HelperDB(this);

        bSubmit.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String className = etClass.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || className.isEmpty()) {
                Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> saveStudentData(firstName, lastName, className));

            Intent intent = new Intent(StudentDetailsActivity.this, ModulesChoiceActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void saveStudentData(String firstName, String lastName, String className) {
        SQLiteDatabase db = helperDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HelperDB.COLUMN_FIRST_NAME, firstName);
        values.put(HelperDB.COLUMN_LAST_NAME, lastName);
        values.put(HelperDB.COLUMN_CLASS, className);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        values.put(HelperDB.COLUMN_LOGIN_TIMESTAMP, currentDate);

        long schoolWorkId = db.insert(HelperDB.TABLE_SCHOOL_WORKS, null, values);

        // Save the current session ID for statistics
        if (schoolWorkId != -1) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("current_school_work_id", schoolWorkId);
            editor.apply();
        } else {
            // If saving fails, clear any previous ID
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("current_school_work_id");
            editor.apply();
        }

        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), "Student data saved successfully", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
