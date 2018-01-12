package cz.intesys.trainalert.di;

import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.CategorySharedPrefs;
import cz.intesys.trainalert.entity.Poi;
import dagger.Component;

@PerCategory
@Component (dependencies = ApplicationComponent.class, modules = CategoryModule.class)
public interface CategoryComponent {
    void inject(Poi poi);

    void inject(Alarm alarm);

    CategorySharedPrefs getCategorySharedPrefs();
}
