package modelo;

import java.util.ArrayList;
import java.util.Iterator;

import controlador.ChessController;
import controlador.ToastController;
import enumerados.Color;
import utilidades.Util;

public class Tablero {

	private static ArrayList<Pieza> listaPiezas;

	public Tablero() {

		listaPiezas = new ArrayList<Pieza>();
		crearTablero();
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

			peonNegro = new Peon(Color.N, x, 239, this);
			peonBlanco = new Peon(Color.B, x, 744, this);

			switch (i) {
			case 0, 7:
				piezaBlanca = new Torre(Color.B, x, 845, this);
			piezaNegra = new Torre(Color.N, x, 138, this);
			break;
			case 1, 6:
				piezaBlanca = new Caballo(Color.B, x, 845, this);
			piezaNegra = new Caballo(Color.N, x, 138, this);

			break;
			case 2, 5:
				piezaBlanca = new Alfil(Color.B, x, 845, this);
			piezaNegra = new Alfil(Color.N, x, 138, this);

			break;
			case 3:
				piezaBlanca = new Dama(Color.B, x, 845, this);
				piezaNegra = new Dama(Color.N, x, 138, this);

				break;
			case 4:
				piezaBlanca = new Rey(Color.B, x, 845, this);
				piezaNegra = new Rey(Color.N, x, 138, this);

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

	public static boolean detectarColision(double x, double y, Pieza piezaOrigen) {
		boolean valor = true;
		for (Iterator<Pieza> iterator = listaPiezas.iterator(); iterator.hasNext();) {
			Pieza pieza = iterator.next();
			double posX = pieza.getPosX();
			double posY = pieza.getPosY();

			// Comprobaciones Peon
			if (piezaOrigen.getNombre().equals("Peon")) {

				if (x == posX && y == posY && piezaOrigen.getPosX() == x) {
					// No mover si tiene delante otra pieza
					valor = false;
					break;
				} else if (piezaOrigen.getPosX() != x) {

					valor = false;
				}

			} else {

				// Comprobaciones para Alfil, Torre, Dama y Rey
				if (piezaOrigen.getNombre().equals("Alfil")
						&& piezaEnMedioDiagonales(piezaOrigen.getPosX(), piezaOrigen.getPosY(), x, y, piezaOrigen)) {
					valor = false;
					break;
				} else if (piezaOrigen.getNombre().equals("Torre")
						&& piezaEnMedioRecta(piezaOrigen.getPosX(), piezaOrigen.getPosY(), x, y, piezaOrigen)) {
					valor = false;
					break;
				} else if ((piezaOrigen.getNombre().equals("Dama") || piezaOrigen.getNombre().equals("Rey"))
						&& (piezaEnMedioRecta(piezaOrigen.getPosX(), piezaOrigen.getPosY(), x, y, piezaOrigen)
								|| piezaEnMedioDiagonales(piezaOrigen.getPosX(), piezaOrigen.getPosY(), x, y,
										piezaOrigen))) {
					valor = false;
					break;
				}

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
		if (detectarJaques(x, y, piezaOrigen)) {

			jaqueAlert();
			valor = true;
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

	public static boolean detectarJaques(double x, double y, Pieza piezaOrigen) {

		return ((piezaOrigen.getNombre().equals("Peon")) && jaquePeon(x, y, piezaOrigen.getColor())
				|| (piezaOrigen.getNombre().equals("Alfil") && jaqueDiagonal(x, y, piezaOrigen.getColor(), piezaOrigen))
				|| (piezaOrigen.getNombre().equals("Torre") && jaqueRecta(x, y, piezaOrigen.getColor(), piezaOrigen))
				|| ((piezaOrigen.getNombre().equals("Dama") || piezaOrigen.getNombre().equals("Rey"))
						&& (jaqueRecta(x, y, piezaOrigen.getColor(), piezaOrigen)
								|| jaqueDiagonal(x, y, piezaOrigen.getColor(), piezaOrigen)))
				|| (piezaOrigen.getNombre().equals("Caballo") && jaqueCaballo(x, y, piezaOrigen.getColor())));

	}

	public static boolean jaquePeon(double xOrigen, double yOrigen, Color c) {

		double x = calcularXReyRival(c);
		double y = calcularYReyRival(c);

		if (c == Color.B && Math.abs(x - xOrigen) == Util.SIZE && y == yOrigen - Util.SIZE) {
			return true;
		} else if (c == Color.N && Math.abs(x - xOrigen) == Util.SIZE && y == yOrigen + Util.SIZE) {
			return true;
		}

		return false;
	}

	public static boolean jaqueDiagonal(double xOrigen, double yOrigen, Color c, Pieza piezaActual) {

		double x = calcularXReyRival(c);
		double y = calcularYReyRival(c);

		if (!piezaEnMedioDiagonales(xOrigen, yOrigen, x, y, piezaActual)) {
			return Math.abs(x - xOrigen) == Math.abs(y - yOrigen);
		}

		return false;
	}

	public static boolean jaqueCaballo(double xOrigen, double yOrigen, Color c) {

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

	public static boolean jaqueRecta(double xOrigen, double yOrigen, Color c, Pieza piezaActual) {
		double x = calcularXReyRival(c);
		double y = calcularYReyRival(c);

		if (!piezaEnMedioRecta(xOrigen, yOrigen, x, y, piezaActual)) {

			return x == xOrigen || y == yOrigen;
		}

		return false;

	}

	public static boolean piezaEnMedioRecta(double xOrigen, double yOrigen, double xDestino, double yDestino,
			Pieza piezaActual) {
		double diferenciaX = xDestino - xOrigen;
		double diferenciaY = yDestino - yOrigen;

		if (diferenciaX == 0 || diferenciaY == 0) {
			if (diferenciaY == 0) {
				for (Pieza pieza : listaPiezas) {
					if (pieza.getPosY() == yOrigen && pieza.getPosY() > Math.min(xOrigen, xDestino)
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

	public static boolean piezaEnMedioDiagonales(double xOrigen, double yOrigen, double xDestino, double yDestino,
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

	public static double calcularXReyRival(Color c) {

		double x = 0;
		for (Pieza pieza : listaPiezas) {

			if (!pieza.getColor().equals(c) && pieza.getNombre().equals("Rey")) {
				x = pieza.getPosX();
				break;
			}
		}
		return x;
	}

	public static double calcularYReyRival(Color c) {

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

		return esReyYTorresValidosParaEnroque(rey, torre) && 
				!hayPiezasEntre(reyX, reyY, torreX, torreY) &&
				!estanCasillasBajoAtaque(reyX, reyY, calcularPosicionX(xTorreMenor), reyY, color);

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

	private boolean estanCasillasBajoAtaque(int x1, int y1, int x2, int y2, Color color) {
		int xRey = x1;
		int tmp = Math.max(x1, x2);
		x1 = Math.min(x1, x2);
		x2 = tmp;
		for (int x = x1; x <= x2; x += Util.SIZE) {
			setReyPos(x, y1, color);
			if (casillaBajoAtaque(x, y1, color)) {
				setReyPos(xRey, y1, color);
				return true;
			}
		}
		setReyPos(x1, y1, color);
		return false;
	}

	public boolean casillaBajoAtaque(double x, double y, Color color) {
		for (Pieza pieza : listaPiezas) {
			if (pieza.getColor() != color) {
				if (pieza.movimientoPermitido(x, y, false) && 
						detectarJaques(pieza.getPosX(), pieza.getPosY(), pieza)
						&& comprobarColision(x, y, pieza)) {

					System.out.println("x: " + x + " y: " + y);
					return true; // Si la pieza puede moverse a (x, y), la casilla está bajo ataque
				}
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
	}

	public void realizarEnroqueCorto(Color color) {
		realizarEnroque(color, 7, 5, 4, 6);
	}

	public void realizarEnroqueLargo(Color color) {
		realizarEnroque(color, 0, 3, 4, 2);
	}

	public static void jaqueMateAlert() {

		ToastController.showToast(ToastController.TOAST_ERROR, ChessController.enviarControl(), "JAQUE MATE");

	}

	public static void jaqueAlert() {

		ToastController.showToast(ToastController.TOAST_WARN, ChessController.enviarControl(), "JAQUE");

	}
	
	public static void setReyPos(double x, double y, Color color) {
		
		for (Pieza pieza : listaPiezas) {
			if (pieza.getNombre().equals("Rey") && pieza.getColor().equals(color)) {
				
				System.out.println("Antes: " + pieza.getPosX());
				pieza.setPosX(x);
				pieza.setPosY(y);
				System.out.println("Despues: " + pieza.getPosX());
				break;
			}
		}
	}

}
