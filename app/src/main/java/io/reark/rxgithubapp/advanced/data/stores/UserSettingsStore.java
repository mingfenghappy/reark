/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.advanced.data.stores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.rxgithubapp.advanced.data.DataLayer;
import io.reark.rxgithubapp.advanced.data.schematicProvider.GitHubProvider;
import io.reark.rxgithubapp.advanced.data.schematicProvider.JsonIdColumns;
import io.reark.rxgithubapp.advanced.data.schematicProvider.UserSettingsColumns;
import io.reark.rxgithubapp.shared.pojo.UserSettings;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public class UserSettingsStore extends GsonStoreBase<UserSettings, Integer> {

    private static final int DEFAULT_REPOSITORY_ID = 15491874;

    public UserSettingsStore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);

        initUserSettings();
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull final UserSettings item) {
        return DataLayer.DEFAULT_USER_ID;
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return GitHubProvider.UserSettings.USER_SETTINGS;
    }

    private void initUserSettings() {
        getOnce(DataLayer.DEFAULT_USER_ID)
                .first()
                .filter(userSettings -> userSettings == null)
                .subscribe(userSettings -> {
                    put(new UserSettings(DEFAULT_REPOSITORY_ID));
                });
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { UserSettingsColumns.ID, UserSettingsColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final UserSettings item) {
        checkNotNull(item);

        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, DataLayer.DEFAULT_USER_ID);
        contentValues.put(JsonIdColumns.JSON, getGson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected UserSettings read(@NonNull final Cursor cursor) {
        checkNotNull(cursor);

        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        return getGson().fromJson(json, UserSettings.class);
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull final Integer id) {
        checkNotNull(id);

        return GitHubProvider.UserSettings.withId(id);
    }
}
