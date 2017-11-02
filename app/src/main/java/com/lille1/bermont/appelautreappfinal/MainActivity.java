package com.lille1.bermont.appelautreappfinal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;
    private String phoneNumber = "0612345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVoiceInputTv = (TextView) findViewById(R.id.micro_indication);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        // exemple pour itinéraire Google Maps
        Button btn_itineraire = (Button) findViewById(R.id.btn_itineraire);
        btn_itineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doItineraire();
            }
        });

        Button btn_appeler = (Button) findViewById(R.id.btn_appeler);
        btn_appeler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAppeler();
            }
        });

        Button btn_sms = (Button) findViewById(R.id.btn_sms);
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doEnvoyerSms();
            }
        });

        Button btn_chooser = (Button) findViewById(R.id.btn_chooser);
        btn_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appChooser();
            }
        });

        Button btn_explicit_activity = (Button) findViewById(R.id.btn_explicit_activity);
        btn_explicit_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentExplicit();
            }
        });


        Button btn_contact = (Button) findViewById(R.id.btn_getContact);
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });

    }

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Vérifie la requête à laquelle on répond (ici choisir contact)
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Vérifie que la requête a fonctionné
            if (resultCode == RESULT_OK) {
                // Récupère l'URI qui pointe sur le contact sélectionné
                Uri contactUri = data.getData();
                // On a seulement besoin du numéro de la colonne, car il n'y aura qu'une ligne de résultat
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Effectue une requête sur un contact pour récupérer la colonne du numéro
                // Pas besoin de trier, on ne peut recevoir qu'un seul numéro sur l'URI
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                phoneNumber = number;
                Log.d("tag",phoneNumber);

                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("Que voulez-vous faire de ce numéro ? \n" + number);
            }
        }

        // Vérifie la requête à laquelle on répond (ici textToSpeech)
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                }
                break;
            }
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Bonjour, comment puis-je vous aider ?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    private void doItineraire() {
        // Affichage d'un itinéraire de Paris à Lille
        Uri location = Uri.parse("http://maps.google.com/maps?saddr=Paris&daddr=Lille");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        // Vérifie qu'Android connait ce type d'activité
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Démarre une activité qui est sûre de fonctionner
        if (isIntentSafe) {
            startActivity(mapIntent);
        }
    }

    private void doAppeler() {

        Uri number = Uri.parse("tel:"+phoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);

        //version 1
        //startActivity(callIntent);

        //version 2
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(callIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Démarre une activité qui est sûre de fonctionner
        if (isIntentSafe) {
            startActivity(callIntent);
        }
    }

    private void doEnvoyerSms() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null));
        intent.putExtra("sms_body", "Bonjour, comment vas-tu ?");
        startActivity(intent);
    }

    private void appChooser() {
        Intent chooserIntent = new Intent(Intent.ACTION_VIEW);
        chooserIntent.setType("text/plain");

        // Always use string resources for UI text.
        // This says something like "Share this photo with"
        //tring title = getResources().getString(R.string.msg_a_envoyer);
        // Create intent to show chooser
        Intent chooser = Intent.createChooser(chooserIntent, "Envoyez un message à une autre app");

        startActivity(chooser);
    }

    private void intentExplicit() {
        Intent intent = new Intent(this, ExplicitActivity.class);
        startActivity(intent);
    }

}
