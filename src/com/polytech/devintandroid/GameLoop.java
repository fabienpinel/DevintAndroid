package com.polytech.devintandroid;

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
import android.view.SurfaceHolder;
import android.widget.Toast;

public class GameLoop extends Thread {
	/*
	 * TO CHANGE : collision , couleur teexte dans options / présentation choix
	 * voiture /circuit plus changeant / afficher le score en live
	 */

	private static final int	HAUTEUR			= 400;
	private static final int	MAX_SIZE_LIST	= 15;

	private boolean				running;
	private List<mPoint>		pointsGauche	= new LinkedList<mPoint>();
	private List<mPoint>		pointsDroite	= new LinkedList<mPoint>();
	private Context				context;
	private Paint				p;
	private Path				path;
	private int					swidth;
	private int					sheight;
	private SurfaceHolder		holder;
	private int					position, positionx;
	private int					speed			= 710;
	private long				lastUpdate;
	private Bitmap				myCar;
	private int					avancement;
	private long				delta;
	private int					car;
	private int					score;

	public GameLoop(Context context, SurfaceHolder holder, int car) {
		this.context = context;
		this.holder = holder;
		this.car = car;
		path = new Path();
		p = new Paint();
		loadMyCar(this.car);
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

	public void loadPaint(Paint p) {
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
		int[][] pointsD = { { this.getSwidth(), 300 },
				{ this.getSwidth() - (this.getSwidth() / 4), 900 },
				{ this.getSwidth(), 1280 }, { this.getSwidth(), 0 },
				{ this.getSwidth() - (this.getSwidth() / 4), 0 },
				{ this.getSwidth(), 500 }, { this.getSwidth(), 0 },
				{ this.getSwidth() - (this.getSwidth() / 4), 0 },
				{ this.getSwidth(), 500 } };

		// Initialisation des premiers points
		chargementDesPoints(this.pointsGauche, pointsG);
		chargementDesPoints(this.pointsDroite, pointsD);
		this.position = 0;
		this.positionx = 0;
		this.score=0;
		while (this.running) {
			Log.d("running", "running");
			path = new Path();
			this.update();
			Canvas canvas = null;
			try {
				synchronized (this.holder) {
					canvas = holder.lockCanvas(null);
					// Clear
					if (canvas != null) {
						canvas.drawColor(0, Mode.CLEAR);
						if (this.positionx >= 200 || this.positionx <= -200) {
							Log.d("collision", "collision");
							this.score-=200;
						}

						// Generation
						if ((this.position) >= (GameLoop.HAUTEUR)) {
							genererNouveauTriangleGauche(this.pointsGauche
									.get(pointsGauche.size() - 3));
							genererNouveauTriangleDroite(pointsDroite
									.get(pointsDroite.size() - 3));
							Log.d("size sup " + pointsGauche.size(),
									"size sup " + pointsGauche.size());
							if (this.pointsDroite.size() >= GameLoop.MAX_SIZE_LIST
									|| this.pointsGauche.size() >= GameLoop.MAX_SIZE_LIST) {
								cleanLast(this.pointsGauche);
								cleanLast(this.pointsDroite);
							}
							this.position -= GameLoop.HAUTEUR;

						}
						// Triangles
						affichageDesPoints(path, p, canvas);
						// Voiture
						canvas.drawBitmap(myCar, (this.getSwidth() / 2) - 80,
								this.getSheight() - 310, null);
					} else {
						Log.d("canvas null", "canvass null");
					}
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
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

	public int calculAvancement(int sspeed) {
		delta = System.nanoTime() - lastUpdate;
		return (int) Math.round((((delta * 1.0) / (Math.pow(10, 9))) * sspeed));
	}

	/**
	 * Mise à jour des composants du jeu Ici nous déplaçon le personnage avec la
	 * vitesse vx S'il sort de l'écran, on le fait changer de direction
	 * */
	public void update() {
		this.setAvancement(Math.min(calculAvancement(speed), 100));
		/*
		 * Log.d("avancement " + this.getAvancement(), "avancement " +
		 * this.getAvancement());
		 */
		this.position += this.getAvancement();
		this.avancer(this.pointsGauche, this.getAvancement());
		this.avancer(this.pointsDroite, this.getAvancement());
		this.score+=getAvancement();
		this.lastUpdate = System.nanoTime();
		// Log.d("position " + position, "position " + position);

	}

	public void chargementDesPoints(List<mPoint> tlp, int[][] points) {

		for (int i = 0; i < points.length; i++) {
			tlp.add(new mPoint(points[i][0], points[i][1]));
		}

	}

	public void updateOrientation(int x) {

		for (int i = 0; i < this.pointsGauche.size(); i++) {
			this.pointsGauche.get(i).tourne(x);
			this.pointsDroite.get(i).tourne(x);
			this.positionx += x;

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
		this.pointsGauche
				.add(new mPoint(p.getX(), p.getY() - GameLoop.HAUTEUR));
		this.pointsGauche.add(new mPoint(p.getX() + this.getSwidth() / 4, p
				.getY() - 200));
		this.pointsGauche
				.add(new mPoint(p.getX(), p.getY() + GameLoop.HAUTEUR));
	}

	public void genererNouveauTriangleDroite(mPoint p) {
		this.pointsDroite
				.add(new mPoint(p.getX(), p.getY() - GameLoop.HAUTEUR));
		this.pointsDroite.add(new mPoint(p.getX() - this.getSwidth() / 4, p
				.getY() - 200));
		this.pointsDroite
				.add(new mPoint(p.getX(), p.getY() + GameLoop.HAUTEUR));
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

	public int getAvancement() {
		return avancement;
	}

	public void setAvancement(int avancement) {
		this.avancement = avancement;
	}

}
