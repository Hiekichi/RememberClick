package jp.ac.iwasaki.isc.event2013;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class RCView extends SurfaceView implements SurfaceHolder.Callback {

	Masu[][] mMasu; // 升目を表現したクラスを2次元配列で用意
	final int mMasuSizeX = 5;  // 升目を横に何個配置するか
	final int mMasuSizeY = 5;  // 升目を縦に何個配置するか

	int mBan = 0; // 0:先攻  1:後攻
	int mTekazuIma = 0;  // 何個までクリックしたか？
	int mTekazuMax = 0;  // 何個までクリックしなきゃいけないのか？
	int[] mColor = { Color.GREEN, Color.YELLOW };
	int[] mRirekiX = new int[200];
	int[] mRirekiY = new int[200];
	
	int mValue = 1; // いま押されたマスかどうかの判定用
	
	private SurfaceHolder	mHolder;  // サーフェイスホルダー
	Paint mPaint = new Paint();  // ペイントインスタンス
	
	public RCView(Context context) {
		super(context);

		// 升目一つ分のサイズをMasuクラスのスタティックフィールドに設定する
		Masu.width  = this.getWidth()  / mMasuSizeX;
		Masu.height = this.getHeight() / mMasuSizeY;

		// サーフェイスホルダーの準備
		mHolder = this.getHolder();
		mHolder.addCallback(this);
		mHolder.setFixedSize(this.getWidth(), this.getHeight());

		// マスの準備
		mMasu = new Masu[mMasuSizeX][mMasuSizeY];
		for (int x = 0; x < mMasuSizeX; ++x) {
			for(int y = 0; y < mMasuSizeY; ++y) {
				mMasu[x][y] = new Masu(x, y);
			}
		}
		
		// 開始のメッセージ
		Toast.makeText(getContext(), "ゲーム開始", Toast.LENGTH_SHORT).show();
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
		Masu.width  = this.getWidth()  / mMasuSizeX;
		Masu.height = this.getHeight() / mMasuSizeY;

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
		mPaint.setColor(mColor[mBan]);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);

		for (int x = 0; x < mMasuSizeX; ++x) {
			for(int y = 0; y < mMasuSizeY; ++y) {
				mMasu[x][y].draw(canvas);
			}
		}
		
		// キャンバスをアンロック
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// タッチされた時に行わせる処理
			//Toast.makeText(getContext(), "max:" + mTekazuMax + "\nima:" + mTekazuIma, Toast.LENGTH_LONG).show();
			// すべての升目のvalueを0クリア
			for (int x = 0; x < mMasuSizeX; ++x) {
				for(int y = 0; y < mMasuSizeY; ++y) {
					mMasu[x][y].setValue( 0 );
				}
			}
			// 再ゲームの判定
			if (mTekazuIma == 200) {
				mTekazuIma = mTekazuMax = mBan = 0;
				Toast.makeText(getContext(), "ゲーム開始", Toast.LENGTH_SHORT).show();
				repaint(getHolder()); // 再描画を指示
				return true;
			}
			// タッチされた升目がどれかを計算で求めて、その升目のvalueにmValueを設定する
			int clickX = (int)event.getX() / (getWidth()  / mMasuSizeX);
			int clickY = (int)event.getY() / (getHeight() / mMasuSizeY);
			mMasu[clickX][clickY].setValue(mValue);
			repaint(getHolder()); // 再描画を指示
	
			// 履歴すべて正解したかの判定と、そのときの升目が正解かどうかの判定処理
			if (mTekazuIma == mTekazuMax) { //新しい履歴を作成 （ＭＡＸよりひとつ多くクリックしたとき）
				mRirekiX[mTekazuIma] = clickX;
				mRirekiY[mTekazuIma] = clickY;
				mTekazuMax++;
				mTekazuIma = 0;
				mBan = 1 - mBan;
				Toast.makeText(getContext(), "交代", Toast.LENGTH_SHORT).show();
				mMasu[clickX][clickY].setValue( 0 );
				repaint(getHolder()); // 再描画を指示
			}
			else { // 履歴通りにクリックできたかを判定する
				// 履歴通りの正しい升目をクリックできた場合
				if (mRirekiX[mTekazuIma] == clickX && mRirekiY[mTekazuIma] == clickY) {
					mTekazuIma++;
				}
				// 正しい升目をクリックできなかった場合
				else {
					for (int x = 0; x < mMasuSizeX; ++x) { // 升目の表示をすべて×にする
						for(int y = 0; y < mMasuSizeY; ++y) {
							mMasu[x][y].setValue( 2 );
						}
					}
					repaint(getHolder()); // 再描画を指示
					mTekazuMax = mTekazuIma = 200; // 初手に戻す布石
				}
			}
	
		}

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