package com.hiekichi.sample.marubatsu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MBView extends SurfaceView implements SurfaceHolder.Callback {

	Masu[][] masu; // 升目を表現したクラスを2次元配列で用意
	final int masuSizeX = 3;  // 升目を横に何個配置するか
	final int masuSizeY = 3;  // 升目を縦に何個配置するか

	int value = 1; // 次に表示させる○×の値、1:○  2:×
	
	private SurfaceHolder	holder;  // サーフェイスホルダー
	Paint paint = new Paint();  // ペイントインスタンス
	
	public MBView(Context context) {
		super(context);

		// 升目一つ分のサイズをMasuクラスのスタティックフィールドに設定する
		Masu.width  = this.getWidth()  / masuSizeX;
		Masu.height = this.getHeight() / masuSizeY;

		// サーフェイスホルダーの準備
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setFixedSize(this.getWidth(), this.getHeight());

		// マスの準備
		masu = new Masu[masuSizeX][masuSizeY];
		for (int x = 0; x < masuSizeX; ++x) {
			for(int y = 0; y < masuSizeY; ++y) {
				masu[x][y] = new Masu(x, y);
			}
		}

		//repaint(this.getHolder());
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// ビュー作成時に行わせる処理
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder,
			                    int format, int w, int h) {
		// 画面サイズ変化時に行わせる処理
		// 升目一つ分のサイズを再設定
		Masu.width  = this.getWidth()  / masuSizeX;
		Masu.height = this.getHeight() / masuSizeY;

		// 再描画を指示
		this.repaint(holder);
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// ビュー終了時に行わせる処理
	}

	// 再描画を指示するときに明示的に呼び出すメソッド
	protected void repaint(SurfaceHolder holder) {
		// キャンバスをロック
		Canvas canvas = holder.lockCanvas();
		
		// 描画
		paint.setColor(Color.GREEN);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

		for (int x = 0; x < masuSizeX; ++x) {
			for(int y = 0; y < masuSizeY; ++y) {
				masu[x][y].draw(canvas);
			}
		}
		
		// キャンバスをアンロック
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// タッチされた時に行わせる処理

		// タッチされた升目がどれかを計算で求める
		int x = (int)event.getX() / (getWidth()  / masuSizeX);
		int y = (int)event.getY() / (getHeight() / masuSizeY);

		// 次に描画させるのが○か×かを設定する
		if (masu[x][y].getValue() == 0) {
			masu[x][y].setValue(value);
			if (value == 1) {
				value = 2;
			}
			else {
				value = 1;
			}
		}

		// 再描画を指示
		repaint(getHolder());
		return true;
	}
}

class Masu {
	public static int width, height; // 升目一つ分の大きさ
	int x, y; // 2次元配列の添字に相当
	int value = 0; // 0:空白  1:○  2:×
	private Paint paint;

	private Masu() { // 引数なしのコンストラクタ
		// ペイントインスタンスの生成と設定
		paint = new Paint();
		paint.setStrokeWidth(5.0F);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
	}
	
	public Masu(int x, int y) { // 引数付きのコンストラクタ
		this();
		this.x = x;       			this.y = y;
	}
	
	public void draw(Canvas canvas) { 
		// 描画で使用する各種値の計算
		int x1 = width * x;    			int y1 = height * y;
		int x2 = width * x + width;    	int y2 = height * y + height;
		int xMargin = width / 10;		int yMargin = height / 10;
		int hankei = Math.min( width / 2 - xMargin, height / 2 - yMargin);

		// 外枠の描画
		paint.setColor(Color.BLACK);
		canvas.drawRect(x1, y1, x2, y2, paint);

		if (value == 1) { // ○の描画
			paint.setColor(Color.BLUE);
			canvas.drawCircle((x1 + x2) / 2, (y1 + y2) / 2, hankei, paint);
		}
		else if (value == 2) { // ×の描画
			paint.setColor(Color.RED);
			canvas.drawLine(x1 + xMargin, y1 + yMargin, x2 - xMargin, y2 - yMargin, paint);
			canvas.drawLine(x1 + xMargin, y2 - yMargin, x2 - xMargin, y1 + yMargin, paint);
		}
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}