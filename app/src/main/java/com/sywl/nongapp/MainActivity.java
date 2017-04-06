package com.sywl.nongapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sywl.nongapp.dotviewpage.CustomDotGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CustomDotGroup myDotGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void initView()
    {
        myDotGroup = (CustomDotGroup)this.findViewById(R.id.my_dot_group);

        List<String> imageList = new ArrayList<String>();
        imageList.add("aaa");
        imageList.add("bbb");
        imageList.add("ccc");
        myDotGroup.showWebImage(false, imageList);
    }
}
