package overworld;

import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

class PlayerUpdater
{
    private Presenter presenter;
    private Model     model;

    private boolean staleInputUp;
    private boolean staleInputUse;

    PlayerUpdater(Presenter presenter)
    {
        this.presenter = presenter;
        staleInputUp = false;
        staleInputUse = false;
    }

    void update(Model model, Input input) throws SlickException
    {
        this.model = model;

        long playerDX = model.getPlayerDX();
        long playerDY = model.getPlayerDY();

        long proposedPlayerDX = 0;
        long proposedPlayerDY = 0;

        boolean playerFloor = model.isPlayerCollisionDown();

        checkOffWall(playerFloor);
        model.setResetJump(playerFloor);

        boolean playerInputLeft  = input.isKeyDown(Input.KEY_A);
        boolean playerInputRight = input.isKeyDown(Input.KEY_D);
        boolean playerInputUp    = input.isKeyDown(Input.KEY_W);
        boolean playerInputDown  = input.isKeyDown(Input.KEY_S);
        boolean playerInputUse   = input.isKeyDown(Input.KEY_E);

        boolean playerOnWallLeft  = model.isPlayerOnWallLeft();
        boolean playerOnWallRight = model.isPlayerOnWallRight();

        //if the player is not on solid ground, we need to calculate the effects of gravity
        if (!playerFloor)
        {
            proposedPlayerDY = getProposedPlayerDYDueToGravity(playerDY, playerOnWallLeft || playerOnWallRight);
        }

        //if the player is not trying to move left or right, fade her horizontal movement until stopped
        if (playerDX != 0)
        {
            proposedPlayerDX = getProposedPlayerDXDueToFade(playerDX, playerFloor);
        }

        //player input
        //move left
        if (playerInputLeft && !playerInputRight)
        {
            if (playerDX > -(model.getMaxDXDueToInput()))
            {
                proposedPlayerDX = Math.max((playerDX - (model.getDDXDueToInput())), -model.getMaxDXDueToInput());
            }
            else if (playerDX < -(model.getMaxDXDueToInput()))
            {
                proposedPlayerDX = Math.min((playerDX + (model.getDDXDueToInput() / 2)), -model.getMaxDXDueToInput());
            }
            else
            {
                proposedPlayerDX = playerDX;
            }
            if (model.isPlayerCollisionLeft() && !model.isPlayerCollisionDown())
            {
                model.setPlayerOnWallLeft(true);
                //System.out.println("Player is on left wall");
            }
        }
        //move right
        if (playerInputRight && !playerInputLeft)
        {
            if (playerDX < model.getMaxDXDueToInput())
            {
                proposedPlayerDX = Math.min((playerDX + model.getDDXDueToInput()), model.getMaxDXDueToInput());
            }
            else if (playerDX > model.getMaxDXDueToInput())
            {
                proposedPlayerDX = Math.max((playerDX - (model.getDDXDueToInput() / 2)), model.getMaxDXDueToInput());
            }
            else
            {
                proposedPlayerDX = playerDX;
            }
            if (model.isPlayerCollisionRight() && !model.isPlayerCollisionDown())
            {
                model.setPlayerOnWallRight(true);
                //System.out.println("Player is on right wall");
            }
        }
        //jump
        if (playerInputUp && !staleInputUp)
        {
            if (playerFloor)
            { //if the player has solid ground beneath her
                staleInputUp = true;
                proposedPlayerDY = model.getInstantaneousJumpDY();
            }
            else if (playerOnWallLeft)
            {
                staleInputUp = true;
                model.setPlayerOnWallLeft(false);
                proposedPlayerDY = model.getInstantaneousWallJumpDY();
                proposedPlayerDX = model.getInstantaneousWallJumpRightDX();
            }
            else if (playerOnWallRight)
            {
                staleInputUp = true;
                model.setPlayerOnWallRight(false);
                proposedPlayerDY = model.getInstantaneousWallJumpDY();
                proposedPlayerDX = model.getInstantaneousWallJumpLeftDX();
            }
        }
        if (!playerInputUp)
        {
            staleInputUp = false;
        }
        //"move down"
        //will currently cause the player to let go of the wall and do nothing else
        if (playerInputDown)
        {
            model.setPlayerOnWallLeft(false);
            model.setPlayerOnWallRight(false);
        }
        //"use", currently disallowed while jumping or falling
        if (playerInputUse && (playerDY == 0) && !staleInputUse)
        {
            staleInputUse = true;
            String mapHookCurrent = model.getMapHookCurrent();
            //String mapHookPrevious = model.getMapHookSpawn();
            String hooks[] = model.getIntersectingTileHooks();
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
                            return; //if we're transitioning, don't want to handle any other player action
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
        //end player input

        //update player movement and location
        //if the proposed player dx is non-zero, check for collisions and use the collision distance for setting dx
        if (proposedPlayerDX != 0)
        {
            long adjustedDX = model.getHorizontalCollisionDistanceByDX(proposedPlayerDX);

            //if the proposed player movement was left or right but she ends up on a wall
            //(such as from faded movement or jump off of wall movement) set the playerOnWall
            //flag - allows player to jump back and forth between walls by just pressing the
            //jump key
            if (adjustedDX == 0 && !playerFloor)
            {
                if (proposedPlayerDX < 0)
                {
                    model.setPlayerOnWallLeft(true);
                }
                else
                {
                    model.setPlayerOnWallRight(true);
                }
            }

            model.setPlayerDX(adjustedDX);
        }
        else
        {
            model.setPlayerDX(proposedPlayerDX);
        }

        //if the proposed player dy is non-zero, check for collisions and use the collision distance for setting dy
        if (proposedPlayerDY != 0)
        {
            long adjustedDY = model.getVerticalCollisionDistanceByDY(proposedPlayerDY);

            model.setPlayerDY(adjustedDY);
        }
        else
        {
            model.setPlayerDY(proposedPlayerDY);
        }

        model.setPlayerX(model.getPlayerX() + model.getPlayerDX());
        model.setPlayerY(model.getPlayerY() + model.getPlayerDY());
        //end update player movement and location
    }

    private void checkOffWall(boolean floor)
    {
        //if there is not collision to the left of the player or the player is on solid ground,
        //unset the playerOnWallLeft flag
        if (!model.isPlayerCollisionLeft() || floor)
        {
            model.setPlayerOnWallLeft(false);
        }
        //if there is not collision to the right of the player or the player is on solid ground,
        //unset the playerOnWallRight flag
        if (!model.isPlayerCollisionRight() || floor)
        {
            model.setPlayerOnWallRight(false);
        }
    }

    private long getProposedPlayerDYDueToGravity(long playerDY, boolean wall)
    {
        long proposedPlayerDY = 0;
        long maxDYOnWall;
        long maxDYDueToGravity;

        //gravity
        //if the player is on a wall,
        if (wall)
        {
            maxDYOnWall = model.getMaxDYOnWall();
            //if the player dy is less than the max due to gravity while on a wall, add to it
            if (playerDY < maxDYOnWall)
            {
                proposedPlayerDY = Math.min(playerDY + model.getDDYDueToGravity(), maxDYOnWall);
            }
            //else if the player dy is greater than the max due to gravity while on a wall, remove from it
            else if (playerDY > maxDYOnWall)
            {
                proposedPlayerDY = Math.min(model.getPlayerDY() - model.getDDYDueToGravity(), maxDYOnWall);
            }
        }
        //else if the player is not on solid ground,
        else if (!model.isPlayerCollisionDown())
        {
            maxDYDueToGravity = model.getMaxDYDueToGravity();
            //if the player dy is less than the max dy due to gravity, add to it
            if (model.getPlayerDY() < maxDYDueToGravity)
            {
                proposedPlayerDY = Math.min(playerDY + model.getDDYDueToGravity(), maxDYDueToGravity);
            }
            //else, just propose the last player dy
            else
            {
                proposedPlayerDY = Math.min(playerDY, maxDYDueToGravity);
            }
        }
        //end gravity

        return proposedPlayerDY;
    }

    private long getProposedPlayerDXDueToFade(long playerDX, boolean floor)
    {
        long proposedPlayerDX = 0;

        //horizontal player movement fade
        if (playerDX > 0)
        {
            if (floor)
            {
                proposedPlayerDX = Math.max(playerDX - model.getDDXDueToInput(), 0);
            }
            else
            {
                proposedPlayerDX = Math.max((playerDX - (model.getDDXDueToInput() / 2)), 0);
            }
        }
        else if (playerDX < 0)
        {
            if (floor)
            {
                proposedPlayerDX = Math.min(playerDX + model.getDDXDueToInput(), 0);
            }
            else
            {
                proposedPlayerDX = Math.min((playerDX + (model.getDDXDueToInput() / 2)), 0);
            }
        }
        //end horizontal player movement fade

        return proposedPlayerDX;
    }
}
