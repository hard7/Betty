package ru.h7.betty.bettymodule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.getExternalStorageDirectory;

interface ProgressTextHandler {
    void progressTextHandle(String progressText);
}

class ProgressTextManager {
    static private String pathToFolder = getExternalStorageDirectory() + "/BettyProgress/";
    Context context;

    ProgressTextManager(Context context_) {
        context = context_;
    }

    public String load(final ProgressTextHandler progressTextHandler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Title");

// Set up the input
        final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pathToFile = pathToFolder + input.getText().toString();
                File file = new File(pathToFile);
                if(file.exists() && !file.isDirectory()) {
                    String progressText = "";
                    try {
                        Scanner scanner = new Scanner(file, "UTF-8");
                        progressText = scanner.useDelimiter("\\A").next();
                        scanner.close(); // Put this call in a finally block
                        progressTextHandler.progressTextHandle(progressText);
                    }
                    catch(FileNotFoundException ex) {}
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        return "";
    }

    public void save(String progressStr) {
        String dot = "\\u002E";
        Pattern namePattern = Pattern.compile("\\d{4}" + dot + "txt");

        try {
            File folder = new File(pathToFolder);
            folder.mkdirs();

            File files[] = folder.listFiles();
            int max_ = 0;
            for(File file : files) {
                String filename_ = file.getName();
                Matcher matcher = namePattern.matcher(filename_);
                if (matcher.matches()) {
                    int a = Integer.parseInt(filename_.substring(0, filename_.lastIndexOf(".")));
                    max_ = Math.max(max_, a);
                }
            }

            String filename = String.format("%04d.txt", ++max_);
            File myFile = new File(folder, filename);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =  new OutputStreamWriter(fOut);
            myOutWriter.append(progressStr);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(context,
                    "Done writing SD " + filename,
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
