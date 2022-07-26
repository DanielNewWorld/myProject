package ua.in.handycontacts2021.ui.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShareViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Якщо Вам подобається аплікація або Ви бажаєте підтримати проект, натисніть на кнопку \'Підтримати...\'");
    }

    public LiveData<String> getText() {
        return mText;
    }
}