package com.example.sinki.bai60_soap_phan3;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.sinki.com.example.sinki.config.Configuration;
import com.example.sinki.model.Contact;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnGet;

    ListView lvContact;
    ArrayList<Contact>dsContact;
    ArrayAdapter<Contact>arrayAdapter;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLayDanhSach();
            }
        });
    }

    private void xuLyLayDanhSach() {
        ContactTask task = new ContactTask();
        task.execute();
    }

    private void addControls() {
        btnGet = (Button) findViewById(R.id.btnGet);
        lvContact = (ListView) findViewById(R.id.lvContact);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Thông báo");
        progressDialog.setMessage("Đang tải danh sách...");
        progressDialog.setCanceledOnTouchOutside(false);

        dsContact = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<Contact>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                dsContact);
        lvContact.setAdapter(arrayAdapter);

    }
    private class ContactTask extends AsyncTask<Void,Void,ArrayList<Contact>>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            arrayAdapter.clear();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);
            arrayAdapter.clear();
            arrayAdapter.addAll(contacts);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<Contact> doInBackground(Void... params) {
            try
            {
                ArrayList<Contact>ds = new ArrayList<>();

                SoapObject request = new SoapObject(Configuration.NAME_SPACE,Configuration.METHOD_GET5CONTACT);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE = new HttpTransportSE(Configuration.SERVER_URL);
                httpTransportSE.call(Configuration.SOAP_ACTION_GETDETAIL,envelope);
                SoapObject data = (SoapObject) envelope.getResponse();
                for(int i = 0;i < data.getPropertyCount();i++)
                {
                    SoapObject object = (SoapObject) data.getProperty(i);
                    Contact contact = new Contact();
                    if(object.hasProperty("Ma"))
                        contact.setMa(Integer.parseInt(object.getPropertyAsString("Ma")));
                    if(object.hasProperty("Ten"))
                        contact.setTen(object.getPropertyAsString("Ten"));
                    if(object.hasProperty("Phone"))
                        contact.setPhone(object.getPropertyAsString("Phone"));
                    ds.add(contact);
                }
                return ds;
            }
            catch (Exception ex)
            {
                Log.e("LOI", ex.toString());
            }
            return null;
        }
    }
}
