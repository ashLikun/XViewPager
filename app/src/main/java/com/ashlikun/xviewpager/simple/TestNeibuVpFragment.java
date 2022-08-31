package com.ashlikun.xviewpager.simple;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ashlikun.xviewpager.fragment.FragmentPagerItem;

/**
 * 作者　　: 李坤
 * 创建时间: 2019/4/10　17:41
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
@Route(path = "/Fragment/test/neibu")
public class TestNeibuVpFragment extends Fragment {
    String id;
    View view;
    boolean isCache = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        id = getArguments().getString(FragmentPagerItem.ID);
        Log.e("onAttach", "id == " + id);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "id == " + id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_neibu, null);
        Log.e("onCreateView", "id == " + id + "    isCache = " + isCache);
        isCache = true;
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        Log.e("onViewCreated", "id == " + id);
        view = rootView.findViewById(R.id.view);
        TextView textView = rootView.findViewById(R.id.textView);
        textView.setText("我是内部第" + id + "个");
        switch (id) {
            case "1":
                view.setBackgroundColor(0xffffffff);
                break;
            case "2":
                view.setBackgroundColor(0xffffffff);
                break;
            case "3":
                view.setBackgroundColor(0xffffffff);
                break;
            case "4":
                view.setBackgroundColor(0xffffffff);
                break;
            case "5":
                view.setBackgroundColor(0xffffffff);
                break;
            case "6":
                view.setBackgroundColor(0xffffffff);
                break;
            case "7":
                view.setBackgroundColor(0xffffffff);
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("onActivityCreated", "id == " + id);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResume", "id == " + id);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "id == " + id);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("onStop", "id == " + id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("onDestroyView", "id == " + id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "id == " + id);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("onDetach", "id == " + id);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("onHiddenChanged", "id == " + id + "   hidden = " + hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        id = getArguments().getString(FragmentPagerItem.ID);
        Log.e("setUserVisibleHint", "id == " + id + "   isVisibleToUser = " + isVisibleToUser);
    }
}
