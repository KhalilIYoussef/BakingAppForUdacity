package khaliliyoussef.bakingappforudacity.widget;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.support.v7.widget.RecyclerView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import khaliliyoussef.bakingappforudacity.R;

import static khaliliyoussef.bakingappforudacity.data.RecipeContract.RECIPE_CONTENT_URI;
import static khaliliyoussef.bakingappforudacity.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENT_MEASURE;
import static khaliliyoussef.bakingappforudacity.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENT_NAME;
import static khaliliyoussef.bakingappforudacity.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENT_QUANTITY;
import static khaliliyoussef.bakingappforudacity.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_ID;
import static khaliliyoussef.bakingappforudacity.data.RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME;

// a class that extend RemoteViewsServices must implement onGetViewFactory
public class RecipeWidgetRemoteViewsService extends RemoteViewsService {
    // these indices must match the projection
    static final int INDEX_RECIPE_ID = 0;
    static final int INDEX_RECIPE_NAME = 1;
    static final int INDEX_INGREDIENT_NAME = 2;
    static final int INDEX_INGREDIENT_MEASURE = 3;
    static final int INDEX_INGREDIENT_QUANTITY = 4;
    private static final String[] RECIPE_COLUMNS =
            {
            COLUMN_RECIPE_ID,
            COLUMN_RECIPE_NAME,
            COLUMN_INGREDIENT_NAME,
            COLUMN_INGREDIENT_MEASURE,
            COLUMN_INGREDIENT_QUANTITY
            };



            //take an Intent as it's parameter
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate()
            {

            }

            @Override
            public void onDataSetChanged()
            {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                //get all the recipes from the CP
                data = getContentResolver().query(RECIPE_CONTENT_URI,
                        RECIPE_COLUMNS,
                        null,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy()
            {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == RecyclerView.NO_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.ingredient_list_item);

                views.setTextViewText(R.id.tv_ingredient, data.getString(INDEX_INGREDIENT_NAME));
                views.setTextViewText(R.id.tv_measure, data.getString(INDEX_INGREDIENT_MEASURE));
                views.setTextViewText(R.id.tv_quantity, String.valueOf(data.getInt(INDEX_INGREDIENT_QUANTITY)));

                views.setOnClickFillInIntent(R.id.ingredient_item, new Intent());
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.ingredient_list_item);
            }

            @Override
            public int getViewTypeCount()
            {
                return 1;
            }

            @Override
            public long getItemId(int position)
            {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_RECIPE_ID);
                return position;
            }

            @Override
            public boolean hasStableIds()
            {
                return true;
            }
        };
    }
}
