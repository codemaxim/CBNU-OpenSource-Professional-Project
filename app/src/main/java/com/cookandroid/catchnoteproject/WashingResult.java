package com.cookandroid.catchnoteproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WashingResult extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<WashingMachine> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        Intent intent =new Intent(getIntent());

        final String t = intent.getStringExtra("op1");
        final String m = intent.getStringExtra("op2");
        final String g = intent.getStringExtra("op3");

        recyclerView = findViewById(R.id.recyclerView);
       // recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList=new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터 쪽으로)

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        databaseReference = database.getReference("Washing"); //DB테이블연결

       // Query query = databaseReference.orderByChild("manufacturer").equalTo("삼성","1");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스 의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지않게 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {   //반복문으로 데이터 list추출

                    WashingMachine washingMachine = snapshot.getValue(WashingMachine.class); // 만들어뒀던 세탁기 객체에 데이터 담음
                    if((t.equals(washingMachine.getType()))&&(m.equals(washingMachine.getManufacturer()))&&(g.equals(washingMachine.getSize()))) {
                        arrayList.add(washingMachine); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보내준비
                    }

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생시
                Log.e("WashingResult", String.valueOf(databaseError.toException()));
            }
        });

            adapter = new WashAdapter(arrayList,this);
            recyclerView.setAdapter(adapter); //리사클러뷰에 어댑터 연결



    }
    //팝업창
    public void mOnPopupClick(View v){
        //데이터 담아서 팝업(액티비티) 호출
        recyclerView = findViewById(R.id.recyclerView);
        Integer position = recyclerView.getChildLayoutPosition(v);
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("model", arrayList.get(position).getId());
        intent.putExtra("price", String.valueOf(arrayList.get(position).getMoney()));
        intent.putExtra("spec", arrayList.get(position).getSpec());
        intent.putExtra("img", arrayList.get(position).getProfile());
        startActivityForResult(intent, 1);
    }
}
