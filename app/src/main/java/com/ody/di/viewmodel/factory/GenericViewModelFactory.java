package com.ody.di.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GenericViewModelFactory<T extends ViewModel> implements ViewModelProvider.Factory {
    private final Creator<T> creator;

    public interface Creator<T> {
        T create();
    }

    public GenericViewModelFactory(Creator<T> creator) {
        this.creator = creator;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) creator.create();
    }
}
