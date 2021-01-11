package my.notepad.pureNotes;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteViewItem {
    private String name;
    private String description;
    private String dateModifiedDMY;
    private long dateModifiedInMilliseconds;

    public NoteViewItem(AppCompatActivity activity, String filename, long dateModifiedInMilliseconds) {
        Log.i("INFO", "NOTE ABS PATH" + new File(filename).getAbsolutePath());
        String fDesc = "";
        try {
            FileInputStream fileInputStream = activity.getApplicationContext().openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            fDesc = bufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        String date = simpleDateFormat.format(dateModifiedInMilliseconds);

        this.name = filename;
        this.description = fDesc;
        this.dateModifiedDMY = date;
        this.dateModifiedInMilliseconds = dateModifiedInMilliseconds;
    }

    public void updateDescription(AppCompatActivity activity){
        String fDesc = "";
        try {
            FileInputStream fileInputStream = activity.getApplicationContext().openFileInput(name);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            fDesc = bufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.description = fDesc;
    }

    public void updateDateModified(long dateMod){
        this.dateModifiedInMilliseconds = dateMod;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        this.dateModifiedDMY = simpleDateFormat.format(dateModifiedInMilliseconds);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return description;
    }

    public String getDateModifiedDMY() {
        return dateModifiedDMY;
    }

    public long getDateModifiedInMilliseconds() {
        return dateModifiedInMilliseconds;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        if (o == null || getClass() != o.getClass()){ return false; }

        NoteViewItem noteViewItem = (NoteViewItem) o;

        return noteViewItem.getName().equals(((NoteViewItem) o).getName());
    }
}

