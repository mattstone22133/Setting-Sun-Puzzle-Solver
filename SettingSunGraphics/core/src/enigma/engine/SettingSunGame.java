package enigma.engine;

import java.util.ArrayList;
import java.util.Stack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class SettingSunGame extends ApplicationAdapter implements InputProcessor {
	// Graphics
	private OrthographicCamera camera;
	// private ExtendViewport vp;
	private Vector3 conversionVector = new Vector3();
	private SpriteBatch batch;
	private Texture baseTexture;
	private Texture brownBackgroundTexture;
	private TextureRegion sunRegion;
	private TextureRegion smallSquareRegion;
	private TextureRegion wideRegion;
	private TextureRegion tallRegion;

	private Texture buttons;
	private Texture locked;
	private Texture unlocked;
	private TextureRegion nextBtnTexture;
	private TextureRegion backBtnTexture;
	private TextureRegion solutionButtonTexture;

	private Sprite sun;
	private Sprite wide;
	private Sprite small1;
	private Sprite small2;
	private Sprite small3;
	private Sprite small4;
	private Sprite tall1;
	private Sprite tall2;
	private Sprite tall3;
	private Sprite tall4;
	private Sprite boardCorner;
	private Sprite background;
	private Sprite nextBtn;
	private Sprite backBtn;
	private Sprite solutionBtn;
	private Sprite freeModeBtn;
	private Sprite buttonDock;

	private float boardHeight;
	private float boardWidth;

	private ArrayList<Sprite> allPieces = new ArrayList<Sprite>();

	private Point sunPnt = new Point(0, 0);
	private Point widePnt = new Point(0, 0);
	private Point small1Pnt = new Point(0, 0);
	private Point small2Pnt = new Point(0, 0);
	private Point small3Pnt = new Point(0, 0);
	private Point small4Pnt = new Point(0, 0);
	private Point tall1Pnt = new Point(0, 0);
	private Point tall2Pnt = new Point(0, 0);
	private Point tall3Pnt = new Point(0, 0);
	private Point tall4Pnt = new Point(0, 0);

	// logic
	private SettingSun gameSolver;
	// private Random rand = new Random();

	// interaction
	private Sprite movingSprite;
	private Vector2 draggingOffset = new Vector2(0, 0);
	private Vector2 draggingTemp = new Vector2(0, 0);
	private Vector2 originalMovingSpritePos = new Vector2(0, 0);
	private Point movingLastCoordinate = new Point(0, 0);
	private boolean freeMode = false; // allows free placement of blocks without constraints
	private Point movingLastSnapCoordinate = new Point(0, 0);
	private Point movingNewSnapCoordinate = new Point(0, 0);
	private Point tempPoint = new Point(0, 0);

	// Solution Variables
	private State winningSolutionChain = null;
	private State stateIterator = null;
	private Stack<State> previousState = new Stack<State>();

	@Override
	public void create() {
		batch = new SpriteBatch();

		createCamera();
		createTextures();
		createSprites();
		createGameSolver();
		positionSpritesToState(gameSolver.getCurrentState());

		// set this class up to to process input
		Gdx.input.setInputProcessor(this);

	}

	private void createCamera() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = Gdx.graphics.getWidth() / 2;
		camera.position.y = Gdx.graphics.getHeight() / 2;
		// camera.translate(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// camera = new OrthographicCamera();
		// vp = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
	}

	private void createGameSolver() {
		gameSolver = new SettingSun();
	}

	@Override
	public void render() {
		IO();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		// batch.draw(baseTexture, 0, 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		drawSprites();

		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		baseTexture.dispose();
	}

	private void createTextures() {
		baseTexture = new Texture("RisingSunImageSheet.png");
		brownBackgroundTexture = new Texture("BackgroundBrown.png");
		sunRegion = new TextureRegion(baseTexture, 0, 0, 200, 200);
		wideRegion = new TextureRegion(baseTexture, 0, 200, 200, 100);
		tallRegion = new TextureRegion(baseTexture, 200, 0, 100, 200);
		smallSquareRegion = new TextureRegion(baseTexture, 200, 200, 100, 100);

		// button textures
		buttons = new Texture("buttons.png");
		locked = new Texture("Locked500x500.png");
		unlocked = new Texture("unLocked500x500.png");
		nextBtnTexture = new TextureRegion(buttons, 0, 0, 100, 100);
		backBtnTexture = new TextureRegion(buttons, 100, 0, 100, 100);
		solutionButtonTexture = new TextureRegion(buttons, 200, 0, 100, 100);

	}

	private void createSprites() {
		sun = new Sprite(sunRegion);
		wide = new Sprite(wideRegion);
		tall1 = new Sprite(tallRegion);
		tall2 = new Sprite(tallRegion);
		tall3 = new Sprite(tallRegion);
		tall4 = new Sprite(tallRegion);
		small1 = new Sprite(smallSquareRegion);
		small2 = new Sprite(smallSquareRegion);
		small3 = new Sprite(smallSquareRegion);
		small4 = new Sprite(smallSquareRegion);

		allPieces.add(sun);
		allPieces.add(wide);
		allPieces.add(small1);
		allPieces.add(small2);
		allPieces.add(small3);
		allPieces.add(small4);
		allPieces.add(tall1);
		allPieces.add(tall2);
		allPieces.add(tall3);
		allPieces.add(tall4);

		boardCorner = new Sprite(smallSquareRegion);
		background = new Sprite(brownBackgroundTexture);

		createButtons();

		// Calculate corner position (wrote for easy readability)
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		boardHeight = small1.getHeight() * 5;
		boardWidth = small1.getWidth() * 4;
		float xPosition = (width - boardWidth) / 2;
		float yPosition = height - ((height - boardHeight) / 2);
		boardCorner.setPosition(xPosition, yPosition);

		// set up background
		float bgWidth = background.getWidth() * background.getScaleX();
		float bgHeight = background.getHeight() * background.getScaleY();

		float pcntLarger = 0.05f;
		float xFactor = (boardWidth + (boardWidth * pcntLarger)) / bgWidth;
		float yFactor = (boardHeight + (boardHeight * pcntLarger)) / bgHeight;

		background.setScale(xFactor, yFactor);
		// fix positioning after scaling (find the distance it was offset)
		// float xOffset = ((bgWidth * xFactor) - bgWidth) / 2;
		// float yOffset = ((bgHeight * yFactor) - bgHeight) / 2;
		// background.translate(xOffset, yOffset);

		background.setOrigin(xFactor, yFactor);

		positionSpriteAtOtherSprite(background, boardCorner);
		positionPiece(background, new Point(0, 0), small1.getHeight());
		// correct for creating the size slightly larger than the board
		background.translate(-boardWidth * pcntLarger / 2, boardHeight * pcntLarger / 2);

		shrinkPieces();
	}

	private void createButtons() {
		// calculate a scale value
		float scaleValueNumerator = Gdx.graphics.getHeight() * 0.05f;

		buttonDock = new Sprite(brownBackgroundTexture);
		float buttonDockXScale = buttonDock.getWidth() / (Gdx.graphics.getWidth() * 0.5f);
		buttonDock.setScale(buttonDockXScale, (1.3f * scaleValueNumerator) / buttonDock.getHeight());
		buttonDock.setOrigin(buttonDock.getScaleX(), buttonDock.getScaleY());
		float dockPosition = (Gdx.graphics.getWidth()) * 0.5f - (buttonDock.getWidth() * buttonDock.getScaleX() * 0.5f);
		buttonDock.setPosition(dockPosition, Gdx.graphics.getHeight() * 0.05f);

		// set up values to set button locations
		float dockTrim = buttonDock.getWidth() * buttonDock.getScaleX() * 0.05f;
		float btnIncrementValue = (buttonDock.getWidth() * buttonDock.getScaleX() - dockTrim) / 4;
		float btnOffSet = dockTrim;

		nextBtn = new Sprite(nextBtnTexture);
		nextBtn.setScale(scaleValueNumerator / nextBtn.getWidth());
		nextBtn.setOrigin(nextBtn.getScaleX(), nextBtn.getScaleY());
		float yOffSet = (buttonDock.getHeight() * buttonDock.getScaleY() - nextBtn.getHeight() * nextBtn.getScaleY()) / 2;
		nextBtn.setPosition(buttonDock.getX() + btnOffSet + btnIncrementValue * 2, buttonDock.getY() + yOffSet);

		backBtn = new Sprite(backBtnTexture);
		backBtn.setScale(scaleValueNumerator / backBtn.getWidth());
		backBtn.setOrigin(backBtn.getScaleX(), backBtn.getScaleY());
		backBtn.setPosition(buttonDock.getX() + btnOffSet + btnIncrementValue * 0, buttonDock.getY() + yOffSet);

		solutionBtn = new Sprite(solutionButtonTexture);
		solutionBtn.setScale(scaleValueNumerator / solutionBtn.getWidth());
		solutionBtn.setOrigin(solutionBtn.getScaleX(), solutionBtn.getScaleY());
		solutionBtn.setPosition(buttonDock.getX() + btnOffSet + btnIncrementValue * 1, buttonDock.getY() + yOffSet);

		freeModeBtn = new Sprite(locked);
		freeModeBtn.setScale(scaleValueNumerator / freeModeBtn.getWidth());
		freeModeBtn.setOrigin(freeModeBtn.getScaleX(), freeModeBtn.getScaleY());
		freeModeBtn.setPosition(buttonDock.getX() + btnOffSet + btnIncrementValue * 3, buttonDock.getY() + yOffSet);

	}

	private void shrinkPieces() {
		float scaleValue = 0.95f;
		for (Sprite piece : allPieces) {
			piece.setScale(scaleValue);
			piece.setOrigin(scaleValue, scaleValue);
		}
	}

	private void drawSprites() {
		background.draw(batch);
		buttonDock.draw(batch);
		nextBtn.draw(batch);
		backBtn.draw(batch);
		solutionBtn.draw(batch);
		freeModeBtn.draw(batch);

		for (Sprite piece : allPieces) {
			piece.draw(batch);
		}
	}

	private void positionSpritesToState(State state) {
		positionPiece(sun, state.getSunPoint(), small1.getHeight());
		positionPiece(wide, state.getWidePoint(), small1.getHeight());
		positionPiece(small1, state.getSmall1(), small1.getHeight());
		positionPiece(small2, state.getSmall2(), small1.getHeight());
		positionPiece(small3, state.getSmall3(), small1.getHeight());
		positionPiece(small4, state.getSmall4(), small1.getHeight());
		positionPiece(tall1, state.getTall1(), small1.getHeight());
		positionPiece(tall2, state.getTall2(), small1.getHeight());
		positionPiece(tall3, state.getTall3(), small1.getHeight());
		positionPiece(tall4, state.getTall4(), small1.getHeight());

		gameSolver.setSunPoint(convertPieceToPoint(sun, sunPnt));
		gameSolver.setWidePoint(convertPieceToPoint(wide, widePnt));
		gameSolver.setSmall1Point(convertPieceToPoint(small1, small1Pnt));
		gameSolver.setSmall2Point(convertPieceToPoint(small2, small2Pnt));
		gameSolver.setSmall3Point(convertPieceToPoint(small3, small3Pnt));
		gameSolver.setSmall4Point(convertPieceToPoint(small4, small4Pnt));
		gameSolver.setTall1Point(convertPieceToPoint(tall1, tall1Pnt));
		gameSolver.setTall2Point(convertPieceToPoint(tall2, tall2Pnt));
		gameSolver.setTall3Point(convertPieceToPoint(tall3, tall3Pnt));
		gameSolver.setTall4Point(convertPieceToPoint(tall4, tall4Pnt));

	}

	private void positionPiece(Sprite piece, Point position, float coordinateSize) {
		positionSpriteAtOtherSprite(piece, boardCorner);
		moveSpriteDownByHeight(piece);
		float xDiff = position.x * coordinateSize;
		float yDiff = position.y * coordinateSize * -1;
		piece.translate(xDiff, yDiff);
	}

	private void positionSpriteAtOtherSprite(Sprite toMove, Sprite targetLocation) {
		toMove.setX(targetLocation.getX());
		toMove.setY(targetLocation.getY());
	}

	private void moveSpriteDownByHeight(Sprite toAdjust) {
		float height = toAdjust.getHeight() * toAdjust.getScaleY();
		toAdjust.translateY(-height);
	}

	private void IO() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
			calculateSolution();
			// winningSolutionChain = gameSolver.solvePuzzle();
			// stateIterator = winningSolutionChain.linkedNode;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			nextState();
			// if (winningSolutionChain != null && stateIterator != null) {
			// positionSpritesToState(stateIterator);
			// stateIterator = stateIterator.linkedNode;
			// }

		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
			previousState();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
			printPiecePositions();
		}

	}

	private void printPiecePositions() {
		System.out.println("sun " + convertPieceToPoint(sun, sunPnt).toString());
		System.out.println("wide " + convertPieceToPoint(wide, widePnt).toString());
		System.out.println("tall1 " + convertPieceToPoint(tall1, tall1Pnt).toString());
		System.out.println("tall2 " + convertPieceToPoint(tall2, tall2Pnt).toString());
		System.out.println("tall3 " + convertPieceToPoint(tall3, tall3Pnt).toString());
		System.out.println("tall4 " + convertPieceToPoint(tall4, tall4Pnt).toString());
		System.out.println("small1 " + convertPieceToPoint(small1, small1Pnt).toString());
		System.out.println("small2 " + convertPieceToPoint(small2, small2Pnt).toString());
		System.out.println("small3 " + convertPieceToPoint(small3, small3Pnt).toString());
		System.out.println("small4 " + convertPieceToPoint(small4, small4Pnt).toString());
	}

	private Sprite findPieceSpriteTouched(Vector3 pointTouched) {
		for (Sprite piece : allPieces) {
			if (touchInSprite(piece, pointTouched)) {
				return piece;
			}
		}
		return null;
	}

	private boolean touchInSprite(Sprite piece, Vector3 pointTouched) {
		float xMin = piece.getX();
		float xMax = xMin + piece.getWidth() * piece.getScaleX();
		float yMin = piece.getY();
		float yMax = yMin + piece.getHeight() * piece.getScaleY();

		// determine if the touch occurred within the region defined by the sprite's rectangle
		if (pointTouched.x > xMin && pointTouched.x < xMax && pointTouched.y > yMin && pointTouched.y < yMax) {
			return true;
		}

		return false;
	}

	/**
	 * minX and minY : first term is the actual x value, the second term is a tolerance correction
	 * maxX and maxY : it is the minimumX (without tolerance) + the width/height of the sprite
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	private boolean twoSpritesCollide(Sprite first, Sprite second) {
		// find left most sprite
		Sprite leftSprite = first.getX() < second.getX() ? first : second;
		Sprite rightSprite = leftSprite == second ? first : second;
		float xRange = leftSprite.getWidth() * leftSprite.getScaleX();
		float minX = (leftSprite.getX());
		float maxX = (leftSprite.getX() + xRange);

		if (rightSprite.getX() >= minX && rightSprite.getX() <= maxX) {

			Sprite bottomSprite = first.getY() < second.getY() ? first : second;
			Sprite topSprite = bottomSprite == second ? first : second;
			float yRange = bottomSprite.getHeight() * bottomSprite.getScaleY();
			float minY = bottomSprite.getY();
			float maxY = (bottomSprite.getY() + yRange);

			if (topSprite.getY() >= minY && topSprite.getY() <= maxY) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @unfinished
	 * @bug
	 * 
	 * @param piece
	 * @return
	 */
	private boolean pieceOffBoard(Sprite piece) {
		// calculate board corners
		float minX = boardCorner.getX();
		float maxX = boardCorner.getX() + boardWidth;
		float maxY = boardCorner.getY();
		float minY = boardCorner.getY() - boardHeight;

		// account for scaled piece size
		maxY -= piece.getHeight() * piece.getScaleY();
		maxX -= piece.getWidth() * piece.getScaleX();

		return (piece.getX() < minX || piece.getX() > maxX || piece.getY() < minY || piece.getY() > maxY);
	}

	private Point convertPieceToPoint(Sprite piece, Point optional) {
		float xf = piece.getX() - boardCorner.getX();
		float yf = Math.abs((piece.getY() + piece.getHeight()) - boardCorner.getY());
		int x = Math.round(xf / small1.getWidth());
		int y = Math.round(yf / small1.getHeight());

		if (optional == null) {
			return new Point(x, y);
		} else {
			optional.x = x;
			optional.y = y;
			return optional;
		}
	}

	private boolean pieceCollidesWithAnotherPieceOrOffBoard(Sprite piece) {
		if (pieceOffBoard(piece)) {
			return true;
		}

		for (Sprite otherPiece : allPieces) {
			// don't check for collision with self
			if (piece != otherPiece) {
				// check collision for each piece, return true if there was a collision
				if (twoSpritesCollide(piece, otherPiece)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (pointer == 0) {
			camera.unproject(conversionVector.set(screenX, screenY, 0));
			Sprite spriteTouched = findPieceSpriteTouched(conversionVector);
			if (spriteTouched != null) {
				movingSprite = spriteTouched;
				convertPieceToPoint(movingSprite, movingLastCoordinate);
				convertPieceToPoint(movingSprite, movingLastSnapCoordinate);// TODO testing jump
																			// prevention

				originalMovingSpritePos.x = movingSprite.getX();
				originalMovingSpritePos.y = movingSprite.getY();
				// calculate an offset so that dragging appears to only move
				draggingOffset.x = movingSprite.getX() - conversionVector.x;
				draggingOffset.y = movingSprite.getY() - conversionVector.y;

				return true;
			} else {
				// check if a button was pressed
				if (touchInSprite(backBtn, conversionVector)) {
					previousState();
				}
				if (touchInSprite(nextBtn, conversionVector)) {
					nextState();
				}
				if (touchInSprite(solutionBtn, conversionVector)) {
					calculateSolution();
				}
				if (touchInSprite(freeModeBtn, conversionVector)) {
					if (freeModeBtn.getTexture() == locked) {
						freeMode = true;
						freeModeBtn.setTexture(unlocked);
					} else {
						freeMode = false;
						freeModeBtn.setTexture(locked);
						for(Sprite piece : allPieces){
							updateGameSolver(piece, convertPieceToPoint(piece, tempPoint));
						}
					}
				}
			}
		}

		// System.out.println("click:" + conversionVector.x + " " + conversionVector.y);
		// System.out.println("wide:" + wide.getX() + " " + wide.getY());
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (pointer == 0) {
			if (movingSprite != null) {
				convertPieceToPoint(movingSprite, movingLastCoordinate);
				positionPiece(movingSprite, movingLastCoordinate, small1.getWidth());
//				updateGameSolver(movingSprite, movingLastCoordinate); // TODO testing comment out
				 updateGameSolver(movingSprite, movingLastSnapCoordinate);

			}

			// remove any constraints used in moving sprites
			movingSprite = null;
			// lock.unlockAll();
			return true;
		}
		return false;
	}

	private void updateGameSolver(Sprite piece, Point pointToSet) {
		if (piece == sun) {
			gameSolver.setSunPoint(pointToSet);
		} else if (piece == wide) {
			gameSolver.setWidePoint(pointToSet);
		} else if (piece == small1) {
			gameSolver.setSmall1Point(pointToSet);
		} else if (piece == small2) {
			gameSolver.setSmall2Point(pointToSet);
		} else if (piece == small3) {
			gameSolver.setSmall3Point(pointToSet);
		} else if (piece == small4) {
			gameSolver.setSmall4Point(pointToSet);
		} else if (piece == tall1) {
			gameSolver.setTall1Point(pointToSet);
		} else if (piece == tall2) {
			gameSolver.setTall2Point(pointToSet);
		} else if (piece == tall3) {
			gameSolver.setTall3Point(pointToSet);
		} else if (piece == tall4) {
			gameSolver.setTall4Point(pointToSet);
		}

	}

	private void calculateSolution() {
		winningSolutionChain = gameSolver.solvePuzzle();
		stateIterator = winningSolutionChain;// .linkedNode;
		previousState.removeAllElements();	//clear stack
		for(Sprite piece : allPieces){
			updateGameSolver(piece, convertPieceToPoint(piece, tempPoint));
		}
	}

	private void nextState() {
		if (winningSolutionChain != null && stateIterator != null) {
			previousState.push(stateIterator);
			stateIterator = stateIterator.linkedNode;
			if (stateIterator != null) {
				positionSpritesToState(stateIterator);
			}
		}
	}

	private void previousState() {
		if (winningSolutionChain != null && !previousState.isEmpty()) {
			stateIterator = previousState.pop();
			positionSpritesToState(stateIterator);

		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (movingSprite != null && pointer == 0) {
			// TODO: START HERE - add a check to see if piece is transitioning to new coordinate
			// (code already
			// made in the SettingSun.java). use this check along w/ collision.
			// add a field for the last snapped coordinate, always check if the new evaluated snap
			// Point.java
			// instance is something different. If it is, ask the game solver
			// (gameSolver.isValidTransition())
			// if the move will result in a valid configuration.

			// convert the screen coordinates to game coordinates
			camera.unproject(conversionVector.set(screenX, screenY, 0));

			// save the pieces position before any translations are performed
			draggingTemp.set(movingSprite.getX(), movingSprite.getY());

			// calculate the translated position accounting for where the player touched the piece
			float newX = conversionVector.x + draggingOffset.x;
			float newY = conversionVector.y + draggingOffset.y;
			movingSprite.setPosition(newX, newY);

			if (freeMode) {
				if (pieceOffBoard(movingSprite)) {
					movingSprite.setPosition(draggingTemp.x, draggingTemp.y);
				}

				// no collision checking in free mode
				return true;
			}

			// unlock certain regions if user restored point back (piece jumping prevention)
			// lock.attemptUnlock(movingSprite);

			// collision checking
			// test the new position (both x and y changed)
			if (!pieceCollidesWithAnotherPieceOrOffBoard(movingSprite) && checkAndUpdateSnap(movingSprite)) {
				return true;
			} else {
			}

			// test only changing the x value (sliding along a piece) because function yet to return
			movingSprite.setY(draggingTemp.y);
			if (!pieceCollidesWithAnotherPieceOrOffBoard(movingSprite) && checkAndUpdateSnap(movingSprite)) {
				return true;
			} else {
				// something collided, restore the calculated y value before checking x
				movingSprite.setY(newY);
			}

			// test keeping x same while changing y (piece sliding along the edge of another piece)
			movingSprite.setX(draggingTemp.x);
			if (!pieceCollidesWithAnotherPieceOrOffBoard(movingSprite) && checkAndUpdateSnap(movingSprite)) {
				return true;
			} else {
			}

			// restore the position before it was modified because there were no valid locations
			movingSprite.setX(draggingTemp.x);
			movingSprite.setY(draggingTemp.y);
			return true;

		}
		return false;
	}

	private boolean checkAndUpdateSnap(Sprite movePiece) {
		// update the new snap point to compareAgasint
		convertPieceToPoint(movePiece, movingNewSnapCoordinate);
		if (movingNewSnapCoordinate.equals(movingLastSnapCoordinate)) {
			// no transition happened
			return true;
		} else {
			// check if the transition is valid (movingLastSnapCoordinate must be a valid position
			// in game solver)
			if (gameSolver.isValidTransition(movingLastSnapCoordinate, movingNewSnapCoordinate)) {
				// commit transition by updating gameSolver
				// (updates boolean board internally and prepares this method for next call)
				updateGameSolver(movePiece, movingNewSnapCoordinate);

				// update the last snapCoordinate
				movingLastSnapCoordinate.copyPoint(movingNewSnapCoordinate);
				return true;
			} else {
				return false;
			}
		}
	}

	// ------------- Below are un-used input processor method overrides -------------
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}

