package cz.intesys.trainalert.di;

import android.content.SharedPreferences;

import cz.intesys.trainalert.TaApplication;
import cz.intesys.trainalert.entity.CategorySharedPrefs;
import cz.intesys.trainalert.repository.DataHelper;
import dagger.Module;
import dagger.Provides;

@Module
public class CategoryModule {

    private final @DataHelper.CategoryId int mCategoryId;

    public CategoryModule(@DataHelper.CategoryId int category) {
        mCategoryId = category;
    }

    public static CategoryComponent getCategoryComponent(@DataHelper.CategoryId int categoryId) {
        return DaggerCategoryComponent.builder().applicationComponent(TaApplication.getInstance().getApplicationComponent()).categoryModule(new CategoryModule(categoryId)).build();
    }

    @Provides
    CategorySharedPrefs provideCategorySharedPrefs(SharedPreferences sharedPrefs) {
        return new CategorySharedPrefs(mCategoryId, sharedPrefs);
    }
}
