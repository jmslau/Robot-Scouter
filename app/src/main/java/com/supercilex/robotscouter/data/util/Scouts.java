package com.supercilex.robotscouter.data.util;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.supercilex.robotscouter.data.model.Scout;
import com.supercilex.robotscouter.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public final class Scouts implements Builder<Task<Map<TeamHelper, List<Scout>>>>, OnFailureListener, OnSuccessListener<Pair<TeamHelper, List<String>>> {
    private TaskCompletionSource<Map<TeamHelper, List<Scout>>> mScoutsTask = new TaskCompletionSource<>();
    private Map<TeamHelper, List<Scout>> mScouts = new ConcurrentHashMap<>();
    private ArrayList<Task<Void>> mScoutMetricsTasks = new ArrayList<>();

    private List<TeamHelper> mTeamHelpers;

    private Scouts(List<TeamHelper> helpers) {
        mTeamHelpers = helpers;
    }

    public static Task<Map<TeamHelper, List<Scout>>> getAll(List<TeamHelper> teamHelpers) {
        return new Scouts(teamHelpers).build();
    }

    @Override
    public Task<Map<TeamHelper, List<Scout>>> build() {
        List<Task<Pair<TeamHelper, List<String>>>> scoutIndicesTasks = new ArrayList<>();
        for (final TeamHelper helper : mTeamHelpers) {
            final TaskCompletionSource<Pair<TeamHelper, List<String>>> scoutIndicesTask = new TaskCompletionSource<>();
            scoutIndicesTasks.add(scoutIndicesTask.getTask());

            ScoutUtils.getIndicesRef(helper.getTeam().getKey())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            List<String> scoutKeys = new ArrayList<>();
                            for (DataSnapshot scoutKeyTemplate : snapshot.getChildren()) {
                                scoutKeys.add(scoutKeyTemplate.getKey());
                            }
                            scoutIndicesTask.setResult(new Pair<>(helper, scoutKeys));
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            scoutIndicesTask.setException(error.toException());
                            FirebaseCrash.report(error.toException());
                        }
                    });
        }


        for (Task<Pair<TeamHelper, List<String>>> scoutKeysTask : scoutIndicesTasks) {
            scoutKeysTask.addOnSuccessListener(this).addOnFailureListener(this);
        }

        Tasks.whenAll(scoutIndicesTasks).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Tasks.whenAll(mScoutMetricsTasks)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mScoutsTask.setResult(mScouts);
                            }
                        })
                        .addOnFailureListener(Scouts.this);
            }
        }).addOnFailureListener(this);


        return mScoutsTask.getTask();
    }

    @Override
    public void onSuccess(final Pair<TeamHelper, List<String>> pair) {
        for (String scoutKey : pair.second) {
            final TaskCompletionSource<Void> scoutMetricsTask = new TaskCompletionSource<>();
            mScoutMetricsTasks.add(scoutMetricsTask.getTask());

            Constants.FIREBASE_SCOUTS
                    .child(scoutKey)
                    .addListenerForSingleValueEvent(new ScoutListener(pair, scoutMetricsTask));
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        mScoutsTask.setException(e);
    }

    private class ScoutListener implements ValueEventListener {
        private Pair<TeamHelper, List<String>> mPair;
        private TaskCompletionSource<Void> mScoutMetricsTask;

        public ScoutListener(Pair<TeamHelper, List<String>> pair,
                             TaskCompletionSource<Void> scoutMetricsTask) {
            mPair = pair;
            mScoutMetricsTask = scoutMetricsTask;
        }

        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Scout scout = new Scout(snapshot.child(Constants.FIREBASE_NAME).getValue(String.class));
            Iterable<DataSnapshot> metrics =
                    snapshot.child(Constants.FIREBASE_METRICS).getChildren();
            for (DataSnapshot scoutMetricSnapshot : metrics) {
                scout.add(ScoutUtils.getMetric(scoutMetricSnapshot));
            }

            List<Scout> scouts = mScouts.get(mPair.first);
            if (scouts == null) {
                List<Scout> scoutList = new ArrayList<>();
                scoutList.add(scout);
                mScouts.put(mPair.first, scoutList);
            } else {
                scouts.add(scout);
                mScouts.put(mPair.first, scouts);
            }

            mScoutMetricsTask.setResult(null);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            mScoutMetricsTask.setException(error.toException());
            FirebaseCrash.report(error.toException());
        }
    }
}