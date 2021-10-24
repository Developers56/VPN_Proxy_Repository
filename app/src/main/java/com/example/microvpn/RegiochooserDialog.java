package com.example.microvpn;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;

public class RegiochooserDialog extends BottomSheetDialogFragment implements RegionListAdapter.RegionListAdapterInterface {
    public static final String TAG = RegiochooserDialog.class.getSimpleName();
    RecyclerView regionsRecyclerView;
    private RegionListAdapter regionAdapter;
    private RegionChooserInterface regionChooserInterface;
    ProgressBar pgregions;
    View view;
    //RegionListAdapter.onClickInterface onclickInterface;

    public RegiochooserDialog() {
    }

    public static RegiochooserDialog newInstance() {
        RegiochooserDialog frag = new RegiochooserDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_region_chooser, container);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        /*onclickInterface = new RegionListAdapter.onClickInterface() {
            @Override
            public void setClick(int abc) {
                Toast.makeText(getContext(),"Position is"+abc,Toast.LENGTH_LONG).show();
                regionAdapter.notifyDataSetChanged();
                SharedPreferences preferencesvar = getActivity().getSharedPreferences("checkvalue", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorval = preferencesvar.edit();
                editorval.putInt("valueMB", abc);
                editorval.apply();
            }
        };*/

        pgregions = view.findViewById(R.id.regions_progress);
        regionsRecyclerView = view.findViewById(R.id.regions_recycler_view);
        regionsRecyclerView.setHasFixedSize(true);
        regionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       // regionAdapter = new RegionListAdapter(this, onclickInterface);
        regionsRecyclerView.setAdapter(regionAdapter);

        loadServers();
    }
    private void loadServers() {
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {
                pgregions.setVisibility(View.GONE);
                regionAdapter.setRegions(countries.getCountries());
            }
            @Override
            public void failure(VpnException e) {

                dismiss();
            }
        });
    }


    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof RegionChooserInterface) {
            regionChooserInterface = (RegionChooserInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        regionChooserInterface = null;
    }

    @Override
    public void onCountrySelected(Country item) {
        regionChooserInterface.onRegionSelected(item);
        dismiss();
    }

    public interface RegionChooserInterface {
        void onRegionSelected(Country item);
    }
}
