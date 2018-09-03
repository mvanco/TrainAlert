package cz.intesys.trainalert.entity.realm;

import java.util.List;

import cz.intesys.trainalert.repository.DataHelper;
import io.realm.RealmList;
import io.realm.RealmObject;

public class CategorySetting extends RealmObject {
    private @DataHelper.CategoryId
    int categoryId;
    private @DataHelper.GraphicsId
    int graphics;
    private boolean soundNotification;
    private String ringtone;
    private RealmList<String> distances = new RealmList<>();
    private String textBefore;
    private boolean includeDistance;
    private String textAfter;
    public CategorySetting() {
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getGraphics() {
        return graphics;
    }

    public void setGraphics(int graphics) {
        this.graphics = graphics;
    }

    public boolean isSoundNotification() {
        return soundNotification;
    }

    public void setSoundNotification(boolean soundNotification) {
        this.soundNotification = soundNotification;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public List<String> getDistances() {
        return distances;
    }

    public void setDistances(RealmList<String> distances) {
        this.distances = distances;
    }

    public String getTextBefore() {
        return textBefore;
    }

    public void setTextBefore(String textBefore) {
        this.textBefore = textBefore;
    }

    public boolean isIncludeDistance() {
        return includeDistance;
    }

    public void setIncludeDistance(boolean includeDistance) {
        this.includeDistance = includeDistance;
    }

    public String getTextAfter() {
        return textAfter;
    }

    public void setTextAfter(String textAfter) {
        this.textAfter = textAfter;
    }
}
