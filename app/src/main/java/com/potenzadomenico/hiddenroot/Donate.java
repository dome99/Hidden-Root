package com.potenzadomenico.hiddenroot;
/**
 * Created by Dome on 21/03/2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class Donate extends AppCompatActivity implements View.OnClickListener {

    //The views
    private Button buttonPay;
    private EditText editTextAmount;

    //Payment Amount
    private String paymentAmount;
    //private ImageButton donate;
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    public static final String PAYPAL_CLIENT_ID =
            "Ac1aBHHKdBWVYwA2vuG_NmV3pLgfYwFKnMxRZQ8BzB-Zkm-suNgvWggDOllJnGmD6g6F1LZHPaIAtIof";
    public static final String PAYPAL_MARCHANT_NAME = "Domenico Potenza";

    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PAYPAL_CLIENT_ID)
            .merchantName(PAYPAL_MARCHANT_NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            //se il device permette viene creato il tasto '<-' nella toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        buttonPay = (Button) findViewById(R.id.buttonPay);
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);

        buttonPay.setOnClickListener(this);

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //gestione dell'evento del clic del tasto '<-' nella toolbar
        //che eseguirà la main activity
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(Donate.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void getPayment() {
        //Getting the amount from editText
        paymentAmount = editTextAmount.getText().toString();

        //Creating a paypalpayment
        if(!paymentAmount.isEmpty()){
            PayPalPayment payment = new PayPalPayment(
                    new BigDecimal(String.valueOf(paymentAmount)), "USD", "Donation",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            //Creating Paypal Payment activity intent
            Intent intent = new Intent(this, PaymentActivity.class);

            //putting the paypal configuration to the intent
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            //Puting paypal payment to the intent
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

            //Starting the intent activity for result
            //the request code will be used on the method onActivityResult
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        }else{
            Toast.makeText(Donate.this, R.string.null_import, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {
            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(
                        PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("STATE", paymentDetails);

                        //Starting a new activity for the payment details and
                        // also putting the payment details with intent
                        startActivity(new Intent(this, ConfirmationActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", paymentAmount));

                    } catch (JSONException e) {
                        Log.e("STATE", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("STATE", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("STATE", "An invalid Payment or PayPalConfiguration "
                                + "was submitted. Please see the docs.");
            }
        }
    }

    @Override
    public void onClick(View v) {
        getPayment();
    }
}
