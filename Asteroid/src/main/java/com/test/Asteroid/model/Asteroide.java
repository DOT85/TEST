package com.test.Asteroid.model;

public class Asteroide implements Comparable<Asteroide>{

	private String nombre;
	private Double diametro;
	private String planeta;
	private String fecha;
	private String velocidad;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getDiametro() {
		return diametro;
	}

	public void setDiametro(Double diametro) {
		this.diametro = diametro;
	}

	public String getPlaneta() {
		return planeta;
	}

	public void setPlaneta(String planeta) {
		this.planeta = planeta;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getVelocidad() {
		return velocidad;
	}

	public void setVelocidad(String velocidad) {
		this.velocidad = velocidad;
	}

	@Override
	public int compareTo(Asteroide asteroide) {
		// TODO Auto-generated method stub
		
		if (diametro < asteroide.diametro) {
			return 1;
        }
        if (diametro > asteroide.diametro) {
            
            return -1;
        }	
		
		return 0;
	}

	@Override
	public String toString() {
		return "Asteroide [nombre=" + nombre + ", diametro=" + diametro + ", planeta=" + planeta + ", fecha=" + fecha
				+ ", velocidad=" + velocidad + "]";
	}
	
	
	
}
