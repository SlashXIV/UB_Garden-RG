package fr.ubx.poo.ugarden.view;


import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import fr.ubx.poo.ugarden.go.decor.ground.Door;

public class SpriteDoor extends Sprite {


    public SpriteDoor(Pane layer, Door door) {
        super(layer,null, door);
        updateImage();
    }
    @Override
    public void updateImage() {
        Door door = (Door) getGameObject();
        Image image = getImage(door);
        setImage(image);

    }

    private Image getImage( Door door) {
        if (door.isOpen()) {
            return ImageResourceFactory.getInstance().get(ImageResource.DOOR_OPENED);
        }
        else {
            return ImageResourceFactory.getInstance().get(ImageResource.DOOR_CLOSED);
        }
    }
}
