package com.example.skynet;

import com.lf.skynet_service.WorkoutState;
import com.lf.skynet_service.WorkoutStreamData;

import java.util.ArrayList;
import java.util.List;

public class WorkoutStateDao {

    public interface Callback {
        void onWorkoutStateChanged(WorkoutState workoutState);
        void onWorkoutStreamUpdated(WorkoutStreamData workoutStream, Integer antPlusHeartRate);
    }

    private static WorkoutStateDao sInstance = null;

    public synchronized static WorkoutStateDao getInstance() {
        if (sInstance == null) {
            sInstance = new WorkoutStateDao();
        }

        return sInstance;
    }

    private WorkoutState mWorkoutState;
    private WorkoutStreamData mLatestWorkoutStream;
    private Integer mAntPlusHeartRate;
    private final List<Callback> mCallbacks = new ArrayList<>();

    private WorkoutStateDao() {

    }


    public WorkoutState getWorkoutState() {
        return mWorkoutState;
    }

    public String getWorkoutStateString() {
        return getWorkoutStateName(mWorkoutState);
    }

    public WorkoutStreamData getLatestWorkoutStream() {
        return mLatestWorkoutStream;
    }

    public Integer getAntPlusHeartRate() {
        return this.mAntPlusHeartRate;
    }

    public void setWorkoutState(WorkoutState workoutState) {
        if (workoutState != mWorkoutState) {
            mWorkoutState = workoutState;

            for (int i = mCallbacks.size() - 1; i >= 0; --i) {
                mCallbacks.get(i).onWorkoutStateChanged(mWorkoutState);
            }

            if (workoutState == WorkoutState.IDLE_DISABLED ||
                    workoutState == WorkoutState.IDLE_ENABLED ||
                    workoutState == WorkoutState.RESETTING_TO_IDLE ||
                    workoutState == WorkoutState.UNKNOWN) {
                updateWorkoutStream(null);
            }
        }
    }

    public void updateWorkoutStream(WorkoutStreamData workoutStream) {
        this.mLatestWorkoutStream = workoutStream;
        if (workoutStream != null) {
            setWorkoutState(workoutStream.getWorkoutState());
        }
        for (int i = mCallbacks.size() - 1; i >= 0; --i) {
            mCallbacks.get(i).onWorkoutStreamUpdated(workoutStream, mAntPlusHeartRate);
        }
    }

    public void setAntPlusHeartRate(Integer heartRate) {
        if ((heartRate != null && !heartRate.equals(this.mAntPlusHeartRate)) || this.mAntPlusHeartRate != null) {
            this.mAntPlusHeartRate = heartRate;
        }
    }

    public static String getWorkoutStateName(WorkoutState state){

        String workoutStateName = "Unknown";

        switch(state){
            case IDLE_DISABLED:
            case IDLE_ENABLED:
                workoutStateName = "Idle";
                break;
            case IDLE_WAITING_FOR_WORKOUT:
                workoutStateName = "Idle Waiting for Workout";
                break;
            case STARTING_WORKOUT:
                workoutStateName = "Starting";
                break;
            case COUNTDOWN:
                workoutStateName = "Countdown";
                break;
            case ACTIVE:
                workoutStateName = "Active";
                break;

            case COOLDOWN:
                workoutStateName = "Cooldown";
                break;

            case PAUSED:
                workoutStateName = "Paused";
                break;

            case WORKOUT_SUMMARY:
                workoutStateName = "Workout Summary";
                break;
            case SHUTTING_DOWN:
                workoutStateName = "Shutting Down";
                break;
            case RESETTING_TO_IDLE:
                workoutStateName = "Return to Idle";
                break;
            case SYSTEM_SCREEN:
                workoutStateName = "System Screen";
                break;

        }

        return workoutStateName;
    }

    public void registerCallback(Callback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void unregisterCallback(Callback callback) {
        mCallbacks.remove(callback);
    }
}
