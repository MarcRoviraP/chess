package modelo;

import enumerados.Color;

public class Jugador {

	private String email;
	private String password;
	private String nickname;
	private int puntuation;
	private Color color;
	private boolean esTurno;
	
	   public Jugador(String nickname) {
	        this.nickname = nickname;
	        this.esTurno = this.color == Color.B;
	    }
	   public Jugador(String nickname, Color c) {
		   this.nickname = nickname;
		   this.color = c;
		   this.esTurno = this.color == Color.B;
	   }
	   
	   

	@Override
	public String toString() {
		return "Jugador [nickname=" + nickname + "]";
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPuntuation() {
		return puntuation;
	}

	public void setPuntuation(int puntuation) {
		this.puntuation = puntuation;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isEsTurno() {
		return esTurno;
	}

	public void setEsTurno(boolean esTurno) {
		this.esTurno = esTurno;
	}
	   
	   
}
