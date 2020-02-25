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
import java.sql.SQLOutput;
import java.util.UUID;

public class Atualizar extends AppCompatActivity {
    public static final int internal_img =1;
    private final int PERMISSAO_REQUEST = 2;
   EditText edNome,edTel,edEmail,edPrinHab;
   Button atualiza;
   String id,nome,tel,email,prinhab,fotocand, downloadImageUrlAt;
   Uri imagemAtualizada;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    private StorageReference storageReference;
     FirebaseStorage guardar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar);
        edNome = findViewById(R.id.txtNomeAt);
        edTel = findViewById(R.id.txtTelefoneAt);
        edEmail = findViewById(R.id.txtEmailAt);
        edPrinHab = findViewById(R.id.txtPrHabAt);
        atualiza = findViewById(R.id.btAt);
        permissao();

        Intent intent = getIntent();
        id =intent.getStringExtra("id");
        nome = intent.getStringExtra("nome");
        tel = intent.getStringExtra("telefone");
        email = intent.getStringExtra("email");
        prinhab = intent.getStringExtra("princHab");
        fotocand = intent.getStringExtra("FotoDoCandidato");

        edNome.setText(nome);
        edTel.setText(tel);
        edEmail.setText(email);
        edPrinHab.setText(prinhab);
        inicializarFirebase();
        atualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          armazenarFirebase();

            }
        });


    }
    private void armazenarFirebase() {
        final String anome,atel,aemail,aprinhab;
        final Candidato ca = new Candidato();
        anome = edNome.getText().toString();
        atel = edTel.getText().toString();
        aemail = edEmail.getText().toString();
        aprinhab = edPrinHab.getText().toString();

        if (TextUtils.isEmpty(anome)){
            Toast.makeText(Atualizar.this,"Por favor Entre com o Nome",Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(atel)){
            Toast.makeText(Atualizar.this,"Por favor Entre com o Telefone",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(aemail)){
            Toast.makeText(Atualizar.this,"Por favor Entre com o Email",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(aprinhab)){
            Toast.makeText(Atualizar.this,"Por favor Entre com o as Principais Habilidades",Toast.LENGTH_SHORT).show();
        }else if(imagemAtualizada==null){
            Toast.makeText(Atualizar.this,"Por favor Escolha uma Foto",Toast.LENGTH_SHORT).show();

        }else{
            guardar = FirebaseStorage.getInstance();
            System.out.println(fotocand);
            StorageReference imgRef = guardar.getReferenceFromUrl(fotocand);

            imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Atualizar.this,"Foto Antiga Removida",Toast.LENGTH_SHORT).show();
                    final ProgressDialog progressDialog = new ProgressDialog(Atualizar.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    final StorageReference ref = storageReference.child("images/"+UUID.randomUUID().toString());
                    ref.putFile(imagemAtualizada)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Atualizar.this,"Uploaded",Toast.LENGTH_SHORT).show();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadImageUrlAt = uri.toString();




                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Candidato").child(id);
                                            ca.setId(id);
                                            ca.setNome(anome);
                                            ca.setTel(atel);
                                            ca.setEmail(aemail);
                                            ca.setPrincHab(aprinhab);
                                            ca.setFotoDoCandidato(downloadImageUrlAt);
                                            databaseReference.setValue(ca);
                                            limparCampos();
                                            Toast.makeText(Atualizar.this,"Informações atualizadas com sucesso",Toast.LENGTH_SHORT).show();

                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Atualizar.this,"Falhou "+e.getMessage(),Toast.LENGTH_SHORT).show();


                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded" +(int)progress+"%");
                                }
                            });
                }
            });


        }

    }

    public void pegarImg(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,internal_img);
    }
    private void limparCampos() {
        edNome.setText("");
       edTel.setText("");
        edEmail.setText("");
       edPrinHab.setText("");
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
    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == internal_img && resultCode == RESULT_OK){

            imagemAtualizada = intent.getData();
            String[] colunas = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imagemAtualizada,colunas,null,null,null);
            cursor.moveToFirst();

            int index = cursor.getColumnIndex(colunas[0]);
            String pathImg = cursor.getString(index);
            cursor.close();

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagemAtualizada));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ImageView iv = (ImageView) findViewById(R.id.ImgVwAt);
            iv.setImageBitmap(bitmap);


        }

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
        FirebaseApp.initializeApp(Atualizar.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }
}


