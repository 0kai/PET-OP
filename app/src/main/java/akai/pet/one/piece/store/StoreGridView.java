package akai.pet.one.piece.store;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

public class StoreGridView extends GridView{
	
	public StoreGridView(Context context) {
		super(context);
	}

	public StoreGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public StoreGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}
}
