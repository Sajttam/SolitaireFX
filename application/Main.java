package application;

import java.util.List;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import models.Card;
import models.Pile;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Main extends Application {
	// TODO: SETINGS
	
	public MenuBar initMenuBar(Controller controller) {
		MenuBar menuBar = new MenuBar();

		// --- Menu Game
		Menu menuGame = new Menu("Game");
		MenuItem itemDeal = new MenuItem("Deal");
		itemDeal.setOnAction(e -> controller.deal());
		MenuItem itemUndo = new MenuItem("Undo");
		itemUndo.setOnAction(e -> controller.undoMove());
		itemUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		MenuItem itemExit = new MenuItem("Exit");
				
		itemExit.setOnAction(e -> System.exit(0));
		menuGame.getItems().addAll(itemDeal, itemUndo, itemExit);

		// --- Menu Help
		Menu menuHelp = new Menu("Help");
		MenuItem itemAbout = new MenuItem("About"); // TODO: Add about window
		itemAbout.setOnAction(e -> CustomAlerts.getAboutAlert().showAndWait());
		menuHelp.getItems().addAll(itemAbout);

		menuBar.getMenus().addAll(menuGame, menuHelp);
		
		return menuBar;
	}
	
	public void initCardListeneres(Controller controller) {
		for (Card c : controller.getAllCards()) {
			c.setOnDragDetected(e -> {
				if (controller.beginMove(c)) {
					Dragboard db = c.startDragAndDrop(TransferMode.ANY);
					// db.setDragView(c.getImage()); //TODO: FIX IMAGAES
					ClipboardContent content = new ClipboardContent();
					content.putString(c.toString());
					db.setContent(content);
					e.consume();
				}
			});
			c.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
				if (e.getClickCount() > 1)
					controller.quickMove(c);
			});
		}
	}
	
	public HBox initStockAndTalon(Controller controller, Insets insetsStock, Insets instetsTalon) {
		HBox topLeft = new HBox();
		Pile stock = controller.getStock();
		Pile talon = controller.getTalon();
		HBox.setMargin(stock, insetsStock);
		HBox.setMargin(talon, instetsTalon);
		topLeft.setMaxHeight(stock.getHeight());
		topLeft.getChildren().addAll(stock, talon);

		// ADD TALON LISTENER
		controller.getPiles().get(0).addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED,
				e -> controller.stockUncover());
		return topLeft;
	}
	
	public HBox initTableau(Controller controller) {
		HBox bottom = new HBox();
		for (int i = 2; i < 9; i++) {
			Pile tableau = controller.getPiles().get(i);
			bottom.getChildren().add(tableau);
			tableau.setAlignment(Pos.TOP_CENTER);
		}
		HBox.setMargin(controller.getPiles().get(2), new Insets(0, 0, 312, 32));
		bottom.setAlignment(Pos.TOP_LEFT);
		return bottom;
	}
	
	public HBox initFoundation(Controller controller, Insets insets) {
		HBox topRight = new HBox();
		for (int i = 9; i < 13; i++) {
			Pile foundation = controller.getPiles().get(i);
			topRight.getChildren().add(foundation);
			HBox.setMargin(foundation, insets);
		}
		topRight.setMaxHeight(controller.getStock().getHeight());
		topRight.setAlignment(Pos.TOP_RIGHT);
		return topRight;
	}
	
	public void intitListeners(Controller controller) {
		for (int i = 2; i < 13; i++) {
			Pile p = controller.getPiles().get(i);
			p.addEventHandler(javafx.scene.input.DragEvent.DRAG_OVER, e -> {
				Dragboard db = e.getDragboard();
				e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				e.consume();
			});
			p.addEventHandler(javafx.scene.input.DragEvent.DRAG_DROPPED, e -> {
				controller.completeMove(p);
			});
		}	
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			
			double menuStockOffset = 16;
			double stockTableauOffset = 32;
			
			// BASIC BORDER PANE
			VBox vBox = new VBox();

			// CREATE THE LOGIC CONTROLLER
			Controller controller = new Controller();

			// CREATE MENU
			MenuBar menuBar = initMenuBar(controller);

			// ADD LISTENERS TO ALL CARDS
			initCardListeneres(controller);
			
			// STOCK AND TALON
			HBox topLeft = initStockAndTalon(controller, new Insets(menuStockOffset, 0, stockTableauOffset, 16), new Insets(menuStockOffset, 0, stockTableauOffset, 0));

			// TABLEAU
			HBox bottom = initTableau(controller);

			// FOUNDATION
			HBox topRight = initFoundation(controller, new Insets(menuStockOffset, 0, stockTableauOffset, 0));

			// ADD LISTENERS TO TABLEAU AND FOUNDATION
			intitListeners(controller);

			// PLACE STOCK, TALON, FOUNDATION AND TABLEAU IN SCEN
			ObservableList list = vBox.getChildren();
			BorderPane middle = new BorderPane();
			middle.setRight(topRight);
			middle.setLeft(topLeft);
			list.addAll(menuBar, middle, bottom);			
			

			Scene scene = new Scene(vBox);
			vBox.setStyle("-fx-background-color: green;");
			primaryStage.sizeToScene();
			primaryStage.setTitle("SolitaireFX");
			primaryStage.setScene(scene);
			primaryStage.show();

			// UPDATE SPACING WHEN RESIZEING
			primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
				int spacing = (int) (newVal.intValue() - controller.getPiles().get(3).getWidth() * 7 - 48);
				if (spacing > 0) {
					bottom.setSpacing(spacing / 6);
				} else {
					bottom.setSpacing(0);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
