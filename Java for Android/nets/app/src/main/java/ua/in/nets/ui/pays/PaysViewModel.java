package ua.in.nets.ui.pays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PaysViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PaysViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Йде оновлення списку...");
    }

    public LiveData<String> getText() {
        return mText;
    }
}