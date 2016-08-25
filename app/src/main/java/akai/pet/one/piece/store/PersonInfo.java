package akai.pet.one.piece.store;

import android.graphics.Bitmap;



public class PersonInfo {
	public final static int FLAG_NORMAL = 1;
	public final static int FLAG_DOWNLOAD = 2;
	public final static int FLAG_UPDATE = 3;

	public String name;
	public Bitmap image;
	public String tag;
	public int onlineVersion;
	public int flag = FLAG_NORMAL;
}
