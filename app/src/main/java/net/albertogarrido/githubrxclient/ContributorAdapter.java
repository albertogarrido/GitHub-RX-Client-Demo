package net.albertogarrido.githubrxclient;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by AlbertoGarrido on 8/4/15.
 */
public class ContributorAdapter extends RecyclerView.Adapter<ContributorAdapter.ContributorViewHolder> {

    private static final String TAG = ContributorAdapter.class.getSimpleName();
    private List<Contributor> contributorsList;
    private Context context;

    public ContributorAdapter(List<Contributor> contributorsList, Context context) {
        this.contributorsList = contributorsList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return contributorsList.size();
    }

    @Override
    public void onBindViewHolder(ContributorViewHolder contributorViewHolder, int i) {
        Contributor contributor = contributorsList.get(i);

        Resources resources = context.getResources();

        String contributionTimesText = resources.getString(R.string.contributed_times_string, contributor.contributions);
//        String contributorGHProfile = contributor.login + resources.getString(R.string.contributor_profile_string) + contributor.html_url;

        contributorViewHolder.login.setText(contributor.login);
        contributorViewHolder.contributedTimes.setText(contributionTimesText);
        contributorViewHolder.profileURL.setText(contributor.html_url);

    }

    @Override
    public ContributorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contributor_item, viewGroup, false);
        return new ContributorViewHolder(itemView, new ContributorAdapter.ContributorViewHolder.IContributorHolderClicks() {
            @Override
            public void onContributorClick(View caller, int adapterPosition) {
                Contributor contributor = contributorsList.get(adapterPosition);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contributor.html_url));
                itemView.getContext().startActivity(intent);

                Log.d(TAG, contributor.toString());
            }
        });
    }

    public static class ContributorViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final IContributorHolderClicks mListener;

        @Bind(R.id.login) TextView login;
        @Bind(R.id.contributed_times) TextView contributedTimes;
        @Bind(R.id.profile_url) TextView profileURL;


        public interface IContributorHolderClicks {
            void onContributorClick(View caller, int adapterPosition);
        }

        public ContributorViewHolder(View view, IContributorHolderClicks listener) {
            super(view);
            ButterKnife.bind(this, view);
            this.mListener = listener;
            login.setOnClickListener(this);
            contributedTimes.setOnClickListener(this);
            profileURL.setOnClickListener(this);
        }

        @Override
        public void onClick(View viewCaller) {
            mListener.onContributorClick(viewCaller, getAdapterPosition());
        }
    }
}