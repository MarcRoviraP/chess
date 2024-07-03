package modelo;

import controlador.ChessController;
import enumerados.Color;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utilidades.Util;

public class Pieza extends ImageView {
	private Color color;
	private int valor;
	private Posicion posicion;
	private String nombre;
	private boolean movida;
	private boolean haceJaque;
	private Tablero tablero;
	private int numMovimientos;

	public Pieza(Color color, String nombre, int valor, Posicion pos, Tablero tablero) {
		this.color = color;
		this.valor = valor;
		this.posicion = pos;
		this.nombre = nombre;
		this.movida = false;
		this.tablero = tablero;
		this.numMovimientos = 0;
		this.haceJaque = false;
		String str_img = "../utilidades/img/Piezas/" + this.nombre + "_" + color + ".png";
		Image img = new Image(getClass().getResourceAsStream(str_img));
		this.setImage(img);

		this.setCursor(Cursor.OPEN_HAND);

		// Asignar posición y tamaño
		this.setX(this.posicion.getX());
		this.setY(this.posicion.getY());
		this.setFitWidth(Util.SIZE);
		this.setFitHeight(Util.SIZE);

		// Asignar eventos DRAG and DROP
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, this::startMovingPiece);
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::movePiece);
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, this::finishMovingPiece);
	}

	public void startMovingPiece(MouseEvent evt) {

		// Cambiar opacidad para arrastrar figura
		this.setCursor(Cursor.CLOSED_HAND);
		this.setOpacity(0.6d);
	}

	public void movePiece(MouseEvent evt) {

		// Repintar la figura por el tablero
		Point2D mousePoint = new Point2D(evt.getX(), evt.getY());
		Point2D mousePoint_p = this.localToParent(mousePoint);

		this.relocate(mousePoint_p.getX() - (Util.SIZE / 2), mousePoint_p.getY() - (Util.SIZE / 2));
	}

	public void coordsFinishMovingPiece(double x, double y) {
		this.setCursor(Cursor.OPEN_HAND);
		tablero.setBackup(null);
		if (!(x >= 1352 || x <= 549 || y >= 947 || y <= 148)) {

			double tmpX = this.posicion.getX();
			double tmpY = this.posicion.getY();
			x = Math.round(x / 100);
			y = Math.round(y / 100);

			x *= Util.SIZE;
			y *= Util.SIZE;

			x -= 51;
			y -= 64;
			System.out.println("Inicio: X: " + posicion.getX() + "Y: " + posicion.getY());
			System.out.println("Fin:    X: " + x + "Y: " + y);

			// Verificar si es el turno del jugador correspondiente
			if ((color == Color.B && tablero.getJugadorBlanco().isEsTurno())
					|| (color == Color.N && tablero.getJugadorNegro().isEsTurno())) {

				if (movimientoPermitido(x, y, true)) {
					ChessController.borrarRojo();

					if (tablero.detectarColision(x, y, this, true)) {

						this.relocate(x, y);
						this.posicion.setX(x);
						this.posicion.setY(y);

						for (Pieza pieza : tablero.getListaPiezas()) {
							if (pieza.equals(this)) {
								pieza.setPosX(x);
								pieza.setPosY(y);
								break;
							}
						}

						tablero.vaciarJaque(color);

						if (tablero.detectarJaques(x, y, this)) {
							haceJaque = true;
							if (tablero.atacandoOpcionesRey(
									tablero.encontrarPieza("Rey", color == Color.B ? Color.N : Color.B))) {
								tablero.jaqueMateAlert(color == Color.B ? "Blanco" : "Negro");
							} else {
								tablero.jaqueAlert();
							}
						} else {
							haceJaque = false;
						}

						if (tablero.detectarJaquesGeneral(this.color)) {
							for (Pieza pieza : tablero.getListaPiezas()) {
								if (pieza.equals(this)) {
									pieza.setPosX(tmpX);
									pieza.setPosY(tmpY);
									break;
								}
							}
							Pieza p = tablero.getBackup();

							if (p != null) {
								tablero.listaPiezas.add(p);
								this.añadirPieza(p);
							}

							// Borra el backup
							this.numMovimientos--;
							reinciarPosicion();
							cambiarTurno();
						}
						if (nombre.equals("Peon")) {

							if ((color == Color.B && this.getPosY() == 138)
									|| (color == Color.N && this.getPosY() == 845)) {
								tablero.spawnEleccion();
								tablero.transformacionPeon(this);
							}
						}
						this.numMovimientos++;
						// Cambiar el turno después de un movimiento exitoso
						cambiarTurno();
					} else {
						reinciarPosicion();
					}

				} else {
					reinciarPosicion();
				}
			} else {
				reinciarPosicion();
			}
		} else {
			reinciarPosicion();
		}

		// Volver a la opacidad original al soltar la figura
		this.setOpacity(1.0d);
	}

	public boolean puedeMoverseA(double x, double y) {

		boolean valor = false;
		if (!(x >= 1352 || x <= 549 || y >= 947 || y <= 148)) {

			if ((color == Color.B && tablero.getJugadorBlanco().isEsTurno())
					|| (color == Color.N && tablero.getJugadorNegro().isEsTurno())) {

				if (movimientoPermitido(x, y, true)) {

					valor = true;
					if (tablero.detectarColision(x, y, this, true)) {
						valor = true;

						if (tablero.detectarJaques(x, y, this)) {
							valor = true;

							if (tablero.detectarJaquesGeneral(this.color)) {
								valor = true;
							}
						}
					}
				}
			}
		}
		return valor;

	}

	// Método para cambiar el turno de los jugadores
	public void cambiarTurno() {
		if (tablero.getJugadorBlanco().isEsTurno()) {
			tablero.getJugadorBlanco().setEsTurno(false);
			tablero.getJugadorNegro().setEsTurno(true);
		} else {
			tablero.getJugadorBlanco().setEsTurno(true);
			tablero.getJugadorNegro().setEsTurno(false);
		}
	}

	public void vaciarJaque() {
		this.haceJaque = false;
	}

	public void finishMovingPiece(MouseEvent evt) {

		coordsFinishMovingPiece(evt.getSceneX(), evt.getSceneY());
	}

	public void reinciarPosicion() {
		this.relocate(posicion.getX(), posicion.getY());
	}

	public Color getColor() {
		return color;
	}

	public int getValor() {
		return valor;
	}

	public String getNombre() {
		return nombre;
	}

	public double getPosX() {
		return posicion.getX();
	}

	public void setPosX(double posX) {
		this.posicion.setX(posX);
	}

	public double getPosY() {
		return posicion.getY();
	}

	public void setPosY(double posY) {
		this.posicion.setY(posY);
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setPosX(int posX) {
		this.posicion.setX(posX);
	}

	public void setPosY(int posY) {
		this.posicion.setY(posY);

	}

	public void setHaSidoMovida() {

		this.movida = true;
	}

	public boolean getHaSidoMovida() {
		return movida;
	}

	public boolean getHaceJaque() {

		return haceJaque;
	}

	public void setHaceJaque(boolean valor) {

		haceJaque = valor;
	}

	@Override
	public String toString() {
		return color + "" + valor + " primer movimiento " + movida + " x: " + posicion.getX() + " y: "
				+ posicion.getY();
	}

	public boolean movimientoPermitido(double x, double y, boolean rey) {

		boolean valor = false;

		switch (this.nombre) {
		case "Peon":

			// Movimiento permitido Peon Blanco
			if (this.color == Color.B) {

				valor = movimientoPeonBlanco(x, y);
			}
			// Movimiento permitido Peon Negro
			if (this.color == Color.N) {

				valor = movimientoPeonNegro(x, y);
			}
			break;

		case "Alfil":

			// Movimiento permitido Alfil
			valor = moviminetoAlfil(x, y);
			break;

		case "Caballo":

			// Movimiento permitido Caballo
			valor = movimientoCaballo(x, y);
			break;

		case "Torre":

			// Movimiento permitido Torre
			valor = movimientoTorre(x, y);
			break;

		case "Dama":

			// Movimiento permitido Dama
			valor = movimientoTorre(x, y) || moviminetoAlfil(x, y);
			break;

		case "Rey":

			if (rey) {

				// Movimiento permitido Rey
				valor = moviminetoRey(x, y, false);
			}
			break;
		default:
			break;
		}

		if (valor) {
			this.movida = true;
		}
		return valor;
	}

	private boolean movimientoCaballo(double x, double y) {
		if (x == getPosX() && y == getPosY()) {
			return false;
		}
		return (Math.abs(x - posicion.getX()) == Util.SIZE && Math.abs(y - posicion.getY()) == Util.SIZE * 2)
				|| (Math.abs(y - posicion.getY()) == Util.SIZE && Math.abs(x - posicion.getX()) == Util.SIZE * 2);
	}

		public boolean puedeReyMoverse(double x, double y) {
			
			
			if (tablero.jaqueRey(x, y, this)) {
				return false;
			}
			
			if (!tablero.comprobarAliadoReyUnaCasilla(this, x, y)) {
				return false;
			}
			if (tablero.estanCasillasBajoAtaque(x, y, x, y, color)) {
				
				return tablero.puedeAccederCasilla(this);
			}
			
			return (movimientoTorre(x, y) || moviminetoAlfil(x, y))
					&& (Math.abs(x - this.posicion.getX()) == Util.SIZE || Math.abs(y - this.posicion.getY()) == Util.SIZE);
		}
//	public boolean puedeReyMoverse(double x, double y) {
//
//		if (tablero.jaqueRey(x, y, this)) {
//			return false;
//		}
//
//		if (!tablero.comprobarAliadoReyUnaCasilla(this, x, y)) {
//			return false;
//		}
//		if (tablero.estanCasillasBajoAtaque(x, y, x, y, color)) {
//
//			if (!tablero.puedeAliadoAccederAAlgunaCasilla(color)) {
//
//				return tablero.puedeAccederCasilla(this);
//			}
//		}
//
//		return (movimientoTorre(x, y) || moviminetoAlfil(x, y))
//				&& (Math.abs(x - this.posicion.getX()) == Util.SIZE || Math.abs(y - this.posicion.getY()) == Util.SIZE);
//	}

	public boolean moviminetoRey(double x, double y, boolean posicionesValidas) {

		if (movimientoTorre(x, y) || moviminetoAlfil(x, y)) {
			if (this.posicion.getX() < x) {

				// Intento de enroque corto
				if (tablero.esEnroqueCortoPosible(getColor()) && x == tablero.calcularPosicionX(6)) {
					tablero.realizarEnroqueCorto(getColor());
					return true;
				}
			} else {
				// Intento de enroque largo
				if (tablero.esEnroqueLargoPosible(getColor()) && x == tablero.calcularPosicionX(2)) {
					tablero.realizarEnroqueLargo(getColor());
					return true;
				}
			}
		}

		if (tablero.jaqueRey(x, y, this)) {

			return false;
		}

		if (tablero.estanCasillasBajoAtaque(x, y, x, y, color)) {

			return false;
		}

		return (movimientoTorre(x, y) || moviminetoAlfil(x, y))
				&& (Math.abs(x - this.posicion.getX()) == Util.SIZE || Math.abs(y - this.posicion.getY()) == Util.SIZE);
	}

	private boolean movimientoTorre(double x, double y) {

		if (x == getPosX() && y == getPosY()) {
			return false;
		}
		return (x == this.posicion.getX() && y != this.posicion.getY())
				|| (y == this.posicion.getY() && x != this.posicion.getX());
	}

	private boolean movimientoPeonBlanco(double x, double y) {
		return movimientoPeon(744, x, y, Color.B);
	}

	private boolean movimientoPeonNegro(double x, double y) {
		return movimientoPeon(239, x, y, Color.N);
	}

	private boolean movimientoPeon(double posicionInicial, double x, double y, Color color) {
		boolean movimientoValido = false;

		if (x == getPosX() && y == getPosY()) {
			return false;
		}
		if (color == Color.B) {
			// Peón blanco
			if (this.posicion.getY() == posicionInicial) {
				// Primer movimiento: una o dos casillas hacia adelante
				movimientoValido = (x == this.posicion.getX()
						&& (this.posicion.getY() - y == Util.SIZE || this.posicion.getY() - y == Util.SIZE * 2));
			} else {
				// Movimiento normal: una casilla hacia adelante
				movimientoValido = (x == this.posicion.getX() && this.posicion.getY() - y == Util.SIZE);
			}
			// Movimiento en diagonal para capturar
			movimientoValido = movimientoValido
					|| ((Math.abs(this.posicion.getX() - x) == Util.SIZE) && (this.posicion.getY() - y == Util.SIZE));
		} else if (color == Color.N) {
			// Peón negro
			if (this.posicion.getY() == posicionInicial) {
				// Primer movimiento: una o dos casillas hacia adelante
				movimientoValido = (x == this.posicion.getX()
						&& (y - this.posicion.getY() == Util.SIZE || y - this.posicion.getY() == Util.SIZE * 2));
			} else {
				// Movimiento normal: una casilla hacia adelante
				movimientoValido = (x == this.posicion.getX() && y - this.posicion.getY() == Util.SIZE);
			}
			// Movimiento en diagonal para capturar
			movimientoValido = movimientoValido
					|| ((Math.abs(this.posicion.getX() - x) == Util.SIZE) && (y - this.posicion.getY() == Util.SIZE));
		}

		return movimientoValido;
	}

	private boolean moviminetoAlfil(double x, double y) {

		if (x == getPosX() && y == getPosY()) {
			return false;
		}
		return Math.abs(x - this.posicion.getX()) == Math.abs(y - this.posicion.getY());
	}

	public void borrarPieza() {
		tablero.setBackup(this);
		if (this.getParent() != null) {
			((AnchorPane) this.getParent()).getChildren().remove(this);
		}
	}

	public void añadirPieza(Pieza pieza) {
		if (this.getParent() != null) {
			((AnchorPane) this.getParent()).getChildren().add(pieza);
		}
	}

}
