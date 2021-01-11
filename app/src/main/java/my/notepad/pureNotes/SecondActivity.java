package my.notepad.pureNotes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.notepad.note20.R;

import java.io.FileOutputStream;
import java.util.Objects;

public class SecondActivity extends MainActivity {
    String filename;
    boolean markedForDeletion = false;
    int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        SharedPreferences sharedPreferences = getSharedPreferences("my.notepad.note20", Context.MODE_PRIVATE);
        filename = intent.getStringExtra("filename");

        Toolbar toolbar = findViewById(R.id.secondary_toolbar);
        toolbar.setTitle(filename);
        defaultColor = ContextCompat.getColor(SecondActivity.this, R.color.color_second_activity_toolbar);
        int secondaryToolbarColor = sharedPreferences.getInt("secondaryToolbarColor", defaultColor);
        toolbar.setBackgroundColor(secondaryToolbarColor);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        EditText editText = findViewById(R.id.editText);
        editText.setText(getStringFromFile(filename));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    public void openColorPicker(){
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose secondary color theme")
                .initialColor(defaultColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("  OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        Toolbar toolbar = findViewById(R.id.secondary_toolbar);
                        toolbar.setBackgroundColor(selectedColor);
                        defaultColor = selectedColor;
                        SharedPreferences sharedPreferences = getSharedPreferences("my.notepad.note20", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putInt("secondaryToolbarColor", selectedColor).apply();
                    }
                })
                .setNegativeButton("Cancel  ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }


    @Override
    protected void onPause(){
        super.onPause();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.editText).getWindowToken(), 0);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (!markedForDeletion) {
            EditText editText = findViewById(R.id.editText);
            String noteContents = editText.getText().toString();
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                fileOutputStream.write(noteContents.getBytes());
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finish(){
        if(!markedForDeletion) {
            EditText editText = findViewById(R.id.editText);
            String noteContents = editText.getText().toString();
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                fileOutputStream.write(noteContents.getBytes());
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent returnIntent = new Intent();
            returnIntent.putExtra("filename", filename);
            setResult(RESULT_OK, returnIntent);
        }
        else{
            Intent returnIntent = new Intent();
            returnIntent.putExtra("filename", filename);
            setResult(RESULT_CANCELED, returnIntent);
        }
        super.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.second_activity_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return true;
    }

    private void shareNote(){
        EditText editText = findViewById(R.id.editText);
        String message = filename + "\n\n" + editText.getText().toString();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share via"));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_delete_note_button:
            case R.id.action_delete_note:
                presentDeletionDialog();
                return true;

            case R.id.action_share_note_button:
            case R.id.action_share_note:
                shareNote();
                return true;

            case R.id.action_change_secondary_theme:
                openColorPicker();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void presentDeletionDialog(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_note_alert, null);
        Button deleteNoteDialogButton = dialogView.findViewById(R.id.button_delete_note);
        Button cancelDialogButton = dialogView.findViewById(R.id.button_cancel_delete_note);


        deleteNoteDialogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                markedForDeletion = true;
                dialogBuilder.dismiss();
                finish();
            }
        });

        cancelDialogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}
