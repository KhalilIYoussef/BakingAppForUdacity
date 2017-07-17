package khaliliyoussef.bakingappforudacity.activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import khaliliyoussef.bakingappforudacity.R;
import khaliliyoussef.bakingappforudacity.adapter.RecipeAdapter;
import khaliliyoussef.bakingappforudacity.api.ApiClient;
import khaliliyoussef.bakingappforudacity.api.ApiInterface;
import khaliliyoussef.bakingappforudacity.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static khaliliyoussef.bakingappforudacity.adapter.RecipeAdapter.RECIPE;


public class RecipeActivity extends AppCompatActivity {

    @BindView(R.id.rv_recipe)
    RecyclerView mRecipeRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mLoadingIndicator;
    private ArrayList<Recipe> mRecipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        //TODO (1) instead of writing the findViewById() method
        ButterKnife.bind(this);
        // if we have saved some thing in it already then extract it
        if (savedInstanceState != null)
        {
            //get the list of recipes
            mRecipesList = savedInstanceState.getParcelableArrayList(RECIPE);
            //hide the progress bar
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            //pass the list to the adapter to display it
            RecipeAdapter recipeAdapter = new RecipeAdapter(RecipeActivity.this, mRecipesList);
            mRecipeRecyclerView.setAdapter(recipeAdapter);
        }
        else

        {
            getRecipes();
        }
    }

    private void getRecipes() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        final Type TYPE = new TypeToken<ArrayList<Recipe>>() {}.getType();
        Call<JsonArray> call = apiInterface.getRecipe();
        call.enqueue(new Callback<JsonArray>()
        {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response)
            {
                mRecipesList = new Gson().fromJson(response.body(), TYPE);
                //remove the progress bar
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                //pass the arrayList to the adapter to display it
                RecipeAdapter recipeAdapter = new RecipeAdapter(RecipeActivity.this, mRecipesList);
                mRecipeRecyclerView.setAdapter(recipeAdapter);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                Toast.makeText(RecipeActivity.this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE, mRecipesList);
    }
}
