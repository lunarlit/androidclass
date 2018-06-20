package com.example.user.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

// TwitAdapter
// 트윗 데이터 리스트와 레이아웃을 가지고 실제 리스트뷰를 만들어내는 어댑터
public class TwitAdapter extends BaseAdapter {

    // 뷰로 만들어낼 트윗의 목록
    ArrayList<Twit> twitList;
    StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images");

    // 생성자에서 twitList를 초기화한다. (초기화하지 않으면 사용 불가)
    public TwitAdapter(){
        twitList = new ArrayList<>();
    }

    public TwitAdapter(String userId){
        this();
    }

    // 목록의 개수를 요청하는 함수. 리스트의 사이즈를 돌려주도록 작성한다.
    @Override
    public int getCount() {
        return twitList.size();
    }


    // 목록의 특정 위치에 있는 트윗을 요청하는 함수. 리스트에서 꺼내가도록 작성한다.
    @Override
    public Object getItem(int position) {
        return twitList.get(position);
    }


    // 목록의 특정 위치에 있는 트윗의 id를 요청하는 함수. id가 따로 없으므로 위치를 id로 하도록 작성한다.
    @Override
    public long getItemId(int position) {
        return position;
    }


    // =========================================================================
    // getView
    // 특정 위치에 들어가야 하는 뷰를 생성하여 리스트뷰에 집어넣어주는 함수

    // 매개변수
    // position : 현재 그려서 반환해야 할 뷰의 위치 (리스트에서 몇 번째인지)
    // convertView : 뷰가 담기는 변수. 이미 생성되었던 뷰라면 (ex. 새 트윗이 생겼을 때 기존에 있던 트윗 뷰)
    // 비어있지 않다.
    // parent : 뷰가 생성되어 들어갈 부모 뷰
    // -------------------------------------------------------------------------
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // --------------- 뷰가 존재하지 않을 때 새로 생성하는 부분 ---------------
        // 이해하기 어렵고 매번 똑같으니 베껴쓰세요.
        final Context context = parent.getContext();

        TwitViewHolder viewHolder;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.twit, parent, false);

            viewHolder = new TwitViewHolder();
            viewHolder.writer = convertView.findViewById(R.id.writer);
            viewHolder.message = convertView.findViewById(R.id.message);
            viewHolder.profile = convertView.findViewById(R.id.profile);
            viewHolder.mention = convertView.findViewById(R.id.mention);
            viewHolder.like = convertView.findViewById(R.id.like);
            viewHolder.image = convertView.findViewById(R.id.image);

            convertView.setTag(viewHolder);

            // 단, R.layout.twit 자리만 해당하는 레이아웃 파일로 바꿔주시면 됩니다.
            // 이 부분에서 실제로 뷰가 생성되어 convertView 변수에 할당됩니다.

        } else {
            viewHolder = (TwitViewHolder)convertView.getTag();
        }
        // ------------------------------------------------------------------------

        // 트윗 뷰 안의 TextView를 컨트롤하기 위해 변수를 준비하고
        // convertView를 통해 twit.xml에 정의된 TextView들의 id와 연결합니다.

        final Twit t = twitList.get(position);

        // position 번째의 뷰를 만들고 있으므로
        // twitList에서 position 번째 트윗을 가져와 내용을 채웁니다.
        viewHolder.writer.setText(t.writer);
        viewHolder.message.setText(t.message);

        String picUrl = t.picId;
        String imageName = t.imageName;

        if(picUrl != null){
            GlideApp.with(context)
                    .load("https://graph.facebook.com/" + picUrl + "/picture?type=normal")
                    .circleCrop()
                    .into(viewHolder.profile);
        } else {
            GlideApp.with(context)
                    .load(R.drawable.empty_profile)
                    .centerCrop()
                    .into(viewHolder.profile);
        }

        if(imageName != null) {
            StorageReference imageRef = imagesRef.child(imageName);
            GlideApp.with(context)
                    .load(imageRef)
                    .into(viewHolder.image);
        } else {
            viewHolder.image.setImageDrawable(null);
        }

        viewHolder.mention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewTwitActivity.class);
                intent.putExtra("mention_to", t.writer);
                intent.putExtra("mention_on", t.id);
                intent.putExtra("request_code", 101);

                ((Activity)context).startActivityForResult(intent, 101);
            }
        });

        // 완성된 뷰를 돌려줍니다.
        return convertView;
    }
    // -------------------------------------------------------------------------
    // getView 함수 종료
    // =========================================================================

    // twitList에 새로 트윗을 추가하는 함수
    // 추가 후 뷰를 다시 그려야 화면에 반영되므로 notifyDataSetChanged()를 호출한다.
    void addItem(Twit twit) {
        twitList.add(0, twit);
        notifyDataSetChanged();
    }

    public class TwitViewHolder {
        public TextView writer;
        public TextView message;
        public ImageView profile;
        public ImageView mention;
        public ImageView like;
        public ImageView image;
    }

}
// TwitAdapter 종료