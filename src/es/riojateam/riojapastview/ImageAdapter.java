package es.riojateam.riojapastview;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context context;
	int imageBackground;
	List<Integer> pics;

	public ImageAdapter(Context c, List<Integer> pics) {
		this.pics = pics;
		context = c;
		TypedArray ta = /**/context
				.obtainStyledAttributes(R.styleable.Gallery1);
		imageBackground = ta.getResourceId(
				R.styleable.Gallery1_android_galleryItemBackground, 1);
		ta.recycle();
	}

	@Override
	public int getCount() {

		// return pics.length;
		return pics.size();
	}

	@Override
	public Object getItem(int arg0) {

		return arg0;
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ImageView iv = new ImageView(context);
		// iv.setImageResource(pics[arg0]);
		iv.setImageResource(pics.get(arg0));
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		iv.setLayoutParams(new Gallery.LayoutParams(150, 120));
		iv.setBackgroundResource(imageBackground);
		return iv;
	}

}