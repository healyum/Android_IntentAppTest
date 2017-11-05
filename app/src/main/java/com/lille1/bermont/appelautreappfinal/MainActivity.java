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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int PICK_CONTACT_REQUEST = 1;  // The request code
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;
    private String nomAi = "David AI : ";
    // Numéro de téléphone d'exemple pour les émulateurs qui n'ont pas de contact dans le répertoire
    private String phoneNumber = "0612345678";

    //scan
    private Button scanBtn;
    private TextView formatTxt, contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reconnaissance vocale
        mVoiceInputTv = (TextView) findViewById(R.id.micro_indication);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        // ZXing lecteur de code barre, QR Code
        scanBtn = (Button)findViewById(R.id.scan_button);
        contentTxt = (TextView)findViewById(R.id.scan_content);

       // scanBtn.setOnClickListener((View.OnClickListener) this);
        Button btn_scan = (Button) findViewById(R.id.scan_button);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.scan_button){
                    IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                    scanIntegrator.initiateScan();
                }
            }
        });

        // Affiche un itinéraire sur une carte (Google Maps)
        Button btn_itineraire = (Button) findViewById(R.id.btn_itineraire);
        btn_itineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAfficherItineraire();
            }
        });

        // Prank : Attendre 2030 ou 2035 pour les robots domestiques
        Button btn_coffee = (Button) findViewById(R.id.btn_coffee);
        btn_coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCafe();
            }
        });

        // Appel le numéro de téléphone en mémoire ou celui sélectionné
        Button btn_appeler = (Button) findViewById(R.id.btn_appeler);
        btn_appeler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAppeler();
            }
        });

        // Envoie un sms au numéro de téléphone en mémoire ou celui sélectionné
        Button btn_sms = (Button) findViewById(R.id.btn_sms);
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doEnvoyerSms();
            }
        });

        // Partage un message parmi une liste d'application (boîte de dialogue de sélection)
        Button btn_chooser = (Button) findViewById(R.id.btn_chooser);
        btn_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doMontrerAppChooser();
            }
        });

        // Change l'activité en cours vers une nouvelle activité de notre application (Intent Explicite)
        Button btn_explicit_activity = (Button) findViewById(R.id.btn_explicit_activity);
        btn_explicit_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doChangerActivite();
            }
        });

        // Permet de choisir un contact parmi son répertoire téléphonique
        Button btn_contact = (Button) findViewById(R.id.btn_getContact);
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doChoisirContact();
            }
        });
    }

    private void doCafe() {
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(nomAi+"Tu prends un ou deux sucres ?");
    }

    private void doChoisirContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Montrer à l'utilisateur uniquement les contacts avec des numéros de téléphone
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Cas scan
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // si résultat du scan positif
        if (scanResult != null) {
            String scanContent = scanResult.getContents();
            contentTxt.setText("Résultat du scan: " + scanContent);
        }

        // Sélection requête adéquate lorsque l'activité reçoit une réponse
        switch (requestCode) {
            // Cas Reconnaissance vocale
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));

                    // Déclenchement des actions selon ce qu'on dit à Dadid dans le micro
                    if ((result).contains("faire le café") || (result).contains("café")) { doCafe(); }
                    if ((result).contains("partager un message") || (result).contains("partager")) { doMontrerAppChooser(); }
                    if ((result).contains("appeler")) { doAppeler(); }
                    if ((result).contains("sms") || (result).contains("envoyer sms") || (result).contains("envoyer un sms")) { doEnvoyerSms(); }
                    if ((result).contains("changer activité") || (result).contains("changer") || (result).contains("activité")) { doChangerActivite(); }
                    if ((result).contains("récupérer contact") || (result).contains("récupérer") || (result).contains("contact") || (result).contains("récupérer un contact")) { doChoisirContact(); }
                    if ((result).contains("afficher itinéraire") || (result).contains("afficher") || (result).contains("itinéraire")) { doAfficherItineraire(); }
                }
                break;
            }
            // Cas choisir un contact
            case PICK_CONTACT_REQUEST:{
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

                    // Récupère le numéro de téléphone depuis la colone des numéros
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    // stocke le numéro dans la variable privé phoneNumber de la classe
                    phoneNumber = number;

                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText(nomAi+"Que voulez-vous faire de ce numéro ? \n" + number);
                }
                break;
            }
        }
    }

    // Démarrer la reconnaissance vocale
    // On démarre un intent implicite qui va chercher une activité qui fait partie des fonctionnalités de base Android
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

    private void doAfficherItineraire() {
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

    private void doMontrerAppChooser() {
        Intent chooserIntent = new Intent(Intent.ACTION_SEND);
        chooserIntent.setType("text/plain");
        chooserIntent.putExtra(Intent.EXTRA_SUBJECT, "Super message");
        chooserIntent.putExtra(Intent.EXTRA_TEXT, "Je t'envoie un super message, génial non ?");

        // Toujours utiliser une ressource String pour afficher un titre dans l'interface de sélection
        // Ecrire un texte comme : "Partager à vos amis" en titre
        String title = getResources().getString(R.string.msg_a_envoyer);
        // Création d'un intent pour visualiser le chooser
        Intent chooser = Intent.createChooser(chooserIntent, title);
        startActivity(chooser);
    }

    // Intent explicite : on appelle une activité de son application par le nom exacte.
    private void doChangerActivite() {
        Intent intent = new Intent(this, ExplicitActivity.class);
        startActivity(intent);
    }
}
