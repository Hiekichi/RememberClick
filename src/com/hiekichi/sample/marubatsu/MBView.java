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

	Masu[][] masu; // ���ڂ�\�������N���X��2�����z��ŗp��
	final int masuSizeX = 3;  // ���ڂ����ɉ��z�u���邩
	final int masuSizeY = 3;  // ���ڂ��c�ɉ��z�u���邩

	int value = 1; // ���ɕ\�������遛�~�̒l�A1:��  2:�~
	
	private SurfaceHolder	holder;  // �T�[�t�F�C�X�z���_�[
	Paint paint = new Paint();  // �y�C���g�C���X�^���X
	
	public MBView(Context context) {
		super(context);

		// ���ڈ���̃T�C�Y��Masu�N���X�̃X�^�e�B�b�N�t�B�[���h�ɐݒ肷��
		Masu.width  = this.getWidth()  / masuSizeX;
		Masu.height = this.getHeight() / masuSizeY;

		// �T�[�t�F�C�X�z���_�[�̏���
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setFixedSize(this.getWidth(), this.getHeight());

		// �}�X�̏���
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
		// �r���[�쐬���ɍs�킹�鏈��
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder,
			                    int format, int w, int h) {
		// ��ʃT�C�Y�ω����ɍs�킹�鏈��
		// ���ڈ���̃T�C�Y���Đݒ�
		Masu.width  = this.getWidth()  / masuSizeX;
		Masu.height = this.getHeight() / masuSizeY;

		// �ĕ`����w��
		this.repaint(holder);
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// �r���[�I�����ɍs�킹�鏈��
	}

	// �ĕ`����w������Ƃ��ɖ����I�ɌĂяo�����\�b�h
	protected void repaint(SurfaceHolder holder) {
		// �L�����o�X�����b�N
		Canvas canvas = holder.lockCanvas();
		
		// �`��
		paint.setColor(Color.GREEN);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

		for (int x = 0; x < masuSizeX; ++x) {
			for(int y = 0; y < masuSizeY; ++y) {
				masu[x][y].draw(canvas);
			}
		}
		
		// �L�����o�X���A�����b�N
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// �^�b�`���ꂽ���ɍs�킹�鏈��

		// �^�b�`���ꂽ���ڂ��ǂꂩ���v�Z�ŋ��߂�
		int x = (int)event.getX() / (getWidth()  / masuSizeX);
		int y = (int)event.getY() / (getHeight() / masuSizeY);

		// ���ɕ`�悳����̂������~����ݒ肷��
		if (masu[x][y].getValue() == 0) {
			masu[x][y].setValue(value);
			if (value == 1) {
				value = 2;
			}
			else {
				value = 1;
			}
		}

		// �ĕ`����w��
		repaint(getHolder());
		return true;
	}
}

class Masu {
	public static int width, height; // ���ڈ���̑傫��
	int x, y; // 2�����z��̓Y���ɑ���
	int value = 0; // 0:��  1:��  2:�~
	private Paint paint;

	private Masu() { // �����Ȃ��̃R���X�g���N�^
		// �y�C���g�C���X�^���X�̐����Ɛݒ�
		paint = new Paint();
		paint.setStrokeWidth(5.0F);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
	}
	
	public Masu(int x, int y) { // �����t���̃R���X�g���N�^
		this();
		this.x = x;       			this.y = y;
	}
	
	public void draw(Canvas canvas) { 
		// �`��Ŏg�p����e��l�̌v�Z
		int x1 = width * x;    			int y1 = height * y;
		int x2 = width * x + width;    	int y2 = height * y + height;
		int xMargin = width / 10;		int yMargin = height / 10;
		int hankei = Math.min( width / 2 - xMargin, height / 2 - yMargin);

		// �O�g�̕`��
		paint.setColor(Color.BLACK);
		canvas.drawRect(x1, y1, x2, y2, paint);

		if (value == 1) { // ���̕`��
			paint.setColor(Color.BLUE);
			canvas.drawCircle((x1 + x2) / 2, (y1 + y2) / 2, hankei, paint);
		}
		else if (value == 2) { // �~�̕`��
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