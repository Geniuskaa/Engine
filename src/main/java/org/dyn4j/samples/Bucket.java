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

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.samples.framework.input.BooleanStateKeyboardInputHandler;

import java.awt.*;
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

	private final BooleanStateKeyboardInputHandler f;

	/**
	 * Default constructor.
	 */
	public Bucket() {
		super("Bucket", 24.0);

		this.f = new BooleanStateKeyboardInputHandler(this.canvas, KeyEvent.VK_F);
		this.f.install();
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
	    // Bottom
		SimulationBody bucketBottom = new SimulationBody();
		bucketBottom.addFixture(Geometry.createRectangle(16.0, 0.5)); // ширина нижнего пола
	    bucketBottom.setMass(MassType.INFINITE);
		bucketBottom.translate(0.0, -3.0);
	    world.addBody(bucketBottom);

	    // Left-Side
	    SimulationBody bucketLeft = new SimulationBody();
	    bucketLeft.addFixture(Geometry.createRectangle(0.5, 15.0));
	    bucketLeft.translate(new Vector2(-7.5, 7.0));
	    bucketLeft.setMass(MassType.INFINITE);
	    world.addBody(bucketLeft);

	    // Right-Side
	    SimulationBody bucketRight = new SimulationBody();
	    bucketRight.addFixture(Geometry.createRectangle(0.5, 15.0));
	    bucketRight.translate(new Vector2(7.5, 7.0));
	    bucketRight.setMass(MassType.INFINITE);
	    world.addBody(bucketRight);



		// TODO: добавить аргументы в функицию генерации препятствий
		objectsGenerator();

		bucketsGenerator(-7.0, 11);





//		SimulationBody object1 = new SimulationBody();
//		BodyFixture fixture1 = object1.addFixture(Geometry.createRectangle(2.5, 0.3), 0.2);
//		fixture1.setFilter(ALL);
//		object1.setMass(MassType.INFINITE);
//		fixture1.getShape().rotate(Math.toDegrees(60));
//		object1.translate(1.0, 5.5);
//		world.addBody(object1);
//
//		SimulationBody object2 = new SimulationBody();
//		BodyFixture fixture2 = object2.addFixture(Geometry.createRectangle(2.5, 0.3), 0.2);
//		fixture2.setFilter(ALL);
//		object2.setMass(MassType.INFINITE);
//		fixture2.getShape().rotate(Math.toDegrees(-18));
//		object2.translate(-2.5, 3.0);
//		world.addBody(object2);





		// Блок кода с генерацией щара
		final double size = 1; // диаметр шара

		Convex c = null;
		c = Geometry.createCircle(size * 0.5); // Создание шара

		double x = 0; // Координаты падения шара
		double y = 18; // Координаты падения шара

		SimulationBody b = new SimulationBody();
		b.addFixture(c);
		b.translate(new Vector2(x, y));
		b.setMass(MassType.NORMAL);
		world.addBody(b);

	}



	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Bucket simulation = new Bucket();
		simulation.run();
	}

	public void objectsGenerator(){
		Random r = new Random(new Date().getTime());
		double xmin = -7.0;
		double xmax = 7.0;
		double ymin = 0.5;
		double ymax = 12.0;

		for (int i=0; i < 20; i++) {
//			double size = r.nextDouble() * maxSize + minSize;

			int radius = r.nextInt(2);

			double xx = r.nextDouble() * xmax * 2 + xmin;
			double yy = r.nextDouble() * ymax + ymin;


			SimulationBody object = new SimulationBody();
			BodyFixture fixture = object.addFixture(Geometry.createRectangle(2.5, 0.3)); // Характеристика препятствий
			fixture.setFilter(ALL);
			object.setMass(MassType.INFINITE);
			fixture.getShape().rotate(Math.toDegrees(radius)); // Поворот в градусах препятствий
			object.translate(xx, yy); // Смешение препятствий относительно точки 0.0 ( пола )
			world.addBody(object);
		}
	}

	public void bucketsGenerator(double coordinateOfStartBuckets, int countOfBackets){
		int n = countOfBackets;
		double start = coordinateOfStartBuckets;
		final double topOfBucket = -0.7;
		final double bottomOfBucket = -1.7;

		while (n > 0){ // это генерация стаканов
			Vector2[] vectors = new Vector2[4];
			for (int j = 0; j < 4; j++) {
				switch (j) {
					case 0:
						vectors[j] = new Vector2(start, topOfBucket);
						start += 0.3;
						break;
					case 1:
						vectors[j] = new Vector2(start, bottomOfBucket);
						start += 0.7;
						break;
					case 2:
						vectors[j] = new Vector2(start, bottomOfBucket);
						start += 0.3;
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
