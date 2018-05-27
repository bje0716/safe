package kr.ac.dongeui.pangpang.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kr.ac.dongeui.pangpang.R;
import kr.ac.dongeui.pangpang.data.HelperData;
import kr.ac.dongeui.pangpang.databinding.HelperItemBinding;
import kr.ac.dongeui.pangpang.ui.activity.MainActivity;
import kr.ac.dongeui.pangpang.util.PreferenceUtil;

public class HelperAdapter extends RecyclerView.Adapter<HelperAdapter.HelperViewHolder> {

    private List<HelperData> list = new ArrayList<>();
    private PreferenceUtil util;

    public HelperAdapter() {
        // 서버에 있는 계정 db를 가져와서 보여줌
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("helper").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue(HelperData.class));
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public HelperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.helper_item, null);
        return new HelperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelperViewHolder holder, int position) {
        HelperData helperData = list.get(position);
        holder.binding.name.setText(helperData.getName());
        holder.binding.email.setText(helperData.getEmail());

        Glide.with(holder.itemView.getContext())
                .load(helperData.getImg())
                .into(holder.binding.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HelperViewHolder extends RecyclerView.ViewHolder {

        private HelperItemBinding binding;

        public HelperViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            util = new PreferenceUtil(itemView.getContext());

            itemView.setOnClickListener(v -> { // 아이템 클릭시
                switch (getAdapterPosition()) {
                    default:
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 자신의 계정 불러오기
                        if (user != null) {
                            if (binding.name.getText().toString().equals(user.getDisplayName())) { // 자신의 계정이 선택한 계정과 같으면
                                Toast.makeText(itemView.getContext(), "자기 자신은 선택할 수 없습니다", Toast.LENGTH_SHORT).show(); // 선택할 수 없다고 알려줌
                            } else { // 선택된 계정과 다르면
                                util.putString("helper", binding.name.getText().toString()); // 내부 db에 저장
                                Intent intent = new Intent(itemView.getContext(), MainActivity.class);
                                intent.putExtra("helper", binding.name.getText().toString());
                                itemView.getContext().startActivity(intent);
                            }
                        }
                        break;
                }
            });
        }
    }
}
