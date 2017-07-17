package khaliliyoussef.bakingappforudacity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import khaliliyoussef.bakingappforudacity.R;
import khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter;
import khaliliyoussef.bakingappforudacity.model.Recipe;
import khaliliyoussef.bakingappforudacity.adapter.IngredientStepAdapter.*;

import static khaliliyoussef.bakingappforudacity.adapter.RecipeAdapter.RECIPE;

public class MasterListFragment extends Fragment
{


    @BindView(R.id.rv_ingredients_steps)
    RecyclerView mIngredientStepRecyclerView;
    private Recipe mRecipe;
    private IngredientStepAdapter mIngredientStepAdapter;
    private OnIngredientStepListener mClickListener;
//mandatory constructor for the fragment
    public MasterListFragment()
    {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mClickListener = (OnIngredientStepListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    " must implement OnImageClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {       //there is dta saved then get it
            mRecipe = savedInstanceState.getParcelable(RECIPE);
        }
        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
        //here goes the binding for the fragment in the onCreateView
        ButterKnife.bind(this, rootView);
        mRecipe = getActivity().getIntent().getParcelableExtra(RECIPE);
        mIngredientStepAdapter = new IngredientStepAdapter(getContext(), mRecipe, mClickListener);
        mIngredientStepRecyclerView.setAdapter(mIngredientStepAdapter);
        return rootView;
    }



    //if you are using the if savedInstanceState!=null condition then it must be implemented
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(RECIPE, mRecipe);
    }
}
