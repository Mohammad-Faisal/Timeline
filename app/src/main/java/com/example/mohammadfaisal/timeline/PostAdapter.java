package com.example.mohammadfaisal.timeline;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PostAdapter extends ArrayAdapter<TimeLinePost> {

    private  Context mContext;
    private List<TimeLinePost> list;
    int mResource;


    //using viewholder
    private static class ViewHolder{
        TextView postAuthor;
        TextView postCaption;
        TextView postTimeDate;
        ImageView postImage;
        TextView postUpCount;
        TextView postDownCount;
        Button btn_up;
        Button btn_down;
    }


    public PostAdapter(Context context, int resource, List<TimeLinePost> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final View result;
        ViewHolder holder = new ViewHolder();


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource,parent,false);



            holder.postAuthor = (TextView) convertView.findViewById(R.id.txt_name);
            holder.postCaption = (TextView) convertView.findViewById(R.id.txt_caption);
            holder.postTimeDate = (TextView) convertView.findViewById(R.id.txt_time_date);
            holder.postImage = (ImageView) convertView.findViewById(R.id.img_post);
            holder.postUpCount = (TextView) convertView.findViewById(R.id.txt_up_count);
            holder.postDownCount = (TextView) convertView.findViewById(R.id.txt_down_count);
            holder.btn_up = (Button) convertView.findViewById(R.id.btn_thumbs_up);
            holder.btn_down = (Button) convertView.findViewById(R.id.btn_thumbs_down);


            convertView.setTag(holder);
            result  = convertView;
            //convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_post, parent, false);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }




        TimeLinePost post = getItem(position);





        if (post.getImage_url().equals("")){
            holder.postImage.setVisibility(View.GONE);
        }else{
            Picasso.with(holder.postImage.getContext())
                    .load(post.getImage_url())
                    .placeholder(holder.postImage.getContext().getResources().getDrawable(R.drawable.timeline_logo))
                    .fit()
                    .into(holder.postImage);
        }

        holder.postAuthor.setText(post.getName());
        holder.postTimeDate.setText(post.getTime_and_date());
        holder.postCaption.setText(post.getCaption());
        holder.postDownCount.setText(String.valueOf(post.getThumbs_down_cnt()));
        holder.postUpCount.setText(String.valueOf(post.getThumbs_up_cnt()));

        return result;
    }
}
