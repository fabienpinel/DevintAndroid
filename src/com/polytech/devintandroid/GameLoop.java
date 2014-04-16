package com.polytech.devintandroid;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * 
 * @author Fabien Pinel
 * 
 */
public class GameLoop extends Thread {
	/*
	 * TO CHANGE : collision // sons lors du jeu
	 */

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
	private int level;



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

		leftShapes = new ArrayList<GameShape>();
		rightShapes = new ArrayList<GameShape>();

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
			this.setSpeed(2500);
			break;
		}
	}
	/**
	 * Chargement de la valeur du Meilleur Score enregistré
	 */
	public void loadScore() {
		this.bestScore = settings.getInt("bestScore", 0);
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
			Log.d("running", "running");
			path = new Path();
			canvas = null;
			try {
				synchronized (this.getHolder()) {
					canvas = getHolder().lockCanvas(null);
					// Clear
					if (canvas != null) {
						canvas.drawColor(0, Mode.CLEAR);
						// A REVOIR
						if (this.positionx >= (this.getSwidth() / 3)
								|| this.positionx <= -(this.getSwidth() / 3)) {
							Log.d("collision", "collision: " + this.positionx);
							if (this.positionx > 0) {
								this.score -= (int) Math.round(positionx / 150);
							} else {
								this.score += (int) Math.round(positionx / 150);
							}
							if (this.score > this.bestScore) {
								editor.putInt("bestScore", this.score);
								editor.commit();
								loadScore();
							}
						}

						Log.d("debug", "==============================");
						// Log.d("debug", "position : " + position);

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
						// Log.d("debug", "firstElementY : " + firstElementY);

						int lastElementY = sheight;
						if (leftShapes.size() > 0) {
							GameShape last = leftShapes
									.get(leftShapes.size() - 1);
							lastElementY = last.getOriginY() - last.getHeight();
						}
						/*
						 * Log.d("debug", "lastElementY (missing pixels): " +
						 * lastElementY);
						 */

						// int heightDelta = sheight - generatedHeight,
						int missingPixels = lastElementY;
						/*
						 * int heightDelta = lastEl- generatedHeight,
						 * missingShapes = (int) Math.ceil(heightDelta*1.0 /
						 * GameLoop.HAUTEUR);
						 */
						int missingShapes = (int) Math.ceil(missingPixels * 1.0
								/ GameLoop.HAUTEUR);

						// Log.d("debug", "leftShapes:" + leftShapes.size());
						// Log.d("debug", "generatedHeight:"+ generatedHeight);
						// Log.d("debug", "heightDelta:"+ heightDelta +
						// " (sHeight:"+sheight+")");
						// Log.d("debug", "Missing shapes : " + missingShapes);

						if (missingShapes > 0) {
							generateNewShapes(missingShapes);
						}
						this.update();
						if ((this.position) >= (GameLoop.HAUTEUR)) {
							this.position -= GameLoop.HAUTEUR;
						}
						this.updateOrientation(this.getOrientationGap());
						this.setOrientationGap(0);
						// Triangles
						// affichageDesPoints(path, p, canvas);
						displayShapes(path, p, canvas);
						// Voiture
						canvas.drawText("Meilleur: " + bestScore, 0, 70, pscore);
						canvas.drawText("Score: " + score, 0, 150, pscore);
						canvas.drawBitmap(myCar, (this.getSwidth() / 2) - 80,
								this.getSheight() - 310, null);
						this.speed += 1;
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

	/**
	 * Affichage des formes (du décor)
	 * 
	 * @param path
	 * @param p2
	 * @param canvas
	 */
	private void displayShapes(Path path, Paint p2, Canvas canvas) {
		// Log.d("affichage", "affichage");
		displayShapes(leftShapes, path, p2, canvas);
		displayShapes(rightShapes, path, p2, canvas);
	}

	private void displayShapes(List<GameShape> shapesList, Path path2,
			Paint p2, Canvas canvas) {
		for (GameShape s : shapesList) {
			List<Triangle> tris = s.getTriangles();
			for (Triangle t : tris) {
				ajouterUnTriangle(t, path, p, canvas);
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

		// Décommenter la ligne en dessous pour augmenter la difficultée
		// (changement de direction a chaque update)
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

}
