package com.polytech.devintandroid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * 
 * @author Fabien Pinel and Tom Guillermin
 * 
 */
public class GameLoop extends Thread {

	private static final int			HAUTEUR			= 400;

	private boolean						running;
	private List<mPoint>				pointsGauche	= new LinkedList<mPoint>();
	private List<mPoint>				pointsDroite	= new LinkedList<mPoint>();
	private Context						context;
	private Paint						p, pscore;
	private Path						path;
	private int							swidth;
	private int							sheight;
	private SurfaceHolder				holder;
	private int							position, positionx;
	private int							speed;
	private long						lastUpdate;
	private Bitmap						myCar;
	private int							avancement;
	private long						delta;
	private int							car;
	private int							score, bestScore;
	private List<GameShape>				leftShapes, rightShapes;
	private Canvas						canvas;
	private SharedPreferences			settings;
	private SharedPreferences.Editor	editor;
	private int							orientationGap;
	private boolean						isInBoost;
	private int							generatedHeight;
	private int							firstElementY;
	private int							level;
	private int							explosionIdBip, explosionIdBip2;
	private SoundPool					soundPool;
	private boolean						loaded			= false;
	private int							nbCollision		= 0;
	private Vibrator					vibreur;

	public GameLoop(Context context, SurfaceHolder holder, int car, int level) {
		this.context = context;
		this.setHolder(holder);
		this.car = car;
		this.setLevel(level);
		settings = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
		editor = settings.edit();

		path = new Path();
		p = new Paint();
		pscore = new Paint();
		loadMyCar(this.car);
		loadPaint(p);
		loadScore();
		loadLevel();
		loadSong();

		leftShapes = new ArrayList<GameShape>();
		rightShapes = new ArrayList<GameShape>();

		vibreur = (Vibrator) this.context
				.getSystemService(this.context.VIBRATOR_SERVICE);

		this.running = true;
	}

	/**
	 * Chargement de la bonne voiture en fonction de la valeur de "car"
	 * 
	 * @param car
	 */
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

	/**
	 * Chargement du paramètre paint en fonction du thème choisit
	 * 
	 * @param p
	 */
	public void loadPaint(Paint p) {
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.FILL);
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		p.setTextSize((float) 60.0);
		p.setStrokeWidth(1);
		switch (settings.getInt("titreFond", 0)) {
		case OptionsActivity.THEME_BLEU:
			pscore.setColor(Color.BLUE);
			break;
		case OptionsActivity.THEME_ROUGE:
			pscore.setColor(Color.RED);
			break;
		default:
			pscore.setColor(Color.BLUE);
		}
		pscore.setTextSize((float) 60.0);
	}
	/**
	 * Chargement du niveau FACILE / NORMAL / DIFFICILE / HARDCORE
	 */
	public void loadLevel() {
		switch (this.getLevel()) {
		case OptionsActivity.FACILE:
			this.setSpeed(800);
			break;
		case OptionsActivity.NORMAL:
			this.setSpeed(1000);
			break;

		case OptionsActivity.DIFFICILE:
			this.setSpeed(1500);
			break;
		case OptionsActivity.HARDCORE:
			this.setSpeed(3000);
			break;
		default:
			this.setSpeed(1000);
		}
	}

	/**
	 * Chargement de la valeur du Meilleur Score enregistré
	 */
	public void loadScore() {
		this.bestScore = settings.getInt("bestScore", 0);
	}
	 /**
	  * Initialisation du système sonore
	  */
	public void loadSong() {
		/*
		 * Lecture de fichier son
		 */

		((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Chargement du fichier musique.mp3 qui se trouve sous assets de notre

		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		explosionIdBip = soundPool.load(this.context, R.drawable.bip, 1);
		explosionIdBip2 = soundPool.load(this.context, R.drawable.bip2, 1);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				loaded = true;

			}
		});
	}

	/** la boucle de jeu */
	public void run() {
		// Initialisation des premiers points
		// chargementDesPoints(this.pointsGauche, pointsG);
		// chargementDesPoints(this.pointsDroite, pointsD);
		this.position = 0;
		this.positionx = 0;
		this.score = 0;
		while (this.running) {
			path = new Path();
			canvas = null;
			try {
				synchronized (this.getHolder()) {
					canvas = getHolder().lockCanvas(null);
					// Clear
					if (canvas != null) {
						canvas.drawColor(0, Mode.CLEAR);

						if (this.score > this.bestScore) {
							editor.putInt("bestScore", this.score);
							editor.commit();
							loadScore();
						}
						// Log.d("toms", "==============================");

						// Clean unused shapes
						cleanShapes();

						// Check if generation is needed ?
						this.generatedHeight = 0;
						for (GameShape shape : leftShapes) {
							this.generatedHeight += shape.getHeight();
						}

						this.setFirstElementY(sheight);

						if (leftShapes.size() > 0 && leftShapes.get(0) != null) {
							this.setFirstElementY(leftShapes.get(0)
									.getOriginY());
						}

						int lastElementY = sheight;
						if (leftShapes.size() > 0) {
							GameShape last = leftShapes
									.get(leftShapes.size() - 1);
							lastElementY = last.getOriginY() - last.getHeight();
						}

						int missingPixels = lastElementY;
						int missingShapes = (int) Math.ceil(missingPixels * 1.0
								/ GameLoop.HAUTEUR);

						if (missingShapes > 0) {
							generateNewShapes(missingShapes);
						}
						this.update();
						if ((this.position) >= (GameLoop.HAUTEUR)) {
							this.position -= GameLoop.HAUTEUR;
						}
						this.updateOrientation(this.getOrientationGap());
						if(this.getOrientationGap()>0){
							this.setOrientationGap(this.getOrientationGap()-1);
						}else{
							this.setOrientationGap(this.getOrientationGap()+1);
						}
						

						// affichageDesPoints(path, p, canvas);
						displayShapes(path, p, canvas);

						// Textes , scores et points de vie
						canvas.drawText("Meilleur: " + bestScore, 0, 50, pscore);
						canvas.drawText("Score: " + score, 0, 100, pscore);
						canvas.drawText("Points de vie restants: "
								+ (100 - this.getNbCollision()), 0, 150, pscore);
						canvas.drawBitmap(myCar, getCarX(), getCarY(), null);

						// Gestion des collisions
						this.singTheDistance(getWallDistances(getCarY()));

						/*
						 * canvas.drawText("Dist:" + dist[0] + "," + dist[1], 0,
						 * 250, pscore); canvas.drawCircle(getCarX(), getCarY(),
						 * 3, pscore);
						 */

						/*
						 * Log.d("toms","y = "+getCarY());
						 * Log.d("toms","oY = "+leftShapes
						 * .get(0).getOriginY()+", h="
						 * +leftShapes.get(0).getHeight
						 * ()+"/"+leftShapes.get(0).getPoints2());
						 * Log.d("toms","oY2 = "
						 * +leftShapes.get(1).getOriginY()+", h2="
						 * +leftShapes.get
						 * (1).getHeight()+"/"+leftShapes.get(1).getPoints2());
						 */
						GameShape testshape = getShapeForY(getCarY(),
								leftShapes);
						// Log.d("toms",
						// "shape("+getCarY()+"): oY="+testshape.getOriginY()+", h="+testshape.getHeight()+":"+testshape);
						// Log.d("toms",
						// "shape("+getCarY()+")="+getShapeForY(getCarY(),
						// leftShapes));

						if (level != OptionsActivity.FACILE) {
							this.speed += 1;
						}
						// For debugging
						/*
						 * try { Thread.sleep(500); } catch
						 * (InterruptedException e) { e.printStackTrace(); }
						 */
					} else {
						Log.d("canvas null", "canvass null");
					}
				}
			} finally {
				if (canvas != null) {
					getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	public boolean singTheDistance(int[] dist) {
		if (dist[0] <= 0 || dist[1] <= 0) {
			// jouer pleine balle !!
			this.playSound(this.explosionIdBip, 0.5, 0.5);
			this.vibreur.vibrate(100);
			this.setNbCollision(this.getNbCollision() + 1);
			this.score -= 50;
			if (this.getNbCollision() >= 100) {
				this.running = false;
				Intent go = new Intent(this.context, GameOverActivity.class);
				this.context.startActivity(go);
			}
			return true;
		}
		// cote gauche
		else if (dist[0] <= 300) {
			// jouer moyen
			this.playSound(this.explosionIdBip2, 0.7, 0.3);
			return true;
		}// cote droit
		else if (dist[1] <= 300) {
			// jouer moyen
			this.playSound(this.explosionIdBip2, 0.3, 0.7);
			return true;
		}

		// cote gauche
		else if (dist[0] <= 150) {
			// jouer moyen
			this.playSound(this.explosionIdBip2, 0.8, 0.2);
			return true;
		}// cote droit
		else if (dist[1] <= 150) {
			// jouer moyen
			this.playSound(this.explosionIdBip2, 0.2, 0.8);
			return true;
		}

		// côté gauche
		else if (dist[0] <= 50) {
			this.playSound(this.explosionIdBip2, 1.0, 0.0);
			return true;
		}// côté droit
		else if (dist[1] <= 50) {
			this.playSound(this.explosionIdBip2, 0.0, 1.0);
			return true;
		}

		else {
			return true;
		}
	}

	public int getCarX() {
		return (this.getSwidth() / 2) - 80;
	}

	/**
	 * Retourne la position en Y de la voiture
	 * 
	 * @return
	 */
	public int getCarY() {
		return this.getSheight() - myCar.getHeight() - 20;
	}

	public int getCarWidth() {
		return myCar.getWidth();
	}

	/**
	 * Nettoyage des formes (du décor)
	 */
	public void cleanShapes() {
		cleanShapes(leftShapes);
		cleanShapes(rightShapes);
	}

	/**
	 * Removes the shapes that are now invisible
	 */
	private void cleanShapes(List<GameShape> shapesList) {
		if (shapesList.size() > 0) {
			GameShape shape = null;
			for (int i = 0; i < shapesList.size(); ++i) {
				shape = shapesList.get(i);
				int shapeTop = shape.getOriginY() - shape.getHeight();
				if (shapeTop > sheight) {
					shapesList.remove(i);
					i--;
				} else {
					break;
				}
			}
		}
	}

	private void generateNewShapes(int count) {
		for (int i = 0; i < count; ++i) {
			// Log.d("debug", "=== left Shapes");
			generateNewShape(leftShapes, true);
			// Log.d("debug", "=== right Shapes");
			generateNewShape(rightShapes, false);
		}
	}

	private void generateNewShape(List<GameShape> shapeList, boolean isLeft) {
		int previousWidth = 100;
		int originX = 0, originY = sheight;

		if (!isLeft) {
			originX = swidth;
		}

		if (shapeList.size() > 0) {
			GameShape previousShape = shapeList.get(shapeList.size() - 1);
			if (previousShape != null) {
				previousWidth = previousShape.getWidth();
				originY = previousShape.getOriginY()
						- previousShape.getHeight();
			}
		}

		int newWidth = Math.min(400,
				previousWidth + (int) ((0.5 - Math.random()) * 600));
		if (newWidth < 10)
			newWidth = 10;
		GameShape s = new GameShape(newWidth, previousWidth, originX, originY,
				GameLoop.HAUTEUR, isLeft);
		// Log.d("debug", "Generated Shape:" + s);
		shapeList.add(s);
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
		for (int i = 0; i < points.size(); ++i) {// mPoint p : points) {
			mPoint p = points.get(i);
			if (p.getY() > sheight) {
				points.remove(i);
				--i;
			}
		}

		/*
		 * Log.d("apres clean " + pointsGauche.size(), "apres clean " +
		 * pointsGauche.size());
		 */
	}

	private int[] getWallDistances(int y) {
		int result[] = new int[2];
		List<GameShape> shapes = getShapesForY(y);

		// Left side
		result[0] = getCarX() - shapes.get(0).getXForY(y);

		// Right side
		result[1] = shapes.get(1).getXForY(y) - (getCarX() + getCarWidth());

		return result;
	}

	private List<GameShape> getShapesForY(int y) {
		List<GameShape> result = new LinkedList<GameShape>();
		result.add(getShapeForY(y, leftShapes));
		result.add(getShapeForY(y, rightShapes));
		return result;
	}

	private GameShape getShapeForY(int y, List<GameShape> list) {
		for (GameShape p : list) {
			// if (p.getOriginY() <= y && y <= (p.getOriginY() + p.getHeight()))
			// {
			if (p.getOriginY() - p.getHeight() <= y && y <= p.getOriginY()) {
				return p;
			}
		}
		return null;
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
		this.score += getAvancement();
		this.lastUpdate = System.nanoTime();

		/*
		 * Décommenter la ligne en dessous pour augmenter la difficultée
		 * (changement de direction a chaque update)
		 */
		/*
		 * int orientationRandom = (int)Math.round((Math.random()*30)-15);
		 * this.updateOrientation(orientationRandom);
		 */

		// Update des nouveaux points
		int deltaY = getAvancement();
		updateShapes(leftShapes, deltaY);
		updateShapes(rightShapes, deltaY);
	}

	private void updateShapes(List<GameShape> shapes, int deltaY) {
		for (GameShape s : shapes) {
			s.translate(0, deltaY);
		}
	}

	/**
	 * Chargement des poinbts contenus dans le tableau passé en paramètre dans
	 * la liste de tous les points
	 * 
	 * @param tlp
	 * @param points
	 */
	public void chargementDesPoints(List<mPoint> tlp, int[][] points) {
		for (int i = 0; i < points.length; i++) {
			tlp.add(new mPoint(points[i][0], points[i][1]));
		}
	}

	/**
	 * Mise à jour de l'orientation en fonction du capteur du téléphone
	 * 
	 * @param x
	 *            : valeur de l'orientation
	 */
	public void updateOrientation(int x) {
		updateOrientation(leftShapes, x);
		updateOrientation(rightShapes, x);
	}

	/**
	 * Mise à jour de l'orientation en fonction du capteur du téléphone
	 * 
	 * @param shapesList
	 *            liste des triangles à mettre à jour
	 * @param dX
	 *            facteur de l'orientation
	 */
	private void updateOrientation(List<GameShape> shapesList, int dX) {
		for (GameShape s : shapesList) {
			s.translate(dX, 0);
		}
		positionx += dX;
	}

	/**
	 * Avancer tous les points du facteur footo
	 * 
	 * @param points
	 *            : Liste de points à mettre à jour
	 * @param footo
	 *            : facteur d'avancement
	 */
	public void avancer(List<mPoint> points, int footo) {
		for (mPoint p : points) {
			p.monte(footo);
		}
	}

	/**
	 * Affichage des points sur le canvas
	 * 
	 * @param path
	 * @param p
	 *            options de l'affichage
	 * @param c
	 *            canvas sur lequel afficher
	 */
	public void affichageDesPoints(Path path, Paint p, Canvas c) {
		// Log.d("affichage", "affichage");
		for (int i = 0; i < pointsGauche.size(); i += 3) {
			ajouterUnTriangle(pointsGauche.get(i), pointsGauche.get(i + 1),
					pointsGauche.get(i + 2), path, p, c);
		}
		for (int i = 0; i < pointsDroite.size(); i += 3) {
			ajouterUnTriangle(pointsDroite.get(i), pointsDroite.get(i + 1),
					pointsDroite.get(i + 2), path, p, c);
		}
	}

	/**
	 * Ecriture du triangle sur le canvas
	 * 
	 * @param origin
	 *            : point 1
	 * @param line1
	 *            point 2
	 * @param line2
	 *            point 3
	 * @param ppath
	 * @param pp
	 *            options paint
	 * @param ca
	 *            canvas
	 */
	public void ajouterUnTriangle(mPoint origin, mPoint line1, mPoint line2,
			Path ppath, Paint pp, Canvas ca) {
		ppath.moveTo(origin.getX(), origin.getY());
		ppath.lineTo(line1.getX(), line1.getY());
		ppath.lineTo(line1.getX(), line1.getY() + 50);
		ppath.lineTo(line2.getX(), line2.getY());
		ppath.close();
		ppath.offset(0, 0);
		ca.drawPath(ppath, pp);
	}

	public void ajouterUnTriangle(Triangle t, Path path, Paint paint,
			Canvas canvas) {
		List<mPoint> points = t.getPoints();
		ajouterUnTriangle(points.get(0), points.get(1), points.get(2), path,
				paint, canvas);
	}

	/**
	 * Affichage des formes (du décor)
	 * 
	 * @param path
	 * @param p2
	 * @param canvas
	 */
	private void displayShapes(Path path, Paint p2, Canvas canvas) {
		// Log.d("affichage", "affichage");
		displayShapes(leftShapes, path, p2, canvas, true);
		displayShapes(rightShapes, path, p2, canvas, false);
	}

	private void displayShapes(List<GameShape> shapesList, Path path,
			Paint paint, Canvas canvas, boolean left) {
		for (GameShape s : shapesList) {
			// TODO : remove the red painting!
			if (getShapesForY(getCarY()).contains(s)) {
				paint.setColor(Color.RED);
			} else {
				paint.setColor(Color.WHITE);
			}
			drawGameShape(s, path, paint, canvas, left);
		}
	}

	public void drawGameShape(GameShape t, Path path, Paint paint,
			Canvas canvas, boolean left) {
		// path = new Path();
		mPoint p1 = t.getPoints2().get(0), p2 = t.getPoints2().get(1);

		// Dessin du point aligné horizontalement avec p1
		path.moveTo(p1.getX(), p1.getY());
		drawCorrespondingPoint(path, p1, left);
		drawCorrespondingPoint(path, p2, left);
		path.lineTo(p2.getX(), p2.getY());

		/*
		 * if (left) { if (p1.getX() < 0) { // Le point 1 est passé à gauche de
		 * l'écran, du coup, on dessine le point homologue au même endroit
		 * path.moveTo(p1.getX(), p1.getY()); } else { // Sinon, on dessine
		 * normalement en partant du coté gauche de l'écran path.moveTo(0,
		 * p1.getY()); } } else { if (p1.getX() > swidth) { // Le point 1 est
		 * passé à droite de l'écran, du coup, on dessine le point homologue au
		 * même endroit path.moveTo(p1.getX(), p1.getY()); } else { // Sinon, on
		 * dessine normalement en partant du coté droit de l'écran
		 * path.moveTo(swidth, p1.getY()); } }
		 */

		/*
		 * for (mPoint p : t.getPoints()) { int x = p.getX(), y = p.getY(); if
		 * (left) { if (x > 0) {
		 * 
		 * } } path.lineTo(p.getX(), p.getY()); }
		 */
		path.close();
		path.offset(0, 0);
		canvas.drawPath(path, paint);
		path.rewind();
	}

	public void drawCorrespondingPoint(Path path, mPoint p1, boolean left) {
		if (p1.getX() < 0) {
			// Le point 1 est passé à gauche de l'écran, du coup, on dessine le
			// point homologue au même endroit
			path.lineTo(p1.getX(), p1.getY());
		} else {
			// Sinon, on dessine normalement en partant du coté gauche de
			// l'écran
			path.lineTo(left ? 0 : swidth, p1.getY());
		}
	}

	/**
	 * Génération de la prochaine forme à gauche
	 * 
	 * @param p
	 */
	public void genererNouveauTriangleGauche(mPoint p) {
		this.pointsGauche
				.add(new mPoint(p.getX(), p.getY() - GameLoop.HAUTEUR));
		this.pointsGauche.add(new mPoint(p.getX() + this.getSwidth() / 4, p
				.getY() - 200));
		this.pointsGauche
				.add(new mPoint(p.getX(), p.getY() + GameLoop.HAUTEUR));
	}

	/**
	 * Génération de la prochaine forme à droite
	 * 
	 * @param p
	 */
	public void genererNouveauTriangleDroite(mPoint p) {
		this.pointsDroite
				.add(new mPoint(p.getX(), p.getY() - GameLoop.HAUTEUR));
		this.pointsDroite.add(new mPoint(p.getX() - this.getSwidth() / 4, p
				.getY() - 200));
		this.pointsDroite
				.add(new mPoint(p.getX(), p.getY() + GameLoop.HAUTEUR));
	}

	private void playSound(int resId, double left, double right) {
		if (loaded) {
			soundPool.play(resId, (float) left, (float) right, 0, 0, 2);
		}
	}

	/**
	 * Getters et Setters
	 * 
	 * @return
	 */
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

	public SurfaceHolder getHolder() {
		return holder;
	}

	public void setHolder(SurfaceHolder holder) {
		this.holder = holder;
	}

	public int getOrientationGap() {
		return orientationGap;
	}

	public void addOrientationGap(int orientationGap) {
		this.orientationGap += orientationGap;
	}

	public void setOrientationGap(int orientationGap) {
		this.orientationGap = orientationGap;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isInBoost() {
		return isInBoost;
	}

	public void setInBoost(boolean isInBoost) {
		this.isInBoost = isInBoost;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getGeneratedHeight() {
		return generatedHeight;
	}

	public void setGeneratedHeight(int generatedHeight) {
		this.generatedHeight = generatedHeight;
	}

	public int getFirstElementY() {
		return firstElementY;
	}

	public void setFirstElementY(int firstElementY) {
		this.firstElementY = firstElementY;
	}

	public int getNbCollision() {
		return nbCollision;
	}

	public void setNbCollision(int nbCollision) {
		this.nbCollision = nbCollision;
	}

}
