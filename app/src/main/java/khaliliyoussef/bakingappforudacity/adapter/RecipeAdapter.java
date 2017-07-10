package khaliliyoussef.bakingappforudacity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import khaliliyoussef.bakingappforudacity.R;
import khaliliyoussef.bakingappforudacity.model.Recipe;
import khaliliyoussef.bakingappforudacity.activity.IngredientStepActivity;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public static final String RECIPE = "recipe";
    private Context mContext;
    private ArrayList<Recipe> mRecipes;

    public RecipeAdapter(final Context context, ArrayList<Recipe> mRecipes)
    {
        //get the context from the activity
        this.mContext = context;
        //get the passed arrayList
        this.mRecipes = mRecipes;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, final int position) {
        holder.setRecipeName(mRecipes.get(position).getName());
        holder.setRecipeImage(mContext, mRecipes.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) {
            return 0;
        }
        return mRecipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        @BindView(R.id.tv_recipe_name)
        TextView mRecipeName;
        @BindView(R.id.iv_recipe_image)
        ImageView mRecipeImage;

        public RecipeViewHolder(final View itemView)
        {
            super(itemView);
            //TODO this how it's done inside the RecyclerView
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setRecipeName(final String recipeName)
        {
            mRecipeName.setText(recipeName);
        }

        void setRecipeImage(final Context context, final String recipeImage)
        {
            if (!recipeImage.isEmpty())
            {
                mRecipeImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(recipeImage)
                        .into(mRecipeImage);
            }
        }

        @Override
        public void onClick(View v)
        {
            //when he clicks on the recyclerView go to the ingredientStepActivity
            Intent intent = new Intent(mContext, IngredientStepActivity.class);
            //pass the recipe that he touched to the activity
            intent.putExtra(RECIPE, mRecipes.get(getAdapterPosition()));

            mContext.startActivity(intent);
        }
    }
}
