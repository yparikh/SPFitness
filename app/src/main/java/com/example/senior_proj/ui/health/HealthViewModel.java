package com.example.senior_proj.ui.health;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HealthViewModel extends ViewModel {

    private MutableLiveData<Float> userWaterDrank;
    private MutableLiveData<Float> userWaterGoal;
    private MutableLiveData<Boolean> firstTime;
    private MutableLiveData<String> userVolPref;

    public HealthViewModel() {
        firstTime = new MutableLiveData<>();
        userWaterDrank = new MutableLiveData<>();
        userWaterGoal = new MutableLiveData<>();
        userVolPref = new MutableLiveData<>();

        firstTime.setValue(Boolean.TRUE);
        userWaterDrank.setValue((float) 0);
        userWaterGoal.setValue((float) 1893);
        userVolPref.setValue("ml");
    }

    MutableLiveData<Boolean> getFirstTime() {return firstTime;}
    MutableLiveData<Float> getUserDrank() {return userWaterDrank;}
    MutableLiveData<Float> getUserWaterGoal() {return userWaterGoal;}
    MutableLiveData<String> getUserVolPref() {return userVolPref;}
    //LiveData<Boolean> setFirstTime(boolean flagset) {mfirstTime = flagset;}
}