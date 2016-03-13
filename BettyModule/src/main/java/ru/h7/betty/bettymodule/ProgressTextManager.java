package ru.h7.betty.bettymodule;

import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import static android.os.Environment.getExternalStorageDirectory;

class ProgressTextManager {
    static private String pathToFolder = getExternalStorageDirectory() + "/BettyProgress/";

    public String load() {
        return "";
    }

    static public void save(String progressStr) {
        try {
            File folder = new File(pathToFolder);
            folder.mkdirs();

            File files[] = folder.listFiles();
            int max_ = 0;
            for(File file : files) {
                //FIXME OMG!
                String filename_ = file.getName();
                int a = Integer.parseInt(filename_.substring(0, filename_.lastIndexOf(".")));
                max_ = Math.max(max_, a);
            }

            String filename = String.format("%04d.txt", ++max_);
            File myFile = new File(folder, filename);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =  new OutputStreamWriter(fOut);
            myOutWriter.append(progressStr);
            myOutWriter.close();
            fOut.close();
//            Toast.makeText(getBaseContext(),
//                    "Done writing SD " + filename,
//                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//            Toast.makeText(getBaseContext(), e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
        }
    }

}
