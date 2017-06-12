package sayden.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import asciiPanel.AsciiPanel;
import sayden.ApplicationMain;

public class HelpScreen implements Screen {

	@Override
	public void displayOutput(AsciiPanel terminal) {
		terminal.clear();
		terminal.writeCenter("Sayden", 1);
		terminal.write("Este es un juego en etapa de desarrollo", 1, 3);
		
		int y = 5;
		terminal.write("[wasd] Muevete por el mapa", 2, y++);
		terminal.write("Tu velocidad de movimiento define que tan rapido te mueves", 6, y++);
		terminal.write("Tu velocidad de ataque define que tan rapido atacas", 6, y++);
		terminal.write("Muevete a una casilla ocupada para atacar/interactuar", 6, y++);
		terminal.write("Puedes esquivar ataques y proyectiles si te mueves rapido", 6, y++);
		y++;
		terminal.write("[espacio] Saltea un movimiento", 2, y++);
		terminal.write("Saltear un movimiento es mas rapido que moverse", 6, y++);
		y++;
		terminal.write("[i - tab] Abre el inventario", 2, y++);
		terminal.write("Una vez dentro navega con [wasd - enter]", 6, y++);
		y++;
		terminal.write("[r] Observa el entorno", 2, y++);
		terminal.write("[f] Usa tu arma de rango", 2, y++);
		terminal.write("[g - e] Levanta un objeto", 2, y++);
		terminal.write("[q] Lanza conjuros rapidamente", 2, y++);
		
		terminal.writeCenter("-- presiona cualquier tecla --", 22);
	}

	@Override
	public Screen respondToUserInput(KeyEvent key, ApplicationMain main) {
		return null;
	}

	@Override
	public Screen respondToMouseInput(MouseEvent mouse) {
		return this;
	}
}
