package modelo;

import java.util.ArrayList;
import java.util.Iterator;

import controlador.ChessController;
import controlador.ToastController;
import enumerados.Color;
import javafx.application.Platform;
import utilidades.Util;

public class Tablero {

	public ArrayList<Pieza> listaPiezas;
	private Pieza backup;
	private Pieza ultimaPiezaJaque;
	private String eleccion;
	private Jugador jugadorBlanco;
	private Jugador jugadorNegro;

	public Tablero(Jugador blanco, Jugador negro) {

		this.jugadorBlanco = blanco;
		this.jugadorNegro = negro;
		listaPiezas = new ArrayList<Pieza>();
		eleccion = "";
		crearTablero();

//		borrarPiezasMenosRey();
	}

	public Jugador getJugadorBlanco() {
		return jugadorBlanco;
	}

	public void setJugadorBlanco(Jugador jugadorBlanco) {
		this.jugadorBlanco = jugadorBlanco;
	}

	public Jugador getJugadorNegro() {
		return jugadorNegro;
	}

	public void setJugadorNegro(Jugador jugadorNegro) {
		this.jugadorNegro = jugadorNegro;
	}

	public String getEleccion() {
		return eleccion;
	}

	public void setEleccion(String eleccion) {
		this.eleccion = eleccion;
	}

	public Pieza getBackup() {
		Pieza p = backup;
		this.backup = null;

		return p;
	}

	public void setBackup(Pieza backup) {
		this.backup = backup;
	}

	public ArrayList<Pieza> getListaPiezas() {
		return listaPiezas;
	}

	public void crearTablero() {

		Peon peonNegro;
		Peon peonBlanco;
		Pieza piezaBlanca = null;
		Pieza piezaNegra = null;
		int x = 555;
		for (int i = 0; i < 8; i++) {

			peonNegro = new Peon(Color.N, new Posicion(x, 239), this);
			peonBlanco = new Peon(Color.B, new Posicion(x, 744), this);

			switch (i) {
			case 0, 7:
				piezaBlanca = new Torre(Color.B, new Posicion(x, 845), this);
				piezaNegra = new Torre(Color.N, new Posicion(x, 138), this);
				break;
			case 1, 6:
				piezaBlanca = new Caballo(Color.B, new Posicion(x, 845), this);
				piezaNegra = new Caballo(Color.N, new Posicion(x, 138), this);

				break;
			case 2, 5:
				piezaBlanca = new Alfil(Color.B, new Posicion(x, 845), this);
				piezaNegra = new Alfil(Color.N, new Posicion(x, 138), this);

				break;
			case 3:
				piezaBlanca = new Dama(Color.B, new Posicion(x, 845), this);
				piezaNegra = new Dama(Color.N, new Posicion(x, 138), this);

				break;
			case 4:
				piezaBlanca = new Rey(Color.B, new Posicion(x, 845), this);
				piezaNegra = new Rey(Color.N, new Posicion(x, 138), this);

				break;

			default:
				break;
			}
			x += Util.SIZE;
			listaPiezas.add(peonNegro);
			listaPiezas.add(peonBlanco);
			listaPiezas.add(piezaNegra);
			listaPiezas.add(piezaBlanca);

		}

	}

	public boolean detectarColision(double x, double y, Pieza piezaOrigen, boolean primeraVez) {
		boolean valor = true;
		Iterator<Pieza> iterator = listaPiezas.iterator();

		while (iterator.hasNext()) {
			Pieza pieza = iterator.next();
			double posX = pieza.getPosX();
			double posY = pieza.getPosY();

			// Comprobaciones para Peón
			if (piezaOrigen.getNombre().equals("Peon")) {
				valor = false;
				if (x == posX && y == posY && piezaOrigen.getPosX() == x) {
					return false;
				} else if (piezaOrigen.getPosX() != x && x == posX && y == posY) {
					valor = true;
					pieza.borrarPieza();
					iterator.remove();
					break;
				} else if (piezaOrigen.movimientoPermitido(x, y, false)) {

					if (x == piezaOrigen.getPosX()) {

						valor = true;
					}
				}
			}

			// Comprobaciones para Alfil, Torre, Dama y Rey
			if (piezaOrigen.getNombre().equals("Alfil")
					&& piezaEnMedioDiagonales(piezaOrigen.getPosX(), piezaOrigen.getPosY(), x, y, piezaOrigen)) {
				return false;
			} else if (piezaOrigen.getNombre().equals("Torre")
					&& piezaEnMedioRecta(piezaOrigen.getPosX(), piezaOrigen.getPosY(), x, y, piezaOrigen)) {
				return false;
			} else if ((piezaOrigen.getNombre().equals("Dama") || piezaOrigen.getNombre().equals("Rey"))
					&& (piezaEnMedioRecta(piezaOrigen.getPosX(), piezaOrigen.getPosY(), x, y, piezaOrigen)
							|| piezaEnMedioDiagonales(piezaOrigen.getPosX(), piezaOrigen.getPosY(), x, y,
									piezaOrigen))) {
				return false;
			}

			// Comprobaciones de colisión
			if (x == posX && y == posY) {
				if (!piezaOrigen.getColor().equals(pieza.getColor())) {
					pieza.borrarPieza();
					iterator.remove();
					valor = true;
				} else {
					valor = false;
				}
				break;
			}
		}

		return valor;
	}

	public boolean comprobarColision(double x, double y, Pieza piezaOrigen) {
		for (Pieza pieza : listaPiezas) {

			if (x == pieza.getPosX() && y == pieza.getPosY()) {
				if (!piezaOrigen.getColor().equals(pieza.getColor())) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public boolean detectarJaquesGeneral(Color c) {
		Pieza rey = encontrarPieza("Rey", c);
		return casillaBajoAtaque(rey.getPosX(), rey.getPosY(), rey.getColor());
	}

	public boolean detectarJaques(double x, double y, Pieza piezaOrigen) {

		Color color = piezaOrigen.getColor();
		String nombre = piezaOrigen.getNombre();

		if (nombre.equals("Peon") && jaquePeon(x, y, color)) {
			ultimaPiezaJaque = piezaOrigen;
			System.out.println("Peón da jaque");
			return true;
		} else if (nombre.equals("Alfil") && jaqueDiagonal(x, y, color, piezaOrigen)) {
			ultimaPiezaJaque = piezaOrigen;
			return true;
		} else if (nombre.equals("Torre") && jaqueRecta(x, y, color, piezaOrigen)) {
			ultimaPiezaJaque = piezaOrigen;
			return true;
		} else if (nombre.equals("Dama")
				&& (jaqueRecta(x, y, color, piezaOrigen) || jaqueDiagonal(x, y, color, piezaOrigen))) {
			ultimaPiezaJaque = piezaOrigen;
			return true;
		} else if (nombre.equals("Caballo") && jaqueCaballo(x, y, color)) {
			ultimaPiezaJaque = piezaOrigen;
			return true;
		}

		return false;
	}

	public boolean jaqueRey(double x, double y, Pieza piezaOrigen) {

		double xRival = calcularXReyRival(piezaOrigen.getColor());
		double yRival = calcularYReyRival(piezaOrigen.getColor());

		double resX = Math.abs(xRival - x);
		double resY = Math.abs(yRival - y);
		return (resX == Util.SIZE || resY == Util.SIZE) && (resX <= Util.SIZE && resY <= Util.SIZE);
	}

	public boolean jaquePeon(double xOrigen, double yOrigen, Color c) {
		double x = calcularXReyRival(c);
		double y = calcularYReyRival(c);

		System.out.println("Rey rival en posición: (" + x + ", " + y + ")");
		System.out.println("Peón en posición: (" + xOrigen + ", " + yOrigen + ")");

		if (c == Color.B && Math.abs(x - xOrigen) == Util.SIZE && y == yOrigen - Util.SIZE) {
			System.out.println("Peón blanco puede dar jaque");
			return true;
		} else if (c == Color.N && Math.abs(x - xOrigen) == Util.SIZE && y == yOrigen + Util.SIZE) {
			System.out.println("Peón negro puede dar jaque");
			return true;
		}

		return false;
	}

	public boolean jaqueDiagonal(double xOrigen, double yOrigen, Color c, Pieza piezaActual) {

		double x = calcularXReyRival(c);
		double y = calcularYReyRival(c);

		if (!piezaEnMedioDiagonales(xOrigen, yOrigen, x, y, piezaActual)) {
			return Math.abs(x - xOrigen) == Math.abs(y - yOrigen);
		}

		return false;
	}

	public boolean jaqueCaballo(double xOrigen, double yOrigen, Color c) {

		double x = calcularXReyRival(c);
		double y = calcularYReyRival(c);

		if (xOrigen + Util.SIZE == x && yOrigen - Util.SIZE * 2 == y) {
			/*
			 * |- | |
			 */
			return true;
		} else if (xOrigen + Util.SIZE == x && yOrigen + Util.SIZE * 2 == y) {
			/*
			 * | | |-
			 */
			return true;

		} else if (xOrigen - Util.SIZE == x && yOrigen - Util.SIZE * 2 == y) {
			/*
			 * -| | |
			 */
			return true;

		} else if (xOrigen - Util.SIZE == x && yOrigen + Util.SIZE * 2 == y) {
			/*
			 * | | -|
			 */
			return true;

		} else if (xOrigen + Util.SIZE * 2 == x && yOrigen - Util.SIZE == y) {
			/*
			 * | ---
			 */
			return true;
		} else if (xOrigen + Util.SIZE * 2 == x && yOrigen + Util.SIZE == y) {
			/*
			 * --- |
			 */
			return true;

		} else if (xOrigen - Util.SIZE * 2 == x && yOrigen - Util.SIZE == y) {
			/*
			 * | ---
			 */
			return true;

		} else if (xOrigen - Util.SIZE * 2 == x && yOrigen + Util.SIZE == y) {
			/*
			 * 
			 * --- |
			 */
			return true;

		}

		return false;

	}

	public boolean jaqueRecta(double xOrigen, double yOrigen, Color c, Pieza piezaActual) {
		double x = calcularXReyRival(c);
		double y = calcularYReyRival(c);

		if (!piezaEnMedioRecta(xOrigen, yOrigen, x, y, piezaActual)) {

			return x == xOrigen || y == yOrigen;
		}

		return false;

	}

	public boolean piezaEnMedioRecta(double xOrigen, double yOrigen, double xDestino, double yDestino,
			Pieza piezaActual) {
		double diferenciaX = xDestino - xOrigen;
		double diferenciaY = yDestino - yOrigen;

		if (diferenciaX == 0 || diferenciaY == 0) {
			if (diferenciaY == 0) {
				for (Pieza pieza : listaPiezas) {
					if (pieza.getPosY() == yOrigen && pieza.getPosX() > Math.min(xOrigen, xDestino)
							&& pieza.getPosX() < Math.max(xOrigen, xDestino) && pieza != piezaActual) {
						return true;
					}
				}
			}

			if (diferenciaX == 0) {
				for (Pieza pieza : listaPiezas) {
					if (pieza.getPosX() == xOrigen && pieza.getPosY() > Math.min(yOrigen, yDestino)
							&& pieza.getPosY() < Math.max(yDestino, yOrigen) && pieza != piezaActual) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean piezaEnMedioDiagonales(double xOrigen, double yOrigen, double xDestino, double yDestino,
			Pieza piezaActual) {
		double diferenciaX = xDestino - xOrigen;
		double diferenciaY = yDestino - yOrigen;

		if (Math.abs(diferenciaX) == Math.abs(diferenciaY)) {
			double pasoX = diferenciaX > 0 ? Util.SIZE : -Util.SIZE;
			double pasoY = diferenciaY > 0 ? Util.SIZE : -Util.SIZE;
			double x = xOrigen + pasoX;
			double y = yOrigen + pasoY;

			while ((pasoX > 0 && x < xDestino) || (pasoX < 0 && x > xDestino) || (pasoY > 0 && y < yDestino)
					|| (pasoY < 0 && y > yDestino)) {
				for (Pieza pieza : listaPiezas) {
					if (pieza.getPosX() == x && pieza.getPosY() == y && pieza != piezaActual) {
						return true;
					}
				}
				x += pasoX;
				y += pasoY;
			}
		}
		return false;
	}

	public Pieza encontrarPieza(String nombre, Color color) {

		for (Pieza pieza : listaPiezas) {

			if (pieza.getNombre().equals(nombre) && pieza.getColor().equals(color)) {

				return pieza;
			}
		}
		return null;
	}

	public double calcularXReyRival(Color c) {

		double x = 0;
		for (Pieza pieza : listaPiezas) {

			if (!pieza.getColor().equals(c) && pieza.getNombre().equals("Rey")) {
				x = pieza.getPosX();
				break;
			}
		}
		return x;
	}

	public double calcularYReyRival(Color c) {

		double y = 0;
		for (Pieza pieza : listaPiezas) {

			if (!pieza.getColor().equals(c) && pieza.getNombre().equals("Rey")) {
				y = pieza.getPosY();
				break;
			}
		}
		return y;
	}

	public int calcularPosicionX(int columna) {
		return 555 + columna * Util.SIZE;
	}

	private int calcularPosicionY(int fila) {
		return 845 - fila * Util.SIZE;
	}

	private Pieza obtenerPieza(int x, int y) {
		for (Pieza pieza : listaPiezas) {
			if (pieza.getPosX() == x && pieza.getPosY() == y) {
				return pieza;
			}
		}
		return null;
	}

	public boolean esEnroque(int x, int xTorreMenor, Color color) {
		int fila = (color == Color.N) ? 7 : 0;
		int reyX = calcularPosicionX(4);
		int reyY = calcularPosicionY(fila);
		int torreX = calcularPosicionX(x);
		int torreY = calcularPosicionY(fila);

		Pieza rey = obtenerPieza(reyX, reyY);
		Pieza torre = obtenerPieza(torreX, torreY);

		return esReyYTorresValidosParaEnroque(rey, torre) && !hayPiezasEntre(reyX, reyY, torreX, torreY)
				&& !estanCasillasBajoAtaque(reyX, reyY, calcularPosicionX(xTorreMenor), reyY, color);

	}

	public boolean esEnroqueCortoPosible(Color color) {

		return esEnroque(7, 6, color);
	}

	public boolean esEnroqueLargoPosible(Color color) {
		return esEnroque(0, 1, color);
	}

	private boolean esReyYTorresValidosParaEnroque(Pieza rey, Pieza torre) {
		return rey != null && torre != null && rey.getNombre().equals("Rey") && torre.getNombre().equals("Torre")
				&& !rey.getHaSidoMovida() && !torre.getHaSidoMovida();
	}

	private boolean hayPiezasEntre(int x1, int y1, int x2, int y2) {
		int minX = Math.min(x1, x2) + Util.SIZE;
		int maxX = Math.max(x1, x2) - Util.SIZE;
		for (int x = minX; x <= maxX; x += Util.SIZE) {
			if (obtenerPieza(x, y1) != null) {
				return true;
			}
		}
		return false;
	}

	public boolean estanCasillasBajoAtaque(double x1, double y1, double x2, double y2, Color color) {

		Color newColor = color.equals(Color.B) ? Color.N : Color.B;
		double xRey = calcularXReyRival(newColor);
		double yRey = calcularYReyRival(newColor);
		double tmp = Math.max(x1, x2);
		x1 = Math.min(x1, x2);
		x2 = tmp;
		for (double x = x1; x <= x2; x += Util.SIZE) {
			setReyPos(x, y1, color);
			if (casillaBajoAtaque(x, y1, color)) {
				setReyPos(xRey, yRey, color);
				return true;
			}
		}
		setReyPos(xRey, yRey, color);
		return false;
	}

	public boolean casillaBajoAtaque(double x, double y, Color color) {
		for (Pieza pieza : listaPiezas) {
			if (pieza.getColor() != color && pieza.movimientoPermitido(x, y, false)
					&& detectarJaques(pieza.getPosX(), pieza.getPosY(), pieza)) {
				return true; // Si la pieza puede moverse a (x, y), la casilla está bajo ataque
			}
		}
		return false; // Si ninguna pieza enemiga puede moverse a (x, y), la casilla no está bajo
		// ataque
	}

	public void realizarEnroque(Color color, int torreInicio, int torreFin, int reyInicio, int reyFin) {
		int fila = (color == Color.N) ? 7 : 0;
		int torreX = calcularPosicionX(torreInicio);
		int torreY = calcularPosicionY(fila);

		Pieza torre = obtenerPieza(torreX, torreY);
		Pieza rey = obtenerPieza(calcularPosicionX(reyInicio), torreY);

		for (Pieza pieza : listaPiezas) {
			if (pieza.equals(torre)) {
				pieza.setPosX(calcularPosicionX(torreFin));
				pieza.setPosY(torreY);

				pieza.reinciarPosicion();

			} else if (pieza.equals(rey)) {
				pieza.setPosX(calcularPosicionX(reyFin));
			}
		}

		torre.cambiarTurno();
	}

	public void realizarEnroqueCorto(Color color) {
		realizarEnroque(color, 7, 5, 4, 6);
	}

	public void realizarEnroqueLargo(Color color) {
		realizarEnroque(color, 0, 3, 4, 2);
	}

	public void jaqueMateAlert(String mate) {

		ToastController.showToast(ToastController.TOAST_ERROR, ChessController.enviarControl(), "JAQUE MATE");
		
//		ChessController chess = new ChessController();
//		
//		chess.determinarEstadoPartida(mate);

	}

	public void jaqueAlert() {

		ToastController.showToast(ToastController.TOAST_WARN, ChessController.enviarControl(), "JAQUE");

	}

	public void setReyPos(double x, double y, Color color) {

		for (Pieza pieza : listaPiezas) {
			if (pieza.getNombre().equals("Rey") && pieza.getColor().equals(color)) {

				pieza.setPosX(x);
				pieza.setPosY(y);
				break;
			}
		}
	}

	public ArrayList<Posicion> obtenerPosicionesValidasRey(Pieza pieza) {
		ArrayList<Posicion> posicionesValidas = new ArrayList<>();
		double xRey = pieza.getPosX();
		double yRey = pieza.getPosY();

		for (double x = xRey - Util.SIZE; x <= xRey + Util.SIZE; x += Util.SIZE) {
			for (double y = yRey - Util.SIZE; y <= yRey + Util.SIZE; y += Util.SIZE) {
				if (x == xRey && y == yRey)
					continue; // Ignora la posición actual del rey
				if (x < 549 || x > 1352 || y < 137 || y > 945)
					continue; // Fuera del tablero

				if (pieza.puedeReyMoverse(x, y)) {
					System.err.println("Casilla: x: " + x + " y: " + y);

					posicionesValidas.add(new Posicion(x, y));
				}

			}
		}
		System.out.println();
		return posicionesValidas;
	}

	public void vaciarJaque(Color c) {
		for (Pieza pieza : listaPiezas) {

			if (c == pieza.getColor()) {
				pieza.vaciarJaque();

			}
		}
	}

	public boolean casillaBajoAtaqueXRay(double x, double y, Color color) {
		for (Pieza pieza : listaPiezas) {
			if (pieza.getColor() != color && pieza.movimientoPermitido(x, y, false)) {
				return true; // Si la pieza puede moverse a (x, y), la casilla está bajo ataque
			}
		}
		return false; // Si ninguna pieza enemiga puede moverse a (x, y), la casilla no está bajo
		// ataque
	}

	public boolean atacandoOpcionesRey(Pieza rey) {

		ChessController.borrarRojo();
		for (Posicion posicion : obtenerPosicionesValidasRey(rey)) {

			ChessController.insertarRojos(posicion.getX(), posicion.getY());
		}
		return obtenerPosicionesValidasRey(rey).isEmpty();
	}

	public void borrarPiezasMenosRey() {

		ArrayList<String> nombres = new ArrayList<String>();

		nombres.add("Torre");
		nombres.add("Dama");
		nombres.add("Rey");
		for (Iterator<Pieza> listaPieza = listaPiezas.iterator(); listaPieza.hasNext();) {
			Pieza pieza = listaPieza.next();

			if (!nombres.contains(pieza.getNombre())) {
				listaPieza.remove();
				pieza.borrarPieza();
			}
		}
	}

	public Pieza encontrarAmenaza(Pieza piezaAtacada) {

		for (Pieza pieza : listaPiezas) {
			if (pieza.getColor() != piezaAtacada.getColor()) {

				if (pieza.getHaceJaque()) {
					return pieza;
				}
			}
		}
		return null;
	}

	public boolean puedeAccederCasilla(Pieza piezaAtacada) {

		Pieza piezaAtacante = encontrarAmenaza(piezaAtacada);

		if (piezaAtacante != null) {

			Color color = piezaAtacante.getColor();
			double xDestino = piezaAtacante.getPosX();
			double yDestino = piezaAtacante.getPosY();

			for (Pieza pieza : listaPiezas) {
				if (!pieza.getColor().equals(color)) { // Pieza rival
					if (pieza.movimientoPermitido(xDestino, yDestino, false)) {
						switch (pieza.getNombre()) {
						case "Peon":
							if (!comprobarColisionPeon(pieza, xDestino, yDestino)) {
								return true;
							}
							break;
						case "Alfil":
							if (!piezaEnMedioDiagonales(pieza.getPosX(), pieza.getPosY(), xDestino, yDestino, pieza)) {
								return true;
							}
							break;
						case "Torre":
							if (!piezaEnMedioRecta(pieza.getPosX(), pieza.getPosY(), xDestino, yDestino, pieza)) {
								return true;
							}
							break;
						case "Dama":
							if (!piezaEnMedioRecta(pieza.getPosX(), pieza.getPosY(), xDestino, yDestino, pieza)
									|| !piezaEnMedioDiagonales(pieza.getPosX(), pieza.getPosY(), xDestino, yDestino,
											pieza)) {
								return true;
							}
							break;
						default:
							break;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean comprobarColisionPeon(Pieza peon, double xDestino, double yDestino) {
		double xOrigen = peon.getPosX();
		double yOrigen = peon.getPosY();
		Color color = peon.getColor();

		if (color == Color.B) {
			// Peón blanco
			if (xDestino == xOrigen && yDestino == yOrigen - Util.SIZE) {
				// Movimiento hacia adelante una casilla
				return comprobarColision(xDestino, yDestino, peon);
			} else if (Math.abs(xDestino - xOrigen) == Util.SIZE && yDestino == yOrigen - Util.SIZE) {
				// Captura en diagonal
				return !comprobarColision(xDestino, yDestino, peon);
			}
		} else {
			// Peón negro
			if (xDestino == xOrigen && yDestino == yOrigen + Util.SIZE) {
				// Movimiento hacia adelante una casilla
				return comprobarColision(xDestino, yDestino, peon);
			} else if (Math.abs(xDestino - xOrigen) == Util.SIZE && yDestino == yOrigen + Util.SIZE) {
				// Captura en diagonal
				return !comprobarColision(xDestino, yDestino, peon);
			}
		}
		return false;
	}

	public boolean puedeAliadoAccederAAlgunaCasilla(Color color) {

		int x1 = 555;
		int y1 = 138;
		int x2 = 1262;
		int y2 = 845;
		for (int x = x1; x <= x2; x += Util.SIZE) {

			for (int y = y1; y <= y2; y += Util.SIZE) {

				for (Pieza pieza : listaPiezas) {
					if (pieza.getColor() == color && pieza.puedeMoverseA(x, y)){
						
						return true; 
					}
				}
			}
		}
		return false;

	}

	public boolean comprobarAliadoReyUnaCasilla(Pieza rey, double x, double y) {

		for (Pieza pieza : listaPiezas) {

			if (pieza.getColor() == rey.getColor() && pieza.getPosX() == x && pieza.getPosY() == y) {

				return false;
			}
		}
		return true;
	}

	public void spawnEleccion() {
		ChessController.mostrarEleccio();

	}

	public void transformacionPeon(Pieza peon) {
		new Thread(() -> {

			ArrayList<Pieza> piezasAux = new ArrayList<Pieza>(listaPiezas);

			while (eleccion == "") {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Platform.runLater(() -> {
				Pieza newPieza2 = null;
				for (Pieza pieza : piezasAux) {
					if (peon == pieza) {
						switch (eleccion) {
						case "Dama":
							newPieza2 = new Dama(peon.getColor(), new Posicion(peon.getPosX(), peon.getPosY()), this);
							break;
						case "Alfil":
							newPieza2 = new Alfil(peon.getColor(), new Posicion(peon.getPosX(), peon.getPosY()), this);
							break;
						case "Caballo":
							newPieza2 = new Caballo(peon.getColor(), new Posicion(peon.getPosX(), peon.getPosY()),
									this);
							break;
						case "Torre":
							newPieza2 = new Torre(peon.getColor(), new Posicion(peon.getPosX(), peon.getPosY()), this);
							break;
						default:
							break;
						}
						break;
					}
				}
				listaPiezas.add(newPieza2);
				peon.añadirPieza(newPieza2);
				listaPiezas.remove(peon);
				peon.borrarPieza();
				if (detectarJaques(newPieza2.getPosX(), newPieza2.getPosY(), newPieza2)) {
					newPieza2.setHaceJaque(true);
					if (atacandoOpcionesRey(
							encontrarPieza("Rey", newPieza2.getColor() == Color.B ? Color.N : Color.B))) {
						jaqueMateAlert(peon.getColor() == Color.B ? "Blanco":"Negro");
					} else {
						jaqueAlert();
					}
				} else {
					newPieza2.setHaceJaque(false);
				}
				ChessController.amagarEleccio();
				eleccion = "";
			});
		}).start();
	}

}
