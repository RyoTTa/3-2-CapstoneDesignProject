package com.example.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class AddCard extends Fragment implements View.OnClickListener {

    public AddCard() {
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = (View) inflater.inflate(R.layout.addcard,container,false);
        ImageView addcard = (ImageView) layout.findViewById(R.id.addcard);
        addcard.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.addcard:
                //Toast.makeText(getActivity(), "OneFragment", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AddCardDetailActivity.class);
                startActivity(intent);
                break;

        }
    }
}
