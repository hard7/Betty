package ru.h7.betty.bettymodule;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity implements h7Dialog.NoticeDialogListener {
    private DialogFragment foodDialog, sportDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foodDialog = new h7Dialog();
        sportDialog = new h7Dialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void foodButtonPressed(View view) {
        foodDialog.show(getFragmentManager(), "food");
    }

    public void sportButtonPressed(View view) {
        sportDialog.show(getFragmentManager(), "sport");
    }

    @Override
    public void dialogResponse(DialogFragment fragment, h7Dialog.DialogResponse response) {
        String msg = "Response: ";
        if(fragment == foodDialog) msg += ("Food");
        else if(fragment == sportDialog) msg += ("Sport");
        msg += " is " + response;
        showMessage(msg);

    }

    private void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
