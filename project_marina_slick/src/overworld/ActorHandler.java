package overworld;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;
import overworld.model.Actor;
import overworld.model.Model;
import overworld.view.View;

import java.io.IOException;
import java.io.InputStream;

import static org.newdawn.slick.Image.FILTER_NEAREST;
import static overworld.Globals.*;

/**
 * overworld.ActorHandler is used by the presenter to handle actor logic and update the model.
 *
 * @author scorple
 * @version dev02
 * @since 2016_0810
 */
class ActorHandler
{
    private String ref;

    private Presenter presenter;
    private Model     model;
    private View      view;

    private overworld.model.Actor actor;

    private boolean staleInputUp;
    private boolean staleInputUse;

    /**
     * Constructor, sets a callback to the presenter using this updater.
     *
     * @param presenter Presenter: The presenter to call back to where applicable.
     */
    ActorHandler(String ref, Presenter presenter)
    {
        this.ref = ref;

        this.presenter = presenter;

        staleInputUp = false;
        staleInputUse = false;
    }

    ActorHandler(String ref, Presenter presenter, Model model, View view, long x, long y) throws SlickException
    {
        this.ref = ref;

        this.presenter = presenter;
        this.model = model;
        this.view = view;

        setupModel(x, y);
        setupView();

        staleInputUp = false;
        staleInputUse = false;
    }

    private void setupModel(long x, long y)
    {
        SAXBuilder saxBuilder = new SAXBuilder();
        InputStream inputStream = ResourceLoader.getResourceAsStream(
            ACTOR_RESOURCE_PATH + ref + ACTOR_PROPERTIES_FILE);
        Document document = null;
        actor = new Actor(ref, x, y);

        try
        {
            document = saxBuilder.build(inputStream);
        }
        catch (JDOMException | IOException e)
        {
            e.printStackTrace();
        }

        Element actorElement = document.getRootElement();

        Element generalElement = actorElement.getChild("general");

        actor.setWidth(Long.parseLong(generalElement.getChildText("width")));
        actor.setHeight(Long.parseLong(generalElement.getChildText("height")));

        actor.setGraphicOffsetX(Long.parseLong(generalElement.getChildText("graphicOffsetX")));
        actor.setGraphicOffsetY(Long.parseLong(generalElement.getChildText("graphicOffsetY")));

        Element movementElement = actorElement.getChild("movement");

        actor.setInputDDX(Long.parseLong(movementElement.getChildText("inputDDX")));
        //System.out.println("inputDDX:<" + inputDDX + ">");
        actor.setInputMaxDX(Long.parseLong(movementElement.getChildText("inputMaxDX")));
        //System.out.println("inputMaxDX:<" + inputMaxDX + ">");

        actor.setInstantaneousJumpDY(Long.parseLong(movementElement.getChildText("instantaneousJumpDY")));
        //System.out.println("instantaneousJumpDY:<" + instantaneousJumpDY + ">");
        actor.setInstantaneousWallJumpDX(Long.parseLong(movementElement.getChildText("instantaneousWallJumpDX")));
        //System.out.println("instantaneousWallJumpDX:<" + instantaneousWallJumpDX + ">");
        actor.setInstantaneousWallJumpDY(Long.parseLong(movementElement.getChildText("instantaneousWallJumpDY")));
        //System.out.println("instantaneousWallJumpDY:<" + instantaneousWallJumpDY + ">");

        actor.setGravityDDY(Long.parseLong(movementElement.getChildText("gravityDDY")));
        //System.out.println("gravityDDY:<" + gravityDDY + ">");
        actor.setGravityMaxDY(Long.parseLong(movementElement.getChildText("gravityMaxDY")));
        //System.out.println("gravityMaxDY:<" + gravityMaxDY + ">");
        actor.setGravityMaxWallDY(Long.parseLong(movementElement.getChildText("gravityMaxWallDY")));
        //System.out.println("gravityMaxWallDY:<" + gravityMaxWallDY + ">");

        model.addEntity(ref, actor);
    }

    private void setupView() throws SlickException
    {
        InputStream          inputStream;
        overworld.view.Actor actor = new overworld.view.Actor();

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_LEFT +
            PLAYER_GRAPHIC_POSTFIX_WALK + "1" + GRAPHICS_EXTENSION);
        Image[] playerFramesFaceLeft = {new Image(inputStream,
                                                  PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_LEFT +
                                                  PLAYER_GRAPHIC_POSTFIX_WALK + "1" +
                                                  GRAPHICS_EXTENSION,
                                                  false,
                                                  FILTER_NEAREST)};
        Animation playerAnimationFaceLeft = new Animation(playerFramesFaceLeft, 1, false);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_RIGHT +
            PLAYER_GRAPHIC_POSTFIX_WALK + "1" + GRAPHICS_EXTENSION);
        Image[] playerFramesFaceRight = {new Image(inputStream,
                                                   PLAYER_GRAPHIC_PATH_WALK +
                                                   PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                   PLAYER_GRAPHIC_POSTFIX_WALK + "1" +
                                                   GRAPHICS_EXTENSION,
                                                   false,
                                                   FILTER_NEAREST)};
        Animation playerAnimationFaceRight = new Animation(playerFramesFaceRight, 1, false);

        Image[] playerFramesWalkLeft = new Image[PLAYER_GRAPHIC_FRAME_COUNT_WALK];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_WALK; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_LEFT +
                PLAYER_GRAPHIC_POSTFIX_WALK + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesWalkLeft[i] = new Image(inputStream,
                                                PLAYER_GRAPHIC_PATH_WALK +
                                                PLAYER_GRAPHIC_PREFIX_LEFT +
                                                PLAYER_GRAPHIC_POSTFIX_WALK + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationWalkLeft =
            new Animation(playerFramesWalkLeft, PLAYER_GRAPHIC_FRAME_DURATION_WALK, true);
        playerAnimationWalkLeft.setLooping(true);
        playerAnimationWalkLeft.setPingPong(true);

        Image[] playerFramesWalkRight = new Image[PLAYER_GRAPHIC_FRAME_COUNT_WALK];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_WALK; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_RIGHT +
                PLAYER_GRAPHIC_POSTFIX_WALK + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesWalkRight[i] = new Image(inputStream,
                                                 PLAYER_GRAPHIC_PATH_WALK +
                                                 PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                 PLAYER_GRAPHIC_POSTFIX_WALK + (i + 1) +
                                                 GRAPHICS_EXTENSION,
                                                 false,
                                                 FILTER_NEAREST);
        }
        Animation playerAnimationWalkRight =
            new Animation(playerFramesWalkRight, PLAYER_GRAPHIC_FRAME_DURATION_WALK, true);
        playerAnimationWalkRight.setLooping(true);
        playerAnimationWalkRight.setPingPong(true);

        Image[] playerFramesRunLeft = new Image[PLAYER_GRAPHIC_FRAME_COUNT_RUN];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_RUN; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_RUN + PLAYER_GRAPHIC_PREFIX_LEFT +
                PLAYER_GRAPHIC_POSTFIX_RUN + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesRunLeft[i] = new Image(inputStream,
                                               PLAYER_GRAPHIC_PATH_RUN +
                                               PLAYER_GRAPHIC_PREFIX_LEFT +
                                               PLAYER_GRAPHIC_POSTFIX_RUN + (i + 1) +
                                               GRAPHICS_EXTENSION,
                                               false,
                                               FILTER_NEAREST);
        }
        Animation playerAnimationRunLeft =
            new Animation(playerFramesRunLeft, PLAYER_GRAPHIC_FRAME_DURATION_RUN, true);
        playerAnimationRunLeft.setLooping(true);
        playerAnimationRunLeft.setPingPong(true);

        Image[] playerFramesRunRight = new Image[PLAYER_GRAPHIC_FRAME_COUNT_RUN];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_RUN; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_RUN + PLAYER_GRAPHIC_PREFIX_RIGHT +
                PLAYER_GRAPHIC_POSTFIX_RUN + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesRunRight[i] = new Image(inputStream,
                                                PLAYER_GRAPHIC_PATH_RUN +
                                                PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                PLAYER_GRAPHIC_POSTFIX_RUN + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationRunRight =
            new Animation(playerFramesRunRight, PLAYER_GRAPHIC_FRAME_DURATION_RUN, true);
        playerAnimationRunRight.setLooping(true);
        playerAnimationRunRight.setPingPong(true);

        Image[] playerFramesJumpLeft = new Image[PLAYER_GRAPHIC_FRAME_COUNT_JUMP];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_JUMP; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_JUMP + PLAYER_GRAPHIC_PREFIX_LEFT + PLAYER_GRAPHIC_POSTFIX_JUMP + (i + 1) +
                GRAPHICS_EXTENSION);
            playerFramesJumpLeft[i] = new Image(inputStream,
                                                PLAYER_GRAPHIC_PATH_JUMP + PLAYER_GRAPHIC_PREFIX_LEFT +
                                                PLAYER_GRAPHIC_POSTFIX_JUMP + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationJumpLeft =
            new Animation(playerFramesJumpLeft, PLAYER_GRAPHIC_FRAME_DURATION_JUMP, true);
        playerAnimationJumpLeft.setLooping(false);

        Image[] playerFramesJumpRight = new Image[PLAYER_GRAPHIC_FRAME_COUNT_JUMP];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_JUMP; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_JUMP + PLAYER_GRAPHIC_PREFIX_RIGHT + PLAYER_GRAPHIC_POSTFIX_JUMP + (i + 1) +
                GRAPHICS_EXTENSION);
            playerFramesJumpRight[i] = new Image(inputStream,
                                                 PLAYER_GRAPHIC_PATH_JUMP + PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                 PLAYER_GRAPHIC_POSTFIX_JUMP + (i + 1) +
                                                 GRAPHICS_EXTENSION,
                                                 false,
                                                 FILTER_NEAREST);
        }
        Animation playerAnimationJumpRight =
            new Animation(playerFramesJumpRight, PLAYER_GRAPHIC_FRAME_DURATION_JUMP, true);
        playerAnimationJumpRight.setLooping(false);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHIC_PATH_WALL + PLAYER_GRAPHIC_PRE_PREFIX_WALL +
            PLAYER_GRAPHIC_PREFIX_LEFT + GRAPHICS_EXTENSION);
        Image[] playerFramesWallLeft = {new Image(inputStream,
                                                  PLAYER_GRAPHIC_PATH_WALL +
                                                  PLAYER_GRAPHIC_PRE_PREFIX_WALL +
                                                  PLAYER_GRAPHIC_PREFIX_LEFT +
                                                  GRAPHICS_EXTENSION,
                                                  false,
                                                  FILTER_NEAREST)};
        Animation playerAnimationWallLeft = new Animation(playerFramesWallLeft, 1, false);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHIC_PATH_WALL + PLAYER_GRAPHIC_PRE_PREFIX_WALL +
            PLAYER_GRAPHIC_PREFIX_RIGHT + GRAPHICS_EXTENSION);
        Image[] playerFramesWallRight = {new Image(inputStream,
                                                   PLAYER_GRAPHIC_PATH_WALL +
                                                   PLAYER_GRAPHIC_PRE_PREFIX_WALL +
                                                   PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                   GRAPHICS_EXTENSION,
                                                   false,
                                                   FILTER_NEAREST)};
        Animation playerAnimationWallRight = new Animation(playerFramesWallRight, 1, false);

        actor.setAnimationFaceLeft(playerAnimationFaceLeft);
        actor.setAnimationFaceRight(playerAnimationFaceRight);
        actor.setAnimationWalkLeft(playerAnimationWalkLeft);
        actor.setAnimationWalkRight(playerAnimationWalkRight);
        actor.setAnimationRunLeft(playerAnimationRunLeft);
        actor.setAnimationRunRight(playerAnimationRunRight);
        actor.setAnimationJumpLeft(playerAnimationJumpLeft);
        actor.setAnimationJumpRight(playerAnimationJumpRight);
        actor.setAnimationWallLeft(playerAnimationWallLeft);
        actor.setAnimationWallRight(playerAnimationWallRight);

        System.out.println("Adding actor ref:<" + ref + "> to View");

        view.addEntity(ref, actor);
    }

    /**
     * Given the current model and the actor input, apply game logic to check the actor
     * position and update it with environmental effects (i.e. gravity, horizontal movement
     * fade) and actor input, including strafing, jumping, wall-jumping, etc. Also check
     * for actor interactions with the world and call back to the presenter as needed
     * (i.e. map transition).
     *
     * @param model Model: The current overworld game state model.
     * @param input Input: The current actor input object.
     *
     * @throws SlickException Slick library exception.
     */
    void update(Model model, Input input) throws SlickException
    {
        this.model = model;
        actor = (overworld.model.Actor) model.getEntityByRef(ref);

        long playerDX = actor.getDX();
        long playerDY = actor.getDY();

        long proposedPlayerDX = 0;
        long proposedPlayerDY = 0;

        boolean playerFloor = model.isActorCollisionDown(actor);

        checkOffWall(playerFloor);
        actor.setResetJump(playerFloor);

        boolean playerInputLeft  = input.isKeyDown(Input.KEY_A);
        boolean playerInputRight = input.isKeyDown(Input.KEY_D);
        boolean playerInputUp    = input.isKeyDown(Input.KEY_W);
        boolean playerInputDown  = input.isKeyDown(Input.KEY_S);
        boolean playerInputUse   = input.isKeyDown(Input.KEY_E);

        boolean playerOnWallLeft  = actor.isOnWallLeft();
        boolean playerOnWallRight = actor.isOnWallRight();

        //if the actor is not on solid ground, we need to calculate the effects of gravity
        if (!playerFloor)
        {
            proposedPlayerDY = getProposedPlayerDYDueToGravity(playerDY, playerOnWallLeft || playerOnWallRight);
        }

        //if the actor is not trying to move left or right, fade her horizontal movement until stopped
        if (playerDX != 0)
        {
            proposedPlayerDX = getProposedPlayerDXDueToFade(playerDX, playerFloor);
        }

        //actor input
        //move left
        if (playerInputLeft && !playerInputRight)
        {
            if (playerDX > -(actor.getInputMaxDX()))
            {
                proposedPlayerDX = Math.max((playerDX - (actor.getInputDDX())), -(actor.getInputMaxDX()));
            }
            else if (playerDX < -(actor.getInputMaxDX()))
            {
                proposedPlayerDX = Math.min((playerDX + (actor.getInputDDX() / 2)), -(actor.getInputMaxDX()));
            }
            else
            {
                proposedPlayerDX = playerDX;
            }
            if (model.isActorCollisionLeft(actor) && !model.isActorCollisionDown(actor))
            {
                actor.setOnWallLeft(true);
                //System.out.println("Player is on left wall");
            }
        }
        //move right
        if (playerInputRight && !playerInputLeft)
        {
            if (playerDX < actor.getInputMaxDX())
            {
                proposedPlayerDX = Math.min((playerDX + actor.getInputDDX()), actor.getInputMaxDX());
            }
            else if (playerDX > actor.getInputMaxDX())
            {
                proposedPlayerDX = Math.max((playerDX - (actor.getInputDDX() / 2)), actor.getInputMaxDX());
            }
            else
            {
                proposedPlayerDX = playerDX;
            }
            if (model.isActorCollisionRight(actor) && !model.isActorCollisionDown(actor))
            {
                actor.setOnWallRight(true);
                //System.out.println("Player is on right wall");
            }
        }
        //jump
        if (playerInputUp && !staleInputUp)
        {
            if (playerFloor)
            { //if the actor has solid ground beneath her
                staleInputUp = true;
                proposedPlayerDY = actor.getInstantaneousJumpDY();
            }
            else if (playerOnWallLeft)
            {
                staleInputUp = true;
                actor.setOnWallLeft(false);
                proposedPlayerDX = actor.getInstantaneousWallJumpDX();
                proposedPlayerDY = actor.getInstantaneousWallJumpDY();
            }
            else if (playerOnWallRight)
            {
                staleInputUp = true;
                actor.setOnWallRight(false);
                proposedPlayerDX = -(actor.getInstantaneousWallJumpDX());
                proposedPlayerDY = actor.getInstantaneousWallJumpDY();
            }
        }
        if (!playerInputUp)
        {
            staleInputUp = false;
        }
        //"move down"
        //will currently cause the actor to let go of the wall and do nothing else
        if (playerInputDown)
        {
            actor.setOnWallLeft(false);
            actor.setOnWallRight(false);
        }
        //"use", currently disallowed while jumping or falling
        if (playerInputUse && (playerDY == 0) && !staleInputUse)
        {
            staleInputUse = true;
            String mapHookCurrent = model.getMap().getHookCurrent();
            //String mapHookPrevious = model.getHookSpawn();
            String hooks[] = model.getActorIntersectingTileHooks(actor);
            //String feedback = "Intersecting tile hooks:";

            for (String hook : hooks)
            {
                if (!hook.equals(""))
                {
                    for (int i_map_id = 0; i_map_id < Globals.MAP_HOOK_LIST.length; ++i_map_id)
                    {
                        if (hook.equals(Globals.MAP_HOOK_LIST[i_map_id]))
                        {
                            System.out.println("Valid map hook found: <" + hook + ">");
                            System.out.println("Transitioning to <" + hook + "> from <" + mapHookCurrent + ">");
                            presenter.transitionMap(hook, mapHookCurrent);
                            return; //if we're transitioning, don't want to handle any other actor action
                        }
                    }
                    //feedback += " " + hooks[i_hook];
                }
            }
            //System.out.println(feedback);
        }
        if (!playerInputUse)
        {
            staleInputUse = false;
        }
        //end actor input

        //update actor movement and location
        //if the proposed actor dX is non-zero, check for collisions and use the collision distance for setting dX
        if (proposedPlayerDX != 0)
        {
            long adjustedDX = model.getActorHorizontalCollisionDistanceByDX(actor, proposedPlayerDX);

            //if the proposed actor movement was left or right but she ends up on a wall
            //(such as from faded movement or jump off of wall movement) set the playerOnWall
            //flag - allows actor to jump back and forth between walls by just pressing the
            //jump key
            if (adjustedDX == 0 && !playerFloor)
            {
                if (proposedPlayerDX < 0)
                {
                    actor.setOnWallLeft(true);
                }
                else
                {
                    actor.setOnWallRight(true);
                }
            }

            actor.setDX(adjustedDX);
        }
        else
        {
            actor.setDX(proposedPlayerDX);
        }

        //if the proposed actor dy is non-zero, check for collisions and use the collision distance for setting dy
        if (proposedPlayerDY != 0)
        {
            long adjustedDY = model.getActorVerticalCollisionDistanceByDY(actor, proposedPlayerDY);

            actor.setDY(adjustedDY);
        }
        else
        {
            actor.setDY(proposedPlayerDY);
        }

        actor.setX(actor.getX() + actor.getDX());
        actor.setY(actor.getY() + actor.getDY());
        //end update actor movement and location
    }

    /**
     * Utility function for quickly checking and updating if the actor is no longer on a wall.
     *
     * @param floor boolean: True if the actor is standing on solid ground, false otherwise,
     *              expedites check.
     */
    private void checkOffWall(boolean floor)
    {
        //if there is not collision to the left of the actor or the actor is on solid ground,
        //unset the onWallLeft flag
        if (!model.isActorCollisionLeft(actor) || floor)
        {
            actor.setOnWallLeft(false);
        }
        //if there is not collision to the right of the actor or the actor is on solid ground,
        //unset the onWallRight flag
        if (!model.isActorCollisionRight(actor) || floor)
        {
            actor.setOnWallRight(false);
        }
    }

    /**
     * Get the suggested actor dY after applying the effects of gravity, not accounting for
     * collisions but accounting for whether the actor is on a wall and preventing us from
     * exceeding the max dY.
     *
     * @param playerDY long: The current actor dY, modified to produce return.
     * @param wall     boolean: True if the actor is on a wall, false otherwise,
     *                 passed in because it will effect the ddY to apply.
     *
     * @return long: The suggested actor dY after applying the effects of gravity.
     */
    private long getProposedPlayerDYDueToGravity(long playerDY, boolean wall)
    {
        long proposedPlayerDY = 0;
        long maxDYOnWall;
        long maxDYDueToGravity;

        //gravity
        //if the actor is on a wall,
        if (wall)
        {
            maxDYOnWall = actor.getGravityMaxWallDY();
            //if the actor dy is less than the max due to gravity while on a wall, add to it
            if (playerDY < maxDYOnWall)
            {
                proposedPlayerDY = Math.min(playerDY + actor.getGravityDDY(), maxDYOnWall);
            }
            //else if the actor dy is greater than the max due to gravity while on a wall, remove from it
            else if (playerDY > maxDYOnWall)
            {
                proposedPlayerDY = Math.min(actor.getDY() - actor.getGravityDDY(), maxDYOnWall);
            }
            else
            {
                proposedPlayerDY = maxDYOnWall;
            }
        }
        //else if the actor is not on solid ground,
        else if (!model.isActorCollisionDown(actor))
        {
            maxDYDueToGravity = actor.getGravityMaxDY();
            //if the actor dy is less than the max dy due to gravity, add to it
            if (playerDY < maxDYDueToGravity)
            {
                proposedPlayerDY = Math.min(playerDY + actor.getGravityDDY(), maxDYDueToGravity);
            }
            //else, just propose the last actor dy
            else
            {
                proposedPlayerDY = Math.min(playerDY, maxDYDueToGravity);
            }
        }
        //end gravity

        return proposedPlayerDY;
    }

    /**
     * Get the suggested actor dX after applying the effects of horizontal movement fade,
     * accounting for whether or not the actor is on solid ground and will not over or
     * under shoot 0 when applying ddX.
     *
     * @param playerDX long: The current actor dX, modified to produce return.
     * @param floor    boolean:  True if the actor is on solid ground, false otherwise,
     *                 passed in because it will effect the ddX to apply.
     *
     * @return long: The suggested actor dX after applying the effects of horizontal movement fade.
     */
    private long getProposedPlayerDXDueToFade(long playerDX, boolean floor)
    {
        long proposedPlayerDX = 0;

        //horizontal actor movement fade
        if (playerDX > 0)
        {
            if (floor)
            {
                proposedPlayerDX = Math.max(playerDX - actor.getInputDDX(), 0);
            }
            else
            {
                proposedPlayerDX = Math.max((playerDX - (actor.getInputDDX() / 2)), 0);
            }
        }
        else if (playerDX < 0)
        {
            if (floor)
            {
                proposedPlayerDX = Math.min(playerDX + actor.getInputDDX(), 0);
            }
            else
            {
                proposedPlayerDX = Math.min((playerDX + (actor.getInputDDX() / 2)), 0);
            }
        }
        //end horizontal actor movement fade

        return proposedPlayerDX;
    }
}
