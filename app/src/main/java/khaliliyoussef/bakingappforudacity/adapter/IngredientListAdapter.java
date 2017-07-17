package khaliliyoussef.bakingappforudacity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import khaliliyoussef.bakingappforudacity.R;
import khaliliyoussef.bakingappforudacity.model.Ingredient;


public class IngredientListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private ArrayList<Ingredient> mIngredients;

    public IngredientListAdapter(ArrayList<Ingredient> ingredients)
    {
        this.mIngredients = ingredients;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_item, parent, false);

        return new IngredientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof IngredientViewHolder)
        {
            ((IngredientViewHolder) holder).mIngredient.setText(mIngredients.get(position ).getIngredient());
            ((IngredientViewHolder) holder).mMeasure.setText(mIngredients.get(position ).getMeasure());
            ((IngredientViewHolder) holder).mQuantity.setText(String.valueOf(mIngredients.get(position ).getQuantity()));
        }

    }

    @Override
    public int getItemCount() {
        return mIngredients.size() ;
    }

    @Override
    public int getItemViewType(int position)
    {
      return 0;
    }


    static class IngredientViewHolder extends RecyclerView.ViewHolder
    {

        @BindView(R.id.tv_ingredient)
        TextView mIngredient;
        @BindView(R.id.tv_measure)
        TextView mMeasure;
        @BindView(R.id.tv_quantity)
        TextView mQuantity;

        public IngredientViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
