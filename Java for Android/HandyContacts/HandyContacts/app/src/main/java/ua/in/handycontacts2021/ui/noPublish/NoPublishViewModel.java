package ua.in.handycontacts2021.ui.noPublish;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NoPublishViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NoPublishViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("Створіть нове оголошення або виберіть з неопублікованих");
    }

    public LiveData<String> getText() {
        return mText;
    }
}