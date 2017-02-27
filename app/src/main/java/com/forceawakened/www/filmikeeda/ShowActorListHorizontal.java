package com.forceawakened.www.filmikeeda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowActorListHorizontal extends Fragment implements AdapterActorHorizontal.ItemClickListener{
    private ArrayList<Actor> actorList;
    static final String ACTOR_LIST = "actor list";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view_horizontal, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        try {
            JSONObject actorListObject = new JSONObject(getArguments().getString(ACTOR_LIST));
            actorList = MovieUtils.getActorList(actorListObject);
            AdapterActorHorizontal mAdapter = new AdapterActorHorizontal(getActivity(), this, actorList);
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            return v;
        }
    }

    @Override
    public void onItemClick(int position) {
        Actor actor = actorList.get(position);
        Fragment fragment = new ShowActorInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ShowActorInfoFragment.ACTOR_ID, actor.getId());
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.loaded_content, fragment)
                .addToBackStack(null)
                .commit();
    }
}
