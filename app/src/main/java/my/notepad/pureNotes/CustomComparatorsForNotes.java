package my.notepad.pureNotes;

import java.util.Comparator;

public class CustomComparatorsForNotes {

    private String currentComparator;

    public CustomComparatorsForNotes(){

    }

    public Comparator<NoteViewItem> ALPHABETICAL(){
        currentComparator = "ALPHABETICAL";
        return new Comparator<NoteViewItem>() {
            @Override
            public int compare(NoteViewItem o1, NoteViewItem o2){
                return o1.getName().compareTo(o2.getName());
            }
        };
    }

    public Comparator<NoteViewItem> DATE_MODIFIED(){
        currentComparator = "DATE_MODIFIED";
        return new Comparator<NoteViewItem>() {
            @Override
            public int compare(NoteViewItem o1, NoteViewItem o2){
                if (o1.getDateModifiedInMilliseconds() > o2.getDateModifiedInMilliseconds()){
                    return -1;
                }
                else if (o1.getDateModifiedInMilliseconds() < o2.getDateModifiedInMilliseconds()){
                    return 1;
                }
                return 0;
            }
        };
    }

    @Override
    public String toString(){
        return currentComparator;
    }
}

