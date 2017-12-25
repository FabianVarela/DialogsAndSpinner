package com.developer.fabian.dialogsandspinner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.developer.fabian.dialogsandspinner.entity.WorldPopulation;
import com.developer.fabian.dialogsandspinner.function.DataJSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerArray, spinnerList;

    private CharSequence[] items;
    private ArrayList<String> worldlist;
    private ArrayList<WorldPopulation> world;

    private ProgressDialog barProgressDialog;
    private Handler updateBarHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerArray = findViewById(R.id.spinnerArrayRes);
        spinnerList = findViewById(R.id.spinnerList);

        items = getResources().getStringArray(R.array.color_arrays);

        getJSON();
        spinnerArray.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerList.setVisibility(View.GONE);

                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        showDialogWithList();
                        break;
                    case 2:
                        showDialogWithCheck();
                        break;
                    case 3:
                        showDialogRingProgress();
                        break;
                    case 4:
                        showBarDialog();
                        break;
                    case 5:
                        showCustomDialog();
                        break;
                    case 6:
                        spinnerList.setVisibility(View.VISIBLE);
                        break;
                }

                Toast.makeText(parent.getContext(),
                        String.format(getString(R.string.dialog_message), parent.getItemAtPosition(position).toString()),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void getJSON() {
        new DownloadJSON().execute();
    }

    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            world = new ArrayList<>();
            worldlist = new ArrayList<>();
            JSONObject jsonobject = DataJSON.getJSONFromURL();

            try {
                JSONArray jsonarray = jsonobject.getJSONArray("worldpopulation");

                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);

                    WorldPopulation worldpop = new WorldPopulation();

                    worldpop.setRank(Integer.parseInt(jsonobject.optString("rank")));
                    worldpop.setCountry(jsonobject.optString("country"));
                    worldpop.setPopulation(jsonobject.optString("population"));
                    worldpop.setFlag(jsonobject.optString("flag"));
                    world.add(worldpop);

                    worldlist.add(jsonobject.optString("country"));
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            spinnerList.setAdapter(new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, worldlist));

            spinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0,
                                           View arg1, int position, long arg3) {

                    showMessageDialog(world.get(position).getRank(),
                            world.get(position).getCountry(),
                            world.get(position).getPopulation());
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_text)
                .setMessage(R.string.message_text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), R.string.ok_text, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), R.string.cancel_text, Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void showDialogWithList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.select_color_text);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), items[i], Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialogWithCheck() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.select_color_text);
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), items[i], Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialogRingProgress() {
        final ProgressDialog ringProgress = ProgressDialog.show(this, getString(R.string.please_wait_text),
                getString(R.string.downloading_text), true);

        ringProgress.setCancelable(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ringProgress.dismiss();
            }
        }).start();
    }

    private void showBarDialog() {
        barProgressDialog = new ProgressDialog(MainActivity.this);

        barProgressDialog.setTitle(R.string.please_wait_text);
        barProgressDialog.setMessage(getString(R.string.downloading_text));
        barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(20);

        barProgressDialog.show();
        updateBarHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (barProgressDialog.getProgress() <= barProgressDialog.getMax()) {
                        Thread.sleep(2000);

                        updateBarHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                barProgressDialog.incrementProgressBy(2);
                            }
                        });

                        if (barProgressDialog.getProgress() == barProgressDialog.getMax()) {
                            barProgressDialog.dismiss();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showCustomDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);

        builder.setView(view)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText textUser = view.findViewById(R.id.editTextEmail);
                        EditText textPass = view.findViewById(R.id.editTextPassword);

                        if (!TextUtils.isEmpty(textUser.getText()) && !TextUtils.isEmpty(textPass.getText())) {
                            String res = String.format(getString(R.string.user_password_message), textUser.getText(), textPass.getText());
                            Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.empty_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, R.string.empty_message, Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showMessageDialog(int rank, String country, String population) {
        new AlertDialog.Builder(this)
                .setTitle(rank + ". " + country)
                .setMessage(String.format(getString(R.string.population_text), population))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), R.string.ok_text, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), R.string.cancel_text, Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }
}
