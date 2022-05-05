/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.samples;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateKeyboardInputHandler;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * A scene where we fill a "bucket" with shapes.
 * @author William Bittle
 * @since 4.1.1
 * @version 4.1.1
 */
public class Bucket extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -3837218136220591307L;

	private static final CategoryFilter ALL = new CategoryFilter(1, Long.MAX_VALUE);
	private static final CategoryFilter BALL = new CategoryFilter(2, Long.MAX_VALUE);
	private static final CategoryFilter PIN = new CategoryFilter(4, 1 | 2 | 8);
	private static final CategoryFilter NOT_BALL = new CategoryFilter(8, 1 | 4);

	private SimulationBody ball;

	/**
	 * Default constructor.
	 */
	public Bucket() {
		super("Bucket", 16.0);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {


//		this.world.setGravity(new Vector2(0, -3.0)); // Пока не нашел функцию изменения импулься шарика, поэтому пока можно изменять гравитацию
		// UPD: Нашел функцию для изменения начального импульса, тогда не придется менять гравитацию по умолчанию будет земной

		// Bottom
		SimulationBody bucketBottom = new SimulationBody();
		bucketBottom.addFixture(Geometry.createRectangle(20.5, 0.5)); // ширина нижнего пола
	    bucketBottom.setMass(MassType.INFINITE);
		bucketBottom.translate(0.0, -2.0);
	    world.addBody(bucketBottom);

	    // Left-Side
	    SimulationBody bucketLeft = new SimulationBody();
	    bucketLeft.addFixture(Geometry.createRectangle(0.5, 18.0));
	    bucketLeft.translate(new Vector2(-11.0, 8.25));
	    bucketLeft.setMass(MassType.INFINITE);
	    world.addBody(bucketLeft);

	    // Right-Side
	    SimulationBody bucketRight = new SimulationBody();
	    bucketRight.addFixture(Geometry.createRectangle(0.5, 18.0));
	    bucketRight.translate(new Vector2(11.0, 8.25));
	    bucketRight.setMass(MassType.INFINITE);
	    world.addBody(bucketRight);



//		double[][] objects = randomGeneratorOfCoordinates(20);
		Vector2[][] vectors = randomGeneratorOfObjectVectors(20);

		// Версия с объектами с одинарной координатой
//		objectsGenerator(objects); // массив содержит в себе 3 значения: 1) X координата, 2) Y координата, 3) Угол поворота

		// Подходящая под условия Сергея версия
		objectsGeneratorBasedOnVectors(vectors);


		try {
			bucketsGenerator(7);
		}catch (IllegalArgumentException e){
			System.exit(1);
		}





		// Блок кода с генерацией щара
		final double size = 1; // диаметр шара

		Convex c = null;
		c = Geometry.createCircle(size * 0.5); // Создание шара

		double x = 0; // Координаты падения шара
		double y = 24; // Координаты падения шара

		ball = new SimulationBody();
		ball.addFixture(c);
		ball.translate(new Vector2(x, y));
		ball.setMass(MassType.NORMAL);
		world.addBody(ball);

		// Функция для изменения импульса шара, чем меньше значение аргумента product(), тем сильнее импульс вниз
		ball.applyForce(new Vector2(ball.getTransform().getRotationAngle() + Math.PI * 0.5).product(-2000));

	}


	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Bucket simulation = new Bucket();
		simulation.run();
	}

	private double[][] randomGeneratorOfCoordinates(int amountOfCoordinates){
		Random r = new Random(new Date().getTime());
		double xmin = -10.0;
		double xmax = 10.0;
		double ymin = 1.0;
		double ymax = 15.0;
		ArrayList<double[]> list = new ArrayList<double[]>();

		for (int i=0; i < amountOfCoordinates; i++) {
//			double size = r.nextDouble() * maxSize + minSize;

			int radius = r.nextInt(2);

			double x = r.nextDouble() * xmax * 2 + xmin;
			double y = r.nextDouble() * ymax + ymin;
			list.add(new double[]{x, y, radius});
		}

		double[][] objects = new double[list.size()][3];
		for (int i=0; i < objects.length; i++){
			objects[i] = list.get(i);
		}

		return objects;
	}

	private void objectsGenerator(double[][] objectsCoordinates){
			final double widthOfObject = 2.5;
			final double heightOfObject = 0.3;

			for (int i=0; i < objectsCoordinates.length; i++){
				SimulationBody object = new SimulationBody();
				BodyFixture fixture = object.addFixture(Geometry.createRectangle(widthOfObject, heightOfObject)); // Характеристика препятствий
				fixture.setFilter(ALL);
				object.setMass(MassType.INFINITE);
				fixture.getShape().rotate(Math.toDegrees(objectsCoordinates[i][2])); // Поворот в градусах препятствий
				object.translate(objectsCoordinates[i][0], objectsCoordinates[i][1]); // Смешение препятствий относительно точки 0.0 ( пола )
				world.addBody(object);

			}


	}

	private Vector2[][] randomGeneratorOfObjectVectors(int amountOfCoordinates) {
		Random r = new Random(new Date().getTime());
		double xmin = -10.0;
		double xmax = 10.0;
		double ymin = 1.0;
		double ymax = 15.0;

		ArrayList<Vector2[]> list = new ArrayList<Vector2[]>();

		for (int i=0; i < amountOfCoordinates; i++) {
			Vector2 firstVec;
			Vector2 secondVec;

				double x = r.nextDouble() * xmax * 2 + xmin;
				double y = r.nextDouble() * ymax + ymin;
				firstVec = new Vector2(x, y);

				double xX = r.nextDouble() * xmax * 2 + xmin;
				double yY = r.nextDouble() * ymax + ymin;
				secondVec = new Vector2(xX, yY);


			list.add(new Vector2[]{firstVec, secondVec});
		}

		Vector2[][] objects = new Vector2[list.size()][2];
		for (int i=0; i < objects.length; i++){
			objects[i] = list.get(i);
		}

		return objects;
	}

	private void objectsGeneratorBasedOnVectors(Vector2[][] vectors) {
		int size = vectors.length;

		for (int i=0; i < size; i++) {
			Vector2[] vec = new Vector2[2];
			vec[0] = vectors[i][0];
			vec[1] = vectors[i][1];

			List<Link> links = Geometry.createLinks(
					vec
					, false);

			SimulationBody floor = new SimulationBody();
			for (Link link : links) {
				floor.addFixture(link);
			}
			floor.setMass(MassType.INFINITE);
			this.world.addBody(floor);

		}
	}

	private void bucketsGenerator(int countOfBackets){
		final double coordinateOfStartBuckets = -10.75;
		final double coordinateOfEndBuckets = 10.75;
		final double minWidthOfBucket = 1.3;

		final double topOfBucket = -0.6;
		final double bottomOfBucket = -1.7;

		double widthOfBottom = coordinateOfEndBuckets - coordinateOfStartBuckets;
		double widthOfEachBucket = widthOfBottom/countOfBackets;

		if (widthOfEachBucket < minWidthOfBucket){ // Если стаканы слишком узкие - стаканы не сгенерируются
			throw new IllegalArgumentException();
		}

		int n = countOfBackets;
		double start = coordinateOfStartBuckets;

		while (n > 0){ // это генерация стаканов
			Vector2[] vectors = new Vector2[4];
			for (int j = 0; j < 4; j++) {
				switch (j) {
					case 0:
						vectors[j] = new Vector2(start, topOfBucket);
						start += widthOfEachBucket/4;
						break;
					case 1:
						vectors[j] = new Vector2(start, bottomOfBucket);
						start += widthOfEachBucket/2;
						break;
					case 2:
						vectors[j] = new Vector2(start, bottomOfBucket);
						start += widthOfEachBucket/4;
						break;
					case 3:
						vectors[j] = new Vector2(start, topOfBucket);
						break;
				}
			}

			List<Link> links = Geometry.createLinks(
					vectors
					, false);

			SimulationBody floor = new SimulationBody();
			for (Link link : links) {
				floor.addFixture(link);
			}
			floor.setMass(MassType.INFINITE);
			this.world.addBody(floor);

			n--;
		}
	}
}
