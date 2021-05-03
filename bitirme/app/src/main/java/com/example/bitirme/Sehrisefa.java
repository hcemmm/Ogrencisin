package com.example.bitirme;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Sehrisefa extends AppCompatActivity {


    TextView yorumdaki_ad;
    ImageView yorumyap_profilresmi;
    EditText yorum_alani;
    Button yorum_gonder,adresegit,sefrisefawebegit;
    String yorum,url,name_result,uid;
    RecyclerView recyclerView_mpark;
    FirebaseDatabase firebaseDatabase;
    DocumentReference reference;
    FirebaseFirestore firestore;
    DatabaseReference databaseReference;
    Yorumkullanicilar yorumkullanicilar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3,db5;

    Tumkullanicilar tumkullanicilar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sehrisefa);

        yorumdaki_ad = findViewById(R.id.yorumdaki_adsoyad);
        yorum_alani = findViewById(R.id.sefrisefayorum_alani);
        yorum_gonder = findViewById(R.id.sefrisefayorum_yap);
        yorumyap_profilresmi = findViewById(R.id.sefrisefakullanici_yorumpp);
        yorumkullanicilar = new Yorumkullanicilar();
        tumkullanicilar = new Tumkullanicilar();
        recyclerView_mpark = findViewById(R.id.recycler_sefrisefayorumgor);
        recyclerView_mpark.setHasFixedSize(true);
        recyclerView_mpark.setLayoutManager(new LinearLayoutManager(this));
        adresegit = findViewById(R.id.sefrisefaharitagit);
        sefrisefawebegit = findViewById(R.id.sefrisefawebgit);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String kullan_id = user.getUid();

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("kullanici").document(kullan_id);

        db1 = database.getReference("Tumyorumlar").child("SehriSefa").child(kullan_id);
        db2 = database.getReference("KYorumlar").child(kullan_id);
        db3 = database.getReference("Yorumlar").child("sehriisefa");
        db3.keepSynced(true);


        databaseReference =database.getReference("Yorumlar").child("sehriisefa");


        sefrisefawebegit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sehrisefaretrocfe/"));
                startActivity(webintent);

            }
        });

        adresegit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent haritaintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/place/%C5%9Eehr-i+Sefa+Retro+Coffee+%26+Kahvalt%C4%B1/@38.3467679,38.3184346,15z/data=!4m2!3m1!1s0x0:0x9eb2b5806096b2cb?sa=X&ved=2ahUKEwjilqHRwZfwAhWF-aQKHSu7D_kQ_BIwD3oECEUQBQ"));
                startActivity(haritaintent);
            }
        });
        yorum_gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yorum_yap();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            name_result = task.getResult().getString("ad");
                            uid = task.getResult().getString("uid");
                            url = task.getResult().getString("url");

                            Picasso.get().load(url).into(yorumyap_profilresmi);

                        }
                    }
                });

        FirebaseRecyclerOptions<Yorumkullanicilar> options =
                new FirebaseRecyclerOptions.Builder<Yorumkullanicilar>()
                        .setQuery(databaseReference, Yorumkullanicilar.class)
                        .build();


        FirebaseRecyclerAdapter<Yorumkullanicilar, Yorumsahip> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Yorumkullanicilar, Yorumsahip>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Yorumsahip holder, int position, @NonNull Yorumkullanicilar model) {
                        holder.setData(getApplicationContext(), model.getYorum(),model.getAdsoyad(),model.getUrl(),model.getUid(),model.getZaman(),model.getKonum());


                        holder.setProfile(getApplicationContext());
                        String guid = getItem(position).getUid();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String kullans_id = user.getUid();


                        yorum = getItem(position).getYorum();

                        holder.setOnClickListener(new Yorumsahip.Clicklistener() {
                            @Override
                            public void onItemlongClick(View view, int position) {
                                if (guid.equals(kullans_id)){
                                    holder.setOnClickListener(new Yorumsahip.Clicklistener() {
                                        @Override
                                        public void onItemlongClick(View view, int position) {

                                            yorum = getItem(position).getYorum();

                                            yorumsil(yorum);
                                        }
                                    });
                                }else {
                                    holder.setOnClickListener(null);
                                    return;
                                }
                            }
                        });




                        holder.yorumprofilbak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getApplicationContext(),GosterkullaniciActivity.class);
                                intent.putExtra("guid",guid);
                                startActivity(intent);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public Yorumsahip onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.yorumcu_items, parent, false);


                        return new Yorumsahip(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView_mpark.setAdapter(firebaseRecyclerAdapter);
    }

    private void yorumsil(String yorum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Sehrisefa.this);
        builder.setTitle("Sil");
        builder.setMessage("Yorumunuz Silinsin mi?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Query query = databaseReference.orderByChild("yorum").equalTo(yorum);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Sehrisefa.this, "Yorum Silindi", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

                Query query2 = db2.orderByChild("yorum").equalTo(yorum);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Sehrisefa.this, "Yorum Silindi", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void yorum_yap() {
        Calendar callfordate = Calendar.getInstance();
        SimpleDateFormat currentdate = new
                SimpleDateFormat("dd-MMMM-yyyy");
        final  String savedate = currentdate.format(callfordate.getTime());

        Calendar callfortime = Calendar.getInstance();
        SimpleDateFormat currenttime = new
                SimpleDateFormat("HH:mm");
        final  String savetime = currenttime.format(callfortime.getTime());

        String time = savedate+":"+savetime;
        String comment = yorum_alani.getText().toString();
        if (comment != null){

            yorumkullanicilar.setYorum(comment);
            yorumkullanicilar.setAdsoyad(name_result);
            yorumkullanicilar.setUid(uid);
            yorumkullanicilar.setUrl(url);
            yorumkullanicilar.setZaman(time);
            yorumkullanicilar.setKonum("Şehr-i Sefa");



            String id = db1.push().getKey();
            db1.child(id).setValue(yorumkullanicilar);

            String id2 = db2.push().getKey();
            db2.child(id2).setValue(yorumkullanicilar);

            String id1 = db3.push().getKey();
            db3.child(id1).setValue(yorumkullanicilar);

            yorum_alani.setText("");

            Toast.makeText(this, "Yorum Yapıldı.", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Lütfen Yorumunuzu Yazınız...", Toast.LENGTH_SHORT).show();
        }
    }

}