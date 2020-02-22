package com.inatel.avaliao_pratica;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.FileNotFoundException;
import java.util.UUID;


public class Cadastrar extends AppCompatActivity {

    public static final int internal_img =1;
    private final int PERMISSAO_REQUEST = 2;
    private String downloadImageUrl;
    private EditText Nome;
    private EditText Tel;
    private EditText Email;
    private EditText PrincHab;
    private Button botaoCadast;
    private Uri imagemSelecionada;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        inicializarComponentes();
        permissao();
        inicializarFirebase();
        botaoCadast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                armazenarFirebase();



            }
        });


    }

    private void armazenarFirebase() {
        Candidato c = new Candidato();
        String nome = Nome.getText().toString();
        String tel = Tel.getText().toString();
        String email = Email.getText().toString();
        String princhab= PrincHab.getText().toString();

        if (TextUtils.isEmpty(nome)){
            Toast.makeText(Cadastrar.this,"Por favor Entre com o Nome",Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(tel)){
            Toast.makeText(Cadastrar.this,"Por favor Entre com o Telefone",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(email)){
            Toast.makeText(Cadastrar.this,"Por favor Entre com o Email",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(princhab)){
            Toast.makeText(Cadastrar.this,"Por favor Entre com o as Principais Habilidades",Toast.LENGTH_SHORT).show();
        }else if(imagemSelecionada==null){
            Toast.makeText(Cadastrar.this,"Por favor Escolha uma Foto",Toast.LENGTH_SHORT).show();

        }else{
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref = storageReference.child("images/"+UUID.randomUUID().toString());
            ref.putFile(imagemSelecionada)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Cadastrar.this,"Uploaded",Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadImageUrl = uri.toString();

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Cadastrar.this,"Falhou "+e.getMessage(),Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded" +(int)progress+"%");
                        }
                    });
            c.setId(UUID.randomUUID().toString());
            c.setNome(nome);
            c.setTel(tel);
            c.setEmail(email);
            c.setPrincHab(princhab);
            c.setFotoDoCandidato(downloadImageUrl);
            databaseReference.child("Candidato").child(c.getId()).setValue(c);
            Toast.makeText(Cadastrar.this,"Candidato Cadastrado com Sucesso",Toast.LENGTH_SHORT).show();
            limparCampos();
        }

    }



    public void pegarImg(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,internal_img);
    }





    private void limparCampos() {
        Nome.setText("");
        Tel.setText("");
        Email.setText("");
        PrincHab.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == internal_img && resultCode == RESULT_OK){

                imagemSelecionada = intent.getData();
                String[] colunas = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(imagemSelecionada,colunas,null,null,null);
                cursor.moveToFirst();

                int index = cursor.getColumnIndex(colunas[0]);
                String pathImg = cursor.getString(index);
                cursor.close();

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagemSelecionada));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ImageView iv = (ImageView) findViewById(R.id.ImgVw);
                iv.setImageBitmap(bitmap);


        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions [], int[] grantResults) {
        if (requestCode == PERMISSAO_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// A permissão foi concedida. Pode continuar
 }else{

            }
            return;
        }
    }



    private void inicializarComponentes() {
        Nome = findViewById(R.id.txtNome);
        Tel = findViewById(R.id.txtTelefone);
        Email = findViewById(R.id.txtEmail);
        PrincHab = findViewById(R.id.txtPrHab);
        botaoCadast = findViewById(R.id.btCad);
    }
    private void permissao(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }
            else
            { ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSAO_REQUEST);
            }
        }
    }
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(Cadastrar.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }




}
