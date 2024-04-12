/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.engine;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.decor.ground.Door;
import fr.ubx.poo.ugarden.go.personage.Player;
import fr.ubx.poo.ugarden.go.personage.Bee;
import fr.ubx.poo.ugarden.view.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final Game game;
    private final Player player;

    private final List<Bee> bees = new LinkedList<>();

    private final List<Door> doors = new LinkedList<>();

    private final List<Bee> bees2 = new LinkedList<>();

    private final List<Door> doors2 = new LinkedList<>();
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;

    public GameEngine(Game game, final Stage stage) {
        this.stage = stage;
        this.game = game;
        this.player = game.getPlayer();
        this.bees.addAll(game.getBeeList());
        this.doors.addAll(game.getDoorList());
        this.doors2.addAll(game.getDoorList2());
        this.bees2.addAll(game.getBeeList2());
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {
        Group root = new Group();
        layer = new Pane();

        int height = game.world().getGrid().height();
        int width = game.world().getGrid().width();
        int sceneWidth = width * ImageResource.size;
        int sceneHeight = height * ImageResource.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight);

        // Create sprites
        int currentLevel = game.world().currentLevel();


        for (var decor : game.world().getGrid().values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
            var bonus = decor.getBonus();
            if (bonus != null) {
                sprites.add(SpriteFactory.create(layer, bonus));
                bonus.setModified(true);
            }
        }



        sprites.add(new SpritePlayer(layer, player));


        for (Bee bee : bees) {
            sprites.add(new SpriteBee(layer, bee));
        }

        for (Door door : doors) {
            sprites.add(new SpriteDoor(layer, door));
        }

        if(game.world().currentLevel()!=1) {

            for (Bee bee : bees2) {
                sprites.add(new SpriteBee(layer, bee));
            }

            for (Door door : doors2) {
                sprites.add(new SpriteDoor(layer, door));
            }
        }




    }


    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
            checkLevel();

            // Check keyboard actions
            processInput(now);

            // Do actions
            update(now);
            checkCollision(now);

            // Graphic update
            cleanupSprites();
            render();
            statusBar.update(game);
            }
        };
    }


    private void checkLevel() {
        if (game.isSwitchLevelRequested()) {


            sprites.clear();
            bees.clear();
            doors.clear();

            game.world().setCurrentLevel(game.getSwitchLevel());


            //Changement de niveau pour les portes
            LinkedList <Door> doors2 = game.getDoorList2();
            for( Door door : doors2){
                door.setPosition(new Position(game.world().currentLevel(),door.getPosition().x(),door.getPosition().y()));
                player.setPosition(door.getPosition());
                break;
            }

            //Changement de niveau pour les abeilles
            LinkedList <Bee> bees2 = game.getBeeList2();
            for (Bee bee : bees2) {
                bee.setPosition(new Position(game.world().currentLevel(),bee.getPosition().x(),bee.getPosition().y()));
            }





            stage.close();
            initialize();
            game.clearSwitchLevel();


        }
    }

    private void checkCollision(long now) {
        Iterator<Bee> iterator = bees.iterator(); //Cet objet permet de parcourir la liste bees de manière sûre et efficace, en évitant les problèmes liés à la modification de la liste pendant le parcours
        while (iterator.hasNext()) { //Dans le cas où la liste bees ne contient qu'une seule abeille, la boucle ne s'exécutera qu'une seule fois, car l'itérateur parcourt tous les éléments de la liste
            Bee bee = iterator.next();
            if (bee.getPosition().equals(player.getPosition())) {
                iterator.remove();
                bee.remove();



                if(!player.isInvincible()){
                    player.removeLife();
                }
                    player.setInvincible(true);

            }
        }


        for (Door door : doors) {
            if (door.getPosition().equals(player.getPosition())) {
                if (player.getNbKeys() > 0) {
                    player.removeKey();
                    door.setOpen(true);
                    door.update(now);
                    int level = game.world().currentLevel();
                    int nextLevel = level + 1;
                    game.requestSwitchLevel(nextLevel);

                }
            }
        }

        if(game.world().currentLevel()!=1) {
            Iterator<Bee> iterator2 = bees2.iterator();
            while (iterator2.hasNext()) {
                Bee bee = iterator2.next();
                if (bee.getPosition().equals(player.getPosition())) {
                    iterator2.remove();
                    bee.remove();


                    if (!player.isInvincible()) {
                        player.removeLife();
                    }
                    player.setInvincible(true);

                }
            }


            for (Door door : doors2) {
                if (door.getPosition().equals(player.getPosition())) {
                    if (player.getNbKeys() > 0) {
                        player.removeKey();
                        door.setOpen(true);
                        door.update(now);
                        int level = game.world().currentLevel();
                        int nextLevel = level + 1;
                        game.requestSwitchLevel(nextLevel);

                    }
                }
            }
        }


    }


    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            player.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            player.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            player.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            player.requestMove(Direction.UP);
        }


        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);
        bees.forEach(bee -> bee.update(now));
        doors.forEach(door -> door.update(now));
        bees2.forEach(bee -> bee.update(now));
        doors2.forEach(door -> door.update(now));

        if (player.getLives() == 0) {
            gameLoop.stop();
            showMessage("Perdu !", Color.RED);
        }

        if (player.isFoundPrincess()){
            gameLoop.stop();
            showMessage("Gagné !", Color.GREEN);
        }



    }

    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                cleanUpSprites.add(sprite);
            }
        });
        cleanUpSprites.forEach(Sprite::remove);
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

    private void render() {
        sprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
    }
}