package com.ayst.adplayer.settings.template;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.settings.Setting;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/10/30.
 */

public class TemplateSettingFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mTemplateRecyclerView;
    Unbinder unbinder;

    private TemplateListAdapter mTemplateListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_template_setting, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {
        mTemplateRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mTemplateListAdapter = new TemplateListAdapter(mContext);
        mTemplateRecyclerView.setAdapter(mTemplateListAdapter);
        mTemplateListAdapter.setSelectedTemplate(Setting.get(mContext).getCurHomeTemplate());
        mTemplateListAdapter.addData(Setting.get(mContext).getHomeTemplatesMatched());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
