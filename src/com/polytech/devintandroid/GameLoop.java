package com.polytech.devintandroid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GameLoop extends Thread {

	private boolean			running;

	List<mPoint>			tousLesPoints	= new ArrayList<mPoint>();

	private long			sleepTime		= 30;

	private Context			context;
	private Paint			p;
	private Path			path;
	// Our screenresolution
	private int				swidth;
	private int				sheight;
	private SurfaceHolder	holder;

	public GameLoop(Context context, SurfaceHolder holder) {
		this.context = context;
		this.holder = holder;
		running = true;
		
		p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.FILL);
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		p.setStrokeWidth(1);
		p.setColor(Color.WHITE);
		path = new Path();
	}

	/** la boucle de jeu */
	public void run() {
		chargementDesPoints(this.tousLesPoints);
		while (this.running) {
			Log.d("running", "running");
			path = new Path();
			this.update();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas(null);
				synchronized (this.holder) {
					canvas.drawColor(0, Mode.CLEAR);
					Log.d("sync", "sync");
					affichageDesPoints(path, p, canvas);
					Log.d("adesPoints", "adesPoints");
					Bitmap car = BitmapFactory.decodeResource(context.getResources(),
							R.drawable.car);
					canvas.drawBitmap(car, (this.getSwidth() / 2) - 80,
							this.getSheight() - 300, null);
				}
			} finally {
				Log.d("fina", "fina");
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				System.out.println(e.getMessage()+"");
				Log.d("erreur: "+e.getMessage(),"erreur: "+e.getMessage());
			}
		}
	}

	/** Dessiner les composant du jeu sur le buffer de l'écran */
	/*public void render() {
		this.screen.canvas.drawPaint(p);
		screen.affichageDesPoints(this.path, this.p, this.screen.canvas);
		Bitmap car = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.car);
		this.screen.canvas.drawBitmap(car, (this.getSwidth() / 2) - 80,
				this.getSheight() - 300, null);
	}*/

	/**
	 * Mise à jour des composants du jeu Ici nous déplaçon le personnage avec la
	 * vitesse vx S'il sort de l'écran, on le fait changer de direction
	 * */
	public void update() {
		this.avancer(this.tousLesPoints);
	}

	public void chargementDesPoints(List<mPoint> tlp) {
		int[][] points = { { 0, 0 }, { this.getSwidth() / 3, 0 }, { 0, 400 },
				{ 0, 300 }, { this.getSwidth() / 4, 500 }, { 0, 1000 },
				{ 0, 500 }, { this.getSwidth() / 4, 1200 }, { 0, 1280 },
				{ this.getSwidth(), 0 },
				{ this.getSwidth() - (this.getSwidth() / 4), 0 },
				{ this.getSwidth(), 500 }, { this.getSwidth(), 0 },
				{ this.getSwidth() - (this.getSwidth() / 4), 900 },
				{ this.getSwidth(), 1280 } };
		for (int i = 0; i < points.length; i++) {
			tlp.add(new mPoint(points[i][0], points[i][1]));
		}

	}

	public void updateOrientation(int x) {
		for (mPoint p : this.tousLesPoints) {
			p.tourne(x);
		}
	}

	public void avancer(List<mPoint> points) {
		for (mPoint p : points) {
			p.monte();
		}
	}

	public void affichageDesPoints(Path path, Paint p, Canvas c) {
		for (int i = 0; i < tousLesPoints.size(); i += 3) {
			ajouterUnTriangle(tousLesPoints.get(i), tousLesPoints.get(i + 1),
					tousLesPoints.get(i + 2), path, p, c);
		}
	}

	public void ajouterUnTriangle(mPoint origin, mPoint line1, mPoint line2,
			Path ppath, Paint pp, Canvas ca) {
		ppath.moveTo(origin.getX(), origin.getY());
		ppath.lineTo(line1.getX(), line1.getY());
		ppath.lineTo(line2.getX(), line2.getY());
		ppath.close();
		ppath.offset(0, 0);
		ca.drawPath(ppath, pp);
	}

	public int getSwidth() {
		return swidth;
	}

	public void setSwidth(int swidth) {
		this.swidth = swidth;
	}

	public int getSheight() {
		return sheight;
	}

	public void setSheight(int sheight) {
		this.sheight = sheight;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
