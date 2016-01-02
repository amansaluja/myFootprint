package in.myfootprint.myfootprint.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import in.myfootprint.myfootprint.utils.PrefUtils;

/**
 * Created by Aman on 11-12-2015.
 */
public class FetchFBPhoto extends AsyncTask<Void, Void, Bitmap> {

    String fbUserID;
    Context context;
    public AsyncResponse delegate = null;

    public FetchFBPhoto(Context context, String fbUserID, AsyncResponse delegate) {

        this.context = context;
        this.fbUserID = fbUserID;
        this.delegate = delegate;

    }

    public interface AsyncResponse {
        public void processFinish(Bitmap output);
    }

    @Override
    public Bitmap doInBackground(Void... urls) {
        //urls is an array not a string, so iterate through urls.
        //Get Picture Here - BUT DONT UPDATE UI, Return a reference of the object
        // safety check
        if (fbUserID == null)
            return null;

        String url = String.format(
                "https://graph.facebook.com/%s/picture?type=large",
                fbUserID);

        this.delegate = delegate;

        InputStream inputStream = null;

        try {
            inputStream = new URL(url).openStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        return bitmap;
    }

    @Override
    public void onPostExecute(Bitmap bitmap) {
        //Update UI
        if (bitmap != null){
            delegate.processFinish(bitmap);

            /*profileImage.setImageBitmap(bitmap);
            PrefUtils.setProfileImagePath(Controller.encodeTobase64(bitmap));*/

        }
    }
}