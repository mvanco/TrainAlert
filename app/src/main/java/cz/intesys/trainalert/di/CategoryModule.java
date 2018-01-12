package cz.intesys.trainalert.di;

import android.content.SharedPreferences;

import cz.intesys.trainalert.TaApplication;
import cz.intesys.trainalert.entity.CategorySharedPrefs;
import cz.intesys.trainalert.utility.Utility;
import dagger.Module;
import dagger.Provides;

@Module
public class CategoryModule {

    private final @Utility.CategoryId int mCategoryId;

    public CategoryModule(@Utility.CategoryId int category) {
        mCategoryId = category;
    }

    public static CategoryComponent getCategoryComponent(@Utility.CategoryId int categoryId) {
        return DaggerCategoryComponent.builder().applicationComponent(TaApplication.getInstance().getApplicationComponent()).categoryModule(new CategoryModule(categoryId)).build();
    }

    @Provides
    CategorySharedPrefs provideCategorySharedPrefs(SharedPreferences sharedPrefs) {
        return new CategorySharedPrefs(mCategoryId, sharedPrefs);
    }
}
