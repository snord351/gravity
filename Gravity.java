/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gravity;

import static java.lang.Math.sqrt;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author snord
 */
public class Gravity extends Application {

    int width = 600;
    int height = 600;
    int squareSize = 40;
    int offset = 0;
    Canvas canvas = new Canvas(width, height);
    ArrayList<Star> stars = new ArrayList<Star>();
    Planet planet = new Planet(width/2, height / 2, 700);
    boolean following = false;
    int frames = 0;
    boolean noGravity = false;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setCenter(canvas);

        Scene scene = new Scene(root, width * 1.3, height);
        Button follow = new Button("Follow");
        Button gravButton = new Button("Turn On Gravity");
        primaryStage.setScene(scene);
        drawCanvas();
        VBox vbox = new VBox();
        vbox.getChildren().addAll(follow, gravButton);
        root.setRight(vbox);
        primaryStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawCanvas();
                if (noGravity) {
                    gravityPull();
                }
                if (frames % 20== 0) {
                  // orbit();
                }
                frames++;
            }
        }.start();

        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("X: " + event.getX() + " Y: " + event.getY());
                stars.add(new Star(event.getX(), event.getY(), 50));
            }
        });

        gravButton.setOnAction(event
                -> {
            if (noGravity) {
                gravButton.setText("Turn On Gravity");
                noGravity = false;

            } else {
                noGravity = true;
                gravButton.setText("Gravity Off");
            }
        });
        follow.setOnAction(event
                -> {
            if (following) {
                follow.setText("Follow");
                following = false;
                planet.x = width / 2;
                planet.y = height / 2;
            } else {
                following = true;
                follow.setText("Dont Follow");
            }
        });

        canvas.setOnMouseMoved(event
                -> {
            if (following == true) {
                planet.x = event.getX();
                planet.y = event.getY();
            }
        });

    }

    public void orbit() {
          double velocity = 0;
        if (stars.size() > 0) {
            for (int i = 0; i < stars.size(); i++)
            {
                double distancex = stars.get(i).x - planet.x;
                double distancey = stars.get(i).y - planet.y;

                float angle = (float) Math.toDegrees(Math.atan2(distancey, distancex));

                System.out.println("XDIFF:" + distancex + " YDIFF:" + distancey+" ANGLE: "+ angle);

                double distanceTotal = sqrt(Math.pow(distancex, 2) + Math.pow(distancey, 2));
                angle += velocity;
                System.out.println("StarX: "+stars.get(i).x+" StarY: "+ stars.get(i).y+" ANGLE: "+ angle+" Distance:"+ distanceTotal );
                stars.get(i).x = planet.x + distanceTotal * Math.cos(angle);
                stars.get(i).y = planet.y + distanceTotal * Math.sin(angle);
                
                 System.out.println("PX: "+planet.x+" PY: "+ planet.y);

            }
        }
    }

    public void gravityPull() {
        if (stars.size() > 0) {
            for (int i = 0; i < stars.size(); i++) {
                double distancex = stars.get(i).x - planet.x;
                double distancey = stars.get(i).y - planet.y;

                float angle = (float) Math.toDegrees(Math.atan2(distancey, distancex));

                if (angle < 0) {
                    angle += 360;
                }
                //System.out.println("XDIFF:" + distancex + " YDIFF:" + distancey+" ANGLE: "+ angle);

                double distanceTotal = sqrt(Math.pow(distancex, 2) + Math.pow(distancey, 2));

                double gravityConstaint = 30;//6 * Math.pow(10, -11);
                double gravityForce = (gravityConstaint * stars.get(i).mass * planet.mass) / Math.pow(distanceTotal, 2);
                double acceleration = gravityForce / stars.get(i).mass;//f=ma =>a = f/m

                //  System.out.println("Distance: " + distanceTotal + " FORCE: " + gravityForce + " Acceleration: " + acceleration);
                double xAccel = acceleration * Math.cos(angle);
                double yAccel = acceleration * Math.sin(angle);
                // System.out.println("XACCEL: "+xAccel+" YACCEL:"+ yAccel);

                stars.get(i).x += xAccel;
                stars.get(i).y += yAccel;
            }
        }
    }

    public void drawCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        if (stars.size() > 0) {
            for (int i = 0; i < stars.size(); i++) {
                gc.setFill(Color.BLUE);
                gc.fillOval(stars.get(i).x, stars.get(i).y, 20, 20);

            }
        }

        gc.setFill(Color.ORANGE);
        gc.fillOval(planet.x - 45, planet.y - 45, 110, 110);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
