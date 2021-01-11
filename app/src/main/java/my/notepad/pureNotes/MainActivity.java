package my.notepad.pureNotes;

//TODO
// " Main app icon made by Freepik from www.flaticon.com. See Freepik's work at https://www.freepik.com/home "
// " New note icon made by Picol from www.flaticon.com. See Picol's work at https://www.flaticon.com/authors/picol "
// ***
// - To-do list conversion and vice versa
// - Set up settings menu: theme, toolbar color, about
// - Make it so that notes can get their date modified by themselves (THERE MUST BE A WAY!!!!!)
// SEARCH DIES WHEN LEFT ARROW PRESSED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//in oncreate, set color of main and secondary toolbars based on config file


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.notepad.note20.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    int defaultColor;

    public void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void makeNewNote(final View v){

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        dialogBuilder.setCanceledOnTouchOutside(false);
        // todo : determine if icon in new note dialog is a good idea or not
        dialogBuilder.setIcon(R.drawable.ic_baseline_note_add_24px);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.new_note_alert, null);
        final EditText editText = dialogView.findViewById(R.id.create_note_edit_text);
        Button createNoteDialogButton = dialogView.findViewById(R.id.button_create_note);
        final Button cancelDialogButton = dialogView.findViewById(R.id.button_cancel_create_note);

        createNoteDialogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                FileOutputStream outputStream;
                String enteredTitle = editText.getText().toString().trim();
                if (Arrays.asList(fileList()).contains(enteredTitle)){
                    Toast.makeText(MainActivity.this, " Notes must have different titles\n  No note created", Toast.LENGTH_LONG).show();
                }
                else if (enteredTitle.equals("")){
                    Toast.makeText(MainActivity.this, " You must give your note a title\n  No note created", Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        outputStream = openFileOutput(enteredTitle, Context.MODE_PRIVATE);
                        outputStream.close();
                        Toast.makeText(MainActivity.this, "A new note has been created", Toast.LENGTH_LONG).show();
                        int index = 0;
                        Date date = new Date();
                        long lastMod = date.getTime();
                        NoteViewItem noteToAdd = new NoteViewItem(MainActivity.this, enteredTitle, lastMod);
                        myDataSet.add(noteToAdd);
                        mAdapter.add(noteToAdd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                hideSoftKeyboard(view);
                dialogBuilder.dismiss();

            }
        });

        cancelDialogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                hideSoftKeyboard(view);
                dialogBuilder.dismiss();
            }
        });

        editText.requestFocus();
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    public void createNoteSortingDialog(){
        AlertDialog dialog;
        CharSequence[] values = {"Alphabetically", "By Date Modified"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setItems(values, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int item){
                SharedPreferences sharedPreferences = getSharedPreferences("my.notepad.note20", Context.MODE_PRIVATE);
                switch(item){
                    case 0:
                        //"Alphabetically"
                        sharedPreferences.edit().putString("sortingMethod", "ALPHABETICALLY").apply();
                        currentComparator = customComparatorsForNotes.ALPHABETICAL();
                        mAdapter.changeComparator(currentComparator);
                        mAdapter.replaceAll(myDataSet);
                        break;

                    case 1:
                        //"By Date Modified"
                        sharedPreferences.edit().putString("sortingMethod", "BY_DATE_MODIFIED").apply();
                        currentComparator = customComparatorsForNotes.DATE_MODIFIED();
                        mAdapter.changeComparator(currentComparator);
                        mAdapter.replaceAll(myDataSet);
                        break;
                }
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();

    }

    public void createAboutDialog(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.about_alert, null);
        final Button cancelDialogButton = dialogView.findViewById(R.id.button_close_about_dialog);

        cancelDialogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void openColorPicker(){
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose main color theme")
                .initialColor(defaultColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("  OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        Toolbar toolbar = findViewById(R.id.main_toolbar);
                        toolbar.setBackgroundColor(selectedColor);
                        defaultColor = selectedColor;
                        SharedPreferences sharedPreferences = getSharedPreferences("my.notepad.note20", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putInt("mainToolbarColor", selectedColor).apply();
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

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                //User chose "Settings" item -> show the app settings UI
//                Toast.makeText(getApplicationContext(), "Settings pressed", Toast.LENGTH_SHORT).show();
//                return true;

            case R.id.action_sort_notes:
                createNoteSortingDialog();
                return true;

            case R.id.action_change_theme:
                openColorPicker();
                return true;

            case R.id.action_about:
                createAboutDialog();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(searchViewMenuItem);
        ImageView imageView = searchView.findViewById(androidx.appcompat.R.id.search_button);
        imageView.setImageResource(R.drawable.ic_search_black_24px);

        if (searchView.getQuery().length() != 0){
            searchView.setIconified(false);
            searchView.setQuery(searchView.getQuery(), false);
        }

        searchView.setQueryHint(getResources().getString(R.string.edit_text_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideSoftKeyboard(findViewById(android.R.id.content));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchQuery = query;
                Log.i("INFO", "NEW QUERY");
                filteredList = filter(myDataSet, query);
                mAdapter.replaceAll(filteredList);
                mRecyclerView.scrollToPosition(0);
                Log.i("INFO", "END NEW QUERY");
                return true;
            }
        });
        return true;
    }

    private List<NoteViewItem> filter(List<NoteViewItem> notesViews, String query){
        String lowerCaseQuery = query.toLowerCase();
        List<NoteViewItem> filteredList = new ArrayList<>();
        for (NoteViewItem noteViewItem : notesViews){
            String title = noteViewItem.getName().toLowerCase();
            if (title.contains(lowerCaseQuery)){
                filteredList.add(noteViewItem);
            }
            else if (getStringFromFile(noteViewItem.getName()).contains(lowerCaseQuery)){
                filteredList.add(noteViewItem);
            }
        }
        return filteredList;
    }

    protected SpannableString buildString(final String name) {
        Log.i("INFO", "Actual: " + name);
        //set all chars black then set appropriate chars green
        SpannableString spannableString = new SpannableString(name);
        if (searchQuery.equals("")){
            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        else{
            for (NoteViewItem filteredItem : filteredList) {
                Log.i("INFO", "Examining: " + filteredItem.getName() );
                if (filteredItem.getName().toLowerCase().equals(name.toLowerCase())) {
                    Log.i("INFO", "Match found");
                    String currentFilenameToLower = filteredItem.getName().toLowerCase();
                    String queryToLower = searchQuery.toLowerCase();
                    int substringStartIndex = currentFilenameToLower.indexOf(queryToLower);
                    if (substringStartIndex >= 0) {
                        int substringEndIndex = substringStartIndex;
                        for (int j = 0; j < queryToLower.length(); j++) {
                            if (currentFilenameToLower.charAt(substringEndIndex) == queryToLower.charAt(j)) {
                                substringEndIndex++;
                            } else {
                                break;
                            }
                        }
                        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, substringStartIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new ForegroundColorSpan(Color.argb(255, 7, 156, 2)), substringStartIndex, substringEndIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), substringEndIndex, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                }
            }
        }
        return spannableString;
    }

    public void openSecondActivity(String filename){
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("filename", filename);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK) {
            String fName = (String) Objects.requireNonNull(data.getExtras()).get("filename");

            int index = 0;
            for (int i=0; i < myDataSet.size(); i++){
                assert fName != null;
                if (fName.equals(myDataSet.get(i).getName())){
                    index = i;
                    break;
                }
            }

            NoteViewItem noteToUpdate = myDataSet.get(index);
            mAdapter.remove(noteToUpdate);
            noteToUpdate.updateDescription(this);
            File file = new File(fName);
            long lastMod = file.lastModified();
            noteToUpdate.updateDateModified(new Date().getTime());
            mAdapter.add(noteToUpdate);
        }
        else{
            File file = new File((String) Objects.requireNonNull(data.getExtras()).get("filename"));
            int index = 0;
            for (int i=0; i < myDataSet.size(); i++){
                if (file.toString().equals(myDataSet.get(i).getName())){
                    index = i;
                    break;
                }
            }
            mAdapter.remove(myDataSet.get(index));
            myDataSet.remove(index);

            boolean deleted = file.delete();
            if(!deleted){
                getApplicationContext().deleteFile(file.getName());
            }
        }
    }

    protected String getStringFromFile(String name){
        StringBuffer stringBuffer = new StringBuffer();
        try{
            FileInputStream fileInputStream = getApplicationContext().openFileInput(name);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


            String currentLine = bufferedReader.readLine();
            while (currentLine != null){
                stringBuffer.append(currentLine);
                stringBuffer.append("\n");
                currentLine = bufferedReader.readLine();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    RecyclerView mRecyclerView;
    MyRecyclerViewAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<NoteViewItem> myDataSet;
    String noteSortingMethod;
    CustomComparatorsForNotes customComparatorsForNotes;
    Comparator<NoteViewItem> currentComparator;
    List<String> noteTitleHighlights;
    String searchQuery;
    List<NoteViewItem> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        SharedPreferences sharedPreferences = getSharedPreferences("my.notepad.note20", Context.MODE_PRIVATE);

        // Grab default toolbar color when app first opens
        defaultColor = ContextCompat.getColor(MainActivity.this, R.color.color_main_activity_toolbar);
        int mainToolbarColor = sharedPreferences.getInt("mainToolbarColor", defaultColor);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setBackgroundColor(mainToolbarColor);

        // Load in preferred sorting method from config file
        customComparatorsForNotes = new CustomComparatorsForNotes();
        String sortingMethod = sharedPreferences.getString("sortingMethod", "");
        if(sortingMethod.equalsIgnoreCase("ALPHABETICALLY")){
            currentComparator = customComparatorsForNotes.ALPHABETICAL();
        }
        else{
            currentComparator = customComparatorsForNotes.DATE_MODIFIED();
        }
        mAdapter = new MyRecyclerViewAdapter(MainActivity.this,
                new MyRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String item) {
                        openSecondActivity(item);
                    }
                },
                new MyRecyclerViewAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(String item) {
                        // TODO long click stuff
                        //Toast.makeText(getApplicationContext(), "Long press on " + item, Toast.LENGTH_SHORT).show();
                    }
                },
                currentComparator);

        mRecyclerView = findViewById((R.id.noteList));
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        File[] files = getFilesDir().listFiles();
        myDataSet = new ArrayList<>();
        for (File f : files){
            String fName = f.getName();
            if (!fName.equals("instant-run")) {
                long lastMod = f.lastModified();
                myDataSet.add(new NoteViewItem(this, fName, lastMod));
            }
        }
        mAdapter.addAll(myDataSet);

        noteTitleHighlights = new ArrayList<>();
        searchQuery = "";

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        toolbar.setTitle("Your notes");
        setSupportActionBar(toolbar);

    }

}


