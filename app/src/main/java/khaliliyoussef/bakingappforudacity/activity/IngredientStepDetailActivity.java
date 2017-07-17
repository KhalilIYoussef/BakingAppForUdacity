package khaliliyoussef.bakingappforudacity.activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import khaliliyoussef.bakingappforudacity.R;
import khaliliyoussef.bakingappforudacity.fragment.IngredientStepDetailFragment;
import khaliliyoussef.bakingappforudacity.model.Ingredient;
import khaliliyoussef.bakingappforudacity.model.Step;
import static khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter.INGREDIENTS;
import static khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter.STEPS;
import static khaliliyoussef.bakingappforudacity.activity.IngredientStepActivity.isTabKey;
import static khaliliyoussef.bakingappforudacity.activity.IngredientStepActivity.POSITION;


public class IngredientStepDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_step_detail);
        if (savedInstanceState == null)
        {
            ArrayList<Ingredient> ingredients = getIntent().getParcelableArrayListExtra(INGREDIENTS);
            ArrayList<Step> steps = getIntent().getParcelableArrayListExtra(STEPS);
            int position = getIntent().getIntExtra(POSITION, 0);
            //get the boolean and if it doesn't find it the it's false
            boolean isTablet = getIntent().getBooleanExtra(isTabKey, false);
            IngredientStepDetailFragment fragment = new IngredientStepDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(POSITION, position);
            //it's a tablet or not
            bundle.putBoolean(isTabKey, isTablet);
            //save the ingredients
            bundle.putParcelableArrayList(INGREDIENTS, ingredients);
            //save the steps as well
            bundle.putParcelableArrayList(STEPS, steps);

            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_container, fragment)
                    .commit();

        }
    }

}
