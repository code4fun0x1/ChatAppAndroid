package com.example.shashank.enigmaproxy;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Created by Shashank on 04-09-2016.
 */


public class NewImprovedAdapter extends RecyclerView.Adapter<NewImprovedHolder> {

    List<ListModel> list = Collections.emptyList();
    public Context context;
    Bitmap img;
    ListModel list1;
    private String filename = "USERID";
    String uid = "";
    ProgressDialog mProgressDialog;
    int targetW = 0;
    int targetH = 0;

    private OnImageClickListener callback;

    public void setOnImageClickListener(OnImageClickListener callback) {
        this.callback = callback;
    }

    public interface OnImageClickListener {
        void changeFragment(String path, ImageView iv);
    }


    public NewImprovedAdapter(List<ListModel> list, Context context) {
        this.list = list;
        this.context = context;

        uid = context.getSharedPreferences(filename, 0).getString("ID", "-1");
    }

    @Override
    public NewImprovedHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate the layout
        View view;
        NewImprovedHolder holder;
        if (viewType == 0) {
            view = LayoutInflater.from(this.context).inflate(R.layout.chat_left, parent, false);
            holder = new NewImprovedHolder(view, context);
        } else {
            view = LayoutInflater.from(this.context).inflate(R.layout.chat_right, parent, false);
            holder = new NewImprovedHolder(view, context);
        }

        //View v = LayoutInflater.from(this.context).inflate(R.layout.new_card_row, parent, false);
        //  holder = new NewImprovedHolder(view,context);

        return holder;
    }

    @Override
    public void onBindViewHolder(final NewImprovedHolder holder, final int position) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            targetH = holder.imagecontent.getMaxHeight();
            targetW = holder.imagecontent.getMaxWidth();
        } else {
            targetW = 400;
            targetH = 400;
        }


        holder.imagecontent.setOnClickListener(null);


        //use the provided holder on onCreateViewHolder method to populate items
        list1 = this.list.get(position);
        //   holder.loadImageButton.setTag(position);
        holder.imagecontent.setImageBitmap(null);
        // holder.title.setText(list1.name);
        holder.secondryImageLayout.setVisibility(View.GONE);
        if (holder.circularimage != null) {
            //img = BitmapFactory.decodeFile(String.valueOf(file));
            //
            Glide.with(context)
                    .load(CentralURL.Updater_URL2 + list1.propic)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.progressbar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progressbar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .centerCrop()
                    .into(holder.circularimage);
            //    new ProfileLoaderTask(holder.circularimage, this.context).execute(list1.propic, "100", "100");

        }

        //this is for picture message
        if (!(list1.mtype).trim().equals("pic")) {

            holder.progressbar.setVisibility(View.GONE);
            //  holder.loadImageButton.setVisibility(View.GONE);
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(list1.message);
            holder.imagecontent.setVisibility(View.GONE);
            //   holder.loadImageButton.setOnClickListener(null);
        } else {
            holder.secondryImageLayout.setVisibility(View.VISIBLE);
            holder.imagecontent.setVisibility(View.VISIBLE);

            //this is to handle fragment
            holder.imagecontent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callback.changeFragment(list.get(position).message, holder.imagecontent);

                }
            });


            holder.content.setVisibility(View.GONE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                new ImageDownloaderTask(holder.imagecontent, this.context, holder.loadImageButton).execute(list1.message);
//            }
//            holder.loadImageButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.setIsRecyclable(false);
//                    new DownloadImage(holder, v.getContext(), list.get((Integer) v.getTag()).message).execute(list.get((Integer) v.getTag()).message);
//                    v.setVisibility(View.GONE);
//                }
//            });



            Glide.with(context)
                    .load(CentralURL.Updater_URL2 + list1.message)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.progressbar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progressbar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .centerCrop()
                    .into(holder.imagecontent);

        }




   /*     holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(list1);
                return false;
            }


        });
*/


    }


    @Override
    public int getItemViewType(int position) {
        if (list.get(position).uid.trim().equals(uid)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void insert(int position, ListModel data) {
        list.add(data);
        notifyItemInserted(position);
    }

    public void remove(ListModel data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void checkProfilePic(String pic) {
        for (int i = 0; i < getItemCount(); i++) {
            ListModel item = list.get(i);
            if (item.utype.equals("me")) {
                item.propic = pic;
            }
        }
    }


    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        BitmapFactory.Options options = new BitmapFactory.Options();
        FileInputStream fps;
        private WeakReference<ImageView> imageViewReference;
        private WeakReference<ImageButton> imageButton = null;
        int x;
        Context context;
        File file;

        public ImageDownloaderTask(ImageView imageView, Context context) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.context = context;
            x = 0;
        }

        public ImageDownloaderTask(ImageView imageView, Context context, ImageButton button) {

            imageViewReference = new WeakReference<ImageView>(imageView);
            this.context = context;
            x = 1;
            imageButton = new WeakReference<ImageButton>(button);
        }


        @Override
        protected Bitmap doInBackground(final String... params) {

            file = new File(this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), params[0]);
            if (file.exists()) {
                String TAG = "BATMAN ADAPTER";
                Log.d(TAG, "File Exists   " + params[0]);
                try {
                    return getPic(params[0]);
                } catch (Exception e) {
                    Log.d(TAG, "Unable to read file\n" + e.toString());
                }
            } else {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (x == 0) {
                try {
                    if (imageViewReference != null) {
                        ImageView imageView = imageViewReference.get();
                        if (imageView != null) {
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.mainicon);
                                imageView.setImageDrawable(placeholder);
                            }
                        } else {
                            Toast.makeText(this.context, "returned bitmap is null", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(this.context, "imagereference null", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }
            } else {
                if (imageButton != null && imageViewReference != null) {
                    ImageView imageView = imageViewReference.get();
                    ImageButton b = imageButton.get();
                    if (bitmap == null) {
                        b.setVisibility(View.VISIBLE);
                    } else {
                        b.setVisibility(View.GONE);
                        imageView.setImageBitmap(bitmap);
                    }
                }

            }


        }


    }


    private Bitmap getPic(String mCurrentPhotoPath) {
        // Get the dimensions of the View

        File tfile = new File(this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mCurrentPhotoPath);
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(tfile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeFile(tfile.getAbsolutePath(), bmOptions);
        if (bitmap == null)
            Log.d("BATMAN ADAPTER", "returned null");
        return bitmap;
    }


    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private WeakReference<ImageView> imageViewReference;
        private WeakReference<ProgressBar> progressbar;
        private WeakReference<NewImprovedHolder> holder;
        Context con;

        String filename;

        public DownloadImage(NewImprovedHolder holder, Context context, final String name) {
            imageViewReference = new WeakReference<ImageView>(holder.imagecontent);
            progressbar = new WeakReference<ProgressBar>(holder.progressbar);
            this.holder = new WeakReference<NewImprovedHolder>(holder);
            filename = name;
            con = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar p = progressbar.get();
            p.setVisibility(View.VISIBLE);
            // Create a progressdialog
            //    mProgressDialog = new ProgressDialog(con);
            // Set progressdialog title
            // Set progressdialog message
            //    mProgressDialog.setMessage("Loading...");
            //    mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            //     mProgressDialog.show();


        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = CentralURL.Updater_URL2 + URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView

            try {
                if (imageViewReference != null) {
                    ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        if (result != null) {
                            File root = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                            File file = new File(root, filename);
                            if (file.exists()) {

                            } else {
                                file.createNewFile();
                                try {
                                    //FileOutputStream out = new FileOutputStream(file);
                                    FileOutputStream out = new FileOutputStream(file, false);
                                    result.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.flush();
                                    out.close();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            imageView.setImageBitmap(getPic(file.getAbsolutePath()));
                        } else {
                            Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.mainicon);
                            imageView.setImageDrawable(placeholder);
                        }
                        ProgressBar p = progressbar.get();
                        p.setVisibility(View.GONE);
                        NewImprovedHolder v = this.holder.get();
                        v.setIsRecyclable(true);

                    }
                }
            } catch (Exception e) {

            }

            // Close progressdialog
            //  mProgressDialog.dismiss();
        }
    }


    private class ProfileLoaderTask extends AsyncTask<String, Void, Bitmap> {
        String TAG = "BATMAN";
        BitmapFactory.Options options = new BitmapFactory.Options();
        FileInputStream fps;
        private WeakReference<CircularImageView> imageViewReference;
        Context context;
        File file;

        public ProfileLoaderTask(CircularImageView imageView, Context context) {
            imageViewReference = new WeakReference<CircularImageView>(imageView);
            this.context = context;
        }


        @Override
        protected Bitmap doInBackground(final String... params) {

            file = new File(this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), params[0]);
            if (file.exists()) {

                Log.d(TAG, "File Exists");
                try {

                    Bitmap b = BitmapFactory.decodeFile(String.valueOf(file));
                    Log.d(TAG, "File successfully loaded");
                    return b;
                } catch (Exception e) {
                    Log.d(TAG, "Unable to read file\n" + e.toString());
                }
            } else {
            }
            Log.d(TAG, "returning null");
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            try {
                if (imageViewReference != null) {
                    CircularImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            Log.d(TAG, "got null bitmap");
                            Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.default_profile_pic);
                            imageView.setImageDrawable(placeholder);
                        }
                    }
                }
            } catch (Exception e) {

            }


        }
    }
}