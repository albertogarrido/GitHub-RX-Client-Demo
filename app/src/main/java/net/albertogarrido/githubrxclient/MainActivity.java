package net.albertogarrido.githubrxclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RETROFIT = "retrofit";
    private static final String RX_RETROFIT = "rx-retrofit";
    private static final String OWNER = "ReactiveX";
    private static final String REPO = "RxAndroid";

    GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.contributors_recycler_view) RecyclerView contributorsRecyclerView;
    @Bind(R.id.type) TextView typeTextView;
    @Bind(R.id.owner_repo) TextView ownerAndRepo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        configureRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_retrofit) {
            launchRetrofit2Way();
        } else if (id == R.id.action_retrofit_rx_android) {
            launchRetrofit2RxJavaWay();
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchRetrofit2RxJavaWay() {

        Observable<List<Contributor>> observableContributor =
                gitHubService.repoContributorsRx(OWNER, REPO);

        observableContributor
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getApplicationContext(),
                                "Completed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(getApplicationContext(),
                                t.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onNext(List<Contributor> contributors) {
                        displayResults(contributors, RX_RETROFIT);
                    }
                });

    }

    private void launchRetrofit2Way() {

        Call<List<Contributor>> call = gitHubService.repoContributors(OWNER, REPO);
        if (!call.isExecuted()) {
            call.enqueue(new Callback<List<Contributor>>() {
                @Override
                public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                    List<Contributor> contributors = response.body();
                    displayResults(contributors, RETROFIT);
                    Toast.makeText(getApplicationContext(),
                            "Completed",
                            Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onFailure(Call<List<Contributor>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),
                            t.getMessage(),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    private void displayResults(List<Contributor> contributorsList, String type) {

        for (Contributor contributor : contributorsList) {
            Log.d(TAG, contributor.toString());
        }

        ContributorAdapter categoryAdapter = new ContributorAdapter(contributorsList, this);
        contributorsRecyclerView.setAdapter(categoryAdapter);

        typeTextView.setText(type);
        ownerAndRepo.setText(OWNER + "/" + REPO);
    }

    public void configureRecyclerView() {
        contributorsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        contributorsRecyclerView.setLayoutManager(llm);
    }
}
