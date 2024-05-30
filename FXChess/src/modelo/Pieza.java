package modelo;

import enumerados.Color;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utilidades.Util;

public class Pieza extends ImageView {
	private Color color;
	private int valor;
	private double posX;
	private double posY;
	private String nombre;
	private boolean movida;
	private Tablero tablero;

	public Pieza(Color color, String nombre, int valor, int posX, int posY, Tablero tablero) {
		this.color = color;
		this.valor = valor;
		this.posX = posX;
		this.posY = posY;
		this.nombre = nombre;
		this.movida = false;
		this.tablero = tablero;

		String str_img = "../utilidades/img/Piezas/" + this.nombre + "_" + color + ".png";
		Image img = new Image(getClass().getResourceAsStream(str_img));
		this.setImage(img);

		// Asignar posición y tamaño
		this.setX(this.posX);
		this.setY(this.posY);
		this.setFitWidth(Util.SIZE);
		this.setFitHeight(Util.SIZE);

		// Asignar eventos DRAG and DROP
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, this::startMovingPiece);
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::movePiece);
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, this::finishMovingPiece);
	}

	public void startMovingPiece(MouseEvent evt) {

		// Cambiar opacidad para arrastrar figura
		this.setOpacity(0.6d);
	}

	public void movePiece(MouseEvent evt) {


		// Repintar la figura por el tablero
		Point2D mousePoint = new Point2D(evt.getX(), evt.getY());
		Point2D mousePoint_p = this.localToParent(mousePoint);

		this.relocate(mousePoint_p.getX() - (Util.SIZE / 2), mousePoint_p.getY() - (Util.SIZE / 2));
	}

	public void coordsFinishMovingPiece(double x, double y) {

		if (!(x >= 1352 || x <= 549 || y >= 947 || y <= 148)) {

			x = Math.round(x / 100);
			y = Math.round(y / 100);

			x *= Util.SIZE;
			y *= Util.SIZE;

			x -= 51;
			y -= 64;
			System.out.println("Inicio: X: " + posX + "Y: " + posY);
			System.out.println("Fin:    X: " + x + "Y: " + y);
			if (movimientoPermitido(x, y,true)) {

				if (Tablero.detectarColision(x, y, this)) {

					this.relocate(x, y);
					this.posX = x;
					this.posY = y;

					for (Pieza pieza : tablero.getListaPiezas()) {
						if (pieza.equals(this)) {
							pieza.setPosX(x);
							pieza.setPosY(y);
							break;
						}
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

	public void finishMovingPiece(MouseEvent evt) {

		coordsFinishMovingPiece(evt.getSceneX(), evt.getSceneY());
	}

	public void reinciarPosicion() {
		this.relocate(posX, posY);
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
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public void setHaSidoMovida() {

		this.movida = true;
	}

	public boolean getHaSidoMovida() {
		return movida;
	}

	@Override
	public String toString() {
		return color + "" + valor + " primer movimiento " + movida  + " x: " + posX + " y: " + posY;
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
				valor = moviminetoRey(x, y);
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
		return (Math.abs(x - posX) == Util.SIZE && Math.abs(y - posY) == Util.SIZE * 2)
				|| (Math.abs(y - posY) == Util.SIZE && Math.abs(x - posX) == Util.SIZE * 2);
	}

	public boolean moviminetoRey(double x, double y) {

		if (movimientoTorre(x, y) || moviminetoAlfil(x, y)) {
			if (this.posX < x ) {
				
				// Intento de enroque corto
				if (tablero.esEnroqueCortoPosible(getColor()) && x == tablero.calcularPosicionX(6)) {
					tablero.realizarEnroqueCorto(getColor());
					return true;
				}
			} else {
				// Intento de enroque largo
				if (tablero.esEnroqueLargoPosible(getColor())&& x == tablero.calcularPosicionX(2)) {
					tablero.realizarEnroqueLargo(getColor());
					return true;
				}
			}
		}
		
		
		if (tablero.casillaBajoAtaque(x, y, color)) {
			
			return false;
		}
		
		
		
		return (movimientoTorre(x, y) || moviminetoAlfil(x, y))
				&& (Math.abs(x - this.posX) == Util.SIZE || Math.abs(y - this.posY) == Util.SIZE);
	}

	
	private boolean movimientoTorre(double x, double y) {
		return (x == this.posX && y != this.posY) || (y == this.posY && x != this.posX);
	}

	private boolean movimientoPeonBlanco(double x, double y) {
		return movimientoPeon(744, x, y, Color.B);
	}

	private boolean movimientoPeonNegro(double x, double y) {
		return movimientoPeon(239, x, y, Color.N);
	}

	private boolean movimientoPeon(double posicionInicial, double x, double y, Color color) {
		boolean movimientoValido = false;

		if (color == Color.B) {
			// Peón blanco
			if (this.posY == posicionInicial) {
				// Primer movimiento: una o dos casillas hacia adelante
				movimientoValido = (x == this.posX && (this.posY - y == Util.SIZE || this.posY - y == Util.SIZE * 2));
			} else {
				// Movimiento normal: una casilla hacia adelante
				movimientoValido = (x == this.posX && this.posY - y == Util.SIZE);
			}
			// Movimiento en diagonal para capturar
			movimientoValido = movimientoValido
					|| ((Math.abs(this.posX - x) == Util.SIZE) && (this.posY - y == Util.SIZE));
		} else if (color == Color.N) {
			// Peón negro
			if (this.posY == posicionInicial) {
				// Primer movimiento: una o dos casillas hacia adelante
				movimientoValido = (x == this.posX && (y - this.posY == Util.SIZE || y - this.posY == Util.SIZE * 2));
			} else {
				// Movimiento normal: una casilla hacia adelante
				movimientoValido = (x == this.posX && y - this.posY == Util.SIZE);
			}
			// Movimiento en diagonal para capturar
			movimientoValido = movimientoValido
					|| ((Math.abs(this.posX - x) == Util.SIZE) && (y - this.posY == Util.SIZE));
		}

		return movimientoValido;
	}

	private boolean moviminetoAlfil(double x, double y) {
		return Math.abs(x - this.posX) == Math.abs(y - this.posY);
	}

	public void borrarPieza() {
		if (this.getParent() != null) {
			((AnchorPane) this.getParent()).getChildren().remove(this);
		}
	}

}
