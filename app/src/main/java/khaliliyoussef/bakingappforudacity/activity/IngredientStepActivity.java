package khaliliyoussef.bakingappforudacity.activity;
import khaliliyoussef.bakingappforudacity.R;
import khaliliyoussef.bakingappforudacity.data.RecipeContract.*;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter;
import khaliliyoussef.bakingappforudacity.data.RecipeContract;
import khaliliyoussef.bakingappforudacity.fragment.IngredientStepDetailFragment;
import khaliliyoussef.bakingappforudacity.model.Ingredient;
import khaliliyoussef.bakingappforudacity.model.Recipe;
import static khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter.INGREDIENTS;
import static khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter.STEPS;
import static khaliliyoussef.bakingappforudacity.adapter.RecipeAdapter.RECIPE;
import static khaliliyoussef.bakingappforudacity.data.RecipeContract.RECIPE_CONTENT_URI;


public class IngredientStepActivity extends AppCompatActivity implements IngredientStepAdapter.OnIngredientStepListener {


    public static final String POSITION = "position";
    public static final String isTabKey = "isTabKey";
    private boolean isTablet;
    private Recipe mRecipe;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_step);

        //get the recipe that it as passed from the RecipeActivity
        mRecipe = getIntent().getParcelableExtra(RECIPE);

        //if the linear layout (which only can exist if it's a tablet) then isTablet is true
        isTablet = findViewById(R.id.detail_linear_layout) != null;
    }
/*
* well the are four cases
* 1-it's a tablet and he pressed the ingredient
* 2-it's a tablet and he didn't pressed the ingredients (pressed the steps)
* 3-it's not a tablet (it's a phone) and he pressed the ingredients
* 4-it's not a tablet (it's a phone) and he didn't pressed the ingredients (pressed the steps)*/
    @Override
    public void onIngredientStepSelected(int position)
    {
        //to parse it
        Bundle bundle = new Bundle();
//position==0 means the first element in the list (the ingredients)
        if (isTablet && position == 0)
        {
            IngredientStepDetailFragment fragment = new IngredientStepDetailFragment();
            bundle.putInt(POSITION, position);
            bundle.putBoolean(isTabKey, isTablet);
            bundle.putParcelableArrayList(INGREDIENTS, mRecipe.getIngredients());
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_ingredient_container, fragment)
                    .commit();

        }
        //it's tablet and it's not in the ingredient
        else if (isTablet)
        {
            IngredientStepDetailFragment fragment = new IngredientStepDetailFragment();
            bundle.putInt(POSITION, position);
            bundle.putBoolean(isTabKey, isTablet);
            bundle.putParcelableArrayList(STEPS, mRecipe.getSteps());
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_ingredient_container, fragment)
                    .commit();
        }
        else if (position == 0)
        {
            bundle.putInt(POSITION, position);
            bundle.putBoolean(isTabKey, isTablet);
            bundle.putParcelableArrayList(INGREDIENTS, mRecipe.getIngredients());
            Intent intent = new Intent(this, IngredientStepDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else
            {
            bundle.putInt(POSITION, position);
            bundle.putBoolean(isTabKey, isTablet);
            bundle.putParcelableArrayList(STEPS, mRecipe.getSteps());
            Intent intent = new Intent(this, IngredientStepDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_ingredient_step, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_favorite:
                if (isFavorite()) {
                    removeRecipeFromFavorites();
                    item.setIcon(R.drawable.ic_favorite_normal);
                    Toast.makeText(this,getString(R.string.favorite_removed_message)+ mRecipe.getName(), Toast.LENGTH_LONG).show();
                } else {
                    addRecipeToFavorites();
                    item.setIcon(R.drawable.ic_favorite_added);
                    Toast.makeText(this, getString(R.string.favorite_added_message)+ mRecipe.getName(), Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem menuItem = menu.findItem(R.id.action_favorite);
        if (isFavorite())
        {
            //change the icon to indicate it's a favorite recipe
            menuItem.setIcon(R.drawable.ic_favorite_added);
        }
        else //it's not favorite
            {
                //change the icon to indicate it's not favorite
            menuItem.setIcon(R.drawable.ic_favorite_normal);
            }
        return true;
    }


    private boolean isFavorite()
    {

        //get all the recipes where its "id" equal the current recipe if the return cursor is null then it's not fav
        //if the cursor is not null then it's favorite
        String[] projection = {RecipeEntry.COLUMN_RECIPE_ID};
        String selection = RecipeEntry.COLUMN_RECIPE_ID + " = " + mRecipe.getId();
        Cursor cursor = getContentResolver().query(RECIPE_CONTENT_URI,
                projection,
                selection,
                null,
                null,
                null);

        return (cursor != null ? cursor.getCount() : 0) > 0;
    }


    //sync because he might added or remove it  in the activity that we in as many times as he want
    // to completely sync with the database
    synchronized private void removeRecipeFromFavorites() {
        getContentResolver().delete(RECIPE_CONTENT_URI, null, null);
    }

    synchronized private void addRecipeToFavorites()
    {
        //delete the old recipes (it can only save one recipe )
        getContentResolver().delete(RECIPE_CONTENT_URI, null, null);
//save every ingredient in the database with the recipe name and id
        for (Ingredient ingredient : mRecipe.getIngredients()) {
            ContentValues values = new ContentValues();
            values.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, mRecipe.getId());
            values.put(RecipeEntry.COLUMN_RECIPE_NAME, mRecipe.getName());
            values.put(RecipeEntry.COLUMN_INGREDIENT_NAME, ingredient.getIngredient());
            values.put(RecipeEntry.COLUMN_INGREDIENT_MEASURE, ingredient.getMeasure());
            values.put(RecipeEntry.COLUMN_INGREDIENT_QUANTITY, ingredient.getQuantity());
            getContentResolver().insert(RECIPE_CONTENT_URI, values);
        }
    }




}
