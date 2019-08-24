package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class CustomAlerts {	
	public static Alert getVictoryAlert() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game won!");
		alert.setHeaderText("Congratulations you won the game!");
		alert.setContentText("Deal again?");
		return alert;
	}
	
	public static Alert getAboutAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText(null);
		alert.setContentText("Game developed by Mattias Sikvall Källström.");
		return alert;
	}
}
