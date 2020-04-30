package com.example.senior_proj.ui.health;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HealthViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HealthViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Health fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}