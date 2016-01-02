package in.myfootprint.myfootprint.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import in.myfootprint.myfootprint.Controller;
import in.myfootprint.myfootprint.MyFootprintApplication;
import in.myfootprint.myfootprint.R;
import in.myfootprint.myfootprint.network.FetchFBPhoto;
import in.myfootprint.myfootprint.views.RoundedImageView;

/**
 * Created by aman on 05/12/15.
 */
public class NameAutoCompleteAdapter
        extends ArrayAdapter<NameAutoCompleteAdapter.PlaceAutocomplete> implements Filterable {

    private static final String TAG = "NameAutoCompleteAdapter";
    Context context;
    ImageLoader imageLoader = MyFootprintApplication.getInstance().getImageLoader();
    /**
     * Current results returned by this adapter.
     */
    private ArrayList<PlaceAutocomplete> mResultList;

    public NameAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    @Override
    public int getCount() {
        return mResultList.size();
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    @Override
    public PlaceAutocomplete getItem(int position) {

        return mResultList.get(position);
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {

                    mResultList = getAutocomplete(constraint);

                    if (mResultList != null) {

                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence id;
        public CharSequence imageUrl;
        public CharSequence name;

        PlaceAutocomplete(CharSequence id, CharSequence imageUrl, CharSequence name) {
            this.id = id;
            this.imageUrl = imageUrl;
            this.name = name;
        }

        @Override
        public String toString() {
            return name.toString();
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.autocomplete_list_names, null);

        TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);
        final NetworkImageView profileImage = (NetworkImageView) view.findViewById(R.id.profileImage);

        autocompleteTextView.setText(mResultList.get(position).toString());

        if(mResultList.get(position).imageUrl.toString() == null || mResultList.get(position).imageUrl.toString() == ""){

        }else{
            profileImage.setImageUrl(mResultList.get(position).imageUrl.toString(), imageLoader);

            /*String fbparts[] = mResultList.get(position).imageUrl.toString().split("com/");
            String fbparts2[] = fbparts[1].split("/pic");

            String fbUserID = fbparts2[0];
            new FetchFBPhoto(context, fbUserID, new FetchFBPhoto.AsyncResponse() {

                @Override
                public void processFinish(Bitmap output) {

                    profileImage.setImageBitmap(output);
                }
            }).execute();*/
        }

        return view;
    }

    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {

        JSONArray autocompletePredictions = Controller.getAutocompleteNames(constraint);
        if(autocompletePredictions != null){
            ArrayList resultList = new ArrayList<>(autocompletePredictions.length());
            for(int i = 0; i < autocompletePredictions.length(); i++) {

                try {
                    resultList.add(new PlaceAutocomplete(autocompletePredictions.getJSONObject(i).get("id").toString(),
                            autocompletePredictions.getJSONObject(i).get("image_url").toString(),
                            autocompletePredictions.getJSONObject(i).get("name").toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return resultList;
        }
        return null;
    }
}