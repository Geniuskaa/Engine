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

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;

import java.io.*;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public static BodyFixture fixture;

	private static final Vector2 ballCoordinates = new Vector2(0.0,24.0);
//	private static SimulationBody ball;
	private static final Integer countOfSimulations = 11;
	private static SimulationBody[] balls;

	private static final ArrayList<Vector2[]> checkBox = new ArrayList<Vector2[]>();
	private static ArrayList<Integer> gameResult = new ArrayList<>();
	private static final Integer countOfbackets = 5;
	private static Result result;
	private static List<Result> results = new ArrayList<>();
	private static Integer checker = 0;
	private static final List<Result> resultsValidated = new ArrayList<>();

	private static String visualization; // "[{\"map\":{\"lines\":[{\"dot1\":{\"x\":-6.422906268704261,\"y\":14.730467016267111},\"dot2\":{\"x\":4.983574073054616,\"y\":2.84022009708447}},{\"dot1\":{\"x\":0.13738738992197064,\"y\":1.7828100595495957},\"dot2\":{\"x\":3.95880578573008,\"y\":1.0295292241098104}},{\"dot1\":{\"x\":8.848728339547986,\"y\":11.087171442435888},\"dot2\":{\"x\":7.625723589058946,\"y\":3.2907144977177216}}]},\"results\":5}," +
			//"{\"map\":{\"lines\":[{\"dot1\":{\"x\":-7.702907563656737,\"y\":9.437486973245552},\"dot2\":{\"x\":2.125304196710511,\"y\":14.1825206119878}},{\"dot1\":{\"x\":7.006990042938682,\"y\":2.6063984521670887},\"dot2\":{\"x\":5.611580272326142,\"y\":7.478777799288546}},{\"dot1\":{\"x\":3.4208826703505277,\"y\":1.1800640521780417},\"dot2\":{\"x\":-3.147120315459281,\"y\":2.6528756002044496}}]},\"results\":1}]";

	private static List<Result> outputList;
	private static final Integer BASE_INDENT = 25;
	private static final Integer BALL_DIAMETR = 1;
	/**
	 * Default constructor.
	 */
	public Bucket() {
		super("Bucket", 16.0);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */

	class Dot {
		Double x;
		Double y;

		public Dot(Double x, Double y) {
			this.x = x;
			this.y = y;
		}
	}

	class Line {
		Dot dot1;
		Dot dot2;

		public Line(Dot dot1, Dot dot2) {
			this.dot1 = dot1;
			this.dot2 = dot2;
		}
	}



	static class Map {
		List<Line> lines;
	}

	static class Result {
		Map map;
		Integer results;
	}


	protected void initializeWorld() {


		Path path = Paths.get("C:\\code\\Java\\AI project\\dyn4j-samples\\data.txt");

		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		visualization = line;

		java.lang.reflect.Type listOfMyClassObject = new TypeToken<ArrayList<Result>>() {}.getType();

		Gson gson = new Gson();
		outputList = gson.fromJson(visualization, listOfMyClassObject);

		balls = new SimulationBody[outputList.size()];


		SimulationBody b = new SimulationBody();
		b.addFixture(Geometry.createRectangle(28.5, 0.5)); // ширина нижнего пола
		b.setMass(MassType.INFINITE);
		b.translate(0.0, 30.0);
		world.addBody(b);

		// Left-Side
		SimulationBody bl = new SimulationBody();
		bl.addFixture(Geometry.createRectangle(0.5, 18.0));
		bl.translate(new Vector2(-13.0, 38.25));
		bl.setMass(MassType.INFINITE);
		world.addBody(bl);

		// Right-Side
		SimulationBody br = new SimulationBody();
		br.addFixture(Geometry.createRectangle(0.5, 18.0));
		br.translate(new Vector2(13.0, 38.25));
		br.setMass(MassType.INFINITE);
		world.addBody(br);


//		this.world.setGravity(new Vector2(0, -3.0)); // Пока не нашел функцию изменения импулься шарика, поэтому пока можно изменять гравитацию
		// UPD: Нашел функцию для изменения начального импульса, тогда не придется менять гравитацию по умолчанию будет земной


		for (int i=0; i<outputList.size(); i++){

			int indent = BASE_INDENT*i;
			// Bottom
			SimulationBody bucketBottom = new SimulationBody();
			bucketBottom.addFixture(Geometry.createRectangle(20.5, 0.5)); // ширина нижнего пола
			bucketBottom.setMass(MassType.INFINITE);
			bucketBottom.translate(0.0 + indent, -2.0);
			world.addBody(bucketBottom);

			// Left-Side
			SimulationBody bucketLeft = new SimulationBody();
			bucketLeft.addFixture(Geometry.createRectangle(0.5, 18.0));
			bucketLeft.translate(new Vector2(-11.0 + indent, 8.25));
			bucketLeft.setMass(MassType.INFINITE);
			world.addBody(bucketLeft);

			// Right-Side
			SimulationBody bucketRight = new SimulationBody();
			bucketRight.addFixture(Geometry.createRectangle(0.5, 18.0));
			bucketRight.translate(new Vector2(11.0 + indent, 8.25));
			bucketRight.setMass(MassType.INFINITE);
			world.addBody(bucketRight);

			SimulationBody leftHelper = new SimulationBody();
			leftHelper.addFixture(Geometry.createRectangle(0.3, 2.5));
			leftHelper.translate(new Vector2(-10.8 + indent, -0.7));
			leftHelper.setMass(MassType.INFINITE);
			world.addBody(leftHelper);

			SimulationBody rightHelper = new SimulationBody();
			rightHelper.addFixture(Geometry.createRectangle(0.3, 2.5));
			rightHelper.translate(new Vector2(10.8 + indent, -0.7));
			rightHelper.setMass(MassType.INFINITE);
			world.addBody(rightHelper);


			SimulationBody firstHelper = new SimulationBody();
			firstHelper.addFixture(Geometry.createRectangle(0.8, 1.5));
			firstHelper.translate(new Vector2(-6.5 + indent, -1.8));
			firstHelper.setMass(MassType.INFINITE);
			world.addBody(firstHelper);

			SimulationBody secondHelper = new SimulationBody();
			secondHelper.addFixture(Geometry.createRectangle(0.8, 1.5));
			secondHelper.translate(new Vector2(-2.2 + indent, -1.8));
			secondHelper.setMass(MassType.INFINITE);
			world.addBody(secondHelper);

			SimulationBody fourthHelper = new SimulationBody();
			fourthHelper.addFixture(Geometry.createRectangle(0.8, 1.5));
			fourthHelper.translate(new Vector2(2.1 + indent, -1.8));
			fourthHelper.setMass(MassType.INFINITE);
			world.addBody(fourthHelper);

			SimulationBody thirdHelper = new SimulationBody();
			thirdHelper.addFixture(Geometry.createRectangle(0.8, 1.5));
			thirdHelper.translate(new Vector2(6.4 + indent, -1.8));
			thirdHelper.setMass(MassType.INFINITE);
			world.addBody(thirdHelper);



//		double[][] objects = randomGeneratorOfCoordinates(20); генератор под закомиченную ф-цию

			// Версия с объектами с одинарной координатой
//		objectsGenerator(objects); // массив содержит в себе 3 значения: 1) X координата, 2) Y координата, 3) Угол поворота

			Vector2[][] vectors = new Vector2[3][2];
			for (int h=0; h < 3; h++){
				vectors[h][0] = new Vector2(outputList.get(i).map.lines.get(h).dot1.x, outputList.get(i).map.lines.get(h).dot1.y);
				vectors[h][1] = new Vector2(outputList.get(i).map.lines.get(h).dot2.x, outputList.get(i).map.lines.get(h).dot2.y);
			}
			// Подходящая под условия Сергея версия
			objectsGeneratorBasedOnVectors(vectors, indent);


			try {
				bucketsGenerator(countOfbackets, indent);
			}catch (IllegalArgumentException e){
				System.exit(1);
			}


			//эта версия с физикой имеет особую специфику (спросите Эмиля, прежде чем юзать)
//		ball = new SimulationBody();
//		fixture = new BodyFixture(Geometry.createCircle(0.5));
////		fixture.setDensity(194.82);
//		fixture.setRestitution(0.5);
//		fixture.setFilter(BALL);
//		ball.addFixture(fixture);
//		ball.setMass(MassType.NORMAL);
//		ball.translate(ballCoordinates);
//		this.world.addBody(ball);
			//



			// Блок кода с генерацией щара
			final double size = BALL_DIAMETR; // диаметр шара

			Convex c = null;
			c = Geometry.createCircle(size * 0.5); // Создание шара

			double x = 0 + (indent); // Координаты падения шара
			double y = 24; // Координаты падения шара

			balls[i] = new SimulationBody();
			balls[i].addFixture(c);
			balls[i].translate(new Vector2(x, y));
			balls[i].setMass(MassType.NORMAL);
			world.addBody(balls[i]);

//		 Функция для изменения импульса шара, чем меньше значение аргумента product(), тем сильнее импульс вниз
			balls[i].applyForce(new Vector2(balls[i].getTransform().getRotationAngle() + Math.PI * 0.5).product(-500));
		}

	}

	boolean isFinished = false;

	@Override
	protected void handleEvents() {
		super.handleEvents();


		for (int i = 0; i < outputList.size(); i++) {

			SimulationBody ball = balls[i];

			if (ball == null || isFinished) continue;

			if (ball.getWorldCenter().y < -1) {
				int n = checkBox.size();

				for (int j = 0; j < n; j++) {
					if (checkBox.get(j)[0].x <= ball.getWorldCenter().x && ball.getWorldCenter().x <= checkBox.get(j)[1].x) {
						Integer bucketNum = j % countOfbackets + 1;
						System.out.println("Мяч номер " +  (i+1) + " попал в стакан номер: " + bucketNum); // Здесь сделать вывод как и куда удобно

						ball.translate(-25, 10); // - BASE_INDENT*(j/countOfbackets)
						ball.setEnabled(false);
						checker++;

						//запись в файл координат объектов и результат мяча
						String data = String.valueOf(bucketNum);



						if (checker > outputList.size() * 0.95) {
							this.stop();
							isFinished = true;


							System.out.println("Finish");
						}
						gameResult.add(j + 1);
					}
				}
			}
		}
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

		Random r = new Random(); //new Date().getTime()

		double xmin = -9.0;
		double xmax = 9.0;
		double ymin = 1.0;
		double ymax = 15.0;

		ArrayList<Vector2[]> list = new ArrayList<Vector2[]>();


//		File log = new File("data.txt");
//
//		try{
//			if(!log.exists()){
//				System.out.println("We had to make a new file.");
//				log.createNewFile();
//			}
//
//			FileWriter fileWriter = new FileWriter(log, true);
//
//			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//			bufferedWriter.write("\n");
//			bufferedWriter.close();
//
//		} catch(IOException e) {
//			System.out.println("COULD NOT LOG!!");
//		}

		Result res = new Result();
		Map map = new Map();
		res.map = map;
		map.lines = new ArrayList<>();

		int j = amountOfCoordinates;
		while (j != 0){
			Vector2 firstVec;
			Vector2 secondVec;

			Double x = r.nextDouble() * xmax * 2 + xmin;
			Double y = r.nextDouble() * ymax + ymin;
			firstVec = new Vector2(x , y);

			Double xX = r.nextDouble() * xmax * 2 + xmin;
			Double yY = r.nextDouble() * ymax + ymin;
			secondVec = new Vector2(xX , yY);

			if(list.size() > 0){
				boolean everythingOkay = false;
				for(int i=0; i < list.size(); i++){
					Vector2[] line = list.get(i);

					if(firstVec.x < line[0].x && secondVec.x < line[0].x && (line[0].x - firstVec.x >= BALL_DIAMETR) && (line[0].x - secondVec.x >= BALL_DIAMETR)){
						everythingOkay = true;
					}else if(firstVec.x > line[1].x && secondVec.x > line[1].x && (firstVec.x - line[1].x >= BALL_DIAMETR) && (secondVec.x - line[1].x >= BALL_DIAMETR)){
						everythingOkay = true;
					}else if(firstVec.y < line[0].y && firstVec.y < line[1].y && secondVec.y < line[0].y && secondVec.y < line[1].y &&
							(line[0].y - firstVec.y >= BALL_DIAMETR) && (line[0].y - secondVec.y >= BALL_DIAMETR) &&
							(line[1].y - firstVec.y >= BALL_DIAMETR) && (line[1].y - secondVec.y >= BALL_DIAMETR)) {
						everythingOkay = true;
					}else if(firstVec.y > line[0].y && firstVec.y > line[1].y && secondVec.y > line[0].y && secondVec.y > line[1].y &&
							(firstVec.y - line[1].y >= BALL_DIAMETR) && (secondVec.y - line[1].y >= BALL_DIAMETR) &&
							(firstVec.y - line[0].y >= BALL_DIAMETR) && (secondVec.y - line[0].y >= BALL_DIAMETR)){
						everythingOkay = true;
					}else {
						everythingOkay = false;
						break;
					}
				}

				if(!everythingOkay){
//					j--;
//					try {
//						Thread.sleep(2);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					r = new Random(new Date().getTime());
					continue;
				}
			}



			if(firstVec.x > secondVec.x){
				map.lines.add(new Line(new Dot(x,y),new Dot(xX, yY)));
				list.add(new Vector2[]{secondVec, firstVec});
			}else {
				map.lines.add(new Line(new Dot(x,y),new Dot(xX, yY)));
				list.add(new Vector2[]{firstVec, secondVec});
			}

			j--;
		}


//		try{
//			if(!log.exists()){
//				System.out.println("We had to make a new file.");
//				log.createNewFile();
//			}
//
//			FileWriter fileWriter = new FileWriter(log, true);
//			Gson gson = new Gson();
//			String json = gson.toJson(m);
//
//			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//			bufferedWriter.write(json);
//			bufferedWriter.write("\n");
//			bufferedWriter.close();
//
//			System.out.println("Done");
//		} catch(IOException e) {
//			System.out.println("COULD NOT LOG!!");
//		}

		results.add(res);

		Vector2[][] objects = new Vector2[list.size()][2];
		for (int i=0; i < objects.length; i++){
			objects[i] = list.get(i);
		}

		return objects;
	}

	private void objectsGeneratorBasedOnVectors(Vector2[][] vectors, int coef) {
		int size = vectors.length;

		for (int i=0; i < size; i++) {
			Vector2[] vec = new Vector2[2];
			vec[0] = new Vector2(vectors[i][0].x + coef, vectors[i][0].y);
			vec[1] = new Vector2( vectors[i][1].x + coef,  vectors[i][1].y);

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

	private void bucketsGenerator(int countOfBackets, int coef){
		final double coordinateOfStartBuckets = -10.75 + coef;
		final double coordinateOfEndBuckets = 10.75 + coef;
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
			Vector2[] checkBoxCoordinates = new Vector2[2];

			for (int j = 0; j < 4; j++) {
				switch (j) {
					case 0:
						vectors[j] = new Vector2(start, topOfBucket);
						start += widthOfEachBucket/4;
						break;
					case 1:
						vectors[j] = new Vector2(start, bottomOfBucket);
						checkBoxCoordinates[0] = vectors[j];
						start += widthOfEachBucket/2;
						break;
					case 2:
						vectors[j] = new Vector2(start, bottomOfBucket);
						checkBoxCoordinates[1] = vectors[j];
						start += widthOfEachBucket/4;
						break;
					case 3:
						vectors[j] = new Vector2(start, topOfBucket);
						break;
				}
			}

			checkBox.add(checkBoxCoordinates);

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
