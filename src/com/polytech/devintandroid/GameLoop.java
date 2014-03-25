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

	private static final int	FOOT			= 20;
	private static final int	HAUTEUR			= 400;
	private static final int	MAX_SIZE_LIST	= 42;

	private boolean				running;
	List<mPoint>				pointsGauche	= new ArrayList<mPoint>();
	List<mPoint>				pointsDroite	= new ArrayList<mPoint>();

	private long				sleepTime		= 3;

	private Context				context;
	private Paint				p;
	private Path				path;
	// Our screenresolution
	private int					swidth;
	private int					sheight;
	private SurfaceHolder		holder;
	private int					position;
	private boolean				switcher		= false;
	private boolean				switcher2		= false;

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
		int[][] pointsG = { { 0, 500 }, { this.getSwidth() / 4, 1200 },
				{ 0, 1280 }, { 0, 300 }, { this.getSwidth() / 4, 500 },
				{ 0, 1000 }, { 0, 0 }, { this.getSwidth() / 3, 0 }, { 0, 400 } };
		int[][] pointsD = { { this.getSwidth(), 0 },
				{ this.getSwidth() - (this.getSwidth() / 4), 900 },
				{ this.getSwidth(), 1280 }, { this.getSwidth(), 0 },
				{ this.getSwidth() - (this.getSwidth() / 4), 0 },
				{ this.getSwidth(), 500 }

		};
		chargementDesPoints(this.pointsGauche, pointsG);
		chargementDesPoints(this.pointsDroite, pointsD);
		this.position = 0;
		while (this.running) {
			Log.d("running", "running");
			path = new Path();
			this.update();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas(null);
				synchronized (this.holder) {

					// Clear
					canvas.drawColor(0, Mode.CLEAR);

					

					// Generation

					if ((this.position) >= (this.HAUTEUR / 2)) {
						genererNouveauTriangleGauche(this.pointsGauche
								.get(pointsGauche.size() - 3));
						genererNouveauTriangleDroite(pointsDroite
								.get(pointsDroite.size() - 3));
						Log.d("size sup " + pointsGauche.size(),
								"size sup " + pointsGauche.size());
						
						if(switcher){
							if(switcher2){
							cleanLast(this.pointsGauche);
							cleanLast(this.pointsDroite);
							}else{
								switcher2=true;
							}
							switcher = false;
						}else{switcher=true;}
						
						position = 0;
					}
					// Triangles
					affichageDesPoints(path, p, canvas);
					// Voiture
					Bitmap car = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.car);
					canvas.drawBitmap(car, (this.getSwidth() / 2) - 80,
							this.getSheight() - 310, null);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
			try {
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				Log.d("Erreur sleep: " + e.getMessage(),
						"Erreur sleep: " + e.getMessage());
			}
		}
	}

	/** Dessiner les composant du jeu sur le buffer de l'écran */
	/*
	 * public void render() { this.screen.canvas.drawPaint(p);
	 * screen.affichageDesPoints(this.path, this.p, this.screen.canvas); Bitmap
	 * car = BitmapFactory.decodeResource(context.getResources(),
	 * R.drawable.car); this.screen.canvas.drawBitmap(car, (this.getSwidth() /
	 * 2) - 80, this.getSheight() - 300, null); }
	 */

	private void cleanLast(List<mPoint> points) {
		points.remove(0);
		points.remove(0);
		points.remove(0);
		points.remove(0);
		points.remove(0);
		points.remove(0);

		
		Log.d("apres clean " + pointsGauche.size(), "apres clean "
				+ pointsGauche.size());
	}

	/**
	 * Mise à jour des composants du jeu Ici nous déplaçon le personnage avec la
	 * vitesse vx S'il sort de l'écran, on le fait changer de direction
	 * */
	public void update() {
		this.avancer(this.pointsGauche, GameLoop.FOOT);
		this.avancer(this.pointsDroite, GameLoop.FOOT);
		this.position += this.FOOT;
	}

	public void chargementDesPoints(List<mPoint> tlp, int[][] points) {

		for (int i = 0; i < points.length; i++) {
			tlp.add(new mPoint(points[i][0], points[i][1]));
		}

	}

	public void updateOrientation(int x) {
		for (mPoint p : this.pointsGauche) {
			p.tourne(x);
		}
		for (mPoint p : this.pointsDroite) {
			p.tourne(x);
		}
	}

	public void avancer(List<mPoint> points, int foot) {
		for (mPoint p : points) {
			p.monte(foot);
		}
	}

	public void affichageDesPoints(Path path, Paint p, Canvas c) {
		for (int i = 0; i < pointsGauche.size(); i += 3) {
			ajouterUnTriangle(pointsGauche.get(i), pointsGauche.get(i + 1),
					pointsGauche.get(i + 2), path, p, c);
		}
		for (int i = 0; i < pointsDroite.size(); i += 3) {
			ajouterUnTriangle(pointsDroite.get(i), pointsDroite.get(i + 1),
					pointsDroite.get(i + 2), path, p, c);
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

	public void genererNouveauTriangleGauche(mPoint p) {
		this.pointsGauche.add(new mPoint(p.getX(), p.getY() - this.HAUTEUR));
		this.pointsGauche.add(new mPoint(p.getX() + this.getSwidth() / 4, p
				.getY() - 200));
		this.pointsGauche.add(new mPoint(p.getX(), p.getY() + this.HAUTEUR));
	}

	public void genererNouveauTriangleDroite(mPoint p) {
		this.pointsDroite.add(new mPoint(p.getX(), p.getY() - this.HAUTEUR));
		this.pointsDroite.add(new mPoint(p.getX() - this.getSwidth() / 4, p
				.getY() - 200));
		this.pointsDroite.add(new mPoint(p.getX(), p.getY() + this.HAUTEUR));
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
