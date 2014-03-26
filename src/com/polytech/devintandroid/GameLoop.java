package com.polytech.devintandroid;

import java.util.ArrayList;
import java.util.LinkedList;
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
	private static final int	MAX_SIZE_LIST	= 22;

	private boolean				running;
	List<mPoint>				pointsGauche	= new LinkedList<mPoint>();
	List<mPoint>				pointsDroite	= new LinkedList<mPoint>();

	private long				sleepTime		= 3;

	private Context				context;
	private Paint				p;
	private Path				path;
	// Our screenresolution
	private int					swidth;
	private int					sheight;
	private SurfaceHolder		holder;
	private int					position;
	private int					speed			= 710;
	private long				lastUpdate;
	private int					car				= 0;
	private Bitmap				myCar;

	public GameLoop(Context context, SurfaceHolder holder, int car) {
		this.context = context;
		this.holder = holder;
		this.car = car;
		path = new Path();
		p = new Paint();
		loadMyCar(car);
		loadPaint(p);
		this.running = true;
		
	}

	public void loadMyCar(int car) {
		switch (car) {
		case OptionsActivity.RED_CAR:
			myCar = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.redcar);
			break;
		case OptionsActivity.POLICE_CAR:
			myCar = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.police);
			break;

		case OptionsActivity.BLUE_CAR:
			myCar = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.bleu);
			break;
		case OptionsActivity.GREEN_CAR:
			myCar = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.vert);
			break;
		}
	}
	public void loadPaint(Paint p){
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.FILL);
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		p.setStrokeWidth(1);
		p.setColor(Color.WHITE);
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
				{ this.getSwidth(), 500 }, };
		//Initialisation des premiers points
		chargementDesPoints(this.pointsGauche, pointsG);
		chargementDesPoints(this.pointsDroite, pointsD);
		
		this.position = 0;
		while (this.running) {
			Log.d("running", "running");
			path = new Path();
			// this.ancienUpdate();
			this.update();
			Canvas canvas = null;
			try {
				synchronized (this.holder) {
					canvas = holder.lockCanvas(null);
					// Clear
					if(canvas!=null){
						canvas.drawColor(0, Mode.CLEAR);
					
					// Generation
					if ((this.position) >= (this.HAUTEUR)) {
						genererNouveauTriangleGauche(this.pointsGauche
								.get(pointsGauche.size() - 3));
						genererNouveauTriangleDroite(pointsDroite
								.get(pointsDroite.size() - 3));
						Log.d("size sup " + pointsGauche.size(), "size sup "
								+ pointsGauche.size());

						if (this.pointsDroite.size() >= this.MAX_SIZE_LIST
								|| this.pointsGauche.size() >= this.MAX_SIZE_LIST) {
							//cleanLast(this.pointsGauche);
							//cleanLast(this.pointsDroite);
						}
						position = 0;
					}
					// Triangles
					affichageDesPoints(path, p, canvas);
					// Voiture

					canvas.drawBitmap(myCar, (this.getSwidth() / 2) - 80,
							this.getSheight() - 310, null);
					}else{
						Log.d("canvas null", "canvass null");
					}
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

		Log.d("apres clean " + pointsGauche.size(), "apres clean "
				+ pointsGauche.size());
	}

	/**
	 * Mise à jour des composants du jeu Ici nous déplaçon le personnage avec la
	 * vitesse vx S'il sort de l'écran, on le fait changer de direction
	 * */
	public void update() {
		long delta = System.nanoTime() - lastUpdate;
		int avancement = (int) (((delta * 1.0) / (Math.pow(10, 9))) * speed);
		Log.d("avancement " + avancement, "avancement " + avancement);
		

		this.avancer(this.pointsGauche, avancement);
		this.avancer(this.pointsDroite, avancement);
		this.position += avancement;
		Log.d("position "+position, "position "+position);
		this.lastUpdate = System.nanoTime();
	}

	public void ancienUpdate() {
		this.avancer(this.pointsGauche, this.FOOT);
		this.avancer(this.pointsDroite, this.FOOT);
		position += this.FOOT;
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

	public void avancer(List<mPoint> points, int footo) {
		for (mPoint p : points) {
			p.monte(footo);
		}
	}

	public void affichageDesPoints(Path path, Paint p, Canvas c) {
		Log.d("affichage", "affichage");
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
